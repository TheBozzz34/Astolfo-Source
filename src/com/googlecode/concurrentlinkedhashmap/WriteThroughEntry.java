package com.googlecode.concurrentlinkedhashmap;

import java.util.AbstractMap.SimpleEntry;

public final class WriteThroughEntry extends SimpleEntry {
   public static final long serialVersionUID = 1L;
   public final ConcurrentLinkedHashMap map;

   public WriteThroughEntry(ConcurrentLinkedHashMap concurrentLinkedHashMap, ConcurrentLinkedHashMap.Node node) {
      super(node.key, node.getValue());
      this.map = concurrentLinkedHashMap;
   }

   @Override
   public Object setValue(Object object) {
      this.map.put(this.getKey(), object);
      return super.setValue(object);
   }

   public Object writeReplace() {
      return new SimpleEntry(this);
   }
}
