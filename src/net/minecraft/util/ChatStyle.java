package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;

public class ChatStyle {
   private ChatStyle parentStyle;
   private EnumChatFormatting color;
   private Boolean bold;
   private Boolean italic;
   private Boolean underlined;
   private Boolean strikethrough;
   private Boolean obfuscated;
   private ClickEvent chatClickEvent;
   private HoverEvent chatHoverEvent;
   private String insertion;
   private static final ChatStyle rootStyle = new ChatStyle() {
      private static final long c = 17248107746788L;

      @Override
      public EnumChatFormatting getColor() {
         return null;
      }

      @Override
      public boolean getBold() {
         return false;
      }

      @Override
      public boolean getItalic() {
         return false;
      }

      @Override
      public boolean getStrikethrough() {
         return false;
      }

      @Override
      public boolean getUnderlined() {
         return false;
      }

      @Override
      public boolean getObfuscated() {
         return false;
      }

      @Override
      public ClickEvent getChatClickEvent() {
         return null;
      }

      @Override
      public HoverEvent getChatHoverEvent() {
         return null;
      }

      @Override
      public String getInsertion() {
         return null;
      }

      @Override
      public ChatStyle setColor(EnumChatFormatting var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setBold(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setItalic(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setStrikethrough(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setUnderlined(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setObfuscated(Boolean var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setChatClickEvent(ClickEvent var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setChatHoverEvent(HoverEvent var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setParentStyle(ChatStyle var1) {
         throw new UnsupportedOperationException();
      }

      @Override
      public String toString() {
         return "Style.ROOT";
      }

      @Override
      public ChatStyle createShallowCopy() {
         return this;
      }

      @Override
      public ChatStyle createDeepCopy() {
         return this;
      }

      @Override
      public String getFormattingCode() {
         return "";
      }
   };
   private static final long b = 67890870606750L;

   public EnumChatFormatting getColor() {
      return this.color == null ? this.getParent().getColor() : this.color;
   }

   public boolean getBold() {
      return this.bold == null ? this.getParent().getBold() : this.bold;
   }

   public boolean getItalic() {
      return this.italic == null ? this.getParent().getItalic() : this.italic;
   }

   public boolean getStrikethrough() {
      return this.strikethrough == null ? this.getParent().getStrikethrough() : this.strikethrough;
   }

   public boolean getUnderlined() {
      return this.underlined == null ? this.getParent().getUnderlined() : this.underlined;
   }

   public boolean getObfuscated() {
      return this.obfuscated == null ? this.getParent().getObfuscated() : this.obfuscated;
   }

   public boolean isEmpty() {
      return this.bold == null
         && this.italic == null
         && this.strikethrough == null
         && this.underlined == null
         && this.obfuscated == null
         && this.color == null
         && this.chatClickEvent == null
         && this.chatHoverEvent == null;
   }

   public ClickEvent getChatClickEvent() {
      return this.chatClickEvent == null ? this.getParent().getChatClickEvent() : this.chatClickEvent;
   }

   public HoverEvent getChatHoverEvent() {
      return this.chatHoverEvent == null ? this.getParent().getChatHoverEvent() : this.chatHoverEvent;
   }

   public String getInsertion() {
      return this.insertion == null ? this.getParent().getInsertion() : this.insertion;
   }

   public ChatStyle setColor(EnumChatFormatting var1) {
      this.color = var1;
      return this;
   }

   public ChatStyle setBold(Boolean var1) {
      this.bold = var1;
      return this;
   }

   public ChatStyle setItalic(Boolean var1) {
      this.italic = var1;
      return this;
   }

   public ChatStyle setStrikethrough(Boolean var1) {
      this.strikethrough = var1;
      return this;
   }

   public ChatStyle setUnderlined(Boolean var1) {
      this.underlined = var1;
      return this;
   }

   public ChatStyle setObfuscated(Boolean var1) {
      this.obfuscated = var1;
      return this;
   }

   public ChatStyle setChatClickEvent(ClickEvent var1) {
      this.chatClickEvent = var1;
      return this;
   }

   public ChatStyle setChatHoverEvent(HoverEvent var1) {
      this.chatHoverEvent = var1;
      return this;
   }

   public ChatStyle setInsertion(String var1) {
      this.insertion = var1;
      return this;
   }

   public ChatStyle setParentStyle(ChatStyle var1) {
      this.parentStyle = var1;
      return this;
   }

   public String getFormattingCode() {
      if (this.isEmpty()) {
         return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
      }

      StringBuilder var3 = new StringBuilder();
      if (this.getColor() != null) {
         var3.append(this.getColor());
      }

      if (this.getBold()) {
         var3.append(EnumChatFormatting.BOLD);
      }

      if (this.getItalic()) {
         var3.append(EnumChatFormatting.ITALIC);
      }

      if (this.getUnderlined()) {
         var3.append(EnumChatFormatting.UNDERLINE);
      }

      if (this.getObfuscated()) {
         var3.append(EnumChatFormatting.OBFUSCATED);
      }

      if (this.getStrikethrough()) {
         var3.append(EnumChatFormatting.STRIKETHROUGH);
      }

      return var3.toString();
   }

   private ChatStyle getParent() {
      return this.parentStyle == null ? rootStyle : this.parentStyle;
   }

   @Override
   public String toString() {
      return "Style{hasParent="
         + (this.parentStyle != null)
         + ", color="
         + this.color
         + ", bold="
         + this.bold
         + ", italic="
         + this.italic
         + ", underlined="
         + this.underlined
         + ", obfuscated="
         + this.obfuscated
         + ", clickEvent="
         + this.getChatClickEvent()
         + ", hoverEvent="
         + this.getChatHoverEvent()
         + ", insertion="
         + this.getInsertion()
         + '}';
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ChatStyle)) {
         return false;
      }

      ChatStyle var6 = (ChatStyle)var1;
      return this.getBold() == var6.getBold()
         && this.getColor() == var6.getColor()
         && this.getItalic() == var6.getItalic()
         && this.getObfuscated() == var6.getObfuscated()
         && this.getStrikethrough() == var6.getStrikethrough()
         && this.getUnderlined() == var6.getUnderlined()
         && (this.getChatClickEvent() != null ? this.getChatClickEvent().equals(var6.getChatClickEvent()) : var6.getChatClickEvent() == null)
         && (this.getChatHoverEvent() != null ? this.getChatHoverEvent().equals(var6.getChatHoverEvent()) : var6.getChatHoverEvent() == null)
         && (this.getInsertion() != null ? this.getInsertion().equals(var6.getInsertion()) : var6.getInsertion() == null);
   }

   @Override
   public int hashCode() {
      int var3 = this.color.hashCode();
      var3 = 31 * var3 + this.bold.hashCode();
      var3 = 31 * var3 + this.italic.hashCode();
      var3 = 31 * var3 + this.underlined.hashCode();
      var3 = 31 * var3 + this.strikethrough.hashCode();
      var3 = 31 * var3 + this.obfuscated.hashCode();
      var3 = 31 * var3 + this.chatClickEvent.hashCode();
      var3 = 31 * var3 + this.chatHoverEvent.hashCode();
      return 31 * var3 + this.insertion.hashCode();
   }

   public ChatStyle createShallowCopy() {
      ChatStyle var1 = new ChatStyle();
      var1.bold = this.bold;
      var1.italic = this.italic;
      var1.strikethrough = this.strikethrough;
      var1.underlined = this.underlined;
      var1.obfuscated = this.obfuscated;
      var1.color = this.color;
      var1.chatClickEvent = this.chatClickEvent;
      var1.chatHoverEvent = this.chatHoverEvent;
      var1.parentStyle = this.parentStyle;
      var1.insertion = this.insertion;
      return var1;
   }

   public ChatStyle createDeepCopy() {
      ChatStyle var1 = new ChatStyle();
      var1.setBold(this.getBold());
      var1.setItalic(this.getItalic());
      var1.setStrikethrough(this.getStrikethrough());
      var1.setUnderlined(this.getUnderlined());
      var1.setObfuscated(this.getObfuscated());
      var1.setColor(this.getColor());
      var1.setChatClickEvent(this.getChatClickEvent());
      var1.setChatHoverEvent(this.getChatHoverEvent());
      var1.setInsertion(this.getInsertion());
      return var1;
   }

   public static class Serializer implements JsonDeserializer<ChatStyle>, JsonSerializer<ChatStyle> {
      private static final long a = 98196544772308L;

      public ChatStyle deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonObject()) {
            ChatStyle var7 = new ChatStyle();
            JsonObject var8 = var1.getAsJsonObject();
            if (var8 == null) {
               return null;
            }

            if (var8.has("bold")) {
               var7.bold = var8.get("bold").getAsBoolean();
            }

            if (var8.has("italic")) {
               var7.italic = var8.get("italic").getAsBoolean();
            }

            if (var8.has("underlined")) {
               var7.underlined = var8.get("underlined").getAsBoolean();
            }

            if (var8.has("strikethrough")) {
               var7.strikethrough = var8.get("strikethrough").getAsBoolean();
            }

            if (var8.has("obfuscated")) {
               var7.obfuscated = var8.get("obfuscated").getAsBoolean();
            }

            if (var8.has("color")) {
               var7.color = var3.deserialize(var8.get("color"), EnumChatFormatting.class);
            }

            if (var8.has("insertion")) {
               var7.insertion = var8.get("insertion").getAsString();
            }

            if (var8.has("clickEvent")) {
               JsonObject var9 = var8.getAsJsonObject("clickEvent");
               if (var9 != null) {
                  JsonPrimitive var10 = var9.getAsJsonPrimitive("action");
                  ClickEvent.Action var11 = var10 == null ? null : ClickEvent.Action.getValueByCanonicalName(var10.getAsString());
                  JsonPrimitive var12 = var9.getAsJsonPrimitive("value");
                  String var13 = var12 == null ? null : var12.getAsString();
                  if (var11 != null && var13 != null && var11.shouldAllowInChat()) {
                     var7.chatClickEvent = new ClickEvent(var11, var13);
                  }
               }
            }

            if (var8.has("hoverEvent")) {
               JsonObject var14 = var8.getAsJsonObject("hoverEvent");
               if (var14 != null) {
                  JsonPrimitive var15 = var14.getAsJsonPrimitive("action");
                  HoverEvent.Action var16 = var15 == null ? null : HoverEvent.Action.getValueByCanonicalName(var15.getAsString());
                  IChatComponent var17 = var3.deserialize(var14.get("value"), IChatComponent.class);
                  if (var16 != null && var17 != null && var16.shouldAllowInChat()) {
                     var7.chatHoverEvent = new HoverEvent(var16, var17);
                  }
               }
            }

            return var7;
         } else {
            return null;
         }
      }

      public JsonElement serialize(ChatStyle var1, Type var2, JsonSerializationContext var3) {
         if (var1.isEmpty()) {
            return null;
         }

         JsonObject var7 = new JsonObject();
         if (var1.bold != null) {
            var7.addProperty("bold", var1.bold);
         }

         if (var1.italic != null) {
            var7.addProperty("italic", var1.italic);
         }

         if (var1.underlined != null) {
            var7.addProperty("underlined", var1.underlined);
         }

         if (var1.strikethrough != null) {
            var7.addProperty("strikethrough", var1.strikethrough);
         }

         if (var1.obfuscated != null) {
            var7.addProperty("obfuscated", var1.obfuscated);
         }

         if (var1.color != null) {
            var7.add("color", var3.serialize(var1.color));
         }

         if (var1.insertion != null) {
            var7.add("insertion", var3.serialize(var1.insertion));
         }

         if (var1.chatClickEvent != null) {
            JsonObject var8 = new JsonObject();
            var8.addProperty("action", var1.chatClickEvent.getAction().getCanonicalName());
            var8.addProperty("value", var1.chatClickEvent.getValue());
            var7.add("clickEvent", var8);
         }

         if (var1.chatHoverEvent != null) {
            JsonObject var9 = new JsonObject();
            var9.addProperty("action", var1.chatHoverEvent.getAction().getCanonicalName());
            var9.add("value", var3.serialize(var1.chatHoverEvent.getValue()));
            var7.add("hoverEvent", var9);
         }

         return var7;
      }
   }
}
