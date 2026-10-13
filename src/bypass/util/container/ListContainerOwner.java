package bypass.util.container;

import java.util.List;

public class ListContainerOwner<T> extends InitializedContainerOwner<T> {
   private final List<T> y;

   public ListContainerOwner(List<T> var1) {
      this.y = var1;
      this.onInit();
   }

   @Override
   public void onInit() {
      try {
         this.y.stream().forEach(var1 -> this.T.q((T[])(new Object[]{var1})));
      } catch (Exception var2) {
      }
   }
}
