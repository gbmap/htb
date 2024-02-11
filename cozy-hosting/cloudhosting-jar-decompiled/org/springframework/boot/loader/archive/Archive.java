package org.springframework.boot.loader.archive;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.jar.Manifest;

public interface Archive extends Iterable<Archive.Entry>, AutoCloseable {
   URL getUrl() throws MalformedURLException;

   Manifest getManifest() throws IOException;

   Iterator<Archive> getNestedArchives(Archive.EntryFilter searchFilter, Archive.EntryFilter includeFilter) throws IOException;

   default boolean isExploded() {
      return false;
   }

   @Override
   default void close() throws Exception {
   }

   public interface Entry {
      boolean isDirectory();

      String getName();
   }

   @FunctionalInterface
   public interface EntryFilter {
      boolean matches(Archive.Entry entry);
   }
}
