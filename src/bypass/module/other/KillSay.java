package bypass.module.other;

import bypass.Toasts;
import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.NumberSetting;
import bypass.util.RandomUtils;
import bypass.util.Stopwatch;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;

public final class KillSay extends Module {
   private final List<String> s = new ArrayList<>();
   private final Queue<String> d = new LinkedList<>();
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Delay between each message", 3000, 1, 30000, "ms");
   private final BooleanSetting reload = new BooleanSetting("Reload", "Reload killsay", false);
   private final BooleanSetting shout = new BooleanSetting("Shout", "Shout option", false);
   private final BooleanSetting random = new BooleanSetting("Random", "Random killsay", false);
   private final Stopwatch B = new Stopwatch();
   private int C;
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = worldLoadEvent -> this.d.clear();
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = updateEvent -> {
      if (this.reload.getValue()) {
         this.T();
         this.reload.set(false);
         Toasts.TOASTS.g("Reloaded KillSay", "");
      }

      if (this.B.hasElapsed(this.delay.getValue().intValue())) {
         String string = this.d.poll();
         if (string != null && !string.isEmpty()) {
            if (this.C >= this.s.size()) {
               this.C = 0;
            }

            mc.thePlayer
               .sendQueue
               .addToSendQueue(
                  new C01PacketChatMessage(
                     (this.shout.getValue() ? "/shout " : "")
                        + (this.random.getValue() ? this.s.get(RandomUtils.RANDOM.nextInt(this.s.size())) : this.s.get(this.C)).replace("{user}", string)
                  )
               );
            this.C++;
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = packetEvent -> {
      switch (packetEvent.getDirection()) {
         case RECEIVED:
            if (packetEvent.getPacket() instanceof S02PacketChat) {
               S02PacketChat s02PacketChat = (S02PacketChat)packetEvent.getPacket();
               String string = EnumChatFormatting.getTextWithoutFormattingCodes(s02PacketChat.getChatComponent().getUnformattedText());
               if (!s02PacketChat.isChat() && string.contains(String.format("by %s", mc.session.getUsername()))) {
                  String string2 = string.split(" ")[0];
                  if (string2.equals("BED")) {
                     return;
                  }

                  this.d.add(string2);
               }
            }
            break;
         case OUTGOING:
            if (packetEvent.getPacket() instanceof C01PacketChatMessage) {
               this.B.reset();
            }
      }
   };

   public KillSay(Category category) {
      super(category);
      this.addSettings(this.delay, this.reload, this.shout, this.random);
      this.setHidden(false);
      this.s
         .addAll(
            Arrays.asList(
               "LOOOOl owned {user}",
               "i have adod enabled {user}",
               "am usein ethereal 2.4 {user}",
               "why so bad? {user}",
               "sex {user}",
               "*fortnite dances on {user}*"
            )
         );
      Path path = client.getDirectory().getAbsoluteFile().toPath().resolve("killsay.txt");
      if (!Files.exists(path)) {
         try {
            Files.createFile(path);
            FileWriter fileWriter = new FileWriter(path.toFile());

            for (String string : this.s) {
               fileWriter.write(string);
               fileWriter.write("\n");
            }

            fileWriter.close();
         } catch (IOException ioException) {
            ioException.printStackTrace();
         }
      } else {
         this.T();
      }
   }

   private void T() {
      Path path = client.getDirectory().getAbsoluteFile().toPath().resolve("killsay.txt");
      this.s.clear();

      try {
         BufferedReader bufferedReader = new BufferedReader(new FileReader(path.toFile()));

         String string;
         while ((string = bufferedReader.readLine()) != null) {
            this.s.add(string);
         }

         bufferedReader.close();
      } catch (IOException ioException) {
         ioException.printStackTrace();
      }
   }
}
