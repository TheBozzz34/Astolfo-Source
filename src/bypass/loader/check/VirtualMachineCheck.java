package bypass.loader.check;

import bypass.util.Holder;
import bypass.util.collection.ShapedMap;
import java.io.File;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class VirtualMachineCheck {
   private static final ShapedMap<String, String> vmSignatures = new ShapedMap<>();
   private static String[] vmNames;
   private final FileListHolder fileListHolder;
   private boolean flag1;
   private boolean flag2;
   private boolean flag3;
   private boolean flag4 = true;
   private boolean flag5;
   private boolean flag6;

   public VirtualMachineCheck(FileListHolder fileListHolder) {
      this.fileListHolder = fileListHolder;
      vmSignatures.put("bhyve bhyve", "bhyve");
      vmSignatures.put("KVMKVMKVM", "KVM");
      vmSignatures.put("TCGTCGTCGTCG", "QEMU");
      vmSignatures.put("Microsoft Hv", "Microsoft Hyper-V or Windows Virtual PC");
      vmSignatures.put("lrpepyh vr", "Parallels");
      vmSignatures.put("VMwareVMware", "VMware");
      vmSignatures.put("XenVMMXenVMM", "Xen HVM");
      vmSignatures.put("ACRNACRNACRN", "Project ACRN");
      vmSignatures.put("QNXQVMBSQG", "QNX Hypervisor");
      vmNames = new String[]{
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

   public Holder checkVirtualMachine() {
      Holder holder = new Holder();
      holder.value = new NullPointerException("1");
      this.setFlag3(true);

      for (File file : this.fileListHolder.getFileContainer().getOwnedContainer().getValue()) {
         this.setFlag4(!this.flag3);
         if ((file.exists() || !detectVirtualMachine().isEmpty()) && (holder.value = new NullPointerException("123")) != null) {
            this.setFlag1(!this.flag4);
            return holder;
         }
      }

      this.setFlag5(true);
      return holder;
   }

   public static String detectVirtualMachine() {
      SystemInfo systemInfo = new SystemInfo();
      HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
      String string = hardwareAbstractionLayer.getProcessor().getProcessorIdentifier().H().trim();
      if (vmSignatures.containsKey(string)) {
         return vmSignatures.get(string);
      }

      String string2 = hardwareAbstractionLayer.getComputerSystem().z();

      for (String string3 : vmNames) {
         if (string2.contains(string3)) {
            return string3;
         }
      }

      String string4 = hardwareAbstractionLayer.getComputerSystem().V();
      return "Microsoft Corporation".equals(string4) && "Virtual Machine".equals(string2) ? "Microsoft Hyper-V" : "";
   }

   public void setFlag1(boolean bl) {
      this.flag1 = bl;
   }

   public void setFlag2(boolean bl) {
      this.flag2 = bl;
   }

   public void setFlag3(boolean bl) {
      this.flag3 = bl;
   }

   public void setFlag4(boolean bl) {
      this.flag4 = bl;
   }

   public void setFlag5(boolean bl) {
      this.flag5 = bl;
   }

   public void setFlag6(boolean bl) {
      this.flag6 = bl;
   }

   public boolean isFlag1() {
      return this.flag1;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public boolean isFlag4() {
      return this.flag4;
   }

   public boolean isFlag5() {
      return this.flag5;
   }

   public boolean isFlag6() {
      return this.flag6;
   }
}
