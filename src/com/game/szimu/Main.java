package com.game.szimu;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static com.game.szimu.Music.musicPlay;

/**
 * Start the game
 *  The call to startGame() in the model starts
 *    the actual play of the game
 *  Note: Many issues of mutual exclusion on shared variables
 *        are ignored.
 * @author Mike Smith University of Brighton
 */
public class Main
{
    public static final int H = 800; // Height of window
    public static final int W = 600; // Width of window
    public static File music1 = new File("src/music1.wav");


    public static void main( String args[] )
    {
        Debug.trace("BreakOut");
        Debug.set( true );              // Set true to get debug info

        Model model = new Model(W,H);   // model of the Game
        View  view  = new View(W,H);    // View of the Game
        // try catch for background
        try {
            //its on relative path, need src!
            view.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File("src/bg2br.png"/*"/Users/sneakysneak/Library/Mobile Documents/com~apple~CloudDocs/JAVA/CourseWork/GameMiniProj2.iml/src"*/)))));
        } catch (IOException e) {
            System.out.println("No pic, try path, not relative");
        }
        new Controller( model, view );

        model.createGameObjects();       // Ball, Bat & Bricks
        model.addObserver( view );       // Add observer to the model

        view.setResizable(false); // for background
        view.setVisible(true);           // Make visible
        model.startGame();               // Start playing the game
        model.moveBat(5);
        model.createGameObjects();
        musicPlay(music1);
        model.isPaused();
        model.pauseGame();
        view.isPaused();
    }
}
