package bypass.via.platform;

import com.viaversion.viaversion.api.platform.PlatformTask;
import java.util.concurrent.Future;

public class FutureTaskId implements PlatformTask<Future<?>> {
   private final Future<?> D;

   public FutureTaskId(Future<?> future) {
      this.D = future;
   }

   public Future<?> getObject() {
      return this.D;
   }

   @Override
   public void cancel() {
      this.D.cancel(false);
   }
}
