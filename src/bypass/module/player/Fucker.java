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
   private Listener<MotionEvent> onMotion = var1x -> {
      switch (var1x.getState()) {
         case PRE:
            this.H = null;

            for (int var5 = -5; var5 < 6; var5++) {
               for (int var6 = -5; var6 < 6; var6++) {
                  for (int var7 = 5; var7 > -5; var7--) {
                     double var8 = mc.thePlayer.posX + var5;
                     double var10 = mc.thePlayer.posY + var7;
                     double var12 = mc.thePlayer.posZ + var6;
                     if (this.U(new BlockPos(var8, var10, var12))) {
                        this.H = new BlockPos(var8, var10, var12);
                        Vector3d var14 = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                        Vector3d var15 = new Vector3d(Math.floor(this.H.getX()) + 0.5, Math.floor(this.H.getY()) + 0.5, Math.floor(this.H.getZ()) + 0.5);
                        Rotation var16 = this.N.rotationTo(var15, var14);
                        if (this.rayCast.getValue()) {
                           MovingObjectPosition var17 = GameUtil.A(var16.getYaw(), var16.getPitch(), this.range.getValue().floatValue());
                           if (var17 != null) {
                              if (var17.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                                 return;
                              }

                              if (var17.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                                 && !(mc.theWorld.getBlockState(var17.getBlockPos()).getBlock() instanceof BlockAir)) {
                                 this.H = var17.getBlockPos();
                              }
                           }
                        }

                        this.f = var1x.getLastYaw();
                        this.x = var1x.getLastPitch();
                        var1x.setPitch(var16.getPitch());
                        var1x.setYaw(var16.getYaw());
                        this.W = var1x.getYaw();
                        this.z = var1x.getPitch();
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
   private Listener<RenderLivingEvent> onRenderLiving = var1x -> {
      if (this.H != null && var1x.getEntity() == mc.thePlayer) {
      }
   };
   @EventHandler(RenderEvent.class)
   private Listener<RenderEvent> onRender = var1x -> {
      long var2 = c ^ 101615082007119L;
      if (var1x.getType() == RenderType.WORLD) {
         if (this.H == null) {
            return;
         }

         IBlockState var5 = mc.theWorld.getBlockState(this.H);
         if (var5 == null) {
            return;
         }

         if (var5.getBlock() == null) {
            return;
         }

         GL11.glPushMatrix();
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(2848);
         GL11.glLineWidth(2.0F);
         long var10000 = 5651812435595044275L ^ var2;
         GL11.glDisable(3553);
         GL11.glEnable(2884);
         GL11.glDisable(2929);
         double var6 = mc.getRenderManager().viewerPosX;
         double var8 = mc.getRenderManager().viewerPosY;
         double var10 = mc.getRenderManager().viewerPosZ;
         GL11.glTranslated(-var6, -var8, -var10);
         GL11.glTranslated(this.H.getX(), this.H.getY(), this.H.getZ());
         Color var12 = (Color)this.render.getSetting("Color").getValue();
         GL11.glColor4f(var12.getRed() / 255.0F, var12.getGreen() / 255.0F, var12.getBlue() / 255.0F, var12.getAlpha() / 255.0F);
         AxisAlignedBB var13 = var5.getBlock().getSelectedBoundingBox(mc.theWorld, new BlockPos(0, 0, 0));
         if (var13 != null) {
            BlockOverlay.z(var13);
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

   public Fucker(Category var1) {
      super(var1);
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

   private boolean U(BlockPos var1) {
      return mc.theWorld.getBlockState(var1).getBlock().equals(Blocks.cake) && this.blocks.getSetting("Cake").asBoolean()
         || mc.theWorld.getBlockState(var1).getBlock().equals(Blocks.end_stone) && this.blocks.getSetting("End-Stone").asBoolean()
         || mc.theWorld.getBlockState(var1).getBlock().equals(Blocks.bed) && this.blocks.getSetting("Bed").asBoolean();
   }
}
