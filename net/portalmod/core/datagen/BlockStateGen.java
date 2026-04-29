package net.portalmod.core.datagen;

import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Direction.Axis;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.portalmod.common.sorted.panel.PanelBlock;
import net.portalmod.common.sorted.panel.PanelState;
import net.portalmod.core.init.BlockInit;

public class BlockStateGen extends BlockStateProvider {
   public BlockStateGen(DataGenerator gen, ExistingFileHelper exFileHelper) {
      super(gen, "portalmod", exFileHelper);
   }

   protected void registerStatesAndModels() {
      this.genPanel((Block)BlockInit.LUNECAST.get(), false);
      this.genPanel((Block)BlockInit.ARBORED_LUNECAST.get(), false);
      this.genPanel((Block)BlockInit.ERODED_LUNECAST.get(), false);
      this.genPanel((Block)BlockInit.FRACTURED_LUNECAST.get(), false);
      this.genPanel((Block)BlockInit.VINTAGE_LUNECAST.get(), false);
      this.genPanel((Block)BlockInit.BLACKPLATE.get(), true);
      this.genPanel((Block)BlockInit.ARBORED_BLACKPLATE.get(), "", "", false, true);
      this.genPanel((Block)BlockInit.ARBORED_BLACKPLATE.get(), "_foliage", "_tint", false, true);
      this.genPanel((Block)BlockInit.ERODED_BLACKPLATE.get(), true);
      this.genPanel((Block)BlockInit.FRACTURED_BLACKPLATE.get(), true);
      this.genPanel((Block)BlockInit.VINTAGE_BLACKPLATE.get(), true);
      this.genPanelSlab((Block)BlockInit.LUNECAST_SLAB.get(), "lunecast");
      this.genPanelSlab((Block)BlockInit.ARBORED_LUNECAST_SLAB.get(), "arbored_lunecast");
      this.genPanelSlab((Block)BlockInit.ERODED_LUNECAST_SLAB.get(), "eroded_lunecast");
      this.genPanelSlab((Block)BlockInit.FRACTURED_LUNECAST_SLAB.get(), "fractured_lunecast");
      this.genPanelSlab((Block)BlockInit.VINTAGE_LUNECAST_SLAB.get(), "vintage_lunecast");
      this.genPanelSlab((Block)BlockInit.BLACKPLATE_SLAB.get(), "blackplate");
      this.genPanelSlab((Block)BlockInit.ERODED_BLACKPLATE_SLAB.get(), "eroded_blackplate");
      this.genPanelSlab((Block)BlockInit.FRACTURED_BLACKPLATE_SLAB.get(), "fractured_blackplate");
      this.genPanelSlab((Block)BlockInit.VINTAGE_BLACKPLATE_SLAB.get(), "vintage_blackplate");
      this.genPanelStairs((Block)BlockInit.LUNECAST_STAIRS.get(), "lunecast");
      this.genPanelStairs((Block)BlockInit.ARBORED_LUNECAST_STAIRS.get(), "arbored_lunecast");
      this.genPanelStairs((Block)BlockInit.ERODED_LUNECAST_STAIRS.get(), "eroded_lunecast");
      this.genPanelStairs((Block)BlockInit.FRACTURED_LUNECAST_STAIRS.get(), "fractured_lunecast");
      this.genPanelStairsFull((Block)BlockInit.VINTAGE_LUNECAST_STAIRS.get(), "vintage_lunecast");
      this.genPanelStairs((Block)BlockInit.BLACKPLATE_STAIRS.get(), "blackplate");
      this.genPanelStairs((Block)BlockInit.ERODED_BLACKPLATE_STAIRS.get(), "eroded_blackplate");
      this.genPanelStairs((Block)BlockInit.FRACTURED_BLACKPLATE_STAIRS.get(), "fractured_blackplate");
      this.genPanelStairs((Block)BlockInit.VINTAGE_BLACKPLATE_STAIRS.get(), "vintage_blackplate");
   }

   public void genPanel(Block block, boolean blackplateYouBe) {
      this.genPanel(block, "", "", true, blackplateYouBe);
   }

