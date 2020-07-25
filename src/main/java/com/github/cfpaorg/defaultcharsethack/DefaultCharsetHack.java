package com.github.cfpaorg.defaultcharsethack;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    public static final String VERSION = "@VERSION@";

    public static final Logger logger = LogManager.getLogger(MOD_ID);

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        try {
            if(Charset.defaultCharset().equals(Charset.forName("utf-8"))){
                logger.info("当前默认字符集已经是UTF-8");
                return;
            }
        } catch (Exception e) {
            logger.error("当前系统不支持UTF-8字符集：", e);
        }
        try {
            logger.info("尝试修改默认字符集");
            System.setProperty("file.encoding", "UTF-8");
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
            // Success
            logger.info("默认字符集修改成功");
        } catch (Exception e) {
            // Fail
            logger.error("默认字符集修改失败：", e);
        }
    }
}
