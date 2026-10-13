package bypass.util;

import java.util.ArrayList;

public class PathSegmentNode {
   private Vector3 l;
   private PathSegmentNode u;
   private ArrayList<Vector3> C;
   private double T;
   private double o;
   private double n;
   public final PathSegment N;

   public PathSegmentNode(PathSegment var1, Vector3 var2, PathSegmentNode var3, ArrayList var4, double var5, double var7, double var9) {
      this.N = var1;
      this.l = var2;
      this.u = var3;
      this.C = var4;
      this.T = var5;
      this.o = var7;
      this.n = var9;
   }

   private Vector3 D() {
      return this.l;
   }

   private void D(Vector3 var1) {
      this.l = var1;
   }

   public PathSegmentNode M_rtx() {
      return this.u;
   }

   public void l(PathSegmentNode var1) {
      this.u = var1;
   }

   public ArrayList<Vector3> K() {
      return this.C;
   }

   public void P(ArrayList<Vector3> var1) {
      this.C = var1;
   }

   private double f() {
      return this.T;
   }

   private void L(double var1) {
      this.T = var1;
   }

   private double M() {
      return this.o;
   }

   private void s(double var1) {
      this.o = var1;
   }

   private double j() {
      return this.n;
   }

   private void X(double var1) {
      this.n = var1;
   }

   public static Vector3 i(PathSegmentNode var0) {
      return var0.D();
   }

   public static double X(PathSegmentNode var0) {
      return var0.j();
   }

   public static double T(PathSegmentNode var0) {
      return var0.M();
   }

   public static void R(PathSegmentNode var0, Vector3 var1) {
      var0.D(var1);
   }

   public static void z(PathSegmentNode var0, double var1) {
      var0.L(var1);
   }

   public static void s(PathSegmentNode var0, double var1) {
      var0.s(var1);
   }

   public static void K(PathSegmentNode var0, double var1) {
      var0.X(var1);
   }

   public static double z(PathSegmentNode var0) {
      return var0.f();
   }
}
