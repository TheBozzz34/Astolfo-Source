package bypass.loader.check;

import java.util.ArrayList;
import java.util.List;

public class JvmArgumentPatterns {
   private final List<String> patterns = new ArrayList<>();

   public JvmArgumentPatterns() {
      this.patterns.add("-javaagent");
      this.patterns.add("-agentlib");
      this.patterns.add("-agentpath");
      this.patterns.add("-Xrun");
      this.patterns.add("-Xdebug");
      this.patterns.add("-Xrunjdwp");
      this.patterns.add("-Xbootclasspath");
   }

   public List<String> getPatterns() {
      return this.patterns;
   }
}
