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

   @Override
   public void load() {
   }

   @Override
   public void a() {
      ConfigFiles.save(ConfigFiles.serializeModules(), Astolfo.INSTANCE.getConfigName());
   }

   public ModuleInfo getByName(String string) {
      for (ModuleInfo moduleInfo : this.C) {
         if (moduleInfo.getName().equalsIgnoreCase(string)) {
            return moduleInfo;
         }
      }

      return null;
   }

   public Module getModule(String string) {
      return (Module)this.getByName(string);
   }

   public ArrayList<Module> getModules(Category category) {
      ArrayList arrayList = new ArrayList();

      for (ModuleInfo moduleInfo : this.C) {
         if (moduleInfo instanceof Module && moduleInfo.getCategory() == category) {
            arrayList.add((Module)moduleInfo);
         }
      }

      return arrayList;
   }

   public static void C(double d) {
      v = d;
   }

   public static void k(int j) {
      e = j;
   }

   public <T extends ModuleInfo> T get(Class<T> class2) {
      return (T)class2.cast(this.byClass.get(class2));
   }

   public boolean canAdd(ModuleInfo moduleInfo) {
      this.byClass.put((Class<? extends ModuleInfo>)moduleInfo.getClass(), moduleInfo);
      return true;
   }

   @Override
   public boolean canRemove(Object object) {
      return this.byClass.remove(object) != null;
   }
}
