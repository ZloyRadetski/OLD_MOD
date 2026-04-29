package net.portalmod.core.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Registry<T> {
   private final Map<String, Entry<T>> REGISTRY = new HashMap();

   public <S extends T> Entry<S> register(String path, Supplier<S> resource) {
      Entry<S> entry = new Entry<S>(resource);
      this.REGISTRY.put(path, entry);
      return entry;
   }

   public void registerAll() {
      this.REGISTRY.forEach((name, supplier) -> supplier.get());
   }

   public Map<String, Entry<T>> getRegistry() {
      return this.REGISTRY;
   }

   public static class Entry<T> implements Supplier<T> {
      private final Supplier<T> supplier;
      private T value;

      public Entry(Supplier<T> supplier) {
         this.supplier = supplier;
      }

      public T get() {
         if (this.value == null) {
            this.value = (T)this.supplier.get();
         }

         return this.value;
      }
   }
}
