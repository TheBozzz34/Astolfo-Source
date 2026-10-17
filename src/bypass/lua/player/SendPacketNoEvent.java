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

public final class SendPacketNoEvent extends VarArgFunction {
   @Override
   public Varargs invoke(Varargs varargs) {
      if (ClientAccess.mc.thePlayer != null) {
         int i = -1;
         LuaValue luaValue = varargs.arg1();
         if (luaValue.isnumber()) {
            i = luaValue.checkint();
         }

         if (i != -1) {
            LuaValue luaValue2 = varargs.arg(2);
            LuaValue luaValue3 = varargs.arg(3);
            LuaValue luaValue4 = varargs.arg(4);
            LuaValue luaValue5 = varargs.arg(5);
            LuaValue luaValue6 = varargs.arg(6);
            LuaValue luaValue7 = varargs.arg(7);
            LuaValue luaValue8 = varargs.arg(8);
            LuaValue luaValue9 = varargs.arg(9);
            switch (varargs.narg()) {
               case 1:
                  switch (i) {
                     case 7:
                        ClientAccess.mc
                           .thePlayer
                           .sendQueue
                           .sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        return valueOf(true);
                     case 8:
                        ItemStack itemStack = ClientAccess.mc.thePlayer.getHeldItem();
                        if (itemStack != null) {
                           ClientAccess.mc.thePlayer.sendQueue.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(itemStack));
                        }

                        return valueOf(true);
                     case 10:
                        ClientAccess.mc.thePlayer.sendQueue.sendPacketNoEvent(new C0APacketAnimation());
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
                  switch (i) {
                     case 0:
                        if (luaValue2.isint()) {
                           ClientAccess.mc.thePlayer.sendQueue.sendPacketNoEvent(new C00PacketKeepAlive(luaValue2.checkint()));
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
                        if (luaValue2.isboolean()) {
                           ClientAccess.mc.thePlayer.sendQueue.sendPacketNoEvent(new C03PacketPlayer(luaValue2.checkboolean()));
                        }

                        return valueOf(true);
                     case 8:
                        if (luaValue2.isint()) {
                           try {
                              ItemStack itemStack2 = ClientAccess.mc.thePlayer.inventory.getStackInSlot(luaValue2.checkint() - 1);
                              if (itemStack2 != null) {
                                 ClientAccess.mc.thePlayer.sendQueue.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(itemStack2));
                                 return valueOf(true);
                              }
                           } catch (Exception exception) {
                           }

                           return valueOf(true);
                        }

                        return valueOf(true);
                     case 9:
                        if (luaValue2.isint()) {
                           ClientAccess.mc.thePlayer.sendQueue.sendPacketNoEvent(new C09PacketHeldItemChange(luaValue2.checkint() - 1));
                        }

                        return valueOf(true);
                     case 11:
                        if (luaValue2.isint()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .sendPacketNoEvent(
                                 new C0BPacketEntityAction(ClientAccess.mc.thePlayer, C0BPacketEntityAction.Action.values()[luaValue2.checkint() + 1])
                              );
                        }

                        return valueOf(true);
                  }
               case 3:
                  switch (i) {
                     case 2:
                        if (luaValue2.isint() && luaValue3.isint()) {
                           Entity entity = WorldLibrary.entitiesById.get(luaValue2.checkint());
                           if (entity instanceof EntityLivingBase) {
                              ClientAccess.mc
                                 .thePlayer
                                 .sendQueue
                                 .sendPacketNoEvent(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.values()[luaValue3.checkint() - 1]));
                              return valueOf(true);
                           }
                        }

                        return valueOf(true);
                     default:
                        return valueOf(true);
                  }
               case 4:
                  switch (i) {
                     case 5:
                        if (luaValue2.isnumber() && luaValue3.isnumber() && luaValue4.checkboolean()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(luaValue2.tofloat(), luaValue3.tofloat(), luaValue4.checkboolean()));
                        }

                        return valueOf(true);
                     case 14:
                        if (luaValue2.isnumber() && luaValue3.isnumber() && luaValue4.isnumber()) {
                           ClientAccess.mc
                              .playerController
                              .windowClickNoEvent(
                                 ClientAccess.mc.thePlayer.inventoryContainer.windowId,
                                 luaValue2.checkint(),
                                 luaValue3.checkint(),
                                 luaValue4.checkint(),
                                 ClientAccess.mc.thePlayer
                              );
                        }

                        return valueOf(true);
                     case 15:
                        if (luaValue2.isnumber() && luaValue3.isnumber() && luaValue4.checkboolean()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .sendPacketNoEvent(new C0FPacketConfirmTransaction(luaValue2.checkint(), (short)luaValue3.checkint(), luaValue4.checkboolean()));
                        }

                        return valueOf(true);
                     default:
                        return valueOf(true);
                  }
               case 5:
                  switch (i) {
                     case 4:
                        if (luaValue2.isnumber() && luaValue3.isnumber() && luaValue4.isnumber() && luaValue5.isboolean()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .sendPacketNoEvent(
                                 new C03PacketPlayer.C04PacketPlayerPosition(luaValue2.checkdouble(), luaValue3.checkdouble(), luaValue4.checkdouble(), luaValue5.checkboolean())
                              );
                        }

                        return valueOf(true);
                     default:
                        return valueOf(true);
                  }
               case 6:
                  switch (i) {
                     case 7:
                        if (luaValue2.isnumber() && luaValue3.isnumber() && luaValue4.isnumber() && luaValue5.isnumber() && luaValue6.isnumber()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .sendPacketNoEvent(
                                 new C07PacketPlayerDigging(
                                    C07PacketPlayerDigging.Action.values()[luaValue2.checkint() - 1],
                                    new BlockPos(luaValue3.checkdouble(), luaValue4.checkdouble(), luaValue5.checkdouble()),
                                    EnumFacing.values()[luaValue6.checkint() - 1]
                                 )
                              );
                        }

                        return valueOf(true);
                     default:
                        return valueOf(true);
                  }
               case 7:
                  switch (i) {
                     case 6:
                        if (luaValue2.isnumber() && luaValue3.isnumber() && luaValue4.isnumber() && luaValue5.isnumber() && luaValue6.isnumber() && luaValue7.isboolean()) {
                           ClientAccess.mc
                              .thePlayer
                              .sendQueue
                              .sendPacketNoEvent(
                                 new C03PacketPlayer.C06PacketPlayerPosLook(
                                    luaValue2.checkdouble(), luaValue3.checkdouble(), luaValue4.checkdouble(), luaValue5.tofloat(), luaValue6.tofloat(), luaValue7.isboolean()
                                 )
                              );
                        }
                  }
               case 8:
               default:
                  break;
               case 9:
                  switch (i) {
                     case 8:
                        if (luaValue2.isnumber()
                           && luaValue3.isnumber()
                           && luaValue4.isnumber()
                           && luaValue5.isnumber()
                           && luaValue6.isnumber()
                           && luaValue7.isnumber()
                           && luaValue8.isnumber()
                           && luaValue9.isnumber()) {
                           try {
                              ItemStack itemStack3 = ClientAccess.mc.thePlayer.inventory.getStackInSlot(luaValue6.checkint() - 1);
                              if (itemStack3 != null) {
                                 C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement = new C08PacketPlayerBlockPlacement(
                                    new BlockPos(luaValue2.checkdouble(), luaValue3.checkdouble(), luaValue4.checkdouble()),
                                    luaValue5.checkint() - 1,
                                    itemStack3,
                                    luaValue7.tofloat(),
                                    luaValue8.tofloat(),
                                    luaValue9.tofloat()
                                 );
                                 ClientAccess.mc.thePlayer.sendQueue.sendPacketNoEvent(c08PacketPlayerBlockPlacement);
                              }
                           } catch (Exception exception2) {
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
