package com.chunfeng.noteadd;

import com.chunfeng.noteadd.block.NoteRegulator;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class NoteblockAdditionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyBindings.register();

        BlockRenderLayerMap.INSTANCE.putBlock(
                NoteRegulator.NOTE_REGULATOR,
                RenderLayer.getTranslucent()
        );
    }
}
