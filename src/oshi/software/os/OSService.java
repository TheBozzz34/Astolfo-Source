package oshi.software.os;

import oshi.annotation.concurrent.Immutable;

@Immutable
public class OSService {
   private final String name;
   private final int processID;
   private final OSService.State state;

   public OSService(String name, int processID, OSService.State state) {
      this.name = name;
      this.processID = processID;
      this.state = state;
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
