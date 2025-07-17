package com.chunfeng.noteadd;

<<<<<<< HEAD
import com.chunfeng.noteadd.block.NoteRegulator;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
=======
import net.fabricmc.api.ClientModInitializer;
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b

public class NoteblockAdditionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyBindings.register();
<<<<<<< HEAD

        BlockRenderLayerMap.INSTANCE.putBlock(
                NoteRegulator.NOTE_REGULATOR,
                RenderLayer.getTranslucent()
        );
=======
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
    }
}
