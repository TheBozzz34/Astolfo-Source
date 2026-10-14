package bypass.loader.check;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class DefaultCheckRegistry implements CheckRegistry {
   private final CopyOnWriteArrayList<StageCheck> C;
   private final CopyOnWriteArrayList<VerdictProcessor> p;
   private final VerdictResolver y;

   public DefaultCheckRegistry(Set<StageCheck> set, VerdictResolver verdictResolver) {
      Objects.requireNonNull(verdictResolver, "");
      this.C = new CopyOnWriteArrayList<>();
      this.p = new CopyOnWriteArrayList<>();
      this.y = verdictResolver;
      if (this.p(set)) {
         this.C.addAll(set.stream().sorted().collect(Collectors.toList()));
      }
   }

   private boolean p(Set<StageCheck> set) {
      return set != null && !set.isEmpty();
   }

   @Override
   public Verdict u(CheckRequest checkRequest) {
      return this.y.v(checkRequest.Y(this.P(), this.i()));
   }

   @Override
   public boolean S(StageCheck stageCheck) {
      Objects.requireNonNull(stageCheck, "");
      if (this.C.addIfAbsent(stageCheck)) {
         Collections.sort(this.C);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean L(StageCheck stageCheck) {
      Objects.requireNonNull(stageCheck, "");
      return this.C.remove(stageCheck);
   }

   @Override
   public List<StageCheck> P() {
      return Collections.unmodifiableList(this.C);
   }

   @Override
   public boolean h(VerdictProcessor verdictProcessor) {
      return this.p.addIfAbsent(verdictProcessor);
   }

   @Override
   public boolean P(VerdictProcessor verdictProcessor) {
      return this.p.remove(verdictProcessor);
   }

   @Override
   public List<VerdictProcessor> i() {
      return Collections.unmodifiableList(this.p);
   }
}
