package org.jboss.set;

import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.jgroups.util.Util;

import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Ryan Emerson
 */
public class JDBCBenchmark {

   public static final int NUMBER_OF_THREADS = 100;
   public static final int TOTAL_OPERATIONS = 1;
   public static final double READ_PERCENTAGE = 0;

   public static void main(String[] args) throws Exception {
////      System.setProperty("jdbc.c3p0.force", "true");
      benchmark();
//      txStoreCache();
   }

   private static void benchmark() throws Exception {
      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-local.xml");
//      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-local-8.2.xml");
      Cache<Integer, TestObject> cache = manager.getCache("localCacheNoTx");
      System.out.println(cache.get(1));

      cache.stop();
//      cache.put(1, new TestObject(-999));

////      Cache<Integer, TestObject> txCache = manager.getCache("localCacheWithTxStore");
//      ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
//      try {
//         executeOnCache(cache, executorService);
////         executeOnCache(txCache, executorService);
//      } finally {
//         executorService.shutdown();
//      }
   }

   public static void txStoreCache() throws Exception {
      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-local.xml");
      Cache<Integer, TestObject> txCache = manager.getCache("txStoreCache");
      System.out.println("Cache: " + txCache.getAdvancedCache());

      TransactionManager tm = txCache.getAdvancedCache().getTransactionManager();
      tm.begin();
      txCache.put(1, new TestObject(1));
      tm.commit();

      Cache<Integer, TestObject> nonTxCache = manager.getCache("nonTxStoreCache");
//      tm = nonTxCache.getAdvancedCache().getTransactionManager();
//      tm.begin();
      nonTxCache.put(1, new TestObject(1));
//      tm.commit();
   }

   public static void executeOnCache(Cache cache, ExecutorService executorService) throws Exception {
      CountDownLatch complete = new CountDownLatch(NUMBER_OF_THREADS);
      AtomicInteger numberOfOperations = new AtomicInteger();
      AtomicInteger reads = new AtomicInteger();
      AtomicInteger writes = new AtomicInteger();

      long st = System.nanoTime();
      for (int i = 0; i < NUMBER_OF_THREADS; i++) {
         executorService.submit(() -> {
            while (true) {
               int ops = numberOfOperations.getAndIncrement();
               if (ops >= TOTAL_OPERATIONS)
                  break;

               boolean read = Util.tossWeightedCoin(READ_PERCENTAGE);
               if (read) {
                  cache.get(1);
                  reads.incrementAndGet();
               } else {
                  cache.put(ops, new TestObject(ops));
                  writes.incrementAndGet();
               }
            }
            complete.countDown();
         });
      }
      complete.await();
      long tt = System.nanoTime() - st;
      String output = String.format("TimeTaken %s milliseconds | #WRITES %s | #READS %s",
                                    TimeUnit.NANOSECONDS.toMillis(tt), writes, reads);
      System.out.println("----------------------------------------------------------");
      System.out.println(cache.getName());
      System.out.println(output);
//      cache.shutdown();
   }

   static class TestObject implements Serializable {
      private int count;

      public TestObject(int count) {
         this.count = count;
      }

      @Override
      public String toString() {
         return "TestObject{" +
               "count=" + count +
               '}';
      }
   }
}
