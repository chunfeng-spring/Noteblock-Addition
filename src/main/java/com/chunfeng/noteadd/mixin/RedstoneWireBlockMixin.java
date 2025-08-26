package com.chunfeng.noteadd.mixin;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedstoneWireBlock.class)
public abstract class RedstoneWireBlockMixin extends Block {

    @Unique
    private static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public RedstoneWireBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void addWaterloggedProperty(AbstractBlock.Settings settings, CallbackInfo ci) {
        this.setDefaultState(this.getDefaultState().with(WATERLOGGED, false));
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void injectWaterlogged(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        builder.add(WATERLOGGED);
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void setWaterloggedState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        cir.setReturnValue(cir.getReturnValue().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER));
    }

    // 在状态更新时传递含水属性
    @ModifyArg(
            method = "getStateForNeighborUpdate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/RedstoneWireBlock;getPlacementState(Lnet/minecraft/world/BlockView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
            ),
            index = 1
    )
    private BlockState preserveWaterloggedState(BlockState previous) {
        return previous;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED)
                ? Fluids.WATER.getStill(false)
                : super.getFluidState(state);
    }

    @Inject(method = "getStateForNeighborUpdate", at = @At("RETURN"), cancellable = true)
    private void handleFluid(BlockState state, Direction direction, BlockState neighborState,
                             WorldAccess world, BlockPos pos, BlockPos neighborPos,
                             CallbackInfoReturnable<BlockState> cir) {
        BlockState newState = cir.getReturnValue();

        if (state.contains(WATERLOGGED)) {
            newState = newState.with(WATERLOGGED, state.get(WATERLOGGED));
        }

        // 处理流体更新
        if (newState.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        cir.setReturnValue(newState);
    }
}
