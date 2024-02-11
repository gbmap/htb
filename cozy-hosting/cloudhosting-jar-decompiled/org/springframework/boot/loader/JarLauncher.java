package org.springframework.boot.loader;

import org.springframework.boot.loader.archive.Archive;

public class JarLauncher extends ExecutableArchiveLauncher {
   static final Archive.EntryFilter NESTED_ARCHIVE_ENTRY_FILTER = entry -> entry.isDirectory()
         ? entry.getName().equals("BOOT-INF/classes/")
         : entry.getName().startsWith("BOOT-INF/lib/");

   public JarLauncher() {
   }

   protected JarLauncher(Archive archive) {
      super(archive);
   }

   @Override
   protected boolean isPostProcessingClassPathArchives() {
      return false;
   }

   @Override
   protected boolean isNestedArchive(Archive.Entry entry) {
      return NESTED_ARCHIVE_ENTRY_FILTER.matches(entry);
   }

   @Override
   protected String getArchiveEntryPathPrefix() {
      return "BOOT-INF/";
   }

   public static void main(String[] args) throws Exception {
      new JarLauncher().launch(args);
   }
}
