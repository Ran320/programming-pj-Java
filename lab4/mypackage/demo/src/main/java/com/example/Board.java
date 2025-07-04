package com.example;

import java.util.ArrayList;
import java.util.List;

//打印棋盘
public class Board {
    char[][] board = new char[boardSize][boardSize];
    Player player1;
    Player player2;
    int currentPlayer = 0;
    int index = 0;
    int black=0;
    int white=0;

//棋盘本身记录其模式
    public Board(Player player1, Player player2, int currentPlayer, int index) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
        this.index = index;
    }

    public void resetCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public static final char EMPTY = '·';
    public static final char BLACK = '●';
    public static final char WHITE = '○';
    public static final char ABLE = '+';
    public static final int boardSize = 8;
    //八个方向
    public static final int[][] DIRECTIONS = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1}, {1, -1}, {1, 0}, {1, 1}};


    //初始化棋盘
    public void initializeBoard() {
        int center = boardSize / 2;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = EMPTY;
            }
        }
        board[center - 1][center - 1] = player2.getPiece();
        board[center - 1][center] = player1.getPiece();
        board[center][center - 1] = player1.getPiece();
        board[center][center] = player2.getPiece();
    }
    //peace模式打印内容
    public List<String> generatePeaceBoardContent() {
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append("  A B C D E F G H");
        lines.add(sb.toString());

        for (int i = 0; i < boardSize; i++) {
            sb = new StringBuilder();
            sb.append(i + 1 + " ");
            for (int j = 0; j < boardSize; j++) {
                sb.append(board[i][j] + " ");
            }
            lines.add(sb.toString());
        }
        return lines;
    }
    //reversi模式打印内容
    public List<String> generateReversiBoardContent(Player currentPlayer) {
        List<String> lines = new ArrayList<>();
        List<int[]> validMoves = getValid(currentPlayer);
        StringBuilder sb = new StringBuilder();
        sb.append("  A B C D E F G H");
        lines.add(sb.toString());

        for (int i = 0; i < boardSize; i++) {
            sb = new StringBuilder();
            sb.append(i + 1 + " ");
            for (int j = 0; j < boardSize; j++) {
                boolean isValidMove = false;
                for (int[] move : validMoves) {
                    if (move[0] == i && move[1] == j) {
                        isValidMove = true;
                        break;
                    }
                }
                if (isValidMove) {
                    sb.append(ABLE + " ");
                } else {
                    char piece = board[i][j];
                    sb.append(piece + " ");
                }
            }
            lines.add(sb.toString());
        }
        return lines;
    }
    //reversi模式分数计算
    public int calculateScore(char piece) {
        int score = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == piece) {
                    score++;
                }
            }
        }
        return score;
    }

    //检查棋盘是否已满
    public boolean isBoardFull() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    //reversi显示"+"的实现：
    //逻辑：
    // 1.getFlip：从一空白点开始，向八个方向进行遍历寻找对方棋子，并在结尾处有己方棋子的点为落子位置。将所有可翻转的棋子坐标记入一个List
    //              用处：得到下在某一点处所有可翻转棋子的坐标
    // 2.getValid：遍历整个棋盘，查找所有满足1中List不为空的点，即为所有可落子位置，所有位置以二维数组形式记入一个List
    //              用处：得到所有可落子位置坐标
    public List<int[]> getFlip(int x, int y, Player currentPlayer) {
        Player otherPlayer = (currentPlayer.getPiece() == BLACK) ? player2 : player1;
        List<int[]> allFlips = new ArrayList<>();
        for (int[] dir : DIRECTIONS) {
            int dx = dir[0];
            int dy = dir[1];
            int nx = x + dx;
            int ny = y + dy;
            List<int[]> lineFlips = new ArrayList<>();
            //查找连续对方棋子
            while (nx >= 0 && nx < boardSize && ny >= 0 && ny < boardSize) {
                if (board[nx][ny] == otherPlayer.getPiece()) {
                    lineFlips.add(new int[]{nx, ny});
                    nx += dx;
                    ny += dy;
                } else {
                    break;
                }
            }
            //若终点处是己方棋子，则代表此点为可落子位置，所有可翻转棋子记入allFlips
            if (nx >= 0 && nx < boardSize && ny >= 0 && ny < boardSize && board[nx][ny] == currentPlayer.getPiece()) {
                allFlips.addAll(lineFlips);
            }
        }
        return allFlips;
    }

    public List<int[]> getValid(Player currentPlayer){
        List<int[]> ValidMoves = new ArrayList<>();
        for(int i=0;i<boardSize;i++){
            for(int j=0;j<boardSize;j++){
                if(board[i][j]==EMPTY){
                    List<int[]> flips=getFlip(i,j,currentPlayer);
                    if(!flips.isEmpty()){
                        ValidMoves.add(new int[]{i,j});
                    }
                }
            }
        }
        return ValidMoves;
    }
//翻转棋子
    public void flipPiece(int x, int y,Player currentPlayer){
        for(int i=0;i<boardSize;i++){
            for(int j=0;j<boardSize;j++){
                for(int[] move:getFlip(x,y,currentPlayer)){
                    if(move[0]==i&&move[1]==j){
                        board[i][j]= currentPlayer.getPiece();
                        break;
                    }
                }
            }
        }
    }
}

