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

   public PathSegmentNode(PathSegment pathSegment, Vector3 vector3, PathSegmentNode pathSegmentNode, ArrayList arrayList, double d, double d2, double d3) {
      this.N = pathSegment;
      this.l = vector3;
      this.u = pathSegmentNode;
      this.C = arrayList;
      this.T = d;
      this.o = d2;
      this.n = d3;
   }

   private Vector3 D() {
      return this.l;
   }

   private void D(Vector3 vector3) {
      this.l = vector3;
   }

   public PathSegmentNode M_rtx() {
      return this.u;
   }

   public void l(PathSegmentNode pathSegmentNode) {
      this.u = pathSegmentNode;
   }

   public ArrayList<Vector3> K() {
      return this.C;
   }

   public void P(ArrayList<Vector3> arrayList) {
      this.C = arrayList;
   }

   private double f() {
      return this.T;
   }

   private void L(double d) {
      this.T = d;
   }

   private double M() {
      return this.o;
   }

   private void s(double d) {
      this.o = d;
   }

   private double j() {
      return this.n;
   }

   private void X(double d) {
      this.n = d;
   }

   public static Vector3 i(PathSegmentNode pathSegmentNode) {
      return pathSegmentNode.D();
   }

   public static double X(PathSegmentNode pathSegmentNode) {
      return pathSegmentNode.j();
   }

   public static double T(PathSegmentNode pathSegmentNode) {
      return pathSegmentNode.M();
   }

   public static void R(PathSegmentNode pathSegmentNode, Vector3 vector3) {
      pathSegmentNode.D(vector3);
   }

   public static void z(PathSegmentNode pathSegmentNode, double d) {
      pathSegmentNode.L(d);
   }

   public static void s(PathSegmentNode pathSegmentNode, double d) {
      pathSegmentNode.s(d);
   }

   public static void K(PathSegmentNode pathSegmentNode, double d) {
      pathSegmentNode.X(d);
   }

   public static double z(PathSegmentNode pathSegmentNode) {
      return pathSegmentNode.f();
   }
}
