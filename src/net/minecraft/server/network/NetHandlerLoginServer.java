package net.minecraft.server.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer, ITickable {
   private static final AtomicInteger AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();
   private static final Random RANDOM = new Random();
   private final byte[] verifyToken = new byte[4];
   private final MinecraftServer server;
   public final NetworkManager networkManager;
   private NetHandlerLoginServer.LoginState currentLoginState = NetHandlerLoginServer.LoginState.HELLO;
   private int connectionTimer;
   private GameProfile loginGameProfile;
   private String serverId = "";
   private SecretKey secretKey;
   private EntityPlayerMP player;
   private static final long a = 61332921158210L;
   private static final long g = -2314815045571181992L;

   public NetHandlerLoginServer(MinecraftServer var1, NetworkManager var2) {
      this.server = var1;
      this.networkManager = var2;
      RANDOM.nextBytes(this.verifyToken);
   }

   @Override
   public void update() {
      if (this.currentLoginState == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT) {
         this.tryAcceptPlayer();
      } else if (this.currentLoginState == NetHandlerLoginServer.LoginState.DELAY_ACCEPT) {
         EntityPlayerMP var4 = this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId());
         if (var4 == null) {
            this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
            this.server.getConfigurationManager().initializeConnectionToPlayer(this.networkManager, this.player);
            this.player = null;
         }
      }

      if (this.connectionTimer++ == (int)g) {
         this.closeConnection("Took too long to log in");
      }
   }

   public void closeConnection(String var1) {
      try {
         logger.info("Disconnecting " + this.getConnectionInfo() + ": " + var1);
         ChatComponentText var4 = new ChatComponentText(var1);
         this.networkManager.sendPacket(new S00PacketDisconnect(var4));
         this.networkManager.closeChannel(var4);
      } catch (Exception var5) {
         logger.error("Error whilst disconnecting player", var5);
      }
   }

   public void tryAcceptPlayer() {
      if (!this.loginGameProfile.isComplete()) {
         this.loginGameProfile = this.getOfflineProfile(this.loginGameProfile);
      }

      String var4 = this.server.getConfigurationManager().allowUserToConnect(this.networkManager.getRemoteAddress(), this.loginGameProfile);
      if (var4 != null) {
         this.closeConnection(var4);
      } else {
         this.currentLoginState = NetHandlerLoginServer.LoginState.ACCEPTED;
         if (this.server.getNetworkCompressionTreshold() >= 0 && !this.networkManager.isLocalChannel()) {
            this.networkManager.sendPacket(new S03PacketEnableCompression(this.server.getNetworkCompressionTreshold()), new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1) throws Exception {
                  NetHandlerLoginServer.this.networkManager.setCompressionTreshold(NetHandlerLoginServer.this.server.getNetworkCompressionTreshold());
               }
            });
         }

         this.networkManager.sendPacket(new S02PacketLoginSuccess(this.loginGameProfile));
         EntityPlayerMP var5 = this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId());
         if (var5 != null) {
            this.currentLoginState = NetHandlerLoginServer.LoginState.DELAY_ACCEPT;
            this.player = this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile);
         } else {
            this.server
               .getConfigurationManager()
               .initializeConnectionToPlayer(this.networkManager, this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile));
         }
      }
   }

   @Override
   public void onDisconnect(IChatComponent var1) {
      logger.info(this.getConnectionInfo() + " lost connection: " + var1.getUnformattedText());
   }

   public String getConnectionInfo() {
      return this.loginGameProfile != null
         ? this.loginGameProfile.toString() + " (" + this.networkManager.getRemoteAddress().toString() + ")"
         : String.valueOf(this.networkManager.getRemoteAddress());
   }

   @Override
   public void processLoginStart(C00PacketLoginStart var1) {
      Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet");
      this.loginGameProfile = var1.getProfile();
      if (this.server.isServerInOnlineMode() && !this.networkManager.isLocalChannel()) {
         this.currentLoginState = NetHandlerLoginServer.LoginState.KEY;
         this.networkManager.sendPacket(new S01PacketEncryptionRequest(this.serverId, this.server.getKeyPair().getPublic(), this.verifyToken));
      } else {
         this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
      }
   }

   @Override
   public void processEncryptionResponse(C01PacketEncryptionResponse var1) {
      Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.KEY, "Unexpected key packet");
      PrivateKey var6 = this.server.getKeyPair().getPrivate();
      if (!Arrays.equals(this.verifyToken, var1.getVerifyToken(var6))) {
         throw new IllegalStateException("Invalid nonce!");
      }

      this.secretKey = var1.getSecretKey(var6);
      this.currentLoginState = NetHandlerLoginServer.LoginState.AUTHENTICATING;
      this.networkManager.enableEncryption(this.secretKey);
      (new Thread("User Authenticator #" + AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
            private static final long a = 100752301991030L;
            private static final long e = 553132487111344144L;

            @Override
            public void run() {
               GameProfile var4 = NetHandlerLoginServer.this.loginGameProfile;

               try {
                  String var5 = new BigInteger(
                        CryptManager.getServerIdHash(
                           NetHandlerLoginServer.this.serverId,
                           NetHandlerLoginServer.this.server.getKeyPair().getPublic(),
                           NetHandlerLoginServer.this.secretKey
                        )
                     )
                     .toString((int)e);
                  NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.server
                     .getMinecraftSessionService()
                     .hasJoinedServer(new GameProfile((UUID)null, var4.getName()), var5);
                  if (NetHandlerLoginServer.this.loginGameProfile != null) {
                     NetHandlerLoginServer.logger
                        .info(
                           "UUID of player "
                              + NetHandlerLoginServer.this.loginGameProfile.getName()
                              + " is "
                              + NetHandlerLoginServer.this.loginGameProfile.getId()
                        );
                     NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
                     NetHandlerLoginServer.logger.warn("Failed to verify username but will let them in anyway!");
                     NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(var4);
                     NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else {
                     NetHandlerLoginServer.this.closeConnection("Failed to verify username!");
                     NetHandlerLoginServer.logger
                        .error("Username '" + NetHandlerLoginServer.this.loginGameProfile.getName() + "' tried to join with an invalid session");
                  }
               } catch (AuthenticationUnavailableException var6x) {
                  if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
                     NetHandlerLoginServer.logger.warn("Authentication servers are down but will let them in anyway!");
                     NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(var4);
                     NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                  } else {
                     NetHandlerLoginServer.this.closeConnection("Authentication servers are down. Please try again later, sorry!");
                     NetHandlerLoginServer.logger.error("Couldn't verify username because servers are unavailable");
                  }
               }
            }
         })
         .start();
   }

   public GameProfile getOfflineProfile(GameProfile var1) {
      UUID var4 = UUID.nameUUIDFromBytes(("OfflinePlayer:" + var1.getName()).getBytes(Charsets.UTF_8));
      return new GameProfile(var4, var1.getName());
   }

   public enum LoginState {
      HELLO,
      KEY,
      AUTHENTICATING,
      READY_TO_ACCEPT,
      DELAY_ACCEPT,
      ACCEPTED;
   }
}
