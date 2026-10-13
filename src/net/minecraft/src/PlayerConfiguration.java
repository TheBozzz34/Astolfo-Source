package net.minecraft.src;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;

public class PlayerConfiguration {
   private PlayerItemModel[] playerItemModels = new PlayerItemModel[0];
   private boolean initialized = false;
   private static final long a = 16156671693352L;

   public void renderPlayerItems(ModelBiped var1, AbstractClientPlayer var2, float var3, float var4) {
      if (this.initialized) {
         for (int var8 = 0; var8 < this.playerItemModels.length; var8++) {
            PlayerItemModel var9 = this.playerItemModels[var8];
            var9.render(var1, var2, var3, var4);
         }
      }
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public void setInitialized(boolean var1) {
      this.initialized = var1;
   }

   public PlayerItemModel[] getPlayerItemModels() {
      return this.playerItemModels;
   }

   public void addPlayerItemModel(PlayerItemModel var1) {
      this.playerItemModels = (PlayerItemModel[])Config.addObjectToArray(this.playerItemModels, var1);
   }
}
