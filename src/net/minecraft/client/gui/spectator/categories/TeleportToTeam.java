package net.minecraft.client.gui.spectator.categories;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuView;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TeleportToTeam implements ISpectatorMenuView, ISpectatorMenuObject {
   private final List<ISpectatorMenuObject> field_178672_a = Lists.newArrayList();

   public TeleportToTeam() {
      Minecraft minecraft = Minecraft.getMinecraft();

      for (ScorePlayerTeam scorePlayerTeam : minecraft.theWorld.getScoreboard().getTeams()) {
         this.field_178672_a.add(new TeleportToTeam.TeamSelectionObject(scorePlayerTeam));
      }
   }

   @Override
   public List<ISpectatorMenuObject> func_178669_a() {
      return this.field_178672_a;
   }

   @Override
   public IChatComponent func_178670_b() {
      return new ChatComponentText("Select a team to teleport to");
   }

   @Override
   public void func_178661_a(SpectatorMenu menu) {
      menu.func_178647_a(this);
   }

   @Override
   public IChatComponent getSpectatorName() {
      return new ChatComponentText("Teleport to team member");
   }

   @Override
   public void func_178663_a(float f, int alpha) {
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiSpectator.field_175269_a);
      Gui.drawModalRectWithCustomSizedTexture(0, 0, 16.0F, 0.0F, 16, 16, 256.0F, 256.0F);
   }

   @Override
   public boolean func_178662_A_() {
      for (ISpectatorMenuObject iSpectatorMenuObject : this.field_178672_a) {
         if (iSpectatorMenuObject.func_178662_A_()) {
            return true;
         }
      }

      return false;
   }

   public class TeamSelectionObject implements ISpectatorMenuObject {
      private final ScorePlayerTeam field_178676_b;
      private final ResourceLocation field_178677_c;
      private final List<NetworkPlayerInfo> field_178675_d;

      public TeamSelectionObject(ScorePlayerTeam scorePlayerTeam) {
         this.field_178676_b = scorePlayerTeam;
         this.field_178675_d = Lists.newArrayList();

         for (String string : scorePlayerTeam.getMembershipCollection()) {
            NetworkPlayerInfo networkPlayerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(string);
            if (networkPlayerInfo != null) {
               this.field_178675_d.add(networkPlayerInfo);
            }
         }

         if (!this.field_178675_d.isEmpty()) {
            String string2 = this.field_178675_d.get(new Random().nextInt(this.field_178675_d.size())).getGameProfile().getName();
            this.field_178677_c = AbstractClientPlayer.getLocationSkin(string2);
            AbstractClientPlayer.getDownloadImageSkin(this.field_178677_c, string2);
         } else {
            this.field_178677_c = DefaultPlayerSkin.getDefaultSkinLegacy();
         }
      }

      @Override
      public void func_178661_a(SpectatorMenu menu) {
         menu.func_178647_a(new TeleportToPlayer(this.field_178675_d));
      }

      @Override
      public IChatComponent getSpectatorName() {
         return new ChatComponentText(this.field_178676_b.getTeamName());
      }

      @Override
      public void func_178663_a(float f, int alpha) {
         int i = -1;
         String string = FontRenderer.getFormatFromString(this.field_178676_b.getColorPrefix());
         if (string.length() >= 2) {
            i = Minecraft.getMinecraft().fontRendererObj.getColorCode(string.charAt(1));
         }

         if (i >= 0) {
            float g = (i >> 16 & 0xFF) / 255.0F;
            float h = (i >> 8 & 0xFF) / 255.0F;
            float f2 = (i & 0xFF) / 255.0F;
            Gui.drawRect(1, 1, 15, 15, MathHelper.func_180183_b(g * f, h * f, f2 * f) | alpha << 24);
         }

         Minecraft.getMinecraft().getTextureManager().bindTexture(this.field_178677_c);
         GlStateManager.color(f, f, f, alpha / 255.0F);
         Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
         Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
      }

      @Override
      public boolean func_178662_A_() {
         return !this.field_178675_d.isEmpty();
      }
   }
}
