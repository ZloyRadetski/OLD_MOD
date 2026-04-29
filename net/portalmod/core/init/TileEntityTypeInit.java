package net.portalmod.core.init;

import com.mojang.datafixers.types.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.tileentity.TileEntityType.Builder;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.portalmod.common.sorted.antline.AntlineTileEntity;
import net.portalmod.common.sorted.autoportal.AutoPortalTileEntity;
import net.portalmod.common.sorted.cubedropper.CubeDropperTileEntity;
import net.portalmod.common.sorted.door.ChamberDoorTileEntity;
import net.portalmod.common.sorted.faithplate.FaithPlateTileEntity;
import net.portalmod.common.sorted.fizzler.FizzlerEmitterTileEntity;
import net.portalmod.common.sorted.radio.RadioBlockTileEntity;
import net.portalmod.common.sorted.trigger.TriggerTileEntity;

public class TileEntityTypeInit {
   public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES;
   public static final RegistryObject<TileEntityType<RadioBlockTileEntity>> RADIO;
   public static final RegistryObject<TileEntityType<AntlineTileEntity>> ANTLINE;
   public static final RegistryObject<TileEntityType<FaithPlateTileEntity>> FAITHPLATE;
   public static final RegistryObject<TileEntityType<ChamberDoorTileEntity>> CHAMBER_DOOR;
   public static final RegistryObject<TileEntityType<CubeDropperTileEntity>> CUBE_DROPPER;
   public static final RegistryObject<TileEntityType<FizzlerEmitterTileEntity>> FIZZLER_EMITTER;
   public static final RegistryObject<TileEntityType<TriggerTileEntity>> TRIGGER;
   public static final RegistryObject<TileEntityType<AutoPortalTileEntity>> AUTOPORTAL;

   public static Block[] getBlocks(UnaryOperator<Set<Block>> function) {
      return (Block[])((Set)function.apply(new HashSet())).toArray(new Block[0]);
   }

   public static Set<Block> getRadioBlocks(Set<Block> blocks) {
      blocks.add(BlockInit.RADIO.get());
      return blocks;
   }

   public static Set<Block> getAntlineBlocks(Set<Block> blocks) {
      blocks.add(BlockInit.ANTLINE.get());
      return blocks;
   }

   public static Set<Block> getFaithplateBlocks(Set<Block> blocks) {
      blocks.add(BlockInit.FAITHPLATE.get());
      return blocks;
   }

   public static Set<Block> getChamberDoorBlocks(Set<Block> blocks) {
      blocks.add(BlockInit.CHAMBER_DOOR.get());
      return blocks;
   }

   public static Set<Block> getCubeDropperBlocks(Set<Block> blocks) {
      blocks.add(BlockInit.CUBE_DROPPER.get());
      return blocks;
   }

   public static Set<Block> getFizzlerEmitterBlocks(Set<Block> blocks) {
      blocks.add(BlockInit.FIZZLER_EMITTER.get());
      return blocks;
   }

   public static Set<Block> getTriggerBlocks(Set<Block> blocks) {
      blocks.add(BlockInit.TRIGGER.get());
      return blocks;
   }

   public static Set<Block> getAutoPortalBlocks(Set<Block> blocks) {
      blocks.add(BlockInit.AUTOPORTAL.get());
      return blocks;
   }

   static {
      TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, "portalmod");
      RADIO = TILE_ENTITY_TYPES.register("radio", () -> Builder.func_223042_a(RadioBlockTileEntity::new, getBlocks(TileEntityTypeInit::getRadioBlocks)).func_206865_a((Type)null));
      ANTLINE = TILE_ENTITY_TYPES.register("antline", () -> Builder.func_223042_a(AntlineTileEntity::new, getBlocks(TileEntityTypeInit::getAntlineBlocks)).func_206865_a((Type)null));
      FAITHPLATE = TILE_ENTITY_TYPES.register("faithplate", () -> Builder.func_223042_a(FaithPlateTileEntity::new, getBlocks(TileEntityTypeInit::getFaithplateBlocks)).func_206865_a((Type)null));
      CHAMBER_DOOR = TILE_ENTITY_TYPES.register("chamber_door", () -> Builder.func_223042_a(ChamberDoorTileEntity::new, getBlocks(TileEntityTypeInit::getChamberDoorBlocks)).func_206865_a((Type)null));
      CUBE_DROPPER = TILE_ENTITY_TYPES.register("cube_dropper", () -> Builder.func_223042_a(CubeDropperTileEntity::new, getBlocks(TileEntityTypeInit::getCubeDropperBlocks)).func_206865_a((Type)null));
      FIZZLER_EMITTER = TILE_ENTITY_TYPES.register("fizzler_emitter", () -> Builder.func_223042_a(FizzlerEmitterTileEntity::new, getBlocks(TileEntityTypeInit::getFizzlerEmitterBlocks)).func_206865_a((Type)null));
      TRIGGER = TILE_ENTITY_TYPES.register("trigger", () -> Builder.func_223042_a(TriggerTileEntity::new, getBlocks(TileEntityTypeInit::getTriggerBlocks)).func_206865_a((Type)null));
      AUTOPORTAL = TILE_ENTITY_TYPES.register("autoportal", () -> Builder.func_223042_a(AutoPortalTileEntity::new, getBlocks(TileEntityTypeInit::getAutoPortalBlocks)).func_206865_a((Type)null));
   }
}
