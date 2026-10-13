package shadersmod.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShaderProfile {
   private String name = null;
   private Map<String, String> mapOptionValues = new HashMap<>();
   private Set<String> disabledPrograms = new HashSet<>();
   private static final long a = 88101129767981L;

   public ShaderProfile(String var1) {
      this.name = var1;
   }

   public String getName() {
      return this.name;
   }

   public void addOptionValue(String var1, String var2) {
      this.mapOptionValues.put(var1, var2);
   }

   public void addOptionValues(ShaderProfile var1) {
      if (var1 != null) {
         this.mapOptionValues.putAll(var1.mapOptionValues);
      }
   }

   public void applyOptionValues(ShaderOption[] var1) {
      for (int var5 = 0; var5 < var1.length; var5++) {
         ShaderOption var6 = var1[var5];
         String var7 = var6.getName();
         String var8 = this.mapOptionValues.get(var7);
         if (var8 != null) {
            var6.setValue(var8);
         }
      }
   }

   public String[] getOptions() {
      Set var1 = this.mapOptionValues.keySet();
      return (java.lang.String[])(var1.toArray(new String[var1.size()]));
   }

   public String getValue(String var1) {
      return this.mapOptionValues.get(var1);
   }

   public void addDisabledProgram(String var1) {
      this.disabledPrograms.add(var1);
   }

   public Collection<String> getDisabledPrograms() {
      return new HashSet<>(this.disabledPrograms);
   }

   public void addDisabledPrograms(Collection<String> var1) {
      this.disabledPrograms.addAll(var1);
   }

   public boolean isProgramDisabled(String var1) {
      return this.disabledPrograms.contains(var1);
   }
}
