import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY = 100;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int appleEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    boolean startScreen = true;
    boolean state = false;
    static final int GLOW_DELAY = 800;

    GamePanel () {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        startScreen = true;
        newApple();
        running = true;
        timer = new Timer(GLOW_DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void reset() {
        newApple();
        for(int i = 0; i<x.length ; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        appleEaten = 0;
        direction = 'R';
        running = true;
        timer.start();
    }

    public void draw(Graphics g) {

        if(startScreen) {
            showStartScreen(g);
            return;
        }

        if(!running) {
            gameOver(g);
            return;
        }

        /**
         * grids for visualization
         */
        /*for(int i =0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {
            g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
            g.drawLine(0,i*UNIT_SIZE,  SCREEN_WIDTH, i*UNIT_SIZE);
        }*/

        g.setColor(Color.red);
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

        Color color = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        for(int i = 0; i< bodyParts; i++) {
            if(i == 0) {
                g.setColor(Color.green);
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            } else {
//                g.setColor(new Color(45, 180, 0));
//                g.setColor(color);
                g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

        }

        drawScore(g);
    }

    private void showStartScreen(Graphics g) {
        //Title
        g.setColor(Color.green);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("The Snake Game", (SCREEN_WIDTH - metrics.stringWidth("The Snake Game"))/2, SCREEN_HEIGHT/2);

        if(state) {
            g.setColor(Color.white);
            g.setFont(new Font("Consolas", Font.PLAIN, 25));
            FontMetrics metrics2 = getFontMetrics(g.getFont());
            g.drawString("[Press any button to start]", (SCREEN_WIDTH - metrics2.stringWidth("[Press any button to start]")) / 2, SCREEN_HEIGHT / 2 + g.getFont().getSize() * 2);
        }
        //Author
        g.setColor(Color.gray);
        g.setFont(new Font("Ink Free", Font.BOLD, 30));
        FontMetrics metrics3 = getFontMetrics(g.getFont());

        g.drawString("- by Swetha347", (SCREEN_WIDTH - metrics3.stringWidth("- by Swetha347")), SCREEN_HEIGHT - g.getFont().getSize());



    }

    public void newApple() {
        appleX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
        appleY = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;

    }

    public void move() {
        for(int i = bodyParts; i>0;i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U' :
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D' :
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L' :
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R' :
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    public void drawScore(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + appleEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + appleEaten))/2, g.getFont().getSize());
    }

    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            appleEaten++;
            newApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts;i>0; i--) {
            if((x[0] == x [i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        //snake head crossed left or right border
        if(x[0] < 0 || x[0] > SCREEN_WIDTH) {
            running = false;
        }

        //snake head crossed top or bottom border
        if(y[0] < 0 || y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if(!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //GameOver Screen
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
        drawScore(g);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Press R to restart", (SCREEN_WIDTH - metrics2.stringWidth("Press R to restart"))/2, SCREEN_HEIGHT - g.getFont().getSize());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(startScreen) {
            state = !state;
        } else if(running) {
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
                case KeyEvent.VK_LEFT :
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_R:
                    if(!running) {
                        reset();
                    }
                    break;
                default:
                    if(startScreen) {
                        stopStartScreen();
                    }
            }
        }
    }

    public void stopStartScreen() {
        startScreen = false;
        timer.stop();
        timer = new Timer(DELAY,this);
        timer.start();
    }

}
