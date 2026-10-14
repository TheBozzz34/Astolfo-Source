package bypass.module.visuals;

public class Waypoint {
   private final String r;
   private final String a;
   private final int o;
   private final double W;
   private final double T;
   private final double v;

   public Waypoint(String string, String string2, int i, double d, double d2, double d3) {
      this.r = string;
      this.a = string2;
      this.o = i;
      this.W = d;
      this.T = d2;
      this.v = d3;
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

   public static int L(Waypoint waypoint) {
      return waypoint.o;
   }
}
