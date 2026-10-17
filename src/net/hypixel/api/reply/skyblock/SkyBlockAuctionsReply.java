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

   public int getPage() {
      return this.page;
   }

   public int getTotalPages() {
      return this.totalPages;
   }

   public int getTotalAuctions() {
      return this.totalAuctions;
   }

   public long getLastUpdated() {
      return this.lastUpdated;
   }

   public JsonArray getAuctions() {
      return this.auctions != null && !this.auctions.isJsonNull() ? this.auctions.getAsJsonArray() : null;
   }

   public boolean hasNextPage() {
      return this.page < this.totalPages - 1;
   }

   public boolean hasPrevPage() {
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
