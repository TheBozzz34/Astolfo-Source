package net.minecraft.server.management;

import com.google.gson.JsonObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BanEntry<T> extends UserListEntry<T> {
   public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   public final Date banStartDate;
   public final String bannedBy;
   public final Date banEndDate;
   public final String reason;
   private static final long a = 39068475843998L;

   public BanEntry(T var1, Date var2, String var3, Date var4, String var5) {
      super((T)var1);
      this.banStartDate = var2 == null ? new Date() : var2;
      this.bannedBy = var3 == null ? "(Unknown)" : var3;
      this.banEndDate = var4;
      this.reason = var5 == null ? "Banned by an operator." : var5;
   }

   public BanEntry(T var1, JsonObject var2) {
      super((T)var1, var2);

      Date var6;
      try {
         var6 = var2.has("created") ? dateFormat.parse(var2.get("created").getAsString()) : new Date();
      } catch (ParseException var10) {
         var6 = new Date();
      }

      this.banStartDate = var6;
      this.bannedBy = var2.has("source") ? var2.get("source").getAsString() : "(Unknown)";

      Date var7;
      try {
         var7 = var2.has("expires") ? dateFormat.parse(var2.get("expires").getAsString()) : null;
      } catch (ParseException var9) {
         var7 = null;
      }

      this.banEndDate = var7;
      this.reason = var2.has("reason") ? var2.get("reason").getAsString() : "Banned by an operator.";
   }

   public Date getBanEndDate() {
      return this.banEndDate;
   }

   public String getBanReason() {
      return this.reason;
   }

   @Override
   public boolean hasBanExpired() {
      return this.banEndDate == null ? false : this.banEndDate.before(new Date());
   }

   @Override
   public void onSerialization(JsonObject var1) {
      var1.addProperty("created", dateFormat.format(this.banStartDate));
      var1.addProperty("source", this.bannedBy);
      var1.addProperty("expires", this.banEndDate == null ? "forever" : dateFormat.format(this.banEndDate));
      var1.addProperty("reason", this.reason);
   }
}
