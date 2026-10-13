package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginClient implements INetHandlerLoginClient {
   private static final Logger logger = LogManager.getLogger();
   private final Minecraft mc;
   private final GuiScreen previousGuiScreen;
   private final NetworkManager networkManager;
   private GameProfile gameProfile;
   private static final long a = 112898916112982L;
   private static final long b = -2756629822980489200L;

   public NetHandlerLoginClient(NetworkManager var1, Minecraft var2, GuiScreen var3) {
      this.networkManager = var1;
      this.mc = var2;
      this.previousGuiScreen = var3;
   }

   @Override
   public void handleEncryptionRequest(S01PacketEncryptionRequest var1) {
      final SecretKey var4 = CryptManager.createNewSharedKey();
      String var5 = var1.getServerId();
      PublicKey var6 = var1.getPublicKey();
      String var7 = new BigInteger(CryptManager.getServerIdHash(var5, var6, var4)).toString((int)b);
      if (this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().isOnLAN()) {
         try {
            this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), var7);
         } catch (AuthenticationException var13) {
            logger.warn("Couldn't connect to auth servers but will continue to join LAN");
         }
      } else {
         try {
            this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), var7);
         } catch (AuthenticationUnavailableException var9) {
            this.networkManager
               .closeChannel(
                  new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.serversUnavailable"))
               );
            return;
         } catch (InvalidCredentialsException var10) {
            this.networkManager
               .closeChannel(
                  new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.invalidSession"))
               );
            return;
         } catch (AuthenticationException var11) {
            this.networkManager.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", var11.getMessage()));
            return;
         } catch (NullPointerException var12) {
            this.networkManager.closeChannel(new ChatComponentText(EnumChatFormatting.RED + "Log to a Minecraft account"));
            return;
         }
      }

      this.networkManager.sendPacket(new C01PacketEncryptionResponse(var4, var6, var1.getVerifyToken()), new GenericFutureListener<Future<? super Void>>() {
         @Override
         public void operationComplete(Future<? super Void> var1) throws Exception {
            NetHandlerLoginClient.this.networkManager.enableEncryption(var4);
         }
      });
   }

   private MinecraftSessionService getSessionService() {
      return this.mc.getSessionService();
   }

   @Override
   public void handleLoginSuccess(S02PacketLoginSuccess var1) {
      this.gameProfile = var1.getProfile();
      this.networkManager.setConnectionState(EnumConnectionState.PLAY);
      this.networkManager.setNetHandler(new NetHandlerPlayClient(this.mc, this.previousGuiScreen, this.networkManager, this.gameProfile));
   }

   @Override
   public void onDisconnect(IChatComponent var1) {
      this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", var1));
   }

   @Override
   public void handleDisconnect(S00PacketDisconnect var1) {
      this.networkManager.closeChannel(var1.func_149603_c());
   }

   @Override
   public void handleEnableCompression(S03PacketEnableCompression var1) {
      if (!this.networkManager.isLocalChannel()) {
         this.networkManager.setCompressionTreshold(var1.getCompressionTreshold());
      }
   }
}
