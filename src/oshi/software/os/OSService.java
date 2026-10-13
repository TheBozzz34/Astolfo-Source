package oshi.software.os;

import oshi.annotation.concurrent.Immutable;

@Immutable
public class OSService {
   private final String name;
   private final int processID;
   private final OSService.State state;

   public OSService(String var1, int var2, OSService.State var3) {
      this.name = var1;
      this.processID = var2;
      this.state = var3;
   }

   public String getName() {
      return this.name;
   }

   public int getProcessID() {
      return this.processID;
   }

   public OSService.State getState() {
      return this.state;
   }

   public enum State {
      RUNNING,
      STOPPED,
      OTHER;
   }
}
