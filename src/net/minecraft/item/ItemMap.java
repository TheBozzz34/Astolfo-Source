package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;

public class ItemMap extends ItemMapBase {
   private static final long a = 57825591181896L;

   public ItemMap() {
      this.setHasSubtypes(true);
   }

   public static MapData loadMapData(int mapId, World worldIn) {
      String string = "map_" + mapId;
      MapData mapData = (MapData)worldIn.loadItemData(MapData.class, string);
      if (mapData == null) {
         mapData = new MapData(string);
         worldIn.setItemData(string, mapData);
      }

      return mapData;
   }

   public MapData getMapData(ItemStack stack, World worldIn) {
      String string = "map_" + stack.getMetadata();
      MapData mapData = (MapData)worldIn.loadItemData(MapData.class, string);
      if (mapData == null && !worldIn.isRemote) {
         stack.setItemDamage(worldIn.getUniqueDataId("map"));
         string = "map_" + stack.getMetadata();
         mapData = new MapData(string);
         mapData.scale = 3;
         mapData.calculateMapCenter(worldIn.getWorldInfo().getSpawnX(), worldIn.getWorldInfo().getSpawnZ(), mapData.scale);
         mapData.dimension = (byte)worldIn.provider.getDimensionId();
         mapData.markDirty();
         worldIn.setItemData(string, mapData);
      }

      return mapData;
   }

   public void updateMapData(World worldIn, Entity viewer, MapData data) {
      if (worldIn.provider.getDimensionId() == data.dimension && viewer instanceof EntityPlayer) {
         int i = 1 << data.scale;
         int j = data.xCenter;
         int k = data.zCenter;
         int l = MathHelper.floor_double(viewer.posX - j) / i + 64;
         int m = MathHelper.floor_double(viewer.posZ - k) / i + 64;
         int n = 128 / i;
         if (worldIn.provider.getHasNoSky()) {
            n /= 2;
         }

         MapData.MapInfo mapInfo = data.getMapInfo((EntityPlayer)viewer);
         mapInfo.field_82569_d++;
         boolean bl = false;

         for (int i2 = l - n + 1; i2 < l + n; i2++) {
            if ((i2 & 15) == (mapInfo.field_82569_d & 15) || bl) {
               bl = false;
               double d = 0.0;

               for (int i3 = m - n - 1; i3 < m + n; i3++) {
                  if (i2 >= 0 && i3 >= -1 && i2 < 128 && i3 < 128) {
                     int i4 = i2 - l;
                     int i5 = i3 - m;
                     boolean bl2 = i4 * i4 + i5 * i5 > (n - 2) * (n - 2);
                     int i6 = (j / i + i2 - 64) * i;
                     int i7 = (k / i + i3 - 64) * i;
                     HashMultiset hashMultiset = HashMultiset.create();
                     Chunk chunk = worldIn.getChunkFromBlockCoords(new BlockPos(i6, 0, i7));
                     if (!chunk.isEmpty()) {
                        int i8 = i6 & 15;
                        int i9 = i7 & 15;
                        int i10 = 0;
                        double e = 0.0;
                        if (worldIn.provider.getHasNoSky()) {
                           int i11 = i6 + i7 * 231871;
                           i11 = i11 * i11 * 31287121 + i11 * 11;
                           if ((i11 >> 20 & 1) == 0) {
                              hashMultiset.add(Blocks.dirt.getMapColor(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT)), 10);
                           } else {
                              hashMultiset.add(
                                 Blocks.stone.getMapColor(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE)), 100
                              );
                           }

                           e = 100.0;
                        } else {
                           BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

                           for (int i12 = 0; i12 < i; i12++) {
                              for (int i13 = 0; i13 < i; i13++) {
                                 int i14 = chunk.getHeightValue(i12 + i8, i13 + i9) + 1;
                                 IBlockState iBlockState = Blocks.air.getDefaultState();
                                 if (i14 > 1) {
                                    do {
                                       iBlockState = chunk.getBlockState(mutableBlockPos.set(i12 + i8, --i14, i13 + i9));
                                    } while (iBlockState.getBlock().getMapColor(iBlockState) == MapColor.airColor && i14 > 0);

                                    if (i14 > 0 && iBlockState.getBlock().getMaterial().isLiquid()) {
                                       int i15 = i14 - 1;

                                       Block block;
                                       do {
                                          block = chunk.getBlock(i12 + i8, i15--, i13 + i9);
                                          i10++;
                                       } while (i15 > 0 && block.getMaterial().isLiquid());
                                    }
                                 }

                                 e += (double)i14 / (i * i);
                                 hashMultiset.add(iBlockState.getBlock().getMapColor(iBlockState));
                              }
                           }
                        }

                        i10 /= i * i;
                        double d2 = (e - d) * 4.0 / (i + 4) + ((i2 + i3 & 1) - 0.5) * 0.4;
                        byte b = 1;
                        if (d2 > 0.6) {
                           b = 2;
                        }

                        if (d2 < -0.6) {
                           b = 0;
                        }

                        MapColor mapColor = Iterables.getFirst(Multisets.copyHighestCountFirst(hashMultiset), MapColor.airColor);
                        if (mapColor == MapColor.waterColor) {
                           d2 = i10 * 0.1 + (i2 + i3 & 1) * 0.2;
                           b = 1;
                           if (d2 < 0.5) {
                              b = 2;
                           }

                           if (d2 > 0.9) {
                              b = 0;
                           }
                        }

                        d = e;
                        if (i3 >= 0 && i4 * i4 + i5 * i5 < n * n && (!bl2 || (i2 + i3 & 1) != 0)) {
                           byte b2 = data.colors[i2 + i3 * 128];
                           byte b3 = (byte)(mapColor.colorIndex * 4 + b);
                           if (b2 != b3) {
                              data.colors[i2 + i3 * 128] = b3;
                              data.updateMapData(i2, i3);
                              bl = true;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (!worldIn.isRemote) {
         MapData mapData = this.getMapData(stack, worldIn);
         if (entityIn instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entityIn;
            mapData.updateVisiblePlayers(entityPlayer, stack);
         }

         if (isSelected) {
            this.updateMapData(worldIn, entityIn, mapData);
         }
      }
   }

   @Override
   public Packet createMapDataPacket(ItemStack stack, World worldIn, EntityPlayer player) {
      return this.getMapData(stack, worldIn).getMapPacket(stack, worldIn, player);
   }

   @Override
   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("map_is_scaling")) {
         MapData mapData = Items.filled_map.getMapData(stack, worldIn);
         stack.setItemDamage(worldIn.getUniqueDataId("map"));
         MapData mapData2 = new MapData("map_" + stack.getMetadata());
         mapData2.scale = (byte)(mapData.scale + 1);
         if (mapData2.scale > 4) {
            mapData2.scale = 4;
         }

         mapData2.calculateMapCenter(mapData.xCenter, mapData.zCenter, mapData2.scale);
         mapData2.dimension = mapData.dimension;
         mapData2.markDirty();
         worldIn.setItemData("map_" + stack.getMetadata(), mapData2);
      }
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      MapData mapData = this.getMapData(stack, playerIn.worldObj);
      if (advanced) {
         if (mapData == null) {
            tooltip.add("Unknown map");
         } else {
            tooltip.add("Scaling at 1:" + (1 << mapData.scale));
            tooltip.add("(Level " + mapData.scale + "/" + 4 + ")");
         }
      }
   }
}
