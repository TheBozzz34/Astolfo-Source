package bypass.auth.net;

import bypass.loader.check.GuardedConstructor;

public final class ConnectionOptions {
   private int size;
   private String label;
   private String secondaryLabel;
   private String tertiaryLabel;
   private int flags;

   public ConnectionOptions(int i, String string, String string2, String string3) {
      this.size = i;
      this.label = string;
      this.secondaryLabel = string2;
      this.tertiaryLabel = string3;
   }

   public void setSize(int i) {
      this.size = i;
   }

   public void setLabel(String string) {
      this.label = string;
   }

   public void setSecondaryLabel(String string) {
      this.secondaryLabel = string;
   }

   public void setTertiaryLabel(String string) {
      this.tertiaryLabel = string;
   }

   public int getSize() {
      return this.size;
   }

   public String getLabel() {
      return this.label;
   }

   public String getSecondaryLabel() {
      return this.secondaryLabel;
   }

   public String getTertiaryLabel() {
      return this.tertiaryLabel;
   }

   public int getFlags() {
      return this.flags;
   }

   public Object constructGuarded(Class<?> class2, Object... objects) {
      return GuardedConstructor.newInstance(class2, objects);
   }

   public static ConnectionOptions ofSize(int i) {
      return new ConnectionOptions(i, null, null, null);
   }

   public static ConnectionOptions ofSizeAndLabel(int i, String string) {
      return new ConnectionOptions(i, string, null, null);
   }

   public static ConnectionOptions createDefault() {
      return ofSize(0);
   }
}
