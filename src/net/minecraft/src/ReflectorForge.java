package net.minecraft.src;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ReflectorForge {
   private static final long a = 15325865753558L;
   private static final long b = 6593999964567764991L;

   public static void FMLClientHandler_trackBrokenTexture(ResourceLocation var0, String var1) {
      if (!Reflector.FMLClientHandler_trackBrokenTexture.exists()) {
         Object var5 = Reflector.call(Reflector.FMLClientHandler_instance);
         Reflector.call(var5, Reflector.FMLClientHandler_trackBrokenTexture, var0, var1);
      }
   }

   public static void FMLClientHandler_trackMissingTexture(ResourceLocation var0) {
      if (!Reflector.FMLClientHandler_trackMissingTexture.exists()) {
         Object var4 = Reflector.call(Reflector.FMLClientHandler_instance);
         Reflector.call(var4, Reflector.FMLClientHandler_trackMissingTexture, var0);
      }
   }

   public static void putLaunchBlackboard(String var0, Object var1) {
      Map var5 = (Map)Reflector.getFieldValue(Reflector.Launch_blackboard);
      if (var5 != null) {
         var5.put(var0, var1);
      }
   }

   public static boolean renderFirstPersonHand(RenderGlobal var0, float var1, int var2) {
      return !Reflector.ForgeHooksClient_renderFirstPersonHand.exists()
         ? false
         : Reflector.callBoolean(Reflector.ForgeHooksClient_renderFirstPersonHand, var0, var1, var2);
   }

   public static InputStream getOptiFineResourceStream(String var0) {
      if (!Reflector.OptiFineClassTransformer_instance.exists()) {
         return null;
      }

      Object var4 = Reflector.getFieldValue(Reflector.OptiFineClassTransformer_instance);
      if (var4 == null) {
         return null;
      }

      if (var0.startsWith("/")) {
         var0 = var0.substring(1);
      }

      byte[] var5 = (byte[])Reflector.call(var4, Reflector.OptiFineClassTransformer_getOptiFineResource, var0);
      return var5 == null ? null : new ByteArrayInputStream(var5);
   }

   public static boolean blockHasTileEntity(IBlockState var0) {
      Block var4 = var0.getBlock();
      return !Reflector.ForgeBlock_hasTileEntity.exists() ? var4.hasTileEntity() : Reflector.callBoolean(var4, Reflector.ForgeBlock_hasTileEntity, var0);
   }

   public static boolean isItemDamaged(ItemStack var0) {
      return !Reflector.ForgeItem_showDurabilityBar.exists()
         ? var0.isItemDamaged()
         : Reflector.callBoolean(var0.getItem(), Reflector.ForgeItem_showDurabilityBar, var0);
   }

   public static boolean armorHasOverlay(ItemArmor var0, ItemStack var1) {
      int var5 = var0.getColor(var1);
      return var5 != (int)b;
   }
}
