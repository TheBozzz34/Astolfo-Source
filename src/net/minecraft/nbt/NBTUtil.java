package net.minecraft.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.util.StringUtils;

public final class NBTUtil {
   private static final long a = 95182781775091L;

   public static GameProfile readGameProfileFromNBT(NBTTagCompound compound) {
      String string = null;
      String string2 = null;
      if (compound.hasKey("Name", 8)) {
         string = compound.getString("Name");
      }

      if (compound.hasKey("Id", 8)) {
         string2 = compound.getString("Id");
      }

      if (StringUtils.isNullOrEmpty(string) && StringUtils.isNullOrEmpty(string2)) {
         return null;
      }

      UUID uuid;
      try {
         uuid = UUID.fromString(string2);
      } catch (Throwable throwable) {
         uuid = null;
      }

      GameProfile gameProfile = new GameProfile(uuid, string);
      if (compound.hasKey("Properties", 10)) {
         NBTTagCompound nbtTagCompound = compound.getCompoundTag("Properties");

         for (String string3 : nbtTagCompound.getKeySet()) {
            NBTTagList nbtTagList = nbtTagCompound.getTagList(string3, 10);

            for (int i = 0; i < nbtTagList.tagCount(); i++) {
               NBTTagCompound nbtTagCompound2 = nbtTagList.getCompoundTagAt(i);
               String string4 = nbtTagCompound2.getString("Value");
               if (nbtTagCompound2.hasKey("Signature", 8)) {
                  gameProfile.getProperties().put(string3, new Property(string3, string4, nbtTagCompound2.getString("Signature")));
               } else {
                  gameProfile.getProperties().put(string3, new Property(string3, string4));
               }
            }
         }
      }

      return gameProfile;
   }

   public static NBTTagCompound writeGameProfile(NBTTagCompound tagCompound, GameProfile profile) {
      if (!StringUtils.isNullOrEmpty(profile.getName())) {
         tagCompound.setString("Name", profile.getName());
      }

      if (profile.getId() != null) {
         tagCompound.setString("Id", profile.getId().toString());
      }

      if (!profile.getProperties().isEmpty()) {
         NBTTagCompound nbtTagCompound = new NBTTagCompound();

         for (String string : profile.getProperties().keySet()) {
            NBTTagList nbtTagList = new NBTTagList();

            for (Property property : profile.getProperties().get(string)) {
               NBTTagCompound nbtTagCompound2 = new NBTTagCompound();
               nbtTagCompound2.setString("Value", property.getValue());
               if (property.hasSignature()) {
                  nbtTagCompound2.setString("Signature", property.getSignature());
               }

               nbtTagList.appendTag(nbtTagCompound2);
            }

            nbtTagCompound.setTag(string, nbtTagList);
         }

         tagCompound.setTag("Properties", nbtTagCompound);
      }

      return tagCompound;
   }

   public static boolean func_181123_a(NBTBase nbtBase, NBTBase nbtBase2, boolean bl) {
      if (nbtBase == nbtBase2) {
         return true;
      }

      if (nbtBase == null) {
         return true;
      }

      if (nbtBase2 == null) {
         return false;
      }

      if (!nbtBase.getClass().equals(nbtBase2.getClass())) {
         return false;
      }

      if (nbtBase instanceof NBTTagCompound) {
         NBTTagCompound nbtTagCompound = (NBTTagCompound)nbtBase;
         NBTTagCompound nbtTagCompound2 = (NBTTagCompound)nbtBase2;

         for (String string : nbtTagCompound.getKeySet()) {
            NBTBase nbtBase3 = nbtTagCompound.getTag(string);
            if (!func_181123_a(nbtBase3, nbtTagCompound2.getTag(string), bl)) {
               return false;
            }
         }

         return true;
      } else if (nbtBase instanceof NBTTagList && bl) {
         NBTTagList nbtTagList = (NBTTagList)nbtBase;
         NBTTagList nbtTagList2 = (NBTTagList)nbtBase2;
         if (nbtTagList.tagCount() == 0) {
            return nbtTagList2.tagCount() == 0;
         }

         for (int i = 0; i < nbtTagList.tagCount(); i++) {
            NBTBase nbtBase4 = nbtTagList.get(i);
            boolean bl2 = false;

            for (int j = 0; j < nbtTagList2.tagCount(); j++) {
               if (func_181123_a(nbtBase4, nbtTagList2.get(j), bl)) {
                  bl2 = true;
                  break;
               }
            }

            if (!bl2) {
               return false;
            }
         }

         return true;
      } else {
         return nbtBase.equals(nbtBase2);
      }
   }
}
