package com.github.cfpaorg.defaultcharsethack;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import org.apache.logging.log4j.Logger;
import org.lwjgl.openal.AL;

import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

public class SoundSystemReloader {
    public static final Logger logger = DefaultCharsetHack.logger;
    
    private SoundManager soundManager;
    private Charset originCharset;
    private Charset targetCharset;

    SoundSystemReloader(SoundManager soundManager, Charset originCharset, Charset targetCharset){
        this.soundManager=soundManager;
        this.originCharset=originCharset;
        this.targetCharset=targetCharset;
    }
    
    @SideOnly(Side.CLIENT)
    public void reload() {
        if(AL.isCreated()) {
            return;
        }
        logger.info("重新加载声音系统");
        Field charset = null;
        try {
            System.setProperty("file.encoding", originCharset.name());
            charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
            logger.info(Charset.defaultCharset().name());
        } catch (Exception e) {
        }
        try {
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
        } catch (Exception e) {
        }
        soundManager.reloadSoundSystem();
        try {
            while(!AL.isCreated()) {
                Thread.sleep(50);
            }
        } catch (Exception e) {
        }
        try {
            System.setProperty("file.encoding", targetCharset.name());
            charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null, null);
            logger.info(Charset.defaultCharset().name());
            charset.setAccessible(false);
        } catch (Exception e) {
        }
    }

}
