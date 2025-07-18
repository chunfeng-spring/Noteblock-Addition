package com.chunfeng.noteadd;

import com.chunfeng.noteadd.block.NoteRegulator;
import com.chunfeng.noteadd.block.NoteRegulatorEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient()) {
                return ActionResult.PASS;
            }

            final ItemStack heldStack = player.getStackInHand(hand);
            final BlockPos targetPos = hitResult.getBlockPos();
            final Direction face = hitResult.getSide();
            final BlockState targetState = world.getBlockState(targetPos);

            // 验证放置条件
            if (!isHoldingRegulator(heldStack)) {
                return ActionResult.PASS;
            }
            if (!(targetState.getBlock() instanceof NoteBlock)) {
                return ActionResult.PASS;
            }
            if (face != Direction.UP) {
                return ActionResult.PASS;
            }

            final BlockPos placementPos = targetPos.offset(face);
            if (!canPlaceAt(world, placementPos)) {
                return ActionResult.PASS;
            }

            // 处理NBT数据
            final NbtCompound stackTag = heldStack.getOrCreateNbt();
            placeRegulatorBlock(world, placementPos, stackTag);

            // 消耗物品
            if (!player.isCreative()) {
                heldStack.decrement(1);
            }

            // 播放音效
            world.playSound(
                    null,
                    placementPos,
                    SoundEvents.BLOCK_WOOL_PLACE,
                    SoundCategory.BLOCKS,
                    0.8f,
                    0.9f
            );

            return ActionResult.SUCCESS;
        });
    }

    private static boolean isHoldingRegulator(ItemStack stack) {
        final Identifier itemId = Registries.ITEM.getId(stack.getItem());
        final Identifier regulatorId = Registries.BLOCK.getId(NoteRegulator.NOTE_REGULATOR);
        return itemId.equals(regulatorId);
    }

    private static boolean canPlaceAt(World world, BlockPos pos) {
        final BlockState state = world.getBlockState(pos);
        return state.isAir() || state.isReplaceable();
    }

    private static void placeRegulatorBlock(World world, BlockPos pos, NbtCompound stackNbt) {
        // 放置方块
        final BlockState regulatorState = NoteRegulator.NOTE_REGULATOR.getDefaultState();
        world.setBlockState(pos, regulatorState);

        // 处理方块实体数据
        final BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof NoteRegulatorEntity regulatorEntity)) {
            return;
        }

        // 从物品NBT复制数据
        if (stackNbt.contains("BlockEntityTag", NbtCompound.COMPOUND_TYPE)) {
            final NbtCompound entityTag = stackNbt.getCompound("BlockEntityTag");
            regulatorEntity.readNbt(entityTag);
            regulatorEntity.markDirty();

            world.updateListeners(
                    pos,
                    regulatorState,
                    regulatorState,
                    Block.NOTIFY_LISTENERS
            );
        }
    }
}