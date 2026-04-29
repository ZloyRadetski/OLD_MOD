package net.portalmod.common.sorted.portalgun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.portalmod.common.entities.TestElementEntity;
import net.portalmod.common.sorted.fizzler.FizzlerEmitterBlock;
import net.portalmod.common.sorted.fizzler.FizzlerFieldBlock;
import net.portalmod.common.sorted.portal.PortalColors;
import net.portalmod.common.sorted.portal.PortalEnd;
import net.portalmod.common.sorted.portal.PortalEntity;
import net.portalmod.common.sorted.portal.PortalManager;
import net.portalmod.common.sorted.portal.PortalPair;
import net.portalmod.common.sorted.portal.PortalPlacer;
import net.portalmod.common.triggers.CodeBoundTrigger;
import net.portalmod.core.init.BlockInit;
import net.portalmod.core.init.BlockTagInit;
import net.portalmod.core.init.CriteriaTriggerInit;
import net.portalmod.core.init.GameRuleInit;
import net.portalmod.core.init.PacketInit;
import net.portalmod.core.init.SoundInit;
import net.portalmod.core.init.StatsInit;
import net.portalmod.core.math.AABBUtil;
import net.portalmod.core.math.Vec3;
import net.portalmod.core.util.Colour;
import net.portalmod.core.util.ModUtil;

public class PortalGun extends Item {
   public String defaultLeftColor;
   public String defaultRightColor;
   public String defaultAccentColor;
   private static final HashMap<UUID, PortalGun> BY_UUID = new HashMap();

   public PortalGun(Item.Properties properties) {
      this(properties, "blue", "orange", "none");
   }

   public PortalGun(Item.Properties properties, String defaultLeftColor, String defaultRightColor, String defaultAccentColor) {
      super(properties);
      this.defaultLeftColor = defaultLeftColor;
      this.defaultRightColor = defaultRightColor;
      this.defaultAccentColor = defaultAccentColor;
   }

   public static void updateHolding(ItemStack itemStack, PlayerEntity player) {
      if (itemStack.func_77942_o()) {
         CompoundNBT nbt = itemStack.func_77978_p();
         boolean isInMainHand = player.func_184586_b(Hand.MAIN_HAND) == itemStack;
         boolean isInOffHand = player.func_184586_b(Hand.OFF_HAND) == itemStack;
         boolean isInHand = isInMainHand || isInOffHand && !(player.func_184614_ca().func_77973_b() instanceof PortalGun);
         boolean wasHolding = nbt.func_74764_b("Holding") && nbt.func_74767_n("Holding");
         boolean isHolding = isInHand && player.func_184188_bt().stream().anyMatch((entity) -> entity instanceof TestElementEntity);
         if (isHolding && !wasHolding) {
            pickCube(player, itemStack);
         }

         if (!isHolding && wasHolding) {
            dropCube(player, itemStack);
         }

      }
   }

   public static void setHolding(ItemStack itemStack, boolean holding) {
      CompoundNBT nbt = itemStack.func_196082_o();
      nbt.func_74757_a("Holding", holding);
   }

