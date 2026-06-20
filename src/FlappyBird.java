import java.awt.*;
import java.awt.event.*;
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

  //   game logic
  Bird bird;
  int velocityY = 0;
  int gravity = 1;

  javax.swing.Timer gameLoop;

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

    // game timer
    gameLoop = new javax.swing.Timer(1000 / 60, this);
    gameLoop.start();
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
  }

  public void move() {
    velocityY += gravity;
    bird.y += velocityY;
    bird.y = Math.max(0, bird.y);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    move();
    repaint();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
      velocityY = -10    ;
    }  
  }

  @Override
  public void keyTyped(KeyEvent e) {}

  @Override
  public void keyReleased(KeyEvent e) {}
}
