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

   public int getStaffRollingDaily() {
      return this.staffRollingDaily;
   }

   public int getStaffTotal() {
      return this.staffTotal;
   }

   public int getWatchdogTotal() {
      return this.watchdogTotal;
   }

   public int getWatchdogLastMinute() {
      return this.watchdogLastMinute;
   }

   public int getWatchdogRollingDaily() {
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
