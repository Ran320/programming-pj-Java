package org.example.game;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import static org.example.game.Board.*;

//游戏运行

public class Game implements Serializable{
    Player player1;
    Player player2;
    public Board board;
    String mode;
    GameList games;
    private Queue<String> commandQueue = new LinkedList<>(); // 存储命令的队列
    int round=1;
    boolean isGomokuEnd = false;
    public boolean isValidMove = false;//用户成功落子
    boolean isValidChange = false;
    public boolean isValidBomb = false;
    boolean isValidPass = false;
    boolean isValidAdd = false;
    boolean isGameEnd = false;
    int bombNum1=2;
    int bombNum2=3;
    String endTxt;
    String player1State;
    String player2State;
    private boolean bombMode = false;
    int index, newIndex;//分别表示当前棋盘索引，用户新选择的棋盘索引
    public Game(Player player1,Player player2,String mode,int index, GameList games){
        this.player1=player1;
        this.player2=player2;
        this.board=new Board(player1, player2, games.getCurrentPlayer(), index);
        this.mode=mode;
        this.index=index;
        this.games=games;
    }

    public String getMode(){
        return mode;
    }

    public int getIndex(){
        return index;
    }

    public boolean getIsValidPass(){
        return isValidPass;
    }

    public boolean isBombMode() {
        return bombMode;
    }

    public void setBombMode(boolean bombMode) {
        this.bombMode = bombMode;
    }
    //获取棋盘大小
    public int getSize() {
        return "gomoku".equals(mode) ? gomokuBoardSize : boardSize;
    }


    public boolean getIsValidMove(){
        return isValidMove;
    }

    //获取棋子
    public char getPiece(int i,int j,Player player){
        if(mode.equals("peace")){
            return board.getPeacePiece(i,j);
        }
        else if(mode.equals("reversi")){
            return board.getReversiPiece(i,j,player);
        }
        else{
            return board.getGomokuPiece(i,j);
        }
    }

    //获取炸弹数量
    public int getBomb(Player currentPlayer){
        if(currentPlayer.getPiece()==player1.getPiece()){
            return bombNum1;
        }
        else{
            return bombNum2;
        }
    }

    //游戏是否结束
    public boolean getIsGameEnd(){
        isGameEnd = false;
        if(mode.equals("reversi")){
            if(board.isBoardFull()||(board.getValid(player1).isEmpty()&&board.getValid(player2).isEmpty())){
                isGameEnd = true;
            }
        }
        else if(mode.equals("peace")){
            if(board.isBoardFull()){
                isGameEnd = true;
            }
        }
        else{
            if(board.isGomokuBoardFull()||isGomokuEnd){
                isGameEnd = true;
            }
        }
        return isGameEnd;
    }

    public String getPlayer1State(){
        StringBuilder sb = new StringBuilder();
        sb.append("玩家["+player1.getName()+"]:"+(games.currentPlayer==1?player1.getPiece():"  "));
        if(mode.equals("reversi")){
            sb.append(board.calculateScore(player1.getPiece()));
        }
        if(mode.equals("gomoku")){
            sb.append(" bomb*"+bombNum1);
        }
        player1State = sb.toString();
        return player1State;
    }

    public String getPlayer2State(){
        StringBuilder sb = new StringBuilder();
        sb.append("玩家["+player2.getName()+"]:"+(games.currentPlayer==2?player2.getPiece():"  "));
        if(mode.equals("reversi")){
            sb.append(board.calculateScore(player2.getPiece()));
        }
        if(mode.equals("gomoku")){
            sb.append(" bomb*"+bombNum2);
        }
        player2State = sb.toString();
        return player2State;
    }

    public int getCurrentRound(){
        if(round%2==0){
            return round/2;
        }
        else{
            return (round+1)/2;
        }
    }

