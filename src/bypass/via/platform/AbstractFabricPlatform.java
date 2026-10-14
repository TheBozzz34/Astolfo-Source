package bypass.via.platform;

import bypass.via.util.JLoggerToLog4j;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.configuration.ViaVersionConfig;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.libs.gson.JsonObject;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;

public abstract class AbstractFabricPlatform implements ViaPlatform<UUID> {
   private final Logger e = new JLoggerToLog4j(LogManager.getLogger("ViaVersion"));
   private final VRViaConfig S = new VRViaConfig();
   private final ViaAPI<UUID> N = new VRViaAPI();
   private static final long a = 108350551935021L;

   public abstract ExecutorService O();

   public abstract EventLoop I();

   public FutureTaskId l(Runnable runnable) {
      return new FutureTaskId(this.I().submit(runnable).addListener(this.errorLogger()));
   }

   public FutureTaskId runAsync(Runnable runnable) {
      return new FutureTaskId(CompletableFuture.runAsync(runnable, this.O()).exceptionally(throwable -> {
         if (!(throwable instanceof CancellationException)) {
            throwable.printStackTrace();
         }

         return null;
      }));
   }

   public FutureTaskId runSync(Runnable runnable, long l) {
      return new FutureTaskId(this.I().schedule(() -> this.runSync(runnable), l * 50L, TimeUnit.MILLISECONDS).addListener(this.errorLogger()));
   }

   public FutureTaskId runRepeatingSync(Runnable runnable, long l) {
      return new FutureTaskId(this.I().scheduleAtFixedRate(() -> this.runSync(runnable), 0L, l * 50L, TimeUnit.MILLISECONDS).addListener(this.errorLogger()));
   }

   public <T extends Future<?>> GenericFutureListener<T> errorLogger() {
      return value -> {
         if (!value.isCancelled() && value.cause() != null) {
            value.cause().printStackTrace();
         }
      };
   }

   @Override
   public boolean isProxy() {
      return true;
   }

   @Override
   public void onReload() {
   }

   @Override
   public Logger getLogger() {
      return this.e;
   }

   @Override
   public ViaVersionConfig getConf() {
      return this.S;
   }

   @Override
   public ViaAPI<UUID> getApi() {
      return this.N;
   }

   @Override
   public File getDataFolder() {
      return new File("do");
   }

   @Override
   public String getPluginVersion() {
      return "UNKNOWN";
   }

   @Override
   public String getPlatformName() {
      return "ViaFabric";
   }

   @Override
   public String getPlatformVersion() {
      return "UNKNOWN";
   }

   @Override
   public boolean isPluginEnabled() {
      return true;
   }

   @Override
   public boolean isOldClientsAllowed() {
      return true;
   }

   @Override
   public JsonObject getDump() {
      return new JsonObject();
   }
}
