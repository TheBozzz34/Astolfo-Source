package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance {
   private final BaseAttributeMap attributeMap;
   private final IAttribute genericAttribute;
   private final Map<Integer, Set<AttributeModifier>> mapByOperation = Maps.newHashMap();
   private final Map<String, Set<AttributeModifier>> mapByName = Maps.newHashMap();
   private final Map<UUID, AttributeModifier> mapByUUID = Maps.newHashMap();
   private double baseValue;
   private boolean needsUpdate = true;
   private double cachedValue;
   private static final long a = 57671382977394L;

   public ModifiableAttributeInstance(BaseAttributeMap var1, IAttribute var2) {
      this.attributeMap = var1;
      this.genericAttribute = var2;
      this.baseValue = var2.getDefaultValue();

      for (int var6 = 0; var6 < 3; var6++) {
         this.mapByOperation.put(var6, Sets.newHashSet());
      }
   }

   @Override
   public IAttribute getAttribute() {
      return this.genericAttribute;
   }

   @Override
   public double getBaseValue() {
      return this.baseValue;
   }

   @Override
   public void setBaseValue(double var1) {
      if (var1 != this.getBaseValue()) {
         this.baseValue = var1;
         this.flagForUpdate();
      }
   }

   @Override
   public Collection<AttributeModifier> getModifiersByOperation(int var1) {
      return this.mapByOperation.get(var1);
   }

   @Override
   public Collection<AttributeModifier> func_111122_c() {
      HashSet var4 = Sets.newHashSet();

      for (int var5 = 0; var5 < 3; var5++) {
         var4.addAll(this.getModifiersByOperation(var5));
      }

      return var4;
   }

   @Override
   public AttributeModifier getModifier(UUID var1) {
      return this.mapByUUID.get(var1);
   }

   @Override
   public boolean hasModifier(AttributeModifier var1) {
      return this.mapByUUID.get(var1.getID()) != null;
   }

   @Override
   public void applyModifier(AttributeModifier var1) {
      if (this.getModifier(var1.getID()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      }

      Set var6 = this.mapByName.get(var1.getName());
      if (var6 == null) {
         var6 = Sets.newHashSet();
         this.mapByName.put(var1.getName(), var6);
      }

      this.mapByOperation.get(var1.getOperation()).add(var1);
      var6.add(var1);
      this.mapByUUID.put(var1.getID(), var1);
      this.flagForUpdate();
   }

   public void flagForUpdate() {
      this.needsUpdate = true;
      this.attributeMap.func_180794_a(this);
   }

   @Override
   public void removeModifier(AttributeModifier var1) {
      for (int var5 = 0; var5 < 3; var5++) {
         Set var6 = this.mapByOperation.get(var5);
         var6.remove(var1);
      }

      Set var7 = this.mapByName.get(var1.getName());
      if (var7 != null) {
         var7.remove(var1);
         if (var7.isEmpty()) {
            this.mapByName.remove(var1.getName());
         }
      }

      this.mapByUUID.remove(var1.getID());
      this.flagForUpdate();
   }

   @Override
   public void removeAllModifiers() {
      Collection var4 = this.func_111122_c();
      if (var4 != null) {
         for (Object var6$raw : Lists.newArrayList(var4)) {
            AttributeModifier var6 = (AttributeModifier)var6$raw;
            this.removeModifier(var6);
         }
      }
   }

   @Override
   public double getAttributeValue() {
      if (this.needsUpdate) {
         this.cachedValue = this.computeValue();
         this.needsUpdate = false;
      }

      return this.cachedValue;
   }

   private double computeValue() {
      double var4 = this.getBaseValue();

      for (AttributeModifier var7 : this.func_180375_b(0)) {
         var4 += var7.getAmount();
      }

      double var10 = var4;

      for (AttributeModifier var9 : this.func_180375_b(1)) {
         var10 += var4 * var9.getAmount();
      }

      for (AttributeModifier var12 : this.func_180375_b(2)) {
         var10 *= 1.0 + var12.getAmount();
      }

      return this.genericAttribute.clampValue(var10);
   }

   private Collection<AttributeModifier> func_180375_b(int var1) {
      HashSet var5 = Sets.newHashSet(this.getModifiersByOperation(var1));

      for (IAttribute var6 = this.genericAttribute.func_180372_d(); var6 != null; var6 = var6.func_180372_d()) {
         IAttributeInstance var7 = this.attributeMap.getAttributeInstance(var6);
         if (var7 != null) {
            var5.addAll(var7.getModifiersByOperation(var1));
         }
      }

      return var5;
   }
}
