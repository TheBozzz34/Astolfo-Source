package bypass.loader.check;

import java.util.ArrayList;
import java.util.List;

public class ProcessNames {
   private final List<String> processNames = new ArrayList<>();

   public ProcessNames() {
      this.processNames.add("processhacker");
      this.processNames.add("wireshark");
      this.processNames.add("fiddler");
      this.processNames.add("mitmproxy");
      this.processNames.add("charles");
      this.processNames.add("burpsuitecommunity");
      this.processNames.add("tcpviewer");
      this.processNames.add("httpanalyzer");
      this.processNames.add("procexp");
      this.processNames.add("socksa");
      this.processNames.add("\\u6b63\\u5728\\u6355\\u83b7");
   }

   public List<String> getProcessNames() {
      return this.processNames;
   }
}
