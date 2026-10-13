package net.minecraft.client.gui.inventory;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

public class GuiEditSign extends GuiScreen {
   private TileEntitySign tileSign;
   private int updateCounter;
   private int editLine;
   private GuiButton doneBtn;
   private static final long a = 135560770405129L;

   public GuiEditSign(TileEntitySign var1) {
      this.tileSign = var1;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done")));
      this.tileSign.setEditable(false);
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      NetHandlerPlayClient var4 = this.mc.getNetHandler();
      if (var4 != null) {
         var4.addToSendQueue(new C12PacketUpdateSign(this.tileSign.getPos(), this.tileSign.signText));
      }

      this.tileSign.setEditable(true);
   }

   @Override
   public void updateScreen() {
      this.updateCounter++;
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled && var1.id == 0) {
         this.tileSign.markDirty();
         this.mc.displayGuiScreen((GuiScreen)null);
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      if (var2 == 200) {
         this.editLine = this.editLine - 1 & 3;
      }

      if (var2 == 208 || var2 == 28 || var2 == 156) {
         this.editLine = this.editLine + 1 & 3;
      }

      String var6 = this.tileSign.signText[this.editLine].getUnformattedText();
      if (var2 == 14 && var6.length() > 0) {
         var6 = var6.substring(0, var6.length() - 1);
      }

      if (ChatAllowedCharacters.isAllowedCharacter(var1) && this.fontRendererObj.getStringWidth(var6 + var1) <= 90) {
         var6 = var6 + var1;
      }

      this.tileSign.signText[this.editLine] = new ChatComponentText(var6);
      if (var2 == 1) {
         this.actionPerformed(this.doneBtn);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      this.drawDefaultBackground();
      this.d(this.fontRendererObj, I18n.format("sign.edit"), this.width / 2, 40, 16777215);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate(this.width / 2, 0.0F, 50.0F);
      float var7 = 93.75F;
      GlStateManager.scale(-var7, -var7, -var7);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      Block var8 = this.tileSign.getBlockType();
      if (var8 == Blocks.standing_sign) {
         float var9 = this.tileSign.getBlockMetadata() * 360 / 16.0F;
         GlStateManager.rotate(var9, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -1.0625F, 0.0F);
      } else {
         int var11 = this.tileSign.getBlockMetadata();
         float var10 = 0.0F;
         if (var11 == 2) {
            var10 = 180.0F;
         }

         if (var11 == 4) {
            var10 = 90.0F;
         }

         if (var11 == 5) {
            var10 = -90.0F;
         }

         GlStateManager.rotate(var10, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -1.0625F, 0.0F);
      }

      if (this.updateCounter / 6 % 2 == 0) {
         this.tileSign.lineBeingEdited = this.editLine;
      }

      TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileSign, -0.5, -0.75, -0.5, 0.0F);
      this.tileSign.lineBeingEdited = -1;
      GlStateManager.popMatrix();
      super.drawScreen(var1, var2, var3);
   }
}
