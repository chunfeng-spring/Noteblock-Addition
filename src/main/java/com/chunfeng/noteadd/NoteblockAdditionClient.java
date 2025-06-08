package com.chunfeng.noteadd;

import net.fabricmc.api.ClientModInitializer;

public class NoteblockAdditionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyBindings.register();
    }
}
