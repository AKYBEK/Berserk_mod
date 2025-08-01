package net.akul.berserkmod.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CharacterScreen extends Screen {
    private static final ResourceLocation BACKGROUND_TEXTURE = 
            new ResourceLocation("berserkmod", "textures/gui/character_menu.png");
    
    private final int imageWidth = 176;
    private final int imageHeight = 166;
    private int leftPos;
    private int topPos;

    public CharacterScreen() {
        super(Component.translatable("gui.berserkmod.character_menu"));
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
        
        // Рендерим серый фон
        guiGraphics.fill(this.leftPos, this.topPos, 
                        this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 
                        0xFF808080); // Серый цвет
        
        // Рендерим границу
        guiGraphics.fill(this.leftPos - 1, this.topPos - 1, 
                        this.leftPos + this.imageWidth + 1, this.topPos + this.imageHeight + 1, 
                        0xFF000000); // Черная граница
        
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Заголовок меню
        Component title = Component.translatable("gui.berserkmod.character_menu");
        guiGraphics.drawCenteredString(this.font, title, 
                                     this.leftPos + this.imageWidth / 2, 
                                     this.topPos + 10, 
                                     0xFFFFFF);
        
        // Временный текст
        Component placeholder = Component.literal("Character Development Menu");
        guiGraphics.drawCenteredString(this.font, placeholder, 
                                     this.leftPos + this.imageWidth / 2, 
                                     this.topPos + this.imageHeight / 2, 
                                     0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Не ставим игру на паузу
    }
}