package bypass.command;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class IteminfoCommand extends Command {
   public IteminfoCommand(String string, String string2) {
      super(string, string2);
   }

   @Override
   public void runCommand(String[] strings) {
      if (strings.length == 2) {
         for (EntityPlayer entityPlayer : mc.theWorld.playerEntities) {
            if (strings[1].equals(entityPlayer.getName())) {
               ItemStack itemStack = entityPlayer.getHeldItem();
               if (itemStack != null) {
                  if (itemStack.V() != null) {
                     chat.I(itemStack.getDisplayName());
                     if (itemStack.V().hasKey("display", 10)) {
                        NBTTagCompound nbtTagCompound = itemStack.V().getCompoundTag("display");
                        NBTTagList nbtTagList = nbtTagCompound.getTagList("Lore", 8);
                        if (nbtTagList.tagCount() > 0) {
                           for (int i = 0; i < nbtTagList.tagCount(); i++) {
                              chat.I(nbtTagList.getStringTagAt(i));
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
