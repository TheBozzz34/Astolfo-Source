package bypass.module.hidden;

import bypass.command.Command;
import bypass.command.ScriptCommand;
import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.player.ChatEvent;
import bypass.module.HiddenModule;
import bypass.module.Module;
import bypass.module.ModuleInfo;
import bypass.setting.BooleanSetting;
import bypass.setting.EnumSetting;
import bypass.setting.MapSetting;
import bypass.setting.NumberSetting;
import bypass.setting.Setting;
import java.text.NumberFormat;
import java.text.ParseException;
import net.minecraft.network.play.client.C01PacketChatMessage;

public final class ChatCommandHandler extends HiddenModule {
   @EventHandler(ChatEvent.class)
   private Listener<ChatEvent> onChat = var1 -> {
      long var2 = c ^ 2250943439137L;
      long var4 = var2 ^ 82034894370664L;
      long var6 = var2 ^ 43272982567901L;
      switch (var1.getDirection()) {
         case TO_SEND:
            if (var1.getWords().length == 0) {
               return;
            } else if (var1.getWords()[0].startsWith(".")) {
               if (var1.getMessage().startsWith("..")) {
                  mc.thePlayer.sendQueue.u(new C01PacketChatMessage(var1.getMessage().substring(1)));
                  var1.cancel();
                  return;
               } else {
                  Command var9 = (Command)client.getCommandManager().getCommand(var1.getWords()[0].replace(".", ""));
                  if (var9 != null) {
                     if (!var1.isUserTyped() && var9 instanceof ScriptCommand) {
                        chat.P("https://www.youtube.com/watch?v=WSeNSzJ2-Jw");
                        return;
                     }

                     var9.runCommand(var1.getWords());
                  } else {
                     for (ModuleInfo var11 : client.getModuleManager().getList()) {
                        if (var11 instanceof Module) {
                           StringBuilder var12 = new StringBuilder();
                           String[] var13 = var1.getWords();
                           if (var13[0].replace(".", "").equalsIgnoreCase(var11.getName())) {
                              if (var13.length <= 1) {
                                 ((Module)var11).toggle();
                                 if (var1.isUserTyped()) {
                                    chat.I(String.format("§7%s §fhas been toggled", var11.getName()));
                                 }
                              } else if (var13.length <= 2 && var13[1].equalsIgnoreCase("help")) {
                                 int var28 = 0;

                                 for (Setting var34 : ((Module)var11).getSettings()) {
                                    if (var28 == ((Module)var11).getSettings().size() - 1) {
                                       var12.append(var34.getName()).append(".");
                                    } else {
                                       var12.append(var34.getName()).append(", ");
                                    }

                                    var28++;
                                 }

                                 if (var1.isUserTyped()) {
                                    chat.I(String.format("%s: §7%s", var11.getName(), var12.toString()));
                                 }
                              } else {
                                 for (Setting var15 : ((Module)var11).getSettings()) {
                                    if (var13[1].equalsIgnoreCase(var15.getName())) {
                                       if (var15 instanceof BooleanSetting) {
                                          if (var13.length == 3) {
                                             try {
                                                BooleanSetting var33 = (BooleanSetting)var15;
                                                var33.set(Boolean.parseBoolean(var13[2]));
                                                if (var1.isUserTyped()) {
                                                   chat.I(
                                                      String.format(
                                                         "§7%s§f has been set to: %s%s", var15.getName(), this.booleanColor(var33.getValue()), var33.getValue()
                                                      )
                                                   );
                                                }
                                             } catch (Exception var27) {
                                                chat.I("put boolean state correctly");
                                             }
                                          } else {
                                             ((BooleanSetting)var15).set(!((BooleanSetting)var15).getValue());
                                             if (var1.isUserTyped()) {
                                                chat.I(
                                                   String.format(
                                                      "§7%s§f has been set to: %s%s",
                                                      var15.getName(),
                                                      this.booleanColor(((BooleanSetting)var15).getValue()),
                                                      ((BooleanSetting)var15).getValue()
                                                   )
                                                );
                                             }
                                          }
                                       } else if (var15 instanceof NumberSetting) {
                                          if (var13.length >= 3) {
                                             try {
                                                if (var15.getValueClass() == Integer.class && var13[2].contains(",")) {
                                                   chat.P(String.format("%s is an Integer, you cant input a decimal on it.", var15.getName()));
                                                   var1.cancel();
                                                   return;
                                                }

                                                NumberFormat var32 = NumberFormat.getInstance();
                                                Number var40 = var32.parse(var13[2]);
                                                if (var15.getValueClass().equals(Float.class)) {
                                                   var15.setValue(Float.valueOf(var13[2]));
                                                } else if (var15.getValueClass().equals(Integer.class)) {
                                                   var15.setValue(Integer.valueOf(var13[2]));
                                                } else {
                                                   var15.setValue(var40);
                                                }

                                                if (var1.isUserTyped()) {
                                                   chat.I(
                                                      String.format(
                                                         "%s is now %s (before set to %s)", var15.getName(), var15.getValue(), var15.getPreviousValue()
                                                      )
                                                   );
                                                }
                                             } catch (NumberFormatException var25) {
                                                chat.P("wrong number argument");
                                             } catch (ParseException var26) {
                                                var26.printStackTrace();
                                             }
                                          } else if (var1.isUserTyped()) {
                                             chat.I(
                                                String.format(
                                                   "%s -> %s: %s (min: %s, max: %s)",
                                                   var11.getName(),
                                                   var15.getName(),
                                                   var15.getDescription(),
                                                   ((NumberSetting)var15).getMin(),
                                                   ((NumberSetting)var15).getMax()
                                                )
                                             );
                                          }
                                       } else if (var15 instanceof EnumSetting) {
                                          if (var13.length > 2) {
                                             if (var13.length >= 3) {
                                                var15.parse(var13[2]);
                                                if (var1.isUserTyped()) {
                                                   chat.I(
                                                      String.format(
                                                         "§7%s§f has been set to: §7%s§f (before set to §7%s§f)",
                                                         var15.getName(),
                                                         var15.getValue(),
                                                         var15.getPreviousValue()
                                                      )
                                                   );
                                                }
                                             }
                                          } else {
                                             StringBuilder var31 = new StringBuilder();

                                             for (Enum var46 : (Enum[])((Enum)var15.getValue()).getClass().getEnumConstants()) {
                                                var31.append(var46).append(" ");
                                             }

                                             if (var1.isUserTyped()) {
                                                chat.I(
                                                   String.format(
                                                      "%s -> %s: %s (values: %s)",
                                                      var11.getName(),
                                                      var15.getName(),
                                                      var15.getDescription(),
                                                      var31.toString().toLowerCase()
                                                   )
                                                );
                                             }
                                          }
                                       } else if (var15 instanceof MapSetting) {
                                          if (var13.length == 2) {
                                             StringBuilder var16 = new StringBuilder();

                                             for (Setting var18 : ((MapSetting)var15).getSettings().values()) {
                                                var16.append(var18.getName()).append(" ");
                                             }

                                             if (var1.isUserTyped()) {
                                                chat.I(String.format("Available values: §7%s", var16.toString()));
                                             }
                                          }

                                          if (var13.length >= 3) {
                                             Setting var30 = ((MapSetting)var15).getSetting(var13[2]);
                                             if (var13.length == 3) {
                                                if (var30 instanceof BooleanSetting) {
                                                   BooleanSetting var35 = (BooleanSetting)var30;
                                                   var35.set(!var35.getValue());
                                                   if (var1.isUserTyped()) {
                                                      chat.I(
                                                         String.format(
                                                            "§7%s§f has been set to: %s%s",
                                                            var15.getName(),
                                                            this.booleanColor(var35.getValue()),
                                                            var35.getValue()
                                                         )
                                                      );
                                                   }
                                                }

                                                if (var30 instanceof NumberSetting && var1.isUserTyped()) {
                                                   chat.I(
                                                      String.format(
                                                         "%s -> %s: %s (min: %s, max: %s)",
                                                         var11.getName(),
                                                         var30.getName(),
                                                         var30.getDescription(),
                                                         ((NumberSetting)var30).getMin(),
                                                         ((NumberSetting)var30).getMax()
                                                      )
                                                   );
                                                }

                                                if (var30 instanceof EnumSetting) {
                                                   StringBuilder var36 = new StringBuilder();

                                                   for (Enum var21 : (Enum[])((Enum)var30.getValue()).getClass().getEnumConstants()) {
                                                      var36.append(var21).append(" ");
                                                   }

                                                   if (var1.isUserTyped()) {
                                                      chat.I(
                                                         String.format(
                                                            "%s -> %s: %s (values: %s)",
                                                            var11.getName(),
                                                            var30.getName(),
                                                            var30.getDescription(),
                                                            var36.toString().toLowerCase()
                                                         )
                                                      );
                                                   }
                                                }
                                             }

                                             if (var13.length == 4) {
                                                if (var30 instanceof BooleanSetting) {
                                                   try {
                                                      BooleanSetting var37 = (BooleanSetting)var30;
                                                      var37.set(Boolean.parseBoolean(var13[3]));
                                                      if (var1.isUserTyped()) {
                                                         chat.I(
                                                            String.format(
                                                               "§7%s§f has been set to: %s%s",
                                                               var15.getName(),
                                                               this.booleanColor(var37.getValue()),
                                                               var37.getValue()
                                                            )
                                                         );
                                                      }
                                                   } catch (Exception var24) {
                                                      chat.I("put boolean state correctly");
                                                   }
                                                }

                                                if (var30 instanceof NumberSetting) {
                                                   NumberSetting var38 = (NumberSetting)var30;

                                                   try {
                                                      if (var15.getValueClass() == Integer.class && var13[3].contains(",")) {
                                                         chat.P(String.format("%s is an Integer, you cant input a decimal on it.", var30.getName()));
                                                         var1.cancel();
                                                         return;
                                                      }

                                                      NumberFormat var42 = NumberFormat.getInstance();
                                                      Number var44 = var42.parse(var13[3]);
                                                      if (var38.getValueClass().equals(Float.class)) {
                                                         var38.setValue(Float.valueOf(var13[3]));
                                                      } else if (var38.getValueClass().equals(Integer.class)) {
                                                         var38.setValue(Integer.valueOf(var13[3]));
                                                      } else {
                                                         var38.setValue(var44);
                                                      }

                                                      if (var1.isUserTyped()) {
                                                         chat.I(
                                                            String.format(
                                                               "%s is now %s (before set to %s)", var30.getName(), var30.getValue(), var30.getPreviousValue()
                                                            )
                                                         );
                                                      }
                                                   } catch (NumberFormatException var22) {
                                                      chat.P("wrong number argument");
                                                   } catch (ParseException var23) {
                                                      var23.printStackTrace();
                                                   }
                                                }

                                                if (var30 instanceof EnumSetting) {
                                                   var30.parse(var13[3]);
                                                   if (var1.isUserTyped()) {
                                                      chat.I(
                                                         String.format(
                                                            "§7%s§f has been set to: §7%s§f (before set to §7%s§f)",
                                                            var30.getName(),
                                                            var30.getValue(),
                                                            var30.getPreviousValue()
                                                         )
                                                      );
                                                   }
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }

                              var1.cancel();
                              return;
                           }
                        }
                     }
                  }

                  var1.cancel();
               }
            }
      }
   };
   private static final long c = 140634036546554L;

   public ChatCommandHandler() {
      client.getEventBus().subscribe(this);
   }

   private String booleanColor(boolean var1) {
      return var1 ? "§2" : "§4";
   }
}
