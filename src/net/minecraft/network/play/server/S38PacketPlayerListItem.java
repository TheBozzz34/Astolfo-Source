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

   public S38PacketPlayerListItem() {
   }

   public S38PacketPlayerListItem(S38PacketPlayerListItem.Action actionIn, EntityPlayerMP... players) {
      this.action = actionIn;

      for (EntityPlayerMP entityPlayerMP : players) {
         this.players
            .add(
               new S38PacketPlayerListItem.AddPlayerData(
                  entityPlayerMP.getGameProfile(), entityPlayerMP.ping, entityPlayerMP.theItemInWorldManager.getGameType(), entityPlayerMP.getTabListDisplayName()
               )
            );
      }
   }

   public S38PacketPlayerListItem(S38PacketPlayerListItem.Action actionIn, Iterable<EntityPlayerMP> players) {
      this.action = actionIn;

      for (EntityPlayerMP entityPlayerMP : players) {
         this.players
            .add(
               new S38PacketPlayerListItem.AddPlayerData(
                  entityPlayerMP.getGameProfile(), entityPlayerMP.ping, entityPlayerMP.theItemInWorldManager.getGameType(), entityPlayerMP.getTabListDisplayName()
               )
            );
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.action = buf.readEnumValue(S38PacketPlayerListItem.Action.class);
      int i = buf.readVarIntFromBuffer();

      for (int j = 0; j < i; j++) {
         GameProfile gameProfile = null;
         int k = 0;
         WorldSettings.GameType gameType = null;
         IChatComponent iChatComponent = null;
         switch (this.action) {
            case ADD_PLAYER:
               gameProfile = new GameProfile(buf.readUuid(), buf.readStringFromBuffer(16));
               int l = buf.readVarIntFromBuffer();
               int m = 0;

               for (; m < l; m++) {
                  String string = buf.readStringFromBuffer(32767);
                  String string2 = buf.readStringFromBuffer(32767);
                  if (buf.readBoolean()) {
                     gameProfile.getProperties().put(string, new Property(string, string2, buf.readStringFromBuffer(32767)));
                  } else {
                     gameProfile.getProperties().put(string, new Property(string, string2));
                  }
               }

               gameType = WorldSettings.GameType.getByID(buf.readVarIntFromBuffer());
               k = buf.readVarIntFromBuffer();
               if (buf.readBoolean()) {
                  iChatComponent = buf.readChatComponent();
               }
               break;
            case UPDATE_GAME_MODE:
               gameProfile = new GameProfile(buf.readUuid(), (String)null);
               gameType = WorldSettings.GameType.getByID(buf.readVarIntFromBuffer());
               break;
            case UPDATE_LATENCY:
               gameProfile = new GameProfile(buf.readUuid(), (String)null);
               k = buf.readVarIntFromBuffer();
               break;
            case UPDATE_DISPLAY_NAME:
               gameProfile = new GameProfile(buf.readUuid(), (String)null);
               if (buf.readBoolean()) {
                  iChatComponent = buf.readChatComponent();
               }
               break;
            case REMOVE_PLAYER:
               gameProfile = new GameProfile(buf.readUuid(), (String)null);
         }

         this.players.add(new S38PacketPlayerListItem.AddPlayerData(gameProfile, k, gameType, iChatComponent));
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.action);
      buf.writeVarIntToBuffer(this.players.size());

      for (S38PacketPlayerListItem.AddPlayerData addPlayerData : this.players) {
         switch (this.action) {
            case ADD_PLAYER:
               buf.writeUuid(addPlayerData.getProfile().getId());
               buf.writeString(addPlayerData.getProfile().getName());
               buf.writeVarIntToBuffer(addPlayerData.getProfile().getProperties().size());

               for (Property property : addPlayerData.getProfile().getProperties().values()) {
                  buf.writeString(property.getName());
                  buf.writeString(property.getValue());
                  if (property.hasSignature()) {
                     buf.writeBoolean(true);
                     buf.writeString(property.getSignature());
                  } else {
                     buf.writeBoolean(false);
                  }
               }

               buf.writeVarIntToBuffer(addPlayerData.getGameMode().getID());
               buf.writeVarIntToBuffer(addPlayerData.getPing());
               if (addPlayerData.getDisplayName() == null) {
                  buf.writeBoolean(false);
               } else {
                  buf.writeBoolean(true);
                  buf.writeChatComponent(addPlayerData.getDisplayName());
               }
               break;
            case UPDATE_GAME_MODE:
               buf.writeUuid(addPlayerData.getProfile().getId());
               buf.writeVarIntToBuffer(addPlayerData.getGameMode().getID());
               break;
            case UPDATE_LATENCY:
               buf.writeUuid(addPlayerData.getProfile().getId());
               buf.writeVarIntToBuffer(addPlayerData.getPing());
               break;
            case UPDATE_DISPLAY_NAME:
               buf.writeUuid(addPlayerData.getProfile().getId());
               if (addPlayerData.getDisplayName() == null) {
                  buf.writeBoolean(false);
               } else {
                  buf.writeBoolean(true);
                  buf.writeChatComponent(addPlayerData.getDisplayName());
               }
               break;
            case REMOVE_PLAYER:
               buf.writeUuid(addPlayerData.getProfile().getId());
         }
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handlePlayerListItem(this);
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

      public AddPlayerData(GameProfile profile, int pingIn, WorldSettings.GameType gamemodeIn, IChatComponent displayNameIn) {
         this.profile = profile;
         this.ping = pingIn;
         this.gamemode = gamemodeIn;
         this.displayName = displayNameIn;
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
