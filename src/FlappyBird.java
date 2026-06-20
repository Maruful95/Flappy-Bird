import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class FlappyBird extends JPanel {

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

  FlappyBird() {
    setPreferredSize(new Dimension(boardWidth, boardHeight));

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

    bird = new Bird(birdImage);
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
}
