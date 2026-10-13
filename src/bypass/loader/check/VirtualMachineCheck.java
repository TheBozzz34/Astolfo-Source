package bypass.loader.check;

import bypass.util.Holder;
import bypass.util.collection.ShapedMap;
import java.io.File;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class VirtualMachineCheck {
   private static final ShapedMap<String, String> O = new ShapedMap<>();
   private static String[] j;
   private final FileListHolder p;
   private boolean m;
   private boolean J;
   private boolean K;
   private boolean I = true;
   private boolean z;
   private boolean P;
   private static final long a = 123813266773985L;

   public VirtualMachineCheck(FileListHolder var1) {
      this.p = var1;
      O.put(VirtualMachineCheckStrings.g("5fSmFkE3oW4K2Y0="), VirtualMachineCheckStrings.g("5fSmFkE="));
      O.put(VirtualMachineCheckStrings.g("zMqSK3JaiFA+"), VirtualMachineCheckStrings.g("zMqS"));
      O.put(VirtualMachineCheckStrings.g("09+YNGdQl0U0+6vO"), VirtualMachineCheckStrings.g("1tmSNQ=="));
      O.put(VirtualMachineCheckStrings.g("yvW8EktkrGAHj6D/"), VirtualMachineCheckStrings.g("yvW8EktkrGAHj6DwVxrJdL6gxol9j72eKHzFe2dcj6ojyI6g6kzC"));
      O.put(VirtualMachineCheckStrings.g("6+6vBVRuqyYF3Q=="), VirtualMachineCheckStrings.g("1/2tAUh7pmoA"));
      O.put(VirtualMachineCheckStrings.g("0dGoAVZylUsEzprs"), VirtualMachineCheckStrings.g("0dGoAVZy"));
      O.put(VirtualMachineCheckStrings.g("3/mxNmlam2Md+aXE"), VirtualMachineCheckStrings.g("3/mxQGxBjg=="));
      O.put(VirtualMachineCheckStrings.g("xt+NLmVUkUgy7LrH"), VirtualMachineCheckStrings.g("1+6wCkF0tyYy7LrH"));
      O.put(VirtualMachineCheckStrings.g("1tKHMXJagVUi6A=="), VirtualMachineCheckStrings.g("1tKHQGxus2MB2YH6SA0="));
      j = new String[]{
         VirtualMachineCheckStrings.g("y/WxFVw3iFA+"),
         VirtualMachineCheckStrings.g("y/WxFVw3r2EGypv9"),
         VirtualMachineCheckStrings.g("yOy6DnJN"),
         VirtualMachineCheckStrings.g("1vmyFQ=="),
         VirtualMachineCheckStrings.g("yvW8EktkrGAHj77gVQvOOISg+bg="),
         VirtualMachineCheckStrings.g("0dGIAVZy"),
         VirtualMachineCheckStrings.g("6/WxFVw6tXUW3Z7sVQ=="),
         VirtualMachineCheckStrings.g("3/mx"),
         VirtualMachineCheckStrings.g("we66BWZEhyY5zoHl"),
         VirtualMachineCheckStrings.g("0fWtFFF2r0Qc1w=="),
         VirtualMachineCheckStrings.g("1/2tAUh7pmoA"),
         VirtualMachineCheckStrings.g("y/WxFVw3gGkd24ngSRrJKg=="),
         VirtualMachineCheckStrings.g("y8Sc")
      };
   }

   public Holder U() {
      Holder var4 = new Holder();
      var4.value = new NullPointerException("1");
      this.T(true);

      for (File var6 : this.p.v().L().getValue()) {
         this.q(!this.K);
         if ((var6.exists() || !z().isEmpty()) && (var4.value = new NullPointerException("123")) != null) {
            this.m(!this.I);
            return var4;
         }
      }

      this.N(true);
      return var4;
   }

   public static String z() {
      SystemInfo var3 = new SystemInfo();
      HardwareAbstractionLayer var4 = var3.getHardware();
      String var5 = var4.getProcessor().getProcessorIdentifier().H().trim();
      if (O.containsKey(var5)) {
         return O.get(var5);
      }

      String var6 = var4.getComputerSystem().z();

      for (String var10 : j) {
         if (var6.contains(var10)) {
            return var10;
         }
      }

      String var11 = var4.getComputerSystem().V();
      return VirtualMachineCheckStrings.g("yvW8EktkrGAHj6vmVQ/UK4n0wJQz").equals(var11) && VirtualMachineCheckStrings.g("0fWtFFF2ryY+zovhThHe").equals(var6)
         ? VirtualMachineCheckStrings.g("yvW8EktkrGAHj6DwVxrJdL4=")
         : "";
   }

   public void m(boolean var1) {
      this.m = var1;
   }

   public void k(boolean var1) {
      this.J = var1;
   }

   public void T(boolean var1) {
      this.K = var1;
   }

   public void q(boolean var1) {
      this.I = var1;
   }

   public void N(boolean var1) {
      this.z = var1;
   }

   public void n(boolean var1) {
      this.P = var1;
   }

   public boolean S() {
      return this.m;
   }

   public boolean Y() {
      return this.J;
   }

   public boolean E() {
      return this.K;
   }

   public boolean X() {
      return this.I;
   }

   public boolean w() {
      return this.z;
   }

   public boolean Z() {
      return this.P;
   }
}
