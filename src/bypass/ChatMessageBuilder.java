package bypass;

import bypass.chat.AlignedChatComponent;
import bypass.chat.TextAlignment;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

public class ChatMessageBuilder implements ClientAccess {
   private ChatComponentText root;
   private boolean autoColor;
   private boolean linkDetection;
   private ChatStyle style;
   private ChatComponentStyle current;
   public final ClientChat clientChat;

   public ChatMessageBuilder(ClientChat clientChat) {
      this.clientChat = clientChat;
      this.root = new ChatComponentText("");
      this.autoColor = false;
      this.style = new ChatStyle();
      this.current = new ChatComponentText("");
   }

   public ChatMessageBuilder append(String string) {
      this.flushCurrent();
      this.current = new ChatComponentText(string);
      this.style = new ChatStyle();
      if (this.linkDetection) {
         Matcher matcher2 = ClientChat.getUrlPattern().matcher(string);

         while (matcher2.find()) {
            this.openUrlOnClick(string.substring(matcher2.start(1), matcher2.end(0)));
         }
      }

      if (this.autoColor) {
         this.color(ClientChat.getAccentColor(this.clientChat));
      }

      return this;
   }

   public ChatMessageBuilder enableLinkDetection() {
      this.linkDetection = true;
      return this;
   }

   public ChatMessageBuilder color(EnumChatFormatting enumChatFormatting) {
      this.style.setColor(enumChatFormatting);
      return this;
   }

   public ChatMessageBuilder bold() {
      this.style.setBold(true);
      return this;
   }

   public ChatMessageBuilder italic() {
      this.style.setItalic(true);
      return this;
   }

   public ChatMessageBuilder strikethrough() {
      this.style.setStrikethrough(true);
      return this;
   }

   public ChatMessageBuilder underline() {
      this.style.setUnderlined(true);
      return this;
   }

   public ChatMessageBuilder runCommandOnClick(String string) {
      this.style.setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, string));
      return this;
   }

   public ChatMessageBuilder showTextOnHover(String string) {
      this.style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(string)));
      return this;
   }

   public ChatMessageBuilder openUrlOnClick(String string) {
      this.style.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, string));
      return this;
   }

   public ChatMessageBuilder align(TextAlignment textAlignment) {
      this.flushCurrent();
      this.root.appendSibling(new AlignedChatComponent(this.clientChat, textAlignment));
      return this;
   }

   public ChatMessage build() {
      this.flushCurrent();
      this.resolveColumns();
      return new ChatMessage(this.clientChat, this.root);
   }

   private void flushCurrent() {
      if (this.current != null) {
         this.root.appendSibling(this.current.setChatStyle(this.style));
         this.current = null;
      }
   }

   private void resolveColumns() {
      if (!this.root.getSiblings().stream().noneMatch(iChatComponent -> iChatComponent instanceof AlignedChatComponent)) {
         int i = MathHelper.floor_float(MathHelper.floor_float(mc.gameSettings.chatWidth * 280.0F + 40.0F) / mc.gameSettings.chatScale);
         List list = GuiUtilRenderComponents.splitText(this.root, i, mc.fontRendererObj, false, false);
         ChatComponentText chatComponentText = this.root;
         chatComponentText.getSiblings().add(0, new AlignedChatComponent(this.clientChat, TextAlignment.CENTER));
         int j = 0;
         ArrayList arrayList = new ArrayList();
         ChatComponentText chatComponentText2 = new ChatComponentText("");

         for (IChatComponent iChatComponent2 : chatComponentText) {
            chatComponentText2.getSiblings().add(new ChatComponentText(iChatComponent2.getUnformattedText()).setChatStyle(iChatComponent2.getChatStyle()));
            if (iChatComponent2 instanceof AlignedChatComponent) {
               AlignedChatComponent alignedChatComponent = (AlignedChatComponent)iChatComponent2;
               String string = GuiUtilRenderComponents.func_178909_a(chatComponentText2.getFormattedText(), false);
               alignedChatComponent.padWidth = mc.fontRendererObj.getStringWidth(string);
               j += alignedChatComponent.padWidth;
               arrayList.add(alignedChatComponent);
               chatComponentText2 = new ChatComponentText("");
            }
         }

         int k = i - j;
         int l = arrayList.size();
         int m = k / l;

         for (int n = 0; n < arrayList.size(); n++) {
            AlignedChatComponent alignedChatComponent2 = (AlignedChatComponent)arrayList.get(n);
            AlignedChatComponent alignedChatComponent3 = n > 0 ? (AlignedChatComponent)arrayList.get(n - 1) : null;
            alignedChatComponent2.padWidth = m;
            switch (alignedChatComponent2.alignment) {
               case LEFT:
                  alignedChatComponent3.padWidth -= m;
                  alignedChatComponent2.padWidth += m;
                  break;
               case RIGHT:
                  alignedChatComponent3.padWidth += m;
                  alignedChatComponent2.padWidth -= m;
            }
         }
      }
   }

   public static ChatComponentText getRoot(ChatMessageBuilder chatMessageBuilder) {
      return chatMessageBuilder.root;
   }

   public static boolean setAutoColor(ChatMessageBuilder chatMessageBuilder, boolean bl) {
      return chatMessageBuilder.autoColor = bl;
   }
}
