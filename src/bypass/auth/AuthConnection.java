package bypass.auth;

import bypass.Astolfo;
import bypass.ClientAccess;
import bypass.auth.handler.PacketHandlers;
import bypass.auth.net.Channel;
import bypass.auth.net.ChannelGroup;
import bypass.auth.net.Connector;
import bypass.auth.net.DefaultConnectionHandlerFactory;
import bypass.auth.net.DefaultSelectorRegistrar;
import bypass.auth.net.HandshakeConnector;
import bypass.auth.net.ImmutableChannelGroup;
import bypass.auth.net.PacketChannel;
import bypass.auth.net.PacketOutput;
import bypass.auth.net.PacketWriters;
import bypass.auth.net.SocketChannelOpener;
import bypass.auth.net.SocketConnection;
import bypass.auth.net.reader.BansReader;
import bypass.auth.net.reader.IntReader;
import bypass.auth.net.reader.IrcMessageReader;
import bypass.auth.net.reader.LoginResponseReader;
import bypass.auth.net.reader.NickReader;
import bypass.auth.net.reader.ServerCommandReader;
import bypass.auth.net.reader.StringReader;
import bypass.auth.net.reader.UserListReader;
import bypass.auth.net.writer.BansWriter;
import bypass.auth.net.writer.IntWriter;
import bypass.auth.net.writer.IrcMessageWriter;
import bypass.auth.net.writer.LoginPacketWriter;
import bypass.auth.net.writer.StringWriter;
import bypass.auth.net.writer.UserListWriter;
import bypass.auth.packet.LoginPacket;
import bypass.gui.screen.LoginScreen;
import bypass.loader.check.Task;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;

public class AuthConnection implements ClientAccess {
   private final PacketOutput g;
   private final ChannelGroup q;
   private final Channel W;
   private final SocketConnection O;
   private final Connector A;
   private final PacketHandlers R = new PacketHandlers();
   private static final long b = 113458559165862L;

   public AuthConnection() {
      this.g = PacketWriters.U()
         .y((short)562, new LoginPacketWriter())
         .y((short)237, new IntWriter())
         .y((short)635, new LoginPacketWriter())
         .y((short)236, new UserListWriter())
         .y((short)926, new IrcMessageWriter())
         .y((short)863, new BansWriter())
         .y((short)469, new StringWriter())
         .y((short)452, new StringWriter())
         .P();
      this.q = ImmutableChannelGroup.g()
         .H(
            "packet",
            PacketChannel.U()
               .z(new DisconnectHandler())
               .V((short)623, new IntReader())
               .V((short)672, new LoginResponseReader())
               .V((short)831, new ServerCommandReader())
               .V((short)723, new UserListReader())
               .V((short)732, new IrcMessageReader())
               .V((short)373, new StringReader())
               .V((short)892, new BansReader())
               .V((short)864, new NickReader())
               .Z((short)723, Collections.singletonList(var1 -> this.R.handle((short)723, var1, this.g)))
               .Z((short)732, Collections.singletonList(var1 -> this.R.handle((short)732, var1, this.g)))
               .Z((short)672, Collections.singletonList(var1 -> this.R.handle((short)672, var1, this.g)))
               .Z((short)623, Collections.singletonList(var1 -> this.R.handle((short)623, var1, this.g)))
               .Z((short)864, Collections.singletonList(var1 -> this.R.handle((short)864, var1, this.g)))
               .Z((short)831, Collections.singletonList(var1 -> this.R.handle((short)831, var1, this.g)))
               .Z((short)373, Collections.singletonList(var1 -> this.R.handle((short)373, var1, this.g)))
               .Z((short)892, Collections.singletonList(var1 -> this.R.handle((short)892, var1, this.g)))
               .Q(this.g)
               .a(new DefaultConnectionHandlerFactory(new DefaultSelectorRegistrar()))
               .K(
                  () -> this.Z(
                     () -> this.Z(
                        () -> this.Z(
                           () -> this.Z(
                              () -> this.Z(
                                 () -> this.Z(
                                    () -> this.Z(
                                       () -> this.Z(
                                          () -> this.Z(
                                             () -> this.Z(
                                                () -> this.Z(
                                                   () -> this.Z(
                                                      () -> this.Z(
                                                         () -> {
                                                            long var0 = b ^ 56795120037848L;
                                                            long var2 = (var0 ^ 88444972987244L) >>> 16;
                                                            int var4 = (int)((var0 ^ 88444972987244L) << 48 >>> 48);
                                                            if (DisconnectHandler.disconnected) {
                                                               mc.mouseHelper
                                                                  .Y()
                                                                  .getEndpoint()
                                                                  .B()
                                                                  .t()
                                                                  .getSender()
                                                                  .send(
                                                                     (short)635,
                                                                     new LoginPacket(
                                                                        "",
                                                                        mc.mouseHelper.Y().getAccount().getUsername(),
                                                                        LoginScreen.passwordField.getText(),
                                                                        "",
                                                                        mc.autoLoginToken
                                                                     )
                                                                  );
                                                            } else {
                                                               mc.mouseHelper
                                                                  .Y()
                                                                  .getEndpoint()
                                                                  .B()
                                                                  .t()
                                                                  .getSender()
                                                                  .send(
                                                                     (short)562,
                                                                     new LoginPacket(
                                                                        "1,8,8+" + Astolfo.INSTANCE.getVersion(),
                                                                        LoginScreen.usernameField.getText(),
                                                                        LoginScreen.passwordField.getText(),
                                                                        AuthUtil.getHwid(),
                                                                        mc.autoLoginToken
                                                                     )
                                                                  );
                                                               LoginScreen.p = true;
                                                               LoginScreen.N.reset();
                                                            }
                                                         }
                                                      )
                                                   )
                                                )
                                             )
                                          )
                                       )
                                    )
                                 )
                              )
                           )
                        )
                     )
                  )
               )
               .A(Throwable::printStackTrace)
               .C()
         )
         .K();
      this.W = this.q.get("packet");
      this.O = (SocketConnection)this.W.K();
      this.A = HandshakeConnector.l().d();
   }

   public void Z(Task var1) {
      var1.c();
   }

   public void J(String var1, int var2) {
      new Thread(() -> {
         try {
            this.A.K(this.O, SocketChannelOpener.INSTANCE, new InetSocketAddress(var1, var2));
         } catch (IOException var4) {
         }
      }).start();
   }

   public SocketConnection J() {
      return this.O;
   }

   public Channel g() {
      return this.W;
   }

   public PacketOutput t() {
      return this.g;
   }

   public ChannelGroup o() {
      return this.q;
   }
}
