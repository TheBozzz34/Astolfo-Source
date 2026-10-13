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

   public IAttributeInstance getAttributeInstance(IAttribute var1) {
      return this.attributes.get(var1);
   }

   public IAttributeInstance getAttributeInstanceByName(String var1) {
      return this.attributesByName.get(var1);
   }

   public IAttributeInstance registerAttribute(IAttribute var1) {
      if (this.attributesByName.containsKey(var1.getAttributeUnlocalizedName())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      }

      IAttributeInstance var5 = this.func_180376_c(var1);
      this.attributesByName.put(var1.getAttributeUnlocalizedName(), var5);
      this.attributes.put(var1, var5);

      for (IAttribute var6 = var1.func_180372_d(); var6 != null; var6 = var6.func_180372_d()) {
         this.field_180377_c.put(var6, var1);
      }

      return var5;
   }

   public abstract IAttributeInstance func_180376_c(IAttribute var1);

   public Collection<IAttributeInstance> getAllAttributes() {
      return this.attributesByName.values();
   }

   public void func_180794_a(IAttributeInstance var1) {
   }

   public void removeAttributeModifiers(Multimap<String, AttributeModifier> var1) {
      for (Entry var6 : var1.entries()) {
         IAttributeInstance var7 = this.getAttributeInstanceByName((String)var6.getKey());
         if (var7 != null) {
            var7.removeModifier((AttributeModifier)var6.getValue());
         }
      }
   }

   public void applyAttributeModifiers(Multimap<String, AttributeModifier> var1) {
      for (Entry var6 : var1.entries()) {
         IAttributeInstance var7 = this.getAttributeInstanceByName((String)var6.getKey());
         if (var7 != null) {
            var7.removeModifier((AttributeModifier)var6.getValue());
            var7.applyModifier((AttributeModifier)var6.getValue());
         }
      }
   }
}
