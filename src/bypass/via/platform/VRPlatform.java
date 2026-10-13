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
   private static final long b = 111879757538929L;

   @Override
   public ExecutorService O() {
      return ViaFabric.ASYNC_EXECUTOR;
   }

   @Override
   public EventLoop I() {
      return ViaFabric.EVENT_LOOP;
   }

   @Override
   public FutureTaskId runAsync(Runnable var1) {
      return new FutureTaskId(CompletableFuture.runAsync(var1, ViaFabric.ASYNC_EXECUTOR).exceptionally(var0 -> {
         if (!(var0 instanceof CancellationException)) {
            var0.printStackTrace();
         }

         return null;
      }));
   }

   public FutureTaskId runSync(Runnable var1) {
      return this.l(var1);
   }

   @Override
   public ViaCommandSender[] getOnlinePlayers() {
      return null;
   }

   @Override
   public void sendMessage(UUID var1, String var2) {
   }

   @Override
   public boolean kickPlayer(UUID var1, String var2) {
      return false;
   }

   @Override
   public ConfigurationProvider getConfigurationProvider() {
      return new VRPlatform.y3();
   }

   public class y3 implements ConfigurationProvider {
      @Override
      public void set(String var1, Object var2) {
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
