package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.util.collection.ShapedMap;

public class PacketHandlers {
   private final ShapedMap<Short, PacketHandler<?>> handlers = new ShapedMap<>();

   public PacketHandlers() {
      LoginResponseHandler loginResponseHandler = new LoginResponseHandler();
      this.handlers.put(loginResponseHandler.getPacketId().getId(), loginResponseHandler);
      PingHandler pingHandler = new PingHandler();
      this.handlers.put(pingHandler.getPacketId().getId(), pingHandler);
      ServerCommandHandler serverCommandHandler = new ServerCommandHandler();
      this.handlers.put(serverCommandHandler.getPacketId().getId(), serverCommandHandler);
      UserListHandler userListHandler = new UserListHandler();
      this.handlers.put(userListHandler.getPacketId().getId(), userListHandler);
      IrcMessageHandler ircMessageHandler = new IrcMessageHandler();
      this.handlers.put(ircMessageHandler.getPacketId().getId(), ircMessageHandler);
      BansHandler bansHandler = new BansHandler();
      this.handlers.put(bansHandler.getPacketId().getId(), bansHandler);
      NickHandler nickHandler = new NickHandler();
      this.handlers.put(nickHandler.getPacketId().getId(), nickHandler);
      DisablerScriptHandler disablerScriptHandler = new DisablerScriptHandler();
      this.handlers.put(disablerScriptHandler.getPacketId().getId(), disablerScriptHandler);
   }

   public <T> void handle(short s, T object, PacketOutput packetOutput) {
      ((PacketHandler<Object>)this.handlers.get(s)).handle(object, packetOutput);
   }
}
