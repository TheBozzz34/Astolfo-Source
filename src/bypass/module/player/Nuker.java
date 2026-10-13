package bypass.module.player;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.MotionEvent;
import bypass.event.render.RenderLivingEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.util.Rotation;
import bypass.util.Rotator;
import bypass.util.Stopwatch;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;

public class Nuker extends Module {
   private final Rotator rotator = new Rotator(false, 15.0F);
   private final NumberSetting<Double> range = new NumberSetting<>("Range", "Distance of blocks", 4.5, 3.0, 6.0);
   private final BooleanSetting flat = new BooleanSetting("Flat", "Doesnt mine under you", true);
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Ticks to mine a new block", 1, 0, 50, " ticks");
   private final BooleanSetting selective = new BooleanSetting("Selective", "Select blocks right clicking on them", true);
   private Block selectedBlock;
   private EnumFacing facing;
   private BlockPos target;
   private int delayTicks = 0;
   private float damage = 0.0F;
   private float unusedSentYaw;
   private float unusedLastYaw;
   private float unusedSentPitch;
   private float unusedLastPitch;
   private final Stopwatch selectTimer = new Stopwatch();
   @EventHandler(PacketEvent.class)
   private final Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING && var1x.getPacket() instanceof C08PacketPlayerBlockPlacement) {
         C08PacketPlayerBlockPlacement var5 = (C08PacketPlayerBlockPlacement)var1x.getPacket();
         if (var5.getPosition().getX() != C08PacketPlayerBlockPlacement.field_179726_a.getX()
            && var5.getPosition().getY() != C08PacketPlayerBlockPlacement.field_179726_a.getY()
            && var5.getPosition().getZ() != C08PacketPlayerBlockPlacement.field_179726_a.getZ()) {
            IBlockState var6 = mc.theWorld.getBlockState(var5.getPosition());
            if (var6 != null && var6.getBlock() != null && this.selectTimer.hasElapsed(500.0F)) {
               if (this.selectedBlock == var6.getBlock()) {
                  this.selectedBlock = null;
               } else {
                  this.selectedBlock = var6.getBlock();
               }

               this.selectTimer.reset();
            }
         }
      }
   };
   @EventHandler(value = MotionEvent.class, priority = EventPriority.LOWEST)
   private final Listener<MotionEvent> onMotion = var1x -> {
      if (this.delayTicks > 0) {
         this.delayTicks--;
      } else {
         switch (var1x.getState()) {
            case PRE:
               if (this.target == null) {
                  this.target = this.scan();
               }

               if (this.target == null) {
                  return;
               }

               Vector3d var5 = Scaffold.getAimPoint(this.target, this.facing);
               Vector3d var6 = new Vector3d((float)mc.thePlayer.posX, (float)mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), (float)mc.thePlayer.posZ);
               Rotation var7 = this.rotator.rotationTo(var5, var6);
               this.unusedLastYaw = var1x.getLastYaw();
               this.unusedLastPitch = var1x.getLastPitch();
               var1x.setYaw(var7.getYaw());
               var1x.setPitch(var7.getPitch());
               this.unusedSentYaw = var1x.getYaw();
               this.unusedSentPitch = var1x.getPitch();
               break;
            case POST:
               if (this.target == null) {
                  return;
               }

               Block var8 = mc.theWorld.getBlockState(this.target).getBlock();
               mc.thePlayer.swingItem();
               if (mc.thePlayer.ticksExisted % 4 == 0) {
                  mc.getSoundHandler()
                     .playSound(
                        new PositionedSoundRecord(
                           new ResourceLocation(var8.stepSound.getStepSound()),
                           (var8.stepSound.getVolume() + 1.0F) / 8.0F,
                           var8.stepSound.getFrequency() * 0.5F,
                           this.target.getX() + 0.5F,
                           this.target.getY() + 0.5F,
                           this.target.getZ() + 0.5F
                        )
                     );
               }

               if (this.damage == 0.0F) {
                  mc.thePlayer
                     .sendQueue
                     .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, this.target, this.facing));
               }

               if (mc.playerController.isInCreativeMode()) {
                  this.damage = 1.0F;
               }

               IBlockState var9 = mc.theWorld.getBlockState(this.target);
               this.damage = this.damage + var9.getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.target);
               mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), this.target, (int)(this.damage * 10.0F) - 1);
               mc.playerController.curBlockDamageMP = this.damage;
               if (this.damage < 1.0F) {
                  return;
               }

               mc.theWorld.playAuxSFX(2001, this.target, Block.getStateId(var9));
               mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.target, this.facing));
               mc.playerController.onPlayerDamageBlock(this.target, EnumFacing.UP);
               this.damage = 0.0F;
               this.target = null;
               this.delayTicks = this.delay.asInt();
         }
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = var1x -> {
      if (this.target != null && var1x.getEntity() == mc.thePlayer) {
      }
   };
   private static final long c = 128718734328322L;

   public Nuker(Category var1) {
      super(var1);
      this.addSettings(this.range, this.flat, this.selective, this.delay);
   }

   @Override
   public void onDisable() {
      this.target = null;
      this.facing = null;
      super.onDisable();
   }

   private BlockPos scan() {
      ArrayList var4 = new ArrayList();
      double var5 = this.range.asDouble() + 0.5;

      for (int var7 = MathHelper.ceiling_double_int(var5); var7 > -1; var7--) {
         for (int var8 = -MathHelper.floor_double(var5); var8 < MathHelper.ceiling_double_int(var5); var8++) {
            for (int var9 = -MathHelper.floor_double(var5); var9 < MathHelper.ceiling_double_int(var5); var9++) {
               BlockPos var10 = new BlockPos(
                  MathHelper.floor_double(mc.thePlayer.posX) + var8,
                  MathHelper.floor_double(mc.thePlayer.posY + 0.02) + var7,
                  MathHelper.floor_double(mc.thePlayer.posZ) + var9
               );
               Vec3 var11 = new Vec3(var10.getX() + 0.5, var10.getY() + 0.5, var10.getZ() + 0.5);
               IBlockState var12 = mc.theWorld.getBlockState(var10);
               if (!(var12.getBlock() instanceof BlockAir)
                  && !(var12.getBlock() instanceof BlockLiquid)
                  && !(var12.getBlock() instanceof BlockTallGrass)
                  && !(var12.getBlock() instanceof BlockFlower)
                  && var12.getBlock() != Blocks.bedrock
                  && (var12.getBlock() == this.selectedBlock || !this.selective.getValue())
                  && !(
                     new Vec3(Math.floor(mc.thePlayer.posX) + 0.5, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), Math.floor(mc.thePlayer.posZ) + 0.5)
                           .distanceTo(var11)
                        > this.range.asDouble()
                  )
                  && getVisibleFacing(var10) != null) {
                  var4.add(var10);
               }
            }
         }
      }

      if (var4.isEmpty()) {
         for (int var13 = -1; var13 > -MathHelper.floor_double(var5) && !this.flat.asBoolean(); var13--) {
            for (int var15 = -MathHelper.floor_double(var5); var15 < MathHelper.ceiling_double_int(var5); var15++) {
               for (int var16 = -MathHelper.floor_double(var5); var16 < MathHelper.ceiling_double_int(var5); var16++) {
                  BlockPos var17 = new BlockPos(
                     MathHelper.floor_double(mc.thePlayer.posX) + var15,
                     MathHelper.floor_double(mc.thePlayer.posY) + var13,
                     MathHelper.floor_double(mc.thePlayer.posZ) + var16
                  );
                  Vec3 var18 = new Vec3(var17.getX() + 0.5, var17.getY() + 0.5, var17.getZ() + 0.5);
                  IBlockState var19 = mc.theWorld.getBlockState(var17);
                  if (!(var19.getBlock() instanceof BlockAir)
                     && !(var19.getBlock() instanceof BlockLiquid)
                     && (var19.getBlock() == this.selectedBlock || !this.selective.getValue())
                     && !(
                        new Vec3(Math.floor(mc.thePlayer.posX) + 0.5, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), Math.floor(mc.thePlayer.posZ) + 0.5)
                              .distanceTo(var18)
                           > this.range.asDouble()
                     )
                     && getVisibleFacing(var17) != null) {
                     var4.add(var17);
                  }
               }
            }
         }
      }

      var4.sort((var0, var1) -> {
         Vec3 var5x = new Vec3(((net.minecraft.util.Vec3i)var0).getX() + 0.5, ((net.minecraft.util.Vec3i)var0).getY() + 0.5, ((net.minecraft.util.Vec3i)var0).getZ() + 0.5);
         Vec3 var6 = new Vec3(((net.minecraft.util.Vec3i)var1).getX() + 0.5, ((net.minecraft.util.Vec3i)var1).getY() + 0.5, ((net.minecraft.util.Vec3i)var1).getZ() + 0.5);
         return mc.thePlayer.getPositionEyes(1.0F).distanceTo(var5x) > mc.thePlayer.getPositionEyes(1.0F).distanceTo(var6) ? 1 : -1;
      });
      if (!var4.isEmpty()) {
         BlockPos var14 = (BlockPos)var4.get(0);
         this.facing = getVisibleFacing(var14);
         return (BlockPos)var4.get(0);
      } else {
         this.facing = null;
         return null;
      }
   }

   private static boolean isPathClear(Vec3 var0, Vec3 var1) {
      Vec3 var5 = var1.subtract(var0);
      byte var6 = 10;
      double var7 = var5.xCoord / var6;
      double var9 = var5.yCoord / var6;
      double var11 = var5.zCoord / var6;
      Vec3 var13 = var0;

      for (int var14 = 0; var14 < var6; var14++) {
         var13 = var13.addVector(var7, var9, var11);
         BlockPos var15 = new BlockPos(var13);
         IBlockState var16 = mc.theWorld.getBlockState(var15);
         if (!(var16.getBlock() instanceof BlockLiquid) && !(var16.getBlock() instanceof BlockAir)) {
            AxisAlignedBB var17 = var16.getBlock().getCollisionBoundingBox(mc.theWorld, var15, var16);
            if (var17 == null) {
               var17 = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            }

            var17 = var17.K(var15);
            if (var17.isVecInside(var13)) {
               return false;
            }
         }
      }

      return true;
   }

   public static EnumFacing getVisibleFacing(BlockPos var0) {
      for (EnumFacing var7 : EnumFacing.values()) {
         if (isPathClear(mc.thePlayer.getPositionEyes(1.0F), getFaceCenter(var0, var7))) {
            return var7;
         }
      }

      return null;
   }

   private static Vec3 getFaceCenter(BlockPos var0, EnumFacing var1) {
      Vec3 var2 = new Vec3(var1.getDirectionVec().getX() / 1.99, var1.getDirectionVec().getY() / 1.99, var1.getDirectionVec().getZ() / 1.99);
      Vec3 var3 = new Vec3(var0.getX() + 0.5, var0.getY() + 0.5, var0.getZ() + 0.5);
      return var3.l(var2);
   }
}
