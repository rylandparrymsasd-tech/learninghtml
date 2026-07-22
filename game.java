import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class Game extends JPanel implements ActionListener, KeyListener {
    // Game dimensions
    private final int BOARD_WIDTH = 800;
    private final int BOARD_HEIGHT = 600;

    // Game loop timer
    private Timer timer;
    private boolean isPlaying = true;
    private int score = 0;

    // Player attributes
    private int playerX = 350;
    private final int playerY = 520;
    private final int PLAYER_WIDTH = 100;
    private final int PLAYER_HEIGHT = 20;
    private int playerSpeed = 0;

    // Falling objects
    private ArrayList<Point> objects;
    private final int OBJECT_SIZE = 20;
    private final int OBJECT_SPEED = 5;
    private Random random;

    public Game() {
        // Configure the game panel
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Initialize entities
        objects = new ArrayList<>();
        random = new Random();

        // Start the game loop (runs roughly every 16ms ~ 60 FPS)
        timer = new Timer(16, this);
        timer.start();
    }

    // --- GAME ENGINE LOOP ---
    @Override
    public void actionPerformed(ActionEvent e) {
        if (isPlaying) {
            updatePlayer();
            updateObjects();
            checkCollisions();
        }
        repaint(); // Redraws the screen
    }

    // --- GAME LOGIC ---
    private void updatePlayer() {
        playerX += playerSpeed;
        // Keep player inside window boundaries
        if (playerX < 0) playerX = 0;
        if (playerX > BOARD_WIDTH - PLAYER_WIDTH) playerX = BOARD_WIDTH - PLAYER_WIDTH;
    }

    private void updateObjects() {
        // Randomly spawn new falling items
        if (random.nextInt(100) < 3) {
            int startX = random.nextInt(BOARD_WIDTH - OBJECT_SIZE);
            objects.add(new Point(startX, 0));
        }

        // Move items downward
        for (int i = 0; i < objects.size(); i++) {
            Point p = objects.get(i);
            p.y += OBJECT_SPEED;

            // Remove items that fall off the screen
            if (p.y > BOARD_HEIGHT) {
                objects.remove(i);
                i--;
            }
        }
    }

    private void checkCollisions() {
        Rectangle playerBounds = new Rectangle(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

        for (int i = 0; i < objects.size(); i++) {
            Point p = objects.get(i);
            Rectangle objectBounds = new Rectangle(p.x, p.y, OBJECT_SIZE, OBJECT_SIZE);

            // If player catches an object
            if (playerBounds.intersects(objectBounds)) {
                score += 10;
                objects.remove(i);
                i--;
            }
        }
    }

    // --- GRAPHICS RENDERING ---
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isPlaying) {
            // Draw Player Paddle
            g.setColor(Color.GREEN);
            g.fillRect(playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT);

            // Draw Falling Objects
            g.setColor(Color.RED);
            for (Point p : objects) {
                g.fillOval(p.x, p.y, OBJECT_SIZE, OBJECT_SIZE);
            }

            // Draw Live Score Dashboard
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 20, 30);
        }
    }

    // --- CONTROLS INPUT ---
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
            playerSpeed = -8; // Move Left
        }
        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            playerSpeed = 8;  // Move Right
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT || 
            key == KeyEvent.VK_A || key == KeyEvent.VK_D) {
            playerSpeed = 0; // Stop moving when key is released
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // --- MAIN METHOD TO START GAME ---
    public static void main(String[] args) {
        JFrame frame = new JFrame("Java Arcade Catch");
        Game gamePanel = new Game();

        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center window
        frame.setVisible(true);
    }
}
