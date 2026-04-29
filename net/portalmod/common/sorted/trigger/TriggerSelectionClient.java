package net.portalmod.common.sorted.trigger;

import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.portalmod.core.init.PacketInit;

public class TriggerSelectionClient {
   public static final int MAX_FIELD_SIZE = 32;
   public static final int MAX_DISTANCE_FROM_BLOCK = 32;
   public static TriggerTileEntity selected;
   public static BlockPos selectingStart;
   public static BlockPos selectingEnd;

   public static boolean isSelecting() {
      return selected != null;
   }

   public static boolean isSelectingStart() {
      return selected != null && selectingStart != null && selectingEnd == null;
   }

   public static boolean isSelectingEnd() {
      return selected != null && selectingStart != null && selectingEnd != null;
   }

   public static boolean isSelecting(TriggerTileEntity trigger) {
      return selected == trigger;
   }

   public static void startSelecting(TriggerTileEntity trigger) {
      selected = trigger;
      selectingStart = null;
      selectingEnd = null;
   }

   public static void confirmSelection() {
      if (isSelectingStart()) {
         selectingEnd = new BlockPos(selectingStart);
      } else if (isSelectingEnd()) {
         PacketInit.INSTANCE.sendToServer(new CTriggerEndConfigPacket(selected.func_174877_v(), selectingStart, selectingEnd));
         stopSelecting();
      }

   }

   public static void abort() {
      PacketInit.INSTANCE.sendToServer(new CTriggerAbortConfigPacket(selected.func_174877_v()));
      stopSelecting();
   }

   public static void stopSelecting() {
      selected = null;
      selectingStart = null;
      selectingEnd = null;
   }

   public static void updateSelectedPos(BlockPos pos) {
      if (selected != null && !selected.func_145837_r()) {
         BlockPos blockEntityPos = selected.func_174877_v();
         if (isSelectingEnd()) {
            selectingEnd = limitPosToDistance(limitPosToDistance(pos, selectingStart.func_177971_a(blockEntityPos), 31), blockEntityPos, 31).func_177973_b(blockEntityPos);
         } else {
            selectingStart = limitPosToDistance(pos, blockEntityPos, 31).func_177973_b(blockEntityPos);
         }

      } else {
         abort();
      }
   }

   public static BlockPos limitPosToDistance(BlockPos pos, BlockPos origin, int distance) {
      BlockPos min = origin.func_177982_a(-distance, -distance, -distance);
      BlockPos max = origin.func_177982_a(distance, distance, distance);
      BlockPos.Mutable result = pos.func_239590_i_();
      result.func_239620_a_(Axis.X, min.func_177958_n(), max.func_177958_n());
      result.func_239620_a_(Axis.Y, min.func_177956_o(), max.func_177956_o());
      result.func_239620_a_(Axis.Z, min.func_177952_p(), max.func_177952_p());
      return result.func_185334_h();
   }

   public static AxisAlignedBB getBox() {
      if (isSelectingStart()) {
         return new AxisAlignedBB(selectingStart);
      } else {
         return isSelectingEnd() ? (new AxisAlignedBB(selectingStart, selectingEnd)).func_72321_a((double)1.0F, (double)1.0F, (double)1.0F) : null;
      }
   }
}
