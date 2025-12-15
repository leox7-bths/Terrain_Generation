import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int x = 0;
        int y = 0;
        int temph = 0;
        String tempb = "";
        Random random = new Random();
        int randomIndex = 0;
        List<List<String>> blocks = new ArrayList<>();
        List<String> noblocks = new ArrayList<>();
        List<String> plainsblocks = new ArrayList<>();
        noblocks.add("B");
        plainsblocks.add("S");
        plainsblocks.add("G");
        plainsblocks.add("W");
        plainsblocks.add("D");
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
                } else if (input1.equals("plains")) {
                    temph = (int) (Math.random() * 10);
                    randomIndex = random.nextInt(blocks.get(1).size());
                    tempb = blocks.get(1).get(randomIndex);
                }
                chunk[i][j] = temph + tempb;

            }
        }

        chunk[6][8] = "[]";
        y = 8;
        x = 6;
        String before = "0B";

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
                    chunk[x][y] = before;
                    x--;
                    before = chunk[x][y];
                    chunk[x][y] = "[]";
                    break;
                case "s":
                    chunk[x][y] = before;
                    x++;
                    before = chunk[x][y];
                    chunk[x][y] = "[]";
                    break;
                case "a":
                    chunk[x][y] = before;
                    y--;
                    before = chunk[x][y];
                    chunk[x][y] = "[]";
                    break;
                case "d":
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
