package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

public class S01PacketJoinGame implements Packet<INetHandlerPlayClient> {
   private int entityId;
   private boolean hardcoreMode;
   private WorldSettings.GameType gameType;
   private int dimension;
   private EnumDifficulty difficulty;
   private int maxPlayers;
   private WorldType worldType;
   private boolean reducedDebugInfo;
   private static final long a = 119535100722861L;

   public S01PacketJoinGame() {
   }

   public S01PacketJoinGame(int var1, WorldSettings.GameType var2, boolean var3, int var4, EnumDifficulty var5, int var6, WorldType var7, boolean var8) {
      this.entityId = var1;
      this.dimension = var4;
      this.difficulty = var5;
      this.gameType = var2;
      this.maxPlayers = var6;
      this.hardcoreMode = var3;
      this.worldType = var7;
      this.reducedDebugInfo = var8;
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.entityId = var1.readInt();
      int var5 = var1.readUnsignedByte();
      this.hardcoreMode = (var5 & 8) == 8;
      var5 &= -9;
      this.gameType = WorldSettings.GameType.getByID(var5);
      this.dimension = var1.readByte();
      this.difficulty = EnumDifficulty.getDifficultyEnum(var1.readUnsignedByte());
      this.maxPlayers = var1.readUnsignedByte();
      this.worldType = WorldType.parseWorldType(var1.readStringFromBuffer(16));
      if (this.worldType == null) {
         this.worldType = WorldType.DEFAULT;
      }

      this.reducedDebugInfo = var1.readBoolean();
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeInt(this.entityId);
      int var5 = this.gameType.getID();
      if (this.hardcoreMode) {
         var5 |= 8;
      }

      var1.writeByte(var5);
      var1.writeByte(this.dimension);
      var1.writeByte(this.difficulty.getDifficultyId());
      var1.writeByte(this.maxPlayers);
      var1.writeString(this.worldType.getWorldTypeName());
      var1.writeBoolean(this.reducedDebugInfo);
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handleJoinGame(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public boolean isHardcoreMode() {
      return this.hardcoreMode;
   }

   public WorldSettings.GameType getGameType() {
      return this.gameType;
   }

   public int getDimension() {
      return this.dimension;
   }

   public EnumDifficulty getDifficulty() {
      return this.difficulty;
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public WorldType getWorldType() {
      return this.worldType;
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }
}
