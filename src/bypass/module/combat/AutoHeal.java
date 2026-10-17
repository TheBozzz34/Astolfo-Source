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
   private Listener<UpdateEvent> onUpdate = updateEvent -> this.updateDisplayName();
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = renderLivingEvent -> {
      if (healItem != null && renderLivingEvent.getEntity() == mc.thePlayer && HealItem.typeOf(healItem) == HealItemType.POTION) {
      }
   };
   private boolean unusedFlag;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.LOWEST)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      if (Flight.slotSpoofed) {
         healItem = null;
      } else if (mc.thePlayer.ticksExisted < 15) {
         healItem = null;
      } else {
         switch (motionEvent.getState()) {
            case PRE:
               if (healItem != null && HealItem.typeOf(healItem) == HealItemType.HOLD) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
               }

               healItem = null;
               int i = this.countHotbarItems();
               if (i <= 0) {
                  if (mc.currentScreen instanceof GuiContainer) {
                     return;
                  }

                  int j = this.findInventoryItem();
                  if (j != -1 && this.clickTimer.hasElapsed(this.delay.getValue().intValue()) && this.hotBarMove.getValue()) {
                     mc.playerController.windowClick(0, j, this.slot.getValue() - 1, 2, mc.thePlayer);
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

                  for (int k = 0; k < 9; k++) {
                     ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(k);
                     if (itemStack != null) {
                        if (this.isAndurilEnabled() && this.isUsableAnduril(itemStack) && (!mc.thePlayer.isUsingItem() || GameUtil.isHoldingSword(mc.thePlayer))) {
                           healItem = new HealItem(HealItemType.HOLD, itemStack, k);
                        }

                        boolean bl = RiderGod.isAtLeast1122Protocol() && GameUtil.isOnHypixel() && client.getModuleManager().get(FastUse.class).isEnabled();
                        float f = (float)(85.0 + Math.random());
                        MovingObjectPosition movingObjectPosition = GameUtil.rayTrace(mc.thePlayer.rotationYaw, f, 3.0);
                        if (itemStack.getItem() instanceof ItemPotion && (this.belowMinHealth && this.isHealingPotion(itemStack) || this.isBuffPotion(itemStack))) {
                           if (ItemPotion.isSplash(itemStack.getMetadata())) {
                              if (mc.thePlayer.onGround
                                 && movingObjectPosition != null
                                 && movingObjectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.MISS
                                 && mc.thePlayer.isCollidedVertically) {
                                 this.lastPitch = motionEvent.getLastPitch();
                                 motionEvent.setPitch(f);
                                 this.serverPitch = motionEvent.getPitch();
                                 healItem = new HealItem(HealItemType.POTION, itemStack, k);
                              }
                           } else if (bl && this.healTimer.hasElapsed(this.delay.getValue().intValue())) {
                              this.useInstantly(k, itemStack);
                              this.healTimer.reset();
                              break;
                           }
                        }

                        boolean bl2 = this.minHealthAbsorption.asInt() > 5;
                        if (this.belowMinHealth) {
                           boolean bl3 = GameUtil.scoreboardTitleContains("PIT");
                           if (itemStack.getItem() == Items.mutton
                              && itemStack.getDisplayName().contains("AAA-Rated Steak")
                              && !mc.thePlayer.isPotionActive(Potion.resistance)
                              && bl3
                              && this.isHeadsEnabled()) {
                              healItem = new HealItem(HealItemType.USABLE, itemStack, k);
                              break;
                           }

                           if ((
                                    itemStack.getItem() instanceof ItemSkull && OldInventoryManager.isHead(itemStack)
                                       || itemStack.getDisplayName().contains("Rage Potato")
                                       || itemStack.getItem() == Items.magma_cream && itemStack.getDisplayName().contains("Fractured Soul") && bl3
                                 )
                                 && (!this.delayRegeneration.getValue() || !mc.thePlayer.isPotionActive(Potion.regeneration))
                                 && this.isHeadsEnabled()
                              || itemStack.getItem() instanceof ItemSoup && this.isSoupsEnabled()) {
                              healItem = new HealItem(HealItemType.USABLE, itemStack, k);
                              if (!bl
                                 || itemStack.getDisplayName() != null
                                    && (itemStack.getDisplayName().equals("§6Golden Head") || itemStack.getDisplayName().contains("Fractured Soul"))
                                    && bl2
                                    && this.belowMinHealthAbsorption) {
                                 break;
                              }
                           }
                        }
                     }
                  }
               }

               this.serverPitch = motionEvent.getPitch();
               break;
            case POST:
               if (healItem != null
                  && HealItem.stackOf(healItem) != null
                  && (this.healTimer.hasElapsed(this.delay.getValue().intValue()) || HealItem.typeOf(healItem) == HealItemType.FAKE)
                  && (HealItem.typeOf(healItem) != HealItemType.POTION || this.serverPitch <= 86.0F && this.serverPitch >= 85.0F)) {
                  if (HealItem.typeOf(healItem) == HealItemType.FAKE) {
                     mc.thePlayer.sendQueue.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
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
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (packetEvent.getPacket() instanceof C0EPacketClickWindow) {
            this.clickTimer.reset();
         }

         if (packetEvent.getPacket() instanceof C03PacketPlayer.C05PacketPlayerLook) {
            C03PacketPlayer.C05PacketPlayerLook c05PacketPlayerLook = (C03PacketPlayer.C05PacketPlayerLook)packetEvent.getPacket();
            this.serverPitch = c05PacketPlayerLook.getPitch();
         }

         if (packetEvent.getPacket() instanceof C03PacketPlayer.C06PacketPlayerPosLook) {
            C03PacketPlayer.C06PacketPlayerPosLook c06PacketPlayerPosLook = (C03PacketPlayer.C06PacketPlayerPosLook)packetEvent.getPacket();
            this.serverPitch = c06PacketPlayerPosLook.getPitch();
         }
      }

      if (packetEvent.getDirection() == PacketDirection.RECEIVED && mc.thePlayer != null) {
         if (packetEvent.getPacket() instanceof S06PacketUpdateHealth) {
            S06PacketUpdateHealth s06PacketUpdateHealth = (S06PacketUpdateHealth)packetEvent.getPacket();
            this.belowMinHealth = (s06PacketUpdateHealth.getHealth() + mc.thePlayer.getAbsorptionAmount()) / mc.thePlayer.getMaxHealth() * 100.0F <= this.minHealth.asInt();
            this.belowMinHealthAbsorption = s06PacketUpdateHealth.getHealth() / mc.thePlayer.getMaxHealth() * 100.0F <= this.minHealthAbsorption.asInt();
         }

         if (packetEvent.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook s08PacketPlayerPosLook = (S08PacketPlayerPosLook)packetEvent.getPacket();
            this.serverPitch = s08PacketPlayerPosLook.getPitch();
         }
      }
   };

   public AutoHeal(Category category) {
      super(category);
      this.addSettings(
         this.potions, this.other, this.minHealth, this.minHealthAbsorption, this.whenTarget, this.delayRegeneration, this.delay, this.slot, this.hotBarMove
      );
   }

   private void useInstantly(int i, ItemStack itemStack) {
      FastUse.sendUseItemPackets(i, itemStack, false);
      mc.thePlayer.sendQueue.sendPacketNoEvent(new C03PacketPlayer(true));
      mc.thePlayer.sendQueue.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
      healItem = null;
   }

   @Override
   public void onDisable() {
      healItem = null;
      this.serverPitch = this.lastPitch = 0.0F;
      super.onDisable();
   }

   @Override
   public void updateDisplayName() {
      this.setDisplayName("Auto Heal " + EnumChatFormatting.GRAY + this.countItems());
      super.updateDisplayName();
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

   private boolean isHealingPotion(ItemStack itemStack) {
      if (!this.belowMinHealth && !this.belowMinHealthAbsorption) {
         return false;
      }

      boolean bl = RiderGod.isAtLeast1122Protocol() && GameUtil.isOnHypixel() && client.getModuleManager().get(FastUse.class).isEnabled();
      return !ItemPotion.isSplash(itemStack.getMetadata()) && !bl
         ? false
         : InventoryUtil.hasPotionEffect(itemStack, Potion.regeneration.id)
               && !mc.thePlayer.isPotionActive(Potion.regeneration)
               && this.isRegenerationEnabled()
            || InventoryUtil.hasPotionEffect(itemStack, Potion.heal.id) && this.isHealEnabled()
            || InventoryUtil.hasPotionEffect(itemStack, Potion.absorption.id) && this.isAbsorptionEnabled();
   }

   private boolean isBuffPotion(ItemStack itemStack) {
      boolean bl = RiderGod.isAtLeast1122Protocol() && GameUtil.isOnHypixel() && client.getModuleManager().get(FastUse.class).isEnabled();
      return !ItemPotion.isSplash(itemStack.getMetadata()) && !bl
         ? false
         : InventoryUtil.hasPotionEffect(itemStack, Potion.fireResistance.id)
               && this.isFireResistanceEnabled()
               && !mc.thePlayer.isPotionActive(Potion.fireResistance)
            || InventoryUtil.hasPotionEffect(itemStack, Potion.resistance.id)
               && this.isResistanceEnabled()
               && this.findNearestPlayer() != null
               && !mc.thePlayer.isPotionActive(Potion.resistance)
            || InventoryUtil.hasPotionEffect(itemStack, Potion.jump.id) && !mc.thePlayer.isPotionActive(Potion.jump) && this.isJumpBoostEnabled()
            || InventoryUtil.hasPotionEffect(itemStack, Potion.moveSpeed.id)
               && !this.hasJumpEffect(itemStack)
               && !mc.thePlayer.isPotionActive(Potion.moveSpeed)
               && this.isSpeedEnabled();
   }

   private boolean hasJumpEffect(ItemStack itemStack) {
      if (itemStack.getItem() instanceof ItemPotion) {
         ItemPotion itemPotion = (ItemPotion)itemStack.getItem();

         for (PotionEffect potionEffect : itemPotion.getEffects(itemStack)) {
            if (potionEffect.getPotionID() == Potion.jump.id) {
               return true;
            }
         }
      }

      return false;
   }

   private EntityPlayer findNearestPlayer() {
      float f = 10.0F;
      EntityPlayer entityPlayer = null;

      for (EntityPlayer entityPlayer2 : mc.theWorld.playerEntities) {
         if (entityPlayer2 != mc.thePlayer && !BotDetector.bots.contains(entityPlayer2)) {
            float g = mc.thePlayer.getDistanceToEntity(entityPlayer2);
            if (g < f) {
               entityPlayer = entityPlayer2;
               f = g;
            }
         }
      }

      return entityPlayer;
   }

   private int countHotbarItems() {
      int i = 0;

      for (int j = 0; j < 9; j++) {
         ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(j);
         if (itemStack != null && this.isHealItem(itemStack)) {
            i += itemStack.stackSize;
         }
      }

      return i;
   }

   private int countItems() {
      int i = 0;

      for (int j = 9; j <= 44; j++) {
         if (mc.thePlayer.inventoryContainer.getSlot(j).getHasStack()) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(j).getStack();
            if (this.isHealItem(itemStack)) {
               i += itemStack.stackSize;
            }
         }
      }

      return i;
   }

   private int findInventoryItem() {
      for (int i = 9; i <= 35; i++) {
         if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (this.isHealItem(itemStack)) {
               return i;
            }
         }
      }

      return -1;
   }

   private boolean isHealItem(ItemStack itemStack) {
      if (itemStack == null) {
         return false;
      }

      boolean bl = GameUtil.scoreboardTitleContains("PIT");
      return itemStack.getItem() instanceof ItemSoup && this.isSoupsEnabled()
         || (
               itemStack.getItem() instanceof ItemSkull && OldInventoryManager.isHead(itemStack)
                  || itemStack.getDisplayName().contains("Rage Potato")
                  || itemStack.getItem() == Items.mutton
                     && itemStack.getDisplayName().contains("AAA-Rated Steak")
                     && !mc.thePlayer.isPotionActive(Potion.resistance)
                     && bl
                  || itemStack.getItem() == Items.magma_cream && itemStack.getDisplayName().contains("Fractured Soul") && bl
            )
            && (this.belowMinHealthAbsorption || this.belowMinHealth)
            && this.isHeadsEnabled()
         || this.isHealingPotion(itemStack)
         || this.isAndurilEnabled() && this.isUsableAnduril(itemStack)
         || this.isBuffPotion(itemStack);
   }

   private boolean isUsableAnduril(ItemStack itemStack) {
      return itemStack.getItem() == Items.iron_sword
         && itemStack.getDisplayName().contains("Andúril")
         && (
            !mc.thePlayer.isPotionActive(Potion.resistance)
               || mc.thePlayer.getActivePotionEffect(Potion.resistance).getDuration() <= 25
                  && mc.thePlayer.getActivePotionEffect(Potion.resistance).getDuration() >= 23
         )
         && itemStack.getItem().hasEffect(itemStack);
   }
}
