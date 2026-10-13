package bypass.module;

import bypass.Astolfo;
import bypass.config.ConfigFiles;
import bypass.manager.Loadable;
import bypass.manager.Manager;
import bypass.util.collection.ShapedMap;
import java.util.ArrayList;

public class ModuleManager extends Manager<ModuleInfo> implements Loadable {
   public final ShapedMap<Class<? extends ModuleInfo>, ModuleInfo> byClass = new ShapedMap<>();
   public static double v = 1.0;
   public static int e = 6969;
   public static Module t;
   public static Module D;
   public static Module G;
   public static Module k;
   public static Module S;
   public static Module I;
   public static Module i;
   public static Module r;
   public static Module s;
   public static Module Z;
   public static Module X;
   public static Module B;
   public static Module Q;
   public static Module M;
   public static Module J;
   public static Module N;
   private static final long a = 16331876270956L;

   @Override
   public void load() {
   }

   @Override
   public void a() {
      ConfigFiles.save(ConfigFiles.serializeModules(), Astolfo.INSTANCE.getConfigName());
   }

   public ModuleInfo getByName(String var1) {
      for (ModuleInfo var6 : this.C) {
         if (var6.getName().equalsIgnoreCase(var1)) {
            return var6;
         }
      }

      return null;
   }

   public Module getModule(String var1) {
      return (Module)this.getByName(var1);
   }

   public ArrayList<Module> getModules(Category var1) {
      ArrayList var6 = new ArrayList();

      for (ModuleInfo var8 : this.C) {
         if (var8 instanceof Module && var8.getCategory() == var1) {
            var6.add((Module)var8);
         }
      }

      return var6;
   }

   public static void C(double var0) {
      v = var0;
   }

   public static void k(int var0) {
      e = var0;
   }

   public <T extends ModuleInfo> T get(Class<T> var1) {
      return (T)var1.cast(this.byClass.get(var1));
   }

   public boolean canAdd(ModuleInfo var1) {
      this.byClass.put((Class<? extends ModuleInfo>)var1.getClass(), var1);
      return true;
   }

   @Override
   public boolean canRemove(Object var1) {
      return this.byClass.remove(var1) != null;
   }
}
