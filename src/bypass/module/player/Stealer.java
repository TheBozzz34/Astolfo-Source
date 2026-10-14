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
   private Listener<PacketEvent> onPacket = packetEvent -> {
      long l = c ^ 128838197311750L;
      long m = l ^ 90164795600890L;
      if (packetEvent.getDirection() == PacketDirection.OUTGOING) {
         if (packetEvent.getPacket() instanceof C0EPacketClickWindow) {
            this.clickTimer.reset();
         }

         if (packetEvent.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement = (C08PacketPlayerBlockPlacement)packetEvent.getPacket();
            if (c08PacketPlayerBlockPlacement.getPlacedBlockDirection() != 255) {
               IBlockState iBlockState = mc.theWorld.getBlockState(c08PacketPlayerBlockPlacement.getPosition());
               if (iBlockState != null && iBlockState.getBlock() instanceof BlockChest) {
                  this.closeTimer.reset();
                  this.clickTimer.reset();
               }
            }
         }

         if (packetEvent.getPacket() instanceof C0DPacketCloseWindow) {
            this.resetSilentChest();
         }
      } else {
         if (packetEvent.getPacket() instanceof S2DPacketOpenWindow) {
            S2DPacketOpenWindow s2DPacketOpenWindow = (S2DPacketOpenWindow)packetEvent.getPacket();
            if (s2DPacketOpenWindow.getWindowTitle() != null && mc.thePlayer.inventory != null) {
               this.resetSilentChest();
               this.silentChest = new GuiChest(mc.thePlayer.inventory, new ContainerLocalMenu(s2DPacketOpenWindow.getGuiId(), s2DPacketOpenWindow.getWindowTitle(), s2DPacketOpenWindow.getSlotCount()));
               this.closeTimer.reset();
               if (this.silent.getValue() && this.silentChest.lowerChestInventory != null && this.silentChest.lowerChestInventory.getDisplayName() != null) {
                  if (isChestTitle(this.silentChest.lowerChestInventory.getDisplayName().getUnformattedText())) {
                     packetEvent.cancel();
                  } else {
                     this.resetSilentChest();
                  }
               }
            }
         }

         if (this.silent.getValue() && this.silentChest != null && packetEvent.getPacket() instanceof S30PacketWindowItems) {
            S30PacketWindowItems s30PacketWindowItems = (S30PacketWindowItems)packetEvent.getPacket();
            if (s30PacketWindowItems.getItemStacks() != null) {
               short s = 0;

               for (int i = 0; i < s30PacketWindowItems.getItemStacks().length - 36; i++) {
                  try {
                     ItemStack itemStack = s30PacketWindowItems.getItemStacks()[i];
                     if (itemStack != null
                        && this.silentChest.lowerChestInventory != null
                        && this.silentChest.lowerChestInventory.getDisplayName() != null
                        && isChestTitle(this.silentChest.lowerChestInventory.getDisplayName().getUnformattedText())
                        && (
                           client.getModuleManager().get(InventoryManager.class).isEnabled()
                                 && client.getModuleManager().get(InventoryManager.class).wouldKeep(itemStack)
                              || !client.getModuleManager().get(InventoryManager.class).isEnabled()
                        )) {
                        this.pendingClicks.offer(new C0EPacketClickWindow(s30PacketWindowItems.func_148911_c(), i, 0, 1, itemStack, s++));
                     }
                  } catch (Exception exception) {
                  }
               }

               if (this.silentChest.inventorySlots != null) {
                  this.silentChest.inventorySlots.putStacksInSlots(s30PacketWindowItems.getItemStacks());
               }

               this.windowId = s30PacketWindowItems.func_148911_c();
            }
         }

         if (packetEvent.getPacket() instanceof S2EPacketCloseWindow) {
            this.resetSilentChest();
         }
      }
   };
   private boolean stealing;
   @EventHandler(MotionEvent.class)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      boolean bl = this.aura.getSetting("Enabled").asBoolean();
      this.stealing = false;
      if (!this.silent.getValue()) {
         if (mc.currentScreen instanceof GuiContainer) {
            GuiContainer guiContainer = (GuiContainer)mc.currentScreen;
            if (guiContainer instanceof GuiChest) {
               GuiChest guiChest = (GuiChest)mc.currentScreen;
               if (isChestTitle(guiChest.lowerChestInventory.getDisplayName().getUnformattedText())) {
                  if (this.moveMouse.getValue()) {
                     Mouse.setGrabbed(true);
                     mc.inGameHasFocus = true;
                  }

                  this.stealing = true;
                  if (this.reverse.getValue()) {
                     int i = guiChest.lowerChestInventory.getSizeInventory();

                     while (i-- > 0) {
                        this.stealSlot(guiContainer, guiChest, i);
                     }
                  } else {
                     for (int j = 0; j < guiChest.lowerChestInventory.getSizeInventory(); j++) {
                        this.stealSlot(guiContainer, guiChest, j);
                     }
                  }

                  if (this.close.getValue() && this.closeTimer.hasElapsed(this.closeDelay.getValue().intValue()) && !this.hasItemsLeft(guiChest)) {
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
                     C0EPacketClickWindow c0EPacketClickWindow = this.pendingClicks.poll();
                     if (c0EPacketClickWindow != null) {
                        this.stealing = true;
                        mc.thePlayer.sendQueue.addToSendQueue(c0EPacketClickWindow);
                        this.silentChest.inventorySlots.slotClick(c0EPacketClickWindow.getSlotId(), c0EPacketClickWindow.getUsedButton(), c0EPacketClickWindow.getMode(), mc.thePlayer);
                     }
                  }
               } else if (this.closeTimer.hasElapsed(this.closeDelay.getValue().intValue())) {
                  mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(this.windowId));
                  this.closeTimer.reset();
               }
            }
         } catch (Exception exception) {
         }
      }

      if (mc.currentScreen instanceof GuiChest || this.silentChest != null || mc.currentScreen instanceof GuiInventory || !bl) {
         this.auraTarget = null;
      } else if (this.aura.getSetting("Assist").asBoolean() && !Mouse.isButtonDown(1)) {
         this.auraTarget = null;
      } else {
         boolean bl2 = this.aura.getSetting("Lock-View").asBoolean();
         switch (motionEvent.getState()) {
            case PRE:
               this.auraTarget = null;

               for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
                  if (this.canOpen(tileEntity)) {
                     this.auraTarget = tileEntity;
                     Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                     Vector3d vector3d2 = new Vector3d(
                        this.auraTarget.getPos().getX() + 0.5, this.auraTarget.getPos().getY() + 0.5, this.auraTarget.getPos().getZ() + 0.5
                     );
                     Rotation rotation = this.rotator.rotationTo(vector3d2, vector3d);
                     float f = rotation.getYaw();
                     float g = rotation.getPitch();
                     float h = MathHelper.wrapAngleTo180_float(f - mc.thePlayer.rotationYaw);
                     f = mc.thePlayer.rotationYaw + h;
                     this.lastYaw = motionEvent.getLastYaw();
                     this.lastPitch = motionEvent.getLastPitch();
                     if (!GameUtil.isOnHypixel()) {
                        motionEvent.setYaw(bl2 ? (mc.thePlayer.rotationYaw = f) : f);
                        motionEvent.setPitch(bl2 ? (mc.thePlayer.rotationPitch = g) : g);
                     }

                     this.auraYaw = motionEvent.getYaw();
                     this.auraPitch = motionEvent.getPitch();
                  }
               }
               break;
            case POST:
               if (this.auraTarget != null && this.clickTimer.hasElapsed(this.aura.getSetting("Delay").asInt())) {
                  MovingObjectPosition movingObjectPosition = GameUtil.A(this.auraYaw, this.auraPitch, mc.playerController.getBlockReachDistance());
                  if (this.isChestHit(movingObjectPosition)) {
                     if (mc.playerController
                        .onPlayerRightClick(
                           mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem(), movingObjectPosition.getBlockPos(), movingObjectPosition.sideHit, movingObjectPosition.hitVec
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
   private Listener<MoveEvent> onMove = moveEvent -> {
      if (this.stealing && this.autoStop.getValue() && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround) {
         moveEvent.setX(mc.thePlayer.motionX = 0.0);
         moveEvent.setY(mc.thePlayer.motionY = 0.0);
         moveEvent.setZ(mc.thePlayer.motionZ = 0.0);
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = renderLivingEvent -> {
      if (this.auraTarget != null && renderLivingEvent.getEntity() == mc.thePlayer) {
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private final Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> {
      this.openedChests.clear();
      this.resetSilentChest();
   };
   private static final long c = 17985607461711L;

   public Stealer(Category category) {
      super(category);
      this.addSettings(this.aura, this.delay, this.closeDelay, this.close, this.reverse, titleCheck, this.silent, this.moveMouse, this.autoStop);
   }

   private void stealSlot(GuiContainer guiContainer, GuiChest guiChest, int i) {
      long l = c ^ 10525064128907L;
      long m = l ^ 51397149228919L;
      ItemStack itemStack = guiChest.lowerChestInventory.getStackInSlot(i);
      if (itemStack != null && itemStack.getItem() != null) {
         if ((
               client.getModuleManager().get(InventoryManager.class).isEnabled() && client.getModuleManager().get(InventoryManager.class).wouldKeep(itemStack)
                  || !client.getModuleManager().get(InventoryManager.class).isEnabled()
                  || this.silent.getValue()
            )
            && this.clickTimer.hasElapsed(this.delay.getValue().intValue())) {
            mc.playerController.windowClick(this.silent.getValue() ? this.windowId : guiContainer.inventorySlots.windowId, i, 0, 1, mc.thePlayer);
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

   private boolean isChestHit(MovingObjectPosition movingObjectPosition) {
      if (movingObjectPosition != null && movingObjectPosition.getBlockPos() != null) {
         BlockPos blockPos = movingObjectPosition.getBlockPos();
         Block block = mc.theWorld.getBlockState(blockPos).getBlock();
         return (block.getMaterial() != Material.air || block.getMaterial() != Material.water || block.getMaterial() != Material.lava)
            && block instanceof BlockChest;
      } else {
         return false;
      }
   }

   private boolean canOpen(TileEntity tileEntity) {
      return !(tileEntity instanceof TileEntityChest)
         ? false
         : !this.openedChests.contains(tileEntity.getPos())
            && mc.thePlayer.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ())
               < mc.playerController.getBlockReachDistance();
   }

   private boolean hasItemsLeft(GuiChest guiChest) {
      long l = c ^ 84239377091547L;
      long m = l ^ 118411581644071L;

      for (int i = 0; i < guiChest.lowerChestInventory.getSizeInventory(); i++) {
         if (isChestTitle(guiChest.lowerChestInventory.getDisplayName().getFormattedText())
            && guiChest.inventorySlots.getSlot(i).getHasStack()
            && (
               client.getModuleManager().get(InventoryManager.class).isEnabled()
                     && client.getModuleManager().get(InventoryManager.class).wouldKeep(guiChest.inventorySlots.getSlot(i).getStack())
                  || !client.getModuleManager().get(InventoryManager.class).isEnabled()
            )) {
            return true;
         }
      }

      return false;
   }

   public static boolean isChestTitle(String string) {
      if (!titleCheck.getValue()) {
         return true;
      } else {
         return string == null ? true : string.contains("Chest") || string.contains("LOW") || StringUtils.stripControlCodes(string).isEmpty();
      }
   }
}
