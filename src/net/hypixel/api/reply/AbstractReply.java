package net.hypixel.api.reply;

import com.google.gson.annotations.SerializedName;

public abstract class AbstractReply {
   @SerializedName("throttle")
   public boolean throttle;
   @SerializedName("success")
   public boolean success;
   @SerializedName("cause")
   public String cause;

   public boolean isThrottle() {
      return this.throttle;
   }

   public boolean isSuccess() {
      return this.success;
   }

   public String getCause() {
      return this.cause;
   }

   @Override
   public String toString() {
      return "AbstractReply{throttle=" + this.throttle + ", success=" + this.success + ", cause='" + this.cause + '\'' + '}';
   }
}
