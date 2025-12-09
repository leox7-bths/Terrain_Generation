import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int x = 0;
        int y = 0;
        ArrayList<String> chunks = getFileData("src/data");
        String[][] chunk = new String[12][16];
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 16; j++) {
                chunk[i][j] = ".";
            }
        }
        chunk[6][8] = "o";
        y = 8;
        x = 6;

        while (true) {
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("q")) {
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

            switch (input.toLowerCase()) {
                case "w":
                    chunk[x][y] = ".";
                    x--;
                    chunk[x][y] = "o";
                    break;
                case "s":
                    chunk[x][y] = ".";
                    x++;
                    chunk[x][y] = "o";
                    break;
                case "a":
                    chunk[x][y] = ".";
                    y--;
                    chunk[x][y] = "o";
                    break;
                case "d":
                    chunk[x][y] = ".";
                    y++;
                    chunk[x][y] = "o";
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
