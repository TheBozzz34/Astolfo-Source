package net.minecraft.nbt;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import net.minecraft.util.StringUtils;

public final class NBTUtil {
   private static final long a = 95182781775091L;

   public static GameProfile readGameProfileFromNBT(NBTTagCompound var0) {
      String var4 = null;
      String var5 = null;
      if (var0.hasKey("Name", 8)) {
         var4 = var0.getString("Name");
      }

      if (var0.hasKey("Id", 8)) {
         var5 = var0.getString("Id");
      }

      if (StringUtils.isNullOrEmpty(var4) && StringUtils.isNullOrEmpty(var5)) {
         return null;
      }

      UUID var6;
      try {
         var6 = UUID.fromString(var5);
      } catch (Throwable var15) {
         var6 = null;
      }

      GameProfile var7 = new GameProfile(var6, var4);
      if (var0.hasKey("Properties", 10)) {
         NBTTagCompound var8 = var0.getCompoundTag("Properties");

         for (String var10 : var8.getKeySet()) {
            NBTTagList var11 = var8.getTagList(var10, 10);

            for (int var12 = 0; var12 < var11.tagCount(); var12++) {
               NBTTagCompound var13 = var11.getCompoundTagAt(var12);
               String var14 = var13.getString("Value");
               if (var13.hasKey("Signature", 8)) {
                  var7.getProperties().put(var10, new Property(var10, var14, var13.getString("Signature")));
               } else {
                  var7.getProperties().put(var10, new Property(var10, var14));
               }
            }
         }
      }

      return var7;
   }

   public static NBTTagCompound writeGameProfile(NBTTagCompound var0, GameProfile var1) {
      if (!StringUtils.isNullOrEmpty(var1.getName())) {
         var0.setString("Name", var1.getName());
      }

      if (var1.getId() != null) {
         var0.setString("Id", var1.getId().toString());
      }

      if (!var1.getProperties().isEmpty()) {
         NBTTagCompound var5 = new NBTTagCompound();

         for (String var7 : var1.getProperties().keySet()) {
            NBTTagList var8 = new NBTTagList();

            for (Property var10 : var1.getProperties().get(var7)) {
               NBTTagCompound var11 = new NBTTagCompound();
               var11.setString("Value", var10.getValue());
               if (var10.hasSignature()) {
                  var11.setString("Signature", var10.getSignature());
               }

               var8.appendTag(var11);
            }

            var5.setTag(var7, var8);
         }

         var0.setTag("Properties", var5);
      }

      return var0;
   }

   public static boolean func_181123_a(NBTBase var0, NBTBase var1, boolean var2) {
      if (var0 == var1) {
         return true;
      }

      if (var0 == null) {
         return true;
      }

      if (var1 == null) {
         return false;
      }

      if (!var0.getClass().equals(var1.getClass())) {
         return false;
      }

      if (var0 instanceof NBTTagCompound) {
         NBTTagCompound var12 = (NBTTagCompound)var0;
         NBTTagCompound var13 = (NBTTagCompound)var1;

         for (String var15 : var12.getKeySet()) {
            NBTBase var16 = var12.getTag(var15);
            if (!func_181123_a(var16, var13.getTag(var15), var2)) {
               return false;
            }
         }

         return true;
      } else if (var0 instanceof NBTTagList && var2) {
         NBTTagList var6 = (NBTTagList)var0;
         NBTTagList var7 = (NBTTagList)var1;
         if (var6.tagCount() == 0) {
            return var7.tagCount() == 0;
         }

         for (int var8 = 0; var8 < var6.tagCount(); var8++) {
            NBTBase var9 = var6.get(var8);
            boolean var10 = false;

            for (int var11 = 0; var11 < var7.tagCount(); var11++) {
               if (func_181123_a(var9, var7.get(var11), var2)) {
                  var10 = true;
                  break;
               }
            }

            if (!var10) {
               return false;
            }
         }

         return true;
      } else {
         return var0.equals(var1);
      }
   }
}
