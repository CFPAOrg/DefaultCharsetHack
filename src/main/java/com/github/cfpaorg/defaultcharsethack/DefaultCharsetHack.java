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
        String targetCharsetName = "UTF-8";
        Charset targetCharset = null;
        try {
            targetCharset = Charset.forName(targetCharsetName);
        } catch (Exception e) {
            logger.error(String.format("当前Java虚拟机不支持%s字符集：", targetCharsetName), e);
            return;
        }
        Charset originCharset = Charset.defaultCharset();
        if(originCharset.equals(targetCharset)){
            logger.info(String.format("当前默认字符集已经是%s", targetCharsetName));
            return;
        }
        Field charset = null;
        try {
            logger.info(String.format("当前默认字符集为%s, 尝试修改为%s", originCharset.name(), targetCharset.name()));
            System.setProperty("file.encoding", targetCharsetName);
            charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
            if(Charset.defaultCharset().equals(targetCharset)){
                // Success
                logger.info("默认字符集修改成功");
                charset.setAccessible(false);
                return;
            } else {
                logger.error("默认字符集修改失败");
            }
        } catch (Exception e) {
            // Fail
            logger.error("默认字符集修改失败：", e);
        }
        try {
            // Do some cleaning
            charset.set(null, originCharset);
            charset.setAccessible(false);
        } catch (Exception e) {
            logger.error("复原过程中发生错误：", e);
        }
    }
}
