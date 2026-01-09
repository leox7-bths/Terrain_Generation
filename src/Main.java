import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import static java.lang.Math.random;


public class Main {

    public static class ColorConsole {
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_ORANGE = "\u001B[38;5;208m";
        public static final String ANSI_RED    = "\u001B[31m";
        public static final String ANSI_GREEN  = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE   = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN   = "\u001B[36m";
        public static final String ANSI_WHITE  = "\u001B[37m";
        public static final String ANSI_PINK = "\u001B[38;5;205m";
        public static final String ANSI_BRIGHT_RED   = "\u001B[91m";
        public static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int x = 0;
        int y = 0;
        int temph = 0;
        String tempb = "";
        Random random = new Random();
        int randomIndex = 0;
        int generatenum = 0;
        String before = "";
        List<List<String>> blocks = new ArrayList<>();
        List<String> noblocks = new ArrayList<>();
        List<String> plainsblocks = new ArrayList<>();
        noblocks.add("B");
//        plainsblocks.add("S");
        plainsblocks.add("G");
        plainsblocks.add("W");
//        plainsblocks.add("D");
        blocks.add(noblocks);                                   //noblocks id 0
        blocks.add(plainsblocks);                               //plainsblocks id 2


        //generate chunks
        String input1 = scanner.nextLine();
        ArrayList<String> chunks = getFileData("src/data");
        String[][] chunk = new String[12][16];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 16; j++) {
                if (input1.equals("bedrock")) {
                    temph = 0;
                    tempb = "B";
                    before = "0B";
                } else if (input1.equals("plains")) {
                    temph = (int) (random() * 10);
                    randomIndex = random.nextInt(blocks.get(1).size());
                    tempb = blocks.get(1).get(randomIndex);
                }
                chunk[i][j] = temph + tempb;
            }
        }



        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 16; j++) {
                if (input1.equals("bedrock")) {
                    temph = 0;
                    tempb = "B";
                    before = "0B";
                } else if (input1.equals("plains")) {
                    generatenum = (int) (Math.random() * 10);
                    if ((generatenum == 4)) {
                        //bottom
                        if (i+1 < chunk.length) {
                            System.out.println("bottom");
                            String[] parts = chunk[i+1][j].split("");
                            if (!((int) (Math.random() * 10) == 9) && !((int) (Math.random() * 10) == 10)) {
                                temph = Integer.parseInt(parts[0]);
                            } else {
                                temph = (int) (random() * 10);
                            }
                            if (!((int) (Math.random() * 10) == 9) && !((int) (Math.random() * 10) == 10)) {
                                tempb = parts[1];
                            } else {
                                randomIndex = random.nextInt(blocks.get(1).size());
                                tempb = blocks.get(1).get(randomIndex);
                            }
                        }
                    } else if (generatenum == 3) {
                        //top
                        if (i-1 > 0) {
                            System.out.println("top");
                            String[] parts = chunk[i-1][j].split("");
                            if (!((int) (Math.random() * 10) == 9) && !((int) (Math.random() * 10) == 10)) {
                                temph = Integer.parseInt(parts[0]);
                            } else {
                                temph = (int) (random() * 10);
                            }
                            if (!((int) (Math.random() * 10) == 9) && !((int) (Math.random() * 10) == 10)) {
                                tempb = parts[1];
                            } else {
                                randomIndex = random.nextInt(blocks.get(1).size());
                                tempb = blocks.get(1).get(randomIndex);
                            }
                        }
                    } else if (generatenum == 2) {
                        //right
                        if (j+1 < chunk[i].length) {
                            System.out.println("right");
                            String[] parts = chunk[i][j+1].split("");
                            if (!((int) (Math.random() * 10) == 9) && !((int) (Math.random() * 10) == 10)) {
                                temph = Integer.parseInt(parts[0]);
                            } else {
                                temph = (int) (random() * 10);
                            }
                            if (!((int) (Math.random() * 10) == 9) && !((int) (Math.random() * 10) == 10)) {
                                tempb = parts[1];
                            } else {
                                randomIndex = random.nextInt(blocks.get(1).size());
                                tempb = blocks.get(1).get(randomIndex);
                            }
                        }
                    } else {
                        //left
                        if (j-1 > 0) {
                            System.out.println("left");
                            String[] parts = chunk[i][j-1].split("");
                            if (!((int) (Math.random() * 10) == 9) && !((int) (Math.random() * 10) == 10)) {
                                temph = Integer.parseInt(parts[0]);
                            } else {
                                temph = (int) (random() * 10);
                            }
                            if (!((int) (Math.random() * 10) == 9) && !((int) (Math.random() * 10) == 10)) {
                                tempb = parts[1];
                            } else {
                                randomIndex = random.nextInt(blocks.get(1).size());
                                tempb = blocks.get(1).get(randomIndex);
                            }
                        }
                    }
                }
                chunk[i][j] = temph + tempb;
            }
        }

        before = chunk[6][8];
        chunk[6][8] = "[]";
        y = 8;
        x = 6;

        //remove
        for (int a = 0; a < chunk.length; a++) {
            for (int b = 0; b < chunk[a].length; b++) {
                System.out.print(chunk[a][b] + " ");
            }
            System.out.println();
        }
        System.out.println("");
        //remove

        String[] parts = new String[0];
        String[] parts2 = new String[0];
        while (true) {
            String input2 = scanner.nextLine();

            if (input2.equalsIgnoreCase("q")) {
                System.out.println("Exiting game.");
                break;
            }

            //remove
            for (int a = 0; a < chunk.length; a++) {
                for (int b = 0; b < chunk[a].length; b++) {
                    System.out.print(chunk[a][b] + " ");
                }
                System.out.println();
            }
            System.out.println("");
            //remove

            switch (input2.toLowerCase()) {
                case "w":
                    parts = before.split("");
                    parts2 = chunk[x-1][y].split("");
                    if (Integer.parseInt(parts[0]) < Integer.parseInt(parts2[0])) {
                        if (Integer.parseInt(parts[0]) + 1 != Integer.parseInt(parts2[0])) {
                            break;
                        }
                    }
                    chunk[x][y] = before;
                    x--;
                    before = chunk[x][y];
                    chunk[x][y] = "[]";
                    break;
                case "s":
                    parts = before.split("");
                    parts2 = chunk[x+1][y].split("");
                    if (Integer.parseInt(parts[0]) < Integer.parseInt(parts2[0])) {
                        if (Integer.parseInt(parts[0]) + 1 != Integer.parseInt(parts2[0])) {
                            break;
                        }
                    }
                    chunk[x][y] = before;
                    x++;
                    before = chunk[x][y];
                    chunk[x][y] = "[]";
                    break;
                case "a":
                    parts = before.split("");
                    parts2 = chunk[x][y-1].split("");
                    if (Integer.parseInt(parts[0]) < Integer.parseInt(parts2[0])) {
                        if (Integer.parseInt(parts[0]) + 1 != Integer.parseInt(parts2[0])) {
                            break;
                        }
                    }
                    chunk[x][y] = before;
                    y--;
                    before = chunk[x][y];
                    chunk[x][y] = "[]";
                    break;
                case "d":
                    parts = before.split("");
                    parts2 = chunk[x][y+1].split("");
                    if (Integer.parseInt(parts[0]) < Integer.parseInt(parts2[0])) {
                        if (Integer.parseInt(parts[0]) + 1 != Integer.parseInt(parts2[0])) {
                            break;
                        }
                    }
                    chunk[x][y] = before;
                    y++;
                    before = chunk[x][y];
                    chunk[x][y] = "[]";
                    break;
                default:
                    System.out.println("Invalid input. Use W, A, S, D, or Q.");
            }
        }
    }


    public static int move(int xcords, int ycords) {
        return 0;
    }

    public static ArrayList<String> getFileData(String fileName) {
        ArrayList<String> fileData = new ArrayList<String>();
        try {
            File f = new File(fileName);
            Scanner s = new Scanner(f);
            while (s.hasNextLine()) {
                String line = s.nextLine();
                if (!line.equals(""))
                    fileData.add(line);
            }
            return fileData;
        }
        catch (FileNotFoundException e) {
            return fileData;
        }
    }

}
