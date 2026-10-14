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
      Thread thread = new Thread(this, "File IO Thread");
      thread.setPriority(1);
      thread.start();
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
      for (int i = 0; i < this.threadedIOQueue.size(); i++) {
         IThreadedFileIO iThreadedFileIO = this.threadedIOQueue.get(i);
         boolean bl = iThreadedFileIO.writeNextIO();
         if (!bl) {
            this.threadedIOQueue.remove(i--);
            this.savedIOCounter++;
         }

         try {
            Thread.sleep(this.isThreadWaiting ? 0L : 10L);
         } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
         }
      }

      if (this.threadedIOQueue.isEmpty()) {
         try {
            Thread.sleep(25L);
         } catch (InterruptedException interruptedException2) {
            interruptedException2.printStackTrace();
         }
      }
   }

   public void queueIO(IThreadedFileIO iThreadedFileIO) {
      if (!this.threadedIOQueue.contains(iThreadedFileIO)) {
         this.writeQueuedCounter++;
         this.threadedIOQueue.add(iThreadedFileIO);
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
