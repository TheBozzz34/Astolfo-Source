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

   public ModifiableAttributeInstance(BaseAttributeMap attributeMapIn, IAttribute genericAttributeIn) {
      this.attributeMap = attributeMapIn;
      this.genericAttribute = genericAttributeIn;
      this.baseValue = genericAttributeIn.getDefaultValue();

      for (int i = 0; i < 3; i++) {
         this.mapByOperation.put(i, Sets.newHashSet());
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
   public void setBaseValue(double baseValue) {
      if (baseValue != this.getBaseValue()) {
         this.baseValue = baseValue;
         this.flagForUpdate();
      }
   }

   @Override
   public Collection<AttributeModifier> getModifiersByOperation(int operation) {
      return this.mapByOperation.get(operation);
   }

   @Override
   public Collection<AttributeModifier> func_111122_c() {
      HashSet hashSet = Sets.newHashSet();

      for (int i = 0; i < 3; i++) {
         hashSet.addAll(this.getModifiersByOperation(i));
      }

      return hashSet;
   }

   @Override
   public AttributeModifier getModifier(UUID uuid) {
      return this.mapByUUID.get(uuid);
   }

   @Override
   public boolean hasModifier(AttributeModifier modifier) {
      return this.mapByUUID.get(modifier.getID()) != null;
   }

   @Override
   public void applyModifier(AttributeModifier modifier) {
      if (this.getModifier(modifier.getID()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      }

      Set set = this.mapByName.get(modifier.getName());
      if (set == null) {
         set = Sets.newHashSet();
         this.mapByName.put(modifier.getName(), set);
      }

      this.mapByOperation.get(modifier.getOperation()).add(modifier);
      set.add(modifier);
      this.mapByUUID.put(modifier.getID(), modifier);
      this.flagForUpdate();
   }

   public void flagForUpdate() {
      this.needsUpdate = true;
      this.attributeMap.func_180794_a(this);
   }

   @Override
   public void removeModifier(AttributeModifier modifier) {
      for (int i = 0; i < 3; i++) {
         Set set = this.mapByOperation.get(i);
         set.remove(modifier);
      }

      Set set2 = this.mapByName.get(modifier.getName());
      if (set2 != null) {
         set2.remove(modifier);
         if (set2.isEmpty()) {
            this.mapByName.remove(modifier.getName());
         }
      }

      this.mapByUUID.remove(modifier.getID());
      this.flagForUpdate();
   }

   @Override
   public void removeAllModifiers() {
      Collection collection = this.func_111122_c();
      if (collection != null) {
         for (Object object : Lists.newArrayList(collection)) {
            AttributeModifier attributeModifier = (AttributeModifier)object;
            this.removeModifier(attributeModifier);
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
      double d = this.getBaseValue();

      for (AttributeModifier attributeModifier : this.func_180375_b(0)) {
         d += attributeModifier.getAmount();
      }

      double e = d;

      for (AttributeModifier attributeModifier2 : this.func_180375_b(1)) {
         e += d * attributeModifier2.getAmount();
      }

      for (AttributeModifier attributeModifier3 : this.func_180375_b(2)) {
         e *= 1.0 + attributeModifier3.getAmount();
      }

      return this.genericAttribute.clampValue(e);
   }

   private Collection<AttributeModifier> func_180375_b(int operation) {
      HashSet hashSet = Sets.newHashSet(this.getModifiersByOperation(operation));

      for (IAttribute iAttribute = this.genericAttribute.func_180372_d(); iAttribute != null; iAttribute = iAttribute.func_180372_d()) {
         IAttributeInstance iAttributeInstance = this.attributeMap.getAttributeInstance(iAttribute);
         if (iAttributeInstance != null) {
            hashSet.addAll(iAttributeInstance.getModifiersByOperation(operation));
         }
      }

      return hashSet;
   }
}
