package com.game.szimu;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import static com.game.szimu.Music.musicStop;
import static com.game.szimu.Music.soundPlay;
/**
 * Model of the game of breakout
 * @author Mike Smith University of Brighton
 */

public class Model extends Observable
{
    // Boarder
    private static final int B              = 6;  // Border offset
    private static final int M              = 40; // Menu offset

    // Size of things
    private static final float BALL_SIZE    = 30; // Ball side
    private static final float BRICK_WIDTH  = 50; // Brick size
    private static final float BRICK_HEIGHT = 30;

    private static final int BAT_MOVE       = 15; // Distance to move bat

    // Scores
    private static final int HIT_BRICK1      = 100;  // Score
    private static final int HIT_BRICK2      = 50;  // Score
    private static final int HIT_BOTTOM     = -200;// Score

    private GameObj ball;          // The ball
    private List<GameObj> bricks1;  // The bricks
    private List<GameObj> bricks2;  // The bricks
    private GameObj bat;           // The bat


    private boolean runGame = true; // Game running
    private boolean isPaused = false;
    private boolean fast = false;   // Sleep in run loop
    private boolean falling = false;
    private boolean bonus = false;


    private int score = 0;
    public int balls = 3;

//    public int minVelocity, maxVelocity;

    private final float W;         // Width of area
    private final float H;         // Height of area

    public static final int NUM_OF_BRICKS1 = 45;
    public static final int NUM_OF_BRICKS2 = 45;
    private int countAllBricks = 90;
    private Brick brick1[];
    private Brick brick2[];

    public static File sound1 = new File("src/sound1.wav");
    public static File sound2 = new File("src/sound2.wav");
    public static File sound3 = new File("src/sound3.wav");
    public static File sound4 = new File("src/sound4.wav");
    public static File sound5 = new File("src/sound5.wav");

    public Model( int width, int height)
    {
        this.W = width; this.H = height;
    }

