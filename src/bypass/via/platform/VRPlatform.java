package bypass.via.platform;

import bypass.via.ViaFabric;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import com.viaversion.viaversion.api.configuration.ConfigurationProvider;
import io.netty.channel.EventLoop;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class VRPlatform extends AbstractFabricPlatform {
   @Override
   public ExecutorService O() {
      return ViaFabric.ASYNC_EXECUTOR;
   }

   @Override
   public EventLoop I() {
      return ViaFabric.EVENT_LOOP;
   }

   @Override
   public FutureTaskId runAsync(Runnable runnable) {
      return new FutureTaskId(CompletableFuture.runAsync(runnable, ViaFabric.ASYNC_EXECUTOR).exceptionally(throwable -> {
         if (!(throwable instanceof CancellationException)) {
            throwable.printStackTrace();
         }

         return null;
      }));
   }

   public FutureTaskId runSync(Runnable runnable) {
      return this.l(runnable);
   }

   @Override
   public ViaCommandSender[] getOnlinePlayers() {
      return null;
   }

   @Override
   public void sendMessage(UUID uuid, String string) {
   }

   @Override
   public boolean kickPlayer(UUID uuid, String string) {
      return false;
   }

   @Override
   public ConfigurationProvider getConfigurationProvider() {
      return new VRPlatform.y3();
   }

   public class y3 implements ConfigurationProvider {
      @Override
      public void set(String string, Object object) {
      }

      @Override
      public void saveConfig() {
      }

      @Override
      public void reloadConfig() {
      }

      @Override
      public Map<String, Object> getValues() {
         return new HashMap<>();
      }
   }
}
