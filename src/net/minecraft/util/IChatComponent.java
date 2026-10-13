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
   IChatComponent setChatStyle(ChatStyle var1);

   ChatStyle getChatStyle();

   IChatComponent appendText(String var1);

   IChatComponent appendSibling(IChatComponent var1);

   String getUnformattedTextForChat();

   String getUnformattedText();

   String getFormattedText();

   List<IChatComponent> getSiblings();

   IChatComponent createCopy();

   class Serializer implements JsonDeserializer<IChatComponent>, JsonSerializer<IChatComponent> {
      private static final Gson GSON;
      private static final long a = 50223679704650L;

      public IChatComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         if (var1.isJsonPrimitive()) {
            return new ChatComponentText(var1.getAsString());
         }

         if (!var1.isJsonObject()) {
            if (var1.isJsonArray()) {
               JsonArray var14 = var1.getAsJsonArray();
               IChatComponent var15 = null;

               for (JsonElement var20 : var14) {
                  IChatComponent var21 = this.deserialize(var20, var20.getClass(), var3);
                  if (var15 == null) {
                     var15 = var21;
                  } else {
                     var15.appendSibling(var21);
                  }
               }

               return var15;
            } else {
               throw new JsonParseException("Don't know how to turn " + var1.toString() + " into a Component");
            }
         } else {
            JsonObject var7 = var1.getAsJsonObject();
            ChatComponentStyle var8;
            if (var7.has("text")) {
               var8 = new ChatComponentText(var7.get("text").getAsString());
            } else if (var7.has("translate")) {
               String var9 = var7.get("translate").getAsString();
               if (var7.has("with")) {
                  JsonArray var10 = var7.getAsJsonArray("with");
                  Object[] var11 = new Object[var10.size()];

                  for (int var12 = 0; var12 < var11.length; var12++) {
                     var11[var12] = this.deserialize(var10.get(var12), var2, var3);
                     if (var11[var12] instanceof ChatComponentText) {
                        ChatComponentText var13 = (ChatComponentText)var11[var12];
                        if (var13.getChatStyle().isEmpty() && var13.getSiblings().isEmpty()) {
                           var11[var12] = var13.getChatComponentText_TextValue();
                        }
                     }
                  }

                  var8 = new ChatComponentTranslation(var9, var11);
               } else {
                  var8 = new ChatComponentTranslation(var9);
               }
            } else if (var7.has("score")) {
               JsonObject var16 = var7.getAsJsonObject("score");
               if (!var16.has("name") || !var16.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               var8 = new ChatComponentScore(JsonUtils.getString(var16, "name"), JsonUtils.getString(var16, "objective"));
               if (var16.has("value")) {
                  ((ChatComponentScore)var8).setValue(JsonUtils.getString(var16, "value"));
               }
            } else {
               if (!var7.has("selector")) {
                  throw new JsonParseException("Don't know how to turn " + var1.toString() + " into a Component");
               }

               var8 = new ChatComponentSelector(JsonUtils.getString(var7, "selector"));
            }

            if (var7.has("extra")) {
               JsonArray var17 = var7.getAsJsonArray("extra");
               if (var17.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for (int var19 = 0; var19 < var17.size(); var19++) {
                  var8.appendSibling(this.deserialize(var17.get(var19), var2, var3));
               }
            }

            var8.setChatStyle(var3.deserialize(var1, ChatStyle.class));
            return var8;
         }
      }

      private void serializeChatStyle(ChatStyle var1, JsonObject var2, JsonSerializationContext var3) {
         JsonElement var7 = var3.serialize(var1);
         if (var7.isJsonObject()) {
            JsonObject var8 = (JsonObject)var7;

            for (Entry var10 : var8.entrySet()) {
               var2.add((String)var10.getKey(), (JsonElement)var10.getValue());
            }
         }
      }

      public JsonElement serialize(IChatComponent var1, Type var2, JsonSerializationContext var3) {
         if (var1 instanceof ChatComponentText && var1.getChatStyle().isEmpty() && var1.getSiblings().isEmpty()) {
            return new JsonPrimitive(((ChatComponentText)var1).getChatComponentText_TextValue());
         }

         JsonObject var7 = new JsonObject();
         if (!var1.getChatStyle().isEmpty()) {
            this.serializeChatStyle(var1.getChatStyle(), var7, var3);
         }

         if (!var1.getSiblings().isEmpty()) {
            JsonArray var8 = new JsonArray();

            for (IChatComponent var10 : var1.getSiblings()) {
               var8.add(this.serialize(var10, var10.getClass(), var3));
            }

            var7.add("extra", var8);
         }

         if (var1 instanceof ChatComponentText) {
            var7.addProperty("text", ((ChatComponentText)var1).getChatComponentText_TextValue());
         } else if (var1 instanceof ChatComponentTranslation) {
            ChatComponentTranslation var14 = (ChatComponentTranslation)var1;
            var7.addProperty("translate", var14.getKey());
            if (var14.getFormatArgs() != null && var14.getFormatArgs().length > 0) {
               JsonArray var17 = new JsonArray();

               for (Object var13 : var14.getFormatArgs()) {
                  if (var13 instanceof IChatComponent) {
                     var17.add(this.serialize((IChatComponent)var13, var13.getClass(), var3));
                  } else {
                     var17.add(new JsonPrimitive(String.valueOf(var13)));
                  }
               }

               var7.add("with", var17);
            }
         } else if (var1 instanceof ChatComponentScore) {
            ChatComponentScore var15 = (ChatComponentScore)var1;
            JsonObject var18 = new JsonObject();
            var18.addProperty("name", var15.getName());
            var18.addProperty("objective", var15.getObjective());
            var18.addProperty("value", var15.getUnformattedTextForChat());
            var7.add("score", var18);
         } else {
            if (!(var1 instanceof ChatComponentSelector)) {
               throw new IllegalArgumentException("Don't know how to serialize " + var1 + " as a Component");
            }

            ChatComponentSelector var16 = (ChatComponentSelector)var1;
            var7.addProperty("selector", var16.getSelector());
         }

         return var7;
      }

      public static String componentToJson(IChatComponent var0) {
         return GSON.toJson(var0);
      }

      public static IChatComponent jsonToComponent(String var0) {
         return GSON.fromJson(var0, IChatComponent.class);
      }

      static {
         GsonBuilder var11 = new GsonBuilder();
         var11.registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer());
         var11.registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer());
         var11.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
         GSON = var11.create();
      }
   }
}
