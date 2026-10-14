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

   private static void registerStructure(Class<? extends StructureStart> startClass, String structureName) {
      startNameToClassMap.put(structureName, startClass);
      startClassToNameMap.put(startClass, structureName);
   }

   public static void registerStructureComponent(Class<? extends StructureComponent> componentClass, String componentName) {
      componentNameToClassMap.put(componentName, componentClass);
      componentClassToNameMap.put(componentClass, componentName);
   }

   public static String getStructureStartName(StructureStart startIn) {
      return startClassToNameMap.get(startIn.getClass());
   }

   public static String getStructureComponentName(StructureComponent component) {
      return componentClassToNameMap.get(component.getClass());
   }

   public static StructureStart getStructureStart(NBTTagCompound tagCompound, World worldIn) {
      StructureStart structureStart = null;

      try {
         Class class2 = startNameToClassMap.get(tagCompound.getString("id"));
         if (class2 != null) {
            structureStart = (StructureStart)class2.newInstance();
         }
      } catch (Exception exception) {
         logger.warn("Failed Start with id " + tagCompound.getString("id"));
         exception.printStackTrace();
      }

      if (structureStart != null) {
         structureStart.readStructureComponentsFromNBT(worldIn, tagCompound);
      } else {
         logger.warn("Skipping Structure with id " + tagCompound.getString("id"));
      }

      return structureStart;
   }

   public static StructureComponent getStructureComponent(NBTTagCompound tagCompound, World worldIn) {
      StructureComponent structureComponent = null;

      try {
         Class class2 = componentNameToClassMap.get(tagCompound.getString("id"));
         if (class2 != null) {
            structureComponent = (StructureComponent)class2.newInstance();
         }
      } catch (Exception exception) {
         logger.warn("Failed Piece with id " + tagCompound.getString("id"));
         exception.printStackTrace();
      }

      if (structureComponent != null) {
         structureComponent.readStructureBaseNBT(worldIn, tagCompound);
      } else {
         logger.warn("Skipping Piece with id " + tagCompound.getString("id"));
      }

      return structureComponent;
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
