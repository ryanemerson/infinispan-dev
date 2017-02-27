package org.infinispan.ryan;

import org.infinispan.AdvancedCache;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

/**
 * @author Ryan Emerson
 * @since 9.0
 */
public class JDBCBinaryPopulator {
   public static void main(String[] args) throws Exception {
      EmbeddedCacheManager manager = new DefaultCacheManager("infinispan-local-string-store.xml");
      AdvancedCache<Object, Object> cache = manager.getCache("test").getAdvancedCache();
//      System.out.println(cache.get(1000));
//      IntStream.range(1, 101).forEach(i -> cache.put(i * 1000, i));
      cache.put(1, "String");
      cache.put(2, 20.0);
      cache.put(3, Collections.singleton("Set"));
      cache.put(4, Collections.singletonList("List"));
      cache.put(5, Collections.singletonMap("key", "value"));
      cache.put(6, false);
   }
}
