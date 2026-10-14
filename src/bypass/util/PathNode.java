package bypass.util;

import java.util.ArrayList;

public class PathNode {
   private Vector3 B;
   private PathNode e;
   private ArrayList<Vector3> c;
   private double P;
   private double a;
   private double o;

   public PathNode(Vector3 vector3, PathNode pathNode, ArrayList<Vector3> arrayList, double d, double d2, double d3) {
      this.B = vector3;
      this.e = pathNode;
      this.c = arrayList;
      this.P = d;
      this.a = d2;
      this.o = d3;
   }

   public Vector3 K() {
      return this.B;
   }

   public void p(Vector3 vector3) {
      this.B = vector3;
   }

   public PathNode b() {
      return this.e;
   }

   public void u(PathNode pathNode) {
      this.e = pathNode;
   }

   public ArrayList<Vector3> l() {
      return this.c;
   }

   public void t(ArrayList<Vector3> arrayList) {
      this.c = arrayList;
   }

   public double M() {
      return this.P;
   }

   public void y(double d) {
      this.P = d;
   }

   public double u() {
      return this.a;
   }

   public void b(double d) {
      this.a = d;
   }

   public double d() {
      return this.o;
   }

   public void S(double d) {
      this.o = d;
   }
}
