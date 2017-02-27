package org.infinispan.ryan;

import org.infinispan.commons.io.ByteBufferImpl;
import org.infinispan.commons.marshall.AdvancedExternalizer;
import org.infinispan.commons.marshall.MarshallUtil;
import org.infinispan.commons.marshall.StreamingMarshaller;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.container.entries.ImmortalCacheEntry;
import org.infinispan.container.entries.ImmortalCacheValue;
import org.infinispan.container.entries.MortalCacheEntry;
import org.infinispan.container.entries.MortalCacheValue;
import org.infinispan.container.entries.TransientCacheEntry;
import org.infinispan.container.entries.TransientCacheValue;
import org.infinispan.container.entries.TransientMortalCacheEntry;
import org.infinispan.container.entries.TransientMortalCacheValue;
import org.infinispan.container.entries.metadata.MetadataImmortalCacheEntry;
import org.infinispan.container.entries.metadata.MetadataImmortalCacheValue;
import org.infinispan.container.entries.metadata.MetadataMortalCacheEntry;
import org.infinispan.container.entries.metadata.MetadataMortalCacheValue;
import org.infinispan.container.entries.metadata.MetadataTransientCacheEntry;
import org.infinispan.container.entries.metadata.MetadataTransientCacheValue;
import org.infinispan.container.entries.metadata.MetadataTransientMortalCacheEntry;
import org.infinispan.container.entries.metadata.MetadataTransientMortalCacheValue;
import org.infinispan.container.versioning.NumericVersion;
import org.infinispan.container.versioning.SimpleClusteredVersion;
import org.infinispan.factories.ComponentRegistry;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.metadata.EmbeddedMetadata;
import org.infinispan.metadata.Metadata;
import org.infinispan.metadata.impl.InternalMetadataImpl;
import org.infinispan.test.data.Key;
import org.infinispan.test.data.Person;
import org.infinispan.util.KeyValuePair;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for generating binary file, needs to be executed with 8.x marshaller.
 */
public class ByteOutputGenerator {
   private static Path OUTPUT_PATH = Paths.get("/home/remerson/workspace/RedHat/infinispan/dev_user/infinispan-jdbc-migrator/target/marshalled_bytes_8.x.bin");

   public static void main(String[] args) throws Exception {
      GlobalConfiguration configuration = new GlobalConfigurationBuilder()
            .serialization().addAdvancedExternalizer(new TestObjectExternalizer()).build();
      EmbeddedCacheManager manager = new DefaultCacheManager(configuration);
      ComponentRegistry registry = manager.getCache().getAdvancedCache().getComponentRegistry();
      StreamingMarshaller marshaller = registry.getCacheMarshaller();



      OutputMap outputMap = new OutputMap(marshaller);
      generateOutput(outputMap);
      Files.write(OUTPUT_PATH, outputMap.getBytes());
   }

   private static void generateOutput(OutputMap map) throws Exception {
      map.put("List", Arrays.asList(new Person("Alan Shearer"), new Person("Nolberto Solano")));
      map.put("SingletonList", Collections.singletonList(new Key("Key", false)));
      map.put("SingletonMap", Collections.singletonMap("Key", "Value"));
      map.put("SingletonSet", Collections.singleton(new Key("Key", false)));
      map.put("KeyValuePair", new KeyValuePair<>("Key", "Value"));
      map.put("ImmortalCacheEntry", new ImmortalCacheEntry("Key", "Value"));
      map.put("MortalCacheEntry", new MortalCacheEntry("Key", "Value", 1, 1));
      map.put("TransientCacheEntry", new TransientCacheEntry("Key", "Value", 1, 1));
      map.put("TransientMortalCacheEntry", new TransientMortalCacheEntry("Key", "Value", 1, 1, 1));
      map.put("ImmortalCacheValue", new ImmortalCacheValue("Value"));
      map.put("MortalCacheValue", new MortalCacheValue("Value", 1, 1));
      map.put("TransientCacheValue", new TransientCacheValue("Value", 1, 1));
      map.put("TransientMortalCacheValue", new TransientMortalCacheValue("Value", 1, 1, 1));

      Metadata metadata = new EmbeddedMetadata.Builder().version(new NumericVersion(1)).build();
      map.put("EmbeddedMetadata", metadata);

      map.put("SimpleClusteredVersion", new SimpleClusteredVersion(1, 1));
      map.put("MetadataImmortalCacheEntry", new MetadataImmortalCacheEntry("Key", "Value", metadata));
      map.put("MetadataMortalCacheEntry", new MetadataMortalCacheEntry("Key", "Value", metadata, 1));
      map.put("MetadataTransientCacheEntry", new MetadataTransientCacheEntry("Key", "Value", metadata, 1));
      map.put("MetadataTransientMortalCacheEntry", new MetadataTransientMortalCacheEntry("Key", "Value", metadata, 1));
      map.put("MetadataImmortalCacheValue", new MetadataImmortalCacheValue("Value", metadata));
      map.put("MetadataMortalCacheValue", new MetadataMortalCacheValue("Value", metadata, 1));
      map.put("MetadataTransientCacheValue", new MetadataTransientCacheValue("Value", metadata, 1));
      map.put("MetadataTransientMortalCacheValue", new MetadataTransientMortalCacheValue("Value", metadata, 1, 1));

      byte[] bytes = "Test".getBytes();
      map.put("ByteBufferImpl", new ByteBufferImpl(bytes, 0, bytes.length));
      map.put("KeyValuePair", new KeyValuePair<>("Key", "Value"));
      InternalMetadataImpl internalMetadata = new InternalMetadataImpl(metadata, 1, 1);
      map.put("InternalMetadataImpl", internalMetadata);

      map.put("CustomExternalizer", new TestObject(1, "Test"));
   }

   private static class OutputMap {
      final Map<String, byte[]> outputMap = new HashMap<>();
      final StreamingMarshaller marshaller;

      OutputMap(StreamingMarshaller marshaller) {
         this.marshaller = marshaller;
      }

      void put(String key, Object object) throws Exception {
         outputMap.put(key, marshaller.objectToByteBuffer(object));
      }

      byte[] getBytes() throws Exception {
         return marshaller.objectToByteBuffer(outputMap);
      }
   }

   private static class TestObject {
      int id;
      String someString;

      TestObject() {
      }

      TestObject(int id, String someString) {
         this.id = id;
         this.someString = someString;
      }
   }

   private static class TestObjectExternalizer implements AdvancedExternalizer<TestObject> {
      @Override
      public Set<Class<? extends TestObject>> getTypeClasses() {
         return Collections.singleton(TestObject.class);
      }

      @Override
      public Integer getId() {
         return 256;
      }

      @Override
      public void writeObject(ObjectOutput objectOutput, TestObject testObject) throws IOException {
         objectOutput.writeInt(testObject.id);
         MarshallUtil.marshallString(testObject.someString, objectOutput);
      }

      @Override
      public TestObject readObject(ObjectInput objectInput) throws IOException, ClassNotFoundException {
         TestObject testObject = new TestObject();
         testObject.id = objectInput.readInt();
         testObject.someString = MarshallUtil.unmarshallString(objectInput);
         return testObject;
      }
   }
}
