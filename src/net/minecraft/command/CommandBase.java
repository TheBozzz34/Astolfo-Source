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

   public int getRequiredPermissionLevel() {
      return 4;
   }

   @Override
   public List<String> getCommandAliases() {
      return Collections.emptyList();
   }

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender sender) {
      return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return null;
   }

   public static int parseInt(String input) throws NumberInvalidException {
      try {
         return Integer.parseInt(input);
      } catch (NumberFormatException numberFormatException) {
         throw new NumberInvalidException("commands.generic.num.invalid", input);
      }
   }

   public static int parseInt(String input, int minIn) throws NumberInvalidException {
      return parseInt(input, minIn, Integer.MAX_VALUE);
   }

   public static int parseInt(String input, int minIn, int maxIn) throws NumberInvalidException {
      int i = parseInt(input);
      if (i < minIn) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", i, minIn);
      } else if (i > maxIn) {
         throw new NumberInvalidException("commands.generic.num.tooBig", i, maxIn);
      } else {
         return i;
      }
   }

   public static long parseLong(String input) throws NumberInvalidException {
      try {
         return Long.parseLong(input);
      } catch (NumberFormatException numberFormatException) {
         throw new NumberInvalidException("commands.generic.num.invalid", input);
      }
   }

   public static long parseLong(String input, long minIn, long maxIn) throws NumberInvalidException {
      long l = parseLong(input);
      if (l < minIn) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", l, minIn);
      } else if (l > maxIn) {
         throw new NumberInvalidException("commands.generic.num.tooBig", l, maxIn);
      } else {
         return l;
      }
   }

   public static BlockPos parseBlockPos(ICommandSender sender, String[] args, int startIndex, boolean centerBlock) throws NumberInvalidException {
      BlockPos blockPos = sender.getPosition();
      return new BlockPos(
         parseDouble(blockPos.getX(), args[startIndex], -30000000, 30000000, centerBlock),
         parseDouble(blockPos.getY(), args[startIndex + 1], 0, 256, false),
         parseDouble(blockPos.getZ(), args[startIndex + 2], -30000000, 30000000, centerBlock)
      );
   }

   public static double parseDouble(String input) throws NumberInvalidException {
      try {
         double d = Double.parseDouble(input);
         if (!Doubles.isFinite(d)) {
            throw new NumberInvalidException("commands.generic.num.invalid", input);
         } else {
            return d;
         }
      } catch (NumberFormatException numberFormatException) {
         throw new NumberInvalidException("commands.generic.num.invalid", input);
      }
   }

   public static double parseDouble(String input, double minIn) throws NumberInvalidException {
      return parseDouble(input, minIn, Double.MAX_VALUE);
   }

   public static double parseDouble(String input, double minIn, double maxIn) throws NumberInvalidException {
      double d = parseDouble(input);
      if (d < minIn) {
         throw new NumberInvalidException("commands.generic.double.tooSmall", d, minIn);
      } else if (d > maxIn) {
         throw new NumberInvalidException("commands.generic.double.tooBig", d, maxIn);
      } else {
         return d;
      }
   }

   public static boolean parseBoolean(String input) throws CommandException {
      if (input.equals("true") || input.equals("1")) {
         return true;
      } else if (!input.equals("false") && !input.equals("0")) {
         throw new CommandException("commands.generic.boolean.invalid", input);
      } else {
         return false;
      }
   }

   public static EntityPlayerMP getCommandSenderAsPlayer(ICommandSender sender) throws PlayerNotFoundException {
      if (sender instanceof EntityPlayerMP) {
         return (EntityPlayerMP)sender;
      } else {
         throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.");
      }
   }

   public static EntityPlayerMP getPlayer(ICommandSender sender, String username) throws PlayerNotFoundException {
      EntityPlayerMP entityPlayerMP = PlayerSelector.matchOnePlayer(sender, username);
      if (entityPlayerMP == null) {
         try {
            entityPlayerMP = MinecraftServer.getServer().getConfigurationManager().getPlayerByUUID(UUID.fromString(username));
         } catch (IllegalArgumentException illegalArgumentException) {
         }
      }

      if (entityPlayerMP == null) {
         entityPlayerMP = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username);
      }

      if (entityPlayerMP == null) {
         throw new PlayerNotFoundException();
      } else {
         return entityPlayerMP;
      }
   }

   public static Entity getEntity(ICommandSender iCommandSender, String string) throws EntityNotFoundException {
      return getEntity(iCommandSender, string, Entity.class);
   }

   public static <T extends Entity> T getEntity(ICommandSender commandSender, String string, Class<? extends T> class2) throws EntityNotFoundException {
      Entity entity2 = PlayerSelector.matchOneEntity(commandSender, string, class2);
      MinecraftServer minecraftServer = MinecraftServer.getServer();
      if (entity2 == null) {
         entity2 = minecraftServer.getConfigurationManager().getPlayerByUsername(string);
      }

      if (entity2 == null) {
         try {
            UUID uuid = UUID.fromString(string);
            entity2 = minecraftServer.getEntityFromUuid(uuid);
            if (entity2 == null) {
               entity2 = minecraftServer.getConfigurationManager().getPlayerByUUID(uuid);
            }
         } catch (IllegalArgumentException illegalArgumentException) {
            throw new EntityNotFoundException("commands.generic.entity.invalidUuid");
         }
      }

      if (entity2 != null && class2.isAssignableFrom(entity2.getClass())) {
         return (T)entity2;
      } else {
         throw new EntityNotFoundException();
      }
   }

   public static List<Entity> func_175763_c(ICommandSender iCommandSender, String string) throws EntityNotFoundException {
      return PlayerSelector.hasArguments(string)
         ? PlayerSelector.matchEntities(iCommandSender, string, Entity.class)
         : Lists.newArrayList(getEntity(iCommandSender, string));
   }

   public static String getPlayerName(ICommandSender sender, String query) throws PlayerNotFoundException {
      try {
         return getPlayer(sender, query).getName();
      } catch (PlayerNotFoundException playerNotFoundException) {
         if (PlayerSelector.hasArguments(query)) {
            throw playerNotFoundException;
         } else {
            return query;
         }
      }
   }

   public static String getEntityName(ICommandSender iCommandSender, String string) throws EntityNotFoundException {
      try {
         return getPlayer(iCommandSender, string).getName();
      } catch (PlayerNotFoundException playerNotFoundException) {
         try {
            return getEntity(iCommandSender, string).getUniqueID().toString();
         } catch (EntityNotFoundException entityNotFoundException) {
            if (PlayerSelector.hasArguments(string)) {
               throw entityNotFoundException;
            } else {
               return string;
            }
         }
      }
   }

   public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int i) throws CommandException, PlayerNotFoundException {
      return getChatComponentFromNthArg(sender, args, i, false);
   }

   public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int index, boolean bl) throws PlayerNotFoundException {
      ChatComponentText chatComponentText = new ChatComponentText("");

      for (int i = index; i < args.length; i++) {
         if (i > index) {
            chatComponentText.appendText(" ");
         }

         IChatComponent iChatComponent = new ChatComponentText(args[i]);
         if (bl) {
            IChatComponent iChatComponent2 = PlayerSelector.matchEntitiesToChatComponent(sender, args[i]);
            if (iChatComponent2 == null) {
               if (PlayerSelector.hasArguments(args[i])) {
                  throw new PlayerNotFoundException();
               }
            } else {
               iChatComponent = iChatComponent2;
            }
         }

         chatComponentText.appendSibling(iChatComponent);
      }

      return chatComponentText;
   }

   public static String buildString(String[] args, int startPos) {
      StringBuilder stringBuilder = new StringBuilder();

      for (int i = startPos; i < args.length; i++) {
         if (i > startPos) {
            stringBuilder.append(" ");
         }

         String string = args[i];
         stringBuilder.append(string);
      }

      return stringBuilder.toString();
   }

   public static CommandBase.CoordinateArg parseCoordinate(double base, String string, boolean centerBlock) throws NumberInvalidException {
      return parseCoordinate(base, string, -30000000, 30000000, centerBlock);
   }

   public static CommandBase.CoordinateArg parseCoordinate(double d, String string, int minIn, int maxIn, boolean centerBlock) throws NumberInvalidException {
      boolean bl = string.startsWith("~");
      if (bl && Double.isNaN(d)) {
         throw new NumberInvalidException("commands.generic.num.invalid", d);
      }

      double e = 0.0;
      if (!bl || string.length() > 1) {
         boolean bl2 = string.contains(".");
         if (bl) {
            string = string.substring(1);
         }

         e += parseDouble(string);
         if (!bl2 && !bl && centerBlock) {
            e += 0.5;
         }
      }

      if (minIn != 0 || maxIn != 0) {
         if (e < minIn) {
            throw new NumberInvalidException("commands.generic.double.tooSmall", e, minIn);
         }

         if (e > maxIn) {
            throw new NumberInvalidException("commands.generic.double.tooBig", e, maxIn);
         }
      }

      return new CommandBase.CoordinateArg(e + (bl ? d : 0.0), e, bl);
   }

   public static double parseDouble(double base, String input, boolean centerBlock) throws NumberInvalidException {
      return parseDouble(base, input, -30000000, 30000000, centerBlock);
   }

   public static double parseDouble(double base, String input, int minIn, int maxIn, boolean centerBlock) throws NumberInvalidException {
      boolean bl = input.startsWith("~");
      if (bl && Double.isNaN(base)) {
         throw new NumberInvalidException("commands.generic.num.invalid", base);
      }

      double d = bl ? base : 0.0;
      if (!bl || input.length() > 1) {
         boolean bl2 = input.contains(".");
         if (bl) {
            input = input.substring(1);
         }

         d += parseDouble(input);
         if (!bl2 && !bl && centerBlock) {
            d += 0.5;
         }
      }

      if (minIn != 0 || maxIn != 0) {
         if (d < minIn) {
            throw new NumberInvalidException("commands.generic.double.tooSmall", d, minIn);
         }

         if (d > maxIn) {
            throw new NumberInvalidException("commands.generic.double.tooBig", d, maxIn);
         }
      }

      return d;
   }

   public static Item getItemByText(ICommandSender sender, String id) throws NumberInvalidException {
      ResourceLocation resourceLocation = new ResourceLocation(id);
      Item item2 = Item.itemRegistry.getObject(resourceLocation);
      if (item2 == null) {
         throw new NumberInvalidException("commands.give.item.notFound", resourceLocation);
      } else {
         return item2;
      }
   }

   public static Block getBlockByText(ICommandSender sender, String id) throws NumberInvalidException {
      ResourceLocation resourceLocation = new ResourceLocation(id);
      if (!Block.blockRegistry.containsKey(resourceLocation)) {
         throw new NumberInvalidException("commands.give.block.notFound", resourceLocation);
      } else {
         Block block2 = Block.blockRegistry.getObject(resourceLocation);
         if (block2 == null) {
            throw new NumberInvalidException("commands.give.block.notFound", resourceLocation);
         } else {
            return block2;
         }
      }
   }

   public static String joinNiceString(Object[] elements) {
      StringBuilder stringBuilder = new StringBuilder();

      for (int i = 0; i < elements.length; i++) {
         String string = elements[i].toString();
         if (i > 0) {
            if (i == elements.length - 1) {
               stringBuilder.append(" and ");
            } else {
               stringBuilder.append(", ");
            }
         }

         stringBuilder.append(string);
      }

      return stringBuilder.toString();
   }

   public static IChatComponent join(List<IChatComponent> components) {
      ChatComponentText chatComponentText = new ChatComponentText("");

      for (int i = 0; i < components.size(); i++) {
         if (i > 0) {
            if (i == components.size() - 1) {
               chatComponentText.appendText(" and ");
            } else if (i > 0) {
               chatComponentText.appendText(", ");
            }
         }

         chatComponentText.appendSibling((IChatComponent)components.get(i));
      }

      return chatComponentText;
   }

   public static String joinNiceStringFromCollection(Collection<String> strings) {
      return joinNiceString(strings.toArray(new String[strings.size()]));
   }

   public static List<String> func_175771_a(String[] strings, int i, BlockPos blockPos) {
      if (blockPos == null) {
         return null;
      }

      int j = strings.length - 1;
      String string;
      if (j == i) {
         string = Integer.toString(blockPos.getX());
      } else if (j == i + 1) {
         string = Integer.toString(blockPos.getY());
      } else {
         if (j != i + 2) {
            return null;
         }

         string = Integer.toString(blockPos.getZ());
      }

      return Lists.newArrayList(string);
   }

   public static List<String> func_181043_b(String[] strings, int i, BlockPos blockPos) {
      if (blockPos == null) {
         return null;
      }

      int j = strings.length - 1;
      String string;
      if (j == i) {
         string = Integer.toString(blockPos.getX());
      } else {
         if (j != i + 1) {
            return null;
         }

         string = Integer.toString(blockPos.getZ());
      }

      return Lists.newArrayList(string);
   }

   public static boolean doesStringStartWith(String original, String region) {
      return region.regionMatches(true, 0, original, 0, original.length());
   }

   public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibilities) {
      return getListOfStringsMatchingLastWord(args, Arrays.asList(possibilities));
   }

   public static List<String> getListOfStringsMatchingLastWord(String[] strings, Collection<?> collection) {
      String string = strings[strings.length - 1];
      ArrayList arrayList = Lists.newArrayList();
      if (!collection.isEmpty()) {
         for (String string2 : Iterables.transform(collection, Functions.toStringFunction())) {
            if (doesStringStartWith(string, string2)) {
               arrayList.add(string2);
            }
         }

         if (arrayList.isEmpty()) {
            for (Object object : collection) {
               if (object instanceof ResourceLocation && doesStringStartWith(string, ((ResourceLocation)object).getResourcePath())) {
                  arrayList.add(String.valueOf(object));
               }
            }
         }
      }

      return arrayList;
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }

   public static void notifyOperators(ICommandSender sender, ICommand command, String msgFormat, Object... msgParams) {
      notifyOperators(sender, command, 0, msgFormat, msgParams);
   }

   public static void notifyOperators(ICommandSender sender, ICommand command, int i, String msgFormat, Object... msgParams) {
      if (theAdmin != null) {
         theAdmin.notifyOperators(sender, command, i, msgFormat, msgParams);
      }
   }

   public static void setAdminCommander(IAdminCommand command) {
      theAdmin = command;
   }

   public int compareTo(ICommand iCommand) {
      return this.getCommandName().compareTo(iCommand.getCommandName());
   }

   public static class CoordinateArg {
      private final double field_179633_a;
      private final double field_179631_b;
      private final boolean field_179632_c;

      public CoordinateArg(double d, double e, boolean bl) {
         this.field_179633_a = d;
         this.field_179631_b = e;
         this.field_179632_c = bl;
      }

      public double getResult() {
         return this.field_179633_a;
      }

      public double getAmount() {
         return this.field_179631_b;
      }

      public boolean func_179630_c() {
         return this.field_179632_c;
      }
   }
}
