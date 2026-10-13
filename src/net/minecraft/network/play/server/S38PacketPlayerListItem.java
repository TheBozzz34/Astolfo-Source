package net.minecraft.network.play.server;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class S38PacketPlayerListItem implements Packet<INetHandlerPlayClient> {
   private S38PacketPlayerListItem.Action action;
   private final List<S38PacketPlayerListItem.AddPlayerData> players = Lists.newArrayList();
   private static final long a = 39689564778700L;

   public S38PacketPlayerListItem() {
   }

   public S38PacketPlayerListItem(S38PacketPlayerListItem.Action var1, EntityPlayerMP... var2) {
      this.action = var1;

      for (EntityPlayerMP var9 : var2) {
         this.players
            .add(
               new S38PacketPlayerListItem.AddPlayerData(
                  var9.getGameProfile(), var9.ping, var9.theItemInWorldManager.getGameType(), var9.getTabListDisplayName()
               )
            );
      }
   }

   public S38PacketPlayerListItem(S38PacketPlayerListItem.Action var1, Iterable<EntityPlayerMP> var2) {
      this.action = var1;

      for (EntityPlayerMP var7 : var2) {
         this.players
            .add(
               new S38PacketPlayerListItem.AddPlayerData(
                  var7.getGameProfile(), var7.ping, var7.theItemInWorldManager.getGameType(), var7.getTabListDisplayName()
               )
            );
      }
   }

   @Override
   public void readPacketData(PacketBuffer var1) throws IOException {
      this.action = var1.readEnumValue(S38PacketPlayerListItem.Action.class);
      int var5 = var1.readVarIntFromBuffer();

      for (int var6 = 0; var6 < var5; var6++) {
         GameProfile var7 = null;
         int var8 = 0;
         WorldSettings.GameType var9 = null;
         IChatComponent var10 = null;
         switch (this.action) {
            case ADD_PLAYER:
               var7 = new GameProfile(var1.readUuid(), var1.readStringFromBuffer(16));
               int var11 = var1.readVarIntFromBuffer();
               int var12 = 0;

               for (; var12 < var11; var12++) {
                  String var13 = var1.readStringFromBuffer(32767);
                  String var14 = var1.readStringFromBuffer(32767);
                  if (var1.readBoolean()) {
                     var7.getProperties().put(var13, new Property(var13, var14, var1.readStringFromBuffer(32767)));
                  } else {
                     var7.getProperties().put(var13, new Property(var13, var14));
                  }
               }

               var9 = WorldSettings.GameType.getByID(var1.readVarIntFromBuffer());
               var8 = var1.readVarIntFromBuffer();
               if (var1.readBoolean()) {
                  var10 = var1.readChatComponent();
               }
               break;
            case UPDATE_GAME_MODE:
               var7 = new GameProfile(var1.readUuid(), (String)null);
               var9 = WorldSettings.GameType.getByID(var1.readVarIntFromBuffer());
               break;
            case UPDATE_LATENCY:
               var7 = new GameProfile(var1.readUuid(), (String)null);
               var8 = var1.readVarIntFromBuffer();
               break;
            case UPDATE_DISPLAY_NAME:
               var7 = new GameProfile(var1.readUuid(), (String)null);
               if (var1.readBoolean()) {
                  var10 = var1.readChatComponent();
               }
               break;
            case REMOVE_PLAYER:
               var7 = new GameProfile(var1.readUuid(), (String)null);
         }

         this.players.add(new S38PacketPlayerListItem.AddPlayerData(var7, var8, var9, var10));
      }
   }

   @Override
   public void writePacketData(PacketBuffer var1) throws IOException {
      var1.writeEnumValue(this.action);
      var1.writeVarIntToBuffer(this.players.size());

      for (S38PacketPlayerListItem.AddPlayerData var6 : this.players) {
         switch (this.action) {
            case ADD_PLAYER:
               var1.writeUuid(var6.getProfile().getId());
               var1.writeString(var6.getProfile().getName());
               var1.writeVarIntToBuffer(var6.getProfile().getProperties().size());

               for (Property var8 : var6.getProfile().getProperties().values()) {
                  var1.writeString(var8.getName());
                  var1.writeString(var8.getValue());
                  if (var8.hasSignature()) {
                     var1.writeBoolean(true);
                     var1.writeString(var8.getSignature());
                  } else {
                     var1.writeBoolean(false);
                  }
               }

               var1.writeVarIntToBuffer(var6.getGameMode().getID());
               var1.writeVarIntToBuffer(var6.getPing());
               if (var6.getDisplayName() == null) {
                  var1.writeBoolean(false);
               } else {
                  var1.writeBoolean(true);
                  var1.writeChatComponent(var6.getDisplayName());
               }
               break;
            case UPDATE_GAME_MODE:
               var1.writeUuid(var6.getProfile().getId());
               var1.writeVarIntToBuffer(var6.getGameMode().getID());
               break;
            case UPDATE_LATENCY:
               var1.writeUuid(var6.getProfile().getId());
               var1.writeVarIntToBuffer(var6.getPing());
               break;
            case UPDATE_DISPLAY_NAME:
               var1.writeUuid(var6.getProfile().getId());
               if (var6.getDisplayName() == null) {
                  var1.writeBoolean(false);
               } else {
                  var1.writeBoolean(true);
                  var1.writeChatComponent(var6.getDisplayName());
               }
               break;
            case REMOVE_PLAYER:
               var1.writeUuid(var6.getProfile().getId());
         }
      }
   }

   public void processPacket(INetHandlerPlayClient var1) {
      var1.handlePlayerListItem(this);
   }

   public List<S38PacketPlayerListItem.AddPlayerData> getEntries() {
      return this.players;
   }

   public S38PacketPlayerListItem.Action getAction() {
      return this.action;
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("action", this.action).add("entries", this.players).toString();
   }

   public enum Action {
      ADD_PLAYER,
      UPDATE_GAME_MODE,
      UPDATE_LATENCY,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER;
   }

   public class AddPlayerData {
      private final int ping;
      private final WorldSettings.GameType gamemode;
      private final GameProfile profile;
      private final IChatComponent displayName;
      private static final long a = 70903277749821L;

      public AddPlayerData(GameProfile var2, int var3, WorldSettings.GameType var4, IChatComponent var5) {
         this.profile = var2;
         this.ping = var3;
         this.gamemode = var4;
         this.displayName = var5;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getPing() {
         return this.ping;
      }

      public WorldSettings.GameType getGameMode() {
         return this.gamemode;
      }

      public IChatComponent getDisplayName() {
         return this.displayName;
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this)
            .add("latency", this.ping)
            .add("gameMode", this.gamemode)
            .add("profile", this.profile)
            .add("displayName", this.displayName == null ? null : IChatComponent.Serializer.componentToJson(this.displayName))
            .toString();
      }
   }
}
