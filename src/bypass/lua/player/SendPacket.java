package bypass.lua.player;

import bypass.ClientAccess;
import bypass.lua.WorldLibrary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

public final class SendPacket extends VarArgFunction {
   private static final long a = 61400430168137L;

   @Override
   public Varargs invoke(Varargs var1) {
      if (ClientAccess.mc.thePlayer != null) {
         int var5 = -1;
         LuaValue var6 = var1.arg1();
         if (var6.isnumber()) {
            var5 = var6.checkint();
         }

         if (var5 != -1) {
            LuaValue var7 = var1.arg(2);
            LuaValue var8 = var1.arg(3);
            LuaValue var9 = var1.arg(4);
            LuaValue var10 = var1.arg(5);
            LuaValue var11 = var1.arg(6);
            LuaValue var12 = var1.arg(7);
            LuaValue var13 = var1.arg(8);
            LuaValue var14 = var1.arg(9);
            switch (var1.narg()) {
               case 1:
                  switch (var5) {
                     case 7:
                        ClientAccess.mc
                           .thePlayer
                           .sendQueue
                           .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        return valueOf(true);
                     case 8:
                        ItemStack var21 = ClientAccess.mc.thePlayer.getHeldItem();
                        if (var21 != null) {
                           ClientAccess.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(var21));
                        }

                        return valueOf(true);
                     case 10:
                        ClientAccess.mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        return valueOf(true);
                     case 13:
                        ClientAccess.mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(0));
                        return valueOf(true);
                     case 22:
                        ClientAccess.mc
                           .thePlayer
                           .sendQueue
                           .addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                        return valueOf(true);
                     default:
                        return valueOf(true);
                  }
               case 2:
                  switch (var5) {
                     case 0:
                        if (var7.isint()) {
                           ClientAccess.mc.thePlayer.sendQueue.addToSendQueue(new C00PacketKeepAlive(var7.checkint()));
                        }

                        return valueOf(true);
                     case 1:
                     case 2:
                     case 4:
                     case 5:
                     case 6:
                     case 7:
                     case 10:
                     default:
                        return valueOf(true);
                     case 3:
                        if (var7.isboolean()) {
                           ClientAccess.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(var7.checkboolean()));
                        }

                        return valueOf(true);
                     case 8:
                        if (var7.isint()) {
                           try {
                              ItemStack var20 = ClientAccess.mc.thePlayer.inventory.getStackInSlot(var7.checkint() - 1);
                              if (var20 != null) {
                                 ClientAccess.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(var20));
                                 return valueOf(true);
                              }
                           } catch (Exception var18) {
                           }

                           return valueOf(true);
                        }

                        return valueOf(true);
                     case 9:
                        if (var7.isint()) {
                           ClientAccess.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(var7.checkint() - 1));
                        }

                        return valueOf(true);
                     case 11:
                        if (var7.isint()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .addToSendQueue(new C0BPacketEntityAction(ClientAccess.mc.thePlayer, C0BPacketEntityAction.Action.values()[var7.checkint() + 1]));
                        }

                        return valueOf(true);
                  }
               case 3:
                  switch (var5) {
                     case 2:
                        if (var7.isint() && var8.isint()) {
                           Entity var19 = WorldLibrary.X.get(var7.checkint());
                           if (var19 instanceof EntityLivingBase) {
                              ClientAccess.mc
                                 .thePlayer
                                 .sendQueue
                                 .addToSendQueue(new C02PacketUseEntity(var19, C02PacketUseEntity.Action.values()[var8.checkint() - 1]));
                              return valueOf(true);
                           }
                        }

                        return valueOf(true);
                     default:
                        return valueOf(true);
                  }
               case 4:
                  switch (var5) {
                     case 5:
                        if (var7.isnumber() && var8.isnumber() && var9.checkboolean()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(var7.tofloat(), var8.tofloat(), var9.checkboolean()));
                        }

                        return valueOf(true);
                     case 14:
                        if (var7.isnumber() && var8.isnumber() && var9.isnumber()) {
                           ClientAccess.mc
                              .playerController
                              .windowClick(
                                 ClientAccess.mc.thePlayer.inventoryContainer.windowId,
                                 var7.checkint(),
                                 var8.checkint(),
                                 var9.checkint(),
                                 ClientAccess.mc.thePlayer
                              );
                        }

                        return valueOf(true);
                     case 15:
                        if (var7.isnumber() && var8.isnumber() && var9.checkboolean()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .addToSendQueue(new C0FPacketConfirmTransaction(var7.checkint(), (short)var8.checkint(), var9.checkboolean()));
                        }

                        return valueOf(true);
                     default:
                        return valueOf(true);
                  }
               case 5:
                  switch (var5) {
                     case 4:
                        if (var7.isnumber() && var8.isnumber() && var9.isnumber() && var10.isboolean()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .addToSendQueue(
                                 new C03PacketPlayer.C04PacketPlayerPosition(var7.checkdouble(), var8.checkdouble(), var9.checkdouble(), var10.checkboolean())
                              );
                        }

                        return valueOf(true);
                     default:
                        return valueOf(true);
                  }
               case 6:
                  switch (var5) {
                     case 7:
                        if (var7.isnumber() && var8.isnumber() && var9.isnumber() && var10.isnumber() && var11.isnumber()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .addToSendQueue(
                                 new C07PacketPlayerDigging(
                                    C07PacketPlayerDigging.Action.values()[var7.checkint() - 1],
                                    new BlockPos(var8.checkdouble(), var9.checkdouble(), var10.checkdouble()),
                                    EnumFacing.values()[var11.checkint() - 1]
                                 )
                              );
                        }

                        return valueOf(true);
                     default:
                        return valueOf(true);
                  }
               case 7:
                  switch (var5) {
                     case 6:
                        if (var7.isnumber() && var8.isnumber() && var9.isnumber() && var10.isnumber() && var11.isnumber() && var12.isboolean()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .addToSendQueue(
                                 new C03PacketPlayer.C06PacketPlayerPosLook(
                                    var7.checkdouble(), var8.checkdouble(), var9.checkdouble(), var10.tofloat(), var11.tofloat(), var12.isboolean()
                                 )
                              );
                        }
                  }
               case 8:
               default:
                  break;
               case 9:
                  switch (var5) {
                     case 8:
                        if (var7.isnumber()
                           && var8.isnumber()
                           && var9.isnumber()
                           && var10.isnumber()
                           && var11.isnumber()
                           && var12.isnumber()
                           && var13.isnumber()
                           && var14.isnumber()) {
                           try {
                              ItemStack var15 = ClientAccess.mc.thePlayer.inventory.getStackInSlot(var11.checkint() - 1);
                              if (var15 != null) {
                                 C08PacketPlayerBlockPlacement var16 = new C08PacketPlayerBlockPlacement(
                                    new BlockPos(var7.checkdouble(), var8.checkdouble(), var9.checkdouble()),
                                    var10.checkint() - 1,
                                    var15,
                                    var12.tofloat(),
                                    var13.tofloat(),
                                    var14.tofloat()
                                 );
                                 ClientAccess.mc.thePlayer.sendQueue.addToSendQueue(var16);
                              }
                           } catch (Exception var17) {
                              var17.printStackTrace();
                           }
                        }
                  }
            }

            return valueOf(true);
         }
      }

      return NIL;
   }
}
