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
  Clip hitSound;
  Clip scoreSound;

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

    topPipe = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();

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

  // Load sound
  private void loadSounds() {
    try {
      // Flap sound
      URL flapURL = getClass().getResource("/sounds/flap.wav");

      if (flapURL != null) {
        flapSound = AudioSystem.getClip();
        flapSound.open(AudioSystem.getAudioInputStream(flapURL));
        System.out.println("Flap sound loaded.");
      }

      // Hit sound
      URL hitURL = getClass().getResource("/sounds/hit.wav");

      if (hitURL != null) {
        hitSound = AudioSystem.getClip();
        hitSound.open(AudioSystem.getAudioInputStream(hitURL));
        System.out.println("Hit sound loaded.");
      }

      // Score sound
      URL scoreURL = getClass().getResource("/sounds/score.wav");

      if (scoreURL != null) {
        scoreSound = AudioSystem.getClip();
        scoreSound.open(AudioSystem.getAudioInputStream(scoreURL));
        System.out.println("Score sound loaded.");
      }
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

  // Play hit sound
  private void playHitSound() {
    if (hitSound != null) {
      hitSound.stop();
      hitSound.setFramePosition(0);
      hitSound.start();
    }
  }

  // Play score sound
  private void playScoreSound() {
    if (scoreSound != null) {
      scoreSound.stop();
      scoreSound.setFramePosition(0);
      scoreSound.start();
    }
  }

  // Create pipes
  public void placedPipes() {
    int randomPipeY = (int) (pipeY -
      pipeHeight / 4 -
      Math.random() * (pipeHeight / 2));

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
    g.drawImage(background, 0, 0, boardWidth, boardHeight, null);

    // Bird animation
    Graphics2D g2 = (Graphics2D) g.create();

    double angle = Math.toRadians(velocityY * 3);

    AffineTransform oldTransform = g2.getTransform();

    g2.rotate(angle, bird.x + bird.width / 2, bird.y + bird.height / 2);

    g2.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

    g2.setTransform(oldTransform);
    g2.dispose();

    // Pipes
    for (int i = 0; i < pipes.size(); i++) {
      Pipe pipe = pipes.get(i);

      g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
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

      g.drawString("Score: " + (int) score, 10, 30);
    }
  }

  // Start screen
  private void drawStartScreen(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();

    g2.setColor(new Color(0, 0, 0, 100));
    g2.fillRect(0, 0, boardWidth, boardHeight);

    g2.setColor(Color.WHITE);
    g2.setFont(new Font("Arial", Font.BOLD, 42));

    String title = "FLAPPY BIRD";
    int titleWidth = g2.getFontMetrics().stringWidth(title);

    g2.drawString(title, (boardWidth - titleWidth) / 2, 220);

    g2.setFont(new Font("Arial", Font.BOLD, 22));

    String instruction = "Press SPACE to Start";
    int instructionWidth = g2.getFontMetrics().stringWidth(instruction);

    g2.drawString(instruction, (boardWidth - instructionWidth) / 2, 320);

    g2.setFont(new Font("Arial", Font.PLAIN, 18));

    String controlText = "SPACE = Flap";
    int controlWidth = g2.getFontMetrics().stringWidth(controlText);

    g2.drawString(controlText, (boardWidth - controlWidth) / 2, 360);

    String objectiveText = "Avoid the pipes!";
    int objectiveWidth = g2.getFontMetrics().stringWidth(objectiveText);

    g2.drawString(objectiveText, (boardWidth - objectiveWidth) / 2, 390);
    g2.dispose();
  }

  // Game Over screen
  private void drawGameOverScreen(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();

    g2.setColor(new Color(0, 0, 0, 150));
    g2.fillRect(0, 0, boardWidth, boardHeight);

    g2.setColor(Color.WHITE);
    g2.setFont(new Font("Arial", Font.BOLD, 42));

    g2.setColor(Color.RED);
    String gameOverText = "GAME OVER";
    int textWidth = g2.getFontMetrics().stringWidth(gameOverText);

    g2.drawString(gameOverText, (boardWidth - textWidth) / 2, 250);

    g2.setColor(Color.WHITE);
    g2.setFont(new Font("Arial", Font.BOLD, 28));

    String scoreText = "Score: " + score;
    int scoreWidth = g2.getFontMetrics().stringWidth(scoreText);

    g2.drawString(scoreText, (boardWidth - scoreWidth) / 2, 310);

    g2.setFont(new Font("Arial", Font.BOLD, 20));

    String restartText = "Press SPACE to Restart";
    int restartWidth = g2.getFontMetrics().stringWidth(restartText);

    g2.drawString(restartText, (boardWidth - restartWidth) / 2, 370);

    g2.dispose();
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
        if (score % 5 == 0) {
          playScoreSound();
        }
      }

      // Collision detection
      if (collision(bird, pipe)) {
        playHitSound();
        gameOver = true;
        gameState = GAME_OVER;
      }

      if (pipe.x + pipe.width < 0) {
        pipes.remove(i);
        i--;
      }
    }

    // Bottom boundary
    if (bird.y > boardHeight - bird.height) {
      playHitSound();
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
