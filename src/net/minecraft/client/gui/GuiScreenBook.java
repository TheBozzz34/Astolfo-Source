package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiScreenBook extends GuiScreen {
   private static final Logger logger = LogManager.getLogger();
   private static final ResourceLocation bookGuiTextures = new ResourceLocation("textures/gui/book.png");
   private final EntityPlayer editingPlayer;
   private final ItemStack bookObj;
   private final boolean bookIsUnsigned;
   private boolean bookIsModified;
   private boolean bookGettingSigned;
   private int updateCount;
   private int bookImageWidth = 192;
   private int bookImageHeight = 192;
   private int bookTotalPages = 1;
   private int currPage;
   private NBTTagList bookPages;
   private String bookTitle = "";
   private List<IChatComponent> field_175386_A;
   private int field_175387_B = -1;
   private GuiScreenBook.NextPageButton buttonNextPage;
   private GuiScreenBook.NextPageButton buttonPreviousPage;
   private GuiButton buttonDone;
   private GuiButton buttonSign;
   private GuiButton buttonFinalize;
   private GuiButton buttonCancel;
   private static final long b = 15684465452249L;

   public GuiScreenBook(EntityPlayer var1, ItemStack var2, boolean var3) {
      this.editingPlayer = var1;
      this.bookObj = var2;
      this.bookIsUnsigned = var3;
      if (var2.hasTagCompound()) {
         NBTTagCompound var7 = var2.getTagCompound();
         this.bookPages = var7.getTagList("pages", 8);
         if (this.bookPages != null) {
            this.bookPages = (NBTTagList)this.bookPages.copy();
            this.bookTotalPages = this.bookPages.tagCount();
            if (this.bookTotalPages < 1) {
               this.bookTotalPages = 1;
            }
         }
      }

      if (this.bookPages == null && var3) {
         this.bookPages = new NBTTagList();
         this.bookPages.appendTag(new NBTTagString(""));
         this.bookTotalPages = 1;
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.updateCount++;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      if (this.bookIsUnsigned) {
         this.buttonList.add(this.buttonSign = new GuiButton(3, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.signButton")));
         this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.done")));
         this.buttonList
            .add(this.buttonFinalize = new GuiButton(5, this.width / 2 - 100, 4 + this.bookImageHeight, 98, 20, I18n.format("book.finalizeButton")));
         this.buttonList.add(this.buttonCancel = new GuiButton(4, this.width / 2 + 2, 4 + this.bookImageHeight, 98, 20, I18n.format("gui.cancel")));
      } else {
         this.buttonList.add(this.buttonDone = new GuiButton(0, this.width / 2 - 100, 4 + this.bookImageHeight, 200, 20, I18n.format("gui.done")));
      }

      int var4 = (this.width - this.bookImageWidth) / 2;
      byte var5 = 2;
      this.buttonList.add(this.buttonNextPage = new GuiScreenBook.NextPageButton(1, var4 + 120, var5 + 154, true));
      this.buttonList.add(this.buttonPreviousPage = new GuiScreenBook.NextPageButton(2, var4 + 38, var5 + 154, false));
      this.updateButtons();
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   private void updateButtons() {
      this.buttonNextPage.visible = !this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned);
      this.buttonPreviousPage.visible = !this.bookGettingSigned && this.currPage > 0;
      this.buttonDone.visible = !this.bookIsUnsigned || !this.bookGettingSigned;
      if (this.bookIsUnsigned) {
         this.buttonSign.visible = !this.bookGettingSigned;
         this.buttonCancel.visible = this.bookGettingSigned;
         this.buttonFinalize.visible = this.bookGettingSigned;
         this.buttonFinalize.enabled = this.bookTitle.trim().length() > 0;
      }
   }

   private void sendBookToServer(boolean var1) throws IOException {
      if (this.bookIsUnsigned && this.bookIsModified && this.bookPages != null) {
         while (this.bookPages.tagCount() > 1) {
            String var5 = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);
            if (var5.length() != 0) {
               break;
            }

            this.bookPages.removeTag(this.bookPages.tagCount() - 1);
         }

         if (this.bookObj.hasTagCompound()) {
            NBTTagCompound var9 = this.bookObj.getTagCompound();
            var9.setTag("pages", this.bookPages);
         } else {
            this.bookObj.setTagInfo("pages", this.bookPages);
         }

         String var10 = "MC|BEdit";
         if (var1) {
            var10 = "MC|BSign";
            this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getName()));
            this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));

            for (int var6 = 0; var6 < this.bookPages.tagCount(); var6++) {
               String var7 = this.bookPages.getStringTagAt(var6);
               ChatComponentText var8 = new ChatComponentText(var7);
               var7 = IChatComponent.Serializer.componentToJson(var8);
               this.bookPages.set(var6, new NBTTagString(var7));
            }

            this.bookObj.setItem(Items.written_book);
         }

         PacketBuffer var11 = new PacketBuffer(Unpooled.buffer());
         var11.writeItemStackToBuffer(this.bookObj);
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload(var10, var11));
      }
   }

   @Override
   public void actionPerformed(GuiButton var1) throws IOException {
      if (var1.enabled) {
         if (var1.id == 0) {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.sendBookToServer(false);
         } else if (var1.id == 3 && this.bookIsUnsigned) {
            this.bookGettingSigned = true;
         } else if (var1.id == 1) {
            if (this.currPage < this.bookTotalPages - 1) {
               this.currPage++;
            } else if (this.bookIsUnsigned) {
               this.addNewPage();
               if (this.currPage < this.bookTotalPages - 1) {
                  this.currPage++;
               }
            }
         } else if (var1.id == 2) {
            if (this.currPage > 0) {
               this.currPage--;
            }
         } else if (var1.id == 5 && this.bookGettingSigned) {
            this.sendBookToServer(true);
            this.mc.displayGuiScreen((GuiScreen)null);
         } else if (var1.id == 4 && this.bookGettingSigned) {
            this.bookGettingSigned = false;
         }

         this.updateButtons();
      }
   }

   private void addNewPage() {
      if (this.bookPages != null && this.bookPages.tagCount() < 50) {
         this.bookPages.appendTag(new NBTTagString(""));
         this.bookTotalPages++;
         this.bookIsModified = true;
      }
   }

   @Override
   public void keyTyped(char var1, int var2) throws IOException {
      super.keyTyped(var1, var2);
      if (this.bookIsUnsigned) {
         if (this.bookGettingSigned) {
            this.keyTypedInTitle(var1, var2);
         } else {
            this.keyTypedInBook(var1, var2);
         }
      }
   }

   private void keyTypedInBook(char var1, int var2) {
      if (GuiScreen.isKeyComboCtrlV(var2)) {
         this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
      } else {
         switch (var2) {
            case 14:
               String var6 = this.pageGetCurrent();
               if (var6.length() > 0) {
                  this.pageSetCurrent(var6.substring(0, var6.length() - 1));
               }

               return;
            case 28:
            case 156:
               this.pageInsertIntoCurrent("\n");
               return;
            default:
               if (ChatAllowedCharacters.isAllowedCharacter(var1)) {
                  this.pageInsertIntoCurrent(Character.toString(var1));
               }
         }
      }
   }

   private void keyTypedInTitle(char var1, int var2) throws IOException {
      switch (var2) {
         case 14:
            if (!this.bookTitle.isEmpty()) {
               this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
               this.updateButtons();
            }

            return;
         case 28:
         case 156:
            if (!this.bookTitle.isEmpty()) {
               this.sendBookToServer(true);
               this.mc.displayGuiScreen((GuiScreen)null);
            }

            return;
         default:
            if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(var1)) {
               this.bookTitle = this.bookTitle + Character.toString(var1);
               this.updateButtons();
               this.bookIsModified = true;
            }
      }
   }

   private String pageGetCurrent() {
      return this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount() ? this.bookPages.getStringTagAt(this.currPage) : "";
   }

   private void pageSetCurrent(String var1) {
      if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
         this.bookPages.set(this.currPage, new NBTTagString(var1));
         this.bookIsModified = true;
      }
   }

   private void pageInsertIntoCurrent(String var1) {
      String var5 = this.pageGetCurrent();
      String var6 = var5 + var1;
      int var7 = this.fontRendererObj.splitStringWidth(var6 + "" + EnumChatFormatting.BLACK + "_", 118);
      if (var7 <= 128 && var6.length() < 256) {
         this.pageSetCurrent(var6);
      }
   }

   @Override
   public void drawScreen(int var1, int var2, float var3) {
      long var4 = b ^ 14081054391756L;
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(bookGuiTextures);
      int var7 = (this.width - this.bookImageWidth) / 2;
      byte var8 = 2;
      this.drawTexturedModalRect(var7, var8, 0, 0, this.bookImageWidth, this.bookImageHeight);
      if (this.bookGettingSigned) {
         String var9 = this.bookTitle;
         if (this.bookIsUnsigned) {
            if (this.updateCount / 6 % 2 == 0) {
               var9 = var9 + "" + EnumChatFormatting.BLACK + "_";
            } else {
               var9 = var9 + "" + EnumChatFormatting.GRAY + "_";
            }
         }

         String var10 = I18n.format("book.editTitle");
         int var11 = this.fontRendererObj.getStringWidth(var10);
         long var10000 = 132735952594630839L ^ var4;
         this.fontRendererObj.D(var10, var7 + 36 + (116 - var11) / 2, var8 + 16 + 16, 0);
         int var12 = this.fontRendererObj.getStringWidth(var9);
         this.fontRendererObj.D(var9, var7 + 36 + (116 - var12) / 2, var8 + 48, 0);
         String var13 = I18n.format("book.byAuthor", this.editingPlayer.getName());
         int var14 = this.fontRendererObj.getStringWidth((java.lang.String)(var13));
         var10000 = 5221885836547389812L ^ var4;
         this.fontRendererObj.D(EnumChatFormatting.DARK_GRAY + var13, var7 + 36 + (116 - var14) / 2, var8 + 48 + 10, 0);
         String var15 = I18n.format("book.finalizeWarning");
         this.fontRendererObj.drawSplitString(var15, var7 + 36, var8 + 80, 116, 0);
      } else {
         String var17 = I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
         String var18 = "";
         if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            var18 = this.bookPages.getStringTagAt(this.currPage);
         }

         if (this.bookIsUnsigned) {
            if (this.fontRendererObj.U()) {
               var18 = var18 + "_";
            } else if (this.updateCount / 6 % 2 == 0) {
               var18 = var18 + "" + EnumChatFormatting.BLACK + "_";
            } else {
               var18 = var18 + "" + EnumChatFormatting.GRAY + "_";
            }
         } else if (this.field_175387_B != this.currPage) {
            if (ItemEditableBook.validBookTagContents(this.bookObj.getTagCompound())) {
               try {
                  IChatComponent var19 = IChatComponent.Serializer.jsonToComponent(var18);
                  this.field_175386_A = var19 != null ? GuiUtilRenderComponents.splitText(var19, 116, this.fontRendererObj, true, true) : null;
               } catch (JsonParseException var16) {
                  this.field_175386_A = null;
               }
            } else {
               ChatComponentText var20 = new ChatComponentText(EnumChatFormatting.DARK_RED.toString() + "* Invalid book tag *");
               this.field_175386_A = Lists.newArrayList(var20);
            }

            this.field_175387_B = this.currPage;
         }

         int var21 = this.fontRendererObj.getStringWidth(var17);
         this.fontRendererObj.D(var17, var7 - var21 + this.bookImageWidth - 44, var8 + 16, 0);
         if (this.field_175386_A == null) {
            this.fontRendererObj.drawSplitString(var18, var7 + 36, var8 + 16 + 16, 116, 0);
         } else {
            int var22 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.field_175386_A.size());

            for (int var23 = 0; var23 < var22; var23++) {
               IChatComponent var25 = this.field_175386_A.get(var23);
               this.fontRendererObj.D(var25.getUnformattedText(), var7 + 36, var8 + 16 + 16 + var23 * this.fontRendererObj.FONT_HEIGHT, 0);
            }

            IChatComponent var24 = this.func_175385_b(var1, var2);
            if (var24 != null) {
               this.handleComponentHover(var24, var1, var2);
            }
         }
      }

      super.drawScreen(var1, var2, var3);
   }

   @Override
   public void mouseClicked(int var1, int var2, int var3) throws IOException {
      if (var3 == 0) {
         IChatComponent var7 = this.func_175385_b(var1, var2);
         if (this.handleComponentClick(var7)) {
            return;
         }
      }

      super.mouseClicked(var1, var2, var3);
   }

   @Override
   public boolean handleComponentClick(IChatComponent var1) {
      ClickEvent var5 = var1 == null ? null : var1.getChatStyle().getChatClickEvent();
      if (var5 == null) {
         return false;
      }

      if (var5.getAction() == ClickEvent.Action.CHANGE_PAGE) {
         String var9 = var5.getValue();

         try {
            int var7 = Integer.parseInt(var9) - 1;
            if (var7 >= 0 && var7 < this.bookTotalPages && var7 != this.currPage) {
               this.currPage = var7;
               this.updateButtons();
               return true;
            }
         } catch (Throwable var8) {
         }

         return false;
      } else {
         boolean var6 = super.handleComponentClick(var1);
         if (var6 && var5.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }

         return var6;
      }
   }

   public IChatComponent func_175385_b(int var1, int var2) {
      if (this.field_175386_A == null) {
         return null;
      }

      int var6 = var1 - (this.width - this.bookImageWidth) / 2 - 36;
      int var7 = var2 - 2 - 16 - 16;
      if (var6 >= 0 && var7 >= 0) {
         int var8 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.field_175386_A.size());
         if (var6 <= 116 && var7 < this.mc.fontRendererObj.FONT_HEIGHT * var8 + var8) {
            int var9 = var7 / this.mc.fontRendererObj.FONT_HEIGHT;
            if (var9 >= 0 && var9 < this.field_175386_A.size()) {
               IChatComponent var10 = this.field_175386_A.get(var9);
               int var11 = 0;

               for (IChatComponent var13 : var10) {
                  if (var13 instanceof ChatComponentText) {
                     var11 += this.mc.fontRendererObj.getStringWidth(((ChatComponentText)var13).getChatComponentText_TextValue());
                     if (var11 > var6) {
                        return var13;
                     }
                  }
               }
            }

            return null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static class NextPageButton extends GuiButton {
      private final boolean field_146151_o;
      private static final long c = 76067126608523L;

      public NextPageButton(int var1, int var2, int var3, boolean var4) {
         super(var1, var2, var3, 23, 13, "");
         this.field_146151_o = var4;
      }

      @Override
      public void drawButton(Minecraft var1, int var2, int var3) {
         long var4 = c ^ 25209480794327L;
         if (this.visible) {
            boolean var7 = var2 >= this.xPosition && var3 >= this.yPosition && var2 < this.xPosition + this.width && var3 < this.yPosition + this.height;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            long var10000 = 4505584382785786102L ^ var4;
            var1.getTextureManager().bindTexture(GuiScreenBook.bookGuiTextures);
            byte var8 = 0;
            short var9 = 192;
            if (var7) {
               var8 += 23;
            }

            if (!this.field_146151_o) {
               var9 += 13;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, var8, var9, 23, 13);
         }
      }
   }
}
