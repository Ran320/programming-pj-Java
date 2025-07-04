
package com.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("请输入玩家1的名字：");
        String name1 = sc.nextLine();
        System.out.print("请输入玩家2的名字：");
        String name2 = sc.nextLine();
        //创建玩家
        Player player1 = new Player(name1, Board.BLACK);
        Player player2 = new Player(name2, Board.WHITE);

        GameList gameList = new GameList(player1, player2);
        while(true) {
            gameList.startGame();
        }

    }
}