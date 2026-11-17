package com.exemplo.pinv;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class Keys {
    public static final String CAT = "Pinv Tools";
    public static final KeyBinding OPEN = new KeyBinding("Abrir menu do mod", Keyboard.KEY_K, CAT);
    public static final KeyBinding INVIS = new KeyBinding("Ligar/desligar ver invisiveis", Keyboard.KEY_NONE, CAT);
    public static final KeyBinding APPLE = new KeyBinding("Ligar/desligar alerta da maca", Keyboard.KEY_NONE, CAT);
    public static final KeyBinding PINK = new KeyBinding("Ligar/desligar rosa (fim da regeneracao)", Keyboard.KEY_NONE, CAT);

    public static void register() {
        ClientRegistry.registerKeyBinding(OPEN);
        ClientRegistry.registerKeyBinding(INVIS);
        ClientRegistry.registerKeyBinding(APPLE);
        ClientRegistry.registerKeyBinding(PINK);
    }
}
