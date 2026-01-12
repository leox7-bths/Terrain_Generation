import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.Scanner;

public class Main extends JPanel implements KeyListener {
    static int x = 24;
    static int y = 32;
    static int width = 576;
    static int length = 768;

    static int viewWidth = 100;
    static int viewLength = 200;

    static final int MIN_VIEW = 20;
    static final int MAX_VIEW = 700;

    static int tileSize = 12;

    static String[][] chunk = new String[width][length];
    static String before;

    static boolean up=false, down=false, left=false, right=false;
    static boolean showText=false;

    public Main() {
        setFocusable(true);
        addKeyListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

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

                Color c;
                if (t == 'G') c = new Color(0, 80 + h * 18, 0);
                else if (t == 'S') c = new Color(220, 200, 130);
                else if (t == 'W') c = new Color(0, 0, 120 + h * 10);
                else if (t == 'F') c = new Color(255, 220, 80);
                else if (t == 'B') c = new Color(80, 80, 80);
                else c = Color.GRAY;

                g.setColor(c);
                g.fillRect(px, py, tileSize, tileSize);

                if (showText) {
                    g.setColor(Color.BLACK);
                    g.drawString(cell, px + 1, py + tileSize - 2);
                }
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();

        if (k == KeyEvent.VK_W) up = true;
        if (k == KeyEvent.VK_S) down = true;
        if (k == KeyEvent.VK_A) left = true;
        if (k == KeyEvent.VK_D) right = true;
        if (k == KeyEvent.VK_N) showText = !showText;

        if (k == KeyEvent.VK_MINUS || k == KeyEvent.VK_SUBTRACT) {
            if (viewWidth < MAX_VIEW && viewLength < MAX_VIEW) {
                viewWidth += 10;
                viewLength += 10;
                tileSize = Math.max(3, tileSize - 1);
            }
        }

        if (k == KeyEvent.VK_EQUALS || k == KeyEvent.VK_ADD) {
            if (viewWidth > MIN_VIEW && viewLength > MIN_VIEW) {
                viewWidth -= 10;
                viewLength -= 10;
                tileSize = Math.min(20, tileSize + 1);
            }
        }
    }

    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter terrain type (bedrock or plains):");
        String terrain = scanner.nextLine().toLowerCase();
        if (!terrain.equals("bedrock") && !terrain.equals("plains")) terrain = "plains";

        Random random = new Random();
        double[][] heightMap = new double[width][length];

        for (int i = 0; i < width; i++)
            for (int j = 0; j < length; j++)
                heightMap[i][j] =
                        Math.sin(i / 18.0) +
                                Math.cos(j / 18.0) +
                                0.6 * Math.sin(i / 8.0) * Math.cos(j / 8.0) +
                                random.nextDouble();

        double minH = 999, maxH = -999;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < length; j++) {
                minH = Math.min(minH, heightMap[i][j]);
                maxH = Math.max(maxH, heightMap[i][j]);
            }

        for (int i = 0; i < width; i++)
            for (int j = 0; j < length; j++) {
                int h = (int) ((heightMap[i][j] - minH) / (maxH - minH) * 9);
                chunk[i][j] = h + (terrain.equals("bedrock") ? "B" : "G");
            }

        int numLakes = width * length / 18000;

        for (int l = 0; l < numLakes; l++) {
            int cx = random.nextInt(width - 60) + 30;
            int cy = random.nextInt(length - 60) + 30;
            double angle = random.nextDouble() * Math.PI * 2;
            int steps = 120 + random.nextInt(20);
            int radius = 3 + random.nextInt(5);

            for (int s = 0; s < steps; s++) {
                angle += (random.nextDouble() - 0.5) * 0.2;
                int wx = cx + (int) (Math.cos(angle) * s);
                int wy = cy + (int) (Math.sin(angle) * s);
                if (wx < 8 || wy < 8 || wx >= width - 8 || wy >= length - 8) continue;

                radius += random.nextInt(3) - 1;
                radius = Math.max(2, Math.min(6, radius));

                for (int dx = -radius; dx <= radius; dx++)
                    for (int dy = -radius; dy <= radius; dy++)
                        if (dx * dx + dy * dy <= radius * radius) {
                            int nx = wx + dx, ny = wy + dy;
                            int h = Integer.parseInt(chunk[nx][ny].substring(0, 1));
                            chunk[nx][ny] = h + "W";
                        }
            }
        }

        for (int layer = 0; layer < 5; layer++) {
            String[][] tmp = new String[width][length];
            for (int i = 0; i < width; i++)
                for (int j = 0; j < length; j++)
                    tmp[i][j] = chunk[i][j];

            for (int i = 1; i < width - 1; i++)
                for (int j = 1; j < length - 1; j++)
                    if (chunk[i][j].endsWith("G")) {
                        boolean near = false;
                        for (int di = -1; di <= 1; di++)
                            for (int dj = -1; dj <= 1; dj++)
                                if (chunk[i + di][j + dj].endsWith("W")) near = true;
                        if (near) {
                            int h = Integer.parseInt(chunk[i][j].substring(0, 1));
                            tmp[i][j] = h + "S";
                        }
                    }
            chunk = tmp;
        }

        for (int i = 0; i < width; i++)
            for (int j = 0; j < length; j++)
                if (chunk[i][j].endsWith("G") && random.nextInt(70) == 0) {
                    int h = Integer.parseInt(chunk[i][j].substring(0, 1));
                    chunk[i][j] = h + "F";
                }

        before = chunk[x][y];
        chunk[x][y] = "[]";

        JFrame frame = new JFrame("Terrain Viewer");
        Main panel = new Main();
        frame.setContentPane(panel);
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        new Timer(50, e -> {
            chunk[x][y] = before;
            if (up && x > 0) x--;
            if (down && x < width - 1) x++;
            if (left && y > 0) y--;
            if (right && y < length - 1) y++;
            before = chunk[x][y];
            chunk[x][y] = "[]";
            panel.repaint();
            up = down = left = right = false;
        }).start();
    }
}
