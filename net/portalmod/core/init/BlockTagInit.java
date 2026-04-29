package net.portalmod.core.init;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class BlockTagInit {
   public static final Tags.IOptionalNamedTag<Block> PORTALABLE = blockTag("portal/portalable");
   public static final Tags.IOptionalNamedTag<Block> UNPORTALABLE = blockTag("portal/unportalable");
   public static final Tags.IOptionalNamedTag<Block> PORTAL_TRANSPARENT = blockTag("portal/portal_transparent");
   public static final Tags.IOptionalNamedTag<Block> PORTAL_NONBLOCKING = blockTag("portal/portal_nonblocking");
   public static final Tags.IOptionalNamedTag<Block> PORTAL_INHERITING = blockTag("portal/portal_inheriting");
   public static final Tags.IOptionalNamedTag<Block> PORTALABLE_QUALITY = blockTag("portal/portalable_quality");
   public static final Tags.IOptionalNamedTag<Block> BLOCK_TRANSPARENT = blockTag("block_transparent");
   public static final Tags.IOptionalNamedTag<Block> BLOCK_PERMEABLE = blockTag("block_permeable");

   private BlockTagInit() {
   }

   public static void init() {
   }

   public static Tags.IOptionalNamedTag<Block> blockTag(String name) {
      return BlockTags.createOptional(new ResourceLocation("portalmod", name));
   }
}
