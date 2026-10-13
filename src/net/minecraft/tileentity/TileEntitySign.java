package net.minecraft.tileentity;

import com.google.gson.JsonParseException;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class TileEntitySign extends TileEntity {
   public final IChatComponent[] signText = new IChatComponent[]{
      new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")
   };
   public int lineBeingEdited = -1;
   private boolean isEditable = true;
   private EntityPlayer player;
   private final CommandResultStats stats = new CommandResultStats();
   private static final long a = 8097414756637L;

   @Override
   public void writeToNBT(NBTTagCompound var1) {
      super.writeToNBT(var1);

      for (int var5 = 0; var5 < 4; var5++) {
         String var6 = IChatComponent.Serializer.componentToJson(this.signText[var5]);
         var1.setString("Text" + (var5 + 1), var6);
      }

      this.stats.writeStatsToNBT(var1);
   }

   @Override
   public void readFromNBT(NBTTagCompound var1) {
      this.isEditable = false;
      super.readFromNBT(var1);
      TileEntitySign.aa7 var5 = new TileEntitySign.aa7();

      for (int var6 = 0; var6 < 4; var6++) {
         String var7 = var1.getString("Text" + (var6 + 1));

         try {
            IChatComponent var8 = IChatComponent.Serializer.jsonToComponent(var7);

            try {
               this.signText[var6] = ChatComponentProcessor.processComponent(var5, var8, (Entity)null);
            } catch (CommandException var10) {
               this.signText[var6] = var8;
            }
         } catch (JsonParseException var11) {
            this.signText[var6] = new ChatComponentText(var7);
         }
      }

      this.stats.readStatsFromNBT(var1);
   }

   @Override
   public Packet getDescriptionPacket() {
      IChatComponent[] var1 = new IChatComponent[4];
      System.arraycopy(this.signText, 0, var1, 0, 4);
      return new S33PacketUpdateSign(this.worldObj, this.pos, var1);
   }

   @Override
   public boolean func_183000_F() {
      return true;
   }

   public boolean getIsEditable() {
      return this.isEditable;
   }

   public void setEditable(boolean var1) {
      this.isEditable = var1;
      if (!var1) {
         this.player = null;
      }
   }

   public void setPlayer(EntityPlayer var1) {
      this.player = var1;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public boolean executeCommand(final EntityPlayer var1) {
      ICommandSender var5 = new ICommandSender() {
         private static final long b = 38315389708230L;

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
         }

         @Override
         public boolean canCommandSenderUseCommand(int var1x, String var2) {
            return var1x <= 2;
         }

         @Override
         public BlockPos getPosition() {
            return TileEntitySign.this.pos;
         }

         @Override
         public Vec3 getPositionVector() {
            return new Vec3(TileEntitySign.this.pos.getX() + 0.5, TileEntitySign.this.pos.getY() + 0.5, TileEntitySign.this.pos.getZ() + 0.5);
         }

         @Override
         public World getEntityWorld() {
            return var1.getEntityWorld();
         }

         @Override
         public Entity getCommandSenderEntity() {
            return var1;
         }

         @Override
         public boolean sendCommandFeedback() {
            return false;
         }

         @Override
         public void setCommandStat(CommandResultStats.Type var1x, int var2) {
            TileEntitySign.this.stats.setCommandStatScore(this, var1x, var2);
         }
      };

      for (int var6 = 0; var6 < this.signText.length; var6++) {
         ChatStyle var7 = this.signText[var6] == null ? null : this.signText[var6].getChatStyle();
         if (var7 != null && var7.getChatClickEvent() != null) {
            ClickEvent var8 = var7.getChatClickEvent();
            if (var8.getAction() == ClickEvent.Action.RUN_COMMAND) {
               MinecraftServer.getServer().getCommandManager().executeCommand(var5, var8.getValue());
            }
         }
      }

      return true;
   }

   public CommandResultStats getStats() {
      return this.stats;
   }

   public class aa7 implements ICommandSender {
      private static final long a = 53345391412476L;

      @Override
      public String getName() {
         return "Sign";
      }

      @Override
      public IChatComponent getDisplayName() {
         return new ChatComponentText(this.getName());
      }

      @Override
      public void addChatMessage(IChatComponent var1) {
      }

      @Override
      public boolean canCommandSenderUseCommand(int var1, String var2) {
         return true;
      }

      @Override
      public BlockPos getPosition() {
         return TileEntitySign.this.pos;
      }

      @Override
      public Vec3 getPositionVector() {
         return new Vec3(TileEntitySign.this.pos.getX() + 0.5, TileEntitySign.this.pos.getY() + 0.5, TileEntitySign.this.pos.getZ() + 0.5);
      }

      @Override
      public World getEntityWorld() {
         return TileEntitySign.this.worldObj;
      }

      @Override
      public Entity getCommandSenderEntity() {
         return null;
      }

      @Override
      public boolean sendCommandFeedback() {
         return false;
      }

      @Override
      public void setCommandStat(CommandResultStats.Type var1, int var2) {
      }
   }
}
