package com.exemplo.pinv.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keys {
    public static final String CAT = "key.categories.pinv";
    public static KeyBinding OPEN;
    public static KeyBinding INVIS;

    public static void register() {
        OPEN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pinv.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, CAT));
        INVIS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.pinv.toggle_invisible", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, CAT));
    }
}
