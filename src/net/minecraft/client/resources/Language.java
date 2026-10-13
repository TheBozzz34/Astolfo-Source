package net.minecraft.client.resources;

public class Language implements Comparable<Language> {
   private final String languageCode;
   private final String region;
   private final String name;
   private final boolean bidirectional;
   private static final long a = 21946613768247L;

   public Language(String var1, String var2, String var3, boolean var4) {
      this.languageCode = var1;
      this.region = var2;
      this.name = var3;
      this.bidirectional = var4;
   }

   public String getLanguageCode() {
      return this.languageCode;
   }

   public boolean isBidirectional() {
      return this.bidirectional;
   }

   @Override
   public String toString() {
      return String.format("%s (%s)", this.name, this.region);
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 ? true : (!(var1 instanceof Language) ? false : this.languageCode.equals(((Language)var1).languageCode));
   }

   @Override
   public int hashCode() {
      return this.languageCode.hashCode();
   }

   public int compareTo(Language var1) {
      return this.languageCode.compareTo(var1.languageCode);
   }
}