    public String getEndTxt(){
        if(mode.equals("peace")){
            endTxt = "此棋盘已满！\n请更换棋盘游玩！";
        }
        else if(mode.equals("reversi")){
            StringBuilder sb = new StringBuilder();
            sb.append("本局游戏结束！\n可输入数字切换棋盘或添加游戏棋盘\n游戏结果： " + "玩家[").append(player1.getName()).append("]").append(player1.getPiece()).append("得分：").append(board.calculateScore(player1.getPiece())).append("\n").append("\t\t").append("玩家[").append(player2.getName()).append("]").append(player2.getPiece()).append("得分：").append(board.calculateScore(player2.getPiece())).append("\n");
            if(board.calculateScore(BLACK)> board.calculateScore(WHITE)){
                sb.append("黑方:玩家[").append(player1.getName()).append("]获胜！");
            }
            else if(board.calculateScore(BLACK)< board.calculateScore(WHITE)){
                sb.append("白方:玩家[").append(player2.getName()).append("]获胜！");
            }
            else sb.append("双方平局！");
            endTxt = sb.toString();
        }
        else{
            StringBuilder stringBuilder = new StringBuilder();
            Player winPlayer = games.getCurrentPlayer()==1?player2:player1;
            if(board.isGomokuBoardFull()){
                stringBuilder.append("棋盘已满！\n");
            }
            else{
                stringBuilder.append("玩家["+winPlayer.getName()+"]获胜！\n");
            }
            stringBuilder.append("游戏结束！此时不可再落子，可切换棋盘或添加游戏棋盘");
            endTxt = stringBuilder.toString();
        }
        return endTxt;
    }

    //获取输入/命令
    public String getNextCommand(){
        //System.out.println("getnextcommand");
        if (!commandQueue.isEmpty()) {
            return commandQueue.poll();
        } else {
            Scanner sc = new Scanner(System.in);
            return sc.nextLine();
        }
    }

    // 处理用户输入和命令
    public void handleInput(String input,Player player) {
            isValidMove=false;
            isValidChange = false;
            isValidBomb = false;
            isValidPass = false;
            isValidAdd = false;
            newIndex = games.currentGameIndex;
            int length = input.length();
            if (input.equals("quit")) {
                System.exit(0);
            } else if (input.equals("peace")) {
                isValidAdd = true;
                games.addNewGame("peace");
            } else if (input.equals("reversi")) {
                isValidAdd = true;
                games.addNewGame("reversi");
            } else if (input.equals("gomoku")) {
                games.addNewGame("gomoku");
                isValidAdd = true;
            }else if(input.equals("pass")&&"reversi".equals(mode)){
                if(!board.getValid(player).isEmpty()){
                    System.out.println("不允许放弃本轮行棋！");
                }
                else{
                    isValidPass = true;
                }
            } else if (length == 1) {
                handleSingleCharInput(input);
            } else if (length == 2) {
                handleTwoCharInput(player, input);
            } else if (length == 3 && input.charAt(0) == '@') {
                if ("gomoku".equals(this.mode)) {
                    int row = Character.digit(input.charAt(1), 16) - 1;
                    int col = Character.toUpperCase(input.charAt(2)) - 'A';
                    handleBombCommand(player, row,col);
                }
            } else {
                System.out.println("输入格式有误，请重新输入");
            }
    }

    // 处理单字符输入：换棋盘
    private void handleSingleCharInput(String input) {
        int ch = input.charAt(0) - '0';
        if (ch > 0 && ch <= games.gameListSize()) {
            newIndex = ch - 1;
            if(newIndex!=index){
                isValidChange = true;
            }
            else{
                System.out.println("如想更换棋盘请输入非本棋盘序号！");
            }
        } else {
            System.out.println("输入格式有误，请重新输入");
        }
    }

    // 处理双字符输入：换棋盘或落子
    private void handleTwoCharInput(Player player, String input) {
        int row = Character.digit(input.charAt(0), 16) - 1;
        int col = Character.toUpperCase(input.charAt(1)) - 'A';
        //System.out.println("handletwocharinput");
        handleMove(player,row,col);
    }

