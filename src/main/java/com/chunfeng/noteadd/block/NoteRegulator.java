package com.chunfeng.noteadd.block;

import com.chunfeng.noteadd.gui.NoteRegulatorGUI;
import com.chunfeng.noteadd.NoteblockAddition;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
<<<<<<< HEAD
import net.minecraft.block.*;
=======
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
<<<<<<< HEAD
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
=======
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
import net.minecraft.world.World;

public class NoteRegulator {
    public static BlockEntityType<NoteRegulatorEntity> NOTE_REGULATOR_ENTITY;

    // 注册方块
    public static Block register(String id, Block block) {
        registerBlockItems(id, block);
        return Registry.register(Registries.BLOCK, new Identifier(NoteblockAddition.MOD_ID, id), block);
    }

    // 注册方块物品
    public static void registerBlockItems(String id, Block block) {
        Registry.register(Registries.ITEM, new Identifier(NoteblockAddition.MOD_ID, id),
                new BlockItem(block, new Item.Settings()));
    }

    private static void addItemToItemGroup(FabricItemGroupEntries entries) {
        entries.add(NOTE_REGULATOR);
    }

    public static class NoteRegulatorBlock extends Block implements BlockEntityProvider {
        public NoteRegulatorBlock(Settings settings) {
            super(settings);
        }

<<<<<<< HEAD
        private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 0.1, 16);

        @Override
        public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
            return SHAPE;
        }

=======
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
        @Override
        public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
            return new NoteRegulatorEntity(pos, state);
        }

        @Override
        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
            if (!world.isClient) {
                return ActionResult.SUCCESS;
            }

            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof NoteRegulatorEntity) {
                NoteRegulatorEntity regulatorEntity = (NoteRegulatorEntity) blockEntity;
                MinecraftClient.getInstance().setScreen(
                        new NoteRegulatorGUI(pos, regulatorEntity)
                );
            }
            return ActionResult.SUCCESS;
        }
    }

    // 注册音符盒调节器
    public static final Block NOTE_REGULATOR = register("note_regulator",
<<<<<<< HEAD
            new NoteRegulatorBlock(FabricBlockSettings.copyOf(Blocks.RED_CARPET).nonOpaque().noCollision().nonOpaque()));
=======
            new NoteRegulatorBlock(FabricBlockSettings.copyOf(Blocks.RED_CARPET).nonOpaque().noCollision()));
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b

    // 注册方块和相关实体
    public static void registerBlock() {
        NOTE_REGULATOR_ENTITY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                new Identifier(NoteblockAddition.MOD_ID, "note_regulator_entity"),
                FabricBlockEntityTypeBuilder.create(NoteRegulatorEntity::new, NOTE_REGULATOR).build()
        );
    }

    // 注册物品
    public static void registerItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(NoteRegulator::addItemToItemGroup);
    }
}