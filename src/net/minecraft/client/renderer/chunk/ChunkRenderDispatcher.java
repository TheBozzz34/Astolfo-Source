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
      for (int var4 = 0; var4 < 2; var4++) {
         ChunkRenderWorker var5 = new ChunkRenderWorker(this);
         Thread var6 = threadFactory.newThread(var5);
         var6.start();
         this.listThreadedWorkers.add(var5);
      }

      for (int var7 = 0; var7 < 5; var7++) {
         this.queueFreeRenderBuilders.add(new RegionRenderCacheBuilder());
      }

      this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
   }

   public String getDebugInfo() {
      return String.format("pC: %03d, pU: %1d, aB: %1d", this.queueChunkUpdates.size(), this.queueChunkUploads.size(), this.queueFreeRenderBuilders.size());
   }

   public boolean runChunkUploads(long var1) {
      boolean var6 = false;

      long var11;
      do {
         boolean var7 = false;
         synchronized (this.queueChunkUploads) {
            if (!this.queueChunkUploads.isEmpty()) {
               this.queueChunkUploads.poll().run();
               var7 = true;
               var6 = true;
            }
         }

         if (var1 == 0L || !var7) {
            break;
         }

         var11 = var1 - System.nanoTime();
      } while (var11 >= 0L);

      return var6;
   }

   public boolean N(RenderChunk var1) {
      var1.getLockCompileTask().lock();

      boolean var5;
      try {
         ChunkCompileTaskGenerator var6 = var1.makeCompileTaskChunk();
         var6.addFinishRunnable(new ChunkRenderDispatcher.RemoveChunkTask(this, var6));
         boolean var7 = this.queueChunkUpdates.offer(var6);
         if (!var7) {
            var6.finish();
         }

         var5 = var7;
      } finally {
         var1.getLockCompileTask().unlock();
      }

      return var5;
   }

   public boolean updateChunkNow(RenderChunk var1) {
      var1.getLockCompileTask().lock();

      boolean var2;
      try {
         ChunkCompileTaskGenerator var3 = var1.makeCompileTaskChunk();

         try {
            this.renderWorker.processTask(var3);
         } catch (InterruptedException var8) {
         }

         var2 = true;
      } finally {
         var1.getLockCompileTask().unlock();
      }

      return var2;
   }

   public void stopChunkUpdates() {
      this.clearChunkUpdates();

      while (this.runChunkUploads(0L)) {
      }

      ArrayList var4 = Lists.newArrayList();

      while (var4.size() != 5) {
         try {
            var4.add(this.allocateRenderBuilder());
         } catch (InterruptedException var6) {
         }
      }

      this.queueFreeRenderBuilders.addAll(var4);
   }

   public void freeRenderBuilder(RegionRenderCacheBuilder var1) {
      this.queueFreeRenderBuilders.add(var1);
   }

   public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException {
      return this.queueFreeRenderBuilders.take();
   }

   public ChunkCompileTaskGenerator getNextChunkUpdate() throws InterruptedException {
      return this.queueChunkUpdates.take();
   }

   public boolean p(RenderChunk var1) {
      var1.getLockCompileTask().lock();

      boolean var5;
      try {
         ChunkCompileTaskGenerator var6 = var1.makeCompileTaskTransparency();
         if (var6 == null) {
            return true;
         }

         var6.addFinishRunnable(new ChunkRenderDispatcher.RemoveTransparencyTask(this, var6));
         var5 = this.queueChunkUpdates.offer(var6);
      } finally {
         var1.getLockCompileTask().unlock();
      }

      return var5;
   }

   public ListenableFuture<Object> uploadChunk(final EnumWorldBlockLayer var1, final WorldRenderer var2, final RenderChunk var3, final CompiledChunk var4) {
      if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
         if (OpenGlHelper.useVbo()) {
            this.uploadVertexBuffer(var2, var3.getVertexBufferByLayer(var1.ordinal()));
         } else {
            this.uploadDisplayList(var2, ((ListedRenderChunk)var3).getDisplayList(var1, var4), var3);
         }

         var2.setTranslation(0.0, 0.0, 0.0);
         return Futures.immediateFuture(null);
      } else {
         ListenableFutureTask var8 = ListenableFutureTask.create(new Runnable() {
            @Override
            public void run() {
               ChunkRenderDispatcher.this.uploadChunk(var1, var2, var3, var4);
            }
         }, null);
         synchronized (this.queueChunkUploads) {
            this.queueChunkUploads.add(var8);
            return var8;
         }
      }
   }

   private void uploadDisplayList(WorldRenderer var1, int var2, RenderChunk var3) {
      GL11.glNewList(var2, 4864);
      GlStateManager.pushMatrix();
      var3.multModelviewMatrix();
      this.worldVertexUploader.draw(var1);
      GlStateManager.popMatrix();
      GL11.glEndList();
   }

   private void uploadVertexBuffer(WorldRenderer var1, VertexBuffer var2) {
      this.vertexUploader.setVertexBuffer(var2);
      this.vertexUploader.draw(var1);
   }

   public void clearChunkUpdates() {
      while (!this.queueChunkUpdates.isEmpty()) {
         ChunkCompileTaskGenerator var4 = this.queueChunkUpdates.poll();
         if (var4 != null) {
            var4.finish();
         }
      }
   }

   public static BlockingQueue access$000(ChunkRenderDispatcher var0) {
      return var0.queueChunkUpdates;
   }

   public static class RemoveChunkTask implements Runnable {
      public final ChunkCompileTaskGenerator m;
      public final ChunkRenderDispatcher h;

      public RemoveChunkTask(ChunkRenderDispatcher var1, ChunkCompileTaskGenerator var2) {
         this.h = var1;
         this.m = var2;
      }

      @Override
      public void run() {
         ChunkRenderDispatcher.access$000(this.h).remove(this.m);
      }
   }

   public static class RemoveTransparencyTask implements Runnable {
      public final ChunkCompileTaskGenerator a;
      public final ChunkRenderDispatcher T;

      public RemoveTransparencyTask(ChunkRenderDispatcher var1, ChunkCompileTaskGenerator var2) {
         this.T = var1;
         this.a = var2;
      }

      @Override
      public void run() {
         ChunkRenderDispatcher.access$000(this.T).remove(this.a);
      }
   }
}
