package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

public class TileEntityItemStackRenderer {
   public static TileEntityItemStackRenderer instance = new TileEntityItemStackRenderer();
   private TileEntityChest field_147717_b = new TileEntityChest(0);
   private TileEntityChest field_147718_c = new TileEntityChest(1);
   private TileEntityEnderChest enderChest = new TileEntityEnderChest();
   private TileEntityBanner banner = new TileEntityBanner();
   private TileEntitySkull skull = new TileEntitySkull();
   private static final long a = 81243457928505L;

   public void renderByItem(ItemStack var1) {
      if (var1.getItem() == Items.banner) {
         this.banner.setItemValues(var1);
         TileEntityRendererDispatcher.instance.renderTileEntityAt(this.banner, 0.0, 0.0, 0.0, 0.0F);
      } else if (var1.getItem() == Items.skull) {
         GameProfile var5 = null;
         if (var1.hasTagCompound()) {
            NBTTagCompound var6 = var1.getTagCompound();
            if (var6.hasKey("SkullOwner", 10)) {
               var5 = NBTUtil.readGameProfileFromNBT(var6.getCompoundTag("SkullOwner"));
            } else if (var6.hasKey("SkullOwner", 8) && var6.getString("SkullOwner").length() > 0) {
               GameProfile var7 = new GameProfile((UUID)null, var6.getString("SkullOwner"));
               var5 = TileEntitySkull.updateGameprofile(var7);
               var6.removeTag("SkullOwner");
               var6.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), var5));
            }
         }

         if (TileEntitySkullRenderer.instance != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, 0.0F, -0.5F);
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            GlStateManager.disableCull();
            TileEntitySkullRenderer.instance.renderSkull(0.0F, 0.0F, 0.0F, EnumFacing.UP, 0.0F, var1.getMetadata(), var5, -1);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
         }
      } else {
         Block var8 = Block.getBlockFromItem(var1.getItem());
         if (var8 == Blocks.ender_chest) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.enderChest, 0.0, 0.0, 0.0, 0.0F);
         } else if (var8 == Blocks.trapped_chest) {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.field_147718_c, 0.0, 0.0, 0.0, 0.0F);
         } else {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.field_147717_b, 0.0, 0.0, 0.0, 0.0F);
         }
      }
   }
}
