package org.springframework.boot.loader.archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.Manifest;
import org.springframework.boot.loader.jar.JarFile;

public class JarFileArchive implements Archive {
   private static final String UNPACK_MARKER = "UNPACK:";
   private static final int BUFFER_SIZE = 32768;
   private static final FileAttribute<?>[] NO_FILE_ATTRIBUTES = new FileAttribute[0];
   private static final EnumSet<PosixFilePermission> DIRECTORY_PERMISSIONS = EnumSet.of(
      PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE
   );
   private static final EnumSet<PosixFilePermission> FILE_PERMISSIONS = EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
   private final JarFile jarFile;
   private URL url;
   private Path tempUnpackDirectory;

   public JarFileArchive(File file) throws IOException {
      this(file, file.toURI().toURL());
   }

   public JarFileArchive(File file, URL url) throws IOException {
      this(new JarFile(file));
      this.url = url;
   }

   public JarFileArchive(JarFile jarFile) {
      this.jarFile = jarFile;
   }

   @Override
   public URL getUrl() throws MalformedURLException {
      return this.url != null ? this.url : this.jarFile.getUrl();
   }

   @Override
   public Manifest getManifest() throws IOException {
      return this.jarFile.getManifest();
   }

   @Override
   public Iterator<Archive> getNestedArchives(Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) throws IOException {
      return new JarFileArchive.NestedArchiveIterator(this.jarFile.iterator(), searchFilter, includeFilter);
   }

   @Deprecated(
      since = "2.3.10",
      forRemoval = false
   )
   @Override
   public Iterator<Archive.Entry> iterator() {
      return new JarFileArchive.EntryIterator(this.jarFile.iterator(), null, null);
   }

   @Override
   public void close() throws IOException {
      this.jarFile.close();
   }

   protected Archive getNestedArchive(Archive.Entry entry) throws IOException {
      JarEntry jarEntry = ((JarFileArchive.JarFileEntry)entry).getJarEntry();
      if (jarEntry.getComment().startsWith("UNPACK:")) {
         return this.getUnpackedNestedArchive(jarEntry);
      } else {
         try {
            JarFile jarFile = this.jarFile.getNestedJarFile(jarEntry);
            return new JarFileArchive(jarFile);
         } catch (Exception var4) {
            throw new IllegalStateException("Failed to get nested archive for entry " + entry.getName(), var4);
         }
      }
   }

   private Archive getUnpackedNestedArchive(JarEntry jarEntry) throws IOException {
      String name = jarEntry.getName();
      if (name.lastIndexOf(47) != -1) {
         name = name.substring(name.lastIndexOf(47) + 1);
      }

      Path path = this.getTempUnpackDirectory().resolve(name);
      if (!Files.exists(path) || Files.size(path) != jarEntry.getSize()) {
         this.unpack(jarEntry, path);
      }

      return new JarFileArchive(path.toFile(), path.toUri().toURL());
   }

   private Path getTempUnpackDirectory() {
      if (this.tempUnpackDirectory == null) {
         Path tempDirectory = Paths.get(System.getProperty("java.io.tmpdir"));
         this.tempUnpackDirectory = this.createUnpackDirectory(tempDirectory);
      }

      return this.tempUnpackDirectory;
   }

   private Path createUnpackDirectory(Path parent) {
      int attempts = 0;

      while(attempts++ < 1000) {
         String fileName = Paths.get(this.jarFile.getName()).getFileName().toString();
         Path unpackDirectory = parent.resolve(fileName + "-spring-boot-libs-" + UUID.randomUUID());

         try {
            this.createDirectory(unpackDirectory);
            return unpackDirectory;
         } catch (IOException var6) {
         }
      }

      throw new IllegalStateException("Failed to create unpack directory in directory '" + parent + "'");
   }

   private void unpack(JarEntry entry, Path path) throws IOException {
      this.createFile(path);
      path.toFile().deleteOnExit();

      try (
         InputStream inputStream = this.jarFile.getInputStream(entry);
         OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
      ) {
         byte[] buffer = new byte[32768];

         int bytesRead;
         while((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
         }

         outputStream.flush();
      }
   }

   private void createDirectory(Path path) throws IOException {
      Files.createDirectory(path, this.getFileAttributes(path.getFileSystem(), DIRECTORY_PERMISSIONS));
   }

   private void createFile(Path path) throws IOException {
      Files.createFile(path, this.getFileAttributes(path.getFileSystem(), FILE_PERMISSIONS));
   }

   private FileAttribute<?>[] getFileAttributes(FileSystem fileSystem, EnumSet<PosixFilePermission> ownerReadWrite) {
      return !fileSystem.supportedFileAttributeViews().contains("posix")
         ? NO_FILE_ATTRIBUTES
         : new FileAttribute[]{PosixFilePermissions.asFileAttribute(ownerReadWrite)};
   }

   @Override
   public String toString() {
      try {
         return this.getUrl().toString();
      } catch (Exception var2) {
         return "jar archive";
      }
   }

   private abstract static class AbstractIterator<T> implements Iterator<T> {
      private final Iterator<JarEntry> iterator;
      private final Archive.EntryFilter searchFilter;
      private final Archive.EntryFilter includeFilter;
      private Archive.Entry current;

      AbstractIterator(Iterator<JarEntry> iterator, Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) {
         this.iterator = iterator;
         this.searchFilter = searchFilter;
         this.includeFilter = includeFilter;
         this.current = this.poll();
      }

      @Override
      public boolean hasNext() {
         return this.current != null;
      }

      @Override
      public T next() {
         T result = this.adapt(this.current);
         this.current = this.poll();
         return result;
      }

      private Archive.Entry poll() {
         while(this.iterator.hasNext()) {
            JarFileArchive.JarFileEntry candidate = new JarFileArchive.JarFileEntry(this.iterator.next());
            if ((this.searchFilter == null || this.searchFilter.matches(candidate)) && (this.includeFilter == null || this.includeFilter.matches(candidate))) {
               return candidate;
            }
         }

         return null;
      }

      protected abstract T adapt(Archive.Entry entry);
   }

   private static class EntryIterator extends JarFileArchive.AbstractIterator<Archive.Entry> {
      EntryIterator(Iterator<JarEntry> iterator, Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) {
         super(iterator, searchFilter, includeFilter);
      }

      protected Archive.Entry adapt(Archive.Entry entry) {
         return entry;
      }
   }

   private static class JarFileEntry implements Archive.Entry {
      private final JarEntry jarEntry;

      JarFileEntry(JarEntry jarEntry) {
         this.jarEntry = jarEntry;
      }

      JarEntry getJarEntry() {
         return this.jarEntry;
      }

      @Override
      public boolean isDirectory() {
         return this.jarEntry.isDirectory();
      }

      @Override
      public String getName() {
         return this.jarEntry.getName();
      }
   }

   private class NestedArchiveIterator extends JarFileArchive.AbstractIterator<Archive> {
      NestedArchiveIterator(Iterator<JarEntry> iterator, Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) {
         super(iterator, searchFilter, includeFilter);
      }

      protected Archive adapt(Archive.Entry entry) {
         try {
            return JarFileArchive.this.getNestedArchive(entry);
         } catch (IOException var3) {
            throw new IllegalStateException(var3);
         }
      }
   }
}
