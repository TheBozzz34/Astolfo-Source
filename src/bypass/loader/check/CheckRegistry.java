package bypass.loader.check;

import java.util.List;

public interface CheckRegistry {
   Verdict u(CheckRequest var1);

   boolean S(StageCheck var1);

   boolean L(StageCheck var1);

   List<StageCheck> P();

   boolean h(VerdictProcessor var1);

   boolean P(VerdictProcessor var1);

   List<VerdictProcessor> i();
}
