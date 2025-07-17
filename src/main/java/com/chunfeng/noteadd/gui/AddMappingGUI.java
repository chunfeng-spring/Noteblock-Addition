package com.chunfeng.noteadd.gui;

import com.chunfeng.noteadd.config.SoundConfig;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class AddMappingGUI extends Screen {
    private final Screen parent;
    private BlockSelectionList blockList;
    private SoundSelectionList soundList;
    private ButtonWidget addButton;
    private TextFieldWidget blockSearchField;
    private TextFieldWidget soundSearchField;

    // 记录列表位置
    private int blockListX;
    private int soundListX;

    public AddMappingGUI(Screen parent) {
        super(Text.literal("添加新映射"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int topMargin = 30; // 顶部边距
        int bottomMargin = 50; // 底部边距
        int listHeight = height - topMargin - bottomMargin;
        int listWidth = (width - 30) / 2; // 列表宽度
        int listItemHeight = 16; // 列表项的高度
        int searchFieldWidth = listWidth - 90; // 搜索框宽度

        // 创建方块搜索框
        blockSearchField = new TextFieldWidget(
                textRenderer,
                blockListX + 60,
                13,
                searchFieldWidth,
                15,
                Text.literal("")
        );
        blockSearchField.setPlaceholder(Text.literal("搜索方块..."));
        addDrawableChild(blockSearchField);

        // 创建音效搜索框
        soundSearchField = new TextFieldWidget(
                textRenderer,
                soundListX + listWidth + 70,
                13,
                searchFieldWidth,
                15,
                Text.literal("")
        );
        soundSearchField.setPlaceholder(Text.literal("搜索音效..."));
        addDrawableChild(soundSearchField);

        // 方块选择列表
        this.blockListX = 10;
        this.blockList = new BlockSelectionList(client, listWidth, listHeight, topMargin, listItemHeight) {
            @Override
            protected int getScrollbarPositionX() {
                return this.left + this.width - 6;
            }
        };
        blockList.setLeftPos(blockListX);
        addSelectableChild(blockList);

        // 音效选择列表
        this.soundListX = width - listWidth - 10;
        this.soundList = new SoundSelectionList(client, listWidth, listHeight, topMargin, listItemHeight) {
            @Override
            protected int getScrollbarPositionX() {
                return this.left + this.width - 6;
            }
        };
        soundList.setLeftPos(soundListX);
        addSelectableChild(soundList);

        blockSearchField.setChangedListener(text -> blockList.reloadList());
        soundSearchField.setChangedListener(text -> soundList.reloadList());

        // 添加按钮
        int centerX = width / 2;
        int buttonY = height - 45;
        int buttonWidth = 100;
        int buttonSpacing = 10;

        this.addButton = ButtonWidget.builder(Text.literal("添加"), button -> {
            addSelectedMapping();
            close();
        }).dimensions(centerX - buttonWidth - buttonSpacing/2, buttonY, buttonWidth, 20).build();
        addButton.active = false;
        addDrawableChild(addButton);

        // 取消按钮
        addDrawableChild(ButtonWidget.builder(Text.literal("取消"), button -> close())
                .dimensions(centerX + buttonSpacing/2, buttonY, buttonWidth, 20).build());
    }

    private void addSelectedMapping() {
        if (blockList != null && soundList != null) {
            String blockId = blockList.getSelectedItem();
            String soundId = soundList.getSelectedItem();
            if (blockId != null && soundId != null) {
                SoundConfig.addMapping(blockId, soundId);
            }
        }
    }

    private void updateAddButtonState() {
        if (blockList != null && soundList != null) {
            addButton.active = (blockList.getSelectedItem() != null && soundList.getSelectedItem() != null);
        }
    }

    @Override
    public void close() {
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        // 绘制标题和标签
        context.drawText(textRenderer, Text.literal("选择方块"),
                blockListX + 5, 18, 0xFFFFFF, false);
        context.drawText(textRenderer, Text.literal("选择音效"),
                soundListX + 5, 18, 0xFFFFFF, false);

        blockSearchField.render(context, mouseX, mouseY, delta);
        soundSearchField.render(context, mouseX, mouseY, delta);

        blockList.render(context, mouseX, mouseY, delta);
        soundList.render(context, mouseX, mouseY, delta);

        // 悬停提示
        if (blockList.isMouseOver(mouseX, mouseY)) {
            BlockSelectionList.BlockEntry hoveredBlock = (BlockSelectionList.BlockEntry) blockList.getHoveredEntryPublic();
            if (hoveredBlock != null) {
                context.drawTooltip(textRenderer, Text.literal(hoveredBlock.id), mouseX, mouseY);
            }
        }

        if (soundList.isMouseOver(mouseX, mouseY)) {
            SoundSelectionList.SoundEntry hoveredSound = (SoundSelectionList.SoundEntry) soundList.getHoveredEntryPublic();
            if (hoveredSound != null) {
                context.drawTooltip(textRenderer, Text.literal(hoveredSound.id), mouseX, mouseY);
            }
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private abstract class SelectionList extends ElementListWidget<SelectionList.Entry> {
        protected String selectedItem;

        public SelectionList(MinecraftClient client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, top + height, itemHeight);
            this.setRenderBackground(true);
            this.setRenderHorizontalShadows(false);
            this.setRenderSelection(true);
        }

        public Entry getHoveredEntryPublic() {
            return this.getHoveredEntry(); // 可以访问父类的protected方法
        }

        public String getSelectedItem() {
            return selectedItem;
        }

        public void setSelectedItem(String item) {
            this.selectedItem = item;
            updateAddButtonState();
        }

        @Override
        public int getRowWidth() {
            return this.width - 10;
        }

        protected abstract class Entry extends ElementListWidget.Entry<Entry> {
            protected final String id;

            public Entry(String id) {
                this.id = id;
            }

            @Override
            public List<? extends Element> children() {
                return Collections.emptyList();
            }

            @Override
            public List<? extends Selectable> selectableChildren() {
                return Collections.emptyList();
            }

            public abstract boolean isMouseOver(double mouseX, double mouseY);
        }
    }

    private class BlockSelectionList extends SelectionList {
        private List<String> allBlockIds;

        public BlockSelectionList(MinecraftClient client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, itemHeight);
            allBlockIds = new ArrayList<>();
            Registries.BLOCK.forEach(block -> {
                if (!block.asItem().getDefaultStack().isEmpty()) {
                    allBlockIds.add(Registries.BLOCK.getId(block).toString());
                }
            });
            allBlockIds.sort(String::compareToIgnoreCase);
            reloadList();
        }

        public void reloadList() {
            clearEntries();
            String searchText = blockSearchField.getText().toLowerCase();

            allBlockIds.stream()
                    .filter(id -> searchText.isEmpty() || id.toLowerCase().contains(searchText))
                    .forEach(id -> addEntry(new BlockEntry(id)));
        }

        private class BlockEntry extends Entry {
            private int entryX, entryY, entryWidth, entryHeight; // 新增字段记录位置

            public BlockEntry(String id) {
                super(id);
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                               int mouseX, int mouseY, boolean hovered, float delta) {
                this.entryX = x;
                this.entryY = y;
                this.entryWidth = entryWidth;
                this.entryHeight = entryHeight;

                Block block = Registries.BLOCK.get(Identifier.tryParse(id));
                if (block != null) {
                    context.drawItem(new ItemStack(block), x - 2, y - 2, 8, 8);
                    int color = (selectedItem != null && selectedItem.equals(id)) ? 0x55FF55 : 0xFFFFFF;
                    context.drawText(textRenderer, id, x + 15, y + 3, color, false);
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (isMouseOver(mouseX, mouseY)) {
                    setSelectedItem(id);
                    return true;
                }
                return false;
            }

            // 悬停检测
            @Override
            public boolean isMouseOver(double mouseX, double mouseY) {
                return mouseX >= entryX &&
                        mouseX <= entryX + entryWidth &&
                        mouseY >= entryY &&
                        mouseY <= entryY + entryHeight;
            }
        }
    }

    private class SoundSelectionList extends SelectionList {
        private static final Set<String> IGNORED_KEYS = Set.of("remove");
        private final Map<String, List<String>> soundVariations = new HashMap<>();
        private final List<String> allSoundIds = new ArrayList<>();
        private final List<String> customSoundIds = new ArrayList<>();

        public SoundSelectionList(MinecraftClient client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, itemHeight);
            loadAllSounds();
            reloadList();
        }

        private void loadAllSounds() {
            soundVariations.clear();
            allSoundIds.clear();
            customSoundIds.clear();

            ResourceManager resourceManager = client.getResourceManager();
            if (resourceManager == null) return;

            for (String namespace : resourceManager.getAllNamespaces()) {
                Identifier soundsFileId = new Identifier(namespace, "sounds.json");
                try {
                    List<Resource> resources = resourceManager.getAllResources(soundsFileId);
                    boolean isVanilla = true;
                    for (Resource resource : resources) {
                        try (InputStream stream = resource.getInputStream()) {
                            parseSoundJson(stream, namespace, !isVanilla);
                            isVanilla = false;
                        }
                    }
                } catch (Exception e) {
                    // 忽略解析错误
                }
            }

            for (Identifier soundId : Registries.SOUND_EVENT.getIds()) {
                String idStr = soundId.toString();
                if (!allSoundIds.contains(idStr)) {
                    allSoundIds.add(idStr);
                    if (!soundId.getNamespace().equals("minecraft")) {
                        customSoundIds.add(idStr);
                    }
                }
            }
        }

        private void parseSoundJson(InputStream stream, String namespace, boolean isCustom) throws Exception {
            JsonObject json = JsonHelper.deserialize(new InputStreamReader(stream));
            for (String soundKey : json.keySet()) {
                if (IGNORED_KEYS.contains(soundKey)) continue;

                String fullSoundId = namespace + ":" + soundKey;
                if (isCustom && !customSoundIds.contains(fullSoundId)) {
                    customSoundIds.add(fullSoundId);
                }
                if (!allSoundIds.contains(fullSoundId)) {
                    allSoundIds.add(fullSoundId);
                }

                List<String> variations = new ArrayList<>();
                JsonElement soundElement = json.get(soundKey);

                if (soundElement.isJsonObject()) {
                    JsonObject soundObj = soundElement.getAsJsonObject();
                    JsonElement soundsElement = soundObj.get("sounds");
                    if (soundsElement != null && soundsElement.isJsonArray()) {
                        parseSoundArray(soundsElement.getAsJsonArray(), variations);
                    }
                } else if (soundElement.isJsonArray()) {
                    parseSoundArray(soundElement.getAsJsonArray(), variations);
                }

                if (!variations.isEmpty()) {
                    soundVariations.put(fullSoundId, variations);
                }
            }
        }

        private void parseSoundArray(JsonArray soundArray, List<String> variations) {
            for (JsonElement element : soundArray) {
                if (element.isJsonObject()) {
                    JsonObject soundEntry = element.getAsJsonObject();
                    JsonElement nameElement = soundEntry.get("name");
                    if (nameElement != null && nameElement.isJsonPrimitive()) {
                        variations.add(nameElement.getAsString());
                    }
                } else if (element.isJsonPrimitive()) {
                    variations.add(element.getAsString());
                }
            }
        }

        public void reloadList() {
            clearEntries();
            String searchText = soundSearchField.getText().toLowerCase();

            // 添加自定义音效
            customSoundIds.stream()
                    .sorted()
                    .filter(id -> searchText.isEmpty() || id.toLowerCase().contains(searchText))
                    .forEach(this::addSoundEntry);

            // 添加原版音效
            allSoundIds.stream()
                    .sorted()
                    .filter(id -> !customSoundIds.contains(id))
                    .filter(id -> searchText.isEmpty() || id.toLowerCase().contains(searchText))
                    .forEach(this::addSoundEntry);
        }

        private void addSoundEntry(String id) {
            List<String> variations = soundVariations.get(id);
            boolean hasMultipleVariations = variations != null && variations.size() > 1;
            addEntry(new SoundEntry(id, hasMultipleVariations));
        }

        private class SoundEntry extends Entry {
            private final boolean hasMultipleVariations;
            private int entryX, entryY, entryWidth, entryHeight;

            public SoundEntry(String id, boolean hasMultipleVariations) {
                super(id);
                this.hasMultipleVariations = hasMultipleVariations;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                               int mouseX, int mouseY, boolean hovered, float delta) {
                this.entryX = x;
                this.entryY = y;
                this.entryWidth = entryWidth;
                this.entryHeight = entryHeight;

                int color;
                if (selectedItem != null && selectedItem.equals(id)) {
                    color = 0x55FF55;
                } else if (hasMultipleVariations) {
                    color = 0xAAAAAA; // 多个采样显示灰色
                } else {
                    color = 0xFFFF00; // 单个采样显示黄色
                }
                context.drawText(textRenderer, id, x + 5, y + 3, color, false);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (isMouseOver(mouseX, mouseY)) {
                    setSelectedItem(id);
                    return true;
                }
                return false;
            }

            @Override
            public boolean isMouseOver(double mouseX, double mouseY) {
                return mouseX >= entryX &&
                        mouseX <= entryX + entryWidth &&
                        mouseY >= entryY &&
                        mouseY <= entryY + entryHeight;
            }
        }
    }
}