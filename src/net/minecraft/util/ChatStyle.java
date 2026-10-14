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
      public ChatStyle setColor(EnumChatFormatting colorIn) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setBold(Boolean boldIn) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setItalic(Boolean italicIn) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setStrikethrough(Boolean strikethroughIn) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setUnderlined(Boolean underlinedIn) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setObfuscated(Boolean obfuscatedIn) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setChatClickEvent(ClickEvent event) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setChatHoverEvent(HoverEvent event) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ChatStyle setParentStyle(ChatStyle parent) {
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

   public ChatStyle setColor(EnumChatFormatting color) {
      this.color = color;
      return this;
   }

   public ChatStyle setBold(Boolean boldIn) {
      this.bold = boldIn;
      return this;
   }

   public ChatStyle setItalic(Boolean italic) {
      this.italic = italic;
      return this;
   }

   public ChatStyle setStrikethrough(Boolean strikethrough) {
      this.strikethrough = strikethrough;
      return this;
   }

   public ChatStyle setUnderlined(Boolean underlined) {
      this.underlined = underlined;
      return this;
   }

   public ChatStyle setObfuscated(Boolean obfuscated) {
      this.obfuscated = obfuscated;
      return this;
   }

   public ChatStyle setChatClickEvent(ClickEvent event) {
      this.chatClickEvent = event;
      return this;
   }

   public ChatStyle setChatHoverEvent(HoverEvent event) {
      this.chatHoverEvent = event;
      return this;
   }

   public ChatStyle setInsertion(String insertion) {
      this.insertion = insertion;
      return this;
   }

   public ChatStyle setParentStyle(ChatStyle parent) {
      this.parentStyle = parent;
      return this;
   }

   public String getFormattingCode() {
      if (this.isEmpty()) {
         return this.parentStyle != null ? this.parentStyle.getFormattingCode() : "";
      }

      StringBuilder stringBuilder = new StringBuilder();
      if (this.getColor() != null) {
         stringBuilder.append(this.getColor());
      }

      if (this.getBold()) {
         stringBuilder.append(EnumChatFormatting.BOLD);
      }

      if (this.getItalic()) {
         stringBuilder.append(EnumChatFormatting.ITALIC);
      }

      if (this.getUnderlined()) {
         stringBuilder.append(EnumChatFormatting.UNDERLINE);
      }

      if (this.getObfuscated()) {
         stringBuilder.append(EnumChatFormatting.OBFUSCATED);
      }

      if (this.getStrikethrough()) {
         stringBuilder.append(EnumChatFormatting.STRIKETHROUGH);
      }

      return stringBuilder.toString();
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
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }

      if (!(object instanceof ChatStyle)) {
         return false;
      }

      ChatStyle chatStyle = (ChatStyle)object;
      return this.getBold() == chatStyle.getBold()
         && this.getColor() == chatStyle.getColor()
         && this.getItalic() == chatStyle.getItalic()
         && this.getObfuscated() == chatStyle.getObfuscated()
         && this.getStrikethrough() == chatStyle.getStrikethrough()
         && this.getUnderlined() == chatStyle.getUnderlined()
         && (this.getChatClickEvent() != null ? this.getChatClickEvent().equals(chatStyle.getChatClickEvent()) : chatStyle.getChatClickEvent() == null)
         && (this.getChatHoverEvent() != null ? this.getChatHoverEvent().equals(chatStyle.getChatHoverEvent()) : chatStyle.getChatHoverEvent() == null)
         && (this.getInsertion() != null ? this.getInsertion().equals(chatStyle.getInsertion()) : chatStyle.getInsertion() == null);
   }

   @Override
   public int hashCode() {
      int i = this.color.hashCode();
      i = 31 * i + this.bold.hashCode();
      i = 31 * i + this.italic.hashCode();
      i = 31 * i + this.underlined.hashCode();
      i = 31 * i + this.strikethrough.hashCode();
      i = 31 * i + this.obfuscated.hashCode();
      i = 31 * i + this.chatClickEvent.hashCode();
      i = 31 * i + this.chatHoverEvent.hashCode();
      return 31 * i + this.insertion.hashCode();
   }

   public ChatStyle createShallowCopy() {
      ChatStyle chatStyle = new ChatStyle();
      chatStyle.bold = this.bold;
      chatStyle.italic = this.italic;
      chatStyle.strikethrough = this.strikethrough;
      chatStyle.underlined = this.underlined;
      chatStyle.obfuscated = this.obfuscated;
      chatStyle.color = this.color;
      chatStyle.chatClickEvent = this.chatClickEvent;
      chatStyle.chatHoverEvent = this.chatHoverEvent;
      chatStyle.parentStyle = this.parentStyle;
      chatStyle.insertion = this.insertion;
      return chatStyle;
   }

   public ChatStyle createDeepCopy() {
      ChatStyle chatStyle = new ChatStyle();
      chatStyle.setBold(this.getBold());
      chatStyle.setItalic(this.getItalic());
      chatStyle.setStrikethrough(this.getStrikethrough());
      chatStyle.setUnderlined(this.getUnderlined());
      chatStyle.setObfuscated(this.getObfuscated());
      chatStyle.setColor(this.getColor());
      chatStyle.setChatClickEvent(this.getChatClickEvent());
      chatStyle.setChatHoverEvent(this.getChatHoverEvent());
      chatStyle.setInsertion(this.getInsertion());
      return chatStyle;
   }

   public static class Serializer implements JsonDeserializer<ChatStyle>, JsonSerializer<ChatStyle> {
      public ChatStyle deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         if (jsonElement.isJsonObject()) {
            ChatStyle chatStyle = new ChatStyle();
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject == null) {
               return null;
            }

            if (jsonObject.has("bold")) {
               chatStyle.bold = jsonObject.get("bold").getAsBoolean();
            }

            if (jsonObject.has("italic")) {
               chatStyle.italic = jsonObject.get("italic").getAsBoolean();
            }

            if (jsonObject.has("underlined")) {
               chatStyle.underlined = jsonObject.get("underlined").getAsBoolean();
            }

            if (jsonObject.has("strikethrough")) {
               chatStyle.strikethrough = jsonObject.get("strikethrough").getAsBoolean();
            }

            if (jsonObject.has("obfuscated")) {
               chatStyle.obfuscated = jsonObject.get("obfuscated").getAsBoolean();
            }

            if (jsonObject.has("color")) {
               chatStyle.color = jsonDeserializationContext.deserialize(jsonObject.get("color"), EnumChatFormatting.class);
            }

            if (jsonObject.has("insertion")) {
               chatStyle.insertion = jsonObject.get("insertion").getAsString();
            }

            if (jsonObject.has("clickEvent")) {
               JsonObject jsonObject2 = jsonObject.getAsJsonObject("clickEvent");
               if (jsonObject2 != null) {
                  JsonPrimitive jsonPrimitive = jsonObject2.getAsJsonPrimitive("action");
                  ClickEvent.Action action2 = jsonPrimitive == null ? null : ClickEvent.Action.getValueByCanonicalName(jsonPrimitive.getAsString());
                  JsonPrimitive jsonPrimitive2 = jsonObject2.getAsJsonPrimitive("value");
                  String string = jsonPrimitive2 == null ? null : jsonPrimitive2.getAsString();
                  if (action2 != null && string != null && action2.shouldAllowInChat()) {
                     chatStyle.chatClickEvent = new ClickEvent(action2, string);
                  }
               }
            }

            if (jsonObject.has("hoverEvent")) {
               JsonObject jsonObject3 = jsonObject.getAsJsonObject("hoverEvent");
               if (jsonObject3 != null) {
                  JsonPrimitive jsonPrimitive3 = jsonObject3.getAsJsonPrimitive("action");
                  HoverEvent.Action action3 = jsonPrimitive3 == null ? null : HoverEvent.Action.getValueByCanonicalName(jsonPrimitive3.getAsString());
                  IChatComponent iChatComponent = jsonDeserializationContext.deserialize(jsonObject3.get("value"), IChatComponent.class);
                  if (action3 != null && iChatComponent != null && action3.shouldAllowInChat()) {
                     chatStyle.chatHoverEvent = new HoverEvent(action3, iChatComponent);
                  }
               }
            }

            return chatStyle;
         } else {
            return null;
         }
      }

      public JsonElement serialize(ChatStyle chatStyle, Type type2, JsonSerializationContext jsonSerializationContext) {
         if (chatStyle.isEmpty()) {
            return null;
         }

         JsonObject jsonObject = new JsonObject();
         if (chatStyle.bold != null) {
            jsonObject.addProperty("bold", chatStyle.bold);
         }

         if (chatStyle.italic != null) {
            jsonObject.addProperty("italic", chatStyle.italic);
         }

         if (chatStyle.underlined != null) {
            jsonObject.addProperty("underlined", chatStyle.underlined);
         }

         if (chatStyle.strikethrough != null) {
            jsonObject.addProperty("strikethrough", chatStyle.strikethrough);
         }

         if (chatStyle.obfuscated != null) {
            jsonObject.addProperty("obfuscated", chatStyle.obfuscated);
         }

         if (chatStyle.color != null) {
            jsonObject.add("color", jsonSerializationContext.serialize(chatStyle.color));
         }

         if (chatStyle.insertion != null) {
            jsonObject.add("insertion", jsonSerializationContext.serialize(chatStyle.insertion));
         }

         if (chatStyle.chatClickEvent != null) {
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("action", chatStyle.chatClickEvent.getAction().getCanonicalName());
            jsonObject2.addProperty("value", chatStyle.chatClickEvent.getValue());
            jsonObject.add("clickEvent", jsonObject2);
         }

         if (chatStyle.chatHoverEvent != null) {
            JsonObject jsonObject3 = new JsonObject();
            jsonObject3.addProperty("action", chatStyle.chatHoverEvent.getAction().getCanonicalName());
            jsonObject3.add("value", jsonSerializationContext.serialize(chatStyle.chatHoverEvent.getValue()));
            jsonObject.add("hoverEvent", jsonObject3);
         }

         return jsonObject;
      }
   }
}
