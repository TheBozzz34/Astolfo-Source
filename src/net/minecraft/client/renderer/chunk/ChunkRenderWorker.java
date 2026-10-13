package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumWorldBlockLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderWorker implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkRenderDispatcher chunkRenderDispatcher;
   private final RegionRenderCacheBuilder regionRenderCacheBuilder;
   private static final long a = 29960912544986L;

   public ChunkRenderWorker(ChunkRenderDispatcher var1) {
      this(var1, (RegionRenderCacheBuilder)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher var1, RegionRenderCacheBuilder var2) {
      this.chunkRenderDispatcher = var1;
      this.regionRenderCacheBuilder = var2;
   }

   @Override
   public void run() {
      while (true) {
         try {
            this.processTask(this.chunkRenderDispatcher.getNextChunkUpdate());
         } catch (InterruptedException var5) {
            LOGGER.debug("Stopping due to interrupt");
            return;
         } catch (Throwable var6) {
            CrashReport var4 = CrashReport.makeCrashReport(var6, "Batching chunks");
            Minecraft.getMinecraft().crashed(Minecraft.getMinecraft().addGraphicsAndWorldToCrashReport(var4));
            return;
         }
      }
   }

   public void processTask(final ChunkCompileTaskGenerator var1) throws InterruptedException {
      var1.getLock().lock();

      try {
         if (var1.getStatus() != ChunkCompileTaskGenerator.Status.PENDING) {
            if (!var1.isFinished()) {
               LOGGER.warn("Chunk render task was " + var1.getStatus() + " when I expected it to be pending; ignoring task");
            }

            return;
         }

         var1.setStatus(ChunkCompileTaskGenerator.Status.COMPILING);
      } finally {
         var1.getLock().unlock();
      }

      Entity var5 = Minecraft.getMinecraft().getRenderViewEntity();
      if (var5 == null) {
         var1.finish();
      } else {
         var1.setRegionRenderCacheBuilder(this.getRegionRenderCacheBuilder());
         float var6 = (float)var5.posX;
         float var7 = (float)var5.posY + var5.getEyeHeight();
         float var8 = (float)var5.posZ;
         ChunkCompileTaskGenerator.Type var9 = var1.getType();
         if (var9 == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
            var1.getRenderChunk().rebuildChunk(var6, var7, var8, var1);
         } else if (var9 == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
            var1.getRenderChunk().resortTransparency(var6, var7, var8, var1);
         }

         var1.getLock().lock();

         try {
            if (var1.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
               if (!var1.isFinished()) {
                  LOGGER.warn("Chunk render task was " + var1.getStatus() + " when I expected it to be compiling; aborting task");
               }

               this.freeRenderBuilder(var1);
               return;
            }

            var1.setStatus(ChunkCompileTaskGenerator.Status.UPLOADING);
         } finally {
            var1.getLock().unlock();
         }

         final CompiledChunk var10 = var1.getCompiledChunk();
         ArrayList var11 = Lists.newArrayList();
         if (var9 == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
            for (EnumWorldBlockLayer var15 : EnumWorldBlockLayer.values()) {
               if (var10.isLayerStarted(var15)) {
                  var11.add(
                     this.chunkRenderDispatcher
                        .uploadChunk(var15, var1.getRegionRenderCacheBuilder().getWorldRendererByLayer(var15), var1.getRenderChunk(), var10)
                  );
               }
            }
         } else if (var9 == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
            var11.add(
               this.chunkRenderDispatcher
                  .uploadChunk(
                     EnumWorldBlockLayer.TRANSLUCENT,
                     var1.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT),
                     var1.getRenderChunk(),
                     var10
                  )
            );
         }

         final ListenableFuture var22 = Futures.allAsList(var11);
         var1.addFinishRunnable(new Runnable() {
            @Override
            public void run() {
               var22.cancel(false);
            }
         });
         Futures.addCallback(var22, new FutureCallback<List<Object>>() {
            private static final long a = 84140900396396L;

            public void onSuccess(List<Object> var1x) {
               ChunkRenderWorker.this.freeRenderBuilder(var1);
               var1.getLock().lock();

               try {
                  if (var1.getStatus() != ChunkCompileTaskGenerator.Status.UPLOADING) {
                     if (!var1.isFinished()) {
                        ChunkRenderWorker.LOGGER.warn("Chunk render task was " + var1.getStatus() + " when I expected it to be uploading; aborting task");
                     }

                     return;
                  }

                  var1.setStatus(ChunkCompileTaskGenerator.Status.DONE);
               } finally {
                  var1.getLock().unlock();
               }

               var1.getRenderChunk().setCompiledChunk(var10);
            }

            @Override
            public void onFailure(Throwable var1x) {
               ChunkRenderWorker.this.freeRenderBuilder(var1);
               if (!(var1x instanceof CancellationException) && !(var1x instanceof InterruptedException)) {
                  Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(var1x, "Rendering chunk"));
               }
            }
         });
      }
   }

   private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException {
      return this.regionRenderCacheBuilder != null ? this.regionRenderCacheBuilder : this.chunkRenderDispatcher.allocateRenderBuilder();
   }

   private void freeRenderBuilder(ChunkCompileTaskGenerator var1) {
      if (this.regionRenderCacheBuilder == null) {
         this.chunkRenderDispatcher.freeRenderBuilder(var1.getRegionRenderCacheBuilder());
      }
   }
}
