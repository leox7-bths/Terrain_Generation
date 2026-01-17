public class Tumbleweed {
    double x, y;
    double vx, vy;
    double angle;
    double spin;
    int life;

    Tumbleweed(double x, double y) {
        this.x = x;
        this.y = y;
        vx = Main.random.nextDouble() * 0.6 + 0.4;
        vy = Main.random.nextDouble() * 0.4 - 0.2;
        spin = Main.random.nextDouble() * 0.2 + 0.1;
        life = 200 + Main.random.nextInt(200);
    }
}