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
   private final PacketOutput packetOutput;
   private final ChannelGroup channelGroup;
   private final Channel packetChannel;
   private final SocketConnection socketConnection;
   private final Connector connector;
   private final PacketHandlers packetHandlers = new PacketHandlers();

   public AuthConnection() {
      this.packetOutput = PacketWriters.newBuilder()
         .withWriter((short)562, new LoginPacketWriter())
         .withWriter((short)237, new IntWriter())
         .withWriter((short)635, new LoginPacketWriter())
         .withWriter((short)236, new UserListWriter())
         .withWriter((short)926, new IrcMessageWriter())
         .withWriter((short)863, new BansWriter())
         .withWriter((short)469, new StringWriter())
         .withWriter((short)452, new StringWriter())
         .build();
      this.channelGroup = ImmutableChannelGroup.newBuilder()
         .withChannel(
            "packet",
            PacketChannel.newBuilder()
               .withConnectionListener(new DisconnectHandler())
               .withReader((short)623, new IntReader())
               .withReader((short)672, new LoginResponseReader())
               .withReader((short)831, new ServerCommandReader())
               .withReader((short)723, new UserListReader())
               .withReader((short)732, new IrcMessageReader())
               .withReader((short)373, new StringReader())
               .withReader((short)892, new BansReader())
               .withReader((short)864, new NickReader())
               .withListeners((short)723, Collections.singletonList(object -> this.packetHandlers.handle((short)723, object, this.packetOutput)))
               .withListeners((short)732, Collections.singletonList(object2 -> this.packetHandlers.handle((short)732, object2, this.packetOutput)))
               .withListeners((short)672, Collections.singletonList(object3 -> this.packetHandlers.handle((short)672, object3, this.packetOutput)))
               .withListeners((short)623, Collections.singletonList(object4 -> this.packetHandlers.handle((short)623, object4, this.packetOutput)))
               .withListeners((short)864, Collections.singletonList(object5 -> this.packetHandlers.handle((short)864, object5, this.packetOutput)))
               .withListeners((short)831, Collections.singletonList(object6 -> this.packetHandlers.handle((short)831, object6, this.packetOutput)))
               .withListeners((short)373, Collections.singletonList(object7 -> this.packetHandlers.handle((short)373, object7, this.packetOutput)))
               .withListeners((short)892, Collections.singletonList(object8 -> this.packetHandlers.handle((short)892, object8, this.packetOutput)))
               .withPacketOutput(this.packetOutput)
               .withHandlerFactory(new DefaultConnectionHandlerFactory(new DefaultSelectorRegistrar()))
               .withConnectCallback(
                  () -> this.runTask(
                     () -> this.runTask(
                        () -> this.runTask(
                           () -> this.runTask(
                              () -> this.runTask(
                                 () -> this.runTask(
                                    () -> this.runTask(
                                       () -> this.runTask(
                                          () -> this.runTask(
                                             () -> this.runTask(
                                                () -> this.runTask(
                                                   () -> this.runTask(
                                                      () -> this.runTask(
                                                         () -> {
                                                            if (DisconnectHandler.disconnected) {
                                                               mc.mouseHelper
                                                                  .Y()
                                                                  .getEndpoint()
                                                                  .getConnection()
                                                                  .getPacketOutput()
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
                                                                  .getConnection()
                                                                  .getPacketOutput()
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
                                                               LoginScreen.authenticating = true;
                                                               LoginScreen.authTimer.reset();
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
               .withErrorHandler(Throwable::printStackTrace)
               .build()
         )
         .build();
      this.packetChannel = this.channelGroup.get("packet");
      this.socketConnection = (SocketConnection)this.packetChannel.getConnection();
      this.connector = HandshakeConnector.createFactory().createConnector();
   }

   public void runTask(Task task) {
      task.run();
   }

   public void connect(String string, int i) {
      new Thread(() -> {
         try {
            this.connector.connect(this.socketConnection, SocketChannelOpener.INSTANCE, new InetSocketAddress(string, i));
         } catch (IOException ioException) {
         }
      }).start();
   }

   public SocketConnection getSocketConnection() {
      return this.socketConnection;
   }

   public Channel getPacketChannel() {
      return this.packetChannel;
   }

   public PacketOutput getPacketOutput() {
      return this.packetOutput;
   }

   public ChannelGroup getChannelGroup() {
      return this.channelGroup;
   }
}
