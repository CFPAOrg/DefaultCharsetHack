package com.github.cfpaorg.defaultcharsethack;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

@Mod(modid = DefaultCharsetHack.MOD_ID,
        name = DefaultCharsetHack.MOD_NAME,
        acceptedMinecraftVersions = "[1.12]",
        version = DefaultCharsetHack.VERSION,
        dependencies = "before:*")
public class DefaultCharsetHack {
    public static final String MOD_ID = "defaultcharsethack";
    public static final String MOD_NAME = "Default Charset Hack";
    public static final String VERSION = "1.0.0";

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        try {
            // Success
            System.setProperty("file.encoding", "UTF-8");
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
        } catch (Exception e) {
            // Fail
        }
    }
}
