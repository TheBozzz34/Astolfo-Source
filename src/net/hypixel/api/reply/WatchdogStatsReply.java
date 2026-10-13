package net.hypixel.api.reply;

import com.google.gson.annotations.SerializedName;

public class WatchdogStatsReply extends AbstractReply {
   @SerializedName("staff_rollingDaily")
   private int staffRollingDaily;
   @SerializedName("staff_total")
   private int staffTotal;
   @SerializedName("watchdog_total")
   private int watchdogTotal;
   @SerializedName("watchdog_lastMinute")
   private int watchdogLastMinute;
   @SerializedName("watchdog_rollingDaily")
   private int watchdogRollingDaily;
   private static final long a = 139738386968404L;

   public int I() {
      return this.staffRollingDaily;
   }

   public int s() {
      return this.staffTotal;
   }

   public int G() {
      return this.watchdogTotal;
   }

   public int n() {
      return this.watchdogLastMinute;
   }

   public int c() {
      return this.watchdogRollingDaily;
   }

   @Override
   public String toString() {
      return "WatchdogStatsReply{staffRollingDaily="
         + this.staffRollingDaily
         + ", staffTotal="
         + this.staffTotal
         + ", watchdogTotal="
         + this.watchdogTotal
         + ", watchdogLastMinute="
         + this.watchdogLastMinute
         + ", watchdogRollingDaily="
         + this.watchdogRollingDaily
         + "} "
         + super.toString();
   }
}
