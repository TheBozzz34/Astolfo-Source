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
   private Listener<UpdateEvent> onUpdate = var0 -> {
      j = i();
      if (j instanceof BlockObsidian) {
         BlockPos var4 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
         IBlockState var5 = mc.theWorld.getBlockState(var4);
         if (mc.playerController.onPlayerDamageBlock(var4, EnumFacing.UP) && var5.getBlock() != Blocks.bedrock && var5.getBlock() != Blocks.obsidian) {
            mc.thePlayer.swingItem();
         }
      }
   };
   public static boolean s;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.OUTGOING && var1x.getPacket() instanceof C03PacketPlayer) {
         if (this.antiSuffocate.getValue() && (j instanceof BlockObsidian || j instanceof BlockWorkbench || j instanceof BlockFalling) && this.j()) {
            s = true;
            var1x.cancel();
         } else {
            s = false;
         }
      }
   };
   @EventHandler(BlockOpaqueEvent.class)
   private final Listener<BlockOpaqueEvent> onBlockOpaque = var0 -> var0.setCancelled(false);
   private static final long c = 120376125722640L;

   public AntiObsidian(Category var1) {
      super(var1);
      this.setHidden(false);
      this.addSettings(this.antiSuffocate);
   }

   private boolean j() {
      boolean var4 = mc.thePlayer.isUsingItem();
      boolean var5 = mc.thePlayer.motionX != 0.0 || !mc.thePlayer.isCollidedVertically || mc.gameSettings.keyBindJump.pressed || mc.thePlayer.motionZ != 0.0;
      return !var4 && !var5;
   }

   public static Block i() {
      if (mc.thePlayer != null && mc.theWorld != null) {
         BlockPos var3 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
         return mc.theWorld.getBlockState(var3).getBlock();
      } else {
         return null;
      }
   }

   public static boolean C() {
      return j instanceof BlockObsidian;
   }
}
