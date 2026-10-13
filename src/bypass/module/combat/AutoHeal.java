package bypass.module.combat;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.RenderLivingEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.exploits.RiderGod;
import bypass.module.hidden.BotDetector;
import bypass.module.movement.Flight;
import bypass.module.player.FastUse;
import bypass.module.player.OldInventoryManager;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.InventoryUtil;
import bypass.util.Stopwatch;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;

public final class AutoHeal extends Module {
   private final MapSetting potions = new MapSettingBuilder("Potions")
      .ordered()
      .description("potions")
      .add("Regeneration", new BooleanSetting("Regeneration", "Throws regeneration", true))
      .add("Heal", new BooleanSetting("Heal", "Throws heal pot", true))
      .add("Jump-Boost", new BooleanSetting("Jump-Boost", "Throws jump boost", false))
      .add("Speed", new BooleanSetting("Speed", "Throws speed pot", false))
      .add("Resistance", new BooleanSetting("Resistance", "Throws resistance pot", false))
      .add("Absorption", new BooleanSetting("Absorption", "Throws Absorption pot", false))
      .add("Fire-Resistance", new BooleanSetting("Fire-Resistance", "Throws Fire-Resistance pot", false))
      .build();
   private final MapSetting other = new MapSettingBuilder("Other")
      .ordered()
      .description("other")
      .add("Heads", new BooleanSetting("Heads", "Uses heads", false))
      .add("Soups", new BooleanSetting("Soups", "Uses soups", false))
      .add("Andúril", new BooleanSetting("Andúril", "Uses Andúril", false))
      .build();
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Delay in milliseconds between healing", 250, 0, 1000, "ms");
   private final NumberSetting<Integer> minHealth = new NumberSetting<>("Min-Health", "Health needed to use items", 50, 0, 100, "%");
   private final NumberSetting<Integer> minHealthAbsorption = new NumberSetting<>("Min-Health-Absorption", "Health needed to use items", 50, 0, 100, "%");
   private final NumberSetting<Integer> slot = new NumberSetting<>("Slot", "Slots where the pots will be on hotbar", 9, 1, 9);
   private final BooleanSetting whenTarget = new BooleanSetting("When-Target", "Pots when target", false);
   private final BooleanSetting delayRegeneration = new BooleanSetting("Delay-Regeneration", "Waits until regeneration expires to eat another head", false);
   private final BooleanSetting hotBarMove = new BooleanSetting("HotBar-Move", "Moves items to hotbar automatically", true);
   private final Stopwatch healTimer = new Stopwatch();
   private final Stopwatch clickTimer = new Stopwatch();
   private final Stopwatch unusedTimer = new Stopwatch();
   private boolean belowMinHealth;
   private boolean belowMinHealthAbsorption;
   public static HealItem healItem;
   private float lastPitch;
   private float serverPitch;
   public static ItemStack unusedStack;
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> this.p();
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = var0 -> {
      if (healItem != null && var0.getEntity() == mc.thePlayer && HealItem.typeOf(healItem) == HealItemType.POTION) {
      }
   };
   private boolean unusedFlag;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.LOWEST)
   private Listener<MotionEvent> onMotion = var1x -> {
      long var2 = c ^ 134748975167122L;
      long var4 = var2 ^ 9385193959109L;
      if (Flight.slotSpoofed) {
         healItem = null;
      } else if (mc.thePlayer.ticksExisted < 15) {
         healItem = null;
      } else {
         switch (var1x.getState()) {
            case PRE:
               if (healItem != null && HealItem.typeOf(healItem) == HealItemType.HOLD) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
               }

               healItem = null;
               int var7 = this.countHotbarItems();
               if (var7 <= 0) {
                  if (mc.currentScreen instanceof GuiContainer) {
                     return;
                  }

                  int var15 = this.findInventoryItem();
                  if (var15 != -1 && this.clickTimer.hasElapsed(this.delay.getValue().intValue()) && this.hotBarMove.getValue()) {
                     mc.playerController.windowClick(0, var15, this.slot.getValue() - 1, 2, mc.thePlayer);
                  }
               } else {
                  if (mc.currentScreen instanceof GuiContainer && this.hotBarMove.getValue()) {
                     return;
                  }

                  if (KillAura.target != null && !this.whenTarget.getValue()) {
                     return;
                  }

                  if (mc.thePlayer.getAbsorptionAmount() >= 4.0F) {
                     this.belowMinHealthAbsorption = false;
                  }

                  for (int var8 = 0; var8 < 9; var8++) {
                     ItemStack var9 = mc.thePlayer.inventory.getStackInSlot(var8);
                     if (var9 != null) {
                        if (this.isAndurilEnabled() && this.isUsableAnduril(var9) && (!mc.thePlayer.isUsingItem() || GameUtil.isHoldingSword(mc.thePlayer))) {
                           healItem = new HealItem(HealItemType.HOLD, var9, var8);
                        }

                        boolean var10 = RiderGod.r() && GameUtil.isOnHypixel() && client.getModuleManager().get(FastUse.class).isEnabled();
                        float var11 = (float)(85.0 + Math.random());
                        MovingObjectPosition var12 = GameUtil.A(mc.thePlayer.rotationYaw, var11, 3.0);
                        if (var9.getItem() instanceof ItemPotion && (this.belowMinHealth && this.isHealingPotion(var9) || this.isBuffPotion(var9))) {
                           if (ItemPotion.isSplash(var9.getMetadata())) {
                              if (mc.thePlayer.onGround
                                 && var12 != null
                                 && var12.typeOfHit != MovingObjectPosition.MovingObjectType.MISS
                                 && mc.thePlayer.isCollidedVertically) {
                                 this.lastPitch = var1x.getLastPitch();
                                 var1x.setPitch(var11);
                                 this.serverPitch = var1x.getPitch();
                                 healItem = new HealItem(HealItemType.POTION, var9, var8);
                              }
                           } else if (var10 && this.healTimer.hasElapsed(this.delay.getValue().intValue())) {
                              this.useInstantly(var8, var9);
                              this.healTimer.reset();
                              break;
                           }
                        }

                        boolean var13 = this.minHealthAbsorption.asInt() > 5;
                        if (this.belowMinHealth) {
                           boolean var14 = GameUtil.scoreboardTitleContains("PIT");
                           if (var9.getItem() == Items.mutton
                              && var9.getDisplayName().contains("AAA-Rated Steak")
                              && !mc.thePlayer.isPotionActive(Potion.resistance)
                              && var14
                              && this.isHeadsEnabled()) {
                              healItem = new HealItem(HealItemType.USABLE, var9, var8);
                              break;
                           }

                           if ((
                                    var9.getItem() instanceof ItemSkull && OldInventoryManager.isHead(var9)
                                       || var9.getDisplayName().contains("Rage Potato")
                                       || var9.getItem() == Items.magma_cream && var9.getDisplayName().contains("Fractured Soul") && var14
                                 )
                                 && (!this.delayRegeneration.getValue() || !mc.thePlayer.isPotionActive(Potion.regeneration))
                                 && this.isHeadsEnabled()
                              || var9.getItem() instanceof ItemSoup && this.isSoupsEnabled()) {
                              healItem = new HealItem(HealItemType.USABLE, var9, var8);
                              if (!var10
                                 || var9.getDisplayName() != null
                                    && (var9.getDisplayName().equals("§6Golden Head") || var9.getDisplayName().contains("Fractured Soul"))
                                    && var13
                                    && this.belowMinHealthAbsorption) {
                                 break;
                              }
                           }
                        }
                     }
                  }
               }

               this.serverPitch = var1x.getPitch();
               break;
            case POST:
               if (healItem != null
                  && HealItem.stackOf(healItem) != null
                  && (this.healTimer.hasElapsed(this.delay.getValue().intValue()) || HealItem.typeOf(healItem) == HealItemType.FAKE)
                  && (HealItem.typeOf(healItem) != HealItemType.POTION || this.serverPitch <= 86.0F && this.serverPitch >= 85.0F)) {
                  if (HealItem.typeOf(healItem) == HealItemType.FAKE) {
                     mc.thePlayer.sendQueue.u(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                     this.unusedFlag = false;
                  } else {
                     mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(HealItem.slotOf(healItem)));
                     if (HealItem.typeOf(healItem) != HealItemType.HOLD) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(HealItem.stackOf(healItem)));
                     }

                     if (HealItem.stackOf(healItem).getItem() instanceof ItemSoup) {
                        mc.thePlayer.dropOneItem(false);
                     }

                     if (HealItem.typeOf(healItem) != HealItemType.HOLD) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                     }

                     this.healTimer.reset();
                  }
               }

               if (healItem != null && HealItem.typeOf(healItem) != HealItemType.HOLD) {
                  healItem = null;
               }
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING) {
         if (var1x.getPacket() instanceof C0EPacketClickWindow) {
            this.clickTimer.reset();
         }

         if (var1x.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
            C03PacketPlayer.C05PacketPlayerLook var5 = (C03PacketPlayer.C05PacketPlayerLook)var1x.getPacket();
            this.serverPitch = var5.getPitch();
         }

         if (var1x.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            C03PacketPlayer.C06PacketPlayerPosLook var6 = (C03PacketPlayer.C06PacketPlayerPosLook)var1x.getPacket();
            this.serverPitch = var6.getPitch();
         }
      }

      if (var1x.getDirection() == PacketDirection.RECEIVED && mc.thePlayer != null) {
         if (var1x.getPacket() instanceof S06PacketUpdateHealth) {
            S06PacketUpdateHealth var7 = (S06PacketUpdateHealth)var1x.getPacket();
            this.belowMinHealth = (var7.getHealth() + mc.thePlayer.getAbsorptionAmount()) / mc.thePlayer.getMaxHealth() * 100.0F <= this.minHealth.asInt();
            this.belowMinHealthAbsorption = var7.getHealth() / mc.thePlayer.getMaxHealth() * 100.0F <= this.minHealthAbsorption.asInt();
         }

         if (var1x.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook var8 = (S08PacketPlayerPosLook)var1x.getPacket();
            this.serverPitch = var8.getPitch();
         }
      }
   };
   private static final long c = 76361736906212L;

   public AutoHeal(Category var1) {
      super(var1);
      this.addSettings(
         this.potions, this.other, this.minHealth, this.minHealthAbsorption, this.whenTarget, this.delayRegeneration, this.delay, this.slot, this.hotBarMove
      );
   }

   private void useInstantly(int var1, ItemStack var2) {
      FastUse.E(var1, var2, false);
      mc.thePlayer.sendQueue.u(new C03PacketPlayer(true));
      mc.thePlayer.sendQueue.u(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
      healItem = null;
   }

   @Override
   public void onDisable() {
      healItem = null;
      this.serverPitch = this.lastPitch = 0.0F;
      super.onDisable();
   }

   @Override
   public void p() {
      this.setDisplayName("Auto Heal " + EnumChatFormatting.GRAY + this.countItems());
      super.p();
   }

   private boolean isJumpBoostEnabled() {
      return this.potions.getSetting("Jump-Boost").asBoolean();
   }

   private boolean isRegenerationEnabled() {
      return this.potions.getSetting("Regeneration").asBoolean();
   }

   private boolean isResistanceEnabled() {
      return this.potions.getSetting("Resistance").asBoolean();
   }

   private boolean isFireResistanceEnabled() {
      return this.potions.getSetting("Fire-Resistance").asBoolean();
   }

   private boolean isSpeedEnabled() {
      return this.potions.getSetting("Speed").asBoolean();
   }

   private boolean isHealEnabled() {
      return this.potions.getSetting("Heal").asBoolean();
   }

   private boolean isAbsorptionEnabled() {
      return this.potions.getSetting("Absorption").asBoolean();
   }

   private boolean isHeadsEnabled() {
      return this.other.getSetting("Heads").asBoolean();
   }

   private boolean isSoupsEnabled() {
      return this.other.getSetting("Soups").asBoolean();
   }

   private boolean isAndurilEnabled() {
      return this.other.getSetting("Andúril").asBoolean();
   }

   private boolean isHealingPotion(ItemStack var1) {
      long var2 = c ^ 75140284277602L;
      long var4 = var2 ^ 59727628827445L;
      if (!this.belowMinHealth && !this.belowMinHealthAbsorption) {
         return false;
      }

      boolean var7 = RiderGod.r() && GameUtil.isOnHypixel() && client.getModuleManager().get(FastUse.class).isEnabled();
      return !ItemPotion.isSplash(var1.getMetadata()) && !var7
         ? false
         : InventoryUtil.hasPotionEffect(var1, Potion.regeneration.id) && !mc.thePlayer.isPotionActive(Potion.regeneration) && this.isRegenerationEnabled()
            || InventoryUtil.hasPotionEffect(var1, Potion.heal.id) && this.isHealEnabled()
            || InventoryUtil.hasPotionEffect(var1, Potion.absorption.id) && this.isAbsorptionEnabled();
   }

   private boolean isBuffPotion(ItemStack var1) {
      long var2 = c ^ 65324971785467L;
      long var4 = var2 ^ 80733546112172L;
      boolean var7 = RiderGod.r() && GameUtil.isOnHypixel() && client.getModuleManager().get(FastUse.class).isEnabled();
      return !ItemPotion.isSplash(var1.getMetadata()) && !var7
         ? false
         : InventoryUtil.hasPotionEffect(var1, Potion.fireResistance.id)
               && this.isFireResistanceEnabled()
               && !mc.thePlayer.isPotionActive(Potion.fireResistance)
            || InventoryUtil.hasPotionEffect(var1, Potion.resistance.id)
               && this.isResistanceEnabled()
               && this.findNearestPlayer() != null
               && !mc.thePlayer.isPotionActive(Potion.resistance)
            || InventoryUtil.hasPotionEffect(var1, Potion.jump.id) && !mc.thePlayer.isPotionActive(Potion.jump) && this.isJumpBoostEnabled()
            || InventoryUtil.hasPotionEffect(var1, Potion.moveSpeed.id)
               && !this.hasJumpEffect(var1)
               && !mc.thePlayer.isPotionActive(Potion.moveSpeed)
               && this.isSpeedEnabled();
   }

   private boolean hasJumpEffect(ItemStack var1) {
      if (var1.getItem() instanceof ItemPotion) {
         ItemPotion var5 = (ItemPotion)var1.getItem();

         for (PotionEffect var7 : var5.getEffects(var1)) {
            if (var7.getPotionID() == Potion.jump.id) {
               return true;
            }
         }
      }

      return false;
   }

   private EntityPlayer findNearestPlayer() {
      float var4 = 10.0F;
      EntityPlayer var5 = null;

      for (EntityPlayer var7 : mc.theWorld.playerEntities) {
         if (var7 != mc.thePlayer && !BotDetector.w.contains(var7)) {
            float var8 = mc.thePlayer.getDistanceToEntity(var7);
            if (var8 < var4) {
               var5 = var7;
               var4 = var8;
            }
         }
      }

      return var5;
   }

   private int countHotbarItems() {
      int var4 = 0;

      for (int var5 = 0; var5 < 9; var5++) {
         ItemStack var6 = mc.thePlayer.inventory.getStackInSlot(var5);
         if (var6 != null && this.isHealItem(var6)) {
            var4 += var6.stackSize;
         }
      }

      return var4;
   }

   private int countItems() {
      int var4 = 0;

      for (int var5 = 9; var5 <= 44; var5++) {
         if (mc.thePlayer.inventoryContainer.getSlot(var5).getHasStack()) {
            ItemStack var6 = mc.thePlayer.inventoryContainer.getSlot(var5).getStack();
            if (this.isHealItem(var6)) {
               var4 += var6.stackSize;
            }
         }
      }

      return var4;
   }

   private int findInventoryItem() {
      for (int var4 = 9; var4 <= 35; var4++) {
         if (mc.thePlayer.inventoryContainer.getSlot(var4).getHasStack()) {
            ItemStack var5 = mc.thePlayer.inventoryContainer.getSlot(var4).getStack();
            if (this.isHealItem(var5)) {
               return var4;
            }
         }
      }

      return -1;
   }

   private boolean isHealItem(ItemStack var1) {
      if (var1 == null) {
         return false;
      }

      boolean var5 = GameUtil.scoreboardTitleContains("PIT");
      return var1.getItem() instanceof ItemSoup && this.isSoupsEnabled()
         || (
               var1.getItem() instanceof ItemSkull && OldInventoryManager.isHead(var1)
                  || var1.getDisplayName().contains("Rage Potato")
                  || var1.getItem() == Items.mutton
                     && var1.getDisplayName().contains("AAA-Rated Steak")
                     && !mc.thePlayer.isPotionActive(Potion.resistance)
                     && var5
                  || var1.getItem() == Items.magma_cream && var1.getDisplayName().contains("Fractured Soul") && var5
            )
            && (this.belowMinHealthAbsorption || this.belowMinHealth)
            && this.isHeadsEnabled()
         || this.isHealingPotion(var1)
         || this.isAndurilEnabled() && this.isUsableAnduril(var1)
         || this.isBuffPotion(var1);
   }

   private boolean isUsableAnduril(ItemStack var1) {
      return var1.getItem() == Items.iron_sword
         && var1.getDisplayName().contains("Andúril")
         && (
            !mc.thePlayer.isPotionActive(Potion.resistance)
               || mc.thePlayer.getActivePotionEffect(Potion.resistance).getDuration() <= 25
                  && mc.thePlayer.getActivePotionEffect(Potion.resistance).getDuration() >= 23
         )
         && var1.getItem().hasEffect(var1);
   }
}
