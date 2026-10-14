package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.EventPriority;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.render.BlockOpaqueEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class AntiObsidian extends Module {
   private final BooleanSetting antiSuffocate = new BooleanSetting("Anti-Suffocate", "Prevents blocks from suffocating you", true);
   public static Block j;
   @EventHandler(value = UpdateEvent.class, priority = EventPriority.HIGHEST)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      j = i();
      if (j instanceof BlockObsidian) {
         BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
         IBlockState iBlockState = mc.theWorld.getBlockState(blockPos);
         if (mc.playerController.onPlayerDamageBlock(blockPos, EnumFacing.UP) && iBlockState.getBlock() != Blocks.bedrock && iBlockState.getBlock() != Blocks.obsidian) {
            mc.thePlayer.swingItem();
         }
      }
   };
   public static boolean s;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      if (packetEvent.getDirection() == PacketDirection.OUTGOING && packetEvent.getPacket() instanceof C03PacketPlayer) {
         if (this.antiSuffocate.getValue() && (j instanceof BlockObsidian || j instanceof BlockWorkbench || j instanceof BlockFalling) && this.j()) {
            s = true;
            packetEvent.cancel();
         } else {
            s = false;
         }
      }
   };
   @EventHandler(BlockOpaqueEvent.class)
   private final Listener<BlockOpaqueEvent> onBlockOpaque = blockOpaqueEvent -> blockOpaqueEvent.setCancelled(false);
   private static final long c = 120376125722640L;

   public AntiObsidian(Category category) {
      super(category);
      this.setHidden(false);
      this.addSettings(this.antiSuffocate);
   }

   private boolean j() {
      boolean bl = mc.thePlayer.isUsingItem();
      boolean bl2 = mc.thePlayer.motionX != 0.0 || !mc.thePlayer.isCollidedVertically || mc.gameSettings.keyBindJump.pressed || mc.thePlayer.motionZ != 0.0;
      return !bl && !bl2;
   }

   public static Block i() {
      if (mc.thePlayer != null && mc.theWorld != null) {
         BlockPos blockPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
         return mc.theWorld.getBlockState(blockPos).getBlock();
      } else {
         return null;
      }
   }

   public static boolean C() {
      return j instanceof BlockObsidian;
   }
}
