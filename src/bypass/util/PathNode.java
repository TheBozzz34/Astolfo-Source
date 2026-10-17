package bypass.util;

import java.util.ArrayList;

public class PathNode {
   private Vector3 position;
   private PathNode parent;
   private ArrayList<Vector3> path;
   private double distanceToGoalSq;
   private double stepCost;
   private double totalCost;

   public PathNode(Vector3 vector3, PathNode pathNode, ArrayList<Vector3> arrayList, double d, double e, double d2) {
      this.position = vector3;
      this.parent = pathNode;
      this.path = arrayList;
      this.distanceToGoalSq = d;
      this.stepCost = e;
      this.totalCost = d2;
   }

   public Vector3 getPosition() {
      return this.position;
   }

   public void setPosition(Vector3 vector3) {
      this.position = vector3;
   }

   public PathNode getParent() {
      return this.parent;
   }

   public void setParent(PathNode pathNode) {
      this.parent = pathNode;
   }

   public ArrayList<Vector3> getPath() {
      return this.path;
   }

   public void setPath(ArrayList<Vector3> arrayList) {
      this.path = arrayList;
   }

   public double getDistanceToGoalSq() {
      return this.distanceToGoalSq;
   }

   public void setDistanceToGoalSq(double d) {
      this.distanceToGoalSq = d;
   }

   public double getStepCost() {
      return this.stepCost;
   }

   public void setStepCost(double d) {
      this.stepCost = d;
   }

   public double getTotalCost() {
      return this.totalCost;
   }

   public void setTotalCost(double d) {
      this.totalCost = d;
   }
}
