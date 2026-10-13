package net.minecraft.client.resources;

import com.google.gson.JsonParseException;
import java.io.IOException;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackListEntryDefault extends ResourcePackListEntry {
   private static final Logger logger = LogManager.getLogger();
   private final IResourcePack field_148320_d = this.mc.getResourcePackRepository().rprDefaultResourcePack;
   private final ResourceLocation resourcePackIcon;
   private static final long a = 63455722881498L;

   public ResourcePackListEntryDefault(GuiScreenResourcePacks var1) {
      super(var1);

      DynamicTexture var4;
      try {
         var4 = new DynamicTexture(this.field_148320_d.getPackImage());
      } catch (IOException var6) {
         var4 = TextureUtil.missingTexture;
      }

      this.resourcePackIcon = this.mc.getTextureManager().getDynamicTextureLocation("texturepackicon", var4);
   }

   @Override
   public int func_183019_a() {
      return 1;
   }

   @Override
   public String func_148311_a() {
      try {
         PackMetadataSection var4 = this.field_148320_d.getPackMetadata(this.mc.getResourcePackRepository().rprMetadataSerializer, "pack");
         if (var4 != null) {
            return var4.getPackDescription().getFormattedText();
         }
      } catch (JsonParseException var5) {
         logger.error("Couldn't load metadata info", var5);
      } catch (IOException var6) {
         logger.error("Couldn't load metadata info", var6);
      }

      return EnumChatFormatting.RED + "Missing pack.mcmeta :(";
   }

   @Override
   public boolean func_148309_e() {
      return false;
   }

   @Override
   public boolean func_148308_f() {
      return false;
   }

   @Override
   public boolean func_148314_g() {
      return false;
   }

   @Override
   public boolean func_148307_h() {
      return false;
   }

   @Override
   public String func_148312_b() {
      return "Default";
   }

   @Override
   public void func_148313_c() {
      this.mc.getTextureManager().bindTexture(this.resourcePackIcon);
   }

   @Override
   public boolean func_148310_d() {
      return false;
   }
}
