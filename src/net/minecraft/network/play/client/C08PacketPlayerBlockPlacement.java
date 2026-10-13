package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.BlockPos;

public class C08PacketPlayerBlockPlacement implements Packet<INetHandlerPlayServer> {
   public static final BlockPos field_179726_a = new BlockPos(-1, -1, -1);
   private BlockPos position;
   private int placedBlockDirection;
   private ItemStack stack;
   private float facingX;
   private float facingY;
   private float facingZ;
   private static final long a = 102971556330639L;
   private static final long c = -5988537694689427201L;

   public C08PacketPlayerBlockPlacement() {
   }

   public C08PacketPlayerBlockPlacement(ItemStack var1) {
      this(field_179726_a, (int)c, var1, 0.0F, 0.0F, 0.0F);
   }

   public C08PacketPlayerBlockPlacement(BlockPos var1, int var2, ItemStack var3, float var4, float var5, float var6) {
      this.position = var1;
      this.placedBlockDirection = var2;
      this.stack = var3 != null ? var3.copy() : null;
      this.facingX = var4;
      this.facingY = var5;
      this.facingZ = var6;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.position = var1.readBlockPos();
      this.placedBlockDirection = var1.readUnsignedByte();
      this.stack = var1.readItemStackFromBuffer();
      this.facingX = var1.readUnsignedByte() / 16.0F;
      this.facingY = var1.readUnsignedByte() / 16.0F;
      this.facingZ = var1.readUnsignedByte() / 16.0F;
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeBlockPos(this.position);
      var1.writeByte(this.placedBlockDirection);
      var1.writeItemStackToBuffer(this.stack);
      var1.writeByte((int)(this.facingX * 16.0F));
      var1.writeByte((int)(this.facingY * 16.0F));
      var1.writeByte((int)(this.facingZ * 16.0F));
   }

   public void processPacket(INetHandlerPlayServer var1) {
      var1.processPlayerBlockPlacement(this);
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public void d(BlockPos var1) {
      this.position = var1;
   }

   public int getPlacedBlockDirection() {
      return this.placedBlockDirection;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public void s(ItemStack var1) {
      this.stack = var1;
   }

   public void T(float var1) {
      this.facingX = var1;
   }

   public void a(float var1) {
      this.facingY = var1;
   }

   public void y(float var1) {
      this.facingZ = var1;
   }

   public void h(int var1) {
      this.placedBlockDirection = var1;
   }

   public float getPlacedBlockOffsetX() {
      return this.facingX;
   }

   public float getPlacedBlockOffsetY() {
      return this.facingY;
   }

   public float getPlacedBlockOffsetZ() {
      return this.facingZ;
   }
}
