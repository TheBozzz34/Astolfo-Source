package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.combat.KillAura;
import bypass.module.other.AntiObsidian;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.util.InventoryUtil;
import bypass.util.Stopwatch;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.util.BlockPos;

public final class AutoTool extends Module {
   private final NumberSetting<Integer> slot = new NumberSetting<>("Slot", "Slots where the tool will be on hotbar", 9, 1, 9);
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Delay in milliseconds between switching items", 250, 0, 1000, "ms");
   private final BooleanSetting swordOnly = new BooleanSetting("Sword-Only", "Only uses sword for attacking", true);
   private final BooleanSetting withKillAura = new BooleanSetting("With-KillAura", "Works with KillAura", true);
   private final BooleanSetting hotbarMove = new BooleanSetting("Hotbar-Move", "Moves items to hotbar automatically", true);
   private final BooleanSetting smartPickaxe = new BooleanSetting("Smart-Pickaxe", "Uses better pickaxes only on ores that it can mine", true);
   private final Stopwatch x = new Stopwatch();
   @EventHandler(value = PacketEvent.class, priority = EventPriority.LOW)
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (client.getModuleManager().get(KillAura.class).isEnabled() && !this.withKillAura.getValue() && !AntiObsidian.C()) {
            return;
         }

         if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity)packetEvent.getPacket();
            Entity entity = c02PacketUseEntity.getEntityFromWorld(mc.theWorld);
            switch (c02PacketUseEntity.getAction()) {
               case ATTACK:
                  if (client.getModuleManager().get(AntiObsidian.class).isEnabled() && AntiObsidian.C() || mc.playerController.isHittingBlock) {
                     return;
                  }

                  if (entity instanceof EntityLivingBase) {
                     int i = this.G(this.swordOnly.getValue());
                     if (i != -1) {
                        this.H(i);
                     }
                  }
                  break;
               case INTERACT:
                  if (entity instanceof EntityLivingBase) {
                     if (entity instanceof EntityCow) {
                        int j = p(Items.bucket);
                        if (j != -1 && ((ItemBucket)mc.thePlayer.inventoryContainer.getSlot(j).getStack().getItem()).N() == Blocks.air) {
                           this.H(j);
                        }
                     }

                     if (entity instanceof EntitySheep) {
                        EntitySheep entitySheep = (EntitySheep)entity;
                        if (!entitySheep.getSheared()) {
                           int k = p(Items.shears);
                           if (k != -1) {
                              this.H(k);
                           }
                        }
                     }
                  }
            }
         }

         if (packetEvent.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging c07PacketPlayerDigging = (C07PacketPlayerDigging)packetEvent.getPacket();
            if (c07PacketPlayerDigging.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
               if (mc.playerController.isInCreativeMode()) {
                  return;
               }

               int l = this.Z(c07PacketPlayerDigging.getPosition());
               if (l != -1) {
                  this.H(l);
               }
            }
         }

         if (packetEvent.getPacket() instanceof C0EPacketClickWindow) {
            this.x.reset();
         }
      }
   };

   public AutoTool(Category category) {
      super(category);
      this.addSettings(this.slot, this.delay, this.swordOnly, this.withKillAura, this.hotbarMove, this.smartPickaxe);
   }

   private void H(int i) {
      if (i >= 36 && i <= 44) {
         mc.thePlayer.inventory.currentItem = i - 36;
         mc.playerController.u();
      } else if (this.hotbarMove.getValue() && this.x.hasElapsed(this.delay.getValue().intValue())) {
         mc.playerController.windowClick(0, i, this.slot.getValue() - 1, 2, mc.thePlayer);
         mc.thePlayer.inventory.currentItem = this.slot.getValue() - 1;
         mc.playerController.u();
      }
   }

   private int Z(BlockPos blockPos) {
      float f = 1.0F;
      int i = -1;
      int j = 9999;

      for (int k = 44; k >= 9; k--) {
         if (k >= 36 && k <= 44 || this.hotbarMove.getValue()) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(k);
            if (slot.getHasStack()) {
               ItemStack itemStack = slot.getStack();
               Block block = mc.theWorld.getBlockState(blockPos).getBlock();
               boolean bl = itemStack.getItem() instanceof ItemPickaxe && this.smartPickaxe.getValue();
               int l = 0;
               if (bl) {
                  ItemTool itemTool = (ItemTool)itemStack.getItem();
                  l = itemTool.getToolMaterial().getHarvestLevel();
               }

               float g = InventoryUtil.getToolEfficiency(itemStack, block.getDefaultState().getBlock());
               if (g > f && !bl || bl && (g > f || l < j) && itemStack.canHarvestBlock(block) && l < j) {
                  f = g;
                  i = k;
                  if (bl) {
                     j = l;
                  }
               }
            }
         }
      }

      return i;
   }

   private int G(boolean bl) {
      float f = 0.0F;
      int i = -1;

      for (int j = 44; j >= 9; j--) {
         if (j >= 36 && j <= 44 || this.hotbarMove.getValue()) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(j);
            if (slot.getHasStack() && (slot.getStack().getItem() instanceof ItemSword || !bl)) {
               ItemStack itemStack = slot.getStack();
               float g = InventoryUtil.getDamageScore(itemStack);
               if (g > f) {
                  f = g;
                  i = j;
               }
            }
         }
      }

      return i;
   }

   public static int p(Item item) {
      for (int i = 44; i >= 9; i--) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            Item item2 = mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem();
            if (item == item2) {
               return i;
            }
         }
      }

      return -1;
   }
}
