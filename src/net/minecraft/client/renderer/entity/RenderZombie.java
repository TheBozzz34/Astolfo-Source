package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerVillagerArmor;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderZombie extends RenderBiped<EntityZombie> {
   private static final ResourceLocation zombieTextures = new ResourceLocation("textures/entity/zombie/zombie.png");
   private static final ResourceLocation zombieVillagerTextures = new ResourceLocation("textures/entity/zombie/zombie_villager.png");
   private final ModelBiped field_82434_o;
   private final ModelZombieVillager zombieVillagerModel;
   private final List<LayerRenderer<EntityZombie>> field_177121_n;
   private final List<LayerRenderer<EntityZombie>> field_177122_o;
   private static final long e = 64054122814966L;

   public RenderZombie(RenderManager var1) {
      super(var1, new ModelZombie(), 0.5F, 1.0F);
      LayerRenderer var5 = this.layerRenderers.get(0);
      this.field_82434_o = this.modelBipedMain;
      this.zombieVillagerModel = new ModelZombieVillager();
      this.addLayer(new LayerHeldItem(this));
      LayerBipedArmor var6 = new LayerBipedArmor(this) {
         @Override
         public void initArmor() {
            this.modelLeggings = new ModelZombie(0.5F, true);
            this.modelArmor = new ModelZombie(1.0F, true);
         }
      };
      this.addLayer(var6);
      this.field_177122_o = Lists.newArrayList(this.layerRenderers);
      if (var5 instanceof LayerCustomHead) {
         this.removeLayer(var5);
         this.addLayer(new LayerCustomHead(this.zombieVillagerModel.bipedHead));
      }

      this.removeLayer(var6);
      this.addLayer(new LayerVillagerArmor(this));
      this.field_177121_n = Lists.newArrayList(this.layerRenderers);
   }

   public void doRender(EntityZombie var1, double var2, double var4, double var6, float var8, float var9) {
      this.func_82427_a(var1);
      super.doRender(var1, var2, var4, var6, var8, var9);
   }

   public ResourceLocation getEntityTexture(EntityZombie var1) {
      return var1.isVillager() ? zombieVillagerTextures : zombieTextures;
   }

   private void func_82427_a(EntityZombie var1) {
      if (var1.isVillager()) {
         this.mainModel = this.zombieVillagerModel;
         this.layerRenderers = this.field_177121_n;
      } else {
         this.mainModel = this.field_82434_o;
         this.layerRenderers = this.field_177122_o;
      }

      this.modelBipedMain = (ModelBiped)this.mainModel;
   }

   public void rotateCorpse(EntityZombie var1, float var2, float var3, float var4) {
      if (var1.isConverting()) {
         var3 += (float)(Math.cos(var1.ticksExisted * 3.25) * Math.PI * 0.25);
      }

      super.rotateCorpse(var1, var2, var3, var4);
   }
}
