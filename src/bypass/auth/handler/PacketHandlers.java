package bypass.auth.handler;

import bypass.auth.net.PacketOutput;
import bypass.util.collection.ShapedMap;

public class PacketHandlers {
   private final ShapedMap<Short, PacketHandler<?>> P = new ShapedMap<>();
   private static final long a = 26584482795829L;

   public PacketHandlers() {
      LoginResponseHandler var5 = new LoginResponseHandler();
      this.P.put(var5.getPacketId().getId(), var5);
      PingHandler var6 = new PingHandler();
      this.P.put(var6.getPacketId().getId(), var6);
      ServerCommandHandler var7 = new ServerCommandHandler();
      this.P.put(var7.getPacketId().getId(), var7);
      UserListHandler var8 = new UserListHandler();
      this.P.put(var8.getPacketId().getId(), var8);
      IrcMessageHandler var9 = new IrcMessageHandler();
      this.P.put(var9.getPacketId().getId(), var9);
      BansHandler var10 = new BansHandler();
      this.P.put(var10.getPacketId().getId(), var10);
      NickHandler var11 = new NickHandler();
      this.P.put(var11.getPacketId().getId(), var11);
      DisablerScriptHandler var12 = new DisablerScriptHandler();
      this.P.put(var12.getPacketId().getId(), var12);
   }

   public <T> void handle(short var1, T var2, PacketOutput var3) {
      ((PacketHandler<Object>)this.P.get(var1)).handle(var2, var3);
   }
}
