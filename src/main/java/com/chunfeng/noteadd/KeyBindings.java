package com.chunfeng.noteadd;

import com.chunfeng.noteadd.gui.SoundMappingGUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class KeyBindings {
    private static KeyBinding openSoundConfig;

    public static void register() {
        openSoundConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "manage_sound_mappings",
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_U,
                "noteblock-addition"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openSoundConfig.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new SoundMappingGUI(null));
            }
        });
    }
}
