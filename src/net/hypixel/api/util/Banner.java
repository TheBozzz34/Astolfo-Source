package net.hypixel.api.util;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Banner {
   @SerializedName("Base")
   private String base;
   @SerializedName("Patterns")
   private List<Banner.Pattern> patterns;
   private static final long a = 81928891541161L;

   public String getBase() {
      return this.base;
   }

   public List<Banner.Pattern> getPatterns() {
      return this.patterns;
   }

   @Override
   public String toString() {
      return "Banner{base='" + this.base + '\'' + ", patterns=" + this.patterns + '}';
   }

   public static class Pattern {
      @SerializedName("Pattern")
      private String pattern;
      @SerializedName("Color")
      private String color;
      private static final long a = 92412467323745L;

      public String F() {
         return this.pattern;
      }

      public String e() {
         return this.color;
      }

      @Override
      public String toString() {
         return "Pattern{pattern='" + this.pattern + '\'' + ", color='" + this.color + '\'' + '}';
      }
   }
}