   public void genPanel(Block block, String suffix, String parentSuffix, boolean genBlockStateFile, boolean blackplateYouBe) {
      String name = block.getRegistryName().func_110623_a();
      String parentPrefix = Objects.equals(parentSuffix, "") ? "minecraft:" : "portalmod:";
      ModelFile singleModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + suffix, parentPrefix + "block/cube_bottom_top" + parentSuffix)).texture("side", blockPath(name + suffix))).texture("top", blockPath(name + "_top" + suffix))).texture("bottom", blockPath(name + "_bottom" + suffix));
      ModelFile topModel = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_top" + suffix, "portalmod:block/cube_part_top" + parentSuffix)).texture("side", blockPath(name + "_panel_top" + suffix))).texture("top", blockPath(name + "_top" + suffix));
      ModelFile bottomModel = ((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_bottom" + suffix, "portalmod:block/cube_part_bottom" + parentSuffix)).texture("side", blockPath(name + "_panel_bottom" + suffix))).texture("bottom", blockPath(name + "_bottom" + suffix));
      ModelFile cornerBottomModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_corner_bottom" + suffix, "portalmod:block/cube_corner_bottom" + parentSuffix)).texture("bottom", blockPath(name + "_bottom" + suffix))).texture("back", blockPath(name + "_panel_bottom" + suffix))).texture("left", blockPath(name + "_panel_bottom_left" + suffix))).texture("right", blockPath(name + "_panel_bottom_right" + suffix));
      ModelFile cornerTopModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_corner_top" + suffix, "portalmod:block/cube_corner_top" + parentSuffix)).texture("top", blockPath(name + "_top" + suffix))).texture("back", blockPath(name + "_panel_top" + suffix))).texture("left", blockPath(name + "_panel_top_left" + suffix))).texture("right", blockPath(name + "_panel_top_right" + suffix));
      if (blackplateYouBe) {
         ModelFile floorBottomLeftModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_bottom_left" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_bottom_left" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_bottom_right" + suffix))).texture("side", blockPath(name + suffix));
         ModelFile floorBottomRightModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_bottom_right" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_bottom_right" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_bottom_left" + suffix))).texture("side", blockPath(name + suffix));
         ModelFile floorTopLeftModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_top_left" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_top_left" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_top_right" + suffix))).texture("side", blockPath(name + suffix));
         ModelFile floorTopRightModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_top_right" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_top_right" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_top_left" + suffix))).texture("side", blockPath(name + suffix));
         if (!genBlockStateFile) {
            return;
         }

         this.getVariantBuilder(block).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.FLOOR_BOTTOM_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorBottomLeftModel, 0, 180, true)}).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.FLOOR_BOTTOM_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorBottomRightModel, 0, 90, true)}).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.FLOOR_TOP_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorTopLeftModel, 0, 270, true)}).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.FLOOR_TOP_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorTopRightModel, 0, 0, true)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.FLOOR_BOTTOM_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorBottomLeftModel, 0, 180, true)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.FLOOR_BOTTOM_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorBottomRightModel, 0, 90, true)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.FLOOR_TOP_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorTopLeftModel, 0, 270, true)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.FLOOR_TOP_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorTopRightModel, 0, 0, true)});
      }

      if (!blackplateYouBe) {
         ModelFile floorXBottomLeftModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_x_bottom_left" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_x_bottom_left" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_x_top_left" + suffix))).texture("side", blockPath(name + suffix));
         ModelFile floorZBottomLeftModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_z_bottom_left" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_z_bottom_left" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_z_top_left" + suffix))).texture("side", blockPath(name + suffix));
         ModelFile floorXBottomRightModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_x_bottom_right" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_x_bottom_right" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_x_top_right" + suffix))).texture("side", blockPath(name + suffix));
         ModelFile floorZBottomRightModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_z_bottom_right" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_z_bottom_right" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_z_top_right" + suffix))).texture("side", blockPath(name + suffix));
         ModelFile floorXTopLeftModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_x_top_left" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_x_top_left" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_x_bottom_left" + suffix))).texture("side", blockPath(name + suffix));
         ModelFile floorZTopLeftModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_z_top_left" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_z_top_left" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_z_bottom_left" + suffix))).texture("side", blockPath(name + suffix));
         ModelFile floorXTopRightModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_x_top_right" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_x_top_right" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_x_bottom_right" + suffix))).texture("side", blockPath(name + suffix));
         ModelFile floorZTopRightModel = ((BlockModelBuilder)((BlockModelBuilder)((BlockModelBuilder)this.models().withExistingParent(name + "_panel_floor_z_top_right" + suffix, "portalmod:block/cube_corner_floor" + parentSuffix)).texture("top", blockPath(name + "_panel_floor_z_top_right" + suffix))).texture("bottom", blockPath(name + "_panel_ceiling_z_bottom_right" + suffix))).texture("side", blockPath(name + suffix));
         if (!genBlockStateFile) {
            return;
         }

         this.getVariantBuilder(block).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.FLOOR_BOTTOM_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorXBottomLeftModel, 0, 180, true)}).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.FLOOR_BOTTOM_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorXBottomRightModel, 0, 90, true)}).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.FLOOR_TOP_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorXTopLeftModel, 0, 270, true)}).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.FLOOR_TOP_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorXTopRightModel, 0, 0, true)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.FLOOR_BOTTOM_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorZBottomLeftModel, 0, 180, true)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.FLOOR_BOTTOM_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorZBottomRightModel, 0, 90, true)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.FLOOR_TOP_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorZTopLeftModel, 0, 270, true)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.FLOOR_TOP_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(floorZTopRightModel, 0, 0, true)});
      }

      this.getVariantBuilder(block).partialState().with(PanelBlock.STATE, PanelState.SINGLE).addModels(new ConfiguredModel[]{new ConfiguredModel(singleModel)}).partialState().with(PanelBlock.STATE, PanelState.TOP).addModels(new ConfiguredModel[]{new ConfiguredModel(topModel)}).partialState().with(PanelBlock.STATE, PanelState.BOTTOM).addModels(new ConfiguredModel[]{new ConfiguredModel(bottomModel)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.BOTTOM_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(cornerBottomModel)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.BOTTOM_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(cornerBottomModel, 0, 180, true)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.TOP_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(cornerTopModel)}).partialState().with(PanelBlock.AXIS, Axis.Z).with(PanelBlock.STATE, PanelState.TOP_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(cornerTopModel, 0, 180, true)}).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.BOTTOM_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(cornerBottomModel, 0, 90, true)}).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.BOTTOM_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(cornerBottomModel, 0, 270, true)}).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.TOP_LEFT).addModels(new ConfiguredModel[]{new ConfiguredModel(cornerTopModel, 0, 90, true)}).partialState().with(PanelBlock.AXIS, Axis.X).with(PanelBlock.STATE, PanelState.TOP_RIGHT).addModels(new ConfiguredModel[]{new ConfiguredModel(cornerTopModel, 0, 270, true)});
   }

   public void genPanelSlab(Block block, String name) {
      this.panelSlabBlock((SlabBlock)block, name, blockPath(name + "_tiles"), blockPath(name + "_slab_bottom"), blockPath(name + "_slab_top"));
   }

   public void panelSlabBlock(SlabBlock block, String name, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
      this.slabBlock(block, this.models().slab(name + "_slab", side, bottom, top), this.models().slabTop(name + "_slab_top", side, bottom, top), this.models().cubeBottomTop(name + "_slab_double", side, bottom, top));
   }

   public void genPanelStairs(Block block, String name) {
      this.stairsBlock((StairsBlock)block, blockPath(name + "_tiles"), blockPath(name + "_bottom"), blockPath(name + "_top"));
   }

   public void genPanelStairsFull(Block block, String name) {
      this.stairsBlock((StairsBlock)block, blockPath(name + "_tiles"));
   }

   @Nonnull
   public static ResourceLocation blockPath(String name) {
      return new ResourceLocation("portalmod", "block/" + name);
   }
}
