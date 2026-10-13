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
   private static final long b = 79985412622763L;

   @Override
   public String getCommandName() {
      return "spreadplayers";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender var1) {
      return "commands.spreadplayers.usage";
   }

   @Override
   public void processCommand(ICommandSender var1, String[] var2) throws CommandException {
      if (var2.length < 6) {
         throw new WrongUsageException("commands.spreadplayers.usage");
      }

      int var6 = 0;
      BlockPos var7 = var1.getPosition();
      double var8 = parseDouble(var7.getX(), var2[var6++], true);
      double var10 = parseDouble(var7.getZ(), var2[var6++], true);
      double var12 = parseDouble(var2[var6++], 0.0);
      double var14 = parseDouble(var2[var6++], var12 + 1.0);
      boolean var16 = parseBoolean(var2[var6++]);
      ArrayList var17 = Lists.newArrayList();

      while (var6 < var2.length) {
         String var18 = var2[var6++];
         if (PlayerSelector.hasArguments(var18)) {
            List var19 = PlayerSelector.matchEntities(var1, var18, Entity.class);
            if (var19.size() == 0) {
               throw new EntityNotFoundException();
            }

            var17.addAll(var19);
         } else {
            EntityPlayerMP var25 = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(var18);
            if (var25 == null) {
               throw new PlayerNotFoundException();
            }

            var17.add(var25);
         }
      }

      var1.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, var17.size());
      if (var17.isEmpty()) {
         throw new EntityNotFoundException();
      }

      var1.addChatMessage(
         new ChatComponentTranslation("commands.spreadplayers.spreading." + (var16 ? "teams" : "players"), var17.size(), var14, var8, var10, var12)
      );
      this.func_110669_a(var1, var17, new CommandSpreadPlayers.Position(var8, var10), var12, var14, ((Entity)var17.get(0)).worldObj, var16);
   }

   private void func_110669_a(ICommandSender var1, List<Entity> var2, CommandSpreadPlayers.Position var3, double var4, double var6, World var8, boolean var9) throws CommandException {
      Random var13 = new Random();
      double var14 = var3.field_111101_a - var6;
      double var16 = var3.field_111100_b - var6;
      double var18 = var3.field_111101_a + var6;
      double var20 = var3.field_111100_b + var6;
      CommandSpreadPlayers.Position[] var22 = this.func_110670_a(var13, var9 ? this.func_110667_a(var2) : var2.size(), var14, var16, var18, var20);
      int var23 = this.func_110668_a(var3, var4, var8, var13, var14, var16, var18, var20, var22, var9);
      double var24 = this.func_110671_a(var2, var8, var22, var9);
      notifyOperators(var1, this, "commands.spreadplayers.success." + (var9 ? "teams" : "players"), var22.length, var3.field_111101_a, var3.field_111100_b);
      if (var22.length > 1) {
         var1.addChatMessage(new ChatComponentTranslation("commands.spreadplayers.info." + (var9 ? "teams" : "players"), String.format("%.2f", var24), var23));
      }
   }

   private int func_110667_a(List<Entity> var1) {
      HashSet var5 = Sets.newHashSet();

      for (Entity var7 : var1) {
         if (var7 instanceof EntityPlayer) {
            var5.add(((EntityPlayer)var7).getTeam());
         } else {
            var5.add((Team)null);
         }
      }

      return var5.size();
   }

   private int func_110668_a(
      CommandSpreadPlayers.Position var1,
      double var2,
      World var4,
      Random var5,
      double var6,
      double var8,
      double var10,
      double var12,
      CommandSpreadPlayers.Position[] var14,
      boolean var15
   ) throws CommandException {
      boolean var19 = true;
      double var20 = Float.MAX_VALUE;

      int var22;
      for (var22 = 0; var22 < 10000 && var19; var22++) {
         var19 = false;
         var20 = Float.MAX_VALUE;

         for (int var23 = 0; var23 < var14.length; var23++) {
            CommandSpreadPlayers.Position var24 = var14[var23];
            int var25 = 0;
            CommandSpreadPlayers.Position var26 = new CommandSpreadPlayers.Position();

            for (int var27 = 0; var27 < var14.length; var27++) {
               if (var23 != var27) {
                  CommandSpreadPlayers.Position var28 = var14[var27];
                  double var29 = var24.func_111099_a(var28);
                  var20 = Math.min(var29, var20);
                  if (var29 < var2) {
                     var25++;
                     var26.field_111101_a = var26.field_111101_a + (var28.field_111101_a - var24.field_111101_a);
                     var26.field_111100_b = var26.field_111100_b + (var28.field_111100_b - var24.field_111100_b);
                  }
               }
            }

            if (var25 > 0) {
               var26.field_111101_a /= var25;
               var26.field_111100_b /= var25;
               double var35 = var26.func_111096_b();
               if (var35 > 0.0) {
                  var26.func_111095_a();
                  var24.func_111094_b(var26);
               } else {
                  var24.func_111097_a(var5, var6, var8, var10, var12);
               }

               var19 = true;
            }

            if (var24.func_111093_a(var6, var8, var10, var12)) {
               var19 = true;
            }
         }

         if (!var19) {
            for (CommandSpreadPlayers.Position var34 : var14) {
               if (!var34.func_111098_b(var4)) {
                  var34.func_111097_a(var5, var6, var8, var10, var12);
                  var19 = true;
               }
            }
         }
      }

      if (var22 >= 10000) {
         throw new CommandException(
            "commands.spreadplayers.failure." + (var15 ? "teams" : "players"),
            var14.length,
            var1.field_111101_a,
            var1.field_111100_b,
            String.format("%.2f", var20)
         );
      } else {
         return var22;
      }
   }

   private double func_110671_a(List<Entity> var1, World var2, CommandSpreadPlayers.Position[] var3, boolean var4) {
      double var8 = 0.0;
      int var10 = 0;
      HashMap var11 = Maps.newHashMap();

      for (int var12 = 0; var12 < var1.size(); var12++) {
         Entity var13 = (Entity)var1.get(var12);
         CommandSpreadPlayers.Position var14;
         if (var4) {
            Team var15 = var13 instanceof EntityPlayer ? ((EntityPlayer)var13).getTeam() : null;
            if (!var11.containsKey(var15)) {
               var11.put(var15, var3[var10++]);
            }

            var14 = (CommandSpreadPlayers.Position)var11.get(var15);
         } else {
            var14 = var3[var10++];
         }

         var13.setPositionAndUpdate(
            MathHelper.floor_double(var14.field_111101_a) + 0.5F, var14.func_111092_a(var2), MathHelper.floor_double(var14.field_111100_b) + 0.5
         );
         double var20 = Double.MAX_VALUE;

         for (int var17 = 0; var17 < var3.length; var17++) {
            if (var14 != var3[var17]) {
               double var18 = var14.func_111099_a(var3[var17]);
               var20 = Math.min(var18, var20);
            }
         }

         var8 += var20;
      }

      return var8 / var1.size();
   }

   private CommandSpreadPlayers.Position[] func_110670_a(Random var1, int var2, double var3, double var5, double var7, double var9) {
      CommandSpreadPlayers.Position[] var14 = new CommandSpreadPlayers.Position[var2];

      for (int var15 = 0; var15 < var14.length; var15++) {
         CommandSpreadPlayers.Position var16 = new CommandSpreadPlayers.Position();
         var16.func_111097_a(var1, var3, var5, var7, var9);
         var14[var15] = var16;
      }

      return var14;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3) {
      return var2.length >= 1 && var2.length <= 2 ? func_181043_b(var2, 0, var3) : null;
   }

   public static class Position {
      public double field_111101_a;
      public double field_111100_b;
      private static final long a = 30187184520590L;
      private static final long b = 5179335181466599681L;

      public Position() {
      }

      public Position(double var1, double var3) {
         this.field_111101_a = var1;
         this.field_111100_b = var3;
      }

      public double func_111099_a(CommandSpreadPlayers.Position var1) {
         double var2 = this.field_111101_a - var1.field_111101_a;
         double var4 = this.field_111100_b - var1.field_111100_b;
         return Math.sqrt(var2 * var2 + var4 * var4);
      }

      public void func_111095_a() {
         double var1 = this.func_111096_b();
         this.field_111101_a /= var1;
         this.field_111100_b /= var1;
      }

      public float func_111096_b() {
         return MathHelper.sqrt_double(this.field_111101_a * this.field_111101_a + this.field_111100_b * this.field_111100_b);
      }

      public void func_111094_b(CommandSpreadPlayers.Position var1) {
         this.field_111101_a = this.field_111101_a - var1.field_111101_a;
         this.field_111100_b = this.field_111100_b - var1.field_111100_b;
      }

      public boolean func_111093_a(double var1, double var3, double var5, double var7) {
         boolean var12 = false;
         if (this.field_111101_a < var1) {
            this.field_111101_a = var1;
            var12 = true;
         } else if (this.field_111101_a > var5) {
            this.field_111101_a = var5;
            var12 = true;
         }

         if (this.field_111100_b < var3) {
            this.field_111100_b = var3;
            var12 = true;
         } else if (this.field_111100_b > var7) {
            this.field_111100_b = var7;
            var12 = true;
         }

         return var12;
      }

      public int func_111092_a(World var1) {
         BlockPos var5 = new BlockPos(this.field_111101_a, 256.0, this.field_111100_b);

         while (var5.getY() > 0) {
            var5 = var5.down();
            if (var1.getBlockState(var5).getBlock().getMaterial() != Material.air) {
               return var5.getY() + 1;
            }
         }

         return (int)b;
      }

      public boolean func_111098_b(World var1) {
         BlockPos var5 = new BlockPos(this.field_111101_a, 256.0, this.field_111100_b);

         while (var5.getY() > 0) {
            var5 = var5.down();
            Material var6 = var1.getBlockState(var5).getBlock().getMaterial();
            if (var6 != Material.air) {
               return !var6.isLiquid() && var6 != Material.fire;
            }
         }

         return false;
      }

      public void func_111097_a(Random var1, double var2, double var4, double var6, double var8) {
         this.field_111101_a = MathHelper.getRandomDoubleInRange(var1, var2, var6);
         this.field_111100_b = MathHelper.getRandomDoubleInRange(var1, var4, var8);
      }
   }
}
