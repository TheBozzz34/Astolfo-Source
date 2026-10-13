package bypass.loader.check;

import java.util.List;

public interface CheckContext {
   CheckRequest f();

   List<StageCheck> i();

   List<VerdictProcessor> a();
}
