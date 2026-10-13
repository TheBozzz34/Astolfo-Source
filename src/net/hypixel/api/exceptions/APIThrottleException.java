package net.hypixel.api.exceptions;

public class APIThrottleException extends HypixelAPIException {
   private static final long a = 7070081534816L;

   public APIThrottleException() {
      super("You have passed the API throttle limit!");
   }
}
