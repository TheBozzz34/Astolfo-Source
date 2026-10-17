package net.minecraft.world;

import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules {
   private TreeMap<String, GameRules.Value> theGameRules = new TreeMap<>();

   public GameRules() {
      this.addGameRule("doFireTick", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("mobGriefing", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("keepInventory", "false", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doMobSpawning", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doMobLoot", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doTileDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doEntityDrops", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("commandBlockOutput", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("naturalRegeneration", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("doDaylightCycle", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("logAdminCommands", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("showDeathMessages", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("randomTickSpeed", "3", GameRules.ValueType.NUMERICAL_VALUE);
      this.addGameRule("sendCommandFeedback", "true", GameRules.ValueType.BOOLEAN_VALUE);
      this.addGameRule("reducedDebugInfo", "false", GameRules.ValueType.BOOLEAN_VALUE);
   }

   public void addGameRule(String key, String value, GameRules.ValueType typeIn) {
      this.theGameRules.put(key, new GameRules.Value(value, typeIn));
   }

   public void setOrCreateGameRule(String key, String ruleValue) {
      GameRules.Value value = this.theGameRules.get(key);
      if (value != null) {
         value.setValue(ruleValue);
      } else {
         this.addGameRule(key, ruleValue, GameRules.ValueType.ANY_VALUE);
      }
   }

   public String getString(String name) {
      GameRules.Value value = this.theGameRules.get(name);
      return value != null ? value.getString() : "";
   }

   public boolean getBoolean(String name) {
      GameRules.Value value = this.theGameRules.get(name);
      return value != null ? value.getBoolean() : false;
   }

   public int getInt(String name) {
      GameRules.Value value = this.theGameRules.get(name);
      return value != null ? value.getInt() : 0;
   }

   public NBTTagCompound writeToNBT() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();

      for (Object object : this.theGameRules.keySet()) {
         GameRules.Value value = this.theGameRules.get(object);
         nbtTagCompound.setString((String)object, value.getString());
      }

      return nbtTagCompound;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      for (String string : nbt.getKeySet()) {
         String string2 = nbt.getString(string);
         this.setOrCreateGameRule(string, string2);
      }
   }

   public String[] getRules() {
      Set set = this.theGameRules.keySet();
      return (java.lang.String[])(set.toArray(new String[set.size()]));
   }

   public boolean hasRule(String name) {
      return this.theGameRules.containsKey(name);
   }

   public boolean areSameType(String key, GameRules.ValueType otherValue) {
      GameRules.Value value = this.theGameRules.get(key);
      return value != null && (value.getType() == otherValue || otherValue == GameRules.ValueType.ANY_VALUE);
   }

   public static class Value {
      private String valueString;
      private boolean valueBoolean;
      private int valueInteger;
      private double valueDouble;
      private final GameRules.ValueType type;

      public Value(String value, GameRules.ValueType typeIn) {
         this.type = typeIn;
         this.setValue(value);
      }

      public void setValue(String value) {
         this.valueString = value;
         if (value != null) {
            if (value.equals("false")) {
               this.valueBoolean = false;
               return;
            }

            if (value.equals("true")) {
               this.valueBoolean = true;
               return;
            }
         }

         this.valueBoolean = Boolean.parseBoolean(value);
         this.valueInteger = this.valueBoolean ? 1 : 0;

         try {
            this.valueInteger = Integer.parseInt(value);
         } catch (NumberFormatException numberFormatException) {
         }

         try {
            this.valueDouble = Double.parseDouble(value);
         } catch (NumberFormatException numberFormatException2) {
         }
      }

      public String getString() {
         return this.valueString;
      }

      public boolean getBoolean() {
         return this.valueBoolean;
      }

      public int getInt() {
         return this.valueInteger;
      }

      public GameRules.ValueType getType() {
         return this.type;
      }
   }

   public enum ValueType {
      ANY_VALUE("ANY_VALUE", 0),
      BOOLEAN_VALUE("BOOLEAN_VALUE", 1),
      NUMERICAL_VALUE("NUMERICAL_VALUE", 2);

      private static final GameRules.ValueType[] VALUES = new GameRules.ValueType[]{ANY_VALUE, BOOLEAN_VALUE, NUMERICAL_VALUE};

      ValueType(String string2, int j) {
      }
   }
}
