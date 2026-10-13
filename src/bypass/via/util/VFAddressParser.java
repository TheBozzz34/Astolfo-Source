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
   private static final long a = 73866748604494L;
   private static final long c = -4307704811790270369L;

   public VFAddressParser c(String var1) {
      return this.d(var1, "viafabric");
   }

   public String v() {
      return this.M != null && !this.M.isEmpty() ? this.M + "." + this.m : this.m;
   }

   public VFAddressParser d(String var1, String var2) {
      var1 = StringUtils.removeEnd(var1, ".");
      String var6 = StringUtils.removeEnd(var1, "." + var2);
      if (var6.equals(var1)) {
         this.p = var1;
         return this;
      }

      boolean var7 = false;
      ArrayList var8 = new ArrayList();
      ArrayList var9 = new ArrayList();

      for (String var11 : Lists.reverse(Arrays.asList(var6.split(Pattern.quote("."))))) {
         if (!var7 && this.n(var11)) {
            var8.add(var11);
         } else {
            var7 = true;
            var9.add(var11);
         }
      }

      this.p = String.join(".", Lists.reverse(var9));
      this.M = String.join(".", Lists.reverse(var8));
      this.m = var2;
      return this;
   }

   public boolean n(String var1) {
      if (var1.length() < 2) {
         return false;
      }

      String var5;
      if (var1.startsWith("_")) {
         var5 = String.valueOf(var1.charAt(1));
      } else {
         if (var1.charAt(1) != (int)c) {
            return false;
         }

         var5 = String.valueOf(var1.charAt(0));
      }

      String var6 = var1.substring(2);
      if ("v".equals(var5)) {
         this.M(var6);
      }

      return true;
   }

   public void M(String var1) {
      this.l = Ints.tryParse(var1);
      if (this.l == null) {
         ProtocolVersion var4 = ProtocolVersion.getClosest(var1.replace("_", "."));
         if (var4 != null) {
            this.l = var4.getVersion();
         }
      }
   }
}