   public static void pickCube(PlayerEntity player, ItemStack gun) {
      setHolding(gun, true);
      player.field_70170_p.func_217384_a(player, player, (SoundEvent)SoundInit.PORTALGUN_LIFT.get(), SoundCategory.PLAYERS, 1.0F, ModUtil.randomSoundPitch());
      Optional<UUID> uuid = getUUID(gun);
      if (player.field_70170_p instanceof ServerWorld && uuid.isPresent()) {
         PacketInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SPortalGunAnimationPacket((UUID)uuid.get(), PortalGunAnimation.LIFT));
      } else {
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PortalGunGrabSoundClient.handlePacket(player, true));
      }
   }

   public static void dropCube(PlayerEntity player, ItemStack gun) {
      setHolding(gun, false);
      player.field_70170_p.func_217384_a(player, player, (SoundEvent)SoundInit.PORTALGUN_DROP.get(), SoundCategory.PLAYERS, 1.0F, ModUtil.randomSoundPitch());
      Optional<UUID> uuid = getUUID(gun);
      if (player.field_70170_p instanceof ServerWorld && uuid.isPresent()) {
         PacketInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SPortalGunAnimationPacket((UUID)uuid.get(), PortalGunAnimation.DROP));
      } else {
         DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PortalGunGrabSoundClient.handlePacket(player, false));
      }
   }

   public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
      dropCube(player, item);
      return super.onDroppedByPlayer(item, player);
   }

   public static BlockRayTraceResult customClip(World level, RayTraceContext context) {
      return (BlockRayTraceResult)IBlockReader.func_217300_a(context, (ctx, pos) -> {
         BlockState blockstate = level.func_180495_p(pos);
         if (blockstate.func_235714_a_(BlockTagInit.PORTAL_TRANSPARENT)) {
            return null;
         } else {
            FluidState fluidstate = level.func_204610_c(pos);
            Vector3d vector3d = ctx.func_222253_b();
            Vector3d vector3d1 = ctx.func_222250_a();
            VoxelShape voxelshape = blockstate.func_177230_c().func_220053_a(blockstate, level, pos, ISelectionContext.func_216377_a());
            Block block = blockstate.func_177230_c();
            if (block == BlockInit.FIZZLER_EMITTER.get()) {
               if ((Boolean)blockstate.func_177229_b(FizzlerEmitterBlock.ACTIVE)) {
                  voxelshape = ((FizzlerEmitterBlock)block).getFieldShape(blockstate);
               }
            } else if (block == BlockInit.FIZZLER_FIELD.get()) {
               voxelshape = ((FizzlerFieldBlock)block).getFieldShape(blockstate);
            }

            BlockRayTraceResult blockraytraceresult = level.func_217296_a(vector3d, vector3d1, pos, voxelshape, blockstate);
            VoxelShape voxelshape1 = ctx.func_222252_a(fluidstate, level, pos);
            BlockRayTraceResult blockraytraceresult1 = voxelshape1.func_212433_a(vector3d, vector3d1, pos);
            double d0 = blockraytraceresult == null ? Double.MAX_VALUE : ctx.func_222253_b().func_72436_e(blockraytraceresult.func_216347_e());
            double d1 = blockraytraceresult1 == null ? Double.MAX_VALUE : ctx.func_222253_b().func_72436_e(blockraytraceresult1.func_216347_e());
            return d0 <= d1 ? blockraytraceresult : blockraytraceresult1;
         }
      }, (ctx) -> {
         Vector3d vector3d = ctx.func_222253_b().func_178788_d(ctx.func_222250_a());
         return BlockRayTraceResult.func_216352_a(ctx.func_222250_a(), Direction.func_210769_a(vector3d.field_72450_a, vector3d.field_72448_b, vector3d.field_72449_c), new BlockPos(ctx.func_222250_a()));
      });
   }

   private static void triggerMoonAdvancement(World level, ServerPlayerEntity player) {
      double timeOfDay = (double)(level.func_242415_f(0.0F) + 0.25F);
      double angle = (timeOfDay - (double)((int)timeOfDay)) * (double)2.0F * Math.PI;
      Vector3d moonVector = new Vector3d(Math.cos(angle), Math.sin(angle), (double)0.0F);
      boolean lookingUp = player.func_70040_Z().func_72430_b(new Vector3d((double)0.0F, (double)1.0F, (double)0.0F)) > (double)0.0F;
      boolean lookingAtTheMoon = player.func_70040_Z().func_72430_b(moonVector) <= -0.997;
      if (lookingUp && lookingAtTheMoon) {
         ((CodeBoundTrigger)CriteriaTriggerInit.SHOOT_MOON.get()).trigger(player);
      }

   }

   private static void triggerPortalAdvancements(World level, ServerPlayerEntity player, PortalEntity portal, double distance) {
      boolean allQualityBlocks = portal.getBlocksBehind().stream().allMatch((pos) -> level.func_180495_p(pos).func_235714_a_(BlockTagInit.PORTALABLE_QUALITY));
      ((CodeBoundTrigger)CriteriaTriggerInit.PLACE_PORTALS.get()).trigger(player);
      if (allQualityBlocks) {
         ((CodeBoundTrigger)CriteriaTriggerInit.PORTAL_SURFACE.get()).trigger(player);
      }

      if (distance > (double)100.0F) {
         ((CodeBoundTrigger)CriteriaTriggerInit.SHOOT_PORTAL_FAR.get()).trigger(player);
      }

      player.func_195066_a(StatsInit.PORTALS_SHOT);
   }

   public static void placePortal(PlayerEntity player, World level, PortalEnd end, ItemStack gun, BlockRayTraceResult ray) {
      if (!player.func_175149_v() && !level.field_72995_K) {
         Optional<UUID> uuid = getUUID(gun);
         if (uuid.isPresent()) {
            CompoundNBT nbt = gun.func_196082_o();
            boolean isPrimary = end == PortalEnd.PRIMARY;
            if (!nbt.func_74764_b("Locked") || !nbt.func_74779_i("Locked").equals(isPrimary ? "Left" : "Right")) {
               PacketInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SPortalGunAnimationPacket((UUID)uuid.get(), PortalGunAnimation.SHOOT));
               level.func_184148_a((PlayerEntity)null, player.func_213303_ch().field_72450_a, player.func_213303_ch().field_72448_b, player.func_213303_ch().field_72449_c, Objects.equals(end.func_176610_l(), "primary") ? (SoundEvent)SoundInit.PORTALGUN_FIRE_PRIMARY.get() : (SoundEvent)SoundInit.PORTALGUN_FIRE_SECONDARY.get(), SoundCategory.PLAYERS, 1.0F, ModUtil.randomSoundPitch());
               if (ray.func_216346_c() == Type.MISS) {
                  triggerMoonAdvancement(level, (ServerPlayerEntity)player);
               } else {
                  Vec3 position = new Vec3(ray.func_216347_e());
                  Direction face = ray.func_216354_b();
                  Direction up = face.func_176740_k().func_176722_c() ? Direction.UP : player.func_174811_aO();
                  String hue;
                  if (nbt.func_74764_b(isPrimary ? "LeftColor" : "RightColor")) {
                     hue = nbt.func_74779_i(isPrimary ? "LeftColor" : "RightColor");
                  } else {
                     hue = "blue";
                  }

                  boolean inFizzler = AABBUtil.getBlocksWithin(player.func_174813_aQ()).stream().anyMatch((pos) -> {
                     BlockState state = level.func_180495_p(pos);
                     VoxelShape voxelshape;
                     if (state.func_177230_c() == BlockInit.FIZZLER_EMITTER.get()) {
                        voxelshape = ((FizzlerEmitterBlock)BlockInit.FIZZLER_EMITTER.get()).getFieldShape(state);
                     } else {
                        if (state.func_177230_c() != BlockInit.FIZZLER_FIELD.get()) {
                           return false;
                        }

                        voxelshape = ((FizzlerFieldBlock)BlockInit.FIZZLER_FIELD.get()).getFieldShape(state);
                     }

                     VoxelShape movedBlockShape = voxelshape.func_197751_a((double)pos.func_177958_n(), (double)pos.func_177956_o(), (double)pos.func_177952_p());
                     VoxelShape entityShape = VoxelShapes.func_197881_a(player.func_174813_aQ());
                     return VoxelShapes.func_197879_c(movedBlockShape, entityShape, IBooleanFunction.field_223238_i_);
                  });
                  double distance = ray.func_216347_e().func_178788_d(player.func_174824_e(0.0F)).func_72433_c();
                  int ticks = (int)Math.ceil(distance / (double)2.0F);
                  Consumer<PortalEntity> onPlace = (placedPortal) -> {
                     if (placedPortal == null) {
                        level.func_184148_a((PlayerEntity)null, position.x, position.y, position.z, (SoundEvent)SoundInit.PORTALGUN_MISS.get(), SoundCategory.PLAYERS, 1.0F, ModUtil.randomSlightSoundPitch());
                        SimpleChannel var10000 = PacketInit.INSTANCE;
                        PacketDistributor var10001 = PacketDistributor.DIMENSION;
                        level.getClass();
                        var10000.send(var10001.with(level::func_234923_W_), new SPortalGunFailShotPacket(position, new Vec3(face), new Vec3(up), hue));
                     } else {
                        triggerPortalAdvancements(level, (ServerPlayerEntity)player, placedPortal, distance);
                        gun.func_196082_o().func_74768_a("LastPortal", end == PortalEnd.PRIMARY ? -1 : 1);
                        player.func_184614_ca().func_196082_o().func_74774_a("color", (byte)end.ordinal());
                     }
                  };
                  if (!inFizzler) {
                     boolean overwriteForeign = level.func_82736_K().func_223586_b(GameRuleInit.ALLOW_PORTAL_OVERWRITE);
                     if (level.func_82736_K().func_223586_b(GameRuleInit.PORTAL_SLOWSHOT)) {
                        PortalManager.getInstance().schedulePlacement(level, end, hue, (UUID)uuid.get(), position.clone(), face, up, false, overwriteForeign, Direction.func_196054_a(player), (ServerPlayerEntity)player, (long)ticks, onPlace);
                     } else {
                        PortalEntity portal = PortalPlacer.placePortal(level, end, hue, (UUID)uuid.get(), position.clone(), face, up, false, overwriteForeign, Direction.func_196054_a(player), (ServerPlayerEntity)player);
                        onPlace.accept(portal);
                     }
                  } else {
                     level.func_184148_a((PlayerEntity)null, position.x, position.y, position.z, (SoundEvent)SoundInit.PORTALGUN_MISS.get(), SoundCategory.PLAYERS, 1.0F, ModUtil.randomSlightSoundPitch());
                     SimpleChannel var10000 = PacketInit.INSTANCE;
                     PacketDistributor var10001 = PacketDistributor.DIMENSION;
                     level.getClass();
                     var10000.send(var10001.with(level::func_234923_W_), new SPortalGunFailShotPacket(position, new Vec3(face), new Vec3(up), hue));
                  }

               }
            }
         }
      }
   }

   public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
      Optional<UUID> newUUID = getUUID(newStack);
      Optional<UUID> oldUUID = getUUID(oldStack);
      return slotChanged || oldUUID.isPresent() && newUUID.isPresent() && !oldUUID.equals(newUUID);
   }

   public static int getColorOverride(ItemStack itemStack, World level, LivingEntity entity) {
      CompoundNBT nbt = itemStack.func_196082_o();
      if (!nbt.func_74764_b("color")) {
         return 0;
      } else {
         byte color = nbt.func_74771_c("color");
         return color != 1 && color != 2 ? 0 : 1;
      }
   }

   public void func_77663_a(ItemStack itemStack, World level, Entity entity, int i, boolean b) {
      super.func_77663_a(itemStack, level, entity, i, b);
      if (entity instanceof PlayerEntity) {
         updateHolding(itemStack, (PlayerEntity)entity);
      }

      if (!level.field_72995_K) {
         addUUID(itemStack);
         Optional<UUID> uuid = getUUID(itemStack);
         CompoundNBT nbt = itemStack.func_196082_o();
         boolean hasBlue = uuid.isPresent() && PortalManager.getInstance().has((UUID)uuid.get(), PortalEnd.PRIMARY);
         boolean hasOrange = uuid.isPresent() && PortalManager.getInstance().has((UUID)uuid.get(), PortalEnd.SECONDARY);
         if (!nbt.func_74764_b("primary") || nbt.func_74767_n("primary") != hasBlue) {
            nbt.func_74757_a("primary", hasBlue);
         }

         if (!nbt.func_74764_b("secondary") || nbt.func_74767_n("secondary") != hasOrange) {
            nbt.func_74757_a("secondary", hasOrange);
         }

         this.addDefaultNbt(nbt);
      }
   }

   public void addDefaultNbt(CompoundNBT nbt) {
      addDefaultNbt(nbt, this.defaultLeftColor, this.defaultRightColor, this.defaultAccentColor);
   }

   public static void addDefaultNbt(CompoundNBT nbt, String leftColor, String rightColor, String accentColor) {
      if (!nbt.func_74764_b("LeftColor")) {
         nbt.func_74778_a("LeftColor", leftColor);
      }

      if (!nbt.func_74764_b("RightColor")) {
         nbt.func_74778_a("RightColor", rightColor);
      }

      if (!nbt.func_74764_b("LastPortal")) {
         nbt.func_74768_a("LastPortal", 0);
      }

      if (!nbt.func_74764_b("AccentColor")) {
         nbt.func_74778_a("AccentColor", accentColor);
      }

      if (!nbt.func_74764_b("Locked")) {
         nbt.func_74778_a("Locked", "None");
      }

   }

   public void func_150895_a(ItemGroup itemGroup, NonNullList<ItemStack> itemStacks) {
      if (this.func_194125_a(itemGroup)) {
         itemStacks.add(new ItemStack(this));
         itemStacks.add(modifyColors(new ItemStack(this), "light_blue", "purple", "light_blue"));
         itemStacks.add(modifyColors(new ItemStack(this), "yellow", "red", "orange"));
      }

   }

   public static ItemStack modifyColors(ItemStack itemStack, String leftColor, String rightColor, String accentColor) {
      addDefaultNbt(itemStack.func_196082_o(), leftColor, rightColor, accentColor);
      return itemStack;
   }

   public static void addUUID(ItemStack itemStack) {
      CompoundNBT nbt = itemStack.func_196082_o();
      if (!nbt.func_74764_b("gunUUID")) {
         nbt.func_186854_a("gunUUID", UUID.randomUUID());
      }

   }

   public static void removeUUID(ItemStack itemStack) {
      CompoundNBT nbt = itemStack.func_196082_o();
      nbt.func_82580_o("gunUUID");
   }

   public static Optional<UUID> getUUID(ItemStack itemStack) {
      if (!itemStack.func_77942_o()) {
         return Optional.empty();
      } else {
         CompoundNBT nbt = itemStack.func_77978_p();
         if (nbt == null) {
            return Optional.empty();
         } else if (nbt.func_74764_b("gunUUID") && itemStack.func_77973_b() instanceof PortalGun) {
            UUID uuid = nbt.func_186857_a("gunUUID");
            BY_UUID.put(uuid, (PortalGun)itemStack.func_77973_b());
            return Optional.of(uuid);
         } else {
            return Optional.empty();
         }
      }
   }

   public static void onDuplicate(ItemStack itemStack) {
      removeUUID(itemStack);
      addUUID(itemStack);
   }

   public static Colour getLeftColour(CompoundNBT nbt) {
      return PortalColors.getColour(getLeftDyeColour(nbt).func_176762_d());
   }

   public static DyeColor getLeftDyeColour(CompoundNBT nbt) {
      DyeColor color = DyeColor.BLUE;
      if (nbt.func_74779_i("Locked").equals("Left")) {
         return getRightDyeColour(nbt);
      } else {
         if (nbt.func_74764_b("LeftColor")) {
            try {
               color = DyeColor.func_204271_a(nbt.func_74779_i("LeftColor"), color);
            } catch (NullPointerException var3) {
            }
         }

         return color;
      }
   }

   public static Colour getRightColour(CompoundNBT nbt) {
      return PortalColors.getColour(getRightDyeColour(nbt).func_176762_d());
   }

   public static DyeColor getRightDyeColour(CompoundNBT nbt) {
      DyeColor color = DyeColor.ORANGE;
      if (nbt.func_74779_i("Locked").equals("Right")) {
         return getLeftDyeColour(nbt);
      } else {
         if (nbt.func_74764_b("RightColor")) {
            try {
               color = DyeColor.func_204271_a(nbt.func_74779_i("RightColor"), color);
            } catch (NullPointerException var3) {
            }
         }

         return color;
      }
   }

   public static PortalGunModel getModel(UUID gunUUID) {
      return PortalGunModelManager.getInstance().getModel(gunUUID);
   }

   public static Colour getAccentColour(CompoundNBT nbt) {
      Colour colour = new Colour(1.0F, 1.0F, 1.0F, 0.0F);
      if (nbt.func_74764_b("AccentColor")) {
         String accentColor = nbt.func_74779_i("AccentColor");
         if (!accentColor.equals("none")) {
            colour = new Colour(PortalColors.getColor(accentColor).getRGB());
         }
      }

      return colour;
   }

   public static void fizzleGunsInInventory(PlayerEntity player) {
      if (player.field_70170_p.field_72995_K) {
         PacketInit.INSTANCE.sendToServer((new CPortalGunInteractionPacket.Builder(PortalGunInteraction.FIZZLE)).build());
      } else {
         boolean didFizzleAny = false;
         ArrayList<ItemStack> test = new ArrayList(player.field_71071_by.field_70462_a);
         test.add(player.func_184592_cb());

         for(ItemStack itemStack : test) {
            if (itemStack.func_77973_b() instanceof PortalGun) {
               didFizzleAny = fizzleGunItem(itemStack) || didFizzleAny;
            }
         }

         if (didFizzleAny) {
            PacketInit.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity)player), new SPortalGunAnimationPacket(UUID.randomUUID(), PortalGunAnimation.FIZZLE));
            player.field_70170_p.func_184148_a((PlayerEntity)null, player.func_213303_ch().field_72450_a, player.func_213303_ch().field_72448_b, player.func_213303_ch().field_72449_c, (SoundEvent)SoundInit.PORTALGUN_FIZZLE.get(), SoundCategory.PLAYERS, 1.0F, ModUtil.randomSlightSoundPitch());
         }

      }
   }

   public static boolean fizzleGunItem(ItemStack itemStack) {
      Optional<UUID> gunUUID = getUUID(itemStack);
      if (!gunUUID.isPresent()) {
         return false;
      } else {
         PortalManager.getInstance().clearScheduledPlacements((UUID)gunUUID.get());
         PortalPair pair = PortalManager.getInstance().getPair((UUID)gunUUID.get());
         if (pair == null) {
            return false;
         } else {
            boolean fizzledAPortal = false;
            String lock = "";
            if (itemStack.func_77942_o()) {
               itemStack.func_77978_p().func_74764_b("Locked");
               lock = itemStack.func_77978_p().func_74779_i("Locked");
            }

            if (pair.has(PortalEnd.PRIMARY) && !lock.equals("Left")) {
               PortalEntity primary = pair.get(PortalEnd.PRIMARY);
               PortalManager.getInstance().scheduleRemoval(primary);
               fizzledAPortal = true;
            }

            if (pair.has(PortalEnd.SECONDARY) && !lock.equals("Right")) {
               PortalEntity secondary = pair.get(PortalEnd.SECONDARY);
               PortalManager.getInstance().scheduleRemoval(secondary);
               fizzledAPortal = true;
            }

            itemStack.func_196082_o().func_74768_a("LastPortal", 0);
            return fizzledAPortal;
         }
      }
   }

   public void func_77624_a(ItemStack itemStack, @Nullable World world, List<ITextComponent> list, ITooltipFlag iTooltipFlag) {
      CompoundNBT nbt = itemStack.func_196082_o();
      String lock = nbt.func_74764_b("Locked") ? nbt.func_74779_i("Locked") : "None";
      String leftColorName = lock.equals("Left") ? "locked" : getLeftDyeColour(nbt).toString();
      String rightColorName = lock.equals("Right") ? "locked" : getRightDyeColour(nbt).toString();
      int leftColor = lock.equals("Left") ? DyeColor.LIGHT_GRAY.getColorValue() : PortalColors.getColor(leftColorName).getRGB();
      int rightColor = lock.equals("Right") ? DyeColor.LIGHT_GRAY.getColorValue() : PortalColors.getColor(rightColorName).getRGB();
      list.add(ModUtil.tooltipComponent("tooltip.portalmod.portalgun.colors"));
      list.add((new TranslationTextComponent("tooltip.portalmod.colors." + leftColorName)).func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(leftColor))).func_240702_b_("§7 & ").func_230529_a_((new TranslationTextComponent("tooltip.portalmod.colors." + rightColorName)).func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(rightColor)))));
      if (Screen.func_231172_r_()) {
         list.add(new StringTextComponent(""));
      }

      ModUtil.addTooltip("portalgun", list);
   }

   public ITextComponent func_200295_i(ItemStack item) {
      CompoundNBT nbt = item.func_196082_o();
      Style colorStyle = Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(getAccentColour(nbt).getRGBValue()));
      return super.func_200295_i(item).func_230532_e_().func_230530_a_(colorStyle);
   }
}
