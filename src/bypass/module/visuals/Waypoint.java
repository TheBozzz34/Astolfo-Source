package bypass.module.visuals;

public class Waypoint {
   private final String name;
   private final String server;
   private final int dimension;
   private final double x;
   private final double y;
   private final double z;

   public Waypoint(String string, String string2, int i, double d, double d2, double d3) {
      this.name = string;
      this.server = string2;
      this.dimension = i;
      this.x = d;
      this.y = d2;
      this.z = d3;
   }

   public String getName() {
      return this.name;
   }

   public String getServer() {
      return this.server;
   }

   public int getDimension() {
      return this.dimension;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public static int dimensionOf(Waypoint waypoint) {
      return waypoint.dimension;
   }
}
