package net.portalmod.core.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.advancements.criterion.StatePropertiesPredicate.Builder;
import net.minecraft.block.Block;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Items;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.loot.conditions.BlockStateProperty;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.portalmod.common.blocks.ForestCakeBlock;
import net.portalmod.common.blocks.MultiBlock;
import net.portalmod.common.sorted.gel.AbstractGelBlock;
import net.portalmod.core.init.BlockInit;

public class LootTableGen extends LootTableProvider {
   public LootTableGen(DataGenerator generator) {
      super(generator);
   }

   protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
      return ImmutableList.of(Pair.of(PMBlockLootTables::new, LootParameterSets.field_216267_h));
   }

   protected void validate(Map<ResourceLocation, LootTable> p_validate_1_, ValidationTracker p_validate_2_) {
   }

   public static class PMBlockLootTables extends BlockLootTables {
      protected void addTables() {
         for(RegistryObject<Block> block : BlockInit.BLOCKS.getEntries()) {
            this.setLootTable((Block)block.get());
         }

      }

      public static boolean skipLootTable(Block block) {
         return block.func_199767_j() == Items.field_190931_a || block instanceof AbstractGelBlock;
      }

      public void setLootTable(Block block) {
         if (!skipLootTable(block)) {
            if (block instanceof ForestCakeBlock) {
               this.func_218466_b(block);
            } else if (block instanceof DoorBlock) {
               this.func_218507_a(block, func_239829_a_(block));
            } else if (block instanceof SlabBlock) {
               this.func_218507_a(block, func_218513_d(block));
            } else if (block instanceof MultiBlock) {
               this.func_218507_a(block, this.multiBlockCondition((MultiBlock)block));
            } else {
               this.func_218492_c(block);
            }

         }
      }

      public LootTable.Builder multiBlockCondition(MultiBlock block) {
         StatePropertiesPredicate.Builder properties = Builder.func_227191_a_();
         HashMap<Property<?>, Comparable<?>> propertyMap = new HashMap();
         block.addMainBlockProperties(propertyMap);
         propertyMap.forEach((property, comparable) -> properties.func_227194_a_(property, comparable.toString()));
         return LootTable.func_216119_b().func_216040_a((LootPool.Builder)func_218560_a(block, LootPool.func_216096_a().func_216046_a(ConstantRange.func_215835_a(1)).func_216045_a(ItemLootEntry.func_216168_a(block).func_212840_b_(BlockStateProperty.func_215985_a(block).func_227567_a_(properties)))));
      }

      protected Iterable<Block> getKnownBlocks() {
         return (Iterable)BlockInit.BLOCKS.getEntries().stream().map(RegistryObject::get).filter((block) -> !skipLootTable(block)).collect(Collectors.toList());
      }
   }
}
