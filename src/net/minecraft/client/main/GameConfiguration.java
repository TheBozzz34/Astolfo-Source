package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import net.minecraft.util.Session;

public class GameConfiguration {
   public final GameConfiguration.UserInformation userInfo;
   public final GameConfiguration.DisplayInformation displayInfo;
   public final GameConfiguration.FolderInformation folderInfo;
   public final GameConfiguration.GameInformation gameInfo;
   public final GameConfiguration.ServerInformation serverInfo;
   public String autoLoginToken;
   private static final long a = 1001822851414L;

   public GameConfiguration(
      GameConfiguration.UserInformation var1,
      GameConfiguration.DisplayInformation var2,
      GameConfiguration.FolderInformation var3,
      GameConfiguration.GameInformation var4,
      GameConfiguration.ServerInformation var5
   ) {
      this.userInfo = var1;
      this.displayInfo = var2;
      this.folderInfo = var3;
      this.gameInfo = var4;
      this.serverInfo = var5;
   }

   public static class DisplayInformation {
      public final int width;
      public final int height;
      public final boolean fullscreen;
      public final boolean checkGlErrors;

      public DisplayInformation(int var1, int var2, boolean var3, boolean var4) {
         this.width = var1;
         this.height = var2;
         this.fullscreen = var3;
         this.checkGlErrors = var4;
      }
   }

   public static class FolderInformation {
      public final File mcDataDir;
      public final File resourcePacksDir;
      public final File assetsDir;
      public final String assetIndex;
      private static final long a = 73759036040101L;

      public FolderInformation(File var1, File var2, File var3, String var4) {
         this.mcDataDir = var1;
         this.resourcePacksDir = var2;
         this.assetsDir = var3;
         this.assetIndex = var4;
      }
   }

   public static class GameInformation {
      public final boolean isDemo;
      public final String version;

      public GameInformation(boolean var1, String var2) {
         this.isDemo = var1;
         this.version = var2;
      }
   }

   public static class ServerInformation {
      public final String serverName;
      public final int serverPort;

      public ServerInformation(String var1, int var2) {
         this.serverName = var1;
         this.serverPort = var2;
      }
   }

   public static class UserInformation {
      public final Session session;
      public final PropertyMap userProperties;
      public final PropertyMap profileProperties;
      public final Proxy proxy;

      public UserInformation(Session var1, PropertyMap var2, PropertyMap var3, Proxy var4) {
         this.session = var1;
         this.userProperties = var2;
         this.profileProperties = var3;
         this.proxy = var4;
      }
   }
}
