package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.player.MotionEvent;
import bypass.event.render.RenderEvent;
import bypass.event.render.RenderLivingEvent;
import bypass.event.render.RenderType;
import bypass.module.Category;
import bypass.module.Module;
import bypass.module.visuals.BlockOverlay;
import bypass.setting.BooleanSetting;
import bypass.setting.ColorSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.util.GameUtil;
import bypass.util.Rotation;
import bypass.util.Rotator;
import bypass.util.Stopwatch;
import java.awt.Color;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import org.lwjgl.opengl.GL11;

public final class Fucker extends Module {
   private final MapSetting blocks = new MapSettingBuilder("Blocks")
      .ordered()
      .add("Cake", new BooleanSetting("Cake", "Activates Cake", true))
      .add("End-Stone", new BooleanSetting("End-Stone", "Activates End-Stone", true))
      .add("Bed", new BooleanSetting("Bed", "Activates Bed", true))
      .build();
   private final NumberSetting<Float> range = new NumberSetting<>("Range", "Range of block destroy", 5.0F, 4.0F, 6.0F);
   private final BooleanSetting rayCast = new BooleanSetting("Ray-Cast", "Ray casts", true);
   private final MapSetting render = new MapSettingBuilder("Render")
      .ordered()
      .description("Delay stuff")
      .add("Enable", new BooleanSetting("Enable", "Enable render", true))
      .add("Color", new ColorSetting("Color", "color", client.getColorSetting().getValue()))
      .build();
   private final Stopwatch breakTimer = new Stopwatch();
   private BlockPos targetBlock;
   private final Rotator rotator = new Rotator(false, 0.0F);
   private float targetYaw;
   private float prevYaw;
   private float targetPitch;
   private float prevPitch;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.LOW)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      switch (motionEvent.getState()) {
         case PRE:
            this.targetBlock = null;

            for (int i = -5; i < 6; i++) {
               for (int j = -5; j < 6; j++) {
                  for (int k = 5; k > -5; k--) {
                     double d = mc.thePlayer.posX + i;
                     double e = mc.thePlayer.posY + k;
                     double d2 = mc.thePlayer.posZ + j;
                     if (this.isTargetBlock(new BlockPos(d, e, d2))) {
                        this.targetBlock = new BlockPos(d, e, d2);
                        Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                        Vector3d vector3d2 = new Vector3d(
                           Math.floor(this.targetBlock.getX()) + 0.5, Math.floor(this.targetBlock.getY()) + 0.5, Math.floor(this.targetBlock.getZ()) + 0.5
                        );
                        Rotation rotation = this.rotator.rotationTo(vector3d2, vector3d);
                        if (this.rayCast.getValue()) {
                           MovingObjectPosition movingObjectPosition = GameUtil.rayTrace(rotation.getYaw(), rotation.getPitch(), this.range.getValue().floatValue());
                           if (movingObjectPosition != null) {
                              if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                 return;
                              }

                              if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                                 && !(mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock() instanceof BlockAir)) {
                                 this.targetBlock = movingObjectPosition.getBlockPos();
                              }
                           }
                        }

                        this.prevYaw = motionEvent.getLastYaw();
                        this.prevPitch = motionEvent.getLastPitch();
                        motionEvent.setPitch(rotation.getPitch());
                        motionEvent.setYaw(rotation.getYaw());
                        this.targetYaw = motionEvent.getYaw();
                        this.targetPitch = motionEvent.getPitch();
                     }
                  }
               }
            }
            break;
         case POST:
            if (this.targetBlock != null
               && mc.thePlayer.getDistance(this.targetBlock.getX(), this.targetBlock.getY(), this.targetBlock.getZ()) <= this.range.getValue().floatValue()) {
               mc.thePlayer.sendQueue.u(new C0APacketAnimation());
               if (this.breakTimer.hasElapsed(100.0F) && !mc.theWorld.isAirBlock(this.targetBlock)) {
                  if (mc.theWorld.getBlockState(this.targetBlock).getBlock().equals(Blocks.cake)) {
                     mc.playerController
                        .onPlayerRightClick(
                           mc.thePlayer,
                           mc.theWorld,
                           mc.thePlayer.getCurrentEquippedItem(),
                           this.targetBlock,
                           EnumFacing.UP,
                           new Vec3(this.targetBlock.getX(), this.targetBlock.getY(), this.targetBlock.getZ())
                        );
                  } else {
                     mc.playerController.onPlayerDamageBlock(this.targetBlock, EnumFacing.UP);
                     mc.thePlayer
                        .sendQueue
                        .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, this.targetBlock, EnumFacing.DOWN));
                     mc.thePlayer
                        .sendQueue
                        .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.targetBlock, EnumFacing.DOWN));
                  }

                  this.breakTimer.reset();
               }
            }
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = renderLivingEvent -> {
      if (this.targetBlock != null && renderLivingEvent.getEntity() == mc.thePlayer) {
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = renderEvent -> {
      if (renderEvent.getType() == RenderType.WORLD) {
         if (this.targetBlock == null) {
            return;
         }

         IBlockState iBlockState = mc.theWorld.getBlockState(this.targetBlock);
         if (iBlockState == null) {
            return;
         }

         if (iBlockState.getBlock() == null) {
            return;
         }

         GL11.glPushMatrix();
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(2848);
         GL11.glLineWidth(2.0F);
         GL11.glDisable(3553);
         GL11.glEnable(2884);
         GL11.glDisable(2929);
         double d = mc.getRenderManager().viewerPosX;
         double e = mc.getRenderManager().viewerPosY;
         double d2 = mc.getRenderManager().viewerPosZ;
         GL11.glTranslated(-d, -e, -d2);
         GL11.glTranslated(this.targetBlock.getX(), this.targetBlock.getY(), this.targetBlock.getZ());
         Color color = (Color)this.render.getSetting("Color").getValue();
         GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
         AxisAlignedBB axisAlignedBB = iBlockState.getBlock().getSelectedBoundingBox(mc.theWorld, new BlockPos(0, 0, 0));
         if (axisAlignedBB != null) {
            BlockOverlay.drawFilledBox(axisAlignedBB);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(2929);
         GL11.glEnable(3553);
         GL11.glDisable(3042);
         GL11.glDisable(2848);
         GL11.glPopMatrix();
      }
   };

   public Fucker(Category category) {
      super(category);
      this.addSettings(this.blocks, this.render, this.range, this.rayCast);
   }

   @Override
   public void onDisable() {
      this.targetBlock = null;
      this.breakTimer.reset();
      this.targetYaw = this.prevYaw = 0.0F;
      this.targetPitch = this.prevPitch = 0.0F;
      super.onDisable();
   }

   private boolean isTargetBlock(BlockPos blockPos) {
      return mc.theWorld.getBlockState(blockPos).getBlock().equals(Blocks.cake) && this.blocks.getSetting("Cake").asBoolean()
         || mc.theWorld.getBlockState(blockPos).getBlock().equals(Blocks.end_stone) && this.blocks.getSetting("End-Stone").asBoolean()
         || mc.theWorld.getBlockState(blockPos).getBlock().equals(Blocks.bed) && this.blocks.getSetting("Bed").asBoolean();
   }
}
