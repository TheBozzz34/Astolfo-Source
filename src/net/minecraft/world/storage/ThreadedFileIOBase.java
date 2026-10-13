package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;

public class ThreadedFileIOBase implements Runnable {
   private static final ThreadedFileIOBase threadedIOInstance = new ThreadedFileIOBase();
   private List<IThreadedFileIO> threadedIOQueue = Collections.synchronizedList(Lists.newArrayList());
   private volatile long writeQueuedCounter;
   private volatile long savedIOCounter;
   private volatile boolean isThreadWaiting;
   private static final long a = 22515376679672L;

   private ThreadedFileIOBase() {
      Thread var3 = new Thread(this, "File IO Thread");
      var3.setPriority(1);
      var3.start();
   }

   public static ThreadedFileIOBase getThreadedIOInstance() {
      return threadedIOInstance;
   }

   @Override
   public void run() {
      while (true) {
         this.processQueue();
      }
   }

   private void processQueue() {
      for (int var4 = 0; var4 < this.threadedIOQueue.size(); var4++) {
         IThreadedFileIO var5 = this.threadedIOQueue.get(var4);
         boolean var6 = var5.writeNextIO();
         if (!var6) {
            this.threadedIOQueue.remove(var4--);
            this.savedIOCounter++;
         }

         try {
            Thread.sleep(this.isThreadWaiting ? 0L : 10L);
         } catch (InterruptedException var9) {
            var9.printStackTrace();
         }
      }

      if (this.threadedIOQueue.isEmpty()) {
         try {
            Thread.sleep(25L);
         } catch (InterruptedException var8) {
            var8.printStackTrace();
         }
      }
   }

   public void queueIO(IThreadedFileIO var1) {
      if (!this.threadedIOQueue.contains(var1)) {
         this.writeQueuedCounter++;
         this.threadedIOQueue.add(var1);
      }
   }

   public void waitForFinish() throws InterruptedException {
      this.isThreadWaiting = true;

      while (this.writeQueuedCounter != this.savedIOCounter) {
         Thread.sleep(10L);
      }

      this.isThreadWaiting = false;
   }
}
