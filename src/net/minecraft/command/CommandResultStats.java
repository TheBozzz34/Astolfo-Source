package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandResultStats {
   private static final int NUM_RESULT_TYPES = CommandResultStats.Type.values().length;
   private static final String[] STRING_RESULT_TYPES = new String[NUM_RESULT_TYPES];
   private String[] entitiesID = STRING_RESULT_TYPES;
   private String[] objectives = STRING_RESULT_TYPES;
   private static final long a = 116147922342162L;

   public void setCommandStatScore(final ICommandSender var1, CommandResultStats.Type var2, int var3) {
      String var7 = this.entitiesID[var2.getTypeID()];
      if (var7 != null) {
         ICommandSender var8 = new ICommandSender() {
            @Override
            public String getName() {
               return var1.getName();
            }

            @Override
            public IChatComponent getDisplayName() {
               return var1.getDisplayName();
            }

            @Override
            public void addChatMessage(IChatComponent var1x) {
               var1.addChatMessage(var1x);
            }

            @Override
            public boolean canCommandSenderUseCommand(int var1x, String var2x) {
               return true;
            }

            @Override
            public BlockPos getPosition() {
               return var1.getPosition();
            }

            @Override
            public Vec3 getPositionVector() {
               return var1.getPositionVector();
            }

            @Override
            public World getEntityWorld() {
               return var1.getEntityWorld();
            }

            @Override
            public Entity getCommandSenderEntity() {
               return var1.getCommandSenderEntity();
            }

            @Override
            public boolean sendCommandFeedback() {
               return var1.sendCommandFeedback();
            }

            @Override
            public void setCommandStat(CommandResultStats.Type var1x, int var2x) {
               var1.setCommandStat(var1x, var2x);
            }
         };

         String var9;
         try {
            var9 = CommandBase.getEntityName(var8, var7);
         } catch (EntityNotFoundException var14) {
            return;
         }

         String var10 = this.objectives[var2.getTypeID()];
         if (var10 != null) {
            Scoreboard var11 = var1.getEntityWorld().getScoreboard();
            ScoreObjective var12 = var11.getObjective(var10);
            if (var12 != null && var11.entityHasObjective(var9, var12)) {
               Score var13 = var11.getValueFromObjective(var9, var12);
               var13.setScorePoints(var3);
            }
         }
      }
   }

   public void readStatsFromNBT(NBTTagCompound var1) {
      if (var1.hasKey("CommandStats", 10)) {
         NBTTagCompound var5 = var1.getCompoundTag("CommandStats");

         for (CommandResultStats.Type var9 : CommandResultStats.Type.values()) {
            String var10 = var9.getTypeName() + "Name";
            String var11 = var9.getTypeName() + "Objective";
            if (var5.hasKey(var10, 8) && var5.hasKey(var11, 8)) {
               String var12 = var5.getString(var10);
               String var13 = var5.getString(var11);
               setScoreBoardStat(this, var9, var12, var13);
            }
         }
      }
   }

   public void writeStatsToNBT(NBTTagCompound var1) {
      NBTTagCompound var5 = new NBTTagCompound();

      for (CommandResultStats.Type var9 : CommandResultStats.Type.values()) {
         String var10 = this.entitiesID[var9.getTypeID()];
         String var11 = this.objectives[var9.getTypeID()];
         if (var10 != null && var11 != null) {
            var5.setString(var9.getTypeName() + "Name", var10);
            var5.setString(var9.getTypeName() + "Objective", var11);
         }
      }

      if (!var5.hasNoTags()) {
         var1.setTag("CommandStats", var5);
      }
   }

   public static void setScoreBoardStat(CommandResultStats var0, CommandResultStats.Type var1, String var2, String var3) {
      if (var2 != null && var2.length() != 0 && var3 != null && var3.length() != 0) {
         if (var0.entitiesID == STRING_RESULT_TYPES || var0.objectives == STRING_RESULT_TYPES) {
            var0.entitiesID = new String[NUM_RESULT_TYPES];
            var0.objectives = new String[NUM_RESULT_TYPES];
         }

         var0.entitiesID[var1.getTypeID()] = var2;
         var0.objectives[var1.getTypeID()] = var3;
      } else {
         removeScoreBoardStat(var0, var1);
      }
   }

   private static void removeScoreBoardStat(CommandResultStats var0, CommandResultStats.Type var1) {
      if (var0.entitiesID != STRING_RESULT_TYPES && var0.objectives != STRING_RESULT_TYPES) {
         var0.entitiesID[var1.getTypeID()] = null;
         var0.objectives[var1.getTypeID()] = null;
         boolean var5 = true;

         for (CommandResultStats.Type var9 : CommandResultStats.Type.values()) {
            if (var0.entitiesID[var9.getTypeID()] != null && var0.objectives[var9.getTypeID()] != null) {
               var5 = false;
               break;
            }
         }

         if (var5) {
            var0.entitiesID = STRING_RESULT_TYPES;
            var0.objectives = STRING_RESULT_TYPES;
         }
      }
   }

   public void addAllStats(CommandResultStats var1) {
      for (CommandResultStats.Type var8 : CommandResultStats.Type.values()) {
         setScoreBoardStat(this, var8, var1.entitiesID[var8.getTypeID()], var1.objectives[var8.getTypeID()]);
      }
   }

   public enum Type {
      SUCCESS_COUNT(0, "SuccessCount"),
      AFFECTED_BLOCKS(1, "AffectedBlocks"),
      AFFECTED_ENTITIES(2, "AffectedEntities"),
      AFFECTED_ITEMS(3, "AffectedItems"),
      QUERY_RESULT(4, "QueryResult");

      public final int typeID;
      public final String typeName;
      private static final long a = 39213673372630L;

      Type(int var3, String var4) {
         this.typeID = var3;
         this.typeName = var4;
      }

      public int getTypeID() {
         return this.typeID;
      }

      public String getTypeName() {
         return this.typeName;
      }

      public static String[] getTypeNames() {
         String[] var3 = new String[values().length];
         int var4 = 0;

         for (CommandResultStats.Type var8 : values()) {
            var3[var4++] = var8.getTypeName();
         }

         return var3;
      }

      public static CommandResultStats.Type getTypeByName(String var0) {
         for (CommandResultStats.Type var7 : values()) {
            if (var7.getTypeName().equals(var0)) {
               return var7;
            }
         }

         return null;
      }
   }
}
