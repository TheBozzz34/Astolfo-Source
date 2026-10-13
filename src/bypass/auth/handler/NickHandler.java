package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.auth.packet.NickPacket;
import bypass.auth.packet.PacketId;
import bypass.hypixel.PlayerStatsChecker;
import bypass.hypixel.PlayerStatsEntry;
import bypass.module.exploits.Denicker;
import bypass.module.hidden.PlayerListTracker;

public class NickHandler extends PacketHandler<NickPacket> {
   private static final long b = 64693951764225L;

   public NickHandler() {
      super(PacketId.NICK);
   }

   public void handle(NickPacket var1, PacketOutput var2) {
      System.out.println(var1.nick + " is " + var1.name);
      Denicker.nicks.put(var1.nick, var1.name);
      PlayerStatsChecker.r95 var7 = PlayerStatsChecker.V.get(var1.nick);
      if (var7 != null) {
         PlayerStatsChecker var8 = new PlayerStatsChecker(PlayerListTracker.hypixelApi);
         var8.initialize(var7.F.getGameProfile().getId(), var1.name, var7.F, true);
         PlayerStatsEntry var9 = new PlayerStatsEntry(var1.name, var7.F, var8);
         var9.E = var1.name;
         var9.Q = var1.nick;
         PlayerListTracker.playerStats.add(var9);
      }
   }
}
