import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
    
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 100;

    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];

    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    // Game States
    enum GameState {
        MENU,
        PLAYING,
        GAME_OVER
    }
    GameState gameState = GameState.MENU;

    int highScore = 0;


    // Image variables
    BufferedImage appleImage;
    BufferedImage headUp, headDown, headLeft, headRight;
    BufferedImage bodyVerticle, bodyHorizontal;
    BufferedImage bodyTopLeft, bodyTopRight, bodyBottomLeft, bodyBottomRight;
    BufferedImage tailUp, tailDown, tailLeft, tailRight;

    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.addMouseListener(new MyMouseAdapter());

        timer = new Timer(DELAY, this);

        // load images
        loadImages();
    }

    private void loadImages() {
        try {
            appleImage = ImageIO.read(new File("Graphics/apple.png"));

            headUp = ImageIO.read(new File("Graphics/head_up.png"));
            headDown = ImageIO.read(new File("Graphics/head_down.png"));
            headRight  = ImageIO.read(new File("Graphics/head_right.png"));
            headLeft  = ImageIO.read(new File("Graphics/head_left.png"));

            bodyVerticle = ImageIO.read(new File("Graphics/body_verticle.png"));
            bodyHorizontal = ImageIO.read(new File("Graphics/body_horizontal.png"));

            bodyTopLeft = ImageIO.read(new File("Graphics/body_topleft.png"));
            bodyTopRight = ImageIO.read(new File("Graphics/body_topright.png"));
            bodyBottomLeft = ImageIO.read(new File("Graphics/body_bottomleft.png"));
            bodyBottomRight = ImageIO.read(new File("Graphics/body_bottomright.png"));

            tailUp = ImageIO.read(new File("Graphics/tail_up.png"));
            tailDown = ImageIO.read(new File("Graphics/tail_down.png"));
            tailLeft = ImageIO.read(new File("Graphics/tail_left.png"));
            tailRight = ImageIO.read(new File("Graphics/tail_right.png"));

            System.out.println("All Images Loaded Successfully");
        } catch (IOException e) {
            System.out.println("Error loading images" + e.getMessage());
            System.out.println("Make sure the Graphics folder is in the same dir as your .java files");
        }
    }

    public void startGame() {
    // Reset game variables
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';

        // Reset snake position
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        
        newApple();
        running = true;
        gameState = GameState.PLAYING;
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (gameState) {
            case MENU:
                drawMenu(g);
                break;
                case PLAYING:
                draw(g);
                break;
                case GAME_OVER:
                gameOver(g);
        }
    }

