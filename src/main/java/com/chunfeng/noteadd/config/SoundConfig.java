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
<<<<<<< HEAD
    private static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("sound_mappings");
    private static final Type MAPPING_LIST_TYPE = new TypeToken<List<SoundMapping>>() {}.getType();
    private static int index = 1;
    private static final Path STATE_FILE = CONFIG_DIR.resolve("current_index.json");
=======
    private static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("sound_mappings.json");
    private static final Type MAPPING_LIST_TYPE = new TypeToken<List<SoundMapping>>() {}.getType();
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b

    public static class SoundMapping {
        private String block;
        private String sound;

<<<<<<< HEAD
=======
        public SoundMapping() {}

>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
        public SoundMapping(String blockId, String soundId) {
            this.block = blockId;
            this.sound = soundId;
        }

<<<<<<< HEAD
        public String getBlock() { return block; }
        public String getSound() { return sound; }

=======
        // Getters
        public String getBlock() { return block; }
        public String getSound() { return sound; }

        // Setters
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
        public void setBlock(String block) { this.block = block; }
        public void setSound(String sound) { this.sound = sound; }
    }

<<<<<<< HEAD
    static {
        try {
            if (Files.exists(STATE_FILE)) {
                String json = Files.readString(STATE_FILE);
                index = GSON.fromJson(json, Integer.class);
            } else {
                Files.createDirectories(CONFIG_DIR);
                saveCurrentIndex();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Path getCurrentTable() {
        return CONFIG_DIR.resolve("sound_mappings_" + index + ".json");
    }

    public static void setCurrentTable(int index) {
        if (index >= 1 && index <= 5) {
            SoundConfig.index = index;
            saveCurrentIndex();
        }
    }

    private static void saveCurrentIndex() {
        try {
            String json = GSON.toJson(index);
            Files.writeString(STATE_FILE, json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getIndex() {
        return index;
    }

    public static List<String> getTableNames() {
        List<String> names = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            names.add("sound_mappings_" + i);
        }
        return names;
    }

    private static List<SoundMapping> getMappings() {
        try {
            Path configFile = getCurrentTable();
            if (!Files.exists(configFile)) {
                Files.createDirectories(configFile.getParent());
=======
    private static List<SoundMapping> getMappings() {
        try {
            if (!Files.exists(CONFIG_FILE)) {
                Files.createDirectories(CONFIG_FILE.getParent());
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
                List<SoundMapping> defaults = createDefaultMappings();
                saveMappings(defaults);
                return defaults;
            }

<<<<<<< HEAD
            String json = Files.readString(configFile);
=======
            String json = Files.readString(CONFIG_FILE);
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
            return GSON.fromJson(json, MAPPING_LIST_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
            return createDefaultMappings();
        }
    }

    private static List<SoundMapping> createDefaultMappings() {
        List<SoundMapping> defaults = new ArrayList<>();
        defaults.add(new SoundMapping(
<<<<<<< HEAD
                "minecraft:air",
                "minecraft:sound_114514"
=======
                "minecraft:stone",
                "minecraft:block.stone.break"
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
        ));
        return defaults;
    }

    private static void saveMappings(List<SoundMapping> mappings) {
        try {
            String json = GSON.toJson(mappings);
<<<<<<< HEAD
            Files.writeString(getCurrentTable(), json);
=======
            Files.writeString(CONFIG_FILE, json);
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
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