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

   public void setCommandStatScore(final ICommandSender sender, CommandResultStats.Type resultTypeIn, int scorePoint) {
      String string = this.entitiesID[resultTypeIn.getTypeID()];
      if (string != null) {
         ICommandSender iCommandSender = new ICommandSender() {
            @Override
            public String getName() {
               return sender.getName();
            }

            @Override
            public IChatComponent getDisplayName() {
               return sender.getDisplayName();
            }

            @Override
            public void addChatMessage(IChatComponent component) {
               sender.addChatMessage(component);
            }

            @Override
            public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
               return true;
            }

            @Override
            public BlockPos getPosition() {
               return sender.getPosition();
            }

            @Override
            public Vec3 getPositionVector() {
               return sender.getPositionVector();
            }

            @Override
            public World getEntityWorld() {
               return sender.getEntityWorld();
            }

            @Override
            public Entity getCommandSenderEntity() {
               return sender.getCommandSenderEntity();
            }

            @Override
            public boolean sendCommandFeedback() {
               return sender.sendCommandFeedback();
            }

            @Override
            public void setCommandStat(CommandResultStats.Type typeIn, int amount) {
               sender.setCommandStat(typeIn, amount);
            }
         };

         String string2;
         try {
            string2 = CommandBase.getEntityName(iCommandSender, string);
         } catch (EntityNotFoundException entityNotFoundException) {
            return;
         }

         String string3 = this.objectives[resultTypeIn.getTypeID()];
         if (string3 != null) {
            Scoreboard scoreboard = sender.getEntityWorld().getScoreboard();
            ScoreObjective scoreObjective = scoreboard.getObjective(string3);
            if (scoreObjective != null && scoreboard.entityHasObjective(string2, scoreObjective)) {
               Score score = scoreboard.getValueFromObjective(string2, scoreObjective);
               score.setScorePoints(scorePoint);
            }
         }
      }
   }

   public void readStatsFromNBT(NBTTagCompound tagcompound) {
      if (tagcompound.hasKey("CommandStats", 10)) {
         NBTTagCompound nbtTagCompound = tagcompound.getCompoundTag("CommandStats");

         for (CommandResultStats.Type type : CommandResultStats.Type.values()) {
            String string = type.getTypeName() + "Name";
            String string2 = type.getTypeName() + "Objective";
            if (nbtTagCompound.hasKey(string, 8) && nbtTagCompound.hasKey(string2, 8)) {
               String string3 = nbtTagCompound.getString(string);
               String string4 = nbtTagCompound.getString(string2);
               setScoreBoardStat(this, type, string3, string4);
            }
         }
      }
   }

   public void writeStatsToNBT(NBTTagCompound tagcompound) {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();

      for (CommandResultStats.Type type : CommandResultStats.Type.values()) {
         String string = this.entitiesID[type.getTypeID()];
         String string2 = this.objectives[type.getTypeID()];
         if (string != null && string2 != null) {
            nbtTagCompound.setString(type.getTypeName() + "Name", string);
            nbtTagCompound.setString(type.getTypeName() + "Objective", string2);
         }
      }

      if (!nbtTagCompound.hasNoTags()) {
         tagcompound.setTag("CommandStats", nbtTagCompound);
      }
   }

   public static void setScoreBoardStat(CommandResultStats stats, CommandResultStats.Type resultType, String entityID, String objectiveName) {
      if (entityID != null && entityID.length() != 0 && objectiveName != null && objectiveName.length() != 0) {
         if (stats.entitiesID == STRING_RESULT_TYPES || stats.objectives == STRING_RESULT_TYPES) {
            stats.entitiesID = new String[NUM_RESULT_TYPES];
            stats.objectives = new String[NUM_RESULT_TYPES];
         }

         stats.entitiesID[resultType.getTypeID()] = entityID;
         stats.objectives[resultType.getTypeID()] = objectiveName;
      } else {
         removeScoreBoardStat(stats, resultType);
      }
   }

   private static void removeScoreBoardStat(CommandResultStats resultStatsIn, CommandResultStats.Type resultTypeIn) {
      if (resultStatsIn.entitiesID != STRING_RESULT_TYPES && resultStatsIn.objectives != STRING_RESULT_TYPES) {
         resultStatsIn.entitiesID[resultTypeIn.getTypeID()] = null;
         resultStatsIn.objectives[resultTypeIn.getTypeID()] = null;
         boolean bl = true;

         for (CommandResultStats.Type type : CommandResultStats.Type.values()) {
            if (resultStatsIn.entitiesID[type.getTypeID()] != null && resultStatsIn.objectives[type.getTypeID()] != null) {
               bl = false;
               break;
            }
         }

         if (bl) {
            resultStatsIn.entitiesID = STRING_RESULT_TYPES;
            resultStatsIn.objectives = STRING_RESULT_TYPES;
         }
      }
   }

   public void addAllStats(CommandResultStats resultStatsIn) {
      for (CommandResultStats.Type type : CommandResultStats.Type.values()) {
         setScoreBoardStat(this, type, resultStatsIn.entitiesID[type.getTypeID()], resultStatsIn.objectives[type.getTypeID()]);
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

      Type(int id, String name) {
         this.typeID = id;
         this.typeName = name;
      }

      public int getTypeID() {
         return this.typeID;
      }

      public String getTypeName() {
         return this.typeName;
      }

      public static String[] getTypeNames() {
         String[] strings = new String[values().length];
         int i = 0;

         for (CommandResultStats.Type type : values()) {
            strings[i++] = type.getTypeName();
         }

         return strings;
      }

      public static CommandResultStats.Type getTypeByName(String name) {
         for (CommandResultStats.Type type : values()) {
            if (type.getTypeName().equals(name)) {
               return type;
            }
         }

         return null;
      }
   }
}
