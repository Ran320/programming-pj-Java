package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GamePrinter {
    private static final int LEFT_WIDTH = 40;    // 棋盘固定宽度
    private static final int MIDDLE_WIDTH = 25;  // 玩家信息宽度
    private static final int RIGHT_WIDTH = 30;   // 游戏列表宽度
    static GameList gameList;
    public GamePrinter(GameList gameList){
        this.gameList = gameList;
    }

    //清屏
    public static void clearScreen() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    //棋盘部分打印
    private static List<String> getBoardLines(Game currentGame) {
        if (currentGame.getMode().equals("reversi")) {
            return currentGame.board.generateReversiBoardContent(currentGame.getCurrentPlayer());
        } else {
            return currentGame.board.generatePeaceBoardContent();
        }
    }

    //玩家信息部分打印
    private static List<String> getPlayerLines(Game currentGame) {
        List<String> lines = new ArrayList<>();
        String mode = currentGame.getMode();
        int index = currentGame.index+1;

        String p1Line = String.format("玩家[%s]: %s %s",
                currentGame.player1.getName(),
                (gameList.currentPlayer == 1 ? currentGame.player1.getPiece() :"  "),
                (mode.equals("reversi") ? currentGame.board.calculateScore(currentGame.player1.getPiece()) : "")
        );

        String p2Line = String.format("玩家[%s]: %s %s",
                currentGame.player2.getName(),
                (gameList.currentPlayer == 2 ? currentGame.player2.getPiece() :"  "),
                (mode.equals("reversi") ? currentGame.board.calculateScore(currentGame.player2.getPiece()) : "")
        );

        lines.add("棋盘"+index);
        lines.add(p1Line);
        lines.add(p2Line);

        return lines;
    }

    //游戏部分打印
    private static List<String> getGameListLines () {
        List<String> lines = new ArrayList<>();
        lines.add("Game List");
        for (int i = 0; i < gameList.gameListSize(); i++) {
            Game game = gameList.getGame(i);
            String line = String.format("%d. %-7s", i+1, game.getMode());
            lines.add(line);
        }

        lines.add("");
        lines.add("操作说明:");
        lines.add("1.落子：输入行号+列字母（eg：1a），reversi模式时只可在+处落子，只有无可落子位置时才可输入pass跳过本轮");
        lines.add("2.切换棋盘：输入右侧游戏列表中已有的数字（eg：2）");
        lines.add("3.新增棋盘：输入游戏模式名");
        lines.add("4.退出游戏：输入quit");

        return lines;
    }

    //最终打印
    static void printGame() {
            // 获取三栏内容
            List<String> leftLines = getBoardLines(gameList.getCurrentGame());
            List<String> middleLines = getPlayerLines(gameList.getCurrentGame());
            List<String> rightLines = getGameListLines();

            // 计算最大行数
            int maxLines = Math.max(leftLines.size(),
                    Math.max(middleLines.size(), rightLines.size()));

            // 逐行打印
            clearScreen();
            for (int i = 0; i < maxLines; i++) {
                String left = i < leftLines.size() ? leftLines.get(i) : "";
                String middle = i < middleLines.size() ? middleLines.get(i) : "";
                String right = i < rightLines.size() ? rightLines.get(i) : "";

                System.out.printf("%-" + LEFT_WIDTH + "s%-" +   MIDDLE_WIDTH + "s%-" +  RIGHT_WIDTH + "s%n",
                        left, middle, right);
            }
        }
}
