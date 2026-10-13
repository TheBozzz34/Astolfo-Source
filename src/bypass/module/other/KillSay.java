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
   private Listener<WorldLoadEvent> onWorldLoad = var1x -> this.d.clear();
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      if (this.reload.getValue()) {
         this.T();
         this.reload.set(false);
         Toasts.TOASTS.g("Reloaded KillSay", "");
      }

      if (this.B.hasElapsed(this.delay.getValue().intValue())) {
         String var5x = this.d.poll();
         if (var5x != null && !var5x.isEmpty()) {
            if (this.C >= this.s.size()) {
               this.C = 0;
            }

            mc.thePlayer
               .sendQueue
               .addToSendQueue(
                  new C01PacketChatMessage(
                     (this.shout.getValue() ? "/shout " : "")
                        + (this.random.getValue() ? this.s.get(RandomUtils.RANDOM.nextInt(this.s.size())) : this.s.get(this.C)).replace("{user}", var5x)
                  )
               );
            this.C++;
         }
      }
   };
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      switch (var1x.getDirection()) {
         case RECEIVED:
            if (var1x.getPacket() instanceof S02PacketChat) {
               S02PacketChat var5x = (S02PacketChat)var1x.getPacket();
               String var6x = EnumChatFormatting.getTextWithoutFormattingCodes(var5x.getChatComponent().getUnformattedText());
               if (!var5x.isChat() && var6x.contains(String.format("by %s", mc.session.getUsername()))) {
                  String var7 = var6x.split(" ")[0];
                  if (var7.equals("BED")) {
                     return;
                  }

                  this.d.add(var7);
               }
            }
            break;
         case OUTGOING:
            if (var1x.getPacket() instanceof C01PacketChatMessage) {
               this.B.reset();
            }
      }
   };
   private static final long c = 116111440784837L;

   public KillSay(Category var1) {
      super(var1);
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
      Path var5 = client.getDirectory().getAbsoluteFile().toPath().resolve("killsay.txt");
      if (!Files.exists(var5)) {
         try {
            Files.createFile(var5);
            FileWriter var6 = new FileWriter(var5.toFile());

            for (String var8 : this.s) {
               var6.write(var8);
               var6.write("\n");
            }

            var6.close();
         } catch (IOException var9) {
            var9.printStackTrace();
         }
      } else {
         this.T();
      }
   }

   private void T() {
      Path var4 = client.getDirectory().getAbsoluteFile().toPath().resolve("killsay.txt");
      this.s.clear();

      try {
         BufferedReader var5 = new BufferedReader(new FileReader(var4.toFile()));

         String var6;
         while ((var6 = var5.readLine()) != null) {
            this.s.add(var6);
         }

         var5.close();
      } catch (IOException var7) {
         var7.printStackTrace();
      }
   }
}
