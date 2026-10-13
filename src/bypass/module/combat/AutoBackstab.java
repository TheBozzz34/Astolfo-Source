package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.EventState;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.util.Stopwatch;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public final class AutoBackstab extends Module {
   private final NumberSetting<Integer> slot = new NumberSetting<>("Slot", "Slots where the golden sword will be on hotbar", 9, 1, 9);
   private final BooleanSetting teleport = new BooleanSetting("Teleport", "Teleports behind player", true);
   private boolean z;
   private int D;
   private final Stopwatch O = new Stopwatch();
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      if (this.a() <= 0) {
         int var5 = this.X_int();
         if (var5 != -1) {
            mc.playerController.windowClick(0, var5, this.slot.getValue() - 1, 2, mc.thePlayer);
         } else if (this.z) {
            mc.thePlayer.inventory.currentItem = this.D;
            this.z = false;
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING && var1x.getPacket() instanceof C02PacketUseEntity) {
         C02PacketUseEntity var5 = (C02PacketUseEntity)var1x.getPacket();
         Entity var6 = var5.getEntityFromWorld(mc.theWorld);
         if (var6 instanceof EntityLivingBase) {
            EntityLivingBase var7 = (EntityLivingBase)var5.getEntityFromWorld(mc.theWorld);
            if (var5.getAction() == C02PacketUseEntity.Action.ATTACK && var7 instanceof EntityPlayer) {
               if (this.teleport.getValue()) {
                  float var8 = var7.rotationYaw + 90.0F + 180.0F;
                  double var9 = var7.posX + Math.cos(Math.toRadians(var8));
                  double var11 = var7.posZ + Math.sin(Math.toRadians(var8));
                  int var13 = this.A((int)var9, (int)var7.posY, (int)var11);
                  if (var13 != -1) {
                     mc.thePlayer.setPosition(var9, var13, var11);
                  }
               }

               Vec3 var15 = var7.getPositionEyes(1.0F);
               Vec3 var16 = mc.thePlayer.getPositionEyes(1.0F);
               Vec3 var10 = var15.subtract(var16);
               Vec3 var17 = new Vec3(
                  var7.getHorizontalFacing().getDirectionVec().getX(),
                  var7.getHorizontalFacing().getDirectionVec().getY(),
                  var7.getHorizontalFacing().getDirectionVec().getZ()
               );
               double var12 = var10.dotProduct(var17);
               if (var12 > 0.4) {
                  int var14 = this.m(-1);
                  if (var14 != -1) {
                     this.D = mc.thePlayer.inventory.currentItem;
                     mc.thePlayer.inventory.currentItem = var14;
                     this.z = true;
                     this.O.reset();
                  }
               }
            }
         }
      }
   };
   @EventHandler(value = MotionEvent.class, priority = EventPriority.LOWEST)
   private Listener<MotionEvent> onMotion = var1x -> {
      if (var1x.getState() == EventState.POST && this.z && this.O.hasElapsed(500.0F)) {
         mc.thePlayer.inventory.currentItem = this.D;
         this.z = false;
      }
   };
   private static final long c = 51983800228055L;

   public AutoBackstab(Category var1) {
      super(var1);
      this.addSettings(this.slot, this.teleport);
   }

   private int A(int var1, int var2, int var3) {
      for (int var7 = var2 + 2; var7 > var2 - 2; var7--) {
         if (mc.theWorld.getBlockState(new BlockPos(var1, var7, var3)).getBlock().getMaterial() != Material.air) {
            return var7 + 1;
         }
      }

      return -1;
   }

   private int X_int() {
      for (int var4 = 9; var4 <= 35; var4++) {
         if (mc.thePlayer.inventoryContainer.getSlot(var4).getHasStack()) {
            ItemStack var5 = mc.thePlayer.inventoryContainer.getSlot(var4).getStack();
            if (this.l(var5)) {
               return var4;
            }
         }
      }

      return -1;
   }

   private int a() {
      int var4 = 0;

      for (int var5 = 0; var5 < 9; var5++) {
         ItemStack var6 = mc.thePlayer.inventory.getStackInSlot(var5);
         if (var6 != null && this.l(var6)) {
            var4 += var6.stackSize;
         }
      }

      return var4;
   }

   private int m(int var1) {
      for (int var5 = 0; var5 < 9; var5++) {
         if (var5 != var1) {
            ItemStack var6 = mc.thePlayer.inventory.getStackInSlot(var5);
            if (var6 != null && this.l(var6)) {
               return var5;
            }
         }
      }

      return -1;
   }

   private boolean l(ItemStack var1) {
      if (var1 == null) {
         return false;
      } else if (var1.getItem() instanceof ItemSword) {
         ItemSword var5 = (ItemSword)var1.getItem();
         String var6 = var5.getToolMaterialName();
         return var6 == null ? false : var6.toLowerCase().contains("gold");
      } else {
         return false;
      }
   }
}
