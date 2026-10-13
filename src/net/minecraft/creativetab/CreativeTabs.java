package net.minecraft.creativetab;

import java.util.List;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class CreativeTabs {
   public static final CreativeTabs[] creativeTabArray = new CreativeTabs[12];
   public static final CreativeTabs tabBlock = new CreativeTabs(0, "buildingBlocks") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.brick_block);
      }
   };
   public static final CreativeTabs tabDecorations = new CreativeTabs(1, "decorations") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.double_plant);
      }

      @Override
      public int getIconItemDamage() {
         return BlockDoublePlant.EnumPlantType.PAEONIA.getMeta();
      }
   };
   public static final CreativeTabs tabRedstone = new CreativeTabs(2, "redstone") {
      @Override
      public Item getTabIconItem() {
         return Items.redstone;
      }
   };
   public static final CreativeTabs tabTransport = new CreativeTabs(3, "transportation") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.golden_rail);
      }
   };
   public static final CreativeTabs tabMisc = (new CreativeTabs(4, "misc") {
      @Override
      public Item getTabIconItem() {
         return Items.lava_bucket;
      }
   }).setRelevantEnchantmentTypes(EnumEnchantmentType.ALL);
   public static final CreativeTabs tabAllSearch = (new CreativeTabs(5, "search") {
      @Override
      public Item getTabIconItem() {
         return Items.compass;
      }
   }).setBackgroundImageName("item_search.png");
   public static final CreativeTabs tabFood = new CreativeTabs(6, "food") {
      @Override
      public Item getTabIconItem() {
         return Items.apple;
      }
   };
   public static final CreativeTabs tabTools = (new CreativeTabs(7, "tools") {
      @Override
      public Item getTabIconItem() {
         return Items.iron_axe;
      }
   }).setRelevantEnchantmentTypes(EnumEnchantmentType.DIGGER, EnumEnchantmentType.FISHING_ROD, EnumEnchantmentType.BREAKABLE);
   public static final CreativeTabs tabCombat = (new CreativeTabs(8, "combat") {
         @Override
         public Item getTabIconItem() {
            return Items.golden_sword;
         }
      })
      .setRelevantEnchantmentTypes(
         EnumEnchantmentType.ARMOR,
         EnumEnchantmentType.ARMOR_FEET,
         EnumEnchantmentType.ARMOR_HEAD,
         EnumEnchantmentType.ARMOR_LEGS,
         EnumEnchantmentType.ARMOR_TORSO,
         EnumEnchantmentType.BOW,
         EnumEnchantmentType.WEAPON
      );
   public static final CreativeTabs tabBrewing = new CreativeTabs(9, "brewing") {
      @Override
      public Item getTabIconItem() {
         return Items.potionitem;
      }
   };
   public static final CreativeTabs tabMaterials = new CreativeTabs(10, "materials") {
      @Override
      public Item getTabIconItem() {
         return Items.stick;
      }
   };
   public static final CreativeTabs tabInventory = (new CreativeTabs(11, "inventory") {
      @Override
      public Item getTabIconItem() {
         return Item.getItemFromBlock(Blocks.chest);
      }
   }).setBackgroundImageName("inventory.png").P().W();
   private final int tabIndex;
   private final String tabLabel;
   private String theTexture = "items.png";
   private boolean hasScrollbar = true;
   private boolean drawTitle = true;
   private EnumEnchantmentType[] enchantmentTypes;
   private ItemStack iconItemStack;
   private static final long a = 13965613623014L;

   public CreativeTabs(int var1, String var2) {
      this.tabIndex = var1;
      this.tabLabel = var2;
      creativeTabArray[var1] = this;
   }

   public int getTabIndex() {
      return this.tabIndex;
   }

   public String getTabLabel() {
      return this.tabLabel;
   }

   public String getTranslatedTabLabel() {
      return "itemGroup." + this.getTabLabel();
   }

   public ItemStack getIconItemStack() {
      if (this.iconItemStack == null) {
         this.iconItemStack = new ItemStack(this.getTabIconItem(), 1, this.getIconItemDamage());
      }

      return this.iconItemStack;
   }

   public abstract Item getTabIconItem();

   public int getIconItemDamage() {
      return 0;
   }

   public String getBackgroundImageName() {
      return this.theTexture;
   }

   public CreativeTabs setBackgroundImageName(String var1) {
      this.theTexture = var1;
      return this;
   }

   public boolean drawInForegroundOfTab() {
      return this.drawTitle;
   }

   public CreativeTabs W() {
      this.drawTitle = false;
      return this;
   }

   public boolean shouldHidePlayerInventory() {
      return this.hasScrollbar;
   }

   public CreativeTabs P() {
      this.hasScrollbar = false;
      return this;
   }

   public int getTabColumn() {
      return this.tabIndex % 6;
   }

   public boolean isTabInFirstRow() {
      return this.tabIndex < 6;
   }

   public EnumEnchantmentType[] getRelevantEnchantmentTypes() {
      return this.enchantmentTypes;
   }

   public CreativeTabs setRelevantEnchantmentTypes(EnumEnchantmentType... var1) {
      this.enchantmentTypes = var1;
      return this;
   }

   public boolean hasRelevantEnchantmentType(EnumEnchantmentType var1) {
      if (this.enchantmentTypes == null) {
         return false;
      }

      for (EnumEnchantmentType var8 : this.enchantmentTypes) {
         if (var8 == var1) {
            return true;
         }
      }

      return false;
   }

   public void displayAllReleventItems(List<ItemStack> var1) {
      for (Item var6 : Item.itemRegistry) {
         if (var6 != null && var6.getCreativeTab() == this) {
            var6.getSubItems(var6, this, var1);
         }
      }

      if (this.getRelevantEnchantmentTypes() != null) {
         this.addEnchantmentBooksToList(var1, this.getRelevantEnchantmentTypes());
      }
   }

   public void addEnchantmentBooksToList(List<ItemStack> var1, EnumEnchantmentType... var2) {
      for (Enchantment var10 : Enchantment.enchantmentsBookList) {
         if (var10 != null && var10.type != null) {
            boolean var11 = false;

            for (int var12 = 0; var12 < var2.length && !var11; var12++) {
               if (var10.type == var2[var12]) {
                  var11 = true;
               }
            }

            if (var11) {
               var1.add(Items.enchanted_book.getEnchantedItemStack(new EnchantmentData(var10, var10.getMaxLevel())));
            }
         }
      }
   }
}
