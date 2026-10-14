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

   public VirtualMachineCheck(FileListHolder fileListHolder) {
      this.p = fileListHolder;
      O.put("bhyve bhyve", "bhyve");
      O.put("KVMKVMKVM", "KVM");
      O.put("TCGTCGTCGTCG", "QEMU");
      O.put("Microsoft Hv", "Microsoft Hyper-V or Windows Virtual PC");
      O.put("lrpepyh vr", "Parallels");
      O.put("VMwareVMware", "VMware");
      O.put("XenVMMXenVMM", "Xen HVM");
      O.put("ACRNACRNACRN", "Project ACRN");
      O.put("QNXQVMBSQG", "QNX Hypervisor");
      j = new String[]{
         "Linux KVM",
         "Linux lguest",
         "OpenVZ",
         "Qemu",
         "Microsoft Virtual PC",
         "VMWare",
         "linux-vserver",
         "Xen",
         "FreeBSD Jail",
         "VirtualBox",
         "Parallels",
         "Linux Containers",
         "LXC"
      };
   }

   public Holder U() {
      Holder holder = new Holder();
      holder.value = new NullPointerException("1");
      this.T(true);

      for (File file : this.p.v().L().getValue()) {
         this.q(!this.K);
         if ((file.exists() || !z().isEmpty()) && (holder.value = new NullPointerException("123")) != null) {
            this.m(!this.I);
            return holder;
         }
      }

      this.N(true);
      return holder;
   }

   public static String z() {
      SystemInfo systemInfo = new SystemInfo();
      HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
      String string = hardwareAbstractionLayer.getProcessor().getProcessorIdentifier().H().trim();
      if (O.containsKey(string)) {
         return O.get(string);
      }

      String string2 = hardwareAbstractionLayer.getComputerSystem().z();

      for (String string3 : j) {
         if (string2.contains(string3)) {
            return string3;
         }
      }

      String string4 = hardwareAbstractionLayer.getComputerSystem().V();
      return "Microsoft Corporation".equals(string4) && "Virtual Machine".equals(string2) ? "Microsoft Hyper-V" : "";
   }

   public void m(boolean bl) {
      this.m = bl;
   }

   public void k(boolean bl) {
      this.J = bl;
   }

   public void T(boolean bl) {
      this.K = bl;
   }

   public void q(boolean bl) {
      this.I = bl;
   }

   public void N(boolean bl) {
      this.z = bl;
   }

   public void n(boolean bl) {
      this.P = bl;
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
