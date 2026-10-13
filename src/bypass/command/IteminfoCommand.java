package bypass.command;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class IteminfoCommand extends Command {
   private static final long d = 81750944261793L;

   public IteminfoCommand(String var1, String var2) {
      super(var1, var2);
   }

   @Override
   public void runCommand(String[] var1) {
      if (var1.length == 2) {
         for (EntityPlayer var6 : mc.theWorld.playerEntities) {
            if (var1[1].equals(var6.getName())) {
               ItemStack var7 = var6.getHeldItem();
               if (var7 != null) {
                  if (var7.V() != null) {
                     chat.I(var7.getDisplayName());
                     if (var7.V().hasKey("display", 10)) {
                        NBTTagCompound var8 = var7.V().getCompoundTag("display");
                        NBTTagList var9 = var8.getTagList("Lore", 8);
                        if (var9.tagCount() > 0) {
                           for (int var10 = 0; var10 < var9.tagCount(); var10++) {
                              chat.I(var9.getStringTagAt(var10));
                           }
                        }
                     }
                  }

                  return;
               }
            }
         }

         chat.I("Player not found");
      }
   }
}
