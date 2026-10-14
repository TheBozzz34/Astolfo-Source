package bypass.module.visuals;

public class XrayScan {
   private final Runnable task;
   private boolean done;
   private int scanned;
   private double total;
   public final Xray xray;

   public XrayScan(Xray xray, Runnable runnable) {
      this.xray = xray;
      this.task = runnable;
   }

   public static boolean isDone(XrayScan xrayScan) {
      return xrayScan.done;
   }

   public static int getScanned(XrayScan xrayScan) {
      return xrayScan.scanned;
   }

   public static double getTotal(XrayScan xrayScan) {
      return xrayScan.total;
   }

   public static double setTotal(XrayScan xrayScan, double d) {
      return xrayScan.total = d;
   }

   public static int incrementScanned(XrayScan xrayScan) {
      return xrayScan.scanned++;
   }

   public static boolean setDone(XrayScan xrayScan, boolean bl) {
      return xrayScan.done = bl;
   }
}
