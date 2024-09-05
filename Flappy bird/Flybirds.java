import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Flybirds extends JPanel implements ActionListener, KeyListener {
    int bwidth = 300;
    int bheight = 600;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Bird
    int birdX = bwidth / 8;
    int birdY = bheight / 2;
    int birdwidth = 34;
    int birdheight = 24;

    // Pipes
    int pipeX = bwidth;
    int pipeY = 0;
    int pipewidth = 64;
    int pipeheight = 512;

    // Bird class
    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdwidth;
        int height = birdheight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipe class
    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipewidth;
        int height = pipeheight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // Game variables
    Bird bird;
    int velocityX = -4; // Pipes moving left speed
    int velocityY = 3;  // Bird's vertical speed
    int gravity = 1;
    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameLoop;
    int delay = 1000 / 60;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    JButton restartButton;

    Flybirds() {
        setPreferredSize(new Dimension(bwidth, bheight));
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImg = new ImageIcon(getClass().getResource("./flybg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // Initialize bird and pipes
        bird = new Bird(birdImg);
        pipes = new ArrayList<>();

        // Timer to place pipes
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        // Game loop
        gameLoop = new Timer(delay, this);
        gameLoop.start();

        // Add restart button
        restartButton = new JButton("Restart");
        restartButton.setFocusable(false); // Ensure it doesn't interfere with key input
        restartButton.setBounds(100, 400, 100, 40); // Set position and size
        restartButton.setVisible(false); // Hide initially
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        this.setLayout(null);
        this.add(restartButton);
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeheight / 4 - Math.random() * (pipeheight / 2));
        int openingSpace = bheight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeheight + openingSpace;
        pipes.add(bottomPipe);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw background
        g.drawImage(backgroundImg, 0, 0, bwidth, bheight, null);

        // Draw bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // Draw pipes
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Draw score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over: " + (int) score, 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // Move bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // Move pipes and check collisions
        for (Pipe pipe : pipes) {
            pipe.x += velocityX;
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > bheight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        } else {
            restartButton.setVisible(true); // Show restart button on game over
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) {
            velocityY = -9;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    // Restart the game
    public void restartGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        restartButton.setVisible(false); // Hide restart button
        gameLoop.start();
        placePipesTimer.start();
    }

}