    // 处理炸弹命令
    public void handleBombCommand(Player player,int row, int col) {
        isValidBomb = false;
        isValidMove = false;

        if (getBomb(player) > 0&&row >= 0 && row < gomokuBoardSize && col >= 0 && col < gomokuBoardSize) {
            Player otherPlayer = (player.getPiece() == player1.getPiece()) ? player2 : player1;
            if (board.gomokuBoard[row][col] == otherPlayer.getPiece()) {
                board.gomokuBoard[row][col] = Board.BOMB;
                if (player.getPiece() == player1.getPiece()) {
                    bombNum1--;
                } else {
                    bombNum2--;
                }
                isValidBomb=true;
                round++;
                bombMode = false;
            }
        }
    }


    // 处理落子逻辑
    public void handleMove(Player player, int row, int col) {
        isValidMove = false;
        if ("gomoku".equals(mode)) {
            if(row >= 0 && row < gomokuBoardSize && col >= 0 && col < gomokuBoardSize){
                if (board.gomokuBoard[row][col] == Board.EMPTY) {
                    board.gomokuBoard[row][col] = player.getPiece();
                    isValidMove = true;
                    if (ifGomokuEnd(row, col, player)) {
                        isGomokuEnd = true;
                    }
                    else{
                        round++;
                    }
                }
            }
        } else if("reversi".equals(mode)){
            if(row >= 0 && row < boardSize && col >= 0 && col < boardSize){
                boolean isValid = false;
                for (int[] move : board.getValid(player)) {
                    if (move[0] == row && move[1] == col) {
                        isValid = true;//此处是合法落子位置
                        break;
                    }
                }
                if(isValid) {
                    board.board[row][col] = player.getPiece();
                    board.flipPiece(row,col,player);
                    isValidMove=true;
                }
            }
        } else{
            if(row >= 0 && row < boardSize && col >= 0 && col < boardSize){
                if (board.board[row][col] == Board.EMPTY) {
                    board.board[row][col] = player.getPiece();
                    //System.out.println("handlemove");
                    isValidMove=true;
                }
            }
        }
    }

    //读取文件命令
    public void handlePlaybackCommand(File file) {
        //System.out.println("handleplaybackcommand");
        // 检查文件是否存在
        if (!file.exists()) {
            System.out.println("错误: 文件不存在 - " + file.getAbsolutePath());
            return;
        }

        // 检查是否是文件（而非目录）
        if (!file.isFile()) {
            System.out.println("错误: 路径不是文件 - " + file.getAbsolutePath());
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                commandQueue.add(line);
            }
            //System.out.println("duquzhilingwancheng");
        } catch (IOException e) {
            System.out.println("读取文件失败: " + e.getMessage());
        }
    }

    //gomoku游戏结束判定
    public boolean ifGomokuEnd(int x, int y, Player currentPlayer) {
        for (int[] dir : DIRECTIONS) {
            int dx = dir[0];
            int dy = dir[1];
            int count = 1; // 初始计数为1（落子点本身）
            // 正向检查
            for (int i = 1; i < 5; i++) {
                int nx = x + dx * i;
                int ny = y + dy * i;
                if (nx >= 0 && nx < gomokuBoardSize && ny >= 0 && ny < gomokuBoardSize) {
                    if (board.gomokuBoard[nx][ny] == currentPlayer.getPiece()) {
                        count++;
                    } else {
                        break; // 如果不是己方棋子，直接跳出循环
                    }
                } else {
                    break; // 如果越界，直接跳出循环
                }
            }
            // 反向检查
            for (int i = 1; i < 5; i++) {
                int nx = x - dx * i;
                int ny = y - dy * i;
                if (nx >= 0 && nx < gomokuBoardSize && ny >= 0 && ny < gomokuBoardSize) {
                    if (board.gomokuBoard[nx][ny] == currentPlayer.getPiece()) {
                        count++;
                    } else {
                        break; // 如果不是己方棋子，直接跳出循环
                    }
                } else {
                    break; // 如果越界，直接跳出循环
                }
            }
            // 如果某个方向上连续的棋子数量达到5，返回true
            if (count >= 5) {
                isGomokuEnd = true;
                return true;
            }
        }
        return false;
    }

}