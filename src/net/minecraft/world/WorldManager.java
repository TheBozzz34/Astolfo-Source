package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class WorldManager implements IWorldAccess {
   private MinecraftServer mcServer;
   private WorldServer theWorldServer;
   private static final long a = 47432873660145L;

   public WorldManager(MinecraftServer var1, WorldServer var2) {
      this.mcServer = var1;
      this.theWorldServer = var2;
   }

   @Override
   public void spawnParticle(int var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
   }

   @Override
   public void onEntityAdded(Entity var1) {
      this.theWorldServer.getEntityTracker().trackEntity(var1);
   }

   @Override
   public void onEntityRemoved(Entity var1) {
      this.theWorldServer.getEntityTracker().untrackEntity(var1);
      this.theWorldServer.getScoreboard().func_181140_a(var1);
   }

   @Override
   public void playSound(String var1, double var2, double var4, double var6, float var8, float var9) {
      this.mcServer
         .getConfigurationManager()
         .sendToAllNear(
            var2,
            var4,
            var6,
            var8 > 1.0F ? 16.0F * var8 : 16.0,
            this.theWorldServer.provider.getDimensionId(),
            new S29PacketSoundEffect(var1, var2, var4, var6, var8, var9)
         );
   }

   @Override
   public void playSoundToNearExcept(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10) {
      this.mcServer
         .getConfigurationManager()
         .sendToAllNearExcept(
            var1,
            var3,
            var5,
            var7,
            var9 > 1.0F ? 16.0F * var9 : 16.0,
            this.theWorldServer.provider.getDimensionId(),
            new S29PacketSoundEffect(var2, var3, var5, var7, var9, var10)
         );
   }

   @Override
   public void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   @Override
   public void markBlockForUpdate(BlockPos var1) {
      this.theWorldServer.getPlayerManager().markBlockForUpdate(var1);
   }

   @Override
   public void notifyLightSet(BlockPos var1) {
   }

   @Override
   public void playRecord(String var1, BlockPos var2) {
   }

   @Override
   public void playAuxSFX(EntityPlayer var1, int var2, BlockPos var3, int var4) {
      this.mcServer
         .getConfigurationManager()
         .sendToAllNearExcept(
            var1, var3.getX(), var3.getY(), var3.getZ(), 64.0, this.theWorldServer.provider.getDimensionId(), new S28PacketEffect(var2, var3, var4, false)
         );
   }

   @Override
   public void broadcastSound(int var1, BlockPos var2, int var3) {
      this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S28PacketEffect(var1, var2, var3, true));
   }

   @Override
   public void sendBlockBreakProgress(int var1, BlockPos var2, int var3) {
      for (EntityPlayerMP var8 : this.mcServer.getConfigurationManager().getPlayerList()) {
         if (var8 != null && var8.worldObj == this.theWorldServer && var8.getEntityId() != var1) {
            double var9 = var2.getX() - var8.posX;
            double var11 = var2.getY() - var8.posY;
            double var13 = var2.getZ() - var8.posZ;
            if (var9 * var9 + var11 * var11 + var13 * var13 < 1024.0) {
               var8.playerNetServerHandler.sendPacket(new S25PacketBlockBreakAnim(var1, var2, var3));
            }
         }
      }
   }
}
