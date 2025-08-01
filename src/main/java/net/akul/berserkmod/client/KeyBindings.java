package net.akul.berserkmod.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String KEY_CATEGORY_BERSERK = "key.category.berserkmod.berserk";
    public static final String KEY_OPEN_CHARACTER_MENU = "key.berserkmod.open_character_menu";

    public static final KeyMapping OPEN_CHARACTER_MENU_KEY = new KeyMapping(
            KEY_OPEN_CHARACTER_MENU,
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            KEY_CATEGORY_BERSERK
    );
}