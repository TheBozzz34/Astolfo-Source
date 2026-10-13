package net.minecraft.client.renderer.block.statemap;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;

public class BlockStateMapper {
   private Map<Block, IStateMapper> blockStateMap = Maps.newIdentityHashMap();
   private Set<Block> setBuiltInBlocks = Sets.newIdentityHashSet();
   private static final long a = 97922312865680L;

   public void registerBlockStateMapper(Block var1, IStateMapper var2) {
      this.blockStateMap.put(var1, var2);
   }

   public void registerBuiltInBlocks(Block... var1) {
      Collections.addAll(this.setBuiltInBlocks, var1);
   }

   public Map<IBlockState, ModelResourceLocation> putAllStateModelLocations() {
      IdentityHashMap var4 = Maps.newIdentityHashMap();

      for (Block var6 : Block.blockRegistry) {
         if (!this.setBuiltInBlocks.contains(var6)) {
            var4.putAll(Objects.firstNonNull(this.blockStateMap.get(var6), new DefaultStateMapper()).putStateModelLocations(var6));
         }
      }

      return var4;
   }
}
