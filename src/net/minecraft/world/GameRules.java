package net.minecraft.world;

import java.util.Set;
import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules {
   private TreeMap<String, GameRules.Value> theGameRules = new TreeMap<>();
   private static final long a = 78103230490780L;

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

   public void addGameRule(String var1, String var2, GameRules.ValueType var3) {
      this.theGameRules.put(var1, new GameRules.Value(var2, var3));
   }

   public void setOrCreateGameRule(String var1, String var2) {
      GameRules.Value var6 = this.theGameRules.get(var1);
      if (var6 != null) {
         var6.setValue(var2);
      } else {
         this.addGameRule(var1, var2, GameRules.ValueType.ANY_VALUE);
      }
   }

   public String getString(String var1) {
      GameRules.Value var5 = this.theGameRules.get(var1);
      return var5 != null ? var5.getString() : "";
   }

   public boolean getBoolean(String var1) {
      GameRules.Value var5 = this.theGameRules.get(var1);
      return var5 != null ? var5.getBoolean() : false;
   }

   public int getInt(String var1) {
      GameRules.Value var5 = this.theGameRules.get(var1);
      return var5 != null ? var5.getInt() : 0;
   }

   public NBTTagCompound writeToNBT() {
      NBTTagCompound var4 = new NBTTagCompound();

      for (Object var6 : this.theGameRules.keySet()) {
         GameRules.Value var7 = this.theGameRules.get(var6);
         var4.setString((String)var6, var7.getString());
      }

      return var4;
   }

   public void readFromNBT(NBTTagCompound var1) {
      for (String var6 : var1.getKeySet()) {
         String var7 = var1.getString(var6);
         this.setOrCreateGameRule(var6, var7);
      }
   }

   public String[] getRules() {
      Set var1 = this.theGameRules.keySet();
      return (java.lang.String[])(var1.toArray(new String[var1.size()]));
   }

   public boolean hasRule(String var1) {
      return this.theGameRules.containsKey(var1);
   }

   public boolean areSameType(String var1, GameRules.ValueType var2) {
      GameRules.Value var6 = this.theGameRules.get(var1);
      return var6 != null && (var6.getType() == var2 || var2 == GameRules.ValueType.ANY_VALUE);
   }

   public static class Value {
      private String valueString;
      private boolean valueBoolean;
      private int valueInteger;
      private double valueDouble;
      private final GameRules.ValueType type;
      private static final long a = 43036533007356L;

      public Value(String var1, GameRules.ValueType var2) {
         this.type = var2;
         this.setValue(var1);
      }

      public void setValue(String var1) {
         this.valueString = var1;
         if (var1 != null) {
            if (var1.equals("false")) {
               this.valueBoolean = false;
               return;
            }

            if (var1.equals("true")) {
               this.valueBoolean = true;
               return;
            }
         }

         this.valueBoolean = Boolean.parseBoolean(var1);
         this.valueInteger = this.valueBoolean ? 1 : 0;

         try {
            this.valueInteger = Integer.parseInt(var1);
         } catch (NumberFormatException var7) {
         }

         try {
            this.valueDouble = Double.parseDouble(var1);
         } catch (NumberFormatException var6) {
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

      private static final GameRules.ValueType[] p = new GameRules.ValueType[]{ANY_VALUE, BOOLEAN_VALUE, NUMERICAL_VALUE};

      ValueType(String var3, int var4) {
      }
   }
}
