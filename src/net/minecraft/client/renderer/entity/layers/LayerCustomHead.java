package net.minecraft.client.renderer.entity.layers;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StringUtils;

public class LayerCustomHead implements LayerRenderer<EntityLivingBase> {
   private final ModelRenderer field_177209_a;
   private static final long a = 107471933750943L;

   public LayerCustomHead(ModelRenderer var1) {
      this.field_177209_a = var1;
   }

   @Override
   public void doRenderLayer(EntityLivingBase var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      ItemStack var12 = var1.getCurrentArmor(3);
      if (var12 != null && var12.getItem() != null) {
         Item var13 = var12.getItem();
         Minecraft var14 = Minecraft.getMinecraft();
         GlStateManager.pushMatrix();
         if (var1.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         boolean var15 = var1 instanceof EntityVillager || var1 instanceof EntityZombie && ((EntityZombie)var1).isVillager();
         if (!var15 && var1.isChild()) {
            float var16 = 2.0F;
            float var17 = 1.4F;
            GlStateManager.scale(var17 / var16, var17 / var16, var17 / var16);
            GlStateManager.translate(0.0F, 16.0F * var8, 0.0F);
         }

         this.field_177209_a.postRender(0.0625F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (var13 instanceof ItemBlock) {
            float var20 = 0.625F;
            GlStateManager.translate(0.0F, -0.25F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(var20, -var20, -var20);
            if (var15) {
               GlStateManager.translate(0.0F, 0.1875F, 0.0F);
            }

            var14.getItemRenderer().renderItem(var1, var12, ItemCameraTransforms.TransformType.HEAD);
         } else if (var13 == Items.skull) {
            float var21 = 1.1875F;
            GlStateManager.scale(var21, -var21, -var21);
            if (var15) {
               GlStateManager.translate(0.0F, 0.0625F, 0.0F);
            }

            GameProfile var22 = null;
            if (var12.hasTagCompound()) {
               NBTTagCompound var18 = var12.getTagCompound();
               if (var18.hasKey("SkullOwner", 10)) {
                  var22 = NBTUtil.readGameProfileFromNBT(var18.getCompoundTag("SkullOwner"));
               } else if (var18.hasKey("SkullOwner", 8)) {
                  String var19 = var18.getString("SkullOwner");
                  if (!StringUtils.isNullOrEmpty(var19)) {
                     var22 = TileEntitySkull.updateGameprofile(new GameProfile((UUID)null, var19));
                     var18.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), var22));
                  }
               }
            }

            TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, var12.getMetadata(), var22, -1);
         }

         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
