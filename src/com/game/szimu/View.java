package com.game.szimu;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import java.util.*;
import java.util.List;
import java.awt.Color;

import static com.game.szimu.Music.musicStop;

/**
 * Displays a graphical view of the game of breakout.
 *  Uses Graphics2D would need to be re-implemented for Android.
 * @author Mike Smith University of Brighton
 */
public class View extends JFrame implements Observer {
    private Controller controller;
    private GameObj bat;            // The bat
    private GameObj ball;           // The ball
    private List<GameObj> bricks1;     // The bricks
    private List<GameObj> bricks2;     // The bricks
    private int score = 0;     // The score
    private int frames = 0;     // Frames output
    private int balls = 3;      //working with private too
    private int countAllBricks = 90;
    private boolean gameOn = false;
    private boolean paused = false;
    public final int width;  // Size of screen Width
    public final int height;  // Size of screen Height
    boolean isOff = true;

    Model model;

    /**
     * Construct the view of the game
     *
     * @param width  Width of the view pixels
     * @param height Height of the view pixels
     */

    public View(int width, int height) {
        this.width = width;
        this.height = height;
        setSize(width, height);                 // Size of window
        addKeyListener(new Transaction());    // Called when key press
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Timer.startTimer();
    }

    public boolean isGameOver() {
        return /*isWin() ||*/ balls == 0;
    }

