import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

// Base Object Class
abstract class GameObject {
    protected int x, y, width, height;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(Graphics g);

    public abstract void move();

    public boolean intersects(GameObject other) {
        return (this.x < other.x + other.width &&
                this.x + this.width > other.x &&
                this.y < other.y + other.height &&
                this.y + this.height > other.y);
    }
}

// Person Class (Player)
class Person extends GameObject {
    private int speed = 5;

    public Person(int x, int y) {
        super(x, y, 30, 30); // Player size
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }

    @Override
    public void move() {
        // Player doesn't move automatically
    }

    public void moveUp() {
        y -= speed;
    }

    public void moveDown() {
        y += speed;
    }

    public void moveLeft() {
        x -= speed;
    }

    public void moveRight() {
        x += speed;
    }
}

// Obstacle Class
class Obstacle extends GameObject {
    private int speed;

    public Obstacle(int x, int y, int speed) {
        super(x, y, 20, 20); // Obstacle size
        this.speed = speed;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, width, height);
    }

    @Override
    public void move() {
        y += speed; // Moves down the screen
        if (y > 600) { // Reset to top if out of bounds
            y = -20;
            x = new Random().nextInt(550);
        }
    }
}

// Main Game Panel
class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Person player;
    private ArrayList<Obstacle> obstacles;
    private Timer timer;
    private boolean gameOver;

    public GamePanel() {
        this.player = new Person(275, 500); // Center-bottom start
        this.obstacles = new ArrayList<>();
        this.gameOver = false;

        // Create obstacles with random speeds
        for (int i = 0; i < 5; i++) {
            int speed = new Random().nextInt(5) + 2; // Speed between 2 and 6
            obstacles.add(new Obstacle(new Random().nextInt(550), new Random().nextInt(400) - 400, speed));
        }

        this.timer = new Timer(30, this); // Game loop timer
        this.timer.start();
        this.setFocusable(true);
        this.addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (gameOver) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over!", 180, 300);
            timer.stop();
        } else {
            player.draw(g);

            for (Obstacle obstacle : obstacles) {
                obstacle.draw(g);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Move obstacles
        for (Obstacle obstacle : obstacles) {
            obstacle.move();

            // Check collision
            if (player.intersects(obstacle)) {
                gameOver = true;
            }
        }

        // Repaint screen
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> player.moveUp();
                case KeyEvent.VK_DOWN -> player.moveDown();
                case KeyEvent.VK_LEFT -> player.moveLeft();
                case KeyEvent.VK_RIGHT -> player.moveRight();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}
}

// Main Class
public class DodgeBallGame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Dodge Ball");
        GamePanel gamePanel = new GamePanel();

        frame.add(gamePanel);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
