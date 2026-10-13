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
   private static final long a = 127024407943985L;

   public static NBTTagList writeBaseAttributeMapToNBT(BaseAttributeMap var0) {
      NBTTagList var4 = new NBTTagList();

      for (IAttributeInstance var6 : var0.getAllAttributes()) {
         var4.appendTag(writeAttributeInstanceToNBT(var6));
      }

      return var4;
   }

   private static NBTTagCompound writeAttributeInstanceToNBT(IAttributeInstance var0) {
      NBTTagCompound var4 = new NBTTagCompound();
      IAttribute var5 = var0.getAttribute();
      var4.setString("Name", var5.getAttributeUnlocalizedName());
      var4.setDouble("Base", var0.getBaseValue());
      Collection var6 = var0.func_111122_c();
      if (var6 != null && !var6.isEmpty()) {
         NBTTagList var7 = new NBTTagList();

         for (Object var9$raw : var6) {
            AttributeModifier var9 = (AttributeModifier)var9$raw;
            if (var9.isSaved()) {
               var7.appendTag(writeAttributeModifierToNBT(var9));
            }
         }

         var4.setTag("Modifiers", var7);
      }

      return var4;
   }

   private static NBTTagCompound writeAttributeModifierToNBT(AttributeModifier var0) {
      NBTTagCompound var3 = new NBTTagCompound();
      var3.setString("Name", var0.getName());
      var3.setDouble("Amount", var0.getAmount());
      var3.setInteger("Operation", var0.getOperation());
      var3.setLong("UUIDMost", var0.getID().getMostSignificantBits());
      var3.setLong("UUIDLeast", var0.getID().getLeastSignificantBits());
      return var3;
   }

   public static void setAttributeModifiers(BaseAttributeMap var0, NBTTagList var1) {
      for (int var5 = 0; var5 < var1.tagCount(); var5++) {
         NBTTagCompound var6 = var1.getCompoundTagAt(var5);
         IAttributeInstance var7 = var0.getAttributeInstanceByName(var6.getString("Name"));
         if (var7 != null) {
            applyModifiersToAttributeInstance(var7, var6);
         } else {
            logger.warn("Ignoring unknown attribute '" + var6.getString("Name") + "'");
         }
      }
   }

   private static void applyModifiersToAttributeInstance(IAttributeInstance var0, NBTTagCompound var1) {
      var0.setBaseValue(var1.getDouble("Base"));
      if (var1.hasKey("Modifiers", 9)) {
         NBTTagList var5 = var1.getTagList("Modifiers", 10);

         for (int var6 = 0; var6 < var5.tagCount(); var6++) {
            AttributeModifier var7 = readAttributeModifierFromNBT(var5.getCompoundTagAt(var6));
            if (var7 != null) {
               AttributeModifier var8 = var0.getModifier(var7.getID());
               if (var8 != null) {
                  var0.removeModifier(var8);
               }

               var0.applyModifier(var7);
            }
         }
      }
   }

   public static AttributeModifier readAttributeModifierFromNBT(NBTTagCompound var0) {
      UUID var3 = new UUID(var0.getLong("UUIDMost"), var0.getLong("UUIDLeast"));

      try {
         return new AttributeModifier(var3, var0.getString("Name"), var0.getDouble("Amount"), var0.getInteger("Operation"));
      } catch (Exception var5) {
         logger.warn("Unable to create attribute: " + var5.getMessage());
         return null;
      }
   }
}
