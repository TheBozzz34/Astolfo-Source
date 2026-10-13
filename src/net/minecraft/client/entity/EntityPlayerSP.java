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
   private static final long bb = 84607402890430L;

   public EntityPlayerSP(Minecraft var1, World var2, NetHandlerPlayClient var3, StatFileWriter var4) {
      super(var2, var3.getGameProfile());
      this.sendQueue = var3;
      this.statWriter = var4;
      this.mc = var1;
      this.dimension = 0;
   }

   @Override
   public boolean attackEntityFrom(DamageSource var1, float var2) {
      return false;
   }

   @Override
   public void heal(float var1) {
   }

   @Override
   public void mountEntity(Entity var1) {
      super.mountEntity(var1);
      if (var1 instanceof EntityMinecart) {
         this.mc.getSoundHandler().playSound(new MovingSoundMinecartRiding(this, (EntityMinecart)var1));
      }
   }

   @Override
   public void onUpdate() {
      long var1 = bb ^ 91624362374408L;
      long var3 = var1 ^ 44873893469189L;
      if (this.worldObj.isBlockLoaded(new BlockPos(this.posX, 0.0, this.posZ))) {
         Astolfo.INSTANCE.getEventBus().post(new UpdateEvent(EventState.PRE));
         super.onUpdate();
         Astolfo.INSTANCE.getEventBus().post(new UpdateEvent(EventState.POST));
         boolean var6 = Astolfo.INSTANCE.getModuleManager().get(RiderGod.class).isEnabled();
         if (this.E()) {
            this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
            this.sendQueue.addToSendQueue(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            if (RiderGod.S()) {
               this.sendQueue.addToSendQueue(new VehicleMovePacket(this.ridingEntity));
            }
         } else {
            this.i();
         }
      }
   }

   public void i() {
      if (!(this.mc.disablerTimer.timerSpeed > 1.0F) || this.mc.timer.timerSpeed != 1.0F) {
         MotionEvent var4 = new MotionEvent(
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
         Astolfo.INSTANCE.getEventBus().post(var4);
         if (var4.isCancelled()) {
            Astolfo.INSTANCE.getEventBus().post(new MotionEvent(EventState.POST, this.sendQueue));
         } else {
            boolean var5 = this.isSprinting();
            if (var5 != this.ej) {
               if (var5) {
                  this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
               } else {
                  this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
               }

               this.ej = var5;
            }

            boolean var6 = this.isSneaking();
            if (var6 != this.eF) {
               if (var6) {
                  this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
               } else {
                  this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
               }

               this.eF = var6;
            }

            if (this.Y_()) {
               double var7 = var4.getX();
               double var9 = var4.getMinY();
               double var11 = var4.getZ();
               float var13 = var4.getYaw();
               float var14 = var4.getPitch();
               double var15 = var7 - this.lastReportedPosX;
               double var17 = var9 - this.lastReportedPosY;
               double var19 = var11 - this.lastReportedPosZ;
               double var21 = var13 - this.lastReportedYaw;
               double var23 = var14 - this.lastReportedPitch;
               boolean var25 = var15 * var15 + var17 * var17 + var19 * var19 > 9.0E-4 || this.eR >= 20;
               boolean var26 = var21 != 0.0 || var23 != 0.0;
               boolean var27 = var4.isOnGround();
               if (this.ridingEntity == null) {
                  if (var25 && var26) {
                     this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(var7, var9, var11, var13, var14, var27));
                  } else if (var25) {
                     this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(var7, var9, var11, var27));
                  } else if (var26) {
                     this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(var13, var14, var27));
                  } else if (RiderGod.r()) {
                     if (this.el != var27) {
                        this.sendQueue.addToSendQueue(new C03PacketPlayer(var27));
                     }
                  } else {
                     this.sendQueue.addToSendQueue(new C03PacketPlayer(var27));
                  }
               } else {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, var13, var14, var27));
                  var25 = false;
               }

               this.eR++;
               if (var25) {
                  this.lastReportedPosX = var7;
                  this.lastReportedPosY = var9;
                  this.lastReportedPosZ = var11;
                  this.eR = 0;
               }

               if (var26) {
                  this.lastReportedYaw = var13;
                  this.lastReportedPitch = var14;
               }

               this.el = var27;
            }

            Astolfo.INSTANCE.getEventBus().post(new MotionEvent(EventState.POST, this.sendQueue));
         }
      }
   }

   public void k() {
      MotionEvent var4 = new MotionEvent(
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
      Astolfo.INSTANCE.getEventBus().post(var4);
      if (var4.isCancelled()) {
         Astolfo.INSTANCE.getEventBus().post(new MotionEvent(EventState.POST, this.sendQueue));
      } else {
         boolean var5 = this.isSprinting();
         if (var5 != this.ej) {
            if (var5) {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
            } else {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }

            this.ej = var5;
         }

         boolean var6 = this.isSneaking();
         if (var6 != this.eF) {
            if (var6) {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
               this.sendQueue.addToSendQueue(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }

            this.eF = var6;
         }

         if (this.Y_()) {
            double var7 = var4.getX();
            double var9 = var4.getMinY();
            double var11 = var4.getZ();
            float var13 = var4.getYaw();
            float var14 = var4.getPitch();
            double var15 = var7 - this.lastReportedPosX;
            double var17 = var9 - this.lastReportedPosY;
            double var19 = var11 - this.lastReportedPosZ;
            double var21 = var13 - this.lastReportedYaw;
            double var23 = var14 - this.lastReportedPitch;
            boolean var25 = var15 * var15 + var17 * var17 + var19 * var19 > 9.0E-4 || this.eR >= 20;
            boolean var26 = var21 != 0.0 || var23 != 0.0;
            boolean var27 = var4.isOnGround();
            if (this.ridingEntity == null) {
               if (var25 && var26) {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(var7, var9, var11, var13, var14, var27));
               } else if (var25) {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(var7, var9, var11, var27));
               } else if (var26) {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(var13, var14, var27));
               } else if (RiderGod.r()) {
                  if (this.el != var27) {
                     this.sendQueue.addToSendQueue(new C03PacketPlayer(var27));
                  }
               } else {
                  this.sendQueue.addToSendQueue(new C03PacketPlayer(var27));
               }
            } else {
               this.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, var13, var14, var27));
               var25 = false;
            }

            this.eR++;
            if (var25) {
               this.lastReportedPosX = var7;
               this.lastReportedPosY = var9;
               this.lastReportedPosZ = var11;
               this.eR = 0;
            }

            if (var26) {
               this.lastReportedYaw = var13;
               this.lastReportedPitch = var14;
            }

            this.el = var27;
         }

         Astolfo.INSTANCE.getEventBus().post(new MotionEvent(EventState.POST, this.sendQueue));
      }
   }

   @Override
   public EntityItem dropOneItem(boolean var1) {
      C07PacketPlayerDigging.Action var4 = var1 ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
      this.sendQueue.addToSendQueue(new C07PacketPlayerDigging(var4, BlockPos.ORIGIN, EnumFacing.DOWN));
      return null;
   }

   @Override
   public void joinEntityItemWithWorld(EntityItem var1) {
   }

   public void sendChatMessage(String var1) {
      ChatEvent var4 = new ChatEvent(ChatDirection.TO_SEND, var1);
      Astolfo.INSTANCE.getEventBus().post(var4);
      if (!var4.isCancelled()) {
         this.sendQueue.addToSendQueue(new C01PacketChatMessage(var4.getMessage()));
      }
   }

   public void sendClientChatMessage(String var1) {
      ChatEvent var4 = new ChatEvent(ChatDirection.TO_SEND, var1);
      var4.setUserTyped(false);
      Astolfo.INSTANCE.getEventBus().post(var4);
      if (!var4.isCancelled()) {
         this.sendQueue.addToSendQueue(new C01PacketChatMessage(var4.getMessage()));
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
   public void damageEntity(DamageSource var1, float var2) {
      if (!this.isEntityInvulnerable(var1)) {
         this.setHealth(this.getHealth() - var2);
      }
   }

   @Override
   public void closeScreen() {
      long var1 = bb ^ 80964073963857L;
      long var3 = var1 ^ 55139246729308L;
      if (!Astolfo.INSTANCE.getModuleManager().get(HideWindow.class).isEnabled()) {
         this.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.openContainer.windowId));
      } else if (this.mc.currentScreen instanceof GuiChest) {
         HideWindow.w = (GuiChest)this.mc.currentScreen;
         HideWindow.W = this.openContainer.windowId;
      }

      this.closeScreenAndDropStack();
   }

   public void closeScreenAndDropStack() {
      this.inventory.setItemStack((ItemStack)null);
      super.closeScreen();
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   public void setPlayerSPHealth(float var1) {
      if (this.hasValidHealth) {
         float var5 = this.getHealth() - var1;
         if (var5 <= 0.0F) {
            this.setHealth(var1);
            if (var5 < 0.0F) {
               this.hurtResistantTime = this.maxHurtResistantTime / 2;
            }
         } else {
            this.lastDamage = var5;
            this.setHealth(this.getHealth());
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(DamageSource.generic, var5);
            this.hurtTime = this.maxHurtTime = 10;
         }
      } else {
         this.setHealth(var1);
         this.hasValidHealth = true;
      }
   }

   @Override
   public void addStat(StatBase var1, int var2) {
      if (var1 != null && var1.isIndependent) {
         super.addStat(var1, var2);
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

   public void setClientBrand(String var1) {
      this.clientBrand = var1;
   }

   public String getClientBrand() {
      return this.clientBrand;
   }

   public StatFileWriter getStatFileWriter() {
      return this.statWriter;
   }

   @Override
   public void addChatComponentMessage(IChatComponent var1) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(var1);
   }

   @Override
   public boolean pushOutOfBlocks(double var1, double var3, double var5) {
      PushOutOfBlocksEvent var10 = new PushOutOfBlocksEvent();
      Astolfo.INSTANCE.getEventBus().post(var10);
      if (var10.isCancelled()) {
         return false;
      }

      if (this.noClip) {
         return false;
      }

      BlockPos var11 = new BlockPos(var1, var3, var5);
      double var12 = var1 - var11.getX();
      double var14 = var5 - var11.getZ();
      if (!this.isOpenBlockSpace(var11)) {
         byte var16 = -1;
         double var17 = 9999.0;
         if (this.isOpenBlockSpace(var11.west()) && var12 < var17) {
            var17 = var12;
            var16 = 0;
         }

         if (this.isOpenBlockSpace(var11.east()) && 1.0 - var12 < var17) {
            var17 = 1.0 - var12;
            var16 = 1;
         }

         if (this.isOpenBlockSpace(var11.north()) && var14 < var17) {
            var17 = var14;
            var16 = 4;
         }

         if (this.isOpenBlockSpace(var11.south()) && 1.0 - var14 < var17) {
            var17 = 1.0 - var14;
            var16 = 5;
         }

         float var19 = 0.1F;
         if (var16 == 0) {
            this.motionX = -var19;
         }

         if (var16 == 1) {
            this.motionX = var19;
         }

         if (var16 == 4) {
            this.motionZ = -var19;
         }

         if (var16 == 5) {
            this.motionZ = var19;
         }
      }

      return false;
   }

   private boolean isOpenBlockSpace(BlockPos var1) {
      return !this.worldObj.getBlockState(var1).getBlock().isNormalCube() && !this.worldObj.getBlockState(var1.up()).getBlock().isNormalCube();
   }

   @Override
   public void setSprinting(boolean var1) {
      super.setSprinting(var1);
      this.sprintingTicksLeft = var1 ? 600 : 0;
   }

   public void setXPStats(float var1, int var2, int var3) {
      this.experience = var1;
      this.experienceTotal = var2;
      this.experienceLevel = var3;
   }

   @Override
   public void addChatMessage(IChatComponent var1) {
      this.mc.ingameGUI.getChatGUI().printChatMessage(var1);
   }

   @Override
   public boolean canCommandSenderUseCommand(int var1, String var2) {
      return var1 <= 0;
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
   }

   @Override
   public void playSound(String var1, float var2, float var3) {
      this.worldObj.playSound(this.posX, this.posY, this.posZ, var1, var2, var3, false);
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
   public void openEditSign(TileEntitySign var1) {
      this.mc.displayGuiScreen(new GuiEditSign(var1));
   }

   @Override
   public void openEditCommandBlock(CommandBlockLogic var1) {
      this.mc.displayGuiScreen(new GuiCommandBlock(var1));
   }

   @Override
   public void displayGUIBook(ItemStack var1) {
      Item var4 = var1.getItem();
      if (var4 == Items.writable_book) {
         this.mc.displayGuiScreen(new GuiScreenBook(this, var1, true));
      }
   }

   @Override
   public void displayGUIChest(IInventory var1) {
      String var5 = var1 instanceof IInteractionObject ? ((IInteractionObject)var1).getGuiID() : "minecraft:container";
      if ("minecraft:chest".equals(var5)) {
         this.mc.displayGuiScreen(new GuiChest(this.inventory, var1));
      } else if ("minecraft:hopper".equals(var5)) {
         this.mc.displayGuiScreen(new GuiHopper(this.inventory, var1));
      } else if ("minecraft:furnace".equals(var5)) {
         this.mc.displayGuiScreen(new GuiFurnace(this.inventory, var1));
      } else if ("minecraft:brewing_stand".equals(var5)) {
         this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, var1));
      } else if ("minecraft:beacon".equals(var5)) {
         this.mc.displayGuiScreen(new GuiBeacon(this.inventory, var1));
      } else if (!"minecraft:dispenser".equals(var5) && !"minecraft:dropper".equals(var5)) {
         this.mc.displayGuiScreen(new GuiChest(this.inventory, var1));
      } else {
         this.mc.displayGuiScreen(new GuiDispenser(this.inventory, var1));
      }
   }

   @Override
   public void displayGUIHorse(EntityHorse var1, IInventory var2) {
      this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, var2, var1));
   }

   @Override
   public void displayGui(IInteractionObject var1) {
      String var5 = var1.getGuiID();
      if ("minecraft:crafting_table".equals(var5)) {
         this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
      } else if ("minecraft:enchanting_table".equals(var5)) {
         this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, var1));
      } else if ("minecraft:anvil".equals(var5)) {
         this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
      }
   }

   @Override
   public void displayVillagerTradeGui(IMerchant var1) {
      this.mc.displayGuiScreen(new GuiMerchant(this.inventory, var1, this.worldObj));
   }

   @Override
   public void L(Entity var1) {
      this.mc.effectRenderer.emitParticleAtEntity(var1, EnumParticleTypes.CRIT);
   }

   @Override
   public void x(Entity var1) {
      this.mc.effectRenderer.emitParticleAtEntity(var1, EnumParticleTypes.CRIT_MAGIC);
   }

   @Override
   public boolean isSneaking() {
      boolean var4 = this.movementInput != null ? this.movementInput.sneak : false;
      return var4 && !this.sleeping;
   }

   @Override
   public void updateEntityActionState() {
      float var4 = this.rotationYaw;
      RotationYawEvent var5 = new RotationYawEvent(this.rotationYaw);
      Astolfo.INSTANCE.getEventBus().post(var5);
      this.rotationYaw = var5.getYaw();
      super.updateEntityActionState();
      this.rotationYaw = var4;
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
      long var1 = bb ^ 135587691026694L;
      long var3 = var1 ^ 910696673291L;
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

      boolean var7 = this.movementInput.jump;
      boolean var8 = this.movementInput.sneak;
      float var9 = 0.8F;
      boolean var10 = this.movementInput.moveForward >= var9;
      this.movementInput.updatePlayerMoveState();
      if (this.isUsingItem() && !this.E() && !ModuleManager.D.isEnabled()) {
         this.movementInput.moveStrafe *= 0.2F;
         this.movementInput.moveForward *= 0.2F;
         this.eD = 0;
      }

      this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
      this.pushOutOfBlocks(this.posX - this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
      this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ - this.width * 0.35);
      this.pushOutOfBlocks(this.posX + this.width * 0.35, this.getEntityBoundingBox().minY + 0.5, this.posZ + this.width * 0.35);
      boolean var11 = this.getFoodStats().getFoodLevel() > 6.0F || this.capabilities.allowFlying;
      if (this.onGround
         && !var8
         && !var10
         && this.movementInput.moveForward >= var9
         && !this.isSprinting()
         && var11
         && !this.isUsingItem()
         && !this.isPotionActive(Potion.blindness)) {
         if (this.eD <= 0 && !this.mc.gameSettings.Ze.isKeyDown()) {
            this.eD = 7;
         } else {
            this.setSprinting(true);
         }
      }

      if (!this.isSprinting()
         && this.movementInput.moveForward >= var9
         && var11
         && !this.isUsingItem()
         && !this.isPotionActive(Potion.blindness)
         && this.mc.gameSettings.Ze.isKeyDown()) {
         this.setSprinting(true);
      }

      if (this.isSprinting() && (this.movementInput.moveForward < var9 || this.isCollidedHorizontally || !var11)) {
         boolean var12 = Astolfo.INSTANCE.getModuleManager().get(Sprint.class).isEnabled();
         if ((!var12 || !Sprint.omnidirectional.getValue())
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
         } else if (!var7 && this.movementInput.jump) {
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

         if (var7 && !this.movementInput.jump) {
            this.eU = -10;
            this.sendHorseJump();
         } else if (!var7 && this.movementInput.jump) {
            this.eU = 0;
            this.horseJumpPower = 0.0F;
         } else if (var7) {
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
   public void moveEntity(double var1, double var3, double var5) {
      MoveEvent var9 = new MoveEvent(var1, var3, var5);
      Astolfo.INSTANCE.getEventBus().post(var9);
      super.moveEntity(var9.getX(), var9.getY(), var9.getZ());
      Astolfo.INSTANCE.getEventBus().post(new MoveEvent());
   }

   @Override
   public void moveEntityWithHeading(float var1, float var2) {
      MoveWithHeadingEvent var6 = new MoveWithHeadingEvent(EventState.PRE, var1, var2);
      Astolfo.INSTANCE.getEventBus().post(var6);
      if (!var6.isCancelled()) {
         super.moveEntityWithHeading(var6.getStrafe(), var6.getForward());
         Astolfo.INSTANCE.getEventBus().post(new MoveWithHeadingEvent(EventState.POST));
      }
   }

   public boolean m(double var1) {
      return !this.mc.theWorld.getCollidingBoundingBoxes(this, this.getEntityBoundingBox().offset(0.0, -var1, 0.0)).isEmpty();
   }

   public double B() {
      return Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
   }

   public void r(double var1) {
      this.motionX = -(Math.sin(this.q()) * var1);
      this.motionZ = Math.cos(this.q()) * var1;
   }

   public void G(double var1) {
      float var6 = this.q();
      if (GameUtil.isMoving(this)) {
         this.motionX = -MathHelper.sin(var6) * var1;
         this.motionZ = MathHelper.cos(var6) * var1;
      } else {
         this.motionX = 0.0;
         this.motionZ = 0.0;
      }
   }

   public float q() {
      float var4 = this.rotationYaw;
      if (this.moveForward < 0.0F) {
         var4 += 180.0F;
      }

      float var5 = 1.0F;
      if (this.moveForward < 0.0F) {
         var5 = -0.5F;
      } else if (this.moveForward > 0.0F) {
         var5 = 0.5F;
      }

      if (this.moveStrafing > 0.0F) {
         var4 -= 90.0F * var5;
      } else if (this.moveStrafing < 0.0F) {
         var4 += 90.0F * var5;
      }

      return var4 * (float) (Math.PI / 180.0);
   }

   public double T(Entity var1) {
      Vec3 var2 = this.getPositionEyes(1.0F);
      Vec3 var3 = this.d(var2, var1.getEntityBoundingBox());
      double var4 = Math.abs(var3.xCoord - var2.xCoord);
      double var6 = Math.abs(var3.yCoord - var2.yCoord);
      double var8 = Math.abs(var3.zCoord - var2.zCoord);
      return Math.sqrt(Math.pow(var4, 2.0) + Math.pow(var6, 2.0) + Math.pow(var8, 2.0));
   }

   public Vec3 d(Vec3 var1, AxisAlignedBB var2) {
      double[] var6 = new double[]{var1.xCoord, var1.yCoord, var1.zCoord};
      double[] var7 = new double[]{var2.minX, var2.minY, var2.minZ};
      double[] var8 = new double[]{var2.maxX, var2.maxY, var2.maxZ};

      for (int var9 = 0; var9 < 2; var9++) {
         if (var6[var9] > var8[var9]) {
            var6[var9] = var8[var9];
         } else if (var6[var9] < var7[var9]) {
            var6[var9] = var7[var9];
         }
      }

      return new Vec3(var6[0], var6[1], var6[2]);
   }

   public boolean T() {
      double var4 = this.posY - 0.03;

      for (int var6 = MathHelper.floor_double(this.posX); var6 < MathHelper.ceiling_double_int(this.posX); var6++) {
         for (int var7 = MathHelper.floor_double(this.posZ); var7 < MathHelper.ceiling_double_int(this.posZ); var7++) {
            BlockPos var8 = new BlockPos(var6, MathHelper.floor_double(var4), var7);
            if (Minecraft.getMinecraft().theWorld.getBlockState(var8).getBlock() instanceof BlockLiquid) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean Yv() {
      double var4 = this.posY + 0.01;

      for (int var6 = MathHelper.floor_double(this.posX); var6 < MathHelper.ceiling_double_int(this.posX); var6++) {
         for (int var7 = MathHelper.floor_double(this.posZ); var7 < MathHelper.ceiling_double_int(this.posZ); var7++) {
            BlockPos var8 = new BlockPos(var6, (int)var4, var7);
            if (Minecraft.getMinecraft().theWorld.getBlockState(var8).getBlock() instanceof BlockLiquid) {
               return true;
            }
         }
      }

      return false;
   }
}
