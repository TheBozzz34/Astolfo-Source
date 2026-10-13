package net.hypixel.api.reply.skyblock;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.hypixel.api.reply.AbstractReply;

public class SkyBlockAuctionsReply extends AbstractReply {
   private int page;
   private int totalPages;
   private int totalAuctions;
   private long lastUpdated;
   private JsonElement auctions;
   private static final long c = 92762293816735L;

   public int C() {
      return this.page;
   }

   public int S() {
      return this.totalPages;
   }

   public int e() {
      return this.totalAuctions;
   }

   public long getLastUpdated() {
      return this.lastUpdated;
   }

   public JsonArray getAuctions() {
      return this.auctions != null && !this.auctions.isJsonNull() ? this.auctions.getAsJsonArray() : null;
   }

   public boolean F() {
      return this.page < this.totalPages - 1;
   }

   public boolean M() {
      return this.page > 0;
   }

   @Override
   public String toString() {
      return "SkyBlockAuctionsReply{page="
         + this.page
         + ", totalPages="
         + this.totalPages
         + ", totalAuctions="
         + this.totalAuctions
         + ", lastUpdated="
         + this.lastUpdated
         + ", auctions="
         + this.auctions
         + "} "
         + super.toString();
   }
}
