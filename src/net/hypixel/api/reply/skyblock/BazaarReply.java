package net.hypixel.api.reply.skyblock;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import net.hypixel.api.reply.AbstractReply;

public class BazaarReply extends AbstractReply {
   private long lastUpdated;
   private Map<String, BazaarReply.Product> products;

   public Map<String, BazaarReply.Product> getProducts() {
      return this.products;
   }

   public BazaarReply.Product getProduct(String productId) {
      return this.products.get(productId);
   }

   @Override
   public String toString() {
      return "BazaarReply{lastUpdated=" + this.lastUpdated + ", products=" + this.products + "} " + super.toString();
   }

   public class Product {
      @SerializedName("product_id")
      private String productId;
      @SerializedName("sell_summary")
      private List<BazaarReply.Product.Summary> sellSummary;
      @SerializedName("buy_summary")
      private List<BazaarReply.Product.Summary> buySummary;
      @SerializedName("quick_status")
      private BazaarReply.Product.Status quickStatus;

      public String getProductId() {
         return this.productId;
      }

      public List<BazaarReply.Product.Summary> getSellSummary() {
         return this.sellSummary;
      }

      public List<BazaarReply.Product.Summary> getBuySummary() {
         return this.buySummary;
      }

      public BazaarReply.Product.Status getQuickStatus() {
         return this.quickStatus;
      }

      @Override
      public String toString() {
         return "Product{productId='"
            + this.productId
            + ", sellSummary="
            + this.sellSummary
            + ", buySummary="
            + this.buySummary
            + ", quickStatus="
            + this.quickStatus
            + "}";
      }

      public class Status {
         private String productId;
         private double sellPrice;
         private int sellVolume;
         private int sellMovingWeek;
         private int sellOrders;
         private double buyPrice;
         private int buyVolume;
         private int buyMovingWeek;
         private int buyOrders;

         public String getProductId() {
            return this.productId;
         }

         public double getSellPrice() {
            return this.sellPrice;
         }

         public int getSellVolume() {
            return this.sellVolume;
         }

         public int getSellMovingWeek() {
            return this.sellMovingWeek;
         }

         public int getSellOrders() {
            return this.sellOrders;
         }

         public double getBuyPrice() {
            return this.buyPrice;
         }

         public int getBuyVolume() {
            return this.buyVolume;
         }

         public int getBuyMovingWeek() {
            return this.buyMovingWeek;
         }

         public int getBuyOrders() {
            return this.buyOrders;
         }

         @Override
         public String toString() {
            return "Status{productId='"
               + this.productId
               + ", sellPrice="
               + this.sellPrice
               + ", sellVolume="
               + this.sellVolume
               + ", sellMovingWeek="
               + this.sellMovingWeek
               + ", sellOrders="
               + this.sellOrders
               + ", buyPrice="
               + this.buyPrice
               + ", buyVolume="
               + this.buyVolume
               + ", buyMovingWeek="
               + this.buyMovingWeek
               + ", buyOrders="
               + this.buyOrders
               + "}";
         }
      }

      public class Summary {
         private int amount;
         private double pricePerUnit;
         private int orders;

         public int getAmount() {
            return this.amount;
         }

         public double getPricePerUnit() {
            return this.pricePerUnit;
         }

         public int getOrders() {
            return this.orders;
         }

         @Override
         public String toString() {
            return "Summary{amount=" + this.amount + ", pricePerUnit=" + this.pricePerUnit + ", orders=" + this.orders + "}";
         }
      }
   }
}
