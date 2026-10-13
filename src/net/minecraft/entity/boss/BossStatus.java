package net.minecraft.entity.boss;

public final class BossStatus {
   public static float healthScale;
   public static int statusBarTime;
   public static String bossName;
   public static boolean hasColorModifier;
   private static final long a = 23815293939922L;
   private static final long b = -4780759592172781468L;

   public static void setBossStatus(IBossDisplayData var0, boolean var1) {
      healthScale = var0.getHealth() / var0.getMaxHealth();
      statusBarTime = (int)b;
      bossName = var0.getDisplayName().getFormattedText();
      hasColorModifier = var1;
   }
}
