package com.exemplo.pinv;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "pinv", name = "Pinv Tools", version = "1.0.0", clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class Pinv {
    @EventHandler
    public void init(FMLInitializationEvent e) {
        Config.load();
        Keys.register();
        MinecraftForge.EVENT_BUS.register(new Events());
    }
}
