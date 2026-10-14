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

   public static void FMLClientHandler_trackBrokenTexture(ResourceLocation loc, String message) {
      if (!Reflector.FMLClientHandler_trackBrokenTexture.exists()) {
         Object object = Reflector.call(Reflector.FMLClientHandler_instance);
         Reflector.call(object, Reflector.FMLClientHandler_trackBrokenTexture, loc, message);
      }
   }

   public static void FMLClientHandler_trackMissingTexture(ResourceLocation loc) {
      if (!Reflector.FMLClientHandler_trackMissingTexture.exists()) {
         Object object = Reflector.call(Reflector.FMLClientHandler_instance);
         Reflector.call(object, Reflector.FMLClientHandler_trackMissingTexture, loc);
      }
   }

   public static void putLaunchBlackboard(String key, Object value) {
      Map map = (Map)Reflector.getFieldValue(Reflector.Launch_blackboard);
      if (map != null) {
         map.put(key, value);
      }
   }

   public static boolean renderFirstPersonHand(RenderGlobal renderGlobal, float partialTicks, int pass) {
      return !Reflector.ForgeHooksClient_renderFirstPersonHand.exists()
         ? false
         : Reflector.callBoolean(Reflector.ForgeHooksClient_renderFirstPersonHand, renderGlobal, partialTicks, pass);
   }

   public static InputStream getOptiFineResourceStream(String path) {
      if (!Reflector.OptiFineClassTransformer_instance.exists()) {
         return null;
      }

      Object object = Reflector.getFieldValue(Reflector.OptiFineClassTransformer_instance);
      if (object == null) {
         return null;
      }

      if (path.startsWith("/")) {
         path = path.substring(1);
      }

      byte[] bytes = (byte[])Reflector.call(object, Reflector.OptiFineClassTransformer_getOptiFineResource, path);
      return bytes == null ? null : new ByteArrayInputStream(bytes);
   }

   public static boolean blockHasTileEntity(IBlockState state) {
      Block block = state.getBlock();
      return !Reflector.ForgeBlock_hasTileEntity.exists() ? block.hasTileEntity() : Reflector.callBoolean(block, Reflector.ForgeBlock_hasTileEntity, state);
   }

   public static boolean isItemDamaged(ItemStack stack) {
      return !Reflector.ForgeItem_showDurabilityBar.exists()
         ? stack.isItemDamaged()
         : Reflector.callBoolean(stack.getItem(), Reflector.ForgeItem_showDurabilityBar, stack);
   }

   public static boolean armorHasOverlay(ItemArmor itemArmor, ItemStack itemStack) {
      int i = itemArmor.getColor(itemStack);
      return i != (int)b;
   }
}
