import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
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

  javax.swing.Timer gameLoop;
  javax.swing.Timer placedPipes;

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
    placedPipes = new javax.swing.Timer(
      1500,
      new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          placedPipes();
        }
      }
    );
    placedPipes.start();

    // game timer
    gameLoop = new javax.swing.Timer(1000 / 60, this);
    gameLoop.start();
  }

  public void placedPipes() {
    Pipe newPipe = new Pipe(topPipe);
    pipes.add(newPipe);
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
  }

  public void move() {
    velocityY += gravity;
    bird.y += velocityY;
    bird.y = Math.max(0, bird.y);

    // pipes
    for (int i = 0; i < pipes.size(); i++) {
      Pipe pipe = pipes.get(i);
      pipe.x += velocityX;
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    move();
    repaint();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
      velocityY = -10;
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}
}