    public boolean isVictory() {
        return countAllBricks == 0;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setGameOn(boolean gameOn) {
        this.gameOn = gameOn;
    }

//    public void setPaused(boolean paused) {
//        this.paused = paused;
//    }
    /**
     *  Code called to draw the current state of the game
     *   Uses draw:       Draw a shape
     *        fill:       Fill the shape
     *        setPaint:   Colour used
     *        drawString: Write string on display
     *  @param g Graphics context to use
     */
    public void drawActualPicture( Graphics2D g ) {
        final int RESET_AFTER = 200; // Movements
        frames++;
        synchronized (Model.class)   // Make thread safe
        {
            if (!gameOn) {
                super.paintComponents(g);
                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(Font.BOLD, 30));
//                drawString(g, "Press Space to Start", 140, height / 2);
                synchronized (Timer.class) {
                    javax.swing.Timer timer1 = new javax.swing.Timer(100, new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            isOff = !isOff;
                            repaint();
                        }
                    });
                    timer1.setRepeats(true);
                    timer1.setCoalesce(true);
                    timer1.start();
                    if (!isOff) {
                        drawString(g, "Press Space to Start", 140, height / 2);
                    } else {
                        g.setColor(Color.LIGHT_GRAY);
                        drawString(g, "Pre s Space to S art", 135, height / 2 - 10);
                    }
                }
                //a 2 boolean methodnak EGYUTT kell lennie h mukodjon egyik, masik...
            } else if (!isGameOver() && !isVictory()) {
//                System.out.println(countAllBricks + " " + score);
                g.setPaint(Color.BLACK.brighter());
                super.paintComponents(g);
                g.fill(new Rectangle2D.Float(0, 0, width, height));
                Font font1 = new Font("Monospaced", Font.BOLD, 24);
                g.setFont(font1);
//                displayGameObj(g, ball);
                displayBall(g, ball);
                displayGameObj(g, bat);   // Display the Bat
                // *[4]****************************************************[4]*
                // * Display the bricks that make up the game                 *
                // * Fill in code to display bricks                           *
                // * Remember only a visible brick is to be displayed         *
                // ************************************************************
                //ez a 4es jo!
                for (GameObj brick1 : bricks1) {
                    if (null != brick1 && brick1.isVisible()) {
//                        displayColouredBricks(g, brick1);
                        displayGameObj(g, brick1);
                        brick1.setVisibility(true); //invisible a labda szamara(atmegy rajta) es mar nem is latszik
                    }
                }
                for (GameObj brick2 : bricks2) {
                    if (null != brick2 && brick2.isVisible()) { //na vaj ez jo?
                        displayGameObj(g, brick2);
                        brick2.setVisibility(true); //invisible a labda szamara(atmegy rajta) es mar nem is latszik
                    }
                }
                // Display state of game
                g.setPaint(Color.WHITE);
                FontMetrics fm1 = getFontMetrics(font1);
                String fmt1 = "Balls: %1d Score = [%6d]  fps=%5.1f";
                String text1 = String.format(fmt1, balls, score,
                        frames / (Timer.timeTaken() / 1000.0)
                );
                if (frames > RESET_AFTER) {
                    frames = 0;
                    Timer.startTimer();
                }
                g.drawString(text1, width / 2 - fm1.stringWidth(text1) / 2, 80);

            } else if (isGameOver()) {
//                musicStop();
                g.fill(new Rectangle2D.Float(0, 0, width, height));
                super.paintComponents(g);
                g.setPaint(Color.LIGHT_GRAY);
                g.setFont(g.getFont().deriveFont(Font.BOLD, 22));
                drawString(g, "Continue?       Press c\nStart new Game? Press r ", 280, 725);
                g.setPaint(Color.BLACK.brighter());
                g.setFont(g.getFont().deriveFont(Font.BOLD, 50));
                drawString(g, "GAME OVER", 80, height/2);
                g.setPaint(Color.LIGHT_GRAY);
                g.setFont(g.getFont().deriveFont(Font.BOLD, 25));
                drawString(g, "Score: " + score + " Bricks left: " + countAllBricks, 60, 600);

            } else if (isVictory()) {
//                musicStop();
                g.fill(new Rectangle2D.Float(0, 0, width, height));
                super.paintComponents(g);
                g.setPaint(Color.LIGHT_GRAY);
                g.setFont(g.getFont().deriveFont(Font.BOLD, 22));
                drawString(g, "Start new Game? Press r ", 120, 725);
                g.setPaint(Color.BLACK.brighter());
                g.setFont(g.getFont().deriveFont(Font.BOLD, 50));
                drawString(g, "VICTORY" /*+ hitCount + " " + brickLife*/, 80, height/2);
                g.setPaint(Color.LIGHT_GRAY);
                g.setFont(g.getFont().deriveFont(Font.BOLD, 25));
                drawString(g, "Score: " + score + " Bricks left: " + countAllBricks, 60, 600);

            } else if (paused) {
                g.fill(new Rectangle2D.Float(0, 0, width, height));
                super.paintComponents(g);
                g.setPaint(Color.GRAY.brighter());
                drawString(g, "Press esc key to continue", 340, 600);
                g.setFont(g.getFont().deriveFont(Font.BOLD, 40));
                drawString(g, "PAUSED", 80, height/2);
                g.setPaint(Color.LIGHT_GRAY);
                g.setFont(g.getFont().deriveFont(Font.BOLD, 25));
                drawString(g, "Score: " + score + " Bricks left: " + countAllBricks, 60, 600);
            }
        }
    }
    //Method to be able to draw new line strings with different size/ font type etc
    private void drawString(Graphics g, String text, int x, int y) {
        for (String line : text.split("\n"))
            g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
//Colors!
    public Color randomColor() {
        Random random = new Random();
        int red=random.nextInt(256);
        int green=random.nextInt(256);
        int blue=random.nextInt(256);
        return new Color(red, green, blue);
    }

    private void displayColouredBricks( Graphics2D g, GameObj go) {
        g.setColor(randomColor());
        g.fill(new Rectangle2D.Float(go.getX(), go.getY(),
                go.getWidth(), go.getHeight()));
    }

    private void displayGameObj( Graphics2D g, GameObj go )
    {
//        repaint();
//        if (model.isBonus()) {
//            g.setColor(randomColor());
//        } else {
            g.setColor(go.getColour().forSwing());
//        }
        g.fill(new Rectangle2D.Float(go.getX(), go.getY(),
                    go.getWidth(), go.getHeight()));
    }

    private void displayBall( Graphics2D g, GameObj go) {
        g.setColor(go.getColour().forSwing());
        //EZ a legfontosabb resz, nem csupan Oval v egyeb szarsag, ELLIPSE2D!!!!!!!!!!
        g.fill(new Ellipse2D.Float(go.getX(), go.getY(), go.getWidth(), go.getHeight()));
    }

    /**
     * Called indirectly from the model when its state has changed
     * @param aModel Model to be displayed
     * @param arg    Any arguments (Not used)
     */
    @Override
    public void update( Observable aModel, Object arg )
    {
        Model model = (Model) aModel;
        // Get from the model the ball, bat, bricks & score
        ball    = model.getBall();              // Ball
        bricks1  = model.getBricks1();            // Bricks
        bricks2  = model.getBricks2();            // Bricks
        bat     = model.getBat();               // Bat
        score   = model.getScore();             // Score
        balls   = model.getBalls();
        countAllBricks = model.getCountAllBricks();
        //Debug.trace("Update");
        repaint();                              // Re draw game
    }

    /**
     * Called by repaint to redraw the Model
     * @param g    Graphics context
     */
    @Override
    public void update( Graphics g )          // Called by repaint
    {
        drawPicture( (Graphics2D) g );          // Draw Picture
    }

    /**
     * Called when window is first shown or damaged
     * @param g    Graphics context
     */
    @Override
    public void paint( Graphics g )           // When 'Window' is first
    {                                         //  shown or damaged
        drawPicture( (Graphics2D) g );          // Draw Picture
    }

    private BufferedImage theAI;              // Alternate Image
    private Graphics2D    theAG;              // Alternate Graphics

    /**
     * Double buffer graphics output to avoid flicker
     * @param g The graphics context
     */
    //elvileg ide kell, a masik override- os
    private void drawPicture( Graphics2D g )   // Double buffer
    {                                          //  to avoid flicker
        if ( bricks1 == null ) return;            // Race condition
        if ( bricks2 == null ) return;            // Race condition
        if (  theAG == null )
        {
            Dimension d = getSize();              // Size of curr. image
            theAI = (BufferedImage) createImage( d.width, d.height );
            theAG = theAI.createGraphics();
        }
        drawActualPicture( theAG );             // Draw Actual Picture
        g.drawImage( theAI, 0, 0, this );       //  Display on screen
    }

    /**
     * Need to be told where the controller is
     * @param aPongController The controller used
     */
    public void setController(Controller aPongController)
    {
        controller = aPongController;
    }

    /**
     * Methods Called on a key press
     *  calls the controller to process
     */
    private class Transaction implements KeyListener  // When character typed
    {
        @Override
        public void keyPressed(KeyEvent e)      // Obey this method
        {
            // Make -ve so not confused with normal characters
            controller.userKeyInteraction( -e.getKeyCode() );
        }

        @Override
        public void keyReleased(KeyEvent e)
        {
            // Called on key release including specials
        }

        @Override
        public void keyTyped(KeyEvent e)
        {
            // Send internal code for key
            controller.userKeyInteraction( e.getKeyChar() );
        }
    }
}
