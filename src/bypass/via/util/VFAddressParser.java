package bypass.via.util;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class VFAddressParser {
   public Integer protocol;
   public String viaSuffix;
   public String serverAddress;
   public String viaOptions;

   public VFAddressParser parse(String string) {
      return this.parseWithSuffix(string, "viafabric");
   }

   public String getFullSuffix() {
      return this.viaOptions != null && !this.viaOptions.isEmpty() ? this.viaOptions + "." + this.viaSuffix : this.viaSuffix;
   }

   public VFAddressParser parseWithSuffix(String string, String string2) {
      string = StringUtils.removeEnd(string, ".");
      String string3 = StringUtils.removeEnd(string, "." + string2);
      if (string3.equals(string)) {
         this.serverAddress = string;
         return this;
      }

      boolean bl = false;
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();

      for (String string4 : Lists.reverse(Arrays.asList(string3.split(Pattern.quote("."))))) {
         if (!bl && this.parseOption(string4)) {
            arrayList.add(string4);
         } else {
            bl = true;
            arrayList2.add(string4);
         }
      }

      this.serverAddress = String.join(".", Lists.reverse(arrayList2));
      this.viaOptions = String.join(".", Lists.reverse(arrayList));
      this.viaSuffix = string2;
      return this;
   }

   public boolean parseOption(String string) {
      if (string.length() < 2) {
         return false;
      }

      String string2;
      if (string.startsWith("_")) {
         string2 = String.valueOf(string.charAt(1));
      } else {
         if (string.charAt(1) != '_') {
            return false;
         }

         string2 = String.valueOf(string.charAt(0));
      }

      String string3 = string.substring(2);
      if ("v".equals(string2)) {
         this.parseProtocol(string3);
      }

      return true;
   }

   public void parseProtocol(String string) {
      this.protocol = Ints.tryParse(string);
      if (this.protocol == null) {
         ProtocolVersion protocolVersion = ProtocolVersion.getClosest(string.replace("_", "."));
         if (protocolVersion != null) {
            this.protocol = protocolVersion.getVersion();
         }
      }
   }
}
