import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Main extends JPanel implements KeyListener, MouseMotionListener, MouseListener {

    static int x = 24;
    static int y = 32;
    static int width = 576;
    static int length = 768;

    static int viewWidth = 40;
    static int viewLength = 60;

    static final int MIN_VIEW = 20;
    static final int MAX_VIEW = 1000;

    //mouse detect and click walking logic
    static int hoverX = -1;
    static int hoverY = -1;
    static java.util.List<Point> path = new java.util.ArrayList<>();
    static boolean followPath = false;

    static int tileSize = 18;
    static int playerScale = 2;

    static String[][] chunk = new String[width][length];
    static String before;

    static boolean up=false, down=false, left=false, right=false;
    static boolean showText=false;

    static String terrain;
    static Random random = new Random();

    static final int ENTITY_ZOOM_THRESHOLD = 8;
    static BufferedImage tumbleweedImage;

    static boolean playerFacingLeft = false;

    public Main() {
        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);
    }
    static java.util.List<Point> findPath(int sx, int sy, int tx, int ty) {
        java.util.List<Point> result = new java.util.ArrayList<>();

        int dx = tx - sx;
        int dy = ty - sy;

        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) return result;

        double xStep = dx / (double) steps;
        double yStep = dy / (double) steps;

        double cx = sx;
        double cy = sy;

        for (int i = 0; i < steps; i++) {
            cx += xStep;
            cy += yStep;

            int ix = (int)Math.round(cx);
            int iy = (int)Math.round(cy);

            if (ix < 0 || iy < 0 || ix >= width || iy >= length)
                break;

            if (!isWalkable(chunk[ix][iy]))
                break;

            result.add(new Point(ix, iy));
        }

        return result;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        int camX = Math.max(0, Math.min(width - viewWidth, x - viewWidth / 2));
        int camY = Math.max(0, Math.min(length - viewLength, y - viewLength / 2));

        g.setFont(new Font("Monospaced", Font.PLAIN, Math.max(8, tileSize - 2)));

        for (int i = 0; i < viewWidth; i++) {
            for (int j = 0; j < viewLength; j++) {
                int wx = i + camX;
                int wy = j + camY;
                if (wx >= width || wy >= length) continue;

                String cell = chunk[wx][wy];
                int px = j * tileSize;
                int py = i * tileSize;

                if (cell.equals("[]")) {
                    continue;
                }

                int h = Integer.parseInt(cell.substring(0, 1));
                char t = cell.charAt(1);

                String cellType = cell.substring(1);

                Color c;

                if (cellType.contains("Grass")) {
                    c = new Color(0, 80 + h * 18, 0);
                }
                else if (cellType.contains("Sand")) {
                    c = new Color(241 - h * 3, 210 - h * 10, 80);
                }
                else if (cellType.contains("Snow")) {
                    c = new Color(200 + h * 10, 230 + h * 6, 255);
                }
                else if (cellType.contains("GlacierIce")) {
                    c = new Color(136 + h * 7, 211 + h * 7, 255);
                }
                else if (cellType.contains("Ice")) {
                    c = new Color(136, 211, 255);
                }
                else if (cellType.contains("Water")) {
                    if (terrain.equals("desert"))
                        c = new Color(0, 122, 128 + h * 6);
                    else
                        c = new Color(0, 0, 120 + h * 10);
                }
                else if (cellType.contains("Flower")) {
                    c = new Color(255, 220, 80);
                }
                else if (cellType.contains("Bedrock")) {
                    c = new Color(45, 42, 42);
                }
                else if (cellType.contains("Cactus")) {
                    c = new Color(32, 145, 32);
                }
                else if (cellType.contains("DeadBush")) {
                    c = new Color(182, 129, 21);
                }
                else if (cellType.contains("PalmTree")) {
                    c = new Color(20, 140, 60);
                }
                else if (cellType.contains("SpruceLeaves1")) {
                    c = new Color(160, 222, 175);
                }
                else if (cellType.contains("SpruceLeaves2")) {
                    c = new Color(155, 191, 162);
                }
                else {
                    c = Color.GRAY;
                }

                g.setColor(c);
                g.fillRect(px, py, tileSize, tileSize);

                if (showText) {
                    g.setColor(Color.BLACK);
                    g.drawString(cell, px + 1, py + tileSize - 2);
                }
                if (wx == hoverX && wy == hoverY) {
                    g.setColor(Color.WHITE);
                    g.fillRect(px, py, tileSize, tileSize);
                }

            }
        }

        int baseX = (y - camY) * tileSize;
        int baseY = (x - camX) * tileSize;

        BufferedImage player = playerSkins[currentSkin];
        if (player != null) {

            int imgWp = player.getWidth();
            int imgHp = player.getHeight();

            int drawX = baseX + tileSize / 2 - imgWp / 2;
            int drawY = baseY + tileSize / 2 - imgHp / 2;

            if (playerFacingLeft) {
                Graphics2D gFlip = (Graphics2D) g2.create();
                gFlip.translate(drawX + imgWp, drawY);
                gFlip.scale(-1, 1);
                gFlip.drawImage(player, 0, 0, null);
                gFlip.dispose();
            } else {
                g2.drawImage(player, drawX, drawY, null);
            }
        }

        g.setColor(Color.LIGHT_GRAY);
        for (Point p : path) {
            int sx = (p.y - camY) * tileSize;
            int sy = (p.x - camX) * tileSize;

            if (sx >= 0 && sy >= 0 &&
                    sx < viewLength * tileSize &&
                    sy < viewWidth * tileSize) {

                g.fillOval(
                        sx + tileSize / 3,
                        sy + tileSize / 3,
                        tileSize / 3,
                        tileSize / 3
                );
            }
        }

        // Only draw entities if zoomed in enough
        if (tileSize >= ENTITY_ZOOM_THRESHOLD) {
            int imgW = 0;
            int imgH = 0;

            // draw penguins
            for (Penguin p : penguins) {
                int screenX = (p.y - camY) * tileSize;
                int screenY = (p.x - camX) * tileSize;

                BufferedImage img = penguinImage;
                if (img == null) continue;

                imgW = img.getWidth();
                imgH = img.getHeight();

                int drawX = screenX + tileSize / 2 - imgW / 2;
                int drawY = screenY + tileSize / 2 - imgH / 2;

                if (screenX + imgW < 0 || screenY + imgH < 0 ||
                        screenX > viewLength * tileSize || screenY > viewWidth * tileSize)
                    continue;

                if (p.facingLeft) {
                    Graphics2D gFlip = (Graphics2D) g2.create();
                    gFlip.translate(drawX + imgW, drawY);
                    gFlip.scale(-1, 1);
                    gFlip.drawImage(img, 0, 0, null);
                    gFlip.dispose();
                } else {
                    g2.drawImage(img, drawX, drawY, null);
                }
            }

            // draw tumbleweeds
            for (Tumbleweed tw : tumbleweeds) {
                int sx = (int)((tw.y - camY) * tileSize);
                int sy = (int)((tw.x - camX) * tileSize);

                BufferedImage img = tumbleweedImage;
                if (img == null) continue;

                imgW = img.getWidth();
                imgH = img.getHeight();

                if (sx < -imgW || sy < -imgH ||
                        sx > viewLength * tileSize || sy > viewWidth * tileSize)
                    continue;

                Graphics2D gRot = (Graphics2D) g2.create();
                gRot.translate(sx + tileSize / 2, sy + tileSize / 2);
                gRot.rotate(tw.angle);
                gRot.drawImage(img, -imgW / 2, -imgH / 2, null);
                gRot.dispose();
            }

            // draw cows
            for (Cow p : cows) {
                int screenX = (p.y - camY) * tileSize;
                int screenY = (p.x - camX) * tileSize;

                BufferedImage img = cowImage;
                if (img == null) continue;

                imgW = img.getWidth();
                imgH = img.getHeight();

                int drawX = screenX + tileSize / 2 - imgW / 2;
                int drawY = screenY + tileSize / 2 - imgH / 2;

                if (screenX + imgW < 0 || screenY + imgH < 0 ||
                        screenX > viewLength * tileSize || screenY > viewWidth * tileSize)
                    continue;

                if (p.facingLeft) {
                    Graphics2D gFlip = (Graphics2D) g2.create();
                    gFlip.translate(drawX + imgW, drawY);
                    gFlip.scale(-1, 1);
                    gFlip.drawImage(img, 0, 0, null);
                    gFlip.dispose();
                } else {
                    g2.drawImage(img, drawX, drawY, null);
                }
            }

            // draw bison
            for (Bison p : bisons) {
                int screenX = (p.y - camY) * tileSize;
                int screenY = (p.x - camX) * tileSize;

                BufferedImage img = bisonImage;
                if (img == null) continue;

                imgW = img.getWidth();
                imgH = img.getHeight();

                int drawX = screenX + tileSize / 2 - imgW / 2;
                int drawY = screenY + tileSize / 2 - imgH / 2;

                if (screenX + imgW < 0 || screenY + imgH < 0 ||
                        screenX > viewLength * tileSize || screenY > viewWidth * tileSize)
                    continue;

                if (p.facingLeft) {
                    Graphics2D gFlip = (Graphics2D) g2.create();
                    gFlip.translate(drawX + imgW, drawY);
                    gFlip.scale(-1, 1);
                    gFlip.drawImage(img, 0, 0, null);
                    gFlip.dispose();
                } else {
                    g2.drawImage(img, drawX, drawY, null);
                }
            }

        }
    }

    static boolean isIce(String cell) {
        return cell.contains("Ice");
    }

    static boolean isWater(String cell) {
        return cell.contains("Water");
    }

    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_W) up = true;
        if (k == KeyEvent.VK_S) down = true;
        if (k == KeyEvent.VK_A) left = true;
        if (k == KeyEvent.VK_D) right = true;
        if (k == KeyEvent.VK_N) showText = !showText;
        if (k == KeyEvent.VK_1) currentSkin = 0;
        if (k == KeyEvent.VK_2) currentSkin = 1;
        if (k == KeyEvent.VK_3) currentSkin = 2;
        if (k == KeyEvent.VK_4) currentSkin = 3;

        if (k == KeyEvent.VK_R) Generation.regenerate();
        if (k == KeyEvent.VK_C) {
            viewWidth = 100;
            viewLength = 110;
        }

        if (k == KeyEvent.VK_EQUALS || k == KeyEvent.VK_ADD) {
            if (viewWidth > MIN_VIEW && viewLength > MIN_VIEW) {
                viewWidth -= 10;
                viewLength -= 10;
                tileSize = Math.min(20, tileSize + 1);
            }
        }

        if (k == KeyEvent.VK_MINUS || k == KeyEvent.VK_SUBTRACT) {
            if (viewWidth < MAX_VIEW && viewLength < MAX_VIEW) {
                viewWidth += 10;
                viewLength += 10;
                tileSize = Math.max(3, tileSize - 1);
            }
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter terrain type (bedrock, plains, desert, snow, glacier):");
        terrain = scanner.nextLine().toLowerCase();
        if (terrain.equals("plains")){
            terrain = "plains";
        } else if (terrain.equals("desert")) {
            terrain = "desert";
        } else if (terrain.equals("snow")) {
            terrain = "snow";
        } else if (terrain.equals("glacier")){
            terrain = "glacier";
        } else {
            terrain = "bedrock";
        }

        try {
            cowImage = ImageIO.read(new File("cow.png"));
            cowPixelColor = new Color(cowImage.getRGB(0, 0), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            penguinImage = ImageIO.read(new File("penguin.png"));
            penguinPixelColor = new Color(penguinImage.getRGB(0, 0), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            bisonImage = ImageIO.read(new File("bison.png"));
            bisonPixelColor = new Color(bisonImage.getRGB(0, 0), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            tumbleweedImage = ImageIO.read(new File("tumbleweed.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            for (int i = 0; i < 4; i++) {
                playerSkins[i] = ImageIO.read(new File("player" + (i + 1) + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Generation.regenerate();

        JFrame frame = new JFrame("Perlin Terrain Viewer");
        Main panel = new Main();
        frame.setContentPane(panel);
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Timer(50, e -> {

            int dx = 0;
            int dy = 0;

            if (up) dx = -1;
            if (down) dx = 1;
            if (left) dy = -1;
            if (right) dy = 1;

            if (dx != 0 || dy != 0) {

                String currentCell = before;
                int steps = 1;

                if (left) playerFacingLeft = true;
                if (right) playerFacingLeft = false;

                // ice move 2 tiles
                if (isIce(currentCell)) {
                    steps = 2;
                }

                // water 30% chance to not move
                if (isWater(currentCell) && random.nextInt(100) < 30) {
                    steps = 0;
                }
                chunk[x][y] = before;

                for (int s = 0; s < steps; s++) {
                    int nx = x + dx;
                    int ny = y + dy;

                    if (nx < 0 || nx >= width || ny < 0 || ny >= length)
                        break;

                    if ((chunk[nx][ny].contains("SpruceLeaves1")) || (chunk[nx][ny].contains("SpruceLeaves2")))
                        break;

                    x = nx;
                    y = ny;
                }

                before = chunk[x][y];
                chunk[x][y] = "[]";
                Penguin.penguinTurn();
                Cow.cowTurn();
                Bison.bisonTurn();
            }
            if (terrain.equals("desert") && random.nextInt(40) == 0) {

                int camX = Math.max(0, Math.min(width - viewWidth, x - viewWidth / 2));
                int camY = Math.max(0, Math.min(length - viewLength, y - viewLength / 2));

                int tx = camX + random.nextInt(viewWidth);
                int ty = camY + random.nextInt(viewLength);

                if (tx >= 0 && ty >= 0 && tx < width && ty < length) {
                    if (chunk[tx][ty].contains("Sand")) {
                        tumbleweeds.add(new Tumbleweed(tx, ty));
                    }
                }
            }
            for (int i = tumbleweeds.size() - 1; i >= 0; i--) {
                Tumbleweed t = tumbleweeds.get(i);

                t.x += t.vx * 0.2;
                t.y += t.vy * 0.2;
                t.angle += t.spin;
                t.life--;

                if (t.life <= 0)
                    tumbleweeds.remove(i);
            }

            if (followPath && !path.isEmpty()) {

                Point next = path.get(0);

                int dirX = Integer.compare(next.x, x);
                int dirY = Integer.compare(next.y, y);
                if (dirY < 0) playerFacingLeft = true;
                if (dirY > 0) playerFacingLeft = false;

                int steps = 1;

                if (isIce(before)) {
                    steps = 2; // slide
                }

                if (isWater(before) && random.nextInt(100) < 30) {
                    steps = 0;
                }

                chunk[x][y] = before;

                for (int s = 0; s < steps; s++) {
                    int nx = x + dirX;
                    int ny = y + dirY;

                    if (nx < 0 || ny < 0 || nx >= width || ny >= length)
                        break;

                    if (!isWalkable(chunk[nx][ny]))
                        break;

                    x = nx;
                    y = ny;

                    // REMOVE path points we passed
                    if (!path.isEmpty() && path.get(0).x == x && path.get(0).y == y) {
                        path.remove(0);
                    }
                }

                before = chunk[x][y];
                chunk[x][y] = "[]";

                Penguin.penguinTurn();
                Cow.cowTurn();
                Bison.bisonTurn();

                if (path.isEmpty())
                    followPath = false;
            }

            panel.repaint();
            up = down = left = right = false;

        }).start();
    }

    //Penguin________________________________________________________
    static java.util.List<Penguin> penguins = new java.util.ArrayList<>();
    static BufferedImage penguinImage;
    static Color penguinPixelColor;

    //Cow________________________________________________________
    static java.util.List<Cow> cows = new java.util.ArrayList<>();
    static BufferedImage cowImage;
    static Color cowPixelColor;

    //Bison________________________________________________________
    static java.util.List<Bison> bisons = new java.util.ArrayList<>();
    static BufferedImage bisonImage;
    static Color bisonPixelColor;

    //Tumbleweed________________________________________________________
    static java.util.List<Tumbleweed> tumbleweeds = new java.util.ArrayList<>();

    //Player------------------------------------------------------------
    static BufferedImage[] playerSkins = new BufferedImage[4];
    static int currentSkin = 0;

    //can walk or not
    static boolean isWalkable(String cell) {
        return !cell.contains("SpruceLeaves1");
    }

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {

        int camX = Math.max(0, Math.min(width - viewWidth, x - viewWidth / 2));
        int camY = Math.max(0, Math.min(length - viewLength, y - viewLength / 2));

        int tileY = e.getX() / tileSize;
        int tileX = e.getY() / tileSize;

        int wx = tileX + camX;
        int wy = tileY + camY;

        if (wx >= 0 && wy >= 0 && wx < width && wy < length) {
            hoverX = wx;
            hoverY = wy;
        } else {
            hoverX = -1;
            hoverY = -1;
        }

        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (hoverX != -1 && hoverY != -1) {
            path = findPath(x, y, hoverX, hoverY);
            followPath = !path.isEmpty();
        }
    }

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

}