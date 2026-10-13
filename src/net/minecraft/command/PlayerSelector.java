package net.minecraft.command;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class PlayerSelector {
   private static final Pattern tokenPattern = Pattern.compile("^@([pare])(?:\\[([\\w=,!-]*)\\])?$");
   private static final Pattern intListPattern = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
   private static final Pattern keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
   private static final Set<String> WORLD_BINDING_ARGS = Sets.newHashSet("x", "y", "z", "dx", "dy", "dz", "rm", "r");
   private static final long b = 101900038582892L;

   public static EntityPlayerMP matchOnePlayer(ICommandSender var0, String var1) {
      return matchOneEntity(var0, var1, EntityPlayerMP.class);
   }

   public static <T extends Entity> T matchOneEntity(ICommandSender var0, String var1, Class<? extends T> var2) {
      List var6 = matchEntities(var0, var1, var2);
      return (T)(var6.size() == 1 ? var6.get(0) : null);
   }

   public static IChatComponent matchEntitiesToChatComponent(ICommandSender var0, String var1) {
      List var5 = matchEntities(var0, var1, Entity.class);
      if (var5.isEmpty()) {
         return null;
      }

      ArrayList var6 = Lists.newArrayList();

      for (Object var8$raw : var5) {
         Entity var8 = (Entity)var8$raw;
         var6.add(var8.getDisplayName());
      }

      return CommandBase.join(var6);
   }

   public static <T extends Entity> List<T> matchEntities(ICommandSender var0, String var1, Class<? extends T> var2) {
      Matcher var6 = tokenPattern.matcher(var1);
      if (var6.matches() && var0.canCommandSenderUseCommand(1, "@")) {
         Map var7 = getArgumentMap(var6.group(2));
         if (!isEntityTypeValid(var0, var7)) {
            return Collections.emptyList();
         }

         String var8 = var6.group(1);
         BlockPos var9 = func_179664_b(var7, var0.getPosition());
         List var10 = getWorlds(var0, var7);
         ArrayList var11 = Lists.newArrayList();

         for (Object var13$raw : var10) {
            World var13 = (World)var13$raw;
            if (var13 != null) {
               ArrayList var14 = Lists.newArrayList();
               var14.addAll(func_179663_a(var7, var8));
               var14.addAll(getXpLevelPredicates(var7));
               var14.addAll(getGamemodePredicates(var7));
               var14.addAll(getTeamPredicates(var7));
               var14.addAll(getScorePredicates(var7));
               var14.addAll(getNamePredicates(var7));
               var14.addAll(func_180698_a(var7, var9));
               var14.addAll(getRotationsPredicates(var7));
               var11.addAll(filterResults(var7, var2, var14, var8, var13, var9));
            }
         }

         return func_179658_a(var11, var7, var0, var2, var8, var9);
      } else {
         return Collections.emptyList();
      }
   }

   private static List<World> getWorlds(ICommandSender var0, Map<String, String> var1) {
      ArrayList var5 = Lists.newArrayList();
      if (func_179665_h(var1)) {
         var5.add(var0.getEntityWorld());
      } else {
         Collections.addAll(var5, MinecraftServer.getServer().worldServers);
      }

      return var5;
   }

   private static <T extends Entity> boolean isEntityTypeValid(ICommandSender var0, Map<String, String> var1) {
      String var5 = func_179651_b(var1, "type");
      var5 = var5 != null && var5.startsWith("!") ? var5.substring(1) : var5;
      if (var5 != null && !EntityList.isStringValidEntityName(var5)) {
         ChatComponentTranslation var6 = new ChatComponentTranslation("commands.generic.entity.invalidType", var5);
         var6.getChatStyle().setColor(EnumChatFormatting.RED);
         var0.addChatMessage(var6);
         return false;
      } else {
         return true;
      }
   }

   private static List<Predicate<Entity>> func_179663_a(Map<String, String> var0, String var1) {
      ArrayList var5 = Lists.newArrayList();
      String var6 = func_179651_b(var0, "type");
      final boolean var7 = var6 != null && var6.startsWith("!");
      if (var7) {
         var6 = var6.substring(1);
      }

      boolean var8 = !var1.equals("e");
      boolean var9 = var1.equals("r") && var6 != null;
      if ((var6 == null || !var1.equals("e")) && !var9) {
         if (var8) {
            var5.add(new Predicate<Entity>() {
               public boolean apply(Entity var1) {
                  return var1 instanceof EntityPlayer;
               }
            });
         }
      } else {
         final String var10 = var6;
         var5.add(new Predicate<Entity>() {
            private static final long a = 30393961879540L;

            public boolean apply(Entity var1) {
               return EntityList.isStringEntityName(var1, var10) != var7;
            }
         });
      }

      return var5;
   }

   private static List<Predicate<Entity>> getXpLevelPredicates(Map<String, String> var0) {
      ArrayList var4 = Lists.newArrayList();
      final int var5 = parseIntWithDefault(var0, "lm", -1);
      final int var6 = parseIntWithDefault(var0, "l", -1);
      if (var5 > -1 || var6 > -1) {
         var4.add(new Predicate<Entity>() {
            private static final long a = 80482689403247L;

            public boolean apply(Entity var1) {
               if (!(var1 instanceof EntityPlayerMP)) {
                  return false;
               }

               EntityPlayerMP var5x = (EntityPlayerMP)var1;
               return (var5 <= -1 || var5x.experienceLevel >= var5) && (var6 <= -1 || var5x.experienceLevel <= var6);
            }
         });
      }

      return var4;
   }

   private static List<Predicate<Entity>> getGamemodePredicates(Map<String, String> var0) {
      ArrayList var4 = Lists.newArrayList();
      final int var5 = parseIntWithDefault(var0, "m", WorldSettings.GameType.NOT_SET.getID());
      if (var5 != WorldSettings.GameType.NOT_SET.getID()) {
         var4.add(new Predicate<Entity>() {
            private static final long a = 8942488599788L;

            public boolean apply(Entity var1) {
               if (!(var1 instanceof EntityPlayerMP)) {
                  return false;
               }

               EntityPlayerMP var5x = (EntityPlayerMP)var1;
               return var5x.theItemInWorldManager.getGameType().getID() == var5;
            }
         });
      }

      return var4;
   }

   private static List<Predicate<Entity>> getTeamPredicates(Map<String, String> var0) {
      ArrayList var4 = Lists.newArrayList();
      String var5 = func_179651_b(var0, "team");
      final boolean var6 = var5 != null && var5.startsWith("!");
      if (var6) {
         var5 = var5.substring(1);
      }

      if (var5 != null) {
         final String var7 = var5;
         var4.add(new Predicate<Entity>() {
            private static final long a = 84524590951163L;

            public boolean apply(Entity var1) {
               if (!(var1 instanceof EntityLivingBase)) {
                  return false;
               }

               EntityLivingBase var5x = (EntityLivingBase)var1;
               Team var6x = var5x.getTeam();
               String var7x = var6x == null ? "" : var6x.getRegisteredName();
               return var7x.equals(var7) != var6;
            }
         });
      }

      return var4;
   }

   private static List<Predicate<Entity>> getScorePredicates(Map<String, String> var0) {
      ArrayList var4 = Lists.newArrayList();
      final Map var5 = func_96560_a(var0);
      if (var5 != null && var5.size() > 0) {
         var4.add(new Predicate<Entity>() {
            private static final long a = 82118467861120L;

            public boolean apply(Entity var1) {
               Scoreboard var5x = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();

               for (Object var7$raw : var5.entrySet()) {
                  Entry var7 = (Entry)var7$raw;
                  String var8 = (String)var7.getKey();
                  boolean var9 = false;
                  if (var8.endsWith("_min") && var8.length() > 4) {
                     var9 = true;
                     var8 = var8.substring(0, var8.length() - 4);
                  }

                  ScoreObjective var10 = var5x.getObjective(var8);
                  if (var10 == null) {
                     return false;
                  }

                  String var11 = var1 instanceof EntityPlayerMP ? var1.getName() : var1.getUniqueID().toString();
                  if (!var5x.entityHasObjective(var11, var10)) {
                     return false;
                  }

                  Score var12 = var5x.getValueFromObjective(var11, var10);
                  int var13 = var12.getScorePoints();
                  if (var13 < (Integer)var7.getValue() && var9) {
                     return false;
                  }

                  if (var13 > (Integer)var7.getValue() && !var9) {
                     return false;
                  }
               }

               return true;
            }
         });
      }

      return var4;
   }

   private static List<Predicate<Entity>> getNamePredicates(Map<String, String> var0) {
      ArrayList var4 = Lists.newArrayList();
      String var5 = func_179651_b(var0, "name");
      final boolean var6 = var5 != null && var5.startsWith("!");
      if (var6) {
         var5 = var5.substring(1);
      }

      if (var5 != null) {
         final String var7 = var5;
         var4.add(new Predicate<Entity>() {
            private static final long a = 92993521027902L;

            public boolean apply(Entity var1) {
               return var1.getName().equals(var7) != var6;
            }
         });
      }

      return var4;
   }

   private static List<Predicate<Entity>> func_180698_a(Map<String, String> var0, final BlockPos var1) {
      ArrayList var5 = Lists.newArrayList();
      final int var6 = parseIntWithDefault(var0, "rm", -1);
      final int var7 = parseIntWithDefault(var0, "r", -1);
      if (var1 != null && (var6 >= 0 || var7 >= 0)) {
         final int var8 = var6 * var6;
         final int var9 = var7 * var7;
         var5.add(new Predicate<Entity>() {
            private static final long a = 81470410171329L;

            public boolean apply(Entity var1x) {
               int var5x = (int)var1x.getDistanceSqToCenter(var1);
               return (var6 < 0 || var5x >= var8) && (var7 < 0 || var5x <= var9);
            }
         });
      }

      return var5;
   }

   private static List<Predicate<Entity>> getRotationsPredicates(Map<String, String> var0) {
      ArrayList var4 = Lists.newArrayList();
      if (var0.containsKey("rym") || var0.containsKey("ry")) {
         final int var5 = func_179650_a(parseIntWithDefault(var0, "rym", 0));
         final int var6 = func_179650_a(parseIntWithDefault(var0, "ry", 359));
         var4.add(new Predicate<Entity>() {
            private static final long a = 39784342842745L;

            public boolean apply(Entity var1) {
               int var5x = PlayerSelector.func_179650_a((int)Math.floor(var1.rotationYaw));
               return var5 > var6 ? var5x >= var5 || var5x <= var6 : var5x >= var5 && var5x <= var6;
            }
         });
      }

      if (var0.containsKey("rxm") || var0.containsKey("rx")) {
         final int var7 = func_179650_a(parseIntWithDefault(var0, "rxm", 0));
         final int var8 = func_179650_a(parseIntWithDefault(var0, "rx", 359));
         var4.add(new Predicate<Entity>() {
            private static final long a = 32459302088491L;

            public boolean apply(Entity var1) {
               int var5 = PlayerSelector.func_179650_a((int)Math.floor(var1.rotationPitch));
               return var7 > var8 ? var5 >= var7 || var5 <= var8 : var5 >= var7 && var5 <= var8;
            }
         });
      }

      return var4;
   }

   private static <T extends Entity> List<T> filterResults(
      Map<String, String> var0, Class<? extends T> var1, List<Predicate<Entity>> var2, String var3, World var4, BlockPos var5
   ) {
      ArrayList var9 = Lists.newArrayList();
      String var10 = func_179651_b(var0, "type");
      var10 = var10 != null && var10.startsWith("!") ? var10.substring(1) : var10;
      boolean var11 = !var3.equals("e");
      boolean var12 = var3.equals("r") && var10 != null;
      int var13 = parseIntWithDefault(var0, "dx", 0);
      int var14 = parseIntWithDefault(var0, "dy", 0);
      int var15 = parseIntWithDefault(var0, "dz", 0);
      int var16 = parseIntWithDefault(var0, "r", -1);
      Predicate var17 = Predicates.and(var2);
      Predicate var18 = Predicates.and(EntitySelectors.selectAnything, var17);
      if (var5 != null) {
         int var19 = var4.playerEntities.size();
         int var20 = var4.loadedEntityList.size();
         boolean var21 = var19 < var20 / 16;
         if (var0.containsKey("dx") || var0.containsKey("dy") || var0.containsKey("dz")) {
            final AxisAlignedBB var25 = func_179661_a(var5, var13, var14, var15);
            if (var11 && var21 && !var12) {
               Predicate var23 = new Predicate<Entity>() {
                  private static final long a = 96725179485928L;

                  public boolean apply(Entity var1) {
                     return var1.posX >= var25.minX && var1.posY >= var25.minY && var1.posZ >= var25.minZ
                        ? var1.posX < var25.maxX && var1.posY < var25.maxY && var1.posZ < var25.maxZ
                        : false;
                  }
               };
               var9.addAll(var4.getPlayers(var1, Predicates.and(var18, var23)));
            } else {
               var9.addAll(var4.getEntitiesWithinAABB(var1, var25, var18));
            }
         } else if (var16 >= 0) {
            AxisAlignedBB var22 = new AxisAlignedBB(
               var5.getX() - var16, var5.getY() - var16, var5.getZ() - var16, var5.getX() + var16 + 1, var5.getY() + var16 + 1, var5.getZ() + var16 + 1
            );
            if (var11 && var21 && !var12) {
               var9.addAll(var4.getPlayers(var1, var18));
            } else {
               var9.addAll(var4.getEntitiesWithinAABB(var1, var22, var18));
            }
         } else if (var3.equals("a")) {
            var9.addAll(var4.getPlayers(var1, var17));
         } else if (var3.equals("p") || var3.equals("r") && !var12) {
            var9.addAll(var4.getPlayers(var1, var18));
         } else {
            var9.addAll(var4.getEntities(var1, var18));
         }
      } else if (var3.equals("a")) {
         var9.addAll(var4.getPlayers(var1, var17));
      } else if (var3.equals("p") || var3.equals("r") && !var12) {
         var9.addAll(var4.getPlayers(var1, var18));
      } else {
         var9.addAll(var4.getEntities(var1, var18));
      }

      return var9;
   }

   private static <T extends Entity> List<T> func_179658_a(
      List<T> var0, Map<String, String> var1, ICommandSender var2, Class<? extends T> var3, String var4, final BlockPos var5
   ) {
      int var9 = parseIntWithDefault(var1, "c", !var4.equals("a") && !var4.equals("e") ? 1 : 0);
      if (!var4.equals("p") && !var4.equals("a") && !var4.equals("e")) {
         if (var4.equals("r")) {
            Collections.shuffle(var0);
         }
      } else if (var5 != null) {
         Collections.sort(var0, new Comparator<Entity>() {
            public int compare(Entity var1, Entity var2x) {
               return ComparisonChain.start().compare(var1.getDistanceSq(var5), var2x.getDistanceSq(var5)).result();
            }
         });
      }

      Entity var10 = var2.getCommandSenderEntity();
      if (var10 != null && var3.isAssignableFrom(var10.getClass()) && var9 == 1 && var0.contains(var10) && !"r".equals(var4)) {
         var0 = (java.util.ArrayList)(Lists.newArrayList(var10));
      }

      if (var9 != 0) {
         if (var9 < 0) {
            Collections.reverse(var0);
         }

         var0 = var0.subList(0, Math.min(Math.abs(var9), var0.size()));
      }

      return var0;
   }

   private static AxisAlignedBB func_179661_a(BlockPos var0, int var1, int var2, int var3) {
      boolean var7 = var1 < 0;
      boolean var8 = var2 < 0;
      boolean var9 = var3 < 0;
      int var10 = var0.getX() + (var7 ? var1 : 0);
      int var11 = var0.getY() + (var8 ? var2 : 0);
      int var12 = var0.getZ() + (var9 ? var3 : 0);
      int var13 = var0.getX() + (var7 ? 0 : var1) + 1;
      int var14 = var0.getY() + (var8 ? 0 : var2) + 1;
      int var15 = var0.getZ() + (var9 ? 0 : var3) + 1;
      return new AxisAlignedBB(var10, var11, var12, var13, var14, var15);
   }

   public static int func_179650_a(int var0) {
      var0 %= 360;
      if (var0 >= 160) {
         var0 -= 360;
      }

      if (var0 < 0) {
         var0 += 360;
      }

      return var0;
   }

   private static BlockPos func_179664_b(Map<String, String> var0, BlockPos var1) {
      return new BlockPos(parseIntWithDefault(var0, "x", var1.getX()), parseIntWithDefault(var0, "y", var1.getY()), parseIntWithDefault(var0, "z", var1.getZ()));
   }

   private static boolean func_179665_h(Map<String, String> var0) {
      for (String var5 : WORLD_BINDING_ARGS) {
         if (var0.containsKey(var5)) {
            return true;
         }
      }

      return false;
   }

   private static int parseIntWithDefault(Map<String, String> var0, String var1, int var2) {
      return var0.containsKey(var1) ? MathHelper.parseIntWithDefault((String)var0.get(var1), var2) : var2;
   }

   private static String func_179651_b(Map<String, String> var0, String var1) {
      return (String)var0.get(var1);
   }

   public static Map<String, Integer> func_96560_a(Map<String, String> var0) {
      HashMap var4 = Maps.newHashMap();

      for (String var6 : var0.keySet()) {
         if (var6.startsWith("score_") && var6.length() > "score_".length()) {
            var4.put(var6.substring("score_".length()), MathHelper.parseIntWithDefault((String)var0.get(var6), 1));
         }
      }

      return var4;
   }

   public static boolean matchesMultiplePlayers(String var0) {
      Matcher var4 = tokenPattern.matcher(var0);
      if (!var4.matches()) {
         return false;
      }

      Map var5 = getArgumentMap(var4.group(2));
      String var6 = var4.group(1);
      int var7 = !"a".equals(var6) && !"e".equals(var6) ? 1 : 0;
      return parseIntWithDefault(var5, "c", var7) != 1;
   }

   public static boolean hasArguments(String var0) {
      return tokenPattern.matcher(var0).matches();
   }

   private static Map<String, String> getArgumentMap(String var0) {
      HashMap var4 = Maps.newHashMap();
      if (var0 == null) {
         return var4;
      }

      int var5 = 0;
      int var6 = -1;

      for (Matcher var7 = intListPattern.matcher(var0); var7.find(); var6 = var7.end()) {
         String var8 = null;
         switch (var5++) {
            case 0:
               var8 = "x";
               break;
            case 1:
               var8 = "y";
               break;
            case 2:
               var8 = "z";
               break;
            case 3:
               var8 = "r";
         }

         if (var8 != null && var7.group(1).length() > 0) {
            var4.put(var8, var7.group(1));
         }
      }

      if (var6 < var0.length()) {
         Matcher var9 = keyValueListPattern.matcher(var6 == -1 ? var0 : var0.substring(var6));

         while (var9.find()) {
            var4.put(var9.group(1), var9.group(2));
         }
      }

      return var4;
   }

   static {
      long var20 = b ^ 46200216771492L;
      long var10000 = 1328885066302306108L ^ var20;
   }
}
