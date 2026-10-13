package bypass.module;

public enum Category {
   COMBAT("Combat", -1618884),
   MOVEMENT("Movement", -13710223),
   PLAYER("Player", -7453523),
   EXPLOITS("Exploit", -13330213),
   OTHER("Other", -812014),
   VISUALS("Visual", -13172530),
   SCRIPTS("Scripts", -337057),
   CONFIG("Config", -1),
   HIDDEN("HIDDEN", -1);

   private String displayName;
   private int color;

   Category(String var3, int var4) {
      this.displayName = var3;
      this.color = var4;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public int getColor() {
      return this.color;
   }
}
