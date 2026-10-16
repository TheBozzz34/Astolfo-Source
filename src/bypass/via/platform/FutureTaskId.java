package bypass.via.platform;

import com.viaversion.viaversion.api.platform.PlatformTask;
import java.util.concurrent.Future;

public class FutureTaskId implements PlatformTask<Future<?>> {
   private final Future<?> future;

   public FutureTaskId(Future<?> future) {
      this.future = future;
   }

   public Future<?> getObject() {
      return this.future;
   }

   @Override
   public void cancel() {
      this.future.cancel(false);
   }
}
