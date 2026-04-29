package net.portalmod.core.math;

import java.util.HashMap;

public class TriHashMap<K1, K2, K3, V> {
   private final HashMap<K1, HashMap<K2, HashMap<K3, V>>> MAP = new HashMap();

   public V get(K1 k1, K2 k2, K3 k3) {
      HashMap<K2, HashMap<K3, V>> map1 = new HashMap();
      HashMap<K3, V> map2 = new HashMap();
      if (this.MAP.containsKey(k1)) {
         map1 = (HashMap)this.MAP.get(k1);
      }

      if (map1.containsKey(k2)) {
         map2 = (HashMap)map1.get(k2);
      }

      return (V)map2.get(k3);
   }

   public void put(K1 k1, K2 k2, K3 k3, V value) {
      HashMap<K2, HashMap<K3, V>> map1 = new HashMap();
      HashMap<K3, V> map2 = new HashMap();
      if (this.MAP.containsKey(k1)) {
         map1 = (HashMap)this.MAP.get(k1);
      }

      if (map1.containsKey(k2)) {
         map2 = (HashMap)map1.get(k2);
      }

      map2.put(k3, value);
      map1.put(k2, map2);
      this.MAP.put(k1, map1);
   }
}
