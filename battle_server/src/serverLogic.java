import java.util.HashSet;

public class serverLogic {
    static int[][] gameBoard1 = new int[11][11];
    static int[][] gameBoard2 = new int[11][11];
    static int numPlayers = 0;
    static boolean gameReady = false;
    public static void addPlayer() {
        numPlayers++;
    }
    public static boolean gameReady() {
        if(numPlayers == 2) {
            gameReady = true;
        }
        return gameReady;
    }
    public static void addToBoard1(int row, int column, int playerNum) {
        gameBoard1[row][column] = playerNum;
    }
    public static void printGameBoard1() {
        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 11; j++) {
                System.out.print(gameBoard1[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public static void addToBoard2(int row, int column, int playerNum) {
        gameBoard2[row][column] = playerNum;
    }

    public static void printGameBoard2() {
        for (int i = 1; i < 11; i++) {
            for (int j = 1; j < 11; j++) {
                System.out.print(gameBoard2[i][j]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
    public static boolean checkHitMiss (int row, int column, int playerNum, boolean hit) {
        System.out.println("row is: " + row + " and column: " + column + " and count: " + playerNum);
        // this takes care of switching between player 1 and 2
        if(playerNum == 1) {
            playerNum =2;
        } else {
            playerNum =1;
        }
        if (gameBoard1[row][column]==playerNum) {
            System.out.println("Yes this is a Hit!");
            hit = true;
            return hit;
        }
        hit = false;
        System.out.println("No this is a Miss");
        return hit;
    }
}