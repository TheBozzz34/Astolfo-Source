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
   private static final long a = 137073617500381L;

   public DefaultCheckRegistry(Set<StageCheck> var1, VerdictResolver var2) {
      Objects.requireNonNull(var2, "");
      this.C = new CopyOnWriteArrayList<>();
      this.p = new CopyOnWriteArrayList<>();
      this.y = var2;
      if (this.p(var1)) {
         this.C.addAll(var1.stream().sorted().collect(Collectors.toList()));
      }
   }

   private boolean p(Set<StageCheck> var1) {
      return var1 != null && !var1.isEmpty();
   }

   @Override
   public Verdict u(CheckRequest var1) {
      return this.y.v(var1.Y(this.P(), this.i()));
   }

   @Override
   public boolean S(StageCheck var1) {
      Objects.requireNonNull(var1, "");
      if (this.C.addIfAbsent(var1)) {
         Collections.sort(this.C);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean L(StageCheck var1) {
      Objects.requireNonNull(var1, "");
      return this.C.remove(var1);
   }

   @Override
   public List<StageCheck> P() {
      return Collections.unmodifiableList(this.C);
   }

   @Override
   public boolean h(VerdictProcessor var1) {
      return this.p.addIfAbsent(var1);
   }

   @Override
   public boolean P(VerdictProcessor var1) {
      return this.p.remove(var1);
   }

   @Override
   public List<VerdictProcessor> i() {
      return Collections.unmodifiableList(this.p);
   }
}
