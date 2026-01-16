import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Scanner;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Main extends JPanel implements KeyListener {

    static int x = 24;
    static int y = 32;
    static int width = 576;
    static int length = 768;

    static int viewWidth = 80;
    static int viewLength = 90;

    static final int MIN_VIEW = viewLength;
    static final int MAX_VIEW = 1000;

    static int tileSize = 12;

    static String[][] chunk = new String[width][length];
    static String before;

    static boolean up=false, down=false, left=false, right=false;
    static boolean showText=false;

    static String terrain;
    static Random random = new Random();

    static final int ENTITY_ZOOM_THRESHOLD = 8;
    static BufferedImage tumbleweedImage;

    public Main() {
        setFocusable(true);
        addKeyListener(this);
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
                    g.setColor(Color.RED);
                    g.fillRect(px, py, tileSize, tileSize);
                    if (showText) {
                        g.setColor(Color.BLACK);
                        g.drawString("[]", px + 1, py + tileSize - 2);
                    }
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

                // Only draw entities if zoomed in enough
                if (tileSize >= ENTITY_ZOOM_THRESHOLD) {

                    // draw penguins
                    int penguinScale = 2;
                    int size = tileSize * penguinScale;

                    for (Penguin p : penguins) {
                        int screenX = (p.y - camY) * tileSize;
                        int screenY = (p.x - camX) * tileSize;

                        if (screenX + size < 0 || screenY + size < 0 ||
                                screenX > viewLength * tileSize || screenY > viewWidth * tileSize)
                            continue;

                        int drawX = screenX - (size - tileSize) / 2;
                        int drawY = screenY - (size - tileSize) / 2;

                        if (p.facingLeft) {
                            g2.drawImage(penguinImage, drawX + size, drawY, -size, size, null);
                        } else {
                            g2.drawImage(penguinImage, drawX, drawY, size, size, null);
                        }
                    }

                    // draw tumbleweeds
                    for (Tumbleweed tw : tumbleweeds) {
                        int sx = (int)((tw.y - camY) * tileSize);
                        int sy = (int)((tw.x - camX) * tileSize);

                        if (sx < -40 || sy < -40 ||
                                sx > viewLength * tileSize || sy > viewWidth * tileSize)
                            continue;

                        Graphics2D gRot = (Graphics2D) g2.create();
                        gRot.translate(sx + tileSize/2, sy + tileSize/2);
                        gRot.rotate(tw.angle);

                        int twSize = tileSize * 2;
                        gRot.drawImage(tumbleweedImage, -twSize/2, -twSize/2, twSize, twSize, null);

                        gRot.dispose();
                    }

                    // draw cows
                    int cowScale = 4;
                    int size2 = tileSize * cowScale;

                    for (Cow p : cows) {
                        int screenX = (p.y - camY) * tileSize;
                        int screenY = (p.x - camX) * tileSize;

                        if (screenX + size2 < 0 || screenY + size2 < 0 ||
                                screenX > viewLength * tileSize || screenY > viewWidth * tileSize)
                            continue;

                        int drawX = screenX - (size2 - tileSize) / 2;
                        int drawY = screenY - (size2 - tileSize) / 2;

                        if (p.facingLeft) {
                            g2.drawImage(cowImage, drawX + size2, drawY, -size2, size2, null);
                        } else {
                            g2.drawImage(cowImage, drawX, drawY, size2, size2, null);
                        }
                    }

                }

                if (showText) {
                    g.setColor(Color.BLACK);
                    g.drawString(cell, px + 1, py + tileSize - 2);
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

        if (k == KeyEvent.VK_R) regenerate();
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

    static void regenerate() {
        penguins.clear();
        PerlinNoise noise = new PerlinNoise(random.nextInt());
        double[][] heightMap = new double[width][length];

        double scale = 0.015;
        int octaves = 5;
        double persistence = 0.5;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {
                double amplitude = 1;
                double frequency = 1;
                double h = 0;

                for (int o = 0; o < octaves; o++) {
                    h += noise.noise(i * scale * frequency, j * scale * frequency) * amplitude;
                    amplitude *= persistence;
                    frequency *= 2;
                }
                heightMap[i][j] = h;
            }
        }

        double minH = Double.MAX_VALUE;
        double maxH = Double.MIN_VALUE;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < length; j++) {
                minH = Math.min(minH, heightMap[i][j]);
                maxH = Math.max(maxH, heightMap[i][j]);
            }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < length; j++) {

                int maxTerrainHeight = 9; // default
                if (terrain.equals("plains")) maxTerrainHeight = 6;
                if (terrain.equals("desert")) maxTerrainHeight = 6;
                if (terrain.equals("snow")) maxTerrainHeight = 4;
                if (terrain.equals("glacier")) maxTerrainHeight = 5;

                int h = (int)((heightMap[i][j] - minH) / (maxH - minH) * maxTerrainHeight);

                boolean isWater = false;
                boolean isIce = false;

                if (terrain.equals("plains")) {
                    isWater = h <= 1;
                }
                else if (terrain.equals("desert")) {
                    isWater = h <= 0;
                }
                else if (terrain.equals("snow")) {
                    isIce = h <= 0;
                }
                else if (terrain.equals("glacier")) {
                    isWater = h <= 2;
                }

                if (isWater) {
                    chunk[i][j] = h + "Water";
                } else if (isIce) {
                    chunk[i][j] = h + "Ice";
                } else {
                    if (terrain.equals("plains"))
                        chunk[i][j] = h + "Grass";
                    else if (terrain.equals("desert"))
                        chunk[i][j] = h + "Sand";
                    else if (terrain.equals("snow"))
                        chunk[i][j] = h + "Snow";
                    else if (terrain.equals("glacier"))
                        chunk[i][j] = h + "GlacierIce";
                    else
                        chunk[i][j] = h + "Bedrock";
                }
            }
        }

        if (terrain.equals("plains")) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < length; j++) {

                    // sand
                    if (i > 0 && i < width - 1 && j > 0 && j < length - 1) {
                        if (chunk[i][j].endsWith("Grass")) {
                            boolean nearWater = false;
                            for (int dx = -1; dx <= 1; dx++) {
                                for (int dy = -1; dy <= 1; dy++) {
                                    if (chunk[i + dx][j + dy].endsWith("Water")) {
                                        nearWater = true;
                                    }
                                }
                            }
                            if (nearWater) {
                                chunk[i][j] = chunk[i][j].charAt(0) + "Sand";
                                continue;
                            }
                        }
                    }

                    // flowers
                    if (chunk[i][j].endsWith("Grass") && random.nextInt(70) == 0) {
                        chunk[i][j] = chunk[i][j].charAt(0) + "Flower";
                    }

                    // cows
                    if (chunk[i][j].endsWith("Grass") && random.nextInt(1000) == 0) {
                        boolean spaceAround = true;
                        // Check adjacent cells for other cows
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                int nx = i + dx;
                                int ny = j + dy;

                                if (nx < 0 || nx >= width || ny < 0 || ny >= length)
                                    continue;

                                if (chunk[nx][ny].endsWith("Cow")) {
                                    spaceAround = false;
                                }
                            }
                        }

                        // inside plains cows placement
                        if (spaceAround) {
                            String original = chunk[i][j];
                            chunk[i][j] = chunk[i][j].charAt(0) + "Cow";
                            cows.add(new Cow(i, j, original));
                        }
                    }
                }
            }
        }

        if (terrain.equals("desert")) {
            for (int i = 1; i < width - 1; i++) {
                for (int j = 1; j < length - 1; j++) {

                    // cactus
                    if (chunk[i][j].endsWith("Sand") && random.nextInt(250) == 0)
                        chunk[i][j] = chunk[i][j].charAt(0) + "Cactus";

                    // dead bush
                    if (chunk[i][j].endsWith("Sand") && random.nextInt(250) == 0)
                        chunk[i][j] = chunk[i][j].charAt(0) + "DeadBush";

                    // palm tree (near water)
                    if (chunk[i][j].endsWith("Sand")) {
                        boolean nearWater = false;

                        for (int dx = -1; dx <= 1; dx++)
                            for (int dy = -1; dy <= 1; dy++)
                                if (chunk[i + dx][j + dy].endsWith("Water"))
                                    nearWater = true;

                        if (nearWater && random.nextInt(10) == 0) {
                            for (int dx = -1; dx <= 1; dx++)
                                for (int dy = -1; dy <= 1; dy++)
                                    chunk[i + dx][j + dy] =
                                            chunk[i][j].charAt(0) + "PalmTree";
                        }
                    }
                }
            }
        }

        if (terrain.equals("snow")) {
            for (int i = 2; i < width - 2; i++) {
                for (int j = 2; j < length - 2; j++) {

                    // spruce tree
                    if (chunk[i][j].endsWith("Snow") && random.nextInt(500) == 0) {
                        // trunk (center)
                        chunk[i][j] = chunk[i][j].charAt(0) + "SpruceLeaves2";

                        // outer light leaves
                        int[][] lightLeaves = {
                                {-2, -1}, {-2, 0}, {-2, 1},
                                {-1, -2}, {-1, -1}, {-1, 1}, {-1, 2},
                                {0, -2},  {0, 2},
                                {1, -2},  {1, -1},  {1, 1},  {1, 2},
                                {2, -1},  {2, 0},  {2, 1}
                        };

                        for (int[] o : lightLeaves) {
                            int x = i + o[0];
                            int y = j + o[1];
                            chunk[x][y] = chunk[x][y].charAt(0) + "SpruceLeaves1";
                        }

                        // inner dark leaves
                        int[][] darkLeaves = {
                                {-1, 0},
                                {0, -1}, {0, 1},
                                {1, 0}
                        };

                        for (int[] o : darkLeaves) {
                            int x = i + o[0];
                            int y = j + o[1];
                            chunk[x][y] = chunk[x][y].charAt(0) + "SpruceLeaves2";
                        }
                    }
                }
            }
        }

        if (terrain.equals("glacier")) {
            for (int i = 2; i < width - 2; i++) {
                for (int j = 2; j < length - 2; j++) {


                    // penguins
                    if (chunk[i][j].endsWith("GlacierIce") && random.nextInt(700) == 0) {
                        boolean spaceAround = true;
                        // Check adjacent cells for other penguins
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                if (chunk[i + dx][j + dy].endsWith("Penguin")) {
                                    spaceAround = false;
                                }
                            }
                        }
                        // inside snow penguin placement
                        if (spaceAround) {
                            String original = chunk[i][j];
                            chunk[i][j] = chunk[i][j].charAt(0) + "Penguin";
                            penguins.add(new Penguin(i, j, original));
                        }
                    }
                }
            }
        }

        before = chunk[x][y];
        chunk[x][y] = "[]";
    }

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
            tumbleweedImage = ImageIO.read(new File("tumbleweed.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        regenerate();

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

                    x = nx;
                    y = ny;
                }

                before = chunk[x][y];
                chunk[x][y] = "[]";
                penguinTurn();
                cowTurn();
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

            panel.repaint();
            up = down = left = right = false;

        }).start();
    }


    static void penguinTurn() {
        for (Penguin p : penguins) {
            int px = p.x;
            int py = p.y;

            // Random direction
            int[] dxs = {-1, 0, 1, 0};
            int[] dys = {0, -1, 0, 1};
            int dir = random.nextInt(4);
            int nx = px + dxs[dir];
            int ny = py + dys[dir];
            p.facingLeft = (dys[dir] == -1);

            // Bounds check
            if (nx < 0 || nx >= width || ny < 0 || ny >= length) continue;

            // Only move on walkable terrain
            String targetCell = chunk[nx][ny];
            if (targetCell.endsWith("Snow") || targetCell.contains("Ice") ||
                    targetCell.contains("Water")) {

                // Restore previous cell
                chunk[px][py] = p.under;

                // Save new cell under penguin
                p.under = chunk[nx][ny];
                p.x = nx;
                p.y = ny;

                // Place penguin
                chunk[nx][ny] = p.under.charAt(0) + "Penguin";
            }
        }
    }

    static class Penguin {
        int x, y;
        String under;
        boolean facingLeft = false;

        Penguin(int x, int y, String under) {
            this.x = x;
            this.y = y;
            this.under = under;
        }
    }

    static java.util.List<Penguin> penguins = new java.util.ArrayList<>();
    static BufferedImage penguinImage;
    static Color penguinPixelColor;

    //Cow________________________________________________________
    static void cowTurn() {
        for (Cow p : cows) {
            int px = p.x;
            int py = p.y;


            // Random direction
            int[] dxs = {-1, 0, 1, 0};
            int[] dys = {0, -1, 0, 1};
            int dir = random.nextInt(4);
            int nx = px + dxs[dir];
            int ny = py + dys[dir];
            p.facingLeft = (dys[dir] == -1);

            // Bounds check
            if (nx < 0 || nx >= width || ny < 0 || ny >= length) continue;

            // Only move on walkable terrain
            String targetCell = chunk[nx][ny];
            if (targetCell.endsWith("Grass")) {

                // Restore previous cell
                chunk[px][py] = p.under;

                // Save new cell under cow
                p.under = chunk[nx][ny];
                p.x = nx;
                p.y = ny;

                // Place cow
                chunk[nx][ny] = p.under.charAt(0) + "Cow";
            }
        }
    }

    static class Cow {
        int x, y;
        String under;
        boolean facingLeft = false;

        Cow(int x, int y, String under) {
            this.x = x;
            this.y = y;
            this.under = under;
        }
    }

    static java.util.List<Cow> cows = new java.util.ArrayList<>();
    static BufferedImage cowImage;
    static Color cowPixelColor;

    //Tumbleweed-----------------------------------------------------------------------------------------------
    static class Tumbleweed {
        double x, y;
        double vx, vy;
        double angle;
        double spin;
        int life;

        Tumbleweed(double x, double y) {
            this.x = x;
            this.y = y;
            vx = random.nextDouble() * 0.6 + 0.4;
            vy = random.nextDouble() * 0.4 - 0.2;
            spin = random.nextDouble() * 0.2 + 0.1;
            life = 200 + random.nextInt(200);
        }
    }
    static java.util.List<Tumbleweed> tumbleweeds = new java.util.ArrayList<>();

}

class PerlinNoise {
    private final int[] p = new int[512];

    public PerlinNoise(int seed) {
        Random rand = new Random(seed);
        int[] perm = new int[256];
        for (int i = 0; i < 256; i++) perm[i] = i;
        for (int i = 255; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int tmp = perm[i];
            perm[i] = perm[j];
            perm[j] = tmp;
        }
        for (int i = 0; i < 512; i++) p[i] = perm[i & 255];
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 3;
        return ((h & 1) == 0 ? x : -x) + ((h & 2) == 0 ? y : -y);
    }

    public double noise(double x, double y) {
        int X = (int)Math.floor(x) & 255;
        int Y = (int)Math.floor(y) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);

        double u = fade(x);
        double v = fade(y);

        int A = p[X] + Y;
        int B = p[X + 1] + Y;

        return lerp(v,
                lerp(u, grad(p[A], x, y), grad(p[B], x - 1, y)),
                lerp(u, grad(p[A + 1], x, y - 1), grad(p[B + 1], x - 1, y - 1))
        );
    }
}