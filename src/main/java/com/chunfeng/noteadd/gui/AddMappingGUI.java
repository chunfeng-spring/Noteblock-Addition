package com.chunfeng.noteadd.gui;

import com.chunfeng.noteadd.config.SoundConfig;
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
        int listItemHeight = 20; // 列表项的高度
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

        // 搜索框事件监听
        blockSearchField.setChangedListener(text -> {
            blockList.reloadList();
        });
        soundSearchField.setChangedListener(text -> {
            soundList.reloadList();
        });

        // 方块选择列表
        this.blockListX = 10;
        this.blockList = new BlockSelectionList(client, listWidth, listHeight, topMargin, listItemHeight) {
            @Override
            protected int getScrollbarPositionX() {
                return this.left + this.width - 6;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return super.mouseClicked(mouseX, mouseY, button);
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

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
        soundList.setLeftPos(soundListX);
        addSelectableChild(soundList);

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
        // 获取选中的blockId和soundId
        Optional.ofNullable(blockList)
                .flatMap(list -> Optional.ofNullable(list.getSelectedItem()))
                .flatMap(blockId -> Optional.ofNullable(soundList)
                        .flatMap(list -> Optional.ofNullable(list.getSelectedItem()))
                        .map(soundId -> {
                            SoundConfig.addMapping(blockId, soundId);
                            return true;
                        })
                );
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
        if (textRenderer != null) {
            context.drawText(textRenderer, Text.literal("选择方块"),
                    blockListX + 5, 20, 0xFFFFFF, false);

            context.drawText(textRenderer, Text.literal("选择音效"),
                    soundListX + 5, 20, 0xFFFFFF, false);
        }

        blockSearchField.render(context, mouseX, mouseY, delta);
        soundSearchField.render(context, mouseX, mouseY, delta);

        if (blockList != null) blockList.render(context, mouseX, mouseY, delta);
        if (soundList != null) soundList.render(context, mouseX, mouseY, delta);

        super.render(context, mouseX, mouseY, delta);
    }

    private abstract class SelectionList extends ElementListWidget<SelectionList.Entry> {
        protected String selectedItem;

        public SelectionList(MinecraftClient client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, top + height, itemHeight);
            this.setRenderBackground(true); // 渲染背景
            this.setRenderHorizontalShadows(false); // 不渲染水平阴影
            this.setRenderSelection(true); // 启用选择渲染
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
        }
    }

    private class BlockSelectionList extends SelectionList {
        private List<String> allBlockIds; // 存储所有方块ID
        public BlockSelectionList(MinecraftClient client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, itemHeight);

            // 获取所有方块并排序
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

            for (String id : allBlockIds) {
                if (searchText.isEmpty() || id.toLowerCase().contains(searchText)) {
                    addEntry(new BlockEntry(id));
                }
            }
        }

        private class BlockEntry extends Entry {
            public BlockEntry(String id) {
                super(id);
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                               int mouseX, int mouseY, boolean hovered, float delta) {
                if (textRenderer != null) {
                    Block block = Registries.BLOCK.get(Identifier.tryParse(id));

                    if (block != null) {
                        // 方块图标
                        context.drawItem(new ItemStack(block), x + 2, y + 2);

                        // 方块名称
                        int color = (selectedItem != null && selectedItem.equals(id)) ? 0x55FF55 : 0xFFFFFF;
                        String displayId = textRenderer.trimToWidth(id, entryWidth - 30);
                        context.drawText(textRenderer, displayId, x + 25, y + 7, color, false);
                    }
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                setSelectedItem(id);
                return true;
            }
        }
    }

    private class SoundSelectionList extends SelectionList {
        private static final Set<String> IGNORED_KEYS = Set.of("remove");
        private final Set<String> customSoundIds = new HashSet<>();
        private final Set<String> vanillaSoundIds = new HashSet<>();

        public SoundSelectionList(MinecraftClient client, int width, int height, int top, int itemHeight) {
            super(client, width, height, top, itemHeight);
            loadAllSounds();
            reloadList();
        }

        public void loadAllSounds() {
            customSoundIds.clear();
            vanillaSoundIds.clear();

            ResourceManager resourceManager = client.getResourceManager();
            if (resourceManager == null) return;

            // 扫描所有自定义音效
            for (String namespace : resourceManager.getAllNamespaces()) {
                Identifier soundsFileId = new Identifier(namespace, "sounds.json");
                Optional<Resource> resource = resourceManager.getResource(soundsFileId);

                if (resource.isEmpty()) continue;

                try (InputStream stream = resource.get().getInputStream()) {
                    JsonObject json = JsonHelper.deserialize(new InputStreamReader(stream));
                    for (String key : json.keySet()) {
                        if (IGNORED_KEYS.contains(key)) continue;
                        customSoundIds.add(namespace + ":" + key);
                    }
                } catch (Exception ignored) {}
            }

            // 添加所有原版音效
            for (Identifier soundId : Registries.SOUND_EVENT.getIds()) {
                vanillaSoundIds.add(soundId.toString());
            }
        }

        public void reloadList() {
            clearEntries();
            String searchText = soundSearchField.getText().toLowerCase();

            customSoundIds.stream()
                    .sorted()
                    .filter(id -> searchText.isEmpty() || id.toLowerCase().contains(searchText))
                    .forEach(id -> addEntry(new SoundEntry(id, false)));

            vanillaSoundIds.stream()
                    .sorted()
                    .filter(id -> searchText.isEmpty() || id.toLowerCase().contains(searchText))
                    .forEach(id -> addEntry(new SoundEntry(id, true)));
        }

        private class SoundEntry extends Entry {
            private final boolean isVanilla;

            public SoundEntry(String id, boolean isVanilla) {
                super(id);
                this.isVanilla = isVanilla;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                setSelectedItem(id);
                return true;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                               int mouseX, int mouseY, boolean hovered, float delta) {
                if (textRenderer != null) {
                    // 自定义音效显示黄色，原版音效显示白色
                    int color = (selectedItem != null && selectedItem.equals(id)) ? 0x55FF55 :
                            isVanilla ? 0xFFFFFF : 0xFFFF00;

                    context.drawText(textRenderer, id, x + 5, y + 7, color, false);
                }
            }
        }
    }
}