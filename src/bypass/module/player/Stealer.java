package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.player.MoveEvent;
import bypass.event.render.RenderLivingEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.Rotation;
import bypass.util.Rotator;
import bypass.util.Stopwatch;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vector3d;
import org.lwjgl.input.Mouse;

public final class Stealer extends Module {
   private final Stopwatch clickTimer = new Stopwatch();
   private final Stopwatch closeTimer = new Stopwatch();
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Delay in milliseconds between moving each item", 250, 0, 2000, "ms");
   private final BooleanSetting close = new BooleanSetting("Close", "Close a chest when done stealing", true);
   private final BooleanSetting reverse = new BooleanSetting("Reverse", "Steals different", true);
   private final BooleanSetting silent = new BooleanSetting("Silent", "Silent", true);
   private final BooleanSetting moveMouse = new BooleanSetting("Move-Mouse", "Lets you move your mouse", false);
   private final BooleanSetting autoStop = new BooleanSetting("Auto-Stop", "Automatically stops while stealing", false);
   private final NumberSetting<Integer> closeDelay = new NumberSetting<>("Close-Delay", "Delay before closing the chest", 150, 0, 3000, "ms");
   private static final BooleanSetting titleCheck = new BooleanSetting("Title-Check", "Checks for custon chest name", true);
   private final MapSetting aura = new MapSettingBuilder("Aura")
      .ordered()
      .description("Aura stuff")
      .add("Enabled", new BooleanSetting("Enabled", "Enable chest aura", false))
      .add("Delay", new NumberSetting<>("Delay", "Delays opening chests", 250, 0, 1000, "ms"))
      .add("Lock-View", new BooleanSetting("Lock-View", "Enables lock view when opening chest", false))
      .add("Assist", new BooleanSetting("Assist", "Only opens chests when right clicking", true))
      .build();
   private TileEntity auraTarget;
   private final Rotator rotator = new Rotator(false, 15.0F);
   private float auraYaw;
   private float lastYaw;
   private float auraPitch;
   private float lastPitch;
   private final List<BlockPos> openedChests = new ArrayList<>();
   private final Queue<C0EPacketClickWindow> pendingClicks = new LinkedList<>();
   private GuiChest silentChest;
   private int windowId;
   private boolean unusedFlag;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      long var2 = c ^ 128838197311750L;
      long var4 = var2 ^ 90164795600890L;
      if (var1x.getDirection() == PacketDirection.OUTGOING) {
         if (var1x.getPacket() instanceof C0EPacketClickWindow) {
            this.clickTimer.reset();
         }

         if (var1x.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement var7 = (C08PacketPlayerBlockPlacement)var1x.getPacket();
            if (var7.getPlacedBlockDirection() != 255) {
               IBlockState var8 = mc.theWorld.getBlockState(var7.getPosition());
               if (var8 != null && var8.getBlock() instanceof BlockChest) {
                  this.closeTimer.reset();
                  this.clickTimer.reset();
               }
            }
         }

         if (var1x.getPacket() instanceof C0DPacketCloseWindow) {
            this.resetSilentChest();
         }
      } else {
         if (var1x.getPacket() instanceof S2DPacketOpenWindow) {
            S2DPacketOpenWindow var12 = (S2DPacketOpenWindow)var1x.getPacket();
            if (var12.getWindowTitle() != null && mc.thePlayer.inventory != null) {
               this.resetSilentChest();
               this.silentChest = new GuiChest(mc.thePlayer.inventory, new ContainerLocalMenu(var12.getGuiId(), var12.getWindowTitle(), var12.getSlotCount()));
               this.closeTimer.reset();
               if (this.silent.getValue() && this.silentChest.lowerChestInventory != null && this.silentChest.lowerChestInventory.getDisplayName() != null) {
                  if (isChestTitle(this.silentChest.lowerChestInventory.getDisplayName().getUnformattedText())) {
                     var1x.cancel();
                  } else {
                     this.resetSilentChest();
                  }
               }
            }
         }

         if (this.silent.getValue() && this.silentChest != null && var1x.getPacket() instanceof S30PacketWindowItems) {
            S30PacketWindowItems var13 = (S30PacketWindowItems)var1x.getPacket();
            if (var13.getItemStacks() != null) {
               short var14 = 0;

               for (int var9 = 0; var9 < var13.getItemStacks().length - 36; var9++) {
                  try {
                     ItemStack var10 = var13.getItemStacks()[var9];
                     if (var10 != null
                        && this.silentChest.lowerChestInventory != null
                        && this.silentChest.lowerChestInventory.getDisplayName() != null
                        && isChestTitle(this.silentChest.lowerChestInventory.getDisplayName().getUnformattedText())
                        && (
                           client.getModuleManager().get(InventoryManager.class).isEnabled()
                                 && client.getModuleManager().get(InventoryManager.class).wouldKeep(var10)
                              || !client.getModuleManager().get(InventoryManager.class).isEnabled()
                        )) {
                        this.pendingClicks.offer(new C0EPacketClickWindow(var13.func_148911_c(), var9, 0, 1, var10, var14++));
                     }
                  } catch (Exception var11) {
                  }
               }

               if (this.silentChest.inventorySlots != null) {
                  this.silentChest.inventorySlots.putStacksInSlots(var13.getItemStacks());
               }

               this.windowId = var13.func_148911_c();
            }
         }

         if (var1x.getPacket() instanceof S2EPacketCloseWindow) {
            this.resetSilentChest();
         }
      }
   };
   private boolean stealing;
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = var1x -> {
      boolean var5 = this.aura.getSetting("Enabled").asBoolean();
      this.stealing = false;
      if (!this.silent.getValue()) {
         if (mc.currentScreen instanceof GuiContainer) {
            GuiContainer var6 = (GuiContainer)mc.currentScreen;
            if (var6 instanceof GuiChest) {
               GuiChest var7 = (GuiChest)mc.currentScreen;
               if (isChestTitle(var7.lowerChestInventory.getDisplayName().getUnformattedText())) {
                  if (this.moveMouse.getValue()) {
                     Mouse.setGrabbed(true);
                     mc.inGameHasFocus = true;
                  }

                  this.stealing = true;
                  if (this.reverse.getValue()) {
                     int var20 = var7.lowerChestInventory.getSizeInventory();

                     while (var20-- > 0) {
                        this.stealSlot(var6, var7, var20);
                     }
                  } else {
                     for (int var8 = 0; var8 < var7.lowerChestInventory.getSizeInventory(); var8++) {
                        this.stealSlot(var6, var7, var8);
                     }
                  }

                  if (this.close.getValue() && this.closeTimer.hasElapsed(this.closeDelay.getValue().intValue()) && !this.hasItemsLeft(var7)) {
                     mc.thePlayer.closeScreen();
                     this.closeTimer.reset();
                  }
               }
            }
         }
      } else {
         try {
            if (this.silentChest != null && this.silentChest.inventorySlots != null) {
               if (!this.pendingClicks.isEmpty()) {
                  if (this.clickTimer.hasElapsed(this.delay.getValue().intValue())) {
                     C0EPacketClickWindow var16 = this.pendingClicks.poll();
                     if (var16 != null) {
                        this.stealing = true;
                        mc.thePlayer.sendQueue.addToSendQueue(var16);
                        this.silentChest.inventorySlots.slotClick(var16.getSlotId(), var16.getUsedButton(), var16.getMode(), mc.thePlayer);
                     }
                  }
               } else if (this.closeTimer.hasElapsed(this.closeDelay.getValue().intValue())) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.windowId));
                  this.closeTimer.reset();
               }
            }
         } catch (Exception var15) {
         }
      }

      if (mc.currentScreen instanceof GuiChest || this.silentChest != null || mc.currentScreen instanceof GuiInventory || !var5) {
         this.auraTarget = null;
      } else if (this.aura.getSetting("Assist").asBoolean() && !Mouse.isButtonDown(1)) {
         this.auraTarget = null;
      } else {
         boolean var17 = this.aura.getSetting("Lock-View").asBoolean();
         switch (var1x.getState()) {
            case PRE:
               this.auraTarget = null;

               for (TileEntity var21 : mc.theWorld.loadedTileEntityList) {
                  if (this.canOpen(var21)) {
                     this.auraTarget = var21;
                     Vector3d var9 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                     Vector3d var10 = new Vector3d(
                        this.auraTarget.getPos().getX() + 0.5, this.auraTarget.getPos().getY() + 0.5, this.auraTarget.getPos().getZ() + 0.5
                     );
                     Rotation var11 = this.rotator.rotationTo(var10, var9);
                     float var12 = var11.getYaw();
                     float var13 = var11.getPitch();
                     float var14 = MathHelper.wrapAngleTo180_float(var12 - mc.thePlayer.rotationYaw);
                     var12 = mc.thePlayer.rotationYaw + var14;
                     this.lastYaw = var1x.getLastYaw();
                     this.lastPitch = var1x.getLastPitch();
                     if (!GameUtil.isOnHypixel()) {
                        var1x.setYaw(var17 ? (mc.thePlayer.rotationYaw = var12) : var12);
                        var1x.setPitch(var17 ? (mc.thePlayer.rotationPitch = var13) : var13);
                     }

                     this.auraYaw = var1x.getYaw();
                     this.auraPitch = var1x.getPitch();
                  }
               }
               break;
            case POST:
               if (this.auraTarget != null && this.clickTimer.hasElapsed(this.aura.getSetting("Delay").asInt())) {
                  MovingObjectPosition var18 = GameUtil.A(this.auraYaw, this.auraPitch, mc.playerController.getBlockReachDistance());
                  if (this.isChestHit(var18)) {
                     if (mc.playerController
                        .onPlayerRightClick(
                           mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), var18.getBlockPos(), var18.sideHit, var18.hitVec
                        )) {
                        this.closeTimer.reset();
                        if (this.silent.getValue()) {
                           mc.thePlayer.sendQueue.u(new C0APacketAnimation());
                        } else {
                           mc.thePlayer.swingItem();
                        }

                        this.clickTimer.reset();
                     }

                     this.openedChests.add(this.auraTarget.getPos());
                  }
               }
         }
      }
   };
   @EventHandler(MoveEvent.class)
   private Listener<MoveEvent> onMove = var1x -> {
      if (this.stealing && this.autoStop.getValue() && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
         var1x.setX(mc.thePlayer.motionX = 0.0);
         var1x.setY(mc.thePlayer.motionY = 0.0);
         var1x.setZ(mc.thePlayer.motionZ = 0.0);
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = var1x -> {
      if (this.auraTarget != null && var1x.getEntity() == mc.thePlayer) {
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = var1x -> {
      this.openedChests.clear();
      this.resetSilentChest();
   };
   private static final long c = 17985607461711L;

   public Stealer(Category var1) {
      super(var1);
      this.addSettings(this.aura, this.delay, this.closeDelay, this.close, this.reverse, titleCheck, this.silent, this.moveMouse, this.autoStop);
   }

   private void stealSlot(GuiContainer var1, GuiChest var2, int var3) {
      long var4 = c ^ 10525064128907L;
      long var6 = var4 ^ 51397149228919L;
      ItemStack var9 = var2.lowerChestInventory.getStackInSlot(var3);
      if (var9 != null && var9.getItem() != null) {
         if ((
               client.getModuleManager().get(InventoryManager.class).isEnabled() && client.getModuleManager().get(InventoryManager.class).wouldKeep(var9)
                  || !client.getModuleManager().get(InventoryManager.class).isEnabled()
                  || this.silent.getValue()
            )
            && this.clickTimer.hasElapsed(this.delay.getValue().intValue())) {
            mc.playerController.windowClick(this.silent.getValue() ? this.windowId : var1.inventorySlots.windowId, var3, 0, 1, mc.thePlayer);
            this.closeTimer.reset();
         }
      }
   }

   @Override
   public void onEnable() {
      this.closeTimer.reset();
      this.resetSilentChest();
      super.onEnable();
   }

   private void resetSilentChest() {
      this.silentChest = null;
      this.windowId = 0;
      this.pendingClicks.clear();
   }

   private boolean isChestHit(MovingObjectPosition var1) {
      if (var1 != null && var1.getBlockPos() != null) {
         BlockPos var5 = var1.getBlockPos();
         Block var6 = mc.theWorld.getBlockState(var5).getBlock();
         return (var6.getMaterial() != Material.air || var6.getMaterial() != Material.water || var6.getMaterial() != Material.lava)
            && var6 instanceof BlockChest;
      } else {
         return false;
      }
   }

   private boolean canOpen(TileEntity var1) {
      return !(var1 instanceof TileEntityChest)
         ? false
         : !this.openedChests.contains(var1.getPos())
            && mc.thePlayer.getDistance(var1.getPos().getX(), var1.getPos().getY(), var1.getPos().getZ()) < mc.playerController.getBlockReachDistance();
   }

   private boolean hasItemsLeft(GuiChest var1) {
      long var2 = c ^ 84239377091547L;
      long var4 = var2 ^ 118411581644071L;

      for (int var7 = 0; var7 < var1.lowerChestInventory.getSizeInventory(); var7++) {
         if (isChestTitle(var1.lowerChestInventory.getDisplayName().getFormattedText())
            && var1.inventorySlots.getSlot(var7).getHasStack()
            && (
               client.getModuleManager().get(InventoryManager.class).isEnabled()
                     && client.getModuleManager().get(InventoryManager.class).wouldKeep(var1.inventorySlots.getSlot(var7).getStack())
                  || !client.getModuleManager().get(InventoryManager.class).isEnabled()
            )) {
            return true;
         }
      }

      return false;
   }

   public static boolean isChestTitle(String var0) {
      if (!titleCheck.getValue()) {
         return true;
      } else {
         return var0 == null ? true : var0.contains("Chest") || var0.contains("LOW") || StringUtils.stripControlCodes(var0).isEmpty();
      }
   }
}
