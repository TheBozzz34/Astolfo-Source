package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.management.LowerStringMap;

public class ServersideAttributeMap extends BaseAttributeMap {
   private final Set<IAttributeInstance> attributeInstanceSet = Sets.newHashSet();
   public final Map<String, IAttributeInstance> descriptionToAttributeInstanceMap = new LowerStringMap<>();

   public ModifiableAttributeInstance getAttributeInstance(IAttribute attribute) {
      return (ModifiableAttributeInstance)super.getAttributeInstance(attribute);
   }

   public ModifiableAttributeInstance getAttributeInstanceByName(String attributeName) {
      IAttributeInstance iAttributeInstance = super.getAttributeInstanceByName(attributeName);
      if (iAttributeInstance == null) {
         iAttributeInstance = this.descriptionToAttributeInstanceMap.get(attributeName);
      }

      return (ModifiableAttributeInstance)iAttributeInstance;
   }

   @Override
   public IAttributeInstance registerAttribute(IAttribute attribute) {
      IAttributeInstance iAttributeInstance = super.registerAttribute(attribute);
      if (attribute instanceof RangedAttribute && ((RangedAttribute)attribute).getDescription() != null) {
         this.descriptionToAttributeInstanceMap.put(((RangedAttribute)attribute).getDescription(), iAttributeInstance);
      }

      return iAttributeInstance;
   }

   @Override
   public IAttributeInstance func_180376_c(IAttribute attribute) {
      return new ModifiableAttributeInstance(this, attribute);
   }

   @Override
   public void func_180794_a(IAttributeInstance instance) {
      if (instance.getAttribute().getShouldWatch()) {
         this.attributeInstanceSet.add(instance);
      }

      for (IAttribute iAttribute : this.field_180377_c.get(instance.getAttribute())) {
         ModifiableAttributeInstance modifiableAttributeInstance = this.getAttributeInstance(iAttribute);
         if (modifiableAttributeInstance != null) {
            modifiableAttributeInstance.flagForUpdate();
         }
      }
   }

   public Set<IAttributeInstance> getAttributeInstanceSet() {
      return this.attributeInstanceSet;
   }

   public Collection<IAttributeInstance> getWatchedAttributes() {
      HashSet hashSet = Sets.newHashSet();

      for (IAttributeInstance iAttributeInstance : this.getAllAttributes()) {
         if (iAttributeInstance.getAttribute().getShouldWatch()) {
            hashSet.add(iAttributeInstance);
         }
      }

      return hashSet;
   }
}
