package bypass.auth;

public class HwidOptions {
   private boolean useBaseboard;
   private boolean useCpu;
   private boolean useDisplays;
   private boolean useUsername;
   private boolean useGpu;
   private boolean useMemory;
   private boolean useDisk;

   public HwidOptions withBaseboard(boolean bl) {
      this.useBaseboard = bl;
      return this;
   }

   public HwidOptions withCpu(boolean bl) {
      this.useCpu = bl;
      return this;
   }

   public HwidOptions withDisplays(boolean bl) {
      this.useDisplays = bl;
      return this;
   }

   public HwidOptions withGpu(boolean bl) {
      this.useGpu = bl;
      return this;
   }

   public HwidOptions withMemory(boolean bl) {
      this.useMemory = bl;
      return this;
   }

   public HwidOptions withDisk(boolean bl) {
      this.useDisk = bl;
      return this;
   }

   public HwidOptions withUsername(boolean bl) {
      this.useUsername = bl;
      return this;
   }

   public HwidBuilder build() {
      return new HwidBuilder(this);
   }

   public static boolean isUsernameEnabled(HwidOptions hwidOptions) {
      return hwidOptions.useUsername;
   }

   public static boolean isBaseboardEnabled(HwidOptions hwidOptions) {
      return hwidOptions.useBaseboard;
   }

   public static boolean isCpuEnabled(HwidOptions hwidOptions) {
      return hwidOptions.useCpu;
   }

   public static boolean isGpuEnabled(HwidOptions hwidOptions) {
      return hwidOptions.useGpu;
   }

   public static boolean isMemoryEnabled(HwidOptions hwidOptions) {
      return hwidOptions.useMemory;
   }

   public static boolean isDiskEnabled(HwidOptions hwidOptions) {
      return hwidOptions.useDisk;
   }

   public static boolean isDisplaysEnabled(HwidOptions hwidOptions) {
      return hwidOptions.useDisplays;
   }
}
