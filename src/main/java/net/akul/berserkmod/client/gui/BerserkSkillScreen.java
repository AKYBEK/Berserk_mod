package net.akul.berserkmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Экран навыков Берсерка, который заменяет стандартное меню Passive Skill Tree
 */
public class BerserkSkillScreen extends Screen {
    private final int imageWidth = 256;
    private final int imageHeight = 200;
    private int leftPos;
    private int topPos;

    public BerserkSkillScreen() {
        super(Component.translatable("gui.berserkmod.berserk_skills"));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        // Рендерим темно-красный фон (в стиле Берсерка)
        guiGraphics.fill(this.leftPos, this.topPos,
                this.leftPos + this.imageWidth, this.topPos + this.imageHeight,
                0xFF8B0000); // Темно-красный цвет

        // Рендерим черную границу
        guiGraphics.fill(this.leftPos - 2, this.topPos - 2,
                this.leftPos + this.imageWidth + 2, this.topPos + this.imageHeight + 2,
                0xFF000000); // Черная граница

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // Заголовок меню
        Component title = Component.translatable("gui.berserkmod.berserk_skills");
        guiGraphics.drawCenteredString(this.font, title,
                this.leftPos + this.imageWidth / 2,
                this.topPos + 15,
                0xFFFFFF);

        // Подзаголовок
        Component subtitle = Component.literal("Berserk Skill Tree");
        guiGraphics.drawCenteredString(this.font, subtitle,
                this.leftPos + this.imageWidth / 2,
                this.topPos + 30,
                0xFFD3D3D3);

        // Временный текст о том, что меню пустое
        Component placeholder1 = Component.literal("Skills will be added here");
        guiGraphics.drawCenteredString(this.font, placeholder1,
                this.leftPos + this.imageWidth / 2,
                this.topPos + this.imageHeight / 2 - 10,
                0xFFFFFFFF);

        Component placeholder2 = Component.literal("(Menu is currently empty)");
        guiGraphics.drawCenteredString(this.font, placeholder2,
                this.leftPos + this.imageWidth / 2,
                this.topPos + this.imageHeight / 2 + 10,
                0xFFAAAAAA);

        // Информация о том, что игрок использовал Behelit
        Component behelitInfo = Component.literal("You have used the Behelit...");
        guiGraphics.drawCenteredString(this.font, behelitInfo,
                this.leftPos + this.imageWidth / 2,
                this.topPos + this.imageHeight - 30,
                0xFFFF6B6B);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Не ставим игру на паузу
    }
}