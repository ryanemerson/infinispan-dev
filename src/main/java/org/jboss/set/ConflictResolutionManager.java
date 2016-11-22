package org.jboss.set;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.context.Flag;
import org.infinispan.distribution.DistributionManager;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;

import java.util.Scanner;

/**
 * @author Ryan Emerson
 * @since 9.0
 */
public class ConflictResolutionManager {
   public static void main(String[] args) throws Exception {
      System.setProperty("jdbc.upsert.disabled", "false");
      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-distributed.xml");
      Cache<Integer, String> cache = manager.getCache("testCache");
      AdvancedCache<Integer, String> advancedCache = cache.getAdvancedCache();
      DistributionManager distributionManager = advancedCache.getDistributionManager();

      Address localAddress = manager.getAddress();
      System.out.println("This node: " + localAddress);
      System.out.println("Members: " + manager.getMembers());
      System.out.println("Is join complete: " + distributionManager.isJoinComplete());
      while (!distributionManager.isJoinComplete()) {
         Thread.sleep(1000);
      }

      if (manager.getMembers().get(0).equals(localAddress)) {
         advancedCache.put(1, "Test Input1");
         advancedCache.put(2, "Test Input2");
         advancedCache.put(3, "Test Input3");
      }

       Scanner reader = new Scanner(System.in);
      while (reader.hasNext()) {
         System.out.println(manager.getTransport().getMembers() + " | Enter input: ");
         String input = reader.next();
         advancedCache.put(4, input);
         System.out.println(advancedCache.get(4));
         advancedCache.put(1, "Test Input1");
         advancedCache.withFlags(Flag.CACHE_MODE_LOCAL).put(1, "LOCAL_VAL");
//         ConflictResolutionManager<Integer, String> crm = advancedCache.getConflictResolutionManager();
//         crm.getConflicts().forEach(System.out::println);

//         System.out.println(crm.getAllVersions(1));
//         System.out.println("Local is: " + advancedCache.withFlags(Flag.CACHE_MODE_LOCAL).get(1));
      }
   }
}
