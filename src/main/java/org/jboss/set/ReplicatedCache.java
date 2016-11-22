package org.jboss.set;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author Ryan Emerson
 */
public class ReplicatedCache {

   public static void main(String[] args) throws Exception {
      System.setProperty("jdbc.upsert.disabled", "false");
      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-replicated.xml");
//      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-replicated-8.2.xml");
      Cache<Integer, String> cache = manager.getCache("repl");

      Scanner reader = new Scanner(System.in);  // Reading from System.in
      System.out.println("Enter any key to create and execute batch");
      while (reader.hasNext()) {
         String input = reader.next();
         if (input.equals("exit")) {
            System.exit(0);
         } else {
            cache.startBatch();
            cache.put(1, "Test Input", 2, TimeUnit.SECONDS);
            cache.put(2, "Test Input", 2, TimeUnit.SECONDS);
            cache.put(3, "Test Input", 2, TimeUnit.SECONDS);
            cache.endBatch(true);
         }
         System.out.println("Enter any key to create and execute batch");
      }
//      if (manager.getMembers().indexOf(manager.getAddress()) < 1) {
//         for (int i = 0; i < 10; i++) {
//            System.out.println("Start batch: " + (i+1));
//            cache.startBatch();
//            cache.put(i, "Test" + i);
//            cache.endBatch(true);
//            System.out.println("End batch: " + (i+1));
//         }
//      }
   }
}
