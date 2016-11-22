package org.jboss.set;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * @author Ryan Emerson
 * @since 9.0
 */
public class ProtostuffTest {
   public static void main(String[] args) {
      ArrayList<Integer> testList = new ArrayList<>();
//      testList.add("New Test");
//      testList.add("asdasdasd");
//      testList.add("asdasdasdad");
      IntStream.range(0, 10).forEach(testList::add);

      Schema<ArrayList> schema = RuntimeSchema.getSchema(ArrayList.class);
      LinkedBuffer buffer = LinkedBuffer.allocate();
      byte[] bytes;
      try {
         bytes = ProtostuffIOUtil.toByteArray(testList, schema, buffer);
      } finally {
         buffer.clear();
      }

      ArrayList<Integer> newList = new ArrayList<>();
      ProtostuffIOUtil.mergeFrom(bytes, newList, schema);
      System.out.println(newList.size());
      System.out.println(newList);
   }
}
