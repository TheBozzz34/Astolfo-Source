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

   public static EntityPlayerMP matchOnePlayer(ICommandSender sender, String token) {
      return matchOneEntity(sender, token, EntityPlayerMP.class);
   }

   public static <T extends Entity> T matchOneEntity(ICommandSender sender, String token, Class<? extends T> targetClass) {
      List list = matchEntities(sender, token, targetClass);
      return (T)(list.size() == 1 ? list.get(0) : null);
   }

   public static IChatComponent matchEntitiesToChatComponent(ICommandSender sender, String token) {
      List list = matchEntities(sender, token, Entity.class);
      if (list.isEmpty()) {
         return null;
      }

      ArrayList arrayList = Lists.newArrayList();

      for (Object object : list) {
         Entity entity = (Entity)object;
         arrayList.add(entity.getDisplayName());
      }

      return CommandBase.join(arrayList);
   }

   public static <T extends Entity> List<T> matchEntities(ICommandSender sender, String token, Class<? extends T> targetClass) {
      Matcher matcher2 = tokenPattern.matcher(token);
      if (matcher2.matches() && sender.canCommandSenderUseCommand(1, "@")) {
         Map map = getArgumentMap(matcher2.group(2));
         if (!isEntityTypeValid(sender, map)) {
            return Collections.emptyList();
         }

         String string = matcher2.group(1);
         BlockPos blockPos = func_179664_b(map, sender.getPosition());
         List list = getWorlds(sender, map);
         ArrayList arrayList = Lists.newArrayList();

         for (Object object : list) {
            World world = (World)object;
            if (world != null) {
               ArrayList arrayList2 = Lists.newArrayList();
               arrayList2.addAll(func_179663_a(map, string));
               arrayList2.addAll(getXpLevelPredicates(map));
               arrayList2.addAll(getGamemodePredicates(map));
               arrayList2.addAll(getTeamPredicates(map));
               arrayList2.addAll(getScorePredicates(map));
               arrayList2.addAll(getNamePredicates(map));
               arrayList2.addAll(func_180698_a(map, blockPos));
               arrayList2.addAll(getRotationsPredicates(map));
               arrayList.addAll(filterResults(map, targetClass, arrayList2, string, world, blockPos));
            }
         }

         return func_179658_a(arrayList, map, sender, targetClass, string, blockPos);
      } else {
         return Collections.emptyList();
      }
   }

   private static List<World> getWorlds(ICommandSender sender, Map<String, String> argumentMap) {
      ArrayList arrayList = Lists.newArrayList();
      if (func_179665_h(argumentMap)) {
         arrayList.add(sender.getEntityWorld());
      } else {
         Collections.addAll(arrayList, MinecraftServer.getServer().worldServers);
      }

      return arrayList;
   }

   private static <T extends Entity> boolean isEntityTypeValid(ICommandSender commandSender, Map<String, String> params) {
      String string = func_179651_b(params, "type");
      string = string != null && string.startsWith("!") ? string.substring(1) : string;
      if (string != null && !EntityList.isStringValidEntityName(string)) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.entity.invalidType", string);
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         commandSender.addChatMessage(chatComponentTranslation);
         return false;
      } else {
         return true;
      }
   }

   private static List<Predicate<Entity>> func_179663_a(Map<String, String> map, String string) {
      ArrayList arrayList = Lists.newArrayList();
      String string2 = func_179651_b(map, "type");
      final boolean bl = string2 != null && string2.startsWith("!");
      if (bl) {
         string2 = string2.substring(1);
      }

      boolean bl2 = !string.equals("e");
      boolean bl3 = string.equals("r") && string2 != null;
      if ((string2 == null || !string.equals("e")) && !bl3) {
         if (bl2) {
            arrayList.add(new Predicate<Entity>() {
               public boolean apply(Entity entity) {
                  return entity instanceof EntityPlayer;
               }
            });
         }
      } else {
         final String string3 = string2;
         arrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               return EntityList.isStringEntityName(entity, string3) != bl;
            }
         });
      }

      return arrayList;
   }

   private static List<Predicate<Entity>> getXpLevelPredicates(Map<String, String> map) {
      ArrayList arrayList = Lists.newArrayList();
      final int i = parseIntWithDefault(map, "lm", -1);
      final int j = parseIntWithDefault(map, "l", -1);
      if (i > -1 || j > -1) {
         arrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               if (!(entity instanceof EntityPlayerMP)) {
                  return false;
               }

               EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entity;
               return (i <= -1 || entityPlayerMP.experienceLevel >= i) && (j <= -1 || entityPlayerMP.experienceLevel <= j);
            }
         });
      }

      return arrayList;
   }

   private static List<Predicate<Entity>> getGamemodePredicates(Map<String, String> map) {
      ArrayList arrayList = Lists.newArrayList();
      final int i = parseIntWithDefault(map, "m", WorldSettings.GameType.NOT_SET.getID());
      if (i != WorldSettings.GameType.NOT_SET.getID()) {
         arrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               if (!(entity instanceof EntityPlayerMP)) {
                  return false;
               }

               EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entity;
               return entityPlayerMP.theItemInWorldManager.getGameType().getID() == i;
            }
         });
      }

      return arrayList;
   }

   private static List<Predicate<Entity>> getTeamPredicates(Map<String, String> map) {
      ArrayList arrayList = Lists.newArrayList();
      String string = func_179651_b(map, "team");
      final boolean bl = string != null && string.startsWith("!");
      if (bl) {
         string = string.substring(1);
      }

      if (string != null) {
         final String string2 = string;
         arrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               if (!(entity instanceof EntityLivingBase)) {
                  return false;
               }

               EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
               Team team2 = entityLivingBase.getTeam();
               String string3 = team2 == null ? "" : team2.getRegisteredName();
               return string3.equals(string2) != bl;
            }
         });
      }

      return arrayList;
   }

   private static List<Predicate<Entity>> getScorePredicates(Map<String, String> map) {
      ArrayList arrayList = Lists.newArrayList();
      final Map map2 = func_96560_a(map);
      if (map2 != null && map2.size() > 0) {
         arrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               Scoreboard scoreboard = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();

               for (Object object : map2.entrySet()) {
                  Entry entry = (Entry)object;
                  String string = (String)entry.getKey();
                  boolean bl = false;
                  if (string.endsWith("_min") && string.length() > 4) {
                     bl = true;
                     string = string.substring(0, string.length() - 4);
                  }

                  ScoreObjective scoreObjective = scoreboard.getObjective(string);
                  if (scoreObjective == null) {
                     return false;
                  }

                  String string2 = entity instanceof EntityPlayerMP ? entity.getName() : entity.getUniqueID().toString();
                  if (!scoreboard.entityHasObjective(string2, scoreObjective)) {
                     return false;
                  }

                  Score score = scoreboard.getValueFromObjective(string2, scoreObjective);
                  int i = score.getScorePoints();
                  if (i < (Integer)entry.getValue() && bl) {
                     return false;
                  }

                  if (i > (Integer)entry.getValue() && !bl) {
                     return false;
                  }
               }

               return true;
            }
         });
      }

      return arrayList;
   }

   private static List<Predicate<Entity>> getNamePredicates(Map<String, String> map) {
      ArrayList arrayList = Lists.newArrayList();
      String string = func_179651_b(map, "name");
      final boolean bl = string != null && string.startsWith("!");
      if (bl) {
         string = string.substring(1);
      }

      if (string != null) {
         final String string2 = string;
         arrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               return entity.getName().equals(string2) != bl;
            }
         });
      }

      return arrayList;
   }

   private static List<Predicate<Entity>> func_180698_a(Map<String, String> map, final BlockPos blockPos) {
      ArrayList arrayList = Lists.newArrayList();
      final int i = parseIntWithDefault(map, "rm", -1);
      final int j = parseIntWithDefault(map, "r", -1);
      if (blockPos != null && (i >= 0 || j >= 0)) {
         final int k = i * i;
         final int l = j * j;
         arrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               int m = (int)entity.getDistanceSqToCenter(blockPos);
               return (i < 0 || m >= k) && (j < 0 || m <= l);
            }
         });
      }

      return arrayList;
   }

   private static List<Predicate<Entity>> getRotationsPredicates(Map<String, String> map) {
      ArrayList arrayList = Lists.newArrayList();
      if (map.containsKey("rym") || map.containsKey("ry")) {
         final int i = func_179650_a(parseIntWithDefault(map, "rym", 0));
         final int j = func_179650_a(parseIntWithDefault(map, "ry", 359));
         arrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               int k = PlayerSelector.func_179650_a((int)Math.floor(entity.rotationYaw));
               return i > j ? k >= i || k <= j : k >= i && k <= j;
            }
         });
      }

      if (map.containsKey("rxm") || map.containsKey("rx")) {
         final int l = func_179650_a(parseIntWithDefault(map, "rxm", 0));
         final int m = func_179650_a(parseIntWithDefault(map, "rx", 359));
         arrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               int n = PlayerSelector.func_179650_a((int)Math.floor(entity.rotationPitch));
               return l > m ? n >= l || n <= m : n >= l && n <= m;
            }
         });
      }

      return arrayList;
   }

   private static <T extends Entity> List<T> filterResults(
      Map<String, String> params, Class<? extends T> entityClass, List<Predicate<Entity>> inputList, String typeIn, World worldIn, BlockPos position
   ) {
      ArrayList arrayList = Lists.newArrayList();
      String string = func_179651_b(params, "type");
      string = string != null && string.startsWith("!") ? string.substring(1) : string;
      boolean bl = !typeIn.equals("e");
      boolean bl2 = typeIn.equals("r") && string != null;
      int i = parseIntWithDefault(params, "dx", 0);
      int j = parseIntWithDefault(params, "dy", 0);
      int k = parseIntWithDefault(params, "dz", 0);
      int l = parseIntWithDefault(params, "r", -1);
      Predicate predicate = Predicates.and(inputList);
      Predicate predicate2 = Predicates.and(EntitySelectors.selectAnything, predicate);
      if (position != null) {
         int m = worldIn.playerEntities.size();
         int n = worldIn.loadedEntityList.size();
         boolean bl3 = m < n / 16;
         if (params.containsKey("dx") || params.containsKey("dy") || params.containsKey("dz")) {
            final AxisAlignedBB axisAlignedBB = func_179661_a(position, i, j, k);
            if (bl && bl3 && !bl2) {
               Predicate predicate3 = new Predicate<Entity>() {
                  public boolean apply(Entity entity) {
                     return entity.posX >= axisAlignedBB.minX && entity.posY >= axisAlignedBB.minY && entity.posZ >= axisAlignedBB.minZ
                        ? entity.posX < axisAlignedBB.maxX && entity.posY < axisAlignedBB.maxY && entity.posZ < axisAlignedBB.maxZ
                        : false;
                  }
               };
               arrayList.addAll(worldIn.getPlayers(entityClass, Predicates.and(predicate2, predicate3)));
            } else {
               arrayList.addAll(worldIn.getEntitiesWithinAABB(entityClass, axisAlignedBB, predicate2));
            }
         } else if (l >= 0) {
            AxisAlignedBB axisAlignedBB2 = new AxisAlignedBB(
               position.getX() - l,
               position.getY() - l,
               position.getZ() - l,
               position.getX() + l + 1,
               position.getY() + l + 1,
               position.getZ() + l + 1
            );
            if (bl && bl3 && !bl2) {
               arrayList.addAll(worldIn.getPlayers(entityClass, predicate2));
            } else {
               arrayList.addAll(worldIn.getEntitiesWithinAABB(entityClass, axisAlignedBB2, predicate2));
            }
         } else if (typeIn.equals("a")) {
            arrayList.addAll(worldIn.getPlayers(entityClass, predicate));
         } else if (typeIn.equals("p") || typeIn.equals("r") && !bl2) {
            arrayList.addAll(worldIn.getPlayers(entityClass, predicate2));
         } else {
            arrayList.addAll(worldIn.getEntities(entityClass, predicate2));
         }
      } else if (typeIn.equals("a")) {
         arrayList.addAll(worldIn.getPlayers(entityClass, predicate));
      } else if (typeIn.equals("p") || typeIn.equals("r") && !bl2) {
         arrayList.addAll(worldIn.getPlayers(entityClass, predicate2));
      } else {
         arrayList.addAll(worldIn.getEntities(entityClass, predicate2));
      }

      return arrayList;
   }

   private static <T extends Entity> List<T> func_179658_a(
      List<T> list, Map<String, String> map, ICommandSender iCommandSender, Class<? extends T> class2, String string, final BlockPos blockPos
   ) {
      int i = parseIntWithDefault(map, "c", !string.equals("a") && !string.equals("e") ? 1 : 0);
      if (!string.equals("p") && !string.equals("a") && !string.equals("e")) {
         if (string.equals("r")) {
            Collections.shuffle(list);
         }
      } else if (blockPos != null) {
         Collections.sort(list, new Comparator<Entity>() {
            public int compare(Entity entity, Entity entity2) {
               return ComparisonChain.start().compare(entity.getDistanceSq(blockPos), entity2.getDistanceSq(blockPos)).result();
            }
         });
      }

      Entity entity3 = iCommandSender.getCommandSenderEntity();
      if (entity3 != null && class2.isAssignableFrom(entity3.getClass()) && i == 1 && list.contains(entity3) && !"r".equals(string)) {
         list = (java.util.ArrayList)(Lists.newArrayList(entity3));
      }

      if (i != 0) {
         if (i < 0) {
            Collections.reverse(list);
         }

         list = list.subList(0, Math.min(Math.abs(i), list.size()));
      }

      return list;
   }

   private static AxisAlignedBB func_179661_a(BlockPos blockPos, int i, int j, int k) {
      boolean bl = i < 0;
      boolean bl2 = j < 0;
      boolean bl3 = k < 0;
      int l = blockPos.getX() + (bl ? i : 0);
      int m = blockPos.getY() + (bl2 ? j : 0);
      int n = blockPos.getZ() + (bl3 ? k : 0);
      int i2 = blockPos.getX() + (bl ? 0 : i) + 1;
      int i3 = blockPos.getY() + (bl2 ? 0 : j) + 1;
      int i4 = blockPos.getZ() + (bl3 ? 0 : k) + 1;
      return new AxisAlignedBB(l, m, n, i2, i3, i4);
   }

   public static int func_179650_a(int i) {
      i %= 360;
      if (i >= 160) {
         i -= 360;
      }

      if (i < 0) {
         i += 360;
      }

      return i;
   }

   private static BlockPos func_179664_b(Map<String, String> map, BlockPos blockPos) {
      return new BlockPos(
         parseIntWithDefault(map, "x", blockPos.getX()), parseIntWithDefault(map, "y", blockPos.getY()), parseIntWithDefault(map, "z", blockPos.getZ())
      );
   }

   private static boolean func_179665_h(Map<String, String> map) {
      for (String string : WORLD_BINDING_ARGS) {
         if (map.containsKey(string)) {
            return true;
         }
      }

      return false;
   }

   private static int parseIntWithDefault(Map<String, String> map, String string, int i) {
      return map.containsKey(string) ? MathHelper.parseIntWithDefault((String)map.get(string), i) : i;
   }

   private static String func_179651_b(Map<String, String> map, String string) {
      return (String)map.get(string);
   }

   public static Map<String, Integer> func_96560_a(Map<String, String> map) {
      HashMap hashMap = Maps.newHashMap();

      for (String string : map.keySet()) {
         if (string.startsWith("score_") && string.length() > "score_".length()) {
            hashMap.put(string.substring("score_".length()), MathHelper.parseIntWithDefault((String)map.get(string), 1));
         }
      }

      return hashMap;
   }

   public static boolean matchesMultiplePlayers(String string) {
      Matcher matcher2 = tokenPattern.matcher(string);
      if (!matcher2.matches()) {
         return false;
      }

      Map map = getArgumentMap(matcher2.group(2));
      String string2 = matcher2.group(1);
      int i = !"a".equals(string2) && !"e".equals(string2) ? 1 : 0;
      return parseIntWithDefault(map, "c", i) != 1;
   }

   public static boolean hasArguments(String string) {
      return tokenPattern.matcher(string).matches();
   }

   private static Map<String, String> getArgumentMap(String argumentString) {
      HashMap hashMap = Maps.newHashMap();
      if (argumentString == null) {
         return hashMap;
      }

      int i = 0;
      int j = -1;

      for (Matcher matcher2 = intListPattern.matcher(argumentString); matcher2.find(); j = matcher2.end()) {
         String string = null;
         switch (i++) {
            case 0:
               string = "x";
               break;
            case 1:
               string = "y";
               break;
            case 2:
               string = "z";
               break;
            case 3:
               string = "r";
         }

         if (string != null && matcher2.group(1).length() > 0) {
            hashMap.put(string, matcher2.group(1));
         }
      }

      if (j < argumentString.length()) {
         Matcher matcher3 = keyValueListPattern.matcher(j == -1 ? argumentString : argumentString.substring(j));

         while (matcher3.find()) {
            hashMap.put(matcher3.group(1), matcher3.group(2));
         }
      }

      return hashMap;
   }
}
