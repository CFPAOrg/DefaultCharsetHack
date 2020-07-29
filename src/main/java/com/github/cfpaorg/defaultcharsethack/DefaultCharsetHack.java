package com.github.cfpaorg.defaultcharsethack;

import net.minecraftforge.client.event.sound.SoundSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    private static Charset originCharset = Charset.defaultCharset();
    private static Charset targetCharset = null;
    private SoundSystemReloader soundSystemReloader = null;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        String targetCharsetName = "UTF-8";
        try {
            targetCharset = Charset.forName(targetCharsetName);
        } catch (Exception e) {
            logger.error(String.format("当前Java虚拟机不支持%s字符集：", targetCharsetName), e);
            return;
        }

        if (originCharset.equals(targetCharset)) {
            logger.info(String.format("当前默认字符集已经是%s", targetCharsetName));
            return;
        }
//        TODO 检查游戏路径是否包含非ASCII字符
//        String gamePath = ;
//        if(!StandardCharsets.US_ASCII.newEncoder().canEncode(gamePath))
        Field charset = null;
        try {
            logger.info(String.format("当前默认字符集为%s, 尝试修改为%s", originCharset.name(), targetCharset.name()));
            System.setProperty("file.encoding", targetCharsetName);
            charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
            if (Charset.defaultCharset().equals(targetCharset)) {
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

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new Object() {
            @SideOnly(Side.CLIENT)
            @SubscribeEvent
            public void onSoundSetup(SoundSetupEvent event) {
                logger.info("即将加载客户端声音系统，若文件路径存在中文可能出现问题，将会在游戏加载完成后尝试按系统编码重新加载声音系统");
                soundSystemReloader = new SoundSystemReloader(event.getManager(), originCharset, targetCharset);
//                try {
//                    //强制在之后手动加载OpenAL
//                    SoundSystemConfig.removeLibrary(LibraryLWJGLOpenAL.class);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
    }

    @Mod.EventHandler
    public void loadFinished(FMLLoadCompleteEvent event) {
        if (soundSystemReloader != null)
            soundSystemReloader.reload();
    }
}
