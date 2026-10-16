package bypass.loader.check;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class DefaultCheckRegistry implements CheckRegistry {
   private final CopyOnWriteArrayList<StageCheck> checks;
   private final CopyOnWriteArrayList<VerdictProcessor> processors;
   private final VerdictResolver resolver;

   public DefaultCheckRegistry(Set<StageCheck> set, VerdictResolver verdictResolver) {
      Objects.requireNonNull(verdictResolver, "");
      this.checks = new CopyOnWriteArrayList<>();
      this.processors = new CopyOnWriteArrayList<>();
      this.resolver = verdictResolver;
      if (this.isNonEmpty(set)) {
         this.checks.addAll(set.stream().sorted().collect(Collectors.toList()));
      }
   }

   private boolean isNonEmpty(Set<StageCheck> set) {
      return set != null && !set.isEmpty();
   }

   @Override
   public Verdict resolve(CheckRequest checkRequest) {
      return this.resolver.resolve(checkRequest.createContext(this.getChecks(), this.getProcessors()));
   }

   @Override
   public boolean addCheck(StageCheck stageCheck) {
      Objects.requireNonNull(stageCheck, "");
      if (this.checks.addIfAbsent(stageCheck)) {
         Collections.sort(this.checks);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean removeCheck(StageCheck stageCheck) {
      Objects.requireNonNull(stageCheck, "");
      return this.checks.remove(stageCheck);
   }

   @Override
   public List<StageCheck> getChecks() {
      return Collections.unmodifiableList(this.checks);
   }

   @Override
   public boolean addProcessor(VerdictProcessor verdictProcessor) {
      return this.processors.addIfAbsent(verdictProcessor);
   }

   @Override
   public boolean removeProcessor(VerdictProcessor verdictProcessor) {
      return this.processors.remove(verdictProcessor);
   }

   @Override
   public List<VerdictProcessor> getProcessors() {
      return Collections.unmodifiableList(this.processors);
   }
}
