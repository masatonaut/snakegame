package com.mycompany.assign3;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import java.util.Random;
import java.util.List;


import javax.swing.JPanel;
/**
 *
 * @author emaoits
 */
public final class GamePanel extends JPanel implements ActionListener{
    private final GameFrame gameFrame;
    
    static final int SCREEN_WIDTH = 1300;
    static final int SCREEN_HEIGHT = 750;
    static final int UNIT_SIZE = 50;
    static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
    static final int DELAY = 175;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 2;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    int numberOfRocks = 5;
    int[] rockX = new int[numberOfRocks];
    int[] rockY = new int[numberOfRocks];
    private List<String> topHighScores;
    private boolean gameOver = false;
    private final int level;

    GamePanel(GameFrame gameFrame, int level){
        this.level = level;
        this.topHighScores = new ArrayList<>();
        this.gameFrame = gameFrame;
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }
    public void startGame() {
        x[0] = (SCREEN_WIDTH / UNIT_SIZE / 2) * UNIT_SIZE;
        y[0] = (SCREEN_HEIGHT / UNIT_SIZE / 2) * UNIT_SIZE;
    
        newApple();
        placeRocks();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
        setRandomDirection();
        loadTopHighScores();
        gameOver = false;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            draw(g);
        } else if (gameOver) {
            drawGameOver(g);
        }
    }
    public void placeRocks() {
        int numberOfRocks = switch (level) {
            case 1 -> 1;
            case 2 -> 3;
            case 3 -> 5;
            default -> this.numberOfRocks;
        };
        for (int i = 0; i < numberOfRocks; i++) {
            rockX[i] = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
            rockY[i] = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
        }
    }

    private void setRandomDirection() {
        int dir = random.nextInt(4);
        switch(dir) {
            case 0 -> direction = 'U';
            case 1 -> direction = 'D';
            case 2 -> direction = 'L';
            case 3 -> direction = 'R';
        }
    }
    
    public void draw(Graphics g) {

        if(running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            g.setColor(Color.gray);
            for(int i = 0; i < numberOfRocks; i++) {
                g.fillRect(rockX[i], rockY[i], UNIT_SIZE, UNIT_SIZE);
            }
            for(int i = 0; i< bodyParts;i++) {
                if(i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
                else {
                    g.setColor(new Color(45,180,0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }			
            }
            g.setColor(Color.red);
            g.setFont( new Font("Ink Free",Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());
            g.setColor(Color.white);
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            int yPosition = 30;
            for (String score : topHighScores) {
                g.drawString(score, 10, yPosition);
                yPosition += 30;
            }
        }
        else {
            gameOver();
        }

    }
    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }
    public void move(){
        for(int i = bodyParts;i>0;i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch(direction) {
        case 'U' -> y[0] = y[0] - UNIT_SIZE;
        case 'D' -> y[0] = y[0] + UNIT_SIZE;
        case 'L' -> x[0] = x[0] - UNIT_SIZE;
        case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }

    }
    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }
    public void checkCollisions() {
        for(int i = 0; i < numberOfRocks; i++) {
            if ((x[0] == rockX[i]) && (y[0] == rockY[i])) {
                running = false;
                break;
            }
        }
        for(int i = bodyParts;i>0;i--) {
            if((x[0] == x[i])&& (y[0] == y[i])) {
                running = false;
            }
        }
        if(x[0] < 0) {
            running = false;
        }
        if(x[0] > SCREEN_WIDTH) {
            running = false;
        }
        if(y[0] < 0) {
            running = false;
        }
        if(y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if(!running) {
            timer.stop();
        }
    }

    public void gameOver() {
        if (gameOver) {
            return;
        }
        gameOver = true;
        timer.stop();

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Game Over! Your score is: " + applesEaten, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            String playerName = JOptionPane.showInputDialog(this, "Enter your name for the high score:");
            if (playerName != null && !playerName.trim().isEmpty()) {
                gameFrame.saveHighScore(playerName, applesEaten);
            }
        });
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }



    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        if (!running && !gameOver) {
            gameOver();
        }
        repaint();
    }

    private void loadTopHighScores() {
        topHighScores = gameFrame.getTopHighScores(); // GameFrame クラスに定義されているメソッドを使用
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> {
                if(direction != 'R') {
                    direction = 'L';
                }
            }
            case KeyEvent.VK_RIGHT -> {
                if(direction != 'L') {
                    direction = 'R';
                }
            }
            case KeyEvent.VK_UP -> {
                if(direction != 'D') {
                    direction = 'U';
                }
            }
            case KeyEvent.VK_DOWN -> {
                if(direction != 'U') {
                    direction = 'D';
                }
            }
            }
        }
    }
}