package org.springframework.boot.loader.jar;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.util.Enumeration;
import java.util.jar.Manifest;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

class JarFileWrapper extends AbstractJarFile {
   private final JarFile parent;

   JarFileWrapper(JarFile parent) throws IOException {
      super(parent.getRootJarFile().getFile());
      this.parent = parent;
      super.close();
   }

   @Override
   URL getUrl() throws MalformedURLException {
      return this.parent.getUrl();
   }

   @Override
   AbstractJarFile.JarFileType getType() {
      return this.parent.getType();
   }

   @Override
   Permission getPermission() {
      return this.parent.getPermission();
   }

   @Override
   public Manifest getManifest() throws IOException {
      return this.parent.getManifest();
   }

   @Override
   public Enumeration<java.util.jar.JarEntry> entries() {
      return this.parent.entries();
   }

   @Override
   public Stream<java.util.jar.JarEntry> stream() {
      return this.parent.stream();
   }

   @Override
   public java.util.jar.JarEntry getJarEntry(String name) {
      return this.parent.getJarEntry(name);
   }

   @Override
   public ZipEntry getEntry(String name) {
      return this.parent.getEntry(name);
   }

   @Override
   InputStream getInputStream() throws IOException {
      return this.parent.getInputStream();
   }

   @Override
   public synchronized InputStream getInputStream(ZipEntry ze) throws IOException {
      return this.parent.getInputStream(ze);
   }

   @Override
   public String getComment() {
      return this.parent.getComment();
   }

   @Override
   public int size() {
      return this.parent.size();
   }

   @Override
   public String toString() {
      return this.parent.toString();
   }

   @Override
   public String getName() {
      return this.parent.getName();
   }

   static JarFile unwrap(java.util.jar.JarFile jarFile) {
      if (jarFile instanceof JarFile file) {
         return file;
      } else if (jarFile instanceof JarFileWrapper wrapper) {
         return unwrap(wrapper.parent);
      } else {
         throw new IllegalStateException("Not a JarFile or Wrapper");
      }
   }
}
