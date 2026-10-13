package shadersmod.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.src.Config;
import net.minecraft.src.ConnectedParser;
import net.minecraft.src.MatchBlock;
import net.minecraft.src.PropertiesOrdered;
import net.minecraft.src.StrUtils;

public class BlockAliases {
   private static BlockAlias[][] blockAliases = (BlockAlias[][])null;
   private static final long a = 73285643416541L;

   public static int getMappedBlockId(int var0, int var1) {
      if (blockAliases == null) {
         return var0;
      }

      if (var0 >= 0 && var0 < blockAliases.length) {
         BlockAlias[] var5 = blockAliases[var0];
         if (var5 == null) {
            return var0;
         }

         for (int var6 = 0; var6 < var5.length; var6++) {
            BlockAlias var7 = var5[var6];
            if (var7.matches(var0, var1)) {
               return var7.getBlockId();
            }
         }

         return var0;
      } else {
         return var0;
      }
   }

   public static void update(IShaderPack var0) {
      long var1 = a ^ 34939924747532L;
      Q();
      String var4 = "/shaders/block.properties";

      try {
         InputStream var5 = var0.getResourceAsStream(var4);
         if (var5 == null) {
            return;
         }

         PropertiesOrdered var6 = new PropertiesOrdered();
         var6.load(var5);
         var5.close();
         Config.dbg("[Shaders] Parsing block mappings: " + var4);
         ArrayList var7 = new ArrayList();
         ConnectedParser var8 = new ConnectedParser("Shaders");

         for (Object var10 : var6.keySet()) {
            String var11 = (String)var10;
            long var10000 = 7580225034291966289L ^ var1;
            String var12 = var6.getProperty(var11);
            String var13 = "block.";
            if (!var11.startsWith(var13)) {
               Config.warn("[Shaders] Invalid block ID: " + var11);
            } else {
               String var14 = StrUtils.removePrefix(var11, var13);
               int var15 = Config.parseInt(var14, -1);
               if (var15 < 0) {
                  Config.warn("[Shaders] Invalid block ID: " + var11);
               } else {
                  MatchBlock[] var16 = var8.t(var12);
                  if (var16 != null && var16.length >= 1) {
                     BlockAlias var17 = new BlockAlias(var15, var16);
                     addToList(var7, var17);
                  } else {
                     Config.warn("[Shaders] Invalid block ID mapping: " + var11 + "=" + var12);
                  }
               }
            }
         }

         if (var7.size() <= 0) {
            return;
         }

         blockAliases = toArrays(var7);
      } catch (IOException var18) {
         Config.warn("[Shaders] Error reading: " + var4);
      }
   }

   private static void addToList(List<List<BlockAlias>> var0, BlockAlias var1) {
      int[] var5 = var1.getMatchBlockIds();

      for (int var6 = 0; var6 < var5.length; var6++) {
         int var7 = var5[var6];

         while (var7 >= var0.size()) {
            var0.add(null);
         }

         List var8 = (List)var0.get(var7);
         if (var8 == null) {
            var8 = new ArrayList();
            var0.set(var7, var8);
         }

         var8.add(var1);
      }
   }

   private static BlockAlias[][] toArrays(List<List<BlockAlias>> var0) {
      BlockAlias[][] var4 = new BlockAlias[var0.size()][];

      for (int var5 = 0; var5 < var4.length; var5++) {
         List var6 = (List)var0.get(var5);
         if (var6 != null) {
            var4[var5] = (shadersmod.client.BlockAlias[])(var6.toArray(new BlockAlias[var6.size()]));
         }
      }

      return var4;
   }

   public static void Q() {
      blockAliases = (BlockAlias[][])null;
   }
}
