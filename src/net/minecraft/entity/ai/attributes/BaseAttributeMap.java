package net.minecraft.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.management.LowerStringMap;

public abstract class BaseAttributeMap {
   public final Map<IAttribute, IAttributeInstance> attributes = Maps.newHashMap();
   public final Map<String, IAttributeInstance> attributesByName = new LowerStringMap<>();
   public final Multimap<IAttribute, IAttribute> field_180377_c = HashMultimap.create();
   private static final long a = 87995099666996L;

   public IAttributeInstance getAttributeInstance(IAttribute attribute) {
      return this.attributes.get(attribute);
   }

   public IAttributeInstance getAttributeInstanceByName(String attributeName) {
      return this.attributesByName.get(attributeName);
   }

   public IAttributeInstance registerAttribute(IAttribute attribute) {
      if (this.attributesByName.containsKey(attribute.getAttributeUnlocalizedName())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      }

      IAttributeInstance iAttributeInstance = this.func_180376_c(attribute);
      this.attributesByName.put(attribute.getAttributeUnlocalizedName(), iAttributeInstance);
      this.attributes.put(attribute, iAttributeInstance);

      for (IAttribute iAttribute = attribute.func_180372_d(); iAttribute != null; iAttribute = iAttribute.func_180372_d()) {
         this.field_180377_c.put(iAttribute, attribute);
      }

      return iAttributeInstance;
   }

   public abstract IAttributeInstance func_180376_c(IAttribute attribute);

   public Collection<IAttributeInstance> getAllAttributes() {
      return this.attributesByName.values();
   }

   public void func_180794_a(IAttributeInstance instance) {
   }

   public void removeAttributeModifiers(Multimap<String, AttributeModifier> modifiers) {
      for (Entry entry : modifiers.entries()) {
         IAttributeInstance iAttributeInstance = this.getAttributeInstanceByName((String)entry.getKey());
         if (iAttributeInstance != null) {
            iAttributeInstance.removeModifier((AttributeModifier)entry.getValue());
         }
      }
   }

   public void applyAttributeModifiers(Multimap<String, AttributeModifier> modifiers) {
      for (Entry entry : modifiers.entries()) {
         IAttributeInstance iAttributeInstance = this.getAttributeInstanceByName((String)entry.getKey());
         if (iAttributeInstance != null) {
            iAttributeInstance.removeModifier((AttributeModifier)entry.getValue());
            iAttributeInstance.applyModifier((AttributeModifier)entry.getValue());
         }
      }
   }
}
