package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.src.CustomItems;
import net.minecraft.util.ResourceLocation;

public class ItemModelMesher {
   private final Map<Integer, ModelResourceLocation> simpleShapes = Maps.newHashMap();
   private final Map<Integer, IBakedModel> simpleShapesCache = Maps.newHashMap();
   private final Map<Item, ItemMeshDefinition> shapers = Maps.newHashMap();
   private final ModelManager modelManager;
   private static final long a = 56581327128507L;
   private static final long b = 2576319923299024912L;

   public ItemModelMesher(ModelManager var1) {
      this.modelManager = var1;
   }

   public TextureAtlasSprite getParticleIcon(Item var1) {
      return this.getParticleIcon(var1, 0);
   }

   public TextureAtlasSprite getParticleIcon(Item var1, int var2) {
      return this.getItemModel(new ItemStack(var1, 1, var2)).getParticleTexture();
   }

   public IBakedModel getItemModel(ItemStack var1) {
      Item var5 = var1.getItem();
      IBakedModel var6 = this.getItemModel(var5, this.getMetadata(var1));
      if (var6 == null) {
         ItemMeshDefinition var7 = this.shapers.get(var5);
         if (var7 != null) {
            var6 = this.modelManager.getModel(var7.getModelLocation(var1));
         }
      }

      if (var6 == null) {
         var6 = this.modelManager.getMissingModel();
      }

      if (Config.isCustomItems()) {
         var6 = CustomItems.r(var1, var6, (ResourceLocation)null, true);
      }

      return var6;
   }

   public int getMetadata(ItemStack var1) {
      return var1.isItemStackDamageable() ? 0 : var1.getMetadata();
   }

   public IBakedModel getItemModel(Item var1, int var2) {
      return this.simpleShapesCache.get(this.getIndex(var1, var2));
   }

   private int getIndex(Item var1, int var2) {
      return Item.getIdFromItem(var1) << (int)b | var2;
   }

   public void register(Item var1, int var2, ModelResourceLocation var3) {
      this.simpleShapes.put(this.getIndex(var1, var2), var3);
      this.simpleShapesCache.put(this.getIndex(var1, var2), this.modelManager.getModel(var3));
   }

   public void register(Item var1, ItemMeshDefinition var2) {
      this.shapers.put(var1, var2);
   }

   public ModelManager getModelManager() {
      return this.modelManager;
   }

   public void rebuildCache() {
      this.simpleShapesCache.clear();

      for (Entry var5 : this.simpleShapes.entrySet()) {
         this.simpleShapesCache.put((Integer)var5.getKey(), this.modelManager.getModel((ModelResourceLocation)var5.getValue()));
      }
   }
}
