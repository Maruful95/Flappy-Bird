import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

  int boardWidth = 360;
  int boardHeight = 640;

  //   images
  Image background;
  Image birdImage;
  Image topPipe;
  Image bottomPipe;

  //   bird
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

  // pipes
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

  //   game logic
  Bird bird;
  int velocityX = -4;
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

  FlappyBird() {
    setPreferredSize(new Dimension(boardWidth, boardHeight));
    setFocusable(true);
    addKeyListener(this);

    // load images
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

    // bird
    bird = new Bird(birdImage);
    pipes = new ArrayList<Pipe>();

    // placed pipe timer
    placedPipesTimer = new javax.swing.Timer(
      1100,
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          placedPipes();
        }
      }
    );
    // placedPipesTimer.start();

    // game timer
    gameLoop = new javax.swing.Timer(1000 / 60, this);
    gameLoop.start();
  }

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

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    draw(g);
  }

  public void draw(Graphics g) {
    // background
    g.drawImage(background, 0, 0, boardWidth, boardHeight, null);

    // bird
    g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

    // pipes
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
  public void drawStartScreen(Graphics g) {
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 36));
    g.drawString("FLAPPY BIRD", 75, 220);
    g.setFont(new Font("Arial", Font.PLAIN, 20));
    g.drawString("Press SPACE to start", 85, 300);
    g.drawString("Avoid the pipes!", 105, 380);
  }

  // Game over screen
  public void drawGameOverScreen(Graphics g) {
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 30));
    g.drawString("GAME OVER", 100, 250);
    g.setFont(new Font("Arial", Font.PLAIN, 20));
    g.drawString("Score: " + (int) score, 130, 300);
    g.drawString("Press SPACE to restart", 80, 350);
  }

  public void move() {
    if (gameState != PLAYING) {
      return;
    }
    velocityY += gravity;
    bird.y += velocityY;
    bird.y = Math.max(0, bird.y);

    // pipes
    for (int i = 0; i < pipes.size(); i++) {
      Pipe pipe = pipes.get(i);
      pipe.x += velocityX;

      if (!pipe.passed && bird.x > pipe.x + pipe.width) {
        score += 0.5;
        pipe.passed = true;
      }

      if (collision(bird, pipe)) {
        gameOver = true;
        gameState = GAME_OVER;
      }

      if (bird.y > boardHeight - bird.height) {
        gameOver = true;
        gameState = GAME_OVER;
      }
    }
  }

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
        return;
      }

      if (gameOver) {
        // reset game
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        score = 0;
        gameOver = false;
        gameState = PLAYING;
        placedPipesTimer.start();
        gameLoop.start();
        return;
      }
      velocityY = -10;
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}
}
