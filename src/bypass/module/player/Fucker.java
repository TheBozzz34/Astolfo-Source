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
   private final Stopwatch h = new Stopwatch();
   private BlockPos H;
   private final Rotator N = new Rotator(false, 0.0F);
   private float W;
   private float f;
   private float z;
   private float x;
   @EventHandler(value = MotionEvent.class, priority = EventPriority.LOW)
   private Listener<MotionEvent> onMotion = motionEvent -> {
      switch (motionEvent.getState()) {
         case PRE:
            this.H = null;

            for (int i = -5; i < 6; i++) {
               for (int j = -5; j < 6; j++) {
                  for (int k = 5; k > -5; k--) {
                     double d = mc.thePlayer.posX + i;
                     double e = mc.thePlayer.posY + k;
                     double d2 = mc.thePlayer.posZ + j;
                     if (this.U(new BlockPos(d, e, d2))) {
                        this.H = new BlockPos(d, e, d2);
                        Vector3d vector3d = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                        Vector3d vector3d2 = new Vector3d(Math.floor(this.H.getX()) + 0.5, Math.floor(this.H.getY()) + 0.5, Math.floor(this.H.getZ()) + 0.5);
                        Rotation rotation = this.N.rotationTo(vector3d2, vector3d);
                        if (this.rayCast.getValue()) {
                           MovingObjectPosition movingObjectPosition = GameUtil.A(rotation.getYaw(), rotation.getPitch(), this.range.getValue().floatValue());
                           if (movingObjectPosition != null) {
                              if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                 return;
                              }

                              if (movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                                 && !(mc.theWorld.getBlockState(movingObjectPosition.getBlockPos()).getBlock() instanceof BlockAir)) {
                                 this.H = movingObjectPosition.getBlockPos();
                              }
                           }
                        }

                        this.f = motionEvent.getLastYaw();
                        this.x = motionEvent.getLastPitch();
                        motionEvent.setPitch(rotation.getPitch());
                        motionEvent.setYaw(rotation.getYaw());
                        this.W = motionEvent.getYaw();
                        this.z = motionEvent.getPitch();
                     }
                  }
               }
            }
            break;
         case POST:
            if (this.H != null && mc.thePlayer.getDistance(this.H.getX(), this.H.getY(), this.H.getZ()) <= this.range.getValue().floatValue()) {
               mc.thePlayer.sendQueue.u(new C0APacketAnimation());
               if (this.h.hasElapsed(100.0F) && !mc.theWorld.isAirBlock(this.H)) {
                  if (mc.theWorld.getBlockState(this.H).getBlock().equals(Blocks.cake)) {
                     mc.playerController
                        .onPlayerRightClick(
                           mc.thePlayer,
                           mc.theWorld,
                           mc.thePlayer.getCurrentEquippedItem(),
                           this.H,
                           EnumFacing.UP,
                           new Vec3(this.H.getX(), this.H.getY(), this.H.getZ())
                        );
                  } else {
                     mc.playerController.onPlayerDamageBlock(this.H, EnumFacing.UP);
                     mc.thePlayer
                        .sendQueue
                        .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, this.H, EnumFacing.DOWN));
                     mc.thePlayer
                        .sendQueue
                        .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.H, EnumFacing.DOWN));
                  }

                  this.h.reset();
               }
            }
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = renderLivingEvent -> {
      if (this.H != null && renderLivingEvent.getEntity() == mc.thePlayer) {
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = renderEvent -> {
      long l = c ^ 101615082007119L;
      if (renderEvent.getType() == RenderType.WORLD) {
         if (this.H == null) {
            return;
         }

         IBlockState iBlockState = mc.theWorld.getBlockState(this.H);
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
         long m = 5651812435595044275L ^ l;
         GL11.glDisable(3553);
         GL11.glEnable(2884);
         GL11.glDisable(2929);
         double d = mc.getRenderManager().viewerPosX;
         double e = mc.getRenderManager().viewerPosY;
         double d2 = mc.getRenderManager().viewerPosZ;
         GL11.glTranslated(-d, -e, -d2);
         GL11.glTranslated(this.H.getX(), this.H.getY(), this.H.getZ());
         Color color = (Color)this.render.getSetting("Color").getValue();
         GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
         AxisAlignedBB axisAlignedBB = iBlockState.getBlock().getSelectedBoundingBox(mc.theWorld, new BlockPos(0, 0, 0));
         if (axisAlignedBB != null) {
            BlockOverlay.z(axisAlignedBB);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glEnable(2929);
         GL11.glEnable(3553);
         GL11.glDisable(3042);
         GL11.glDisable(2848);
         GL11.glPopMatrix();
      }
   };
   private static final long c = 62677715610170L;

   public Fucker(Category category) {
      super(category);
      this.addSettings(this.blocks, this.render, this.range, this.rayCast);
   }

   @Override
   public void onDisable() {
      this.H = null;
      this.h.reset();
      this.W = this.f = 0.0F;
      this.z = this.x = 0.0F;
      super.onDisable();
   }

   private boolean U(BlockPos blockPos) {
      return mc.theWorld.getBlockState(blockPos).getBlock().equals(Blocks.cake) && this.blocks.getSetting("Cake").asBoolean()
         || mc.theWorld.getBlockState(blockPos).getBlock().equals(Blocks.end_stone) && this.blocks.getSetting("End-Stone").asBoolean()
         || mc.theWorld.getBlockState(blockPos).getBlock().equals(Blocks.bed) && this.blocks.getSetting("Bed").asBoolean();
   }
}
