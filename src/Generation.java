public class Generation {
    static void regenerate() {
        Main.penguins.clear();
        PerlinNoise noise = new PerlinNoise(Main.random.nextInt());
        double[][] heightMap = new double[Main.width][Main.length];

        double scale = 0.015;
        int octaves = 5;
        double persistence = 0.5;

        for (int i = 0; i < Main.width; i++) {
            for (int j = 0; j < Main.length; j++) {
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
        for (int i = 0; i < Main.width; i++)
            for (int j = 0; j < Main.length; j++) {
                minH = Math.min(minH, heightMap[i][j]);
                maxH = Math.max(maxH, heightMap[i][j]);
            }

        for (int i = 0; i < Main.width; i++) {
            for (int j = 0; j < Main.length; j++) {

                int maxTerrainHeight = 9; // default
                if (Main.terrain.equals("plains")) maxTerrainHeight = 6;
                if (Main.terrain.equals("desert")) maxTerrainHeight = 6;
                if (Main.terrain.equals("snow")) maxTerrainHeight = 4;
                if (Main.terrain.equals("glacier")) maxTerrainHeight = 5;

                int h = (int)((heightMap[i][j] - minH) / (maxH - minH) * maxTerrainHeight);

                boolean isWater = false;
                boolean isIce = false;

                if (Main.terrain.equals("plains")) {
                    isWater = h <= 1;
                }
                else if (Main.terrain.equals("desert")) {
                    isWater = h <= 0;
                }
                else if (Main.terrain.equals("snow")) {
                    isIce = h <= 0;
                }
                else if (Main.terrain.equals("glacier")) {
                    isWater = h <= 2;
                }

                if (isWater) {
                    Main.chunk[i][j] = h + "Water";
                } else if (isIce) {
                    Main.chunk[i][j] = h + "Ice";
                } else {
                    if (Main.terrain.equals("plains"))
                        Main.chunk[i][j] = h + "Grass";
                    else if (Main.terrain.equals("desert"))
                        Main.chunk[i][j] = h + "Sand";
                    else if (Main.terrain.equals("snow"))
                        Main.chunk[i][j] = h + "Snow";
                    else if (Main.terrain.equals("glacier"))
                        Main.chunk[i][j] = h + "GlacierIce";
                    else
                        Main.chunk[i][j] = h + "Bedrock";
                }
            }
        }

        if (Main.terrain.equals("plains")) {
            for (int i = 0; i < Main.width; i++) {
                for (int j = 0; j < Main.length; j++) {

                    // sand
                    if (i > 0 && i < Main.width - 1 && j > 0 && j < Main.length - 1) {
                        if (Main.chunk[i][j].endsWith("Grass")) {
                            boolean nearWater = false;
                            for (int dx = -1; dx <= 1; dx++) {
                                for (int dy = -1; dy <= 1; dy++) {
                                    if (Main.chunk[i + dx][j + dy].endsWith("Water")) {
                                        nearWater = true;
                                    }
                                }
                            }
                            if (nearWater) {
                                Main.chunk[i][j] = Main.chunk[i][j].charAt(0) + "Sand";
                                continue;
                            }
                        }
                    }

                    // flowers
                    if (Main.chunk[i][j].endsWith("Grass") && Main.random.nextInt(70) == 0) {
                        Main.chunk[i][j] = Main.chunk[i][j].charAt(0) + "Flower";
                    }

                    // cows
                    if (Main.chunk[i][j].endsWith("Grass") && Main.random.nextInt(1500) == 0) {
                        boolean spaceAround = true;
                        // Check adjacent cells for other cows
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                int nx = i + dx;
                                int ny = j + dy;

                                if (nx < 0 || nx >= Main.width || ny < 0 || ny >= Main.length)
                                    continue;

                                if (Main.chunk[nx][ny].endsWith("Cow")) {
                                    spaceAround = false;
                                }
                            }
                        }

                        // inside plains cows placement
                        if (spaceAround) {
                            String original = Main.chunk[i][j];
                            Main.chunk[i][j] = Main.chunk[i][j].charAt(0) + "Cow";
                            Main.cows.add(new Cow(i, j, original));
                        }
                    }
                }
            }
        }

        if (Main.terrain.equals("desert")) {
            for (int i = 1; i < Main.width - 1; i++) {
                for (int j = 1; j < Main.length - 1; j++) {

                    // cactus
                    if (Main.chunk[i][j].endsWith("Sand") && Main.random.nextInt(250) == 0)
                        Main.chunk[i][j] = Main.chunk[i][j].charAt(0) + "Cactus";

                    // dead bush
                    if (Main.chunk[i][j].endsWith("Sand") && Main.random.nextInt(250) == 0)
                        Main.chunk[i][j] = Main.chunk[i][j].charAt(0) + "DeadBush";

                    // palm tree (near water)
                    if (Main.chunk[i][j].endsWith("Sand")) {
                        boolean nearWater = false;

                        for (int dx = -1; dx <= 1; dx++)
                            for (int dy = -1; dy <= 1; dy++)
                                if (Main.chunk[i + dx][j + dy].endsWith("Water"))
                                    nearWater = true;

                        if (nearWater && Main.random.nextInt(10) == 0) {
                            for (int dx = -1; dx <= 1; dx++)
                                for (int dy = -1; dy <= 1; dy++)
                                    Main.chunk[i + dx][j + dy] =
                                            Main.chunk[i][j].charAt(0) + "PalmTree";
                        }
                    }
                }
            }
        }

        if (Main.terrain.equals("snow")) {
            for (int i = 2; i < Main.width - 2; i++) {
                for (int j = 2; j < Main.length - 2; j++) {

                    // spruce tree
                    if (Main.chunk[i][j].endsWith("Snow") && Main.random.nextInt(500) == 0) {
                        // trunk (center)
                        Main.chunk[i][j] = Main.chunk[i][j].charAt(0) + "SpruceLeaves2";

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
                            Main.chunk[x][y] = Main.chunk[x][y].charAt(0) + "SpruceLeaves1";
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
                            Main.chunk[x][y] = Main.chunk[x][y].charAt(0) + "SpruceLeaves2";
                        }
                    }
                    if (Main.chunk[i][j].endsWith("Snow") && Main.random.nextInt(1500) == 0) {
                        boolean spaceAround = true;
                        // Check adjacent cells for other cows
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                int nx = i + dx;
                                int ny = j + dy;

                                if (nx < 0 || nx >= Main.width || ny < 0 || ny >= Main.length)
                                    continue;

                                if (Main.chunk[nx][ny].endsWith("Bison")) {
                                    spaceAround = false;
                                }
                            }
                        }

                        // inside plains Bison placement
                        if (spaceAround) {
                            String original = Main.chunk[i][j];
                            Main.chunk[i][j] = Main.chunk[i][j].charAt(0) + "Bison";
                            Main.bisons.add(new Bison(i, j, original));
                        }
                    }

                }
            }
        }

        if (Main.terrain.equals("glacier")) {
            for (int i = 2; i < Main.width - 2; i++) {
                for (int j = 2; j < Main.length - 2; j++) {


                    // penguins
                    if (Main.chunk[i][j].endsWith("GlacierIce") && Main.random.nextInt(1500) == 0) {
                        boolean spaceAround = true;
                        // Check adjacent cells for other penguins
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                if (Main.chunk[i + dx][j + dy].endsWith("Penguin")) {
                                    spaceAround = false;
                                }
                            }
                        }
                        // inside snow penguin placement
                        if (spaceAround) {
                            String original = Main.chunk[i][j];
                            Main.chunk[i][j] = Main.chunk[i][j].charAt(0) + "Penguin";
                            Main.penguins.add(new Penguin(i, j, original));
                        }
                    }
                }
            }
        }

        Main.before = Main.chunk[Main.x][Main.y];
        Main.chunk[Main.x][Main.y] = "[]";
    }
}
