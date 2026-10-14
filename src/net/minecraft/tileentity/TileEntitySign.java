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

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);

      for (int i = 0; i < 4; i++) {
         String string = IChatComponent.Serializer.componentToJson(this.signText[i]);
         compound.setString("Text" + (i + 1), string);
      }

      this.stats.writeStatsToNBT(compound);
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      this.isEditable = false;
      super.readFromNBT(compound);
      TileEntitySign.aa7 aa72 = new TileEntitySign.aa7();

      for (int i = 0; i < 4; i++) {
         String string = compound.getString("Text" + (i + 1));

         try {
            IChatComponent iChatComponent = IChatComponent.Serializer.jsonToComponent(string);

            try {
               this.signText[i] = ChatComponentProcessor.processComponent(aa72, iChatComponent, (Entity)null);
            } catch (CommandException commandException) {
               this.signText[i] = iChatComponent;
            }
         } catch (JsonParseException jsonParseException) {
            this.signText[i] = new ChatComponentText(string);
         }
      }

      this.stats.readStatsFromNBT(compound);
   }

   @Override
   public Packet getDescriptionPacket() {
      IChatComponent[] iChatComponents = new IChatComponent[4];
      System.arraycopy(this.signText, 0, iChatComponents, 0, 4);
      return new S33PacketUpdateSign(this.worldObj, this.pos, iChatComponents);
   }

   @Override
   public boolean func_183000_F() {
      return true;
   }

   public boolean getIsEditable() {
      return this.isEditable;
   }

   public void setEditable(boolean isEditableIn) {
      this.isEditable = isEditableIn;
      if (!isEditableIn) {
         this.player = null;
      }
   }

   public void setPlayer(EntityPlayer playerIn) {
      this.player = playerIn;
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public boolean executeCommand(final EntityPlayer playerIn) {
      ICommandSender iCommandSender = new ICommandSender() {
         @Override
         public String getName() {
            return playerIn.getName();
         }

         @Override
         public IChatComponent getDisplayName() {
            return playerIn.getDisplayName();
         }

         @Override
         public void addChatMessage(IChatComponent component) {
         }

         @Override
         public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
            return permLevel <= 2;
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
            return playerIn.getEntityWorld();
         }

         @Override
         public Entity getCommandSenderEntity() {
            return playerIn;
         }

         @Override
         public boolean sendCommandFeedback() {
            return false;
         }

         @Override
         public void setCommandStat(CommandResultStats.Type typeIn, int amount) {
            TileEntitySign.this.stats.setCommandStatScore(this, typeIn, amount);
         }
      };

      for (int i = 0; i < this.signText.length; i++) {
         ChatStyle chatStyle = this.signText[i] == null ? null : this.signText[i].getChatStyle();
         if (chatStyle != null && chatStyle.getChatClickEvent() != null) {
            ClickEvent clickEvent = chatStyle.getChatClickEvent();
            if (clickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               MinecraftServer.getServer().getCommandManager().executeCommand(iCommandSender, clickEvent.getValue());
            }
         }
      }

      return true;
   }

   public CommandResultStats getStats() {
      return this.stats;
   }

   public class aa7 implements ICommandSender {
      @Override
      public String getName() {
         return "Sign";
      }

      @Override
      public IChatComponent getDisplayName() {
         return new ChatComponentText(this.getName());
      }

      @Override
      public void addChatMessage(IChatComponent component) {
      }

      @Override
      public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
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
      public void setCommandStat(CommandResultStats.Type typeIn, int amount) {
      }
   }
}
