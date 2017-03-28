package com.YoungSong;

import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int numOfDeck = 8;
        PlayGame game = new PlayGame(6, numOfDeck);
        String choice = "";
        boolean doWhile = true;
        while (doWhile) {
            mainMenu();
            choice = scanner.nextLine().toUpperCase().trim();
            switch (choice) {
                case "Q":
                    doWhile = false;
                    break;
                case "1":
                    for (int i = 0; i < 10000; i++) {
                        game.startGame();
                    }
                    break;
                case "2":
                    game.addPlayer();
                    break;
                case "3":
                    game.removePlayer();
                    break;
                case "4":
                    game.getMoreChips();
                    break;
                case "5":
                    game.showScore();
                    break;
                case "6":
                    game.shuffleCards(numOfDeck);
                    break;
                case "7":
                    break;
            }
        }
    }

    private static void mainMenu() {
        System.out.println("\n- - - Main Menu - - -\n" +
                "  1. Start BlackJack \n" +
                "  2. Add player \n" +
                "  3. Remove Player \n" +
                "  4. Get more chips \n" +
                "  5. Show the score \n" +
                "  6. Shuffle \n" +
                "  Q. Quit the game \n");
        System.out.print("Enter your choice ... ");
    }
}

