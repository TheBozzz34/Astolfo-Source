package bypass.via.util;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class VFAddressParser {
   public Integer l;
   public String m;
   public String p;
   public String M;

   public VFAddressParser c(String string) {
      return this.d(string, "viafabric");
   }

   public String v() {
      return this.M != null && !this.M.isEmpty() ? this.M + "." + this.m : this.m;
   }

   public VFAddressParser d(String string, String string2) {
      string = StringUtils.removeEnd(string, ".");
      String string3 = StringUtils.removeEnd(string, "." + string2);
      if (string3.equals(string)) {
         this.p = string;
         return this;
      }

      boolean bl = false;
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();

      for (String string4 : Lists.reverse(Arrays.asList(string3.split(Pattern.quote("."))))) {
         if (!bl && this.n(string4)) {
            arrayList.add(string4);
         } else {
            bl = true;
            arrayList2.add(string4);
         }
      }

      this.p = String.join(".", Lists.reverse(arrayList2));
      this.M = String.join(".", Lists.reverse(arrayList));
      this.m = string2;
      return this;
   }

   public boolean n(String string) {
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
         this.M(string3);
      }

      return true;
   }

   public void M(String string) {
      this.l = Ints.tryParse(string);
      if (this.l == null) {
         ProtocolVersion protocolVersion = ProtocolVersion.getClosest(string.replace("_", "."));
         if (protocolVersion != null) {
            this.l = protocolVersion.getVersion();
         }
      }
   }
}
