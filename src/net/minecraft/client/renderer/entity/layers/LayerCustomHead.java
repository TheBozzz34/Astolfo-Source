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

   public LayerCustomHead(ModelRenderer modelRenderer) {
      this.field_177209_a = modelRenderer;
   }

   @Override
   public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float g, float partialTicks, float h, float f2, float f3, float scale) {
      ItemStack itemStack = entitylivingbaseIn.getCurrentArmor(3);
      if (itemStack != null && itemStack.getItem() != null) {
         Item item = itemStack.getItem();
         Minecraft minecraft = Minecraft.getMinecraft();
         GlStateManager.pushMatrix();
         if (entitylivingbaseIn.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         boolean bl = entitylivingbaseIn instanceof EntityVillager
            || entitylivingbaseIn instanceof EntityZombie && ((EntityZombie)entitylivingbaseIn).isVillager();
         if (!bl && entitylivingbaseIn.isChild()) {
            float f4 = 2.0F;
            float f5 = 1.4F;
            GlStateManager.scale(f5 / f4, f5 / f4, f5 / f4);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
         }

         this.field_177209_a.postRender(0.0625F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (item instanceof ItemBlock) {
            float f6 = 0.625F;
            GlStateManager.translate(0.0F, -0.25F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(f6, -f6, -f6);
            if (bl) {
               GlStateManager.translate(0.0F, 0.1875F, 0.0F);
            }

            minecraft.getItemRenderer().renderItem(entitylivingbaseIn, itemStack, ItemCameraTransforms.TransformType.HEAD);
         } else if (item == Items.skull) {
            float f7 = 1.1875F;
            GlStateManager.scale(f7, -f7, -f7);
            if (bl) {
               GlStateManager.translate(0.0F, 0.0625F, 0.0F);
            }

            GameProfile gameProfile = null;
            if (itemStack.hasTagCompound()) {
               NBTTagCompound nbtTagCompound = itemStack.getTagCompound();
               if (nbtTagCompound.hasKey("SkullOwner", 10)) {
                  gameProfile = NBTUtil.readGameProfileFromNBT(nbtTagCompound.getCompoundTag("SkullOwner"));
               } else if (nbtTagCompound.hasKey("SkullOwner", 8)) {
                  String string = nbtTagCompound.getString("SkullOwner");
                  if (!StringUtils.isNullOrEmpty(string)) {
                     gameProfile = TileEntitySkull.updateGameprofile(new GameProfile((UUID)null, string));
                     nbtTagCompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameProfile));
                  }
               }
            }

            TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, itemStack.getMetadata(), gameProfile, -1);
         }

         GlStateManager.popMatrix();
      }
   }

   @Override
   public boolean shouldCombineTextures() {
      return true;
   }
}
