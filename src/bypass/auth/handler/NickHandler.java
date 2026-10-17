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
      PlayerStatsChecker.UnresolvedNick unresolvedNick = PlayerStatsChecker.unresolvedNicks.get(nickPacket.nick);
      if (unresolvedNick != null) {
         PlayerStatsChecker playerStatsChecker = new PlayerStatsChecker(PlayerListTracker.hypixelApi);
         playerStatsChecker.initialize(unresolvedNick.playerInfo.getGameProfile().getId(), nickPacket.name, unresolvedNick.playerInfo, true);
         PlayerStatsEntry playerStatsEntry = new PlayerStatsEntry(nickPacket.name, unresolvedNick.playerInfo, playerStatsChecker);
         playerStatsEntry.realName = nickPacket.name;
         playerStatsEntry.name = nickPacket.nick;
         PlayerListTracker.playerStats.add(playerStatsEntry);
      }
   }
}
