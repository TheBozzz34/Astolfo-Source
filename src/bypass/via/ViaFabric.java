package bypass.via;

import bypass.via.commands.VRCommandHandler;
import bypass.via.platform.VRBackwardsLoader;
import bypass.via.platform.VRInjector;
import bypass.via.platform.VRLoader;
import bypass.via.platform.VRPlatform;
import bypass.via.platform.VRRewindLoader;
import bypass.via.protocol.ViaFabricHostnameProtocol;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import io.netty.channel.EventLoop;
import io.netty.channel.local.LocalEventLoopGroup;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public enum ViaFabric {
   INSTANCE;

   private ProtocolVersion version = ProtocolVersion.v1_8;
   public static boolean enabled = true;
   public static final ExecutorService ASYNC_EXECUTOR;
   public static final EventLoop EVENT_LOOP;
   public static CompletableFuture<Void> INIT_FUTURE = new CompletableFuture<>();
   private static final ViaFabric[] m = new ViaFabric[]{INSTANCE};
   private static final long a = 59169997553531L;

   public void init() {
      MappingDataLoader.enableMappingsCache();
      Via.init(
         ViaManagerImpl.builder().injector(new VRInjector()).loader(new VRLoader()).commandHandler(new VRCommandHandler()).platform(new VRPlatform()).build()
      );
      ((ViaManagerImpl)Via.getManager()).init();
      Via.getManager().getProtocolManager().registerBaseProtocol(ViaFabricHostnameProtocol.i, Range.lessThan(Integer.MIN_VALUE));
      ProtocolVersion.register(-2, "AUTO");
      INIT_FUTURE.complete(null);
      new VRBackwardsLoader().run();
      new VRRewindLoader().run();
   }

   public void setVersion(ProtocolVersion protocolVersion) {
      this.version = protocolVersion;
   }

   public boolean isNative() {
      return this.version.getVersion() == 47 || !enabled;
   }

   public boolean isAfter1_12_2() {
      return this.version.getVersion() > ProtocolVersion.v1_12_2.getVersion();
   }

   public ProtocolVersion getVersion() {
      return this.version;
   }

   static {
      long l = a ^ 74516771677396L;
      long m = 7312635741812825108L ^ l;
      ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ViaFabric-%d").build();
      ASYNC_EXECUTOR = Executors.newFixedThreadPool(8, threadFactory);
      EVENT_LOOP = new LocalEventLoopGroup(1, threadFactory).next();
      EVENT_LOOP.submit(INIT_FUTURE::join);
   }
}