// Draw Menu
public void drawMenu(Graphics g) {
    // Title
    g.setColor(Color.green);
    g.setFont(new Font("Ink Free", Font.BOLD, 75));
    FontMetrics metrics1 = getFontMetrics(g.getFont());
    g.drawString("Snake Game", (SCREEN_WIDTH - metrics1.stringWidth("Snake Game")) / 2, 150);

// Play button
    g.setColor(Color.green);
    g.fillRect(200, 250, 200, 60);
    g.setColor(Color.black);
    g.setFont(new Font("Ink Free", Font.BOLD, 40));
    FontMetrics metrics2 = getFontMetrics(g.getFont());
    g.drawString("Play", (SCREEN_WIDTH - metrics2.stringWidth("PLAY")) / 2, 295);

// High Score
    g.setColor(Color.yellow);
    g.setFont(new Font("Ink Free", Font.BOLD, 30));
    FontMetrics metrics3 = getFontMetrics(g.getFont());
    g .drawString("High Score: " + highScore, (SCREEN_WIDTH - metrics3.stringWidth("High Score: " + highScore)) / 2, 400);

// Instructions
    g .setColor(Color.white);
    g.setFont(new Font("Ink Free", Font.PLAIN, 20));
    FontMetrics metrics4 = getFontMetrics(g.getFont());
    g.drawString("Use arrow keys to move", (SCREEN_WIDTH - metrics4.stringWidth("Use arrow keys to move")) / 2, 500);
}
public void draw(Graphics g) {
        if (running) {
            // Draw Grid , optional for debugging
            
            /* 
            for (int i = 0; i < SCREEN_HEIGHT/ UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            } */

            // Draw apple

            if (appleImage != null) {
                g.drawImage(appleImage, appleX, appleY, UNIT_SIZE, UNIT_SIZE, this);
            } else {
                // Fall back to this Drawing of apple, if imags will not load
                g.setColor(Color.red);
                g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            }


                // Draw Snake
                for (int i = 0; i < bodyParts; i++)  {
                    if (i == 0) {
                        drawHead(g, i);
                    } else if (i == bodyParts - 1) {
                        drawTail(g, i);
                    } else {
                        drawBody(g, i);
                    }
                }

                // Draw Score 
                g.setColor(Color.red);
                g.setFont(new Font("Ink Free", Font.BOLD, 40));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) /  2,  g.getFont().getSize());

            /*
                 // Draw High Score - top left
                g.setColor(Color.yellow);
                g  .setFont(new Font("Ink Free", Font.BOLD, 30));
                g.drawString("High Score: " + highScore, 10, 60);

                */
        } else {
            gameOver(g);
        }
    }


    private void drawHead(Graphics g, int index) {
        BufferedImage headImage = null;

        switch (direction) {
            case 'U':
                headImage = headUp;
            break;

            case 'D':
                headImage = headDown;
            break;

            case 'L':
                headImage = headLeft;
            break;

            case 'R':
                headImage = headRight;
        }

        if (headImage != null) {
            g.drawImage(headImage, x[index], y[index], UNIT_SIZE, UNIT_SIZE, this);
        } else {
            // fallback
            g.setColor(Color.green);
            g.fillRect(x[index], y[index], UNIT_SIZE, UNIT_SIZE);
        }
    }

    // Draw body
    private void drawBody(Graphics g, int index) {
        BufferedImage bodyImage = null;

        // get directions
        int prevX = x[index - 1] -  x[index];
        int prevY = y[index - 1] - y[index];
        int nextX = x[index + 1] - x[index];
        int nextY = y[index + 1] - y[index];

        boolean prevFromLeft = prevX > 0;
        boolean prevFromRight = prevX < 0;
        boolean prevFromUp = prevY > 0;
        boolean prevFromDown = prevY < 0;
        boolean nextToLeft = nextX > 0;
        boolean nextToRight = nextX < 0;
        boolean nextToUp = nextY > 0;
        boolean nextToDown = nextY < 0;

        // Straight horizontal
        if ((prevFromLeft && prevFromRight) || (nextToLeft && nextToRight)) {
            bodyImage = bodyHorizontal;
        }

        // Straight vertical
        else if ((prevFromUp && prevFromDown) || (nextToUp && nextToDown)) {
            bodyImage = bodyVerticle;
        }

        // Top-left Corner
        else if ((prevFromUp && nextToRight) || (prevFromRight && nextToDown)) {
            bodyImage = bodyTopLeft;
        }

        // Top-right Corner
        else if ((prevFromDown && nexToLeft) || (prevFromLeft && nextToDown)) {
            bodyImage = bodyTopRight;
        }

        // Bottom-left Corner
        else if ((prevFromUp && nextToRight) || (prevFromRight && nextToUp)) {
            bodyImage = bodyBottomLeft;
        }

        // Bottom-right Corner
        else if ((prevFromDown && nextToLeft) || (prevFromLeft && nextToUp)) {
            bodyImage = bodyBottomRight;
        }

        if (bodyImage != null) {
            g.drawImage(bodyImage, x[index], y[index], UNIT_SIZE, UNIT_SIZE, this);
        } else {
            // fallback
            g.setColor(new Color(45, 180, 0));
            g.fillRect(x[index], y[index], UNIT_SIZE, UNIT_SIZE);
        }
    }

    // draw tail based on direction
    private void drawTail(Graphics g, int index) {
        BufferedImage tailImage = null;

        // Determine tail direction based on the segment before it
        int diffX = x[index] - x[index - 1];
        int diffY = y[index] - y[index - 1];


        if (diffX == UNIT_SIZE) {
            tailImage = tailRight;
        } else if(diffX == -UNIT_SIZE) {
            tailImage = tailLeft;
        } else if (diffY == UNIT_SIZE) {
            tailImage = tailDown;
        } else if (diffY == -UNIT_SIZE) {
            tailImage = tailUp;
        }

        if (tailImage != null) {
            g.drawImage(tailImage, x[index], y[index], UNIT_SIZE, UNIT_SIZE, this);
        } else {
            // fallback
            g.setColor(new Color(45, 180, 0));
            g.fillRect(x[index], y[index], UNIT_SIZE, UNIT_SIZE);
        }
    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for ( int i = bodyParts - 1; i > 0; i --) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0]  - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0]  + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0]  - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0]  + UNIT_SIZE;
                break;
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        // Check of head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // Check if head touches borders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        /* 
        // Check if head touches left border
        if (x[0] < 0) {
            running = false;
        }

        // Check if head touches right border
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }

        // Check if head touches top border
        if (y[0] < 0) {
            running = false;
        }

        // Check if head touches bottom border
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }
*/
        if (!running) {
            timer.stop();

            // Update hight score
            if (applesEaten> highScore) {
                highScore = applesEaten;
            }

            gameState = GameState.GAME_OVER;
        }
    }

    public void gameOver(Graphics g) {
        // Score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) /  2,  g.getFont().getSize());

        // Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

    // High Score
        g.setColor(Color.yellow);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g .drawString("High Score: " + highScore, (SCREEN_WIDTH - metrics3.stringWidth("High Score: " + highScore)) / 2, 100);

    // Play Again button
        g.setColor(Color.green);
        g.fillRect(175, 350, 250, 60);
        g.setColor(Color.black);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics4 = getFontMetrics(g.getFont());
        g.drawString("Play Again", (SCREEN_WIDTH - metrics4.stringWidth("Play Again")) / 2, 395);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && gameState == GameState.PLAYING) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (direction != 'R') {
                            direction = 'L';
                            }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (direction != 'L') {
                            direction = 'R';
                            }
                        break;
                    case KeyEvent.VK_UP:
                        if (direction != 'D') {
                            direction = 'U';
                            }
                        break;
                    case KeyEvent.VK_DOWN:
                            if (direction != 'U') {
                                direction = 'D';
                            }
                        break;
                    case KeyEvent.VK_SPACE:
                            if (gameState == GameState.MENU || gameState == GameState.GAME_OVER) {
                                startGame();
                            }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (gameState == GameState.GAME_OVER) {
                            gameState = GameState.MENU;
                            }
                        break;
                    case KeyEvent.VK_ENTER:
                        if (gameState == GameState.GAME_OVER) {
                            gameState = GameState.MENU;
                        }
                        break;
            }
        }
    }

    public class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();

        // Check if PLAY AGAIN button clicked in GAME OVER state
        if (gameState == GameState.GAME_OVER) {
            // Check if click is within the bounds of the "Play Again" button
            if (mouseX >= 175 && mouseX <= 425 && mouseY >= 350 && mouseY <= 410) {
                startGame();
            }
        }

        // Check if PLAY button clicked in MENU state
        if (gameState == GameState.MENU) {
            // Check if click is within the bounds of the "Play" button
            if (mouseX >= 200 && mouseX <= 400 && mouseY >= 250 && mouseY <= 310) {
                startGame();
            }
        }

        // Back to Menu button
        if (mouseX >= 175 && mouseY <= 425 && mouseY >= 380 && mouseY <= 440) {
            gameState = GameState.MENU;
        }
        }
    }
}
