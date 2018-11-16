package com.game.szimu;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.security.Key;

import static com.game.szimu.Music.musicPlay;
import static com.game.szimu.Music.musicStop;

/**
 * BreakOut controller, handles user interactions
 * @author Mike Smith University of Brighton
 */
public class Controller
{
    private Model model;   // Model of game
    private View  view;    // View of game
    private static Music music;
    public static File music2 = new File("src/music2.wav");

    public Controller(Model aBreakOutModel,
                      View aBreakOutView )
    {
        model  = aBreakOutModel;
        view   = aBreakOutView;
        view.setController( this );    // View could talk to controller
    }

    /**
     * Decide what to do for each interaction from the user
     * Called from the interaction code in the view
     * @param keyCode The key pressed
     */
    public void userKeyInteraction(int keyCode )
    {
        // Key typed includes specials, -ve
        // Char is ASCII value
        switch ( keyCode )               // Character is
        {
            case -KeyEvent.VK_LEFT:        // Left Arrow
                model.moveBat( -1);
                break;
            case -KeyEvent.VK_RIGHT:       // Right arrow
                model.moveBat( +1 );
                break;
            case 'f' :
                // Very fast ball movement now
                model.setFast( true );
                break;
            case 'n' :
                // Normal speed
                model.setFast( false );
                break;
            case -KeyEvent.VK_SPACE:
                model.resetGame(); //muszaj resetelni mert neha fura, olyan mintha continuznank, hiaba van leallitva es ujra...
                view.setGameOn(true);
                musicStop();
                musicPlay(music2);
                break; //kis buzerans muszaj BREAK!!!!!!! es set pesze
            case -KeyEvent.VK_ESCAPE:
                model.isPaused();
                model.pauseGame();
                view.isPaused();
                System.out.println("PAUSEEEED");
                break;
            case -KeyEvent.VK_R:
                if (view.isVictory() || view.isGameOver()) {
                    model.resetGame();
                    musicStop();
                    break; //Use break!!! aaand where the static music "goes" there u gotta stop it
//                    musicPlay(music2);
                }
            case -KeyEvent.VK_C:
                if (view.isGameOver() || view.isVictory()) {
                    model.continueGame();
                    musicStop();
                    break;
//                    musicPlay(music2);
                }
            default :
                Debug.trace( "Ch typed = %3d [%c]", keyCode, (char) keyCode );
        }
    }
}
