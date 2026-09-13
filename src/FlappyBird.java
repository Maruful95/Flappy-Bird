
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.*;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

  int boardWidth = 360;
  int boardHeight = 640;

  // Images
  Image background;
  Image birdImage;
  Image topPipe;
  Image bottomPipe;

  // Sounds
  Clip flapSound;

  // Bird
  int birdX = boardWidth / 8;
  int birdY = boardHeight / 2;
  int birdWidth = 34;
  int birdHeight = 24;

  class Bird {

    int x = birdX;
    int y = birdY;
    int width = birdWidth;
    int height = birdHeight;
    Image img;

    Bird(Image img) {
      this.img = img;
    }
  }

  // Pipes
  int pipeX = boardWidth;
  int pipeY = 0;
  int pipeWidth = 62;
  int pipeHeight = 512;

  class Pipe {

    int x = pipeX;
    int y = pipeY;
    int width = pipeWidth;
    int height = pipeHeight;
    Image img;
    boolean passed = false;

    Pipe(Image img) {
      this.img = img;
    }
  }

  // Game logic
  Bird bird;

  int velocityX = -4;
  int currentSpeed = -4;
  int velocityY = 0;
  int gravity = 1;

  ArrayList<Pipe> pipes;
  Random random = new Random();

  javax.swing.Timer gameLoop;
  javax.swing.Timer placedPipesTimer;

  boolean gameOver = false;
  double score = 0;

  // Game states
  final int START = 0;
  final int PLAYING = 1;
  final int GAME_OVER = 2;

  int gameState = START;

  public static void main(String[] args) {

    JFrame frame = new JFrame("Flappy Bird");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);

    FlappyBird flappyBird = new FlappyBird();

    frame.add(flappyBird);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    flappyBird.requestFocusInWindow();
  }

  FlappyBird() {

    setPreferredSize(new Dimension(boardWidth, boardHeight));
    setFocusable(true);
    addKeyListener(this);

    // Load images
    background = new ImageIcon(
      getClass().getResource("./flappybirdbg.png")
    ).getImage();

    birdImage = new ImageIcon(
      getClass().getResource("./flappybird.png")
    ).getImage();

    topPipe = new ImageIcon(
      getClass().getResource("./toppipe.png")
    ).getImage();

    bottomPipe = new ImageIcon(
      getClass().getResource("./bottompipe.png")
    ).getImage();

    // Bird
    bird = new Bird(birdImage);

    // Pipes
    pipes = new ArrayList<Pipe>();

    // Load sounds
    loadSounds();

    // Pipe timer
    placedPipesTimer = new javax.swing.Timer(
      1100,
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          placedPipes();
        }
      }
    );

    // Game timer
    gameLoop = new javax.swing.Timer(1000 / 60, this);
    gameLoop.start();
  }

  // Load flap sound
  public void loadSounds() {

    try {

      URL flapSoundURL = getClass().getResource("./sounds/flap.wav");

      if (flapSoundURL == null) {
        System.out.println("Flap sound file not found!");
        return;
      }

      AudioInputStream audioInputStream =
        AudioSystem.getAudioInputStream(flapSoundURL);

      flapSound = AudioSystem.getClip();
      flapSound.open(audioInputStream);

      audioInputStream.close();

      System.out.println("Flap sound loaded successfully!");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Play flap sound
  public void playFlapSound() {

    if (flapSound == null) {
      return;
    }

    flapSound.stop();
    flapSound.setFramePosition(0);
    flapSound.start();
  }

  // Create pipes
  public void placedPipes() {

    int randomPipeY = (int) (
      pipeY -
      pipeHeight / 4 -
      Math.random() * (pipeHeight / 2)
    );

    int openingSpace = boardHeight / 4;

    Pipe top = new Pipe(topPipe);
    top.y = randomPipeY;
    pipes.add(top);

    Pipe bottom = new Pipe(bottomPipe);
    bottom.y = randomPipeY + pipeHeight + openingSpace;
    pipes.add(bottom);
  }

  @Override
  public void paintComponent(Graphics g) {

    super.paintComponent(g);
    draw(g);
  }

  public void draw(Graphics g) {

    // Background
    g.drawImage(
      background,
      0,
      0,
      boardWidth,
      boardHeight,
      null
    );

    // Bird animation
    Graphics2D g2 = (Graphics2D) g.create();

    double angle = Math.toRadians(velocityY * 3);

    AffineTransform oldTransform = g2.getTransform();

    g2.rotate(
      angle,
      bird.x + bird.width / 2,
      bird.y + bird.height / 2
    );

    g2.drawImage(
      bird.img,
      bird.x,
      bird.y,
      bird.width,
      bird.height,
      null
    );

    g2.setTransform(oldTransform);
    g2.dispose();

    // Pipes
    for (int i = 0; i < pipes.size(); i++) {

      Pipe pipe = pipes.get(i);

      g.drawImage(
        pipe.img,
        pipe.x,
        pipe.y,
        pipe.width,
        pipe.height,
        null
      );
    }

    // Start screen
    if (gameState == START) {
      drawStartScreen(g);
    }

    // Game Over screen
    if (gameState == GAME_OVER) {

      drawGameOverScreen(g);

    } else {

      // Score
      g.setColor(Color.WHITE);
      g.setFont(new Font("Arial", Font.BOLD, 24));

      g.drawString(
        "Score: " + (int) score,
        10,
        30
      );
    }
  }

  // Start screen
  public void drawStartScreen(Graphics g) {

    g.setColor(Color.WHITE);

    g.setFont(new Font("Arial", Font.BOLD, 36));
    g.drawString("FLAPPY BIRD", 75, 220);

    g.setFont(new Font("Arial", Font.PLAIN, 20));
    g.drawString("Press SPACE to start", 85, 300);
    g.drawString("Avoid the pipes!", 105, 380);
  }

  // Game Over screen
  public void drawGameOverScreen(Graphics g) {

    g.setColor(Color.WHITE);

    g.setFont(new Font("Arial", Font.BOLD, 30));
    g.drawString("GAME OVER", 100, 250);

    g.setFont(new Font("Arial", Font.PLAIN, 20));
    g.drawString("Score: " + (int) score, 130, 300);
    g.drawString("Press SPACE to restart", 80, 350);
  }

  // Update game movement
  public void move() {

    if (gameState != PLAYING) {
      return;
    }

    // Bird movement
    velocityY += gravity;
    bird.y += velocityY;

    bird.y = Math.max(0, bird.y);

    // Increase pipe speed with score
    currentSpeed = -4 - ((int) score / 5);

    // Maximum speed
    if (currentSpeed < -8) {
      currentSpeed = -8;
    }

    // Pipes
    for (int i = 0; i < pipes.size(); i++) {

      Pipe pipe = pipes.get(i);

      // Move pipe
      pipe.x += currentSpeed;

      // Increase score
      if (!pipe.passed && bird.x > pipe.x + pipe.width) {

        score += 0.5;
        pipe.passed = true;
      }

      // Collision detection
      if (collision(bird, pipe)) {

        gameOver = true;
        gameState = GAME_OVER;
      }

      // Remove pipes outside the screen
      if (pipe.x + pipe.width < 0) {

        pipes.remove(i);
        i--;
      }
    }

    // Bottom boundary
    if (bird.y > boardHeight - bird.height) {

      gameOver = true;
      gameState = GAME_OVER;
    }
  }

  // Collision detection
  public boolean collision(Bird a, Pipe b) {

    return (
      a.x < b.x + b.width &&
      a.x + a.width > b.x &&
      a.y < b.y + b.height &&
      a.y + a.height > b.y
    );
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    move();
    repaint();

    if (gameOver) {

      placedPipesTimer.stop();
      gameLoop.stop();
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {

    if (e.getKeyCode() == KeyEvent.VK_SPACE) {

      // Start the game
      if (gameState == START) {

        gameState = PLAYING;
        placedPipesTimer.start();

        velocityY = -10;
        playFlapSound();

        return;
      }

      // Restart after Game Over
      if (gameOver) {

        bird.y = birdY;
        velocityY = 0;

        pipes.clear();

        score = 0;
        currentSpeed = -4;

        gameOver = false;
        gameState = PLAYING;

        placedPipesTimer.start();
        gameLoop.start();

        velocityY = -10;
        playFlapSound();

        return;
      }

      // Normal gameplay
      velocityY = -10;
      playFlapSound();
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}
}