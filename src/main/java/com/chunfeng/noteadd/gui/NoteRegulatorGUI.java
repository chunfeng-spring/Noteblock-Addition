package com.chunfeng.noteadd.gui;

import com.chunfeng.noteadd.NoteRegulatorPacket;
import com.chunfeng.noteadd.block.NoteRegulatorEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class NoteRegulatorGUI extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("noteblock-addition", "textures/gui/background.png");

    private final BlockPos blockPos;
    private final NoteRegulatorEntity blockEntity;
    private TextFieldWidget centField;
    private TextFieldWidget volumeField;
    private CentSliderWidget centSlider;
    private OctaveSliderWidget octaveSlider;
    private VolumeSliderWidget volumeSlider;
    private int initialOctaveValue = 0;
    private int initialCentValue = 0;
    private int initialVolumeValue = 100;

    public NoteRegulatorGUI(BlockPos blockPos, NoteRegulatorEntity blockEntity) {
        super(Text.of("音符盒扩展调节器"));
        this.blockPos = blockPos;
        this.blockEntity = blockEntity;

        // 从方块实体初始化值
        this.initialOctaveValue = blockEntity.getOctaveOffset();
        this.initialCentValue = blockEntity.getCentOffset();
        this.initialVolumeValue = blockEntity.getVolume();
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int sliderWidth = (int)(this.width * 0.3);

        // 八度偏移滑块
        this.octaveSlider = new OctaveSliderWidget(
                centerX - sliderWidth/2, centerY - 50,
                sliderWidth, 20,
                this.initialOctaveValue
        );
        this.addDrawableChild(this.octaveSlider);

        // 音分偏移滑块
        this.centSlider = new CentSliderWidget(
                centerX - sliderWidth/2, centerY - 10,
                sliderWidth, 20,
                initialCentValue
        ) {
            @Override
            protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            }

            @Override
            protected void onValueChanged(int newValue) {
                centField.setText(String.valueOf(newValue));
            }
        };
        this.addDrawableChild(this.centSlider);

        // 音分输入框
        this.centField = new TextFieldWidget(
                this.textRenderer,
                centerX + sliderWidth/2 + 10,
                centerY - 10,
                35, 20,
                Text.of("")
        );
        this.centField.setMaxLength(4);
        this.centField.setText(String.valueOf(initialCentValue));
        this.centField.setChangedListener((text) -> {
            try {
                int newValue = Integer.parseInt(text);
                if (newValue >= -100 && newValue <= 100) {
                    centSlider.setValue(newValue);
                }
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(this.centField);

        // 音量滑块
        this.volumeSlider = new VolumeSliderWidget(
                centerX - sliderWidth/2, centerY + 30,
                sliderWidth, 20,
                initialVolumeValue
        ) {
            @Override
            protected void appendClickableNarrations(NarrationMessageBuilder builder) {
            }

            @Override
            protected void onValueChanged(int newValue) {
                volumeField.setText(String.valueOf(newValue));
            }
        };
        this.addDrawableChild(this.volumeSlider);

        // 音量输入框
        this.volumeField = new TextFieldWidget(
                this.textRenderer,
                centerX + sliderWidth/2 + 10,
                centerY + 30,
                35, 20,
                Text.of("")
        );
        this.volumeField.setMaxLength(3);
        this.volumeField.setText(String.valueOf(initialVolumeValue));
        this.volumeField.setChangedListener((text) -> {
            try {
                int newValue = Integer.parseInt(text);
                if (newValue >= 0 && newValue <= 100) {
                    volumeSlider.setValue(newValue);
                }
            } catch (NumberFormatException ignored) {}
        });
        this.addDrawableChild(this.volumeField);
    }

    @Override
    public void close() {
        // 从滑块获取当前值
        int finalOctave = octaveSlider.getValue();
        int finalCent = centSlider.getValue();
        int finalVolume = volumeSlider.getValue();

        if (finalOctave != initialOctaveValue ||
                finalCent != initialCentValue ||
                finalVolume != initialVolumeValue) {
                NoteRegulatorPacket.sendToServer(
                        blockPos,
                        finalOctave,
                        finalCent,
                        finalVolume
                );
        }
        super.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);

        String title = "音符盒扩展调节器";
        int titleWidth = this.textRenderer.getWidth(title);
        context.drawText(
                this.textRenderer,
                Text.of(title),
                (this.width - titleWidth) / 2,
                40,
                0xFFFF55,
                false
        );

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        context.drawText(
                this.textRenderer,
                Text.of("八度偏移："),
                centerX - (int)(this.width * 0.3),
                centerY - 45,
                0xFFFFFF,
                false
        );

        context.drawText(
                this.textRenderer,
                Text.of("音分偏移："),
                centerX - (int)(this.width * 0.3),
                centerY - 5,
                0xFFFFFF,
                false
        );

        context.drawText(
                this.textRenderer,
                Text.of("音量调节："),
                centerX - (int)(this.width * 0.3),
                centerY + 35,
                0xFFFFFF,
                false
        );

        String octaveLabel = "-2    -1      0      +1    +2";
        int labelWidth = textRenderer.getWidth(octaveLabel);
        context.drawText(
                textRenderer,
                Text.of(octaveLabel),
                (width - labelWidth) / 2,
                centerY - 65,
                0xFFFFFF,
                false
        );

        int percentX = this.volumeField.getX() + this.volumeField.getWidth() + 5;
        int percentY = this.volumeField.getY() + (this.volumeField.getHeight() / 2) - (textRenderer.fontHeight / 2);
        context.drawText(
                this.textRenderer,
                "%",
                percentX,
                percentY,
                0xFFFFFF,
                false
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context) {
        int screenWidth = this.width;
        int screenHeight = this.height;

        int originalWidth = 1920;
        int originalHeight = 1080;

        float ratio = (float) originalHeight / originalWidth;
        int bgWidth = (int) (screenWidth * 0.75f);
        int bgHeight = (int) (bgWidth * ratio);

        int x = (screenWidth - bgWidth) / 2;
        int y = (screenHeight - bgHeight) / 2;

        float opacity = 0.5f;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacity);

        context.drawTexture(
                BACKGROUND_TEXTURE,
                x, y,
                0, 0,
                bgWidth, bgHeight,
                bgWidth, bgHeight
        );

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    // 八度偏移滑块
    private static class OctaveSliderWidget extends ClickableWidget {
        private int value = 0; // 范围：-2, -1, 0, 1, 2
        private final int[] positions = new int[5];
        private Runnable onValueChanged;

        protected OctaveSliderWidget(int x, int y, int width, int height, int initialValue) {
            super(x, y, width, height, Text.empty());
            this.value = MathHelper.clamp(initialValue, -2, 2);

            for (int i = 0; i < 5; i++) {
                int a = width - 4;
                positions[i] = x + 2 + i * a / 4;
            }
        }

        public void setValue(int value) {
            this.value = MathHelper.clamp(value, -2, 2);
        }

        public void setOnValueChanged(Runnable onValueChanged) {
            this.onValueChanged = onValueChanged;
        }

        public int getValue() {
            return this.value;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            int selected = 0;
            int minDistance = Integer.MAX_VALUE;

            for (int i = 0; i < 5; i++) {
                int distance = (int) Math.abs(mouseX - positions[i]);
                if (distance < minDistance) {
                    minDistance = distance;
                    selected = i;
                }
            }

            int newValue = selected - 2;
            if (newValue != this.value) {
                this.value = newValue;
                if (onValueChanged != null) {
                    onValueChanged.run();
                }
            }
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            context.fill(
                    this.getX(), this.getY(),
                    this.getX() + this.width, this.getY() + this.height - 2,
                    0x99444444
            );

            for (int i = 0; i < 5; i++) {
                int markX = positions[i] - 2;
                context.fill(
                        markX, this.getY() - 4,
                        markX + 4, this.getY() + this.height + 1,
                        0xFF333333
                );
            }

            int handleX = positions[this.value + 2] - 4;
            context.fill(
                    handleX, this.getY() - 5,
                    handleX + 8, this.getY() + this.height + 2,
                    0xFFFFFFFF
            );
        }

        @Override
        public void appendClickableNarrations(NarrationMessageBuilder builder) {
        }
    }

    // 抽象滑块类
    private static abstract class BaseSliderWidget extends ClickableWidget {
        protected BaseSliderWidget(int x, int y, int width, int height, Text message) {
            super(x, y, width, height, message);
        }

        public abstract void setValue(int value);
        public abstract int getValue();

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.setValueFromMouse(mouseX);
            onValueChanged(getValue());
        }

        @Override
        protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
            this.setValueFromMouse(mouseX);
            onValueChanged(getValue());
            super.onDrag(mouseX, mouseY, deltaX, deltaY);
        }

        protected abstract void setValueFromMouse(double mouseX);
        protected abstract void onValueChanged(int newValue);
    }

    // 音分滑块控件
    private static abstract class CentSliderWidget extends BaseSliderWidget {
        private double sliderValue;
        private final double minValue = -100;
        private final double maxValue = 100;

        protected CentSliderWidget(int x, int y, int width, int height, int initialValue) {
            super(x, y, width, height, Text.empty());
            setValue(initialValue);
        }

        @Override
        public void setValue(int value) {
            this.sliderValue = MathHelper.clamp((value - minValue) / (maxValue - minValue), 0.0, 1.0);
        }

        @Override
        public int getValue() {
            return (int) Math.round(minValue + sliderValue * (maxValue - minValue));
        }

        @Override
        protected void setValueFromMouse(double mouseX) {
            double normalizedValue = (mouseX - (this.getX() + 4)) / (double)(this.width - 8);
            this.sliderValue = MathHelper.clamp(normalizedValue, 0.0, 1.0);
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            // 绘制滑块轨道背景
            context.fill(
                    this.getX(), this.getY(),
                    this.getX() + this.width, this.getY() + this.height,
                    0x99444444
            );

            // 绘制滑块填充部分（浅蓝色）
            int fillWidth = (int)(this.width * this.sliderValue);
            context.fill(
                    this.getX(), this.getY(),
                    this.getX() + fillWidth, this.getY() + this.height,
                    0xFF66CCFF
            );

            // 绘制滑块手柄（浅灰色）
            int handleX = (int)(this.getX() + (this.sliderValue * (this.width - 8)));
            context.fill(
                    handleX - 2, this.getY() - 4,
                    handleX + 10, this.getY() + this.height + 4,
                    0xFFEEEEEE
            );
        }
    }

    // 音量滑块控件
    private static abstract class VolumeSliderWidget extends BaseSliderWidget {
        private double sliderValue;
        private final double minValue = 0;
        private final double maxValue = 100;

        protected VolumeSliderWidget(int x, int y, int width, int height, int initialValue) {
            super(x, y, width, height, Text.empty());
            setValue(initialValue);
        }

        @Override
        public void setValue(int value) {
            this.sliderValue = MathHelper.clamp((value - minValue) / (maxValue - minValue), 0.0, 1.0);
        }

        @Override
        public int getValue() {
            return (int) Math.round(minValue + sliderValue * (maxValue - minValue));
        }

        @Override
        protected void setValueFromMouse(double mouseX) {
            double normalizedValue = (mouseX - (this.getX() + 4)) / (double)(this.width - 8);
            this.sliderValue = MathHelper.clamp(normalizedValue, 0.0, 1.0);
        }

        @Override
        public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
            // 绘制滑块轨道背景
            context.fill(
                    this.getX(), this.getY(),
                    this.getX() + this.width, this.getY() + this.height,
                    0x99444444
            );

            // 绘制滑块填充部分（绿色）
            int fillWidth = (int)(this.width * this.sliderValue);
            context.fill(
                    this.getX(), this.getY(),
                    this.getX() + fillWidth, this.getY() + this.height,
                    0xFF66FF66
            );

            // 绘制滑块手柄
            int handleX = (int)(this.getX() + (this.sliderValue * (this.width - 8)));
            context.fill(
                    handleX - 2, this.getY() - 4,
                    handleX + 10, this.getY() + this.height + 4,
                    0xFFEEEEEE
            );
        }
    }
}