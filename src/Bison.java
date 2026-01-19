public class Bison {
    int x, y;
    String under;
    boolean facingLeft = false;

    int stepCounter = 0;
    static final int MOVE_SCALE = 3;

    Bison(int x, int y, String under) {
        this.x = x;
        this.y = y;
        this.under = under;
    }

    static void bisonTurn() {
        for (Bison p : Main.bisons) {

            p.stepCounter++;
            if (p.stepCounter < MOVE_SCALE) continue;
            p.stepCounter = 0;

            int px = p.x;
            int py = p.y;

            // Random direction
            int WALK_RADIUS = 30;
            int tx = px + Main.random.nextInt(WALK_RADIUS * 2 + 1) - WALK_RADIUS;
            int ty = py + Main.random.nextInt(WALK_RADIUS * 2 + 1) - WALK_RADIUS;

            tx = Math.max(0, Math.min(Main.width - 1, tx));
            ty = Math.max(0, Math.min(Main.length - 1, ty));

            // move ONE step toward target
            int dx = Integer.compare(tx, px);
            int dy = Integer.compare(ty, py);

            int nx = px + dx;
            int ny = py + dy;

            p.facingLeft = (dy < 0);

            // Bounds check
            if (nx < 0 || nx >= Main.width || ny < 0 || ny >= Main.length)
                continue;

            // Only move on walkable terrain
            String targetCell = Main.chunk[nx][ny];
            if (targetCell.endsWith("Snow")) {

                // Restore previous cell
                Main.chunk[px][py] = p.under;

                // Save new cell under Bison
                p.under = Main.chunk[nx][ny];
                p.x = nx;
                p.y = ny;

                // Place Bison
                Main.chunk[nx][ny] = p.under.charAt(0) + "Bison";
            }
        }
    }
}