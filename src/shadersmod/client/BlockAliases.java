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

   public static int getMappedBlockId(int blockId, int metadata) {
      if (blockAliases == null) {
         return blockId;
      }

      if (blockId >= 0 && blockId < blockAliases.length) {
         BlockAlias[] blockAliass = blockAliases[blockId];
         if (blockAliass == null) {
            return blockId;
         }

         for (int i = 0; i < blockAliass.length; i++) {
            BlockAlias blockAlias = blockAliass[i];
            if (blockAlias.matches(blockId, metadata)) {
               return blockAlias.getBlockId();
            }
         }

         return blockId;
      } else {
         return blockId;
      }
   }

   public static void update(IShaderPack shaderPack) {
      reset();
      String string = "/shaders/block.properties";

      try {
         InputStream inputStream = shaderPack.getResourceAsStream(string);
         if (inputStream == null) {
            return;
         }

         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(inputStream);
         inputStream.close();
         Config.dbg("[Shaders] Parsing block mappings: " + string);
         ArrayList arrayList = new ArrayList();
         ConnectedParser connectedParser = new ConnectedParser("Shaders");

         for (Object object : propertiesOrdered.keySet()) {
            String string2 = (String)object;
            String string3 = propertiesOrdered.getProperty(string2);
            String string4 = "block.";
            if (!string2.startsWith(string4)) {
               Config.warn("[Shaders] Invalid block ID: " + string2);
            } else {
               String string5 = StrUtils.removePrefix(string2, string4);
               int i = Config.parseInt(string5, -1);
               if (i < 0) {
                  Config.warn("[Shaders] Invalid block ID: " + string2);
               } else {
                  MatchBlock[] matchBlocks = connectedParser.parseMatchBlocks(string3);
                  if (matchBlocks != null && matchBlocks.length >= 1) {
                     BlockAlias blockAlias = new BlockAlias(i, matchBlocks);
                     addToList(arrayList, blockAlias);
                  } else {
                     Config.warn("[Shaders] Invalid block ID mapping: " + string2 + "=" + string3);
                  }
               }
            }
         }

         if (arrayList.size() <= 0) {
            return;
         }

         blockAliases = toArrays(arrayList);
      } catch (IOException ioException) {
         Config.warn("[Shaders] Error reading: " + string);
      }
   }

   private static void addToList(List<List<BlockAlias>> blocksAliases, BlockAlias ba) {
      int[] ints = ba.getMatchBlockIds();

      for (int i = 0; i < ints.length; i++) {
         int j = ints[i];

         while (j >= blocksAliases.size()) {
            blocksAliases.add(null);
         }

         List list = (List)blocksAliases.get(j);
         if (list == null) {
            list = new ArrayList();
            blocksAliases.set(j, list);
         }

         list.add(ba);
      }
   }

   private static BlockAlias[][] toArrays(List<List<BlockAlias>> listBlocksAliases) {
      BlockAlias[][] blockAliasss = new BlockAlias[listBlocksAliases.size()][];

      for (int i = 0; i < blockAliasss.length; i++) {
         List list = (List)listBlocksAliases.get(i);
         if (list != null) {
            blockAliasss[i] = (shadersmod.client.BlockAlias[])(list.toArray(new BlockAlias[list.size()]));
         }
      }

      return blockAliasss;
   }

   public static void reset() {
      blockAliases = (BlockAlias[][])null;
   }
}
