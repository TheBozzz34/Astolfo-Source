package net.hypixel.api.reply;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class KeyReply extends AbstractReply {
   private KeyReply.Key record;
   private static final long a = 94413542235754L;

   public KeyReply.Key getRecord() {
      return this.record;
   }

   @Override
   public String toString() {
      return "KeyReply{record=" + this.record + "} " + super.toString();
   }

   public class Key {
      private UUID key;
      @SerializedName("owner")
      private UUID ownerUuid;
      private int totalQueries;
      private int queriesInPastMin;
      private static final long b = 7654865742832L;
      private static final long f = -4665032385576828803L;

      public UUID a() {
         return this.key;
      }

      public UUID S() {
         return this.ownerUuid;
      }

      public int R() {
         return this.totalQueries;
      }

      public int F() {
         return this.queriesInPastMin;
      }

      @Override
      public String toString() {
         return "Key{key="
            + this.key
            + ", ownerUuid="
            + this.ownerUuid
            + ", totalQueries="
            + this.totalQueries
            + ", queriesInPastMin="
            + this.queriesInPastMin
            + (int)f;
      }
   }
}
