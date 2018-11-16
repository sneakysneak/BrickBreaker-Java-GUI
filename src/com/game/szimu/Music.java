package com.game.szimu;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Music {
    public static Clip clip;


    public static void musicStop() {
        clip.stop();
    }

    static void musicPlay(File music) {
        try {
                clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(music));
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch(Exception e) {
            System.out.println("No file jaaj");
        }
    }

    static void soundPlay(File music) {
        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(music));
            clip.start();
        } catch(Exception e) {
            System.out.println("No file jaaj");
        }
    }
}
