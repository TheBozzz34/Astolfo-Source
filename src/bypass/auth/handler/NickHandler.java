package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.auth.packet.NickPacket;
import bypass.auth.packet.PacketId;
import bypass.hypixel.PlayerStatsChecker;
import bypass.hypixel.PlayerStatsEntry;
import bypass.module.exploits.Denicker;
import bypass.module.hidden.PlayerListTracker;

public class NickHandler extends PacketHandler<NickPacket> {
   public NickHandler() {
      super(PacketId.NICK);
   }

   public void handle(NickPacket nickPacket, PacketOutput packetOutput) {
      System.out.println(nickPacket.nick + " is " + nickPacket.name);
      Denicker.nicks.put(nickPacket.nick, nickPacket.name);
      PlayerStatsChecker.r95 r952 = PlayerStatsChecker.V.get(nickPacket.nick);
      if (r952 != null) {
         PlayerStatsChecker playerStatsChecker = new PlayerStatsChecker(PlayerListTracker.hypixelApi);
         playerStatsChecker.initialize(r952.F.getGameProfile().getId(), nickPacket.name, r952.F, true);
         PlayerStatsEntry playerStatsEntry = new PlayerStatsEntry(nickPacket.name, r952.F, playerStatsChecker);
         playerStatsEntry.E = nickPacket.name;
         playerStatsEntry.Q = nickPacket.nick;
         PlayerListTracker.playerStats.add(playerStatsEntry);
      }
   }
}
