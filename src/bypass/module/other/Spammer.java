package bypass.module.other;

import bypass.event.EventHandler;
import bypass.event.Listener;
import bypass.event.network.PacketDirection;
import bypass.event.network.PacketEvent;
import bypass.event.player.UpdateEvent;
import bypass.event.world.WorldLoadEvent;
import bypass.module.Category;
import bypass.module.Module;
import bypass.setting.BooleanSetting;
import bypass.setting.MapSetting;
import bypass.setting.MapSettingBuilder;
import bypass.setting.NumberSetting;
import bypass.setting.StringSetting;
import bypass.util.Stopwatch;
import net.minecraft.network.play.client.C01PacketChatMessage;
import org.apache.commons.lang3.RandomStringUtils;

public final class Spammer extends Module {
   private final NumberSetting<Integer> delay = new NumberSetting<>("Delay", "Delay between each message", 3000, 1, 30000, "ms");
   private final StringSetting message = new StringSetting("Message", "Message to spam", "cocaine");
   private final BooleanSetting multiLine = new BooleanSetting("Multi-Line", "Sends multiple different lines (; separator)", true);
   private final BooleanSetting hypixel = new BooleanSetting("Hypixel", "Switches lobbies in hypixel", false);
   private final MapSetting random = new MapSettingBuilder("Random")
      .ordered()
      .description("random options")
      .add("Enable", new BooleanSetting("Enable", "Randomizes message at the end", false))
      .add("Letters", new BooleanSetting("Letters", "Uses letters to randomize", false))
      .add("Numbers", new BooleanSetting("Numbers", "Uses numbers to randomize", false))
      .add("Length", new NumberSetting<>("Length", "Length of randomized string", 5, 1, 12))
      .build();
   private final Stopwatch m = new Stopwatch();
   private int S;
   @EventHandler(PacketEvent.class)
   private Listener<PacketEvent> onPacket = var1x -> {
      if (var1x.getDirection() == PacketDirection.RECEIVED && var1x.getPacket() instanceof C01PacketChatMessage && !this.hypixel.getValue()) {
         this.m.reset();
      }
   };
   private boolean A;
   private final String[] V = new String[]{"", "sw", "bw", "duels"};
   @EventHandler(UpdateEvent.class)
   private Listener<UpdateEvent> onUpdate = var1x -> {
      String var5 = this.message.getValue();
      if (this.message.getValue().contains(";") && this.multiLine.getValue()) {
         String[] var6 = this.message.getValue().split(";");
         if (this.S >= var6.length) {
            this.S = 0;
         }

         var5 = var6[this.S];
         this.S++;
      }

      boolean var11 = this.random.getSetting("Enable").asBoolean();
      int var7 = this.random.getSetting("Length").asInt();
      boolean var8 = this.random.getSetting("Letters").asBoolean();
      boolean var9 = this.random.getSetting("Numbers").asBoolean();
      String var10 = RandomStringUtils.random(var7, var8, var9);
      if (this.hypixel.getValue()) {
         if (!this.A) {
            mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(var5 + (var11 ? var10 : "")));
            this.A = true;
         } else if (this.m.hasElapsed(200.0F)) {
            mc.thePlayer.sendChatMessage("/hub " + this.V[(int)(Math.random() * this.V.length)]);
            this.m.reset();
         }
      } else {
         if (this.m.hasElapsed(this.delay.getValue().intValue())) {
            if (var5.startsWith("-") || var5.startsWith(" -")) {
               return;
            }

            mc.thePlayer.sendChatMessage(var5 + (var11 ? var10 : ""));
            this.m.reset();
         }
      }
   };
   @EventHandler(WorldLoadEvent.class)
   private Listener<WorldLoadEvent> onWorldLoad = var1x -> {
      this.m.reset();
      this.A = false;
   };
   private static final long c = 285072059933L;

   public Spammer(Category var1) {
      super(var1);
      this.addSettings(this.message, this.delay, this.random, this.multiLine, this.hypixel);
   }
}
