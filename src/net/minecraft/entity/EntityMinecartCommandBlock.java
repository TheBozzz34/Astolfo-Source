package net.minecraft.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityMinecartCommandBlock extends EntityMinecart {
   private final CommandBlockLogic commandBlockLogic = new CommandBlockLogic() {
      private static final long b = 66319594639737L;

      @Override
      public void updateCommand() {
         EntityMinecartCommandBlock.this.getDataWatcher().updateObject(23, this.getCommand());
         EntityMinecartCommandBlock.this.getDataWatcher().updateObject(24, IChatComponent.Serializer.componentToJson(this.getLastOutput()));
      }

      @Override
      public int func_145751_f() {
         return 1;
      }

      @Override
      public void func_145757_a(ByteBuf var1) {
         var1.writeInt(EntityMinecartCommandBlock.this.getEntityId());
      }

      @Override
      public BlockPos getPosition() {
         return new BlockPos(EntityMinecartCommandBlock.this.posX, EntityMinecartCommandBlock.this.posY + 0.5, EntityMinecartCommandBlock.this.posZ);
      }

      @Override
      public Vec3 getPositionVector() {
         return new Vec3(EntityMinecartCommandBlock.this.posX, EntityMinecartCommandBlock.this.posY, EntityMinecartCommandBlock.this.posZ);
      }

      @Override
      public World getEntityWorld() {
         return EntityMinecartCommandBlock.this.worldObj;
      }

      @Override
      public Entity getCommandSenderEntity() {
         return EntityMinecartCommandBlock.this;
      }
   };
   private int activatorRailCooldown = 0;
   private static final long ab = 51525937183698L;

   public EntityMinecartCommandBlock(World var1) {
      super(var1);
   }

   public EntityMinecartCommandBlock(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.getDataWatcher().addObject(23, "");
      this.getDataWatcher().addObject(24, "");
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound var1) {
      super.readEntityFromNBT(var1);
      this.commandBlockLogic.readDataFromNBT(var1);
      this.getDataWatcher().updateObject(23, this.getCommandBlockLogic().getCommand());
      this.getDataWatcher().updateObject(24, IChatComponent.Serializer.componentToJson(this.getCommandBlockLogic().getLastOutput()));
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound var1) {
      super.writeEntityToNBT(var1);
      this.commandBlockLogic.writeDataToNBT(var1);
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.COMMAND_BLOCK;
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.command_block.getDefaultState();
   }

   public CommandBlockLogic getCommandBlockLogic() {
      return this.commandBlockLogic;
   }

   @Override
   public void onActivatorRailPass(int var1, int var2, int var3, boolean var4) {
      if (var4 && this.ticksExisted - this.activatorRailCooldown >= 4) {
         this.getCommandBlockLogic().trigger(this.worldObj);
         this.activatorRailCooldown = this.ticksExisted;
      }
   }

   @Override
   public boolean interactFirst(EntityPlayer var1) {
      this.commandBlockLogic.tryOpenEditCommandBlock(var1);
      return false;
   }

   @Override
   public void onDataWatcherUpdate(int var1) {
      super.onDataWatcherUpdate(var1);
      if (var1 == 24) {
         try {
            this.commandBlockLogic.setLastOutput(IChatComponent.Serializer.jsonToComponent(this.getDataWatcher().getWatchableObjectString(24)));
         } catch (Throwable var6) {
         }
      } else if (var1 == 23) {
         this.commandBlockLogic.setCommand(this.getDataWatcher().getWatchableObjectString(23));
      }
   }
}
