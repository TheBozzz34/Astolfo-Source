package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.util.collection.ShapedMap;

public class PacketHandlers {
   private final ShapedMap<Short, PacketHandler<?>> P = new ShapedMap<>();
   private static final long a = 26584482795829L;

   public PacketHandlers() {
      LoginResponseHandler loginResponseHandler = new LoginResponseHandler();
      this.P.put(loginResponseHandler.getPacketId().getId(), loginResponseHandler);
      PingHandler pingHandler = new PingHandler();
      this.P.put(pingHandler.getPacketId().getId(), pingHandler);
      ServerCommandHandler serverCommandHandler = new ServerCommandHandler();
      this.P.put(serverCommandHandler.getPacketId().getId(), serverCommandHandler);
      UserListHandler userListHandler = new UserListHandler();
      this.P.put(userListHandler.getPacketId().getId(), userListHandler);
      IrcMessageHandler ircMessageHandler = new IrcMessageHandler();
      this.P.put(ircMessageHandler.getPacketId().getId(), ircMessageHandler);
      BansHandler bansHandler = new BansHandler();
      this.P.put(bansHandler.getPacketId().getId(), bansHandler);
      NickHandler nickHandler = new NickHandler();
      this.P.put(nickHandler.getPacketId().getId(), nickHandler);
      DisablerScriptHandler disablerScriptHandler = new DisablerScriptHandler();
      this.P.put(disablerScriptHandler.getPacketId().getId(), disablerScriptHandler);
   }

   public <T> void handle(short s, T object, PacketOutput packetOutput) {
      ((PacketHandler<Object>)this.P.get(s)).handle(object, packetOutput);
   }
}
