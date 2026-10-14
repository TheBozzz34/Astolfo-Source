package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map.Entry;

public interface IChatComponent extends Iterable<IChatComponent> {
   IChatComponent setChatStyle(ChatStyle style);

   ChatStyle getChatStyle();

   IChatComponent appendText(String text);

   IChatComponent appendSibling(IChatComponent component);

   String getUnformattedTextForChat();

   String getUnformattedText();

   String getFormattedText();

   List<IChatComponent> getSiblings();

   IChatComponent createCopy();

   class Serializer implements JsonDeserializer<IChatComponent>, JsonSerializer<IChatComponent> {
      private static final Gson GSON;
      private static final long a = 50223679704650L;

      public IChatComponent deserialize(JsonElement jsonElement, Type type2, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         if (jsonElement.isJsonPrimitive()) {
            return new ChatComponentText(jsonElement.getAsString());
         }

         if (!jsonElement.isJsonObject()) {
            if (jsonElement.isJsonArray()) {
               JsonArray jsonArray = jsonElement.getAsJsonArray();
               IChatComponent iChatComponent = null;

               for (JsonElement jsonElement2 : jsonArray) {
                  IChatComponent iChatComponent2 = this.deserialize(jsonElement2, jsonElement2.getClass(), jsonDeserializationContext);
                  if (iChatComponent == null) {
                     iChatComponent = iChatComponent2;
                  } else {
                     iChatComponent.appendSibling(iChatComponent2);
                  }
               }

               return iChatComponent;
            } else {
               throw new JsonParseException("Don't know how to turn " + jsonElement.toString() + " into a Component");
            }
         } else {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            ChatComponentStyle chatComponentStyle;
            if (jsonObject.has("text")) {
               chatComponentStyle = new ChatComponentText(jsonObject.get("text").getAsString());
            } else if (jsonObject.has("translate")) {
               String string = jsonObject.get("translate").getAsString();
               if (jsonObject.has("with")) {
                  JsonArray jsonArray2 = jsonObject.getAsJsonArray("with");
                  Object[] objects = new Object[jsonArray2.size()];

                  for (int i = 0; i < objects.length; i++) {
                     objects[i] = this.deserialize(jsonArray2.get(i), type2, jsonDeserializationContext);
                     if (objects[i] instanceof ChatComponentText) {
                        ChatComponentText chatComponentText = (ChatComponentText)objects[i];
                        if (chatComponentText.getChatStyle().isEmpty() && chatComponentText.getSiblings().isEmpty()) {
                           objects[i] = chatComponentText.getChatComponentText_TextValue();
                        }
                     }
                  }

                  chatComponentStyle = new ChatComponentTranslation(string, objects);
               } else {
                  chatComponentStyle = new ChatComponentTranslation(string);
               }
            } else if (jsonObject.has("score")) {
               JsonObject jsonObject2 = jsonObject.getAsJsonObject("score");
               if (!jsonObject2.has("name") || !jsonObject2.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               chatComponentStyle = new ChatComponentScore(JsonUtils.getString(jsonObject2, "name"), JsonUtils.getString(jsonObject2, "objective"));
               if (jsonObject2.has("value")) {
                  ((ChatComponentScore)chatComponentStyle).setValue(JsonUtils.getString(jsonObject2, "value"));
               }
            } else {
               if (!jsonObject.has("selector")) {
                  throw new JsonParseException("Don't know how to turn " + jsonElement.toString() + " into a Component");
               }

               chatComponentStyle = new ChatComponentSelector(JsonUtils.getString(jsonObject, "selector"));
            }

            if (jsonObject.has("extra")) {
               JsonArray jsonArray3 = jsonObject.getAsJsonArray("extra");
               if (jsonArray3.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for (int j = 0; j < jsonArray3.size(); j++) {
                  chatComponentStyle.appendSibling(this.deserialize(jsonArray3.get(j), type2, jsonDeserializationContext));
               }
            }

            chatComponentStyle.setChatStyle(jsonDeserializationContext.deserialize(jsonElement, ChatStyle.class));
            return chatComponentStyle;
         }
      }

      private void serializeChatStyle(ChatStyle style, JsonObject object, JsonSerializationContext ctx) {
         JsonElement jsonElement = ctx.serialize(style);
         if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = (JsonObject)jsonElement;

            for (Entry entry : jsonObject.entrySet()) {
               object.add((String)entry.getKey(), (JsonElement)entry.getValue());
            }
         }
      }

      public JsonElement serialize(IChatComponent iChatComponent, Type type2, JsonSerializationContext jsonSerializationContext) {
         if (iChatComponent instanceof ChatComponentText && iChatComponent.getChatStyle().isEmpty() && iChatComponent.getSiblings().isEmpty()) {
            return new JsonPrimitive(((ChatComponentText)iChatComponent).getChatComponentText_TextValue());
         }

         JsonObject jsonObject = new JsonObject();
         if (!iChatComponent.getChatStyle().isEmpty()) {
            this.serializeChatStyle(iChatComponent.getChatStyle(), jsonObject, jsonSerializationContext);
         }

         if (!iChatComponent.getSiblings().isEmpty()) {
            JsonArray jsonArray = new JsonArray();

            for (IChatComponent iChatComponent2 : iChatComponent.getSiblings()) {
               jsonArray.add(this.serialize(iChatComponent2, iChatComponent2.getClass(), jsonSerializationContext));
            }

            jsonObject.add("extra", jsonArray);
         }

         if (iChatComponent instanceof ChatComponentText) {
            jsonObject.addProperty("text", ((ChatComponentText)iChatComponent).getChatComponentText_TextValue());
         } else if (iChatComponent instanceof ChatComponentTranslation) {
            ChatComponentTranslation chatComponentTranslation = (ChatComponentTranslation)iChatComponent;
            jsonObject.addProperty("translate", chatComponentTranslation.getKey());
            if (chatComponentTranslation.getFormatArgs() != null && chatComponentTranslation.getFormatArgs().length > 0) {
               JsonArray jsonArray2 = new JsonArray();

               for (Object object : chatComponentTranslation.getFormatArgs()) {
                  if (object instanceof IChatComponent) {
                     jsonArray2.add(this.serialize((IChatComponent)object, object.getClass(), jsonSerializationContext));
                  } else {
                     jsonArray2.add(new JsonPrimitive(String.valueOf(object)));
                  }
               }

               jsonObject.add("with", jsonArray2);
            }
         } else if (iChatComponent instanceof ChatComponentScore) {
            ChatComponentScore chatComponentScore = (ChatComponentScore)iChatComponent;
            JsonObject jsonObject2 = new JsonObject();
            jsonObject2.addProperty("name", chatComponentScore.getName());
            jsonObject2.addProperty("objective", chatComponentScore.getObjective());
            jsonObject2.addProperty("value", chatComponentScore.getUnformattedTextForChat());
            jsonObject.add("score", jsonObject2);
         } else {
            if (!(iChatComponent instanceof ChatComponentSelector)) {
               throw new IllegalArgumentException("Don't know how to serialize " + iChatComponent + " as a Component");
            }

            ChatComponentSelector chatComponentSelector = (ChatComponentSelector)iChatComponent;
            jsonObject.addProperty("selector", chatComponentSelector.getSelector());
         }

         return jsonObject;
      }

      public static String componentToJson(IChatComponent component) {
         return GSON.toJson(component);
      }

      public static IChatComponent jsonToComponent(String json) {
         return GSON.fromJson(json, IChatComponent.class);
      }

      static {
         GsonBuilder gsonBuilder = new GsonBuilder();
         gsonBuilder.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
         gsonBuilder.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
         gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
         GSON = gsonBuilder.create();
      }
   }
}
