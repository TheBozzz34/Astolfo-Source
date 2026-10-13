package net.minecraft.command;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class CommandBase implements ICommand {
   private static IAdminCommand theAdmin;
   private static final long a = 53717069565400L;

   public int getRequiredPermissionLevel() {
      return 4;
   }

   @Override
   public List<String> getCommandAliases() {
      return Collections.emptyList();
   }

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender var1) {
      return var1.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return null;
   }

   public static int parseInt(String var0) throws NumberInvalidException {
      try {
         return Integer.parseInt(var0);
      } catch (NumberFormatException var4) {
         throw new NumberInvalidException("commands.generic.num.invalid", var0);
      }
   }

   public static int parseInt(String var0, int var1) throws NumberInvalidException {
      return parseInt(var0, var1, Integer.MAX_VALUE);
   }

   public static int parseInt(String var0, int var1, int var2) throws NumberInvalidException {
      int var6 = parseInt(var0);
      if (var6 < var1) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", var6, var1);
      } else if (var6 > var2) {
         throw new NumberInvalidException("commands.generic.num.tooBig", var6, var2);
      } else {
         return var6;
      }
   }

   public static long parseLong(String var0) throws NumberInvalidException {
      try {
         return Long.parseLong(var0);
      } catch (NumberFormatException var4) {
         throw new NumberInvalidException("commands.generic.num.invalid", var0);
      }
   }

   public static long parseLong(String var0, long var1, long var3) throws NumberInvalidException {
      long var8 = parseLong(var0);
      if (var8 < var1) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", var8, var1);
      } else if (var8 > var3) {
         throw new NumberInvalidException("commands.generic.num.tooBig", var8, var3);
      } else {
         return var8;
      }
   }

   public static BlockPos parseBlockPos(ICommandSender var0, String[] var1, int var2, boolean var3) throws NumberInvalidException {
      BlockPos var6 = var0.getPosition();
      return new BlockPos(
         parseDouble(var6.getX(), var1[var2], -30000000, 30000000, var3),
         parseDouble(var6.getY(), var1[var2 + 1], 0, 256, false),
         parseDouble(var6.getZ(), var1[var2 + 2], -30000000, 30000000, var3)
      );
   }

   public static double parseDouble(String var0) throws NumberInvalidException {
      try {
         double var4 = Double.parseDouble(var0);
         if (!Doubles.isFinite(var4)) {
            throw new NumberInvalidException("commands.generic.num.invalid", var0);
         } else {
            return var4;
         }
      } catch (NumberFormatException var6) {
         throw new NumberInvalidException("commands.generic.num.invalid", var0);
      }
   }

   public static double parseDouble(String var0, double var1) throws NumberInvalidException {
      return parseDouble(var0, var1, Double.MAX_VALUE);
   }

   public static double parseDouble(String var0, double var1, double var3) throws NumberInvalidException {
      double var8 = parseDouble(var0);
      if (var8 < var1) {
         throw new NumberInvalidException("commands.generic.double.tooSmall", var8, var1);
      } else if (var8 > var3) {
         throw new NumberInvalidException("commands.generic.double.tooBig", var8, var3);
      } else {
         return var8;
      }
   }

   public static boolean parseBoolean(String var0) throws CommandException {
      if (var0.equals("true") || var0.equals("1")) {
         return true;
      } else if (!var0.equals("false") && !var0.equals("0")) {
         throw new CommandException("commands.generic.boolean.invalid", var0);
      } else {
         return false;
      }
   }

   public static EntityPlayerMP getCommandSenderAsPlayer(ICommandSender var0) throws PlayerNotFoundException {
      if (var0 instanceof EntityPlayerMP) {
         return (EntityPlayerMP)var0;
      } else {
         throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.");
      }
   }

   public static EntityPlayerMP getPlayer(ICommandSender var0, String var1) throws PlayerNotFoundException {
      EntityPlayerMP var5 = PlayerSelector.matchOnePlayer(var0, var1);
      if (var5 == null) {
         try {
            var5 = MinecraftServer.getServer().getConfigurationManager().getPlayerByUUID(UUID.fromString(var1));
         } catch (IllegalArgumentException var7) {
         }
      }

      if (var5 == null) {
         var5 = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(var1);
      }

      if (var5 == null) {
         throw new PlayerNotFoundException();
      } else {
         return var5;
      }
   }

   public static Entity getEntity(ICommandSender var0, String var1) throws EntityNotFoundException {
      return getEntity(var0, var1, Entity.class);
   }

   public static <T extends Entity> T getEntity(ICommandSender var0, String var1, Class<? extends T> var2) throws EntityNotFoundException {
      Entity var6 = PlayerSelector.matchOneEntity(var0, var1, var2);
      MinecraftServer var7 = MinecraftServer.getServer();
      if (var6 == null) {
         var6 = var7.getConfigurationManager().getPlayerByUsername(var1);
      }

      if (var6 == null) {
         try {
            UUID var8 = UUID.fromString(var1);
            var6 = var7.getEntityFromUuid(var8);
            if (var6 == null) {
               var6 = var7.getConfigurationManager().getPlayerByUUID(var8);
            }
         } catch (IllegalArgumentException var9) {
            throw new EntityNotFoundException("commands.generic.entity.invalidUuid");
         }
      }

      if (var6 != null && var2.isAssignableFrom(var6.getClass())) {
         return (T)var6;
      } else {
         throw new EntityNotFoundException();
      }
   }

   public static List<Entity> func_175763_c(ICommandSender var0, String var1) throws EntityNotFoundException {
      return PlayerSelector.hasArguments(var1) ? PlayerSelector.matchEntities(var0, var1, Entity.class) : Lists.newArrayList(getEntity(var0, var1));
   }

   public static String getPlayerName(ICommandSender var0, String var1) throws PlayerNotFoundException {
      try {
         return getPlayer(var0, var1).getName();
      } catch (PlayerNotFoundException var6) {
         if (PlayerSelector.hasArguments(var1)) {
            throw var6;
         } else {
            return var1;
         }
      }
   }

   public static String getEntityName(ICommandSender var0, String var1) throws EntityNotFoundException {
      try {
         return getPlayer(var0, var1).getName();
      } catch (PlayerNotFoundException var8) {
         try {
            return getEntity(var0, var1).getUniqueID().toString();
         } catch (EntityNotFoundException var7) {
            if (PlayerSelector.hasArguments(var1)) {
               throw var7;
            } else {
               return var1;
            }
         }
      }
   }

   public static IChatComponent getChatComponentFromNthArg(ICommandSender var0, String[] var1, int var2) throws CommandException, PlayerNotFoundException {
      return getChatComponentFromNthArg(var0, var1, var2, false);
   }

   public static IChatComponent getChatComponentFromNthArg(ICommandSender var0, String[] var1, int var2, boolean var3) throws PlayerNotFoundException {
      ChatComponentText var7 = new ChatComponentText("");

      for (int var8 = var2; var8 < var1.length; var8++) {
         if (var8 > var2) {
            var7.appendText(" ");
         }

         IChatComponent var9 = new ChatComponentText(var1[var8]);
         if (var3) {
            IChatComponent var10 = PlayerSelector.matchEntitiesToChatComponent(var0, var1[var8]);
            if (var10 == null) {
               if (PlayerSelector.hasArguments(var1[var8])) {
                  throw new PlayerNotFoundException();
               }
            } else {
               var9 = var10;
            }
         }

         var7.appendSibling(var9);
      }

      return var7;
   }

   public static String buildString(String[] var0, int var1) {
      StringBuilder var5 = new StringBuilder();

      for (int var6 = var1; var6 < var0.length; var6++) {
         if (var6 > var1) {
            var5.append(" ");
         }

         String var7 = var0[var6];
         var5.append(var7);
      }

      return var5.toString();
   }

   public static CommandBase.CoordinateArg parseCoordinate(double var0, String var2, boolean var3) throws NumberInvalidException {
      return parseCoordinate(var0, var2, -30000000, 30000000, var3);
   }

   public static CommandBase.CoordinateArg parseCoordinate(double var0, String var2, int var3, int var4, boolean var5) throws NumberInvalidException {
      boolean var9 = var2.startsWith("~");
      if (var9 && Double.isNaN(var0)) {
         throw new NumberInvalidException("commands.generic.num.invalid", var0);
      }

      double var10 = 0.0;
      if (!var9 || var2.length() > 1) {
         boolean var12 = var2.contains(".");
         if (var9) {
            var2 = var2.substring(1);
         }

         var10 += parseDouble(var2);
         if (!var12 && !var9 && var5) {
            var10 += 0.5;
         }
      }

      if (var3 != 0 || var4 != 0) {
         if (var10 < var3) {
            throw new NumberInvalidException("commands.generic.double.tooSmall", var10, var3);
         }

         if (var10 > var4) {
            throw new NumberInvalidException("commands.generic.double.tooBig", var10, var4);
         }
      }

      return new CommandBase.CoordinateArg(var10 + (var9 ? var0 : 0.0), var10, var9);
   }

   public static double parseDouble(double var0, String var2, boolean var3) throws NumberInvalidException {
      return parseDouble(var0, var2, -30000000, 30000000, var3);
   }

   public static double parseDouble(double var0, String var2, int var3, int var4, boolean var5) throws NumberInvalidException {
      boolean var9 = var2.startsWith("~");
      if (var9 && Double.isNaN(var0)) {
         throw new NumberInvalidException("commands.generic.num.invalid", var0);
      }

      double var10 = var9 ? var0 : 0.0;
      if (!var9 || var2.length() > 1) {
         boolean var12 = var2.contains(".");
         if (var9) {
            var2 = var2.substring(1);
         }

         var10 += parseDouble(var2);
         if (!var12 && !var9 && var5) {
            var10 += 0.5;
         }
      }

      if (var3 != 0 || var4 != 0) {
         if (var10 < var3) {
            throw new NumberInvalidException("commands.generic.double.tooSmall", var10, var3);
         }

         if (var10 > var4) {
            throw new NumberInvalidException("commands.generic.double.tooBig", var10, var4);
         }
      }

      return var10;
   }

   public static Item getItemByText(ICommandSender var0, String var1) throws NumberInvalidException {
      ResourceLocation var5 = new ResourceLocation(var1);
      Item var6 = Item.itemRegistry.getObject(var5);
      if (var6 == null) {
         throw new NumberInvalidException("commands.give.item.notFound", var5);
      } else {
         return var6;
      }
   }

   public static Block getBlockByText(ICommandSender var0, String var1) throws NumberInvalidException {
      ResourceLocation var5 = new ResourceLocation(var1);
      if (!Block.blockRegistry.containsKey(var5)) {
         throw new NumberInvalidException("commands.give.block.notFound", var5);
      } else {
         Block var6 = Block.blockRegistry.getObject(var5);
         if (var6 == null) {
            throw new NumberInvalidException("commands.give.block.notFound", var5);
         } else {
            return var6;
         }
      }
   }

   public static String joinNiceString(Object[] var0) {
      StringBuilder var4 = new StringBuilder();

      for (int var5 = 0; var5 < var0.length; var5++) {
         String var6 = var0[var5].toString();
         if (var5 > 0) {
            if (var5 == var0.length - 1) {
               var4.append(" and ");
            } else {
               var4.append(", ");
            }
         }

         var4.append(var6);
      }

      return var4.toString();
   }

   public static IChatComponent join(List<IChatComponent> var0) {
      ChatComponentText var4 = new ChatComponentText("");

      for (int var5 = 0; var5 < var0.size(); var5++) {
         if (var5 > 0) {
            if (var5 == var0.size() - 1) {
               var4.appendText(" and ");
            } else if (var5 > 0) {
               var4.appendText(", ");
            }
         }

         var4.appendSibling((IChatComponent)var0.get(var5));
      }

      return var4;
   }

   public static String joinNiceStringFromCollection(Collection<String> var0) {
      return joinNiceString(var0.toArray(new String[var0.size()]));
   }

   public static List<String> func_175771_a(String[] var0, int var1, BlockPos var2) {
      if (var2 == null) {
         return null;
      }

      int var6 = var0.length - 1;
      String var7;
      if (var6 == var1) {
         var7 = Integer.toString(var2.getX());
      } else if (var6 == var1 + 1) {
         var7 = Integer.toString(var2.getY());
      } else {
         if (var6 != var1 + 2) {
            return null;
         }

         var7 = Integer.toString(var2.getZ());
      }

      return Lists.newArrayList(var7);
   }

   public static List<String> func_181043_b(String[] var0, int var1, BlockPos var2) {
      if (var2 == null) {
         return null;
      }

      int var6 = var0.length - 1;
      String var7;
      if (var6 == var1) {
         var7 = Integer.toString(var2.getX());
      } else {
         if (var6 != var1 + 1) {
            return null;
         }

         var7 = Integer.toString(var2.getZ());
      }

      return Lists.newArrayList(var7);
   }

   public static boolean doesStringStartWith(String var0, String var1) {
      return var1.regionMatches(true, 0, var0, 0, var0.length());
   }

   public static List<String> getListOfStringsMatchingLastWord(String[] var0, String... var1) {
      return getListOfStringsMatchingLastWord(var0, Arrays.asList(var1));
   }

   public static List<String> getListOfStringsMatchingLastWord(String[] var0, Collection<?> var1) {
      String var5 = var0[var0.length - 1];
      ArrayList var6 = Lists.newArrayList();
      if (!var1.isEmpty()) {
         for (String var8 : Iterables.transform(var1, Functions.toStringFunction())) {
            if (doesStringStartWith(var5, var8)) {
               var6.add(var8);
            }
         }

         if (var6.isEmpty()) {
            for (Object var10 : var1) {
               if (var10 instanceof ResourceLocation && doesStringStartWith(var5, ((ResourceLocation)var10).getResourcePath())) {
                  var6.add(String.valueOf(var10));
               }
            }
         }
      }

      return var6;
   }

   @Override
   public boolean isUsernameIndex(String[] var1, int var2) {
      return false;
   }

   public static void notifyOperators(ICommandSender var0, ICommand var1, String var2, Object... var3) {
      notifyOperators(var0, var1, 0, var2, var3);
   }

   public static void notifyOperators(ICommandSender var0, ICommand var1, int var2, String var3, Object... var4) {
      if (theAdmin != null) {
         theAdmin.notifyOperators(var0, var1, var2, var3, var4);
      }
   }

   public static void setAdminCommander(IAdminCommand var0) {
      theAdmin = var0;
   }

   public int compareTo(ICommand var1) {
      return this.getCommandName().compareTo(var1.getCommandName());
   }

   public static class CoordinateArg {
      private final double field_179633_a;
      private final double field_179631_b;
      private final boolean field_179632_c;

      public CoordinateArg(double var1, double var3, boolean var5) {
         this.field_179633_a = var1;
         this.field_179631_b = var3;
         this.field_179632_c = var5;
      }

      public double J() {
         return this.field_179633_a;
      }

      public double v() {
         return this.field_179631_b;
      }

      public boolean func_179630_c() {
         return this.field_179632_c;
      }
   }
}
