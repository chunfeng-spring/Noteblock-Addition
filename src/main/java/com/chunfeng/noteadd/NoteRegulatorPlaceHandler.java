package com.chunfeng.noteadd;

import com.chunfeng.noteadd.block.NoteRegulator;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class NoteRegulatorPlaceHandler {
    public static void register() {
        // 修改为使用右键回调
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;

            // 获取玩家手上的物品
            ItemStack stack = player.getStackInHand(hand);
            BlockPos pos = hitResult.getBlockPos();
            Direction direction = hitResult.getSide();
            BlockState targetedState = world.getBlockState(pos);

            // 检查是否允许放置
            if (isValidPlacement(stack, targetedState, direction)) {
                BlockPos placementPos = pos.offset(direction);

                if (canPlaceAt(world, placementPos)) {
                    placeRegulator(world, placementPos);

                    // 消耗物品（非创造模式）
                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }

                    world.playSound(null, placementPos, SoundEvents.BLOCK_WOOL_PLACE,
                            SoundCategory.BLOCKS, 0.8F, 0.9F);

                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }

    private static boolean isValidPlacement(ItemStack stack, BlockState targetState, Direction direction) {
        Identifier stackId = Registries.ITEM.getId(stack.getItem());
        Identifier regulatorId = Registries.BLOCK.getId(NoteRegulator.NOTE_REGULATOR);

        if (!regulatorId.equals(stackId)) return false;
        if (!(targetState.getBlock() instanceof NoteBlock)) return false;

        return direction == Direction.UP;
    }

    private static boolean canPlaceAt(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isAir() || state.isReplaceable();
    }

    private static void placeRegulator(World world, BlockPos pos) {
        BlockState regulatorState = NoteRegulator.NOTE_REGULATOR.getDefaultState();
        world.setBlockState(pos, regulatorState);
    }
}