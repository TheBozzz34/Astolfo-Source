package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;

public class ChunkCompileTaskGenerator {
   private final RenderChunk renderChunk;
   private final ReentrantLock lock = new ReentrantLock();
   private final List<Runnable> listFinishRunnables = Lists.newArrayList();
   private final ChunkCompileTaskGenerator.Type type;
   private RegionRenderCacheBuilder regionRenderCacheBuilder;
   private CompiledChunk compiledChunk;
   private ChunkCompileTaskGenerator.Status status = ChunkCompileTaskGenerator.Status.PENDING;
   private boolean finished;
   private static final long a = 76594464718134L;

   public ChunkCompileTaskGenerator(RenderChunk var1, ChunkCompileTaskGenerator.Type var2) {
      this.renderChunk = var1;
      this.type = var2;
   }

   public ChunkCompileTaskGenerator.Status getStatus() {
      return this.status;
   }

   public RenderChunk getRenderChunk() {
      return this.renderChunk;
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk var1) {
      this.compiledChunk = var1;
   }

   public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
      return this.regionRenderCacheBuilder;
   }

   public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder var1) {
      this.regionRenderCacheBuilder = var1;
   }

   public void setStatus(ChunkCompileTaskGenerator.Status var1) {
      this.lock.lock();

      try {
         this.status = var1;
      } finally {
         this.lock.unlock();
      }
   }

   public void finish() {
      this.lock.lock();

      try {
         if (this.type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK && this.status != ChunkCompileTaskGenerator.Status.DONE) {
            this.renderChunk.setNeedsUpdate(true);
         }

         this.finished = true;
         this.status = ChunkCompileTaskGenerator.Status.DONE;

         for (Runnable var5 : this.listFinishRunnables) {
            var5.run();
         }
      } finally {
         this.lock.unlock();
      }
   }

   public void addFinishRunnable(Runnable var1) {
      this.lock.lock();

      try {
         this.listFinishRunnables.add(var1);
         if (this.finished) {
            var1.run();
         }
      } finally {
         this.lock.unlock();
      }
   }

   public ReentrantLock getLock() {
      return this.lock;
   }

   public ChunkCompileTaskGenerator.Type getType() {
      return this.type;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public enum Status {
      PENDING,
      COMPILING,
      UPLOADING,
      DONE;
   }

   public enum Type {
      REBUILD_CHUNK,
      RESORT_TRANSPARENCY;
   }
}
