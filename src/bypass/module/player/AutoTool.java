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
   private final Listener<PacketEvent> onPacket = var1x -> {
      long var2 = c ^ 117701742516905L;
      long var4 = var2 ^ 48702075241829L;
      if (var1x.getDirection() == PacketDirection.OUTGOING) {
         if (client.getModuleManager().get(KillAura.class).isEnabled() && !this.withKillAura.getValue() && !AntiObsidian.C()) {
            return;
         }

         if (var1x.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity var7 = (C02PacketUseEntity)var1x.getPacket();
            Entity var8 = var7.getEntityFromWorld(mc.theWorld);
            switch (var7.getAction()) {
               case ATTACK:
                  if (client.getModuleManager().get(AntiObsidian.class).isEnabled() && AntiObsidian.C() || mc.playerController.isHittingBlock) {
                     return;
                  }

                  if (var8 instanceof EntityLivingBase) {
                     int var14 = this.G(this.swordOnly.getValue());
                     if (var14 != -1) {
                        this.H(var14);
                     }
                  }
                  break;
               case INTERACT:
                  if (var8 instanceof EntityLivingBase) {
                     if (var8 instanceof EntityCow) {
                        int var9 = p(Items.bucket);
                        if (var9 != -1 && ((ItemBucket)mc.thePlayer.inventoryContainer.getSlot(var9).getStack().getItem()).N() == Blocks.air) {
                           this.H(var9);
                        }
                     }

                     if (var8 instanceof EntitySheep) {
                        EntitySheep var13 = (EntitySheep)var8;
                        if (!var13.getSheared()) {
                           int var10 = p(Items.shears);
                           if (var10 != -1) {
                              this.H(var10);
                           }
                        }
                     }
                  }
            }
         }

         if (var1x.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging var11 = (C07PacketPlayerDigging)var1x.getPacket();
            if (var11.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
               if (mc.playerController.isInCreativeMode()) {
                  return;
               }

               int var12 = this.Z(var11.getPosition());
               if (var12 != -1) {
                  this.H(var12);
               }
            }
         }

         if (var1x.getPacket() instanceof C0EPacketClickWindow) {
            this.x.reset();
         }
      }
   };
   private static final long c = 123351539458687L;

   public AutoTool(Category var1) {
      super(var1);
      this.addSettings(this.slot, this.delay, this.swordOnly, this.withKillAura, this.hotbarMove, this.smartPickaxe);
   }

   private void H(int var1) {
      if (var1 >= 36 && var1 <= 44) {
         mc.thePlayer.inventory.currentItem = var1 - 36;
         mc.playerController.u();
      } else if (this.hotbarMove.getValue() && this.x.hasElapsed(this.delay.getValue().intValue())) {
         mc.playerController.windowClick(0, var1, this.slot.getValue() - 1, 2, mc.thePlayer);
         mc.thePlayer.inventory.currentItem = this.slot.getValue() - 1;
         mc.playerController.u();
      }
   }

   private int Z(BlockPos var1) {
      float var5 = 1.0F;
      int var6 = -1;
      int var7 = 9999;

      for (int var8 = 44; var8 >= 9; var8--) {
         if (var8 >= 36 && var8 <= 44 || this.hotbarMove.getValue()) {
            Slot var9 = mc.thePlayer.inventoryContainer.getSlot(var8);
            if (var9.getHasStack()) {
               ItemStack var10 = var9.getStack();
               Block var11 = mc.theWorld.getBlockState(var1).getBlock();
               boolean var12 = var10.getItem() instanceof ItemPickaxe && this.smartPickaxe.getValue();
               int var13 = 0;
               if (var12) {
                  ItemTool var14 = (ItemTool)var10.getItem();
                  var13 = var14.getToolMaterial().getHarvestLevel();
               }

               float var15 = InventoryUtil.getToolEfficiency(var10, var11.getDefaultState().getBlock());
               if (var15 > var5 && !var12 || var12 && (var15 > var5 || var13 < var7) && var10.canHarvestBlock(var11) && var13 < var7) {
                  var5 = var15;
                  var6 = var8;
                  if (var12) {
                     var7 = var13;
                  }
               }
            }
         }
      }

      return var6;
   }

   private int G(boolean var1) {
      float var5 = 0.0F;
      int var6 = -1;

      for (int var7 = 44; var7 >= 9; var7--) {
         if (var7 >= 36 && var7 <= 44 || this.hotbarMove.getValue()) {
            Slot var8 = mc.thePlayer.inventoryContainer.getSlot(var7);
            if (var8.getHasStack() && (var8.getStack().getItem() instanceof ItemSword || !var1)) {
               ItemStack var9 = var8.getStack();
               float var10 = InventoryUtil.getDamageScore(var9);
               if (var10 > var5) {
                  var5 = var10;
                  var6 = var7;
               }
            }
         }
      }

      return var6;
   }

   public static int p(Item var0) {
      for (int var4 = 44; var4 >= 9; var4--) {
         if (mc.thePlayer.inventoryContainer.getSlot(var4).getHasStack()) {
            Item var5 = mc.thePlayer.inventoryContainer.getSlot(var4).getStack().getItem();
            if (var0 == var5) {
               return var4;
            }
         }
      }

      return -1;
   }
}
