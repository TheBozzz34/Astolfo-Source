package net.minecraft.client.entity;

import bypass.Astolfo;
import bypass.event.EventState;
import bypass.event.player.ChatDirection;
import bypass.event.player.ChatEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.event.player.MoveWithHeadingEvent;
import bypass.event.player.PushOutOfBlocksEvent;
import bypass.event.player.RotationYawEvent;
import bypass.event.player.UpdateEvent;
import bypass.module.ModuleManager;
import bypass.module.exploits.HideWindow;
import bypass.module.exploits.RiderGod;
import bypass.module.movement.Speed;
import bypass.module.movement.SpeedMode;
import bypass.module.movement.Sprint;
import bypass.network.VehicleMovePacket;
import bypass.util.GameUtil;
import bypass.util.Stopwatch;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class EntityPlayerSP extends AbstractClientPlayer {
   public final NetHandlerPlayClient sendQueue;
   private final StatFileWriter statWriter;
   public double lastReportedPosX;
   public double lastReportedPosY;
   public double lastReportedPosZ;
   private float lastReportedYaw;
   private float lastReportedPitch;
   private boolean eF;
   public boolean ej;
   private int eR;
   private boolean hasValidHealth;
   private String clientBrand;
   public MovementInput movementInput;
   public Minecraft mc;
   public int eD;
   public int sprintingTicksLeft;
   public float renderArmYaw;
   public float renderArmPitch;
   public float prevRenderArmYaw;
   public float prevRenderArmPitch;
   private int eU;
   private float horseJumpPower;
   public float timeInPortal;
   public float prevTimeInPortal;
   private int e8;
   private Stopwatch eM = new Stopwatch();
   private boolean el;

   public EntityPlayerSP(Minecraft mcIn, World worldIn, NetHandlerPlayClient netHandler, StatFileWriter statFile) {
      super(worldIn, netHandler.getGameProfile());
      this.sendQueue = netHandler;
      this.statWriter = statFile;
      this.mc = mcIn;
      this.dimension = 0;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }

   @Override
   public void heal(float healAmount) {
   }

   @Override
   public void mountEntity(Entity entityIn) {
      super.mountEntity(entityIn);
      if (entityIn instanceof EntityMinecart) {
         this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)entityIn));
      }
   }

   @Override
   public void onUpdate() {
      if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0, this.posZ))) {
         Astolfo.INSTANCE.getEventBus().post(new UpdateEvent(EventState.PRE));
         super.onUpdate();
         Astolfo.INSTANCE.getEventBus().post(new UpdateEvent(EventState.POST));
         boolean bl = Astolfo.INSTANCE.getModuleManager().get(RiderGod.class).isEnabled();
         if (this.E()) {
            this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
            this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            if (RiderGod.isExactly1122Protocol()) {
               this.sendQueue.addToSendQueue(new VehicleMovePacket(this.ridingEntity));
            }
         } else {
            this.i();
         }
      }
   }

   public void i() {
      if (!(this.mc.disablerTimer.timerSpeed > 1.0F) || this.mc.timer.timerSpeed != 1.0F) {
         MotionEvent motionEvent = new MotionEvent(
            this.sendQueue,
            this.getEntityBoundingBox(),
            this.onGround,
            this.posX,
            this.posY,
            this.posZ,
            this.rotationYaw,
            this.rotationPitch,
            this.lastReportedPosX,
            this.lastReportedPosY,
            this.lastReportedPosZ,
            this.lastReportedYaw,
            this.lastReportedPitch,
            this.eR,
            EventState.PRE
         );
         Astolfo.INSTANCE.getEventBus().post(motionEvent);
         if (motionEvent.isCancelled()) {
            Astolfo.INSTANCE.getEventBus().post(new MotionEvent(EventState.POST, this.sendQueue));
         } else {
            boolean bl = this.isSprinting();
            if (bl != this.ej) {
               if (bl) {
                  this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
               } else {
                  this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
               }

               this.ej = bl;
            }

            boolean bl2 = this.isSneaking();
            if (bl2 != this.eF) {
               if (bl2) {
                  this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
               } else {
                  this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
               }

               this.eF = bl2;
            }

            if (this.Y_()) {
               double d = motionEvent.getX();
               double e = motionEvent.getMinY();
               double d2 = motionEvent.getZ();
               float f = motionEvent.getYaw();
               float g = motionEvent.getPitch();
               double d3 = d - this.lastReportedPosX;
               double d4 = e - this.lastReportedPosY;
               double d5 = d2 - this.lastReportedPosZ;
               double d6 = f - this.lastReportedYaw;
               double d7 = g - this.lastReportedPitch;
               boolean bl3 = d3 * d3 + d4 * d4 + d5 * d5 > 9.0E-4 || this.eR >= 20;
               boolean bl4 = d6 != 0.0 || d7 != 0.0;
               boolean bl5 = motionEvent.isOnGround();
               if (this.ridingEntity == null) {
                  if (bl3 && bl4) {
                     this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(d, e, d2, f, g, bl5));
                  } else if (bl3) {
                     this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(d, e, d2, bl5));
                  } else if (bl4) {
                     this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(f, g, bl5));
                  } else if (RiderGod.isAtLeast1122Protocol()) {
                     if (this.el != bl5) {
                        this.sendQueue.addToSendQueue(new C03PacketPlayer(bl5));
                     }
                  } else {
                     this.sendQueue.addToSendQueue(new C03PacketPlayer(bl5));
                  }
               } else {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, f, g, bl5));
                  bl3 = false;
               }

               this.eR++;
               if (bl3) {
                  this.lastReportedPosX = d;
                  this.lastReportedPosY = e;
                  this.lastReportedPosZ = d2;
                  this.eR = 0;
               }

               if (bl4) {
                  this.lastReportedYaw = f;
                  this.lastReportedPitch = g;
               }

               this.el = bl5;
            }

            Astolfo.INSTANCE.getEventBus().post(new MotionEvent(EventState.POST, this.sendQueue));
         }
      }
   }

   public void k() {
      MotionEvent motionEvent = new MotionEvent(
         this.sendQueue,
         this.getEntityBoundingBox(),
         this.onGround,
         this.posX,
         this.posY,
         this.posZ,
         this.rotationYaw,
         this.rotationPitch,
         this.lastReportedPosX,
         this.lastReportedPosY,
         this.lastReportedPosZ,
         this.lastReportedYaw,
         this.lastReportedPitch,
         this.eR,
         EventState.PRE
      );
      Astolfo.INSTANCE.getEventBus().post(motionEvent);
      if (motionEvent.isCancelled()) {
         Astolfo.INSTANCE.getEventBus().post(new MotionEvent(EventState.POST, this.sendQueue));
      } else {
         boolean bl = this.isSprinting();
         if (bl != this.ej) {
            if (bl) {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
            } else {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }

            this.ej = bl;
         }

         boolean bl2 = this.isSneaking();
         if (bl2 != this.eF) {
            if (bl2) {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }

            this.eF = bl2;
         }

         if (this.Y_()) {
            double d = motionEvent.getX();
            double e = motionEvent.getMinY();
            double d2 = motionEvent.getZ();
            float f = motionEvent.getYaw();
            float g = motionEvent.getPitch();
            double d3 = d - this.lastReportedPosX;
            double d4 = e - this.lastReportedPosY;
            double d5 = d2 - this.lastReportedPosZ;
            double d6 = f - this.lastReportedYaw;
            double d7 = g - this.lastReportedPitch;
            boolean bl3 = d3 * d3 + d4 * d4 + d5 * d5 > 9.0E-4 || this.eR >= 20;
            boolean bl4 = d6 != 0.0 || d7 != 0.0;
            boolean bl5 = motionEvent.isOnGround();
            if (this.ridingEntity == null) {
               if (bl3 && bl4) {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(d, e, d2, f, g, bl5));
               } else if (bl3) {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(d, e, d2, bl5));
               } else if (bl4) {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(f, g, bl5));
               } else if (RiderGod.isAtLeast1122Protocol()) {
                  if (this.el != bl5) {
                     this.sendQueue.addToSendQueue(new C03PacketPlayer(bl5));
                  }
               } else {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer(bl5));
               }
            } else {
               this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, f, g, bl5));
               bl3 = false;
            }

            this.eR++;
            if (bl3) {
               this.lastReportedPosX = d;
               this.lastReportedPosY = e;
               this.lastReportedPosZ = d2;
               this.eR = 0;
            }

            if (bl4) {
               this.lastReportedYaw = f;
               this.lastReportedPitch = g;
            }

            this.el = bl5;
         }

         Astolfo.INSTANCE.getEventBus().post(new MotionEvent(EventState.POST, this.sendQueue));
      }
   }

   @Override
   public EntityItem dropOneItem(boolean dropAll) {
      C07PacketPlayerDigging.Action action = dropAll ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
      this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(action, BlockPos.ORIGIN, EnumFacing.DOWN));
      return null;
   }

   @Override
   public void joinEntityItemWithWorld(EntityItem itemIn) {
   }

   public void sendChatMessage(String message) {
      ChatEvent chatEvent = new ChatEvent(ChatDirection.TO_SEND, message);
      Astolfo.INSTANCE.getEventBus().post(chatEvent);
      if (!chatEvent.isCancelled()) {
         this.sendQueue.addToSendQueue(new C01PacketChatMessage(chatEvent.getMessage()));
      }
   }

   public void sendClientChatMessage(String string) {
      ChatEvent chatEvent = new ChatEvent(ChatDirection.TO_SEND, string);
      chatEvent.setUserTyped(false);
      Astolfo.INSTANCE.getEventBus().post(chatEvent);
      if (!chatEvent.isCancelled()) {
         this.sendQueue.addToSendQueue(new C01PacketChatMessage(chatEvent.getMessage()));
      }
   }

   @Override
   public void swingItem() {
      super.swingItem();
      this.sendQueue.addToSendQueue(new C0APacketAnimation());
   }

   @Override
   public void respawnPlayer() {
      this.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
   }

   @Override
   public void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isEntityInvulnerable(damageSrc)) {
         this.setHealth(this.getHealth() - damageAmount);
      }
   }

   @Override
   public void closeScreen() {
      if (!Astolfo.INSTANCE.getModuleManager().get(HideWindow.class).isEnabled()) {
         this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
      } else if (this.mc.currentScreen instanceof GuiChest) {
         HideWindow.hiddenChest = (GuiChest)this.mc.currentScreen;
         HideWindow.hiddenWindowId = this.openContainer.windowId;
      }

      this.closeScreenAndDropStack();
   }

   public void closeScreenAndDropStack() {
      this.inventory.setItemStack((ItemStack)null);
      super.closeScreen();
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   public void setPlayerSPHealth(float health) {
      if (this.hasValidHealth) {
         float f = this.getHealth() - health;
         if (f <= 0.0F) {
            this.setHealth(health);
            if (f < 0.0F) {
               this.hurtResistantTime = this.maxHurtResistantTime / 2;
            }
         } else {
            this.lastDamage = f;
            this.setHealth(this.getHealth());
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(DamageSource.generic, f);
            this.hurtTime = this.maxHurtTime = 10;
         }
      } else {
         this.setHealth(health);
         this.hasValidHealth = true;
      }
   }

   @Override
   public void addStat(StatBase stat, int amount) {
      if (stat != null && stat.isIndependent) {
         super.addStat(stat, amount);
      }
   }

   @Override
   public void sendPlayerAbilities() {
      this.sendQueue.addToSendQueue(new C13PacketPlayerAbilities(this.capabilities));
   }

   @Override
   public boolean v() {
      return true;
   }

   public void sendHorseJump() {
      this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.RIDING_JUMP, (int)(this.getHorseJumpPower() * 100.0F)));
   }

   public void v_void() {
      this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.OPEN_INVENTORY));
   }

   public void setClientBrand(String brand) {
      this.clientBrand = brand;
   }

   public String getClientBrand() {
      return this.clientBrand;
   }

   public StatFileWriter getStatFileWriter() {
      return this.statWriter;
   }

   @Override
   public void addChatComponentMessage(IChatComponent chatComponent) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
   }

   @Override
   public boolean pushOutOfBlocks(double x, double y, double z) {
      PushOutOfBlocksEvent pushOutOfBlocksEvent = new PushOutOfBlocksEvent();
      Astolfo.INSTANCE.getEventBus().post(pushOutOfBlocksEvent);
      if (pushOutOfBlocksEvent.isCancelled()) {
         return false;
      }

      if (this.noClip) {
         return false;
      }

      BlockPos blockPos = new BlockPos(x, y, z);
      double d = x - blockPos.getX();
      double e = z - blockPos.getZ();
      if (!this.isOpenBlockSpace(blockPos)) {
         byte b = -1;
         double d2 = 9999.0;
         if (this.isOpenBlockSpace(blockPos.west()) && d < d2) {
            d2 = d;
            b = 0;
         }

         if (this.isOpenBlockSpace(blockPos.east()) && 1.0 - d < d2) {
            d2 = 1.0 - d;
            b = 1;
         }

         if (this.isOpenBlockSpace(blockPos.north()) && e < d2) {
            d2 = e;
            b = 4;
         }

         if (this.isOpenBlockSpace(blockPos.south()) && 1.0 - e < d2) {
            d2 = 1.0 - e;
            b = 5;
         }

         float f = 0.1F;
         if (b == 0) {
            this.motionX = -f;
         }

         if (b == 1) {
            this.motionX = f;
         }

         if (b == 4) {
            this.motionZ = -f;
         }

         if (b == 5) {
            this.motionZ = f;
         }
      }

      return false;
   }

   private boolean isOpenBlockSpace(BlockPos pos) {
      return !this.worldObj.getBlockState(pos).getBlock().isNormalCube() && !this.worldObj.getBlockState(pos.up()).getBlock().isNormalCube();
   }

   @Override
   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      this.sprintingTicksLeft = sprinting ? 600 : 0;
   }

   public void setXPStats(float currentXP, int maxXP, int level) {
      this.experience = currentXP;
      this.experienceTotal = maxXP;
      this.experienceLevel = level;
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(component);
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      return permLevel <= 0;
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
   }

   @Override
   public void playSound(String name, float volume, float pitch) {
      this.worldObj.playSound(this.posX, this.posY, this.posZ, name, volume, pitch, false);
   }

   @Override
   public boolean C() {
      return true;
   }

   public boolean Y8() {
      return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse && ((EntityHorse)this.ridingEntity).isHorseSaddled();
   }

   public float getHorseJumpPower() {
      return this.horseJumpPower;
   }

   @Override
   public void openEditSign(TileEntitySign signTile) {
      this.mc.displayGuiScreen(new GuiEditSign(signTile));
   }

   @Override
   public void openEditCommandBlock(CommandBlockLogic cmdBlockLogic) {
      this.mc.displayGuiScreen(new GuiCommandBlock(cmdBlockLogic));
   }

   @Override
   public void displayGUIBook(ItemStack bookStack) {
      Item item = bookStack.getItem();
      if (item == Items.writable_book) {
         this.mc.displayGuiScreen(new GuiScreenBook(this, bookStack, true));
      }
   }

   @Override
   public void displayGUIChest(IInventory chestInventory) {
      String string = chestInventory instanceof IInteractionObject ? ((IInteractionObject)chestInventory).getGuiID() : "minecraft:container";
      if ("minecraft:chest".equals(string)) {
         this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
      } else if ("minecraft:hopper".equals(string)) {
         this.mc.displayGuiScreen(new GuiHopper(this.inventory, chestInventory));
      } else if ("minecraft:furnace".equals(string)) {
         this.mc.displayGuiScreen(new GuiFurnace(this.inventory, chestInventory));
      } else if ("minecraft:brewing_stand".equals(string)) {
         this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, chestInventory));
      } else if ("minecraft:beacon".equals(string)) {
         this.mc.displayGuiScreen(new GuiBeacon(this.inventory, chestInventory));
      } else if (!"minecraft:dispenser".equals(string) && !"minecraft:dropper".equals(string)) {
         this.mc.displayGuiScreen(new GuiChest(this.inventory, chestInventory));
      } else {
         this.mc.displayGuiScreen(new GuiDispenser(this.inventory, chestInventory));
      }
   }

   @Override
   public void displayGUIHorse(EntityHorse horse, IInventory horseInventory) {
      this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, horseInventory, horse));
   }

   @Override
   public void displayGui(IInteractionObject guiOwner) {
      String string = guiOwner.getGuiID();
      if ("minecraft:crafting_table".equals(string)) {
         this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
      } else if ("minecraft:enchanting_table".equals(string)) {
         this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, guiOwner));
      } else if ("minecraft:anvil".equals(string)) {
         this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
      }
   }

   @Override
   public void displayVillagerTradeGui(IMerchant villager) {
      this.mc.displayGuiScreen(new GuiMerchant(this.inventory, villager, this.worldObj));
   }

   @Override
   public void L(Entity entity) {
      this.mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
   }

   @Override
   public void x(Entity entity) {
      this.mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC);
   }

   @Override
   public boolean isSneaking() {
      boolean bl = this.movementInput != null ? this.movementInput.sneak : false;
      return bl && !this.sleeping;
   }

   @Override
   public void updateEntityActionState() {
      float f = this.rotationYaw;
      RotationYawEvent rotationYawEvent = new RotationYawEvent(this.rotationYaw);
      Astolfo.INSTANCE.getEventBus().post(rotationYawEvent);
      this.rotationYaw = rotationYawEvent.getYaw();
      super.updateEntityActionState();
      this.rotationYaw = f;
      if (this.Y_()) {
         this.moveStrafing = this.movementInput.moveStrafe;
         this.moveForward = this.movementInput.moveForward;
         this.Lr = this.movementInput.jump;
         this.prevRenderArmYaw = this.renderArmYaw;
         this.prevRenderArmPitch = this.renderArmPitch;
         this.renderArmPitch = (float)(this.renderArmPitch + (this.rotationPitch - this.renderArmPitch) * 0.5);
         this.renderArmYaw = (float)(this.renderArmYaw + (this.rotationYaw - this.renderArmYaw) * 0.5);
      }
   }

   public boolean Y_() {
      return this.mc.getRenderViewEntity() == this;
   }

   @Override
   public void onLivingUpdate() {
      if (this.sprintingTicksLeft > 0) {
         this.sprintingTicksLeft--;
         if (this.sprintingTicksLeft == 0) {
            this.setSprinting(false);
         }
      }

      if (this.eD > 0) {
         this.eD--;
      }

      this.prevTimeInPortal = this.timeInPortal;
      if (this.inPortal) {
         if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }

         if (this.timeInPortal == 0.0F) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4F + 0.8F));
         }

         this.timeInPortal += 0.0125F;
         if (this.timeInPortal >= 1.0F) {
            this.timeInPortal = 1.0F;
         }

         this.inPortal = false;
      } else if (this.isPotionActive(Potion.confusion) && this.getActivePotionEffect(Potion.confusion).getDuration() > 60) {
         this.timeInPortal += 0.006666667F;
         if (this.timeInPortal > 1.0F) {
            this.timeInPortal = 1.0F;
         }
      } else {
         if (this.timeInPortal > 0.0F) {
            this.timeInPortal -= 0.05F;
         }

         if (this.timeInPortal < 0.0F) {
            this.timeInPortal = 0.0F;
         }
      }

      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal--;
      }

      boolean bl = this.movementInput.jump;
      boolean bl2 = this.movementInput.sneak;
      float f = 0.8F;
      boolean bl3 = this.movementInput.moveForward >= f;
      this.movementInput.updatePlayerMoveState();
      if (this.isUsingItem() && !this.E() && !ModuleManager.noSlow.isEnabled()) {
         this.movementInput.moveStrafe *= 0.2F;
         this.movementInput.moveForward *= 0.2F;
         this.eD = 0;
      }

      this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
      this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
      this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
      this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
      boolean bl4 = this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
      if (this.onGround
         && !bl2
         && !bl3
         && this.movementInput.moveForward >= f
         && !this.isSprinting()
         && bl4
         && !this.isUsingItem()
         && !this.isPotionActive(Potion.blindness)) {
         if (this.eD <= 0 && !this.mc.gameSettings.Ze.isKeyDown()) {
            this.eD = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if (!this.isSprinting()
         && this.movementInput.moveForward >= f
         && bl4
         && !this.isUsingItem()
         && !this.isPotionActive(Potion.blindness)
         && this.mc.gameSettings.Ze.isKeyDown()) {
         this.setSprinting(true);
      }

      if (this.isSprinting() && (this.movementInput.moveForward < f || this.isCollidedHorizontally || !bl4)) {
         boolean bl5 = Astolfo.INSTANCE.getModuleManager().get(Sprint.class).isEnabled();
         if ((!bl5 || !Sprint.omnidirectional.getValue())
            && (Speed.mode.getValue() != SpeedMode.WATCHDOG || !Astolfo.INSTANCE.getModuleManager().get(Speed.class).isEnabled())) {
            this.setSprinting(false);
         }
      }

      if (this.capabilities.allowFlying) {
         if (this.mc.playerController.C()) {
            if (!this.capabilities.isFlying) {
               this.capabilities.isFlying = true;
               this.sendPlayerAbilities();
            }
         } else if (!bl && this.movementInput.jump) {
            if (this.flyToggleTimer == 0) {
               this.flyToggleTimer = 7;
            } else {
               this.capabilities.isFlying = !this.capabilities.isFlying;
               this.sendPlayerAbilities();
               this.flyToggleTimer = 0;
            }
         }
      }

      if (this.capabilities.isFlying && this.Y_()) {
         if (this.movementInput.sneak) {
            this.motionY = this.motionY - this.capabilities.getFlySpeed() * 3.0F;
         }

         if (this.movementInput.jump) {
            this.motionY = this.motionY + this.capabilities.getFlySpeed() * 3.0F;
         }
      }

      if (this.Y8()) {
         if (this.eU < 0) {
            this.eU++;
            if (this.eU == 0) {
               this.horseJumpPower = 0.0F;
            }
         }

         if (bl && !this.movementInput.jump) {
            this.eU = -10;
            this.sendHorseJump();
         } else if (!bl && this.movementInput.jump) {
            this.eU = 0;
            this.horseJumpPower = 0.0F;
         } else if (bl) {
            this.eU++;
            if (this.eU < 10) {
               this.horseJumpPower = this.eU * 0.1F;
            } else {
               this.horseJumpPower = 0.8F + 2.0F / (this.eU - 9) * 0.1F;
            }
         }
      } else {
         this.horseJumpPower = 0.0F;
      }

      super.onLivingUpdate();
      if (this.onGround && this.capabilities.isFlying && !this.mc.playerController.C()) {
         this.capabilities.isFlying = false;
         this.sendPlayerAbilities();
      }
   }

   @Override
   public void moveEntity(double x, double y, double z) {
      MoveEvent moveEvent = new MoveEvent(x, y, z);
      Astolfo.INSTANCE.getEventBus().post(moveEvent);
      super.moveEntity(moveEvent.getX(), moveEvent.getY(), moveEvent.getZ());
      Astolfo.INSTANCE.getEventBus().post(new MoveEvent());
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      MoveWithHeadingEvent moveWithHeadingEvent = new MoveWithHeadingEvent(EventState.PRE, strafe, forward);
      Astolfo.INSTANCE.getEventBus().post(moveWithHeadingEvent);
      if (!moveWithHeadingEvent.isCancelled()) {
         super.moveEntityWithHeading(moveWithHeadingEvent.getStrafe(), moveWithHeadingEvent.getForward());
         Astolfo.INSTANCE.getEventBus().post(new MoveWithHeadingEvent(EventState.POST));
      }
   }

   public boolean m(double d) {
      return !this.mc.theWorld.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(0.0, -d, 0.0)).isEmpty();
   }

   public double B() {
      return Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
   }

   public void r(double d) {
      this.motionX = -(Math.sin(this.q()) * d);
      this.motionZ = Math.cos(this.q()) * d;
   }

   public void G(double d) {
      float f = this.q();
      if (GameUtil.isMoving(this)) {
         this.motionX = -MathHelper.sin(f) * d;
         this.motionZ = MathHelper.cos(f) * d;
      } else {
         this.motionX = 0.0;
         this.motionZ = 0.0;
      }
   }

   public float q() {
      float f = this.rotationYaw;
      if (this.moveForward < 0.0F) {
         f += 180.0F;
      }

      float g = 1.0F;
      if (this.moveForward < 0.0F) {
         g = -0.5F;
      } else if (this.moveForward > 0.0F) {
         g = 0.5F;
      }

      if (this.moveStrafing > 0.0F) {
         f -= 90.0F * g;
      } else if (this.moveStrafing < 0.0F) {
         f += 90.0F * g;
      }

      return f * (float) (Math.PI / 180.0);
   }

   public double T(Entity entity) {
      Vec3 vec3 = this.getPositionEyes(1.0F);
      Vec3 vec32 = this.d(vec3, entity.getEntityBoundingBox());
      double e = Math.abs(vec32.xCoord - vec3.xCoord);
      double d2 = Math.abs(vec32.yCoord - vec3.yCoord);
      double d3 = Math.abs(vec32.zCoord - vec3.zCoord);
      return Math.sqrt(Math.pow(e, 2.0) + Math.pow(d2, 2.0) + Math.pow(d3, 2.0));
   }

   public Vec3 d(Vec3 vec3, AxisAlignedBB axisAlignedBB) {
      double[] doubles = new double[]{vec3.xCoord, vec3.yCoord, vec3.zCoord};
      double[] doubles2 = new double[]{axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ};
      double[] doubles3 = new double[]{axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ};

      for (int i = 0; i < 2; i++) {
         if (doubles[i] > doubles3[i]) {
            doubles[i] = doubles3[i];
         } else if (doubles[i] < doubles2[i]) {
            doubles[i] = doubles2[i];
         }
      }

      return new Vec3(doubles[0], doubles[1], doubles[2]);
   }

   public boolean T() {
      double d = this.posY - 0.03;

      for (int i = MathHelper.floor_double(this.posX); i < MathHelper.ceiling_double_int(this.posX); i++) {
         for (int j = MathHelper.floor_double(this.posZ); j < MathHelper.ceiling_double_int(this.posZ); j++) {
            BlockPos blockPos = new BlockPos(i, MathHelper.floor_double(d), j);
            if (Minecraft.getMinecraft().theWorld.getBlockState(blockPos).getBlock() instanceof BlockLiquid) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean Yv() {
      double d = this.posY + 0.01;

      for (int i = MathHelper.floor_double(this.posX); i < MathHelper.ceiling_double_int(this.posX); i++) {
         for (int j = MathHelper.floor_double(this.posZ); j < MathHelper.ceiling_double_int(this.posZ); j++) {
            BlockPos blockPos = new BlockPos(i, (int)d, j);
            if (Minecraft.getMinecraft().theWorld.getBlockState(blockPos).getBlock() instanceof BlockLiquid) {
               return true;
            }
         }
      }

      return false;
   }
}
