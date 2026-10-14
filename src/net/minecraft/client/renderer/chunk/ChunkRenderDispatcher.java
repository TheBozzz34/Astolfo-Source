package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.EnumWorldBlockLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ChunkRenderDispatcher {
   private static final Logger logger = LogManager.getLogger();
   private static final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).build();
   private final List<ChunkRenderWorker> listThreadedWorkers = Lists.newArrayList();
   private final BlockingQueue<ChunkCompileTaskGenerator> queueChunkUpdates = Queues.newArrayBlockingQueue(100);
   private final BlockingQueue<RegionRenderCacheBuilder> queueFreeRenderBuilders = Queues.newArrayBlockingQueue(5);
   private final WorldVertexBufferUploader worldVertexUploader = new WorldVertexBufferUploader();
   private final VertexBufferUploader vertexUploader = new VertexBufferUploader();
   private final Queue<ListenableFutureTask<?>> queueChunkUploads = Queues.newArrayDeque();
   private final ChunkRenderWorker renderWorker;
   private static final long c = 87872219545781L;

   public ChunkRenderDispatcher() {
      for (int i = 0; i < 2; i++) {
         ChunkRenderWorker chunkRenderWorker = new ChunkRenderWorker(this);
         Thread thread = threadFactory.newThread(chunkRenderWorker);
         thread.start();
         this.listThreadedWorkers.add(chunkRenderWorker);
      }

      for (int j = 0; j < 5; j++) {
         this.queueFreeRenderBuilders.add(new RegionRenderCacheBuilder());
      }

      this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
   }

   public String getDebugInfo() {
      return String.format("pC: %03d, pU: %1d, aB: %1d", this.queueChunkUpdates.size(), this.queueChunkUploads.size(), this.queueFreeRenderBuilders.size());
   }

   public boolean runChunkUploads(long l) {
      boolean bl = false;

      long m;
      do {
         boolean bl2 = false;
         synchronized (this.queueChunkUploads) {
            if (!this.queueChunkUploads.isEmpty()) {
               this.queueChunkUploads.poll().run();
               bl2 = true;
               bl = true;
            }
         }

         if (l == 0L || !bl2) {
            break;
         }

         m = l - System.nanoTime();
      } while (m >= 0L);

      return bl;
   }

   public boolean N(RenderChunk renderChunk) {
      renderChunk.getLockCompileTask().lock();

      boolean bl;
      try {
         ChunkCompileTaskGenerator chunkCompileTaskGenerator = renderChunk.makeCompileTaskChunk();
         chunkCompileTaskGenerator.addFinishRunnable(new ChunkRenderDispatcher.RemoveChunkTask(this, chunkCompileTaskGenerator));
         boolean bl2 = this.queueChunkUpdates.offer(chunkCompileTaskGenerator);
         if (!bl2) {
            chunkCompileTaskGenerator.finish();
         }

         bl = bl2;
      } finally {
         renderChunk.getLockCompileTask().unlock();
      }

      return bl;
   }

   public boolean updateChunkNow(RenderChunk chunkRenderer) {
      chunkRenderer.getLockCompileTask().lock();

      boolean bl;
      try {
         ChunkCompileTaskGenerator chunkCompileTaskGenerator = chunkRenderer.makeCompileTaskChunk();

         try {
            this.renderWorker.processTask(chunkCompileTaskGenerator);
         } catch (InterruptedException interruptedException) {
         }

         bl = true;
      } finally {
         chunkRenderer.getLockCompileTask().unlock();
      }

      return bl;
   }

   public void stopChunkUpdates() {
      this.clearChunkUpdates();

      while (this.runChunkUploads(0L)) {
      }

      ArrayList arrayList = Lists.newArrayList();

      while (arrayList.size() != 5) {
         try {
            arrayList.add(this.allocateRenderBuilder());
         } catch (InterruptedException interruptedException) {
         }
      }

      this.queueFreeRenderBuilders.addAll(arrayList);
   }

   public void freeRenderBuilder(RegionRenderCacheBuilder regionRenderCacheBuilder) {
      this.queueFreeRenderBuilders.add(regionRenderCacheBuilder);
   }

   public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException {
      return this.queueFreeRenderBuilders.take();
   }

   public ChunkCompileTaskGenerator getNextChunkUpdate() throws InterruptedException {
      return this.queueChunkUpdates.take();
   }

   public boolean p(RenderChunk renderChunk) {
      renderChunk.getLockCompileTask().lock();

      boolean bl;
      try {
         ChunkCompileTaskGenerator chunkCompileTaskGenerator = renderChunk.makeCompileTaskTransparency();
         if (chunkCompileTaskGenerator == null) {
            return true;
         }

         chunkCompileTaskGenerator.addFinishRunnable(new ChunkRenderDispatcher.RemoveTransparencyTask(this, chunkCompileTaskGenerator));
         bl = this.queueChunkUpdates.offer(chunkCompileTaskGenerator);
      } finally {
         renderChunk.getLockCompileTask().unlock();
      }

      return bl;
   }

   public ListenableFuture<Object> uploadChunk(
      final EnumWorldBlockLayer player, final WorldRenderer worldRenderer, final RenderChunk chunkRenderer, final CompiledChunk compiledChunkIn
   ) {
      if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
         if (OpenGlHelper.useVbo()) {
            this.uploadVertexBuffer(worldRenderer, chunkRenderer.getVertexBufferByLayer(player.ordinal()));
         } else {
            this.uploadDisplayList(worldRenderer, ((ListedRenderChunk)chunkRenderer).getDisplayList(player, compiledChunkIn), chunkRenderer);
         }

         worldRenderer.setTranslation(0.0, 0.0, 0.0);
         return Futures.immediateFuture(null);
      } else {
         ListenableFutureTask listenableFutureTask = ListenableFutureTask.create(new Runnable() {
            @Override
            public void run() {
               ChunkRenderDispatcher.this.uploadChunk(player, worldRenderer, chunkRenderer, compiledChunkIn);
            }
         }, null);
         synchronized (this.queueChunkUploads) {
            this.queueChunkUploads.add(listenableFutureTask);
            return listenableFutureTask;
         }
      }
   }

   private void uploadDisplayList(WorldRenderer worldRenderer, int i, RenderChunk chunkRenderer) {
      GL11.glNewList(i, 4864);
      GlStateManager.pushMatrix();
      chunkRenderer.multModelviewMatrix();
      this.worldVertexUploader.draw(worldRenderer);
      GlStateManager.popMatrix();
      GL11.glEndList();
   }

   private void uploadVertexBuffer(WorldRenderer worldRenderer, VertexBuffer vertexBufferIn) {
      this.vertexUploader.setVertexBuffer(vertexBufferIn);
      this.vertexUploader.draw(worldRenderer);
   }

   public void clearChunkUpdates() {
      while (!this.queueChunkUpdates.isEmpty()) {
         ChunkCompileTaskGenerator chunkCompileTaskGenerator = this.queueChunkUpdates.poll();
         if (chunkCompileTaskGenerator != null) {
            chunkCompileTaskGenerator.finish();
         }
      }
   }

   public static BlockingQueue access$000(ChunkRenderDispatcher chunkRenderDispatcher) {
      return chunkRenderDispatcher.queueChunkUpdates;
   }

   public static class RemoveChunkTask implements Runnable {
      public final ChunkCompileTaskGenerator m;
      public final ChunkRenderDispatcher h;

      public RemoveChunkTask(ChunkRenderDispatcher chunkRenderDispatcher, ChunkCompileTaskGenerator chunkCompileTaskGenerator) {
         this.h = chunkRenderDispatcher;
         this.m = chunkCompileTaskGenerator;
      }

      @Override
      public void run() {
         ChunkRenderDispatcher.access$000(this.h).remove(this.m);
      }
   }

   public static class RemoveTransparencyTask implements Runnable {
      public final ChunkCompileTaskGenerator a;
      public final ChunkRenderDispatcher T;

      public RemoveTransparencyTask(ChunkRenderDispatcher chunkRenderDispatcher, ChunkCompileTaskGenerator chunkCompileTaskGenerator) {
         this.T = chunkRenderDispatcher;
         this.a = chunkCompileTaskGenerator;
      }

      @Override
      public void run() {
         ChunkRenderDispatcher.access$000(this.T).remove(this.a);
      }
   }
}
