package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSpreadPlayers extends CommandBase {
   @Override
   public String getCommandName() {
      return "spreadplayers";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.spreadplayers.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 6) {
         throw new WrongUsageException("commands.spreadplayers.usage");
      }

      int i = 0;
      BlockPos blockPos = sender.getPosition();
      double d = parseDouble(blockPos.getX(), args[i++], true);
      double e = parseDouble(blockPos.getZ(), args[i++], true);
      double d2 = parseDouble(args[i++], 0.0);
      double d3 = parseDouble(args[i++], d2 + 1.0);
      boolean bl = parseBoolean(args[i++]);
      ArrayList arrayList = Lists.newArrayList();

      while (i < args.length) {
         String string = args[i++];
         if (PlayerSelector.hasArguments(string)) {
            List list = PlayerSelector.matchEntities(sender, string, Entity.class);
            if (list.size() == 0) {
               throw new EntityNotFoundException();
            }

            arrayList.addAll(list);
         } else {
            EntityPlayerMP entityPlayerMP = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(string);
            if (entityPlayerMP == null) {
               throw new PlayerNotFoundException();
            }

            arrayList.add(entityPlayerMP);
         }
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, arrayList.size());
      if (arrayList.isEmpty()) {
         throw new EntityNotFoundException();
      }

      sender.addChatMessage(
         new ChatComponentTranslation("commands.spreadplayers.spreading." + (bl ? "teams" : "players"), arrayList.size(), d3, d, e, d2)
      );
      this.func_110669_a(sender, arrayList, new CommandSpreadPlayers.Position(d, e), d2, d3, ((Entity)arrayList.get(0)).worldObj, bl);
   }

   private void func_110669_a(
      ICommandSender iCommandSender, List<Entity> list, CommandSpreadPlayers.Position position, double d, double e, World worldIn, boolean bl
   ) throws CommandException {
      Random random = new Random();
      double d2 = position.field_111101_a - e;
      double d3 = position.field_111100_b - e;
      double d4 = position.field_111101_a + e;
      double d5 = position.field_111100_b + e;
      CommandSpreadPlayers.Position[] positions = this.func_110670_a(random, bl ? this.func_110667_a(list) : list.size(), d2, d3, d4, d5);
      int i = this.func_110668_a(position, d, worldIn, random, d2, d3, d4, d5, positions, bl);
      double d6 = this.func_110671_a(list, worldIn, positions, bl);
      notifyOperators(
         iCommandSender, this, "commands.spreadplayers.success." + (bl ? "teams" : "players"), positions.length, position.field_111101_a, position.field_111100_b
      );
      if (positions.length > 1) {
         iCommandSender.addChatMessage(
            new ChatComponentTranslation("commands.spreadplayers.info." + (bl ? "teams" : "players"), String.format("%.2f", d6), i)
         );
      }
   }

   private int func_110667_a(List<Entity> list) {
      HashSet hashSet = Sets.newHashSet();

      for (Entity entity : list) {
         if (entity instanceof EntityPlayer) {
            hashSet.add(((EntityPlayer)entity).getTeam());
         } else {
            hashSet.add((Team)null);
         }
      }

      return hashSet.size();
   }

   private int func_110668_a(
      CommandSpreadPlayers.Position position,
      double d,
      World worldIn,
      Random random2,
      double e,
      double d2,
      double d3,
      double d4,
      CommandSpreadPlayers.Position[] positions,
      boolean bl
   ) throws CommandException {
      boolean bl2 = true;
      double d5 = Float.MAX_VALUE;

      int i;
      for (i = 0; i < 10000 && bl2; i++) {
         bl2 = false;
         d5 = Float.MAX_VALUE;

         for (int j = 0; j < positions.length; j++) {
            CommandSpreadPlayers.Position position2 = positions[j];
            int k = 0;
            CommandSpreadPlayers.Position position3 = new CommandSpreadPlayers.Position();

            for (int l = 0; l < positions.length; l++) {
               if (j != l) {
                  CommandSpreadPlayers.Position position4 = positions[l];
                  double d6 = position2.func_111099_a(position4);
                  d5 = Math.min(d6, d5);
                  if (d6 < d) {
                     k++;
                     position3.field_111101_a = position3.field_111101_a + (position4.field_111101_a - position2.field_111101_a);
                     position3.field_111100_b = position3.field_111100_b + (position4.field_111100_b - position2.field_111100_b);
                  }
               }
            }

            if (k > 0) {
               position3.field_111101_a /= k;
               position3.field_111100_b /= k;
               double d7 = position3.func_111096_b();
               if (d7 > 0.0) {
                  position3.func_111095_a();
                  position2.func_111094_b(position3);
               } else {
                  position2.func_111097_a(random2, e, d2, d3, d4);
               }

               bl2 = true;
            }

            if (position2.func_111093_a(e, d2, d3, d4)) {
               bl2 = true;
            }
         }

         if (!bl2) {
            for (CommandSpreadPlayers.Position position5 : positions) {
               if (!position5.func_111098_b(worldIn)) {
                  position5.func_111097_a(random2, e, d2, d3, d4);
                  bl2 = true;
               }
            }
         }
      }

      if (i >= 10000) {
         throw new CommandException(
            "commands.spreadplayers.failure." + (bl ? "teams" : "players"),
            positions.length,
            position.field_111101_a,
            position.field_111100_b,
            String.format("%.2f", d5)
         );
      } else {
         return i;
      }
   }

   private double func_110671_a(List<Entity> list, World worldIn, CommandSpreadPlayers.Position[] positions, boolean bl) {
      double d = 0.0;
      int i = 0;
      HashMap hashMap = Maps.newHashMap();

      for (int j = 0; j < list.size(); j++) {
         Entity entity = (Entity)list.get(j);
         CommandSpreadPlayers.Position position;
         if (bl) {
            Team team = entity instanceof EntityPlayer ? ((EntityPlayer)entity).getTeam() : null;
            if (!hashMap.containsKey(team)) {
               hashMap.put(team, positions[i++]);
            }

            position = (CommandSpreadPlayers.Position)hashMap.get(team);
         } else {
            position = positions[i++];
         }

         entity.setPositionAndUpdate(
            MathHelper.floor_double(position.field_111101_a) + 0.5F, position.func_111092_a(worldIn), MathHelper.floor_double(position.field_111100_b) + 0.5
         );
         double e = Double.MAX_VALUE;

         for (int k = 0; k < positions.length; k++) {
            if (position != positions[k]) {
               double d2 = position.func_111099_a(positions[k]);
               e = Math.min(d2, e);
            }
         }

         d += e;
      }

      return d / list.size();
   }

   private CommandSpreadPlayers.Position[] func_110670_a(Random random2, int i, double d, double e, double d2, double d3) {
      CommandSpreadPlayers.Position[] positions = new CommandSpreadPlayers.Position[i];

      for (int j = 0; j < positions.length; j++) {
         CommandSpreadPlayers.Position position = new CommandSpreadPlayers.Position();
         position.func_111097_a(random2, d, e, d2, d3);
         positions[j] = position;
      }

      return positions;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length >= 1 && args.length <= 2 ? func_181043_b(args, 0, pos) : null;
   }

   public static class Position {
      public double field_111101_a;
      public double field_111100_b;

      public Position() {
      }

      public Position(double d, double e) {
         this.field_111101_a = d;
         this.field_111100_b = e;
      }

      public double func_111099_a(CommandSpreadPlayers.Position position) {
         double d = this.field_111101_a - position.field_111101_a;
         double e = this.field_111100_b - position.field_111100_b;
         return Math.sqrt(d * d + e * e);
      }

      public void func_111095_a() {
         double d = this.func_111096_b();
         this.field_111101_a /= d;
         this.field_111100_b /= d;
      }

      public float func_111096_b() {
         return MathHelper.sqrt_double(this.field_111101_a * this.field_111101_a + this.field_111100_b * this.field_111100_b);
      }

      public void func_111094_b(CommandSpreadPlayers.Position position) {
         this.field_111101_a = this.field_111101_a - position.field_111101_a;
         this.field_111100_b = this.field_111100_b - position.field_111100_b;
      }

      public boolean func_111093_a(double d, double e, double d2, double d3) {
         boolean bl = false;
         if (this.field_111101_a < d) {
            this.field_111101_a = d;
            bl = true;
         } else if (this.field_111101_a > d2) {
            this.field_111101_a = d2;
            bl = true;
         }

         if (this.field_111100_b < e) {
            this.field_111100_b = e;
            bl = true;
         } else if (this.field_111100_b > d3) {
            this.field_111100_b = d3;
            bl = true;
         }

         return bl;
      }

      public int func_111092_a(World worldIn) {
         BlockPos blockPos = new BlockPos(this.field_111101_a, 256.0, this.field_111100_b);

         while (blockPos.getY() > 0) {
            blockPos = blockPos.down();
            if (worldIn.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
               return blockPos.getY() + 1;
            }
         }

         return 257;
      }

      public boolean func_111098_b(World worldIn) {
         BlockPos blockPos = new BlockPos(this.field_111101_a, 256.0, this.field_111100_b);

         while (blockPos.getY() > 0) {
            blockPos = blockPos.down();
            Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
            if (material != Material.air) {
               return !material.isLiquid() && material != Material.fire;
            }
         }

         return false;
      }

      public void func_111097_a(Random random2, double d, double e, double d2, double d3) {
         this.field_111101_a = MathHelper.getRandomDoubleInRange(random2, d, d2);
         this.field_111100_b = MathHelper.getRandomDoubleInRange(random2, e, d3);
      }
   }
}
