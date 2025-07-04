package com.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        //创建玩家
        Player player1 = new Player("Tom", Board.BLACK);
        Player player2 = new Player("Jerry", Board.WHITE);

        GameList gameList = new GameList(player1, player2);
        while(true) {
            gameList.startGame();
        }

    }
}