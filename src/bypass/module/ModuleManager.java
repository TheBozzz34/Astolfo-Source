package bypass.module;

import bypass.Astolfo;
import bypass.config.ConfigFiles;
import bypass.manager.Loadable;
import bypass.manager.Manager;
import bypass.util.collection.ShapedMap;
import java.util.ArrayList;

public class ModuleManager extends Manager<ModuleInfo> implements Loadable {
   public final ShapedMap<Class<? extends ModuleInfo>, ModuleInfo> byClass = new ShapedMap<>();
   public static double positionDivisor = 1.0;
   public static int modelViewMatrixMode = 6969;
   public static Module caveFinder;
   public static Module noSlow;
   public static Module streamerMode;
   public static Module camera;
   public static Module noTitle;
   public static Module noRender;
   public static Module scoreboard;
   public static Module inventory;
   public static Module xray;
   public static Module infiniteReach;
   public static Module noEffect;
   public static Module antiObsidian;
   public static Module noMouseIntersect;
   public static Module worldColor;
   public static Module itemPhysics;
   public static Module moreParticles;

   @Override
   public void load() {
   }

   @Override
   public void save() {
      ConfigFiles.save(ConfigFiles.serializeModules(), Astolfo.INSTANCE.getConfigName());
   }

   public ModuleInfo getByName(String string) {
      for (ModuleInfo moduleInfo : this.items) {
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

      for (ModuleInfo moduleInfo : this.items) {
         if (moduleInfo instanceof Module && moduleInfo.getCategory() == category) {
            arrayList.add((Module)moduleInfo);
         }
      }

      return arrayList;
   }

   public static void setPositionDivisor(double d) {
      positionDivisor = d;
   }

   public static void setModelViewMatrixMode(int i) {
      modelViewMatrixMode = i;
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
