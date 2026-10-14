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
   private final Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING && packetEvent.getPacket() instanceof C08PacketPlayerBlockPlacement) {
         C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement = (C08PacketPlayerBlockPlacement)packetEvent.getPacket();
         if (c08PacketPlayerBlockPlacement.getPosition().getX() != C08PacketPlayerBlockPlacement.field_179726_a.getX()
            && c08PacketPlayerBlockPlacement.getPosition().getY() != C08PacketPlayerBlockPlacement.field_179726_a.getY()
            && c08PacketPlayerBlockPlacement.getPosition().getZ() != C08PacketPlayerBlockPlacement.field_179726_a.getZ()) {
            IBlockState iBlockState = mc.theWorld.getBlockState(c08PacketPlayerBlockPlacement.getPosition());
            if (iBlockState != null && iBlockState.getBlock() != null && this.selectTimer.hasElapsed(500.0F)) {
               if (this.selectedBlock == iBlockState.getBlock()) {
                  this.selectedBlock = null;
               } else {
                  this.selectedBlock = iBlockState.getBlock();
               }

               this.selectTimer.reset();
            }
         }
      }
   };
   @EventHandler(value = MotionEvent.class, priority = EventPriority.LOWEST)
   private final Listener<MotionEvent> onMotion = motionEvent -> {
      if (this.delayTicks > 0) {
         this.delayTicks--;
      } else {
         switch (motionEvent.getState()) {
            case PRE:
               if (this.target == null) {
                  this.target = this.scan();
               }

               if (this.target == null) {
                  return;
               }

               Vector3d vector3d = Scaffold.getAimPoint(this.target, this.facing);
               Vector3d vector3d2 = new Vector3d((float)mc.thePlayer.posX, (float)mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), (float)mc.thePlayer.posZ);
               Rotation rotation = this.rotator.rotationTo(vector3d, vector3d2);
               this.unusedLastYaw = motionEvent.getLastYaw();
               this.unusedLastPitch = motionEvent.getLastPitch();
               motionEvent.setYaw(rotation.getYaw());
               motionEvent.setPitch(rotation.getPitch());
               this.unusedSentYaw = motionEvent.getYaw();
               this.unusedSentPitch = motionEvent.getPitch();
               break;
            case POST:
               if (this.target == null) {
                  return;
               }

               Block block = mc.theWorld.getBlockState(this.target).getBlock();
               mc.thePlayer.swingItem();
               if (mc.thePlayer.ticksExisted % 4 == 0) {
                  mc.getSoundHandler()
                     .playSound(
                        new PositionedSoundRecord(
                           new ResourceLocation(block.stepSound.getStepSound()),
                           (block.stepSound.getVolume() + 1.0F) / 8.0F,
                           block.stepSound.getFrequency() * 0.5F,
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

               IBlockState iBlockState = mc.theWorld.getBlockState(this.target);
               this.damage = this.damage + iBlockState.getBlock().getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, this.target);
               mc.theWorld.sendBlockBreakProgress(mc.thePlayer.getEntityId(), this.target, (int)(this.damage * 10.0F) - 1);
               mc.playerController.curBlockDamageMP = this.damage;
               if (this.damage < 1.0F) {
                  return;
               }

               mc.theWorld.playAuxSFX(2001, this.target, Block.getStateId(iBlockState));
               mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.target, this.facing));
               mc.playerController.onPlayerDamageBlock(this.target, EnumFacing.UP);
               this.damage = 0.0F;
               this.target = null;
               this.delayTicks = this.delay.asInt();
         }
      }
   };
   @EventHandler(RenderLivingEvent.class)
   private Listener<RenderLivingEvent> onRenderLiving = renderLivingEvent -> {
      if (this.target != null && renderLivingEvent.getEntity() == mc.thePlayer) {
      }
   };
   private static final long c = 128718734328322L;

   public Nuker(Category category) {
      super(category);
      this.addSettings(this.range, this.flat, this.selective, this.delay);
   }

   @Override
   public void onDisable() {
      this.target = null;
      this.facing = null;
      super.onDisable();
   }

   private BlockPos scan() {
      ArrayList arrayList = new ArrayList();
      double d = this.range.asDouble() + 0.5;

      for (int i = MathHelper.ceiling_double_int(d); i > -1; i--) {
         for (int j = -MathHelper.floor_double(d); j < MathHelper.ceiling_double_int(d); j++) {
            for (int k = -MathHelper.floor_double(d); k < MathHelper.ceiling_double_int(d); k++) {
               BlockPos blockPos = new BlockPos(
                  MathHelper.floor_double(mc.thePlayer.posX) + j,
                  MathHelper.floor_double(mc.thePlayer.posY + 0.02) + i,
                  MathHelper.floor_double(mc.thePlayer.posZ) + k
               );
               Vec3 vec3 = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
               IBlockState iBlockState = mc.theWorld.getBlockState(blockPos);
               if (!(iBlockState.getBlock() instanceof BlockAir)
                  && !(iBlockState.getBlock() instanceof BlockLiquid)
                  && !(iBlockState.getBlock() instanceof BlockTallGrass)
                  && !(iBlockState.getBlock() instanceof BlockFlower)
                  && iBlockState.getBlock() != Blocks.bedrock
                  && (iBlockState.getBlock() == this.selectedBlock || !this.selective.getValue())
                  && !(
                     new Vec3(Math.floor(mc.thePlayer.posX) + 0.5, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), Math.floor(mc.thePlayer.posZ) + 0.5)
                           .distanceTo(vec3)
                        > this.range.asDouble()
                  )
                  && getVisibleFacing(blockPos) != null) {
                  arrayList.add(blockPos);
               }
            }
         }
      }

      if (arrayList.isEmpty()) {
         for (int l = -1; l > -MathHelper.floor_double(d) && !this.flat.asBoolean(); l--) {
            for (int m = -MathHelper.floor_double(d); m < MathHelper.ceiling_double_int(d); m++) {
               for (int n = -MathHelper.floor_double(d); n < MathHelper.ceiling_double_int(d); n++) {
                  BlockPos blockPos2 = new BlockPos(
                     MathHelper.floor_double(mc.thePlayer.posX) + m,
                     MathHelper.floor_double(mc.thePlayer.posY) + l,
                     MathHelper.floor_double(mc.thePlayer.posZ) + n
                  );
                  Vec3 vec32 = new Vec3(blockPos2.getX() + 0.5, blockPos2.getY() + 0.5, blockPos2.getZ() + 0.5);
                  IBlockState iBlockState2 = mc.theWorld.getBlockState(blockPos2);
                  if (!(iBlockState2.getBlock() instanceof BlockAir)
                     && !(iBlockState2.getBlock() instanceof BlockLiquid)
                     && (iBlockState2.getBlock() == this.selectedBlock || !this.selective.getValue())
                     && !(
                        new Vec3(Math.floor(mc.thePlayer.posX) + 0.5, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), Math.floor(mc.thePlayer.posZ) + 0.5)
                              .distanceTo(vec32)
                           > this.range.asDouble()
                     )
                     && getVisibleFacing(blockPos2) != null) {
                     arrayList.add(blockPos2);
                  }
               }
            }
         }
      }

      arrayList.sort((object, object2) -> {
         Vec3 vec33 = new Vec3(((net.minecraft.util.Vec3i)object).getX() + 0.5, ((net.minecraft.util.Vec3i)object).getY() + 0.5, ((net.minecraft.util.Vec3i)object).getZ() + 0.5);
         Vec3 vec34 = new Vec3(((net.minecraft.util.Vec3i)object2).getX() + 0.5, ((net.minecraft.util.Vec3i)object2).getY() + 0.5, ((net.minecraft.util.Vec3i)object2).getZ() + 0.5);
         return mc.thePlayer.getPositionEyes(1.0F).distanceTo(vec33) > mc.thePlayer.getPositionEyes(1.0F).distanceTo(vec34) ? 1 : -1;
      });
      if (!arrayList.isEmpty()) {
         BlockPos blockPos3 = (BlockPos)arrayList.get(0);
         this.facing = getVisibleFacing(blockPos3);
         return (BlockPos)arrayList.get(0);
      } else {
         this.facing = null;
         return null;
      }
   }

   private static boolean isPathClear(Vec3 vec3, Vec3 vec32) {
      Vec3 vec33 = vec32.subtract(vec3);
      byte b = 10;
      double d = vec33.xCoord / b;
      double e = vec33.yCoord / b;
      double d2 = vec33.zCoord / b;
      Vec3 vec34 = vec3;

      for (int i = 0; i < b; i++) {
         vec34 = vec34.addVector(d, e, d2);
         BlockPos blockPos = new BlockPos(vec34);
         IBlockState iBlockState = mc.theWorld.getBlockState(blockPos);
         if (!(iBlockState.getBlock() instanceof BlockLiquid) && !(iBlockState.getBlock() instanceof BlockAir)) {
            AxisAlignedBB axisAlignedBB = iBlockState.getBlock().getCollisionBoundingBox(mc.theWorld, blockPos, iBlockState);
            if (axisAlignedBB == null) {
               axisAlignedBB = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            }

            axisAlignedBB = axisAlignedBB.K(blockPos);
            if (axisAlignedBB.isVecInside(vec34)) {
               return false;
            }
         }
      }

      return true;
   }

   public static EnumFacing getVisibleFacing(BlockPos blockPos) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (isPathClear(mc.thePlayer.getPositionEyes(1.0F), getFaceCenter(blockPos, enumFacing))) {
            return enumFacing;
         }
      }

      return null;
   }

   private static Vec3 getFaceCenter(BlockPos blockPos, EnumFacing enumFacing) {
      Vec3 vec3 = new Vec3(enumFacing.getDirectionVec().getX() / 1.99, enumFacing.getDirectionVec().getY() / 1.99, enumFacing.getDirectionVec().getZ() / 1.99);
      Vec3 vec32 = new Vec3(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);
      return vec32.l(vec3);
   }
}
