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
   private boolean swappedToSword;
   private int previousSlot;
   private final Stopwatch switchTimer = new Stopwatch();
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (this.countGoldSwordsInHotbar() <= 0) {
         int i = this.X_int();
         if (i != -1) {
            mc.playerController.windowClick(0, i, this.slot.getValue() - 1, 2, mc.thePlayer);
         } else if (this.swappedToSword) {
            mc.thePlayer.inventory.currentItem = this.previousSlot;
            this.swappedToSword = false;
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING && packetEvent.getPacket() instanceof C02PacketUseEntity) {
         C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)packetEvent.getPacket();
         Entity entity = c02PacketUseEntity.getEntityFromWorld(mc.theWorld);
         if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)c02PacketUseEntity.getEntityFromWorld(mc.theWorld);
            if (c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK && entityLivingBase instanceof EntityPlayer) {
               if (this.teleport.getValue()) {
                  float f = entityLivingBase.rotationYaw + 90.0F + 180.0F;
                  double d = entityLivingBase.posX + Math.cos(Math.toRadians(f));
                  double e = entityLivingBase.posZ + Math.sin(Math.toRadians(f));
                  int i = this.findGroundY((int)d, (int)entityLivingBase.posY, (int)e);
                  if (i != -1) {
                     mc.thePlayer.setPosition(d, i, e);
                  }
               }

               Vec3 vec3 = entityLivingBase.getPositionEyes(1.0F);
               Vec3 vec32 = mc.thePlayer.getPositionEyes(1.0F);
               Vec3 vec33 = vec3.subtract(vec32);
               Vec3 vec34 = new Vec3(
                  entityLivingBase.getHorizontalFacing().getDirectionVec().getX(),
                  entityLivingBase.getHorizontalFacing().getDirectionVec().getY(),
                  entityLivingBase.getHorizontalFacing().getDirectionVec().getZ()
               );
               double d2 = vec33.dotProduct(vec34);
               if (d2 > 0.4) {
                  int j = this.findGoldSwordSlot(-1);
                  if (j != -1) {
                     this.previousSlot = mc.thePlayer.inventory.currentItem;
                     mc.thePlayer.inventory.currentItem = j;
                     this.swappedToSword = true;
                     this.switchTimer.reset();
                  }
               }
            }
         }
      }
   };
   @EventHandler(value = MotionEvent.class, priority = EventPriority.LOWEST)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (motionEvent.getState() == EventState.POST && this.swappedToSword && this.switchTimer.hasElapsed(500.0F)) {
         mc.thePlayer.inventory.currentItem = this.previousSlot;
         this.swappedToSword = false;
      }
   };

   public AutoBackstab(Category category) {
      super(category);
      this.addSettings(this.slot, this.teleport);
   }

   private int findGroundY(int i, int j, int k) {
      for (int l = j + 2; l > j - 2; l--) {
         if (mc.theWorld.getBlockState(new BlockPos(i, l, k)).getBlock().getMaterial() != Material.air) {
            return l + 1;
         }
      }

      return -1;
   }

   private int X_int() {
      for (int i = 9; i <= 35; i++) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (this.isGoldSword(itemStack)) {
               return i;
            }
         }
      }

      return -1;
   }

   private int countGoldSwordsInHotbar() {
      int i = 0;

      for (int j = 0; j < 9; j++) {
         ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(j);
         if (itemStack != null && this.isGoldSword(itemStack)) {
            i += itemStack.stackSize;
         }
      }

      return i;
   }

   private int findGoldSwordSlot(int i) {
      for (int j = 0; j < 9; j++) {
         if (j != i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(j);
            if (itemStack != null && this.isGoldSword(itemStack)) {
               return j;
            }
         }
      }

      return -1;
   }

   private boolean isGoldSword(ItemStack itemStack) {
      if (itemStack == null) {
         return false;
      } else if (itemStack.getItem() instanceof ItemSword) {
         ItemSword itemSword = (ItemSword)itemStack.getItem();
         String string = itemSword.getToolMaterialName();
         return string == null ? false : string.toLowerCase().contains("gold");
      } else {
         return false;
      }
   }
}
