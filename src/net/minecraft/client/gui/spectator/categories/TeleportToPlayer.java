package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class TeleportToPlayer implements ISpectatorMenuView, ISpectatorMenuObject {
   private static final Ordering<NetworkPlayerInfo> field_178674_a = Ordering.from(new Comparator<NetworkPlayerInfo>() {
      public int compare(NetworkPlayerInfo networkPlayerInfo, NetworkPlayerInfo networkPlayerInfo2) {
         return ComparisonChain.start().compare(networkPlayerInfo.getGameProfile().getId(), networkPlayerInfo2.getGameProfile().getId()).result();
      }
   });
   private final List<ISpectatorMenuObject> field_178673_b = Lists.newArrayList();

   public TeleportToPlayer() {
      this(field_178674_a.sortedCopy(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()));
   }

   public TeleportToPlayer(Collection<NetworkPlayerInfo> collection) {
      for (NetworkPlayerInfo networkPlayerInfo : field_178674_a.sortedCopy(collection)) {
         if (networkPlayerInfo.getGameType() != WorldSettings.GameType.SPECTATOR) {
            this.field_178673_b.add(new PlayerMenuObject(networkPlayerInfo.getGameProfile()));
         }
      }
   }

   @Override
   public List<ISpectatorMenuObject> func_178669_a() {
      return this.field_178673_b;
   }

   @Override
   public IChatComponent func_178670_b() {
      return new ChatComponentText("Select a player to teleport to");
   }

   @Override
   public void func_178661_a(SpectatorMenu menu) {
      menu.func_178647_a(this);
   }

   @Override
   public IChatComponent getSpectatorName() {
      return new ChatComponentText("Teleport to player");
   }

   @Override
   public void func_178663_a(float f, int alpha) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.field_175269_a);
      Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   @Override
   public boolean func_178662_A_() {
      return !this.field_178673_b.isEmpty();
   }
}
