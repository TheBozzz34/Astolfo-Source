package com.googlecode.concurrentlinkedhashmap;

import java.util.AbstractMap.SimpleEntry;

public final class WriteThroughEntry extends SimpleEntry {
   public static final long serialVersionUID = 1L;
   public final ConcurrentLinkedHashMap S;

   public WriteThroughEntry(ConcurrentLinkedHashMap var1, ConcurrentLinkedHashMap.Node var2) {
      super(var2.key, var2.getValue());
      this.S = var1;
   }

   @Override
   public Object setValue(Object var1) {
      this.S.put(this.getKey(), var1);
      return super.setValue(var1);
   }

   public Object writeReplace() {
      return new SimpleEntry(this);
   }
}
