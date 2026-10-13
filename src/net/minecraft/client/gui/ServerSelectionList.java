package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;

public class ServerSelectionList extends GuiListExtended {
   private final GuiMultiplayer owner;
   private final List<ServerListEntryNormal> serverListInternet = Lists.newArrayList();
   private final List<ServerListEntryLanDetected> serverListLan = Lists.newArrayList();
   private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
   private int selectedSlotIndex = -1;
   private static final long c = 125064227958210L;

   public ServerSelectionList(GuiMultiplayer var1, Minecraft var2, int var3, int var4, int var5, int var6, int var7) {
      super(var2, var3, var4, var5, var6, var7);
      this.owner = var1;
   }

   @Override
   public GuiListExtended.IGuiListEntry getListEntry(int var1) {
      if (var1 < this.serverListInternet.size()) {
         return this.serverListInternet.get(var1);
      }

      var1 -= this.serverListInternet.size();
      return var1 == 0 ? this.lanScanEntry : this.serverListLan.get(--var1);
   }

   @Override
   public int getSize() {
      return this.serverListInternet.size() + 1 + this.serverListLan.size();
   }

   public void setSelectedSlotIndex(int var1) {
      this.selectedSlotIndex = var1;
   }

   @Override
   public boolean isSelected(int var1) {
      return var1 == this.selectedSlotIndex;
   }

   public int func_148193_k() {
      return this.selectedSlotIndex;
   }

   public void func_148195_a(ServerList var1) {
      this.serverListInternet.clear();

      for (int var5 = 0; var5 < var1.countServers(); var5++) {
         this.serverListInternet.add(new ServerListEntryNormal(this.owner, var1.getServerData(var5)));
      }
   }

   public void func_148194_a(List<LanServerDetector.LanServer> var1) {
      this.serverListLan.clear();

      for (LanServerDetector.LanServer var6 : var1) {
         this.serverListLan.add(new ServerListEntryLanDetected(this.owner, var6));
      }
   }

   @Override
   public int getScrollBarX() {
      return super.getScrollBarX() + 30;
   }

   @Override
   public int getListWidth() {
      return super.getListWidth() + 85;
   }
}
