package bypass.util;

import java.util.ArrayList;

public class PathNode {
   private Vector3 B;
   private PathNode e;
   private ArrayList<Vector3> c;
   private double P;
   private double a;
   private double o;

   public PathNode(Vector3 var1, PathNode var2, ArrayList<Vector3> var3, double var4, double var6, double var8) {
      this.B = var1;
      this.e = var2;
      this.c = var3;
      this.P = var4;
      this.a = var6;
      this.o = var8;
   }

   public Vector3 K() {
      return this.B;
   }

   public void p(Vector3 var1) {
      this.B = var1;
   }

   public PathNode b() {
      return this.e;
   }

   public void u(PathNode var1) {
      this.e = var1;
   }

   public ArrayList<Vector3> l() {
      return this.c;
   }

   public void t(ArrayList<Vector3> var1) {
      this.c = var1;
   }

   public double M() {
      return this.P;
   }

   public void y(double var1) {
      this.P = var1;
   }

   public double u() {
      return this.a;
   }

   public void b(double var1) {
      this.a = var1;
   }

   public double d() {
      return this.o;
   }

   public void S(double var1) {
      this.o = var1;
   }
}
