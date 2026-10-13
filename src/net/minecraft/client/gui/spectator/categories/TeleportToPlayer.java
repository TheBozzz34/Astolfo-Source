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
      public int compare(NetworkPlayerInfo var1, NetworkPlayerInfo var2) {
         return ComparisonChain.start().compare(var1.getGameProfile().getId(), var2.getGameProfile().getId()).result();
      }
   });
   private final List<ISpectatorMenuObject> field_178673_b = Lists.newArrayList();
   private static final long a = 39221179220470L;

   public TeleportToPlayer() {
      this(field_178674_a.sortedCopy(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()));
   }

   public TeleportToPlayer(Collection<NetworkPlayerInfo> var1) {
      for (NetworkPlayerInfo var6 : field_178674_a.sortedCopy(var1)) {
         if (var6.getGameType() != WorldSettings.GameType.SPECTATOR) {
            this.field_178673_b.add(new PlayerMenuObject(var6.getGameProfile()));
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
   public void func_178661_a(SpectatorMenu var1) {
      var1.func_178647_a(this);
   }

   @Override
   public IChatComponent getSpectatorName() {
      return new ChatComponentText("Teleport to player");
   }

   @Override
   public void func_178663_a(float var1, int var2) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.field_175269_a);
      Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   @Override
   public boolean func_178662_A_() {
      return !this.field_178673_b.isEmpty();
   }
}
