public class Penguin {
    int x, y;
    String under;
    boolean facingLeft = false;

    Penguin(int x, int y, String under) {
        this.x = x;
        this.y = y;
        this.under = under;
    }

    static void penguinTurn() {
        for (Penguin p : Main.penguins) {

            int px = p.x;
            int py = p.y;

            int[] dxs = {-1, 0, 1, 0};
            int[] dys = {0, -1, 0, 1};
            int dir = Main.random.nextInt(4);

            int nx = px + dxs[dir];
            int ny = py + dys[dir];

            p.facingLeft = (dxs[dir] == -1);

            if (nx < 0 || nx >= Main.width || ny < 0 || ny >= Main.length)
                continue;

            String targetCell = Main.chunk[nx][ny];
            if (targetCell.endsWith("Snow") ||
                    targetCell.contains("Ice") ||
                    targetCell.contains("Water")) {

                Main.chunk[px][py] = p.under;

                p.under = Main.chunk[nx][ny];
                p.x = nx;
                p.y = ny;

                Main.chunk[nx][ny] = p.under.charAt(0) + "Penguin";
            }
        }
    }
}