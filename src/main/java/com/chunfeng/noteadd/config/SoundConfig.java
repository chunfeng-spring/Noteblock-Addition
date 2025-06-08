package com.chunfeng.noteadd.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SoundConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("sound_mappings.json");
    private static final Type MAPPING_LIST_TYPE = new TypeToken<List<SoundMapping>>() {}.getType();

    public static class SoundMapping {
        private String block;
        private String sound;

        public SoundMapping() {}

        public SoundMapping(String blockId, String soundId) {
            this.block = blockId;
            this.sound = soundId;
        }

        // Getters
        public String getBlock() { return block; }
        public String getSound() { return sound; }

        // Setters
        public void setBlock(String block) { this.block = block; }
        public void setSound(String sound) { this.sound = sound; }
    }

    private static List<SoundMapping> getMappings() {
        try {
            if (!Files.exists(CONFIG_FILE)) {
                Files.createDirectories(CONFIG_FILE.getParent());
                List<SoundMapping> defaults = createDefaultMappings();
                saveMappings(defaults);
                return defaults;
            }

            String json = Files.readString(CONFIG_FILE);
            return GSON.fromJson(json, MAPPING_LIST_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
            return createDefaultMappings();
        }
    }

    private static List<SoundMapping> createDefaultMappings() {
        List<SoundMapping> defaults = new ArrayList<>();
        defaults.add(new SoundMapping(
                "minecraft:stone",
                "minecraft:block.stone.break"
        ));
        return defaults;
    }

    private static void saveMappings(List<SoundMapping> mappings) {
        try {
            String json = GSON.toJson(mappings);
            Files.writeString(CONFIG_FILE, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addMapping(String blockId, String soundId) {
        List<SoundMapping> mappings = getMappings();
        mappings.add(new SoundMapping(blockId, soundId));
        saveMappings(mappings);
    }

    public static void removeMapping(String blockId) {
        List<SoundMapping> mappings = getMappings();
        mappings.removeIf(mapping -> mapping.getBlock().equals(blockId));
        saveMappings(mappings);
    }

    public static List<SoundMapping> getAllMappings() {
        return getMappings();
    }
}