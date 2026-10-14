package bypass.loader.check;

import java.util.ArrayList;
import java.util.List;

public class JvmArgumentPatterns {
   private final List<String> c = new ArrayList<>();

   public JvmArgumentPatterns() {
      this.c.add("-javaagent");
      this.c.add("-agentlib");
      this.c.add("-agentpath");
      this.c.add("-Xrun");
      this.c.add("-Xdebug");
      this.c.add("-Xrunjdwp");
      this.c.add("-Xbootclasspath");
   }

   public List<String> N() {
      return this.c;
   }
}
