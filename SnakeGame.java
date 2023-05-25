import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class SnakeGame extends JPanel implements KeyListener, Runnable {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final int UNIT_SIZE = 20;
    private static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 75;

    private final List<Point> snake;
    private int appleX;
    private int appleY;
    private char direction;
    private boolean running;
    private int score;

    private final Random random;
    private final Font scoreFont;

    public SnakeGame() {
        snake = new ArrayList<>();
        random = new Random();
        scoreFont = new Font("Arial", Font.PLAIN, 20);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
        startGame();
    }

    private void startGame() {
        snake.clear();
        running = true;
        direction = 'R';
        score = 0;

        snake.add(new Point(WIDTH / 2, HEIGHT / 2)); // Starting position of the snake
        spawnApple();

        Thread thread = new Thread(this);
        thread.start();
    }

    private void spawnApple() {
        appleX = random.nextInt(WIDTH / UNIT_SIZE) * UNIT_SIZE;
        appleY = random.nextInt(HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    private void move() {
        Point head = new Point(snake.get(0));
        switch (direction) {
            case 'U':
                head.y -= UNIT_SIZE;
                break;
            case 'D':
                head.y += UNIT_SIZE;
                break;
            case 'L':
                head.x -= UNIT_SIZE;
                break;
            case 'R':
                head.x += UNIT_SIZE;
                break;
        }

        snake.add(0, head);

        // Check if snake has eaten the apple
        if (head.x == appleX && head.y == appleY) {
            score++;
            spawnApple();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void checkCollisions() {
        // Check if snake has collided with itself
        Point head = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                break;
            }
        }

        // Check if snake has collided with the boundaries
        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            running = false;
        }

        if (!running) {
            gameOver();
        }
    }

    private void gameOver() {
        // Handle game over logic
        System.exit(0);
    }

    private void draw(Graphics g) {
        if (running) {
            // Draw snake
            for (Point point : snake) {
                g.setColor(Color.RED);
                g.fillRect(point.x, point.y, UNIT_SIZE, UNIT_SIZE);
            }

            // Draw apple
            g.setColor(Color.GREEN);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw score
            g.setColor(Color.BLACK);
            g.setFont(scoreFont);
            g.drawString("Score: " + score, 10, 20);
        } else {
            gameOverScreen(g);
        }
    }

    private void gameOverScreen(Graphics g) {
        // Game over screen
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString("Game Over", WIDTH / 2 - 110, HEIGHT / 2 - 30);
        g.setFont(scoreFont);
        g.drawString("Score: " + score, WIDTH / 2 - 50, HEIGHT / 2 + 10);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT && direction != 'R') {
            direction = 'L';
        } else if (key == KeyEvent.VK_RIGHT && direction != 'L') {
            direction = 'R';
        } else if (key == KeyEvent.VK_UP && direction != 'D') {
            direction = 'U';
        } else if (key == KeyEvent.VK_DOWN && direction != 'U') {
            direction = 'D';
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void run() {
        while (running) {
            move();
            checkCollisions();
            repaint();

            try {
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("Snake");
                SnakeGame game = new SnakeGame();
                frame.add(game);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }
}                        