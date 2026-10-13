package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.border.WorldBorder;

public class S44PacketWorldBorder implements Packet<INetHandlerPlayClient> {
   private S44PacketWorldBorder.Action action;
   private int size;
   private double centerX;
   private double centerZ;
   private double targetSize;
   private double diameter;
   private long timeUntilTarget;
   private int warningTime;
   private int warningDistance;
   private static final long a = 8912128949544L;

   public S44PacketWorldBorder() {
   }

   public S44PacketWorldBorder(WorldBorder var1, S44PacketWorldBorder.Action var2) {
      this.action = var2;
      this.centerX = var1.getCenterX();
      this.centerZ = var1.getCenterZ();
      this.diameter = var1.getDiameter();
      this.targetSize = var1.getTargetSize();
      this.timeUntilTarget = var1.getTimeUntilTarget();
      this.size = var1.getSize();
      this.warningDistance = var1.getWarningDistance();
      this.warningTime = var1.getWarningTime();
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.action = var1.readEnumValue(S44PacketWorldBorder.Action.class);
      switch (this.action) {
         case SET_SIZE:
            this.targetSize = var1.readDouble();
            break;
         case LERP_SIZE:
            this.diameter = var1.readDouble();
            this.targetSize = var1.readDouble();
            this.timeUntilTarget = var1.readVarLong();
            break;
         case SET_CENTER:
            this.centerX = var1.readDouble();
            this.centerZ = var1.readDouble();
            break;
         case SET_WARNING_BLOCKS:
            this.warningDistance = var1.readVarIntFromBuffer();
            break;
         case SET_WARNING_TIME:
            this.warningTime = var1.readVarIntFromBuffer();
            break;
         case INITIALIZE:
            this.centerX = var1.readDouble();
            this.centerZ = var1.readDouble();
            this.diameter = var1.readDouble();
            this.targetSize = var1.readDouble();
            this.timeUntilTarget = var1.readVarLong();
            this.size = var1.readVarIntFromBuffer();
            this.warningDistance = var1.readVarIntFromBuffer();
            this.warningTime = var1.readVarIntFromBuffer();
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeEnumValue(this.action);
      switch (this.action) {
         case SET_SIZE:
            var1.writeDouble(this.targetSize);
            break;
         case LERP_SIZE:
            var1.writeDouble(this.diameter);
            var1.writeDouble(this.targetSize);
            var1.writeVarLong(this.timeUntilTarget);
            break;
         case SET_CENTER:
            var1.writeDouble(this.centerX);
            var1.writeDouble(this.centerZ);
            break;
         case SET_WARNING_BLOCKS:
            var1.writeVarIntToBuffer(this.warningDistance);
            break;
         case SET_WARNING_TIME:
            var1.writeVarIntToBuffer(this.warningTime);
            break;
         case INITIALIZE:
            var1.writeDouble(this.centerX);
            var1.writeDouble(this.centerZ);
            var1.writeDouble(this.diameter);
            var1.writeDouble(this.targetSize);
            var1.writeVarLong(this.timeUntilTarget);
            var1.writeVarIntToBuffer(this.size);
            var1.writeVarIntToBuffer(this.warningDistance);
            var1.writeVarIntToBuffer(this.warningTime);
      }
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleWorldBorder(this);
   }

   public void func_179788_a(WorldBorder var1) {
      switch (this.action) {
         case SET_SIZE:
            var1.setTransition(this.targetSize);
            break;
         case LERP_SIZE:
            var1.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
            break;
         case SET_CENTER:
            var1.setCenter(this.centerX, this.centerZ);
            break;
         case SET_WARNING_BLOCKS:
            var1.setWarningDistance(this.warningDistance);
            break;
         case SET_WARNING_TIME:
            var1.setWarningTime(this.warningTime);
            break;
         case INITIALIZE:
            var1.setCenter(this.centerX, this.centerZ);
            if (this.timeUntilTarget > 0L) {
               var1.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
            } else {
               var1.setTransition(this.targetSize);
            }

            var1.setSize(this.size);
            var1.setWarningDistance(this.warningDistance);
            var1.setWarningTime(this.warningTime);
      }
   }

   public enum Action {
      SET_SIZE,
      LERP_SIZE,
      SET_CENTER,
      INITIALIZE,
      SET_WARNING_TIME,
      SET_WARNING_BLOCKS;
   }
}
