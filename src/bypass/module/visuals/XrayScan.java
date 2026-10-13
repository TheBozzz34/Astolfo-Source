package bypass.module.visuals;

public class XrayScan {
   private final Runnable task;
   private boolean done;
   private int scanned;
   private double total;
   public final Xray xray;

   public XrayScan(Xray var1, Runnable var2) {
      this.xray = var1;
      this.task = var2;
   }

   public static boolean isDone(XrayScan var0) {
      return var0.done;
   }

   public static int getScanned(XrayScan var0) {
      return var0.scanned;
   }

   public static double getTotal(XrayScan var0) {
      return var0.total;
   }

   public static double setTotal(XrayScan var0, double var1) {
      return var0.total = var1;
   }

   public static int incrementScanned(XrayScan var0) {
      return var0.scanned++;
   }

   public static boolean setDone(XrayScan var0, boolean var1) {
      return var0.done = var1;
   }
}
