package com.chunfeng.noteadd.gui;

import com.chunfeng.noteadd.config.SoundConfig;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class SoundMappingGUI extends Screen {
    private static final int BUTTON_WIDTH = 120;
    private static final int BUTTON_HEIGHT = 20;
    private final Screen parent;
    private MappingListWidget mappingList;

    public SoundMappingGUI(Screen parent) {
        super(Text.literal("SoundMappings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

<<<<<<< HEAD
        this.mappingList = new MappingListWidget(client, width, height, 30, height - 40, 25);
        addDrawableChild(mappingList);

        int selectorWidth = 150;
        int selectorHeight = 20;
        int selectorX = width - selectorWidth - 10;
        int selectorY = 10;

        // 映射表切换按钮
        List<String> tableNames = SoundConfig.getTableNames();
        CyclingButtonWidget<String> tableSelector = CyclingButtonWidget.<String>builder(
                        Text::literal
                )
                .values(tableNames)
                .initially(tableNames.get(SoundConfig.getIndex() - 1))
                .build(
                        selectorX, selectorY,
                        selectorWidth, selectorHeight,
                        Text.literal("点击切换"),
                        (button, value) -> {
                            int index = tableNames.indexOf(value) + 1;
                            SoundConfig.setCurrentTable(index);
                            mappingList.reload();
                        }
                );
        addDrawableChild(tableSelector);

=======
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
        int buttonY = height - 30;
        int centerX = width / 2;
        int buttonSpacing = 10;

<<<<<<< HEAD
=======
        // 映射列表
        this.mappingList = new MappingListWidget(client, width, height, 30, height - 40, 25);
        addDrawableChild(mappingList);

        // 添加新映射按钮
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
        this.addDrawableChild(ButtonWidget.builder(Text.literal("添加新映射"), button -> {
            client.setScreen(new AddMappingGUI(this));
        }).dimensions(centerX - 100 - buttonSpacing, buttonY, 100, BUTTON_HEIGHT).build());

<<<<<<< HEAD
=======
        // 关闭按钮
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
        this.addDrawableChild(ButtonWidget.builder(Text.literal("完成"), button -> close())
                .dimensions(centerX + buttonSpacing, buttonY, 100, BUTTON_HEIGHT).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        mappingList.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("SoundMappings"), width / 2, 10, 0xFFFFFF);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    private class MappingListWidget extends ElementListWidget<MappingListWidget.MappingEntry> {
        public MappingListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
            super(client, width, height, top, bottom, itemHeight);
            reload();
        }

        public void reload() {
            clearEntries();
            List<SoundConfig.SoundMapping> mappings = SoundConfig.getAllMappings();
            for (SoundConfig.SoundMapping mapping : mappings) {
                addEntry(new MappingEntry(mapping));
            }
        }

        @Override
        protected int getScrollbarPositionX() {
            return width - 7;
        }

        @Override
        public int getRowWidth() {
            return width - 20;
        }

        private class MappingEntry extends ElementListWidget.Entry<MappingEntry> {
            private final SoundConfig.SoundMapping mapping;
            private ButtonWidget deleteButton;
            private String blockId;

            public MappingEntry(SoundConfig.SoundMapping mapping) {
                this.mapping = mapping;
                this.blockId = mapping.getBlock();
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight,
                               int mouseX, int mouseY, boolean hovered, float delta) {
                Block block = Registries.BLOCK.get(Identifier.tryParse(mapping.getBlock()));

                String blockName = block != null ? block.getName().getString() : blockId;
                String soundName = mapping.getSound();

                int iconY = y + 5;

<<<<<<< HEAD
=======
                // 绘制方块图标
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
                if (block != null) {
                    context.drawItem(new ItemStack(block), x + 5, iconY);
                }

<<<<<<< HEAD
                int nameX = x + 30;
                int nameY = y + 5;

                context.drawText(textRenderer, blockName, nameX, nameY, 0xFFFFFF, false);

=======
                // 绘制方块名称和ID
                int nameX = x + 30;
                int nameY = y + 5;

                // 方块名称（白色）
                context.drawText(textRenderer, blockName, nameX, nameY, 0xFFFFFF, false);

                // 方块ID（灰色）
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
                int idWidth = textRenderer.getWidth(blockId);
                int idX = nameX + textRenderer.getWidth(blockName) + 10;
                context.drawText(textRenderer, "(" + blockId + ")", idX, nameY, 0xAAAAAA, false);

<<<<<<< HEAD
                context.drawText(textRenderer, "音效: " + soundName, nameX, y + 18, 0xAAAAAA, false);

=======
                // 绘制音效ID
                context.drawText(textRenderer, "音效: " + soundName, nameX, y + 18, 0xAAAAAA, false);

                // 删除按钮
>>>>>>> 236b4fda25b280d805b2fc0de2773740f0da762b
                if (deleteButton == null) {
                    deleteButton = ButtonWidget.builder(Text.literal("删除"), button -> {
                        SoundConfig.removeMapping(mapping.getBlock());
                        reload();
                    }).dimensions(x + entryWidth - 45, y + 5, 40, 20).build();
                }
                deleteButton.setY(y + 5);
                deleteButton.render(context, mouseX, mouseY, delta);
            }

            @Override
            public List<? extends Element> children() {
                List<Element> children = new ArrayList<>();
                if (deleteButton != null) {
                    children.add(deleteButton);
                }
                return children;
            }

            @Override
            public List<? extends Selectable> selectableChildren() {
                List<Selectable> selectables = new ArrayList<>();
                if (deleteButton != null) {
                    selectables.add(deleteButton);
                }
                return selectables;
            }
        }
    }
}