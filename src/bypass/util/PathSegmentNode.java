package bypass.util;

import java.util.ArrayList;

public class PathSegmentNode {
   private Vector3 position;
   private PathSegmentNode parent;
   private ArrayList<Vector3> path;
   private double heuristic;
   private double stepCost;
   private double totalCost;
   public final PathSegment segment;

   public PathSegmentNode(PathSegment pathSegment, Vector3 vector3, PathSegmentNode pathSegmentNode, ArrayList arrayList, double d, double e, double d2) {
      this.segment = pathSegment;
      this.position = vector3;
      this.parent = pathSegmentNode;
      this.path = arrayList;
      this.heuristic = d;
      this.stepCost = e;
      this.totalCost = d2;
   }

   private Vector3 getPosition() {
      return this.position;
   }

   private void setPosition(Vector3 vector3) {
      this.position = vector3;
   }

   public PathSegmentNode getParent() {
      return this.parent;
   }

   public void setParent(PathSegmentNode pathSegmentNode) {
      this.parent = pathSegmentNode;
   }

   public ArrayList<Vector3> getPath() {
      return this.path;
   }

   public void setPath(ArrayList<Vector3> arrayList) {
      this.path = arrayList;
   }

   private double getHeuristic() {
      return this.heuristic;
   }

   private void setHeuristic(double d) {
      this.heuristic = d;
   }

   private double getStepCost() {
      return this.stepCost;
   }

   private void setStepCost(double d) {
      this.stepCost = d;
   }

   private double getTotalCost() {
      return this.totalCost;
   }

   private void setTotalCost(double d) {
      this.totalCost = d;
   }

   public static Vector3 getPositionOf(PathSegmentNode pathSegmentNode) {
      return pathSegmentNode.getPosition();
   }

   public static double getTotalCostOf(PathSegmentNode pathSegmentNode) {
      return pathSegmentNode.getTotalCost();
   }

   public static double getStepCostOf(PathSegmentNode pathSegmentNode) {
      return pathSegmentNode.getStepCost();
   }

   public static void setPositionOf(PathSegmentNode pathSegmentNode, Vector3 vector3) {
      pathSegmentNode.setPosition(vector3);
   }

   public static void setHeuristicOf(PathSegmentNode pathSegmentNode, double d) {
      pathSegmentNode.setHeuristic(d);
   }

   public static void setStepCostOf(PathSegmentNode pathSegmentNode, double d) {
      pathSegmentNode.setStepCost(d);
   }

   public static void setTotalCostOf(PathSegmentNode pathSegmentNode, double d) {
      pathSegmentNode.setTotalCost(d);
   }

   public static double getHeuristicOf(PathSegmentNode pathSegmentNode) {
      return pathSegmentNode.getHeuristic();
   }
}
