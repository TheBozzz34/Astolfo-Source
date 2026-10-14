package net.minecraft.entity;

import java.util.Collection;
import java.util.UUID;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SharedMonsterAttributes {
   private static final Logger logger = LogManager.getLogger();
   public static final IAttribute maxHealth = new RangedAttribute((IAttribute)null, "generic.maxHealth", 20.0, 0.0, 1024.0)
      .setDescription("Max Health")
      .setShouldWatch(true);
   public static final IAttribute followRange = new RangedAttribute((IAttribute)null, "generic.followRange", 32.0, 0.0, 2048.0).setDescription("Follow Range");
   public static final IAttribute knockbackResistance = new RangedAttribute((IAttribute)null, "generic.knockbackResistance", 0.0, 0.0, 1.0)
      .setDescription("Knockback Resistance");
   public static final IAttribute movementSpeed = new RangedAttribute((IAttribute)null, "generic.movementSpeed", 0.7F, 0.0, 1024.0)
      .setDescription("Movement Speed")
      .setShouldWatch(true);
   public static final IAttribute attackDamage = new RangedAttribute((IAttribute)null, "generic.attackDamage", 2.0, 0.0, 2048.0);

   public static NBTTagList writeBaseAttributeMapToNBT(BaseAttributeMap map) {
      NBTTagList nbtTagList = new NBTTagList();

      for (IAttributeInstance iAttributeInstance : map.getAllAttributes()) {
         nbtTagList.appendTag(writeAttributeInstanceToNBT(iAttributeInstance));
      }

      return nbtTagList;
   }

   private static NBTTagCompound writeAttributeInstanceToNBT(IAttributeInstance instance) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      IAttribute iAttribute = instance.getAttribute();
      nbtTagCompound.setString("Name", iAttribute.getAttributeUnlocalizedName());
      nbtTagCompound.setDouble("Base", instance.getBaseValue());
      Collection collection = instance.func_111122_c();
      if (collection != null && !collection.isEmpty()) {
         NBTTagList nbtTagList = new NBTTagList();

         for (Object object : collection) {
            AttributeModifier attributeModifier = (AttributeModifier)object;
            if (attributeModifier.isSaved()) {
               nbtTagList.appendTag(writeAttributeModifierToNBT(attributeModifier));
            }
         }

         nbtTagCompound.setTag("Modifiers", nbtTagList);
      }

      return nbtTagCompound;
   }

   private static NBTTagCompound writeAttributeModifierToNBT(AttributeModifier modifier) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      nbtTagCompound.setString("Name", modifier.getName());
      nbtTagCompound.setDouble("Amount", modifier.getAmount());
      nbtTagCompound.setInteger("Operation", modifier.getOperation());
      nbtTagCompound.setLong("UUIDMost", modifier.getID().getMostSignificantBits());
      nbtTagCompound.setLong("UUIDLeast", modifier.getID().getLeastSignificantBits());
      return nbtTagCompound;
   }

   public static void setAttributeModifiers(BaseAttributeMap map, NBTTagList list) {
      for (int i = 0; i < list.tagCount(); i++) {
         NBTTagCompound nbtTagCompound = list.getCompoundTagAt(i);
         IAttributeInstance iAttributeInstance = map.getAttributeInstanceByName(nbtTagCompound.getString("Name"));
         if (iAttributeInstance != null) {
            applyModifiersToAttributeInstance(iAttributeInstance, nbtTagCompound);
         } else {
            logger.warn("Ignoring unknown attribute '" + nbtTagCompound.getString("Name") + "'");
         }
      }
   }

   private static void applyModifiersToAttributeInstance(IAttributeInstance instance, NBTTagCompound compound) {
      instance.setBaseValue(compound.getDouble("Base"));
      if (compound.hasKey("Modifiers", 9)) {
         NBTTagList nbtTagList = compound.getTagList("Modifiers", 10);

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            AttributeModifier attributeModifier = readAttributeModifierFromNBT(nbtTagList.getCompoundTagAt(i));
            if (attributeModifier != null) {
               AttributeModifier attributeModifier2 = instance.getModifier(attributeModifier.getID());
               if (attributeModifier2 != null) {
                  instance.removeModifier(attributeModifier2);
               }

               instance.applyModifier(attributeModifier);
            }
         }
      }
   }

   public static AttributeModifier readAttributeModifierFromNBT(NBTTagCompound compound) {
      UUID uuid = new UUID(compound.getLong("UUIDMost"), compound.getLong("UUIDLeast"));

      try {
         return new AttributeModifier(uuid, compound.getString("Name"), compound.getDouble("Amount"), compound.getInteger("Operation"));
      } catch (Exception exception) {
         logger.warn("Unable to create attribute: " + exception.getMessage());
         return null;
      }
   }
}
