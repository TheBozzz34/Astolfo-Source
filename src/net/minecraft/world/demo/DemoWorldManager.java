package net.minecraft.world.demo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DemoWorldManager extends ItemInWorldManager {
   private boolean field_73105_c;
   private boolean demoTimeExpired;
   private int field_73104_e;
   private int field_73102_f;
   private static final long b = 134309260531170L;

   public DemoWorldManager(World var1) {
      super(var1);
   }

   @Override
   public void updateBlockRemoving() {
      super.updateBlockRemoving();
      this.field_73102_f++;
      long var5 = this.theWorld.getTotalWorldTime();
      long var7 = var5 / 24000L + 1L;
      if (!this.field_73105_c && this.field_73102_f > 20) {
         this.field_73105_c = true;
         this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 0.0F));
      }

      this.demoTimeExpired = var5 > 120500L;
      if (this.demoTimeExpired) {
         this.field_73104_e++;
      }

      if (var5 % 24000L == 500L) {
         if (var7 <= 6L) {
            this.thisPlayerMP.addChatMessage(new ChatComponentTranslation("demo.day." + var7));
         }
      } else if (var7 == 1L) {
         if (var5 == 100L) {
            this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 101.0F));
         } else if (var5 == 175L) {
            this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 102.0F));
         } else if (var5 == 250L) {
            this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 103.0F));
         }
      } else if (var7 == 5L && var5 % 24000L == 22000L) {
         this.thisPlayerMP.addChatMessage(new ChatComponentTranslation("demo.day.warning"));
      }
   }

   private void sendDemoReminder() {
      if (this.field_73104_e > 100) {
         this.thisPlayerMP.addChatMessage(new ChatComponentTranslation("demo.reminder"));
         this.field_73104_e = 0;
      }
   }

   @Override
   public void onBlockClicked(BlockPos var1, EnumFacing var2) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
      } else {
         super.onBlockClicked(var1, var2);
      }
   }

   @Override
   public void blockRemoving(BlockPos var1) {
      if (!this.demoTimeExpired) {
         super.blockRemoving(var1);
      }
   }

   @Override
   public boolean tryHarvestBlock(BlockPos var1) {
      return this.demoTimeExpired ? false : super.tryHarvestBlock(var1);
   }

   @Override
   public boolean tryUseItem(EntityPlayer var1, World var2, ItemStack var3) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
         return false;
      } else {
         return super.tryUseItem(var1, var2, var3);
      }
   }

   @Override
   public boolean activateBlockOrUseItem(EntityPlayer var1, World var2, ItemStack var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (this.demoTimeExpired) {
         this.sendDemoReminder();
         return false;
      } else {
         return super.activateBlockOrUseItem(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }
}
