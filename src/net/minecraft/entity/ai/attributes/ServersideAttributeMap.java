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
   private static final long b = 38456955220476L;

   public ModifiableAttributeInstance getAttributeInstance(IAttribute var1) {
      return (ModifiableAttributeInstance)super.getAttributeInstance(var1);
   }

   public ModifiableAttributeInstance getAttributeInstanceByName(String var1) {
      IAttributeInstance var5 = super.getAttributeInstanceByName(var1);
      if (var5 == null) {
         var5 = this.descriptionToAttributeInstanceMap.get(var1);
      }

      return (ModifiableAttributeInstance)var5;
   }

   @Override
   public IAttributeInstance registerAttribute(IAttribute var1) {
      IAttributeInstance var5 = super.registerAttribute(var1);
      if (var1 instanceof RangedAttribute && ((RangedAttribute)var1).getDescription() != null) {
         this.descriptionToAttributeInstanceMap.put(((RangedAttribute)var1).getDescription(), var5);
      }

      return var5;
   }

   @Override
   public IAttributeInstance func_180376_c(IAttribute var1) {
      return new ModifiableAttributeInstance(this, var1);
   }

   @Override
   public void func_180794_a(IAttributeInstance var1) {
      if (var1.getAttribute().getShouldWatch()) {
         this.attributeInstanceSet.add(var1);
      }

      for (IAttribute var6 : this.field_180377_c.get(var1.getAttribute())) {
         ModifiableAttributeInstance var7 = this.getAttributeInstance(var6);
         if (var7 != null) {
            var7.flagForUpdate();
         }
      }
   }

   public Set<IAttributeInstance> getAttributeInstanceSet() {
      return this.attributeInstanceSet;
   }

   public Collection<IAttributeInstance> getWatchedAttributes() {
      HashSet var4 = Sets.newHashSet();

      for (IAttributeInstance var6 : this.getAllAttributes()) {
         if (var6.getAttribute().getShouldWatch()) {
            var4.add(var6);
         }
      }

      return var4;
   }
}