    /**
     * Create in the model the objects that form the game
     */
    public void createGameObjects()
    {
        synchronized( Model.class )
        { //-75 puts it to the middle, the whole thingy is 450 (needs to prevent go beyond border)
            ball   = new GameObj(W/2, H/2/*+340*/, BALL_SIZE/2, BALL_SIZE/2, Colour.RED);
            bat    = new GameObj(W/2, /*790*/H - BRICK_HEIGHT*1.5f, BRICK_WIDTH*/*20*/3,
                    BRICK_HEIGHT/4/*7.5 height*/, Colour.GRAY);
            brick1 = new Brick[NUM_OF_BRICKS1];
            int k = 0;
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 9; j++) {
                    brick1[k] = new Brick(j * 55 + 50, i * 25 + 150, BRICK_WIDTH, BRICK_HEIGHT - 10, Colour.CYAN);
                    k++;
                }
            }
            brick2 = new Brick[NUM_OF_BRICKS2];
            int m = 0;
            for (int n=0; n < 5; n++) {
                for (int o=0; o < 9; o++) {
                    brick2[m] = new Brick(o * 55 + 50, n * 25 + 275, BRICK_WIDTH, BRICK_HEIGHT - 10, Colour.ORANGE);
                    m++;
                }
        }
            bricks1 = new ArrayList<>();
            for (GameObj gameObj1 : brick1) {
                bricks1.add(gameObj1);
            }
            bricks2 = new ArrayList<>();
            for (GameObj gameObj2 : brick2) {
                bricks2.add(gameObj2);
            }
            // *[1]******************************************************[1]*
            // * Fill in code to place the bricks on the board              *
            // **************************************************************
        }
    }

    private ActivePart active  = null;

    /**
     * Start the continuous updates to the game
     */
    public void startGame()
    {
        synchronized ( Model.class )
        {
            stopGame();
            active = new ActivePart();
            Thread t = new Thread( active::runAsSeparateThread );
            t.setDaemon(true);   // So may die when program exits
            t.start();
        }
    }

    /**
     * Stop the continuous updates to the game
     * Will freeze the game, and let the thread die.
     */
    public void stopGame()
    {
        synchronized ( Model.class )
        {
            if ( active != null ) { active.stop(); active = null; }
        }
    }

    public void resetGame() {
        createGameObjects();
        startGame();
        balls = 3;
        countAllBricks = 90;
        score = 0;
    }
    //vmiert 3. nyomasra lefagy
    public void continueGame() {
//        musicStop();
        startGame();
        balls = 3;
    }

    public void pauseGame() {
        if (isPaused == true) {
            stopGame();
        } else {
            startGame();
        }
    }

    public boolean isPaused() {

        isPaused = !isPaused;
        return isPaused;
    }

    public boolean isBonus() {
        int bricksDestroyed = getCountAllBricks();
        if (bricksDestroyed > 3 )
            return bonus;
        else return !bonus;
    }

    public boolean isFalling() {
        float y = ball.getY();
        float x = bat.getY();
        if ( y < x - 10 )
            return falling;
        else return !falling;
    }


    public GameObj getBat()             { return bat; }

    public GameObj getBall()            { return ball; }

    public List<GameObj> getBricks1()    { return bricks1; }

    public List<GameObj> getBricks2()    { return bricks2; }

    public int getCountAllBricks() {
        return countAllBricks;
    }

    /**
     * Add to score n units
     * @param n units to add to score
     */
    protected void addToScore(int n)    { score += n; }

    public int getScore()               { return score; }

    public int getBalls() {
        return balls;
    }

    /**
     * Set speed of ball to be fast (true/ false)
     * @param fast Set to true if require fast moving ball
     */
    public void setFast(boolean fast)
    {
        this.fast = fast;
    }

    /**
     * Move the bat. (-1) is left or (+1) is right
     * @param direction - The direction to move
     *                  450a hossz x
     */
    public void moveBat( int direction )
    {
        // *[2]******************************************************[2]*
        // * Fill in code to prevent the bat being moved off the screen *
        // **************************************************************
        /* - keep tracking of x(,y) coordinates of the bat
        *  - ensure x + width of the rectangle is not greater than width of JFrame
        *  for checking the collision with right edge
        *  - Ensure x is not less than 0 to check the collision of the left edge*/
        // GameObj class parameter x?? elvileg az a coordinate
        float dist = direction * BAT_MOVE;    // Actual distance to move
        Debug.trace( "Model: Move bat = %6.2f", dist);
        bat.moveX(dist);
//        float x = bat.getX(); //ezaz kis
//        System.out.println(BAT_MOVE + " " + dist + " " + direction + " " + x);
        if (bat.getX() >= 450) {
            bat.moveX(-15);
        } else if (bat.getX() <= 0) {
            bat.moveX(+15);
        }
    }

    /**
     * This method is run in a separate thread
     * Consequence: Potential concurrent access to shared variables in the class
     */
    class ActivePart
    {
        private boolean runGame = true;

        public void stop()
        {
            runGame = false;
        }

        public void runAsSeparateThread()
        {
            final float S = 3; // Units to move (Speed) Ball
            try {//mi van ha ide rakjuk a sima brick-et es talan lehet life 2
                synchronized (Model.class) // Make thread safe
                {
                    GameObj ball = getBall();     // Ball in game
                    GameObj bat = getBat();      // Bat
                    List<GameObj> bricks1 = getBricks1();   // Bricks
                    List<GameObj> bricks2 = getBricks2();   // Bricks
                }

                while (runGame) {
                    synchronized (Model.class) // Make thread safe
                    {
                        float x = ball.getX();  // Current x,y position
                        float y = ball.getY();
                        // Deal with possible edge of board hit
                        if (x >= W - B - BALL_SIZE) {
                            ball.changeDirectionX();
                            soundPlay(sound3);
                        }

                        if (x <= 0 + B) {
                            ball.changeDirectionX();
                            soundPlay(sound3);
                        }
                        if (y >= H - B - BALL_SIZE + 30/*+ 17*/)   // Bottom, be nice when falls down
                        {
                            soundPlay(sound2);
                            balls--;
                            if (balls == 0) {
                                ball.setVisibility(false);
                                stopGame();
                                musicStop();
                                soundPlay(sound5);
                            } else {
                                ball = new GameObj(W / 2, H / 2 /*+ 340*/, BALL_SIZE / 2, BALL_SIZE / 2, Colour.GREEN);
                            }
                        }
                        if (y <= 0 + M) ball.changeDirectionY();
                        // As only a hit on the bat/ball is detected it is
                        //  assumed to be on the top or bottom of the object.
                        // A hit on the left or right of the object
                        //  has an interesting affect
                        boolean hit = false;
                        // *[3]******************************************************[3]*
                        // * Fill in code to check if a visible brick has been hit      *
                        // *      The ball has no effect on an invisible brick          *
                        // **************************************************************
                        GameObj[] gameObjs1 = bricks1.toArray(new GameObj[bricks1.size()]);
                        for (GameObj aGameObjs1 : gameObjs1) {
                            if (ball.hitBy(aGameObjs1) && aGameObjs1.isVisible()) {
                                ball.changeDirectionY();
                                aGameObjs1.setBrickLife2(aGameObjs1.getBrickLife2() - 1);
                                addToScore(HIT_BRICK1);
                                addToScore(HIT_BRICK2);
                                soundPlay(sound1);
                                System.out.println("life: " + aGameObjs1.getBrickLife2() + " ! " + getCountAllBricks());
                                if (aGameObjs1.getBrickLife2() <= 0) {
                                    //put count bricks here, otherwise can't be precise WIN
                                    countAllBricks--;
                                    aGameObjs1.setVisibility(false);
                                }
                            }
                        }
                        GameObj[] gameObjs2 = bricks2.toArray(new GameObj[bricks2.size()]);
                        for (GameObj aGameObjs2 : gameObjs2) {
                            if (ball.hitBy(aGameObjs2) && aGameObjs2.isVisible()) {
                                ball.changeDirectionY();
                                addToScore(HIT_BRICK1);
                                addToScore(HIT_BRICK2);
                                countAllBricks--;
                                aGameObjs2.setVisibility(false);
                                soundPlay(sound1);
                            }
                        }

                        if (hit)
                            ball.changeDirectionY();
                        if (isFalling())
                            bat.setVisibility(false);
                        else if (ball.hitBy(bat)) {
                            ball.changeDirectionY();
                            soundPlay(sound4);
                        }
                    }
                    modelChanged();      // Model changed refresh screen
                    Thread.sleep( fast ? 2 : 20 );
                    ball.moveX(S);  ball.moveY(S);
                }
            } catch (Exception e)
            {
                Debug.error("Model.runAsSeparateThread - Error\n%s",
                        e.getMessage() );
            }
        }
    }

    /**
     * Model has changed so notify observers so that they
     *  can redraw the current state of the game
     */
    public void modelChanged()
    {
        setChanged(); notifyObservers();
    }
}