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
   private Listener<ChatEvent> onChat = chatEvent -> {
      switch (chatEvent.getDirection()) {
         case TO_SEND:
            if (chatEvent.getWords().length == 0) {
               return;
            } else if (chatEvent.getWords()[0].startsWith(".")) {
               if (chatEvent.getMessage().startsWith("..")) {
                  mc.thePlayer.sendQueue.u(new C01PacketChatMessage(chatEvent.getMessage().substring(1)));
                  chatEvent.cancel();
                  return;
               } else {
                  Command command = (Command)client.getCommandManager().getCommand(chatEvent.getWords()[0].replace(".", ""));
                  if (command != null) {
                     if (!chatEvent.isUserTyped() && command instanceof ScriptCommand) {
                        chat.P("https://www.youtube.com/watch?v=WSeNSzJ2-Jw");
                        return;
                     }

                     command.runCommand(chatEvent.getWords());
                  } else {
                     for (ModuleInfo moduleInfo : client.getModuleManager().getList()) {
                        if (moduleInfo instanceof Module) {
                           StringBuilder stringBuilder = new StringBuilder();
                           String[] strings = chatEvent.getWords();
                           if (strings[0].replace(".", "").equalsIgnoreCase(moduleInfo.getName())) {
                              if (strings.length <= 1) {
                                 ((Module)moduleInfo).toggle();
                                 if (chatEvent.isUserTyped()) {
                                    chat.I(String.format("§7%s §fhas been toggled", moduleInfo.getName()));
                                 }
                              } else if (strings.length <= 2 && strings[1].equalsIgnoreCase("help")) {
                                 int i = 0;

                                 for (Setting setting : ((Module)moduleInfo).getSettings()) {
                                    if (i == ((Module)moduleInfo).getSettings().size() - 1) {
                                       stringBuilder.append(setting.getName()).append(".");
                                    } else {
                                       stringBuilder.append(setting.getName()).append(", ");
                                    }

                                    i++;
                                 }

                                 if (chatEvent.isUserTyped()) {
                                    chat.I(String.format("%s: §7%s", moduleInfo.getName(), stringBuilder.toString()));
                                 }
                              } else {
                                 for (Setting setting2 : ((Module)moduleInfo).getSettings()) {
                                    if (strings[1].equalsIgnoreCase(setting2.getName())) {
                                       if (setting2 instanceof BooleanSetting) {
                                          if (strings.length == 3) {
                                             try {
                                                BooleanSetting booleanSetting = (BooleanSetting)setting2;
                                                booleanSetting.set(Boolean.parseBoolean(strings[2]));
                                                if (chatEvent.isUserTyped()) {
                                                   chat.I(
                                                      String.format(
                                                         "§7%s§f has been set to: %s%s", setting2.getName(), this.booleanColor(booleanSetting.getValue()), booleanSetting.getValue()
                                                      )
                                                   );
                                                }
                                             } catch (Exception exception) {
                                                chat.I("put boolean state correctly");
                                             }
                                          } else {
                                             ((BooleanSetting)setting2).set(!((BooleanSetting)setting2).getValue());
                                             if (chatEvent.isUserTyped()) {
                                                chat.I(
                                                   String.format(
                                                      "§7%s§f has been set to: %s%s",
                                                      setting2.getName(),
                                                      this.booleanColor(((BooleanSetting)setting2).getValue()),
                                                      ((BooleanSetting)setting2).getValue()
                                                   )
                                                );
                                             }
                                          }
                                       } else if (setting2 instanceof NumberSetting) {
                                          if (strings.length >= 3) {
                                             try {
                                                if (setting2.getValueClass() == Integer.class && strings[2].contains(",")) {
                                                   chat.P(String.format("%s is an Integer, you cant input a decimal on it.", setting2.getName()));
                                                   chatEvent.cancel();
                                                   return;
                                                }

                                                NumberFormat numberFormat = NumberFormat.getInstance();
                                                Number number2 = numberFormat.parse(strings[2]);
                                                if (setting2.getValueClass().equals(Float.class)) {
                                                   setting2.setValue(Float.valueOf(strings[2]));
                                                } else if (setting2.getValueClass().equals(Integer.class)) {
                                                   setting2.setValue(Integer.valueOf(strings[2]));
                                                } else {
                                                   setting2.setValue(number2);
                                                }

                                                if (chatEvent.isUserTyped()) {
                                                   chat.I(
                                                      String.format(
                                                         "%s is now %s (before set to %s)", setting2.getName(), setting2.getValue(), setting2.getPreviousValue()
                                                      )
                                                   );
                                                }
                                             } catch (NumberFormatException numberFormatException) {
                                                chat.P("wrong number argument");
                                             } catch (ParseException parseException) {
                                                parseException.printStackTrace();
                                             }
                                          } else if (chatEvent.isUserTyped()) {
                                             chat.I(
                                                String.format(
                                                   "%s -> %s: %s (min: %s, max: %s)",
                                                   moduleInfo.getName(),
                                                   setting2.getName(),
                                                   setting2.getDescription(),
                                                   ((NumberSetting)setting2).getMin(),
                                                   ((NumberSetting)setting2).getMax()
                                                )
                                             );
                                          }
                                       } else if (setting2 instanceof EnumSetting) {
                                          if (strings.length > 2) {
                                             if (strings.length >= 3) {
                                                setting2.parse(strings[2]);
                                                if (chatEvent.isUserTyped()) {
                                                   chat.I(
                                                      String.format(
                                                         "§7%s§f has been set to: §7%s§f (before set to §7%s§f)",
                                                         setting2.getName(),
                                                         setting2.getValue(),
                                                         setting2.getPreviousValue()
                                                      )
                                                   );
                                                }
                                             }
                                          } else {
                                             StringBuilder stringBuilder2 = new StringBuilder();

                                             for (Enum enum2 : (Enum[])((Enum)setting2.getValue()).getClass().getEnumConstants()) {
                                                stringBuilder2.append(enum2).append(" ");
                                             }

                                             if (chatEvent.isUserTyped()) {
                                                chat.I(
                                                   String.format(
                                                      "%s -> %s: %s (values: %s)",
                                                      moduleInfo.getName(),
                                                      setting2.getName(),
                                                      setting2.getDescription(),
                                                      stringBuilder2.toString().toLowerCase()
                                                   )
                                                );
                                             }
                                          }
                                       } else if (setting2 instanceof MapSetting) {
                                          if (strings.length == 2) {
                                             StringBuilder stringBuilder3 = new StringBuilder();

                                             for (Setting setting3 : ((MapSetting)setting2).getSettings().values()) {
                                                stringBuilder3.append(setting3.getName()).append(" ");
                                             }

                                             if (chatEvent.isUserTyped()) {
                                                chat.I(String.format("Available values: §7%s", stringBuilder3.toString()));
                                             }
                                          }

                                          if (strings.length >= 3) {
                                             Setting setting4 = ((MapSetting)setting2).getSetting(strings[2]);
                                             if (strings.length == 3) {
                                                if (setting4 instanceof BooleanSetting) {
                                                   BooleanSetting booleanSetting2 = (BooleanSetting)setting4;
                                                   booleanSetting2.set(!booleanSetting2.getValue());
                                                   if (chatEvent.isUserTyped()) {
                                                      chat.I(
                                                         String.format(
                                                            "§7%s§f has been set to: %s%s",
                                                            setting2.getName(),
                                                            this.booleanColor(booleanSetting2.getValue()),
                                                            booleanSetting2.getValue()
                                                         )
                                                      );
                                                   }
                                                }

                                                if (setting4 instanceof NumberSetting && chatEvent.isUserTyped()) {
                                                   chat.I(
                                                      String.format(
                                                         "%s -> %s: %s (min: %s, max: %s)",
                                                         moduleInfo.getName(),
                                                         setting4.getName(),
                                                         setting4.getDescription(),
                                                         ((NumberSetting)setting4).getMin(),
                                                         ((NumberSetting)setting4).getMax()
                                                      )
                                                   );
                                                }

                                                if (setting4 instanceof EnumSetting) {
                                                   StringBuilder stringBuilder4 = new StringBuilder();

                                                   for (Enum enum3 : (Enum[])((Enum)setting4.getValue()).getClass().getEnumConstants()) {
                                                      stringBuilder4.append(enum3).append(" ");
                                                   }

                                                   if (chatEvent.isUserTyped()) {
                                                      chat.I(
                                                         String.format(
                                                            "%s -> %s: %s (values: %s)",
                                                            moduleInfo.getName(),
                                                            setting4.getName(),
                                                            setting4.getDescription(),
                                                            stringBuilder4.toString().toLowerCase()
                                                         )
                                                      );
                                                   }
                                                }
                                             }

                                             if (strings.length == 4) {
                                                if (setting4 instanceof BooleanSetting) {
                                                   try {
                                                      BooleanSetting booleanSetting3 = (BooleanSetting)setting4;
                                                      booleanSetting3.set(Boolean.parseBoolean(strings[3]));
                                                      if (chatEvent.isUserTyped()) {
                                                         chat.I(
                                                            String.format(
                                                               "§7%s§f has been set to: %s%s",
                                                               setting2.getName(),
                                                               this.booleanColor(booleanSetting3.getValue()),
                                                               booleanSetting3.getValue()
                                                            )
                                                         );
                                                      }
                                                   } catch (Exception exception2) {
                                                      chat.I("put boolean state correctly");
                                                   }
                                                }

                                                if (setting4 instanceof NumberSetting) {
                                                   NumberSetting numberSetting = (NumberSetting)setting4;

                                                   try {
                                                      if (setting2.getValueClass() == Integer.class && strings[3].contains(",")) {
                                                         chat.P(String.format("%s is an Integer, you cant input a decimal on it.", setting4.getName()));
                                                         chatEvent.cancel();
                                                         return;
                                                      }

                                                      NumberFormat numberFormat2 = NumberFormat.getInstance();
                                                      Number number3 = numberFormat2.parse(strings[3]);
                                                      if (numberSetting.getValueClass().equals(Float.class)) {
                                                         numberSetting.setValue(Float.valueOf(strings[3]));
                                                      } else if (numberSetting.getValueClass().equals(Integer.class)) {
                                                         numberSetting.setValue(Integer.valueOf(strings[3]));
                                                      } else {
                                                         numberSetting.setValue(number3);
                                                      }

                                                      if (chatEvent.isUserTyped()) {
                                                         chat.I(
                                                            String.format(
                                                               "%s is now %s (before set to %s)", setting4.getName(), setting4.getValue(), setting4.getPreviousValue()
                                                            )
                                                         );
                                                      }
                                                   } catch (NumberFormatException numberFormatException2) {
                                                      chat.P("wrong number argument");
                                                   } catch (ParseException parseException2) {
                                                      parseException2.printStackTrace();
                                                   }
                                                }

                                                if (setting4 instanceof EnumSetting) {
                                                   setting4.parse(strings[3]);
                                                   if (chatEvent.isUserTyped()) {
                                                      chat.I(
                                                         String.format(
                                                            "§7%s§f has been set to: §7%s§f (before set to §7%s§f)",
                                                            setting4.getName(),
                                                            setting4.getValue(),
                                                            setting4.getPreviousValue()
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

                              chatEvent.cancel();
                              return;
                           }
                        }
                     }
                  }

                  chatEvent.cancel();
               }
            }
      }
   };

   public ChatCommandHandler() {
      client.getEventBus().subscribe(this);
   }

   private String booleanColor(boolean bl) {
      return bl ? "§2" : "§4";
   }
}
