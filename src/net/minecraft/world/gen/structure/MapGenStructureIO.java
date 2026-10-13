package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapGenStructureIO {
   private static final Logger logger = LogManager.getLogger();
   private static Map<String, Class<? extends StructureStart>> startNameToClassMap = Maps.newHashMap();
   private static Map<Class<? extends StructureStart>, String> startClassToNameMap = Maps.newHashMap();
   private static Map<String, Class<? extends StructureComponent>> componentNameToClassMap = Maps.newHashMap();
   private static Map<Class<? extends StructureComponent>, String> componentClassToNameMap = Maps.newHashMap();
   private static final long a = 61532071068083L;

   private static void registerStructure(Class<? extends StructureStart> var0, String var1) {
      startNameToClassMap.put(var1, var0);
      startClassToNameMap.put(var0, var1);
   }

   public static void registerStructureComponent(Class<? extends StructureComponent> var0, String var1) {
      componentNameToClassMap.put(var1, var0);
      componentClassToNameMap.put(var0, var1);
   }

   public static String getStructureStartName(StructureStart var0) {
      return startClassToNameMap.get(var0.getClass());
   }

   public static String getStructureComponentName(StructureComponent var0) {
      return componentClassToNameMap.get(var0.getClass());
   }

   public static StructureStart getStructureStart(NBTTagCompound var0, World var1) {
      StructureStart var5 = null;

      try {
         Class var6 = startNameToClassMap.get(var0.getString("id"));
         if (var6 != null) {
            var5 = (StructureStart)var6.newInstance();
         }
      } catch (Exception var7) {
         logger.warn("Failed Start with id " + var0.getString("id"));
         var7.printStackTrace();
      }

      if (var5 != null) {
         var5.readStructureComponentsFromNBT(var1, var0);
      } else {
         logger.warn("Skipping Structure with id " + var0.getString("id"));
      }

      return var5;
   }

   public static StructureComponent getStructureComponent(NBTTagCompound var0, World var1) {
      StructureComponent var5 = null;

      try {
         Class var6 = componentNameToClassMap.get(var0.getString("id"));
         if (var6 != null) {
            var5 = (StructureComponent)var6.newInstance();
         }
      } catch (Exception var7) {
         logger.warn("Failed Piece with id " + var0.getString("id"));
         var7.printStackTrace();
      }

      if (var5 != null) {
         var5.readStructureBaseNBT(var1, var0);
      } else {
         logger.warn("Skipping Piece with id " + var0.getString("id"));
      }

      return var5;
   }

   static {
      registerStructure(StructureMineshaftStart.class, "Mineshaft");
      registerStructure(MapGenVillage.Start.class, "Village");
      registerStructure(MapGenNetherBridge.Start.class, "Fortress");
      registerStructure(MapGenStronghold.Start.class, "Stronghold");
      registerStructure(MapGenScatteredFeature.Start.class, "Temple");
      registerStructure(StructureOceanMonument.StartMonument.class, "Monument");
      StructureMineshaftPieces.registerStructurePieces();
      StructureVillagePieces.registerVillagePieces();
      StructureNetherBridgePieces.registerNetherFortressPieces();
      StructureStrongholdPieces.registerStrongholdPieces();
      ComponentScatteredFeaturePieces.registerScatteredFeaturePieces();
      StructureOceanMonumentPieces.registerOceanMonumentPieces();
   }
}
