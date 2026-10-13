package bypass.module.visuals;

public class Waypoint {
   private final String r;
   private final String a;
   private final int o;
   private final double W;
   private final double T;
   private final double v;

   public Waypoint(String var1, String var2, int var3, double var4, double var6, double var8) {
      this.r = var1;
      this.a = var2;
      this.o = var3;
      this.W = var4;
      this.T = var6;
      this.v = var8;
   }

   public String r() {
      return this.r;
   }

   public String z() {
      return this.a;
   }

   public int k() {
      return this.o;
   }

   public double c() {
      return this.W;
   }

   public double I() {
      return this.T;
   }

   public double K() {
      return this.v;
   }

   public static int L(Waypoint var0) {
      return var0.o;
   }
}
