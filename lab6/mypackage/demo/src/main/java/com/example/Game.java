package com.example;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import static com.example.Board.*;

//游戏运行

public class Game {
    Player player1;
    Player player2;
    Board board;
    String mode;
    GameList games;
    GamePrinter gamePrinter;
    private Queue<String> commandQueue = new LinkedList<>(); // 存储命令的队列
    int round=1;
    boolean isGomokuEnd = false;
    boolean isValidMove = false;//用户成功落子
    boolean isValidChange = false;
    boolean isValidBomb = false;
    boolean isValidPass = false;
    boolean isValidAdd = false;
    int bombNum1=2;
    int bombNum2=3;
    int index, newIndex;//分别表示当前棋盘索引，用户新选择的棋盘索引
    public Game(Player player1,Player player2,String mode,int index, GameList games){
        this.player1=player1;
        this.player2=player2;
        this.board=new Board(player1, player2, games.getCurrentPlayer(), index);
        this.mode=mode;
        this.index=index;
        this.games=games;
        gamePrinter=new GamePrinter(games);
    }

    public int getNewIndex() {
        return newIndex;
    }

    public String getMode(){
        return mode;
    }

    //获取炸弹数量
    public int getBomb(Player currentPlayer){
        if(currentPlayer==player1){
            return bombNum1;
        }
        else{
            return bombNum2;
        }
    }

    public Player getCurrentPlayer(){
        if(games.getCurrentPlayer()==1){
            return player1;
        }
        else{
            return player2;
        }
    }

    public int getCurrentRound(){
        if(round%2==0){
            return round/2;
        }
        else{
            return (round+1)/2;
        }
    }

    //获取输入/命令
    private String getNextCommand(){
        if (!commandQueue.isEmpty()) {
            // 如果队列中还有命令，则等待1秒
            try {
                Thread.sleep(1000); // 停顿1秒
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
                    handleBombCommand(player, input);
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
        int num1 = input.charAt(1) - '0'; // 个位
        int num2 = input.charAt(0) - '0'; // 十位

        // 当棋盘数到达两位数时换棋盘
        if (num1 >= 0 && num1 < 10 && num2 > 0) {
            int num = num2 * 10 + num1;
            if (num <= games.gameListSize()) {
                newIndex = num - 1;
                if(newIndex!=index){
                    isValidChange = true;
                }
                else{
                    System.out.println("如想更换棋盘请输入非本棋盘序号！");
                }
                return;
            }
        }
        //gomoku已经结束，不再实现落子操作
        if (isGomokuEnd) {
            System.out.println("游戏已结束，不可再落子，请输入其他操作！");
            return;
        }
        handleMove(player,input);
    }

    // 处理炸弹命令
    private void handleBombCommand(Player player, String input) {
        if (getBomb(player) > 0) {
            int row = Character.digit(input.charAt(1), 16) - 1;
            int col = Character.toUpperCase(input.charAt(2)) - 'A';
            Player otherPlayer = getCurrentPlayer() == player1 ? player2 : player1;

            if (row < 0 || row > gomokuBoardSize - 1 || col < 0 || col > gomokuBoardSize - 1) {
                System.out.println("炸弹放置位置有误，请重新输入");
            } else if (board.gomokuBoard[row][col] == otherPlayer.getPiece()) {
                board.gomokuBoard[row][col] = Board.BOMB;
                if (player == player1) {
                    bombNum1--;
                } else {
                    bombNum2--;
                }
                isValidBomb=true;
            } else {
                System.out.println("此处不可放置炸弹！请重新输入");
            }
        } else {
            System.out.println("你已无可用炸弹！请输入其他操作");
        }
    }

    // 处理落子逻辑
    private void handleMove(Player player, String input) {
        int row = Character.digit(input.charAt(0), 16) - 1;
        int col = Character.toUpperCase(input.charAt(1)) - 'A';
        // 检查落子位置是否有效
        boolean isValidPosition = false;
        if ("gomoku".equals(mode)) {
            if (row >= 0 && row < gomokuBoardSize && col >= 0 && col < gomokuBoardSize) {
                if(isGomokuEnd){
                    System.out.println("不可再落子，请重新输入");
                }
                else isValidPosition = true;
            } else {
                System.out.println("落子位置有误，请重新输入");
            }
        } else {
            if (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
                isValidPosition = true;
            } else {
                System.out.println("落子位置有误，请重新输入");
            }
        }

        // 如果位置有效，继续处理落子逻辑
        if (isValidPosition) {
            if ("gomoku".equals(mode)) {
                if (board.gomokuBoard[row][col] == Board.EMPTY) {
                    board.gomokuBoard[row][col] = player.getPiece();
                    isValidMove = true;
                    if (ifGomokuEnd(row, col, player)) {
                        isGomokuEnd = true;
                    }
                    else{
                        round++;
                    }
                } else if (board.gomokuBoard[row][col] == Board.BARRIER) {
                    System.out.println("[" + input + "]是障碍！无法落子！");
                } else if (board.gomokuBoard[row][col] == Board.BOMB) {
                    System.out.println("[" + input + "]是弹坑！无法落子！");
                } else {
                    System.out.println("[" + input + "]已经有棋子了！");
                }
            } else if("reversi".equals(mode)){
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
                    else if(board.board[row][col] == Board.EMPTY){
                        System.out.println("[" + input + "]不是合法落子位置！");
                    }
                    else{
                        System.out.println("[" + input + "]已经有棋子了！");
                    }
            } else{
                    if (board.board[row][col] == Board.EMPTY) {
                        board.board[row][col] = player.getPiece();
                        isValidMove=true;
                    } else {
                        System.out.println("[" + input + "]已经有棋子了！");
                    }
                }
        }
    }

    //读取文件命令
    private void handlePlaybackCommand(String input) {
        String filename = input.substring(9).trim();
        File file = new File(filename);

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
        } catch (IOException e) {
            System.out.println("读取文件失败: " + e.getMessage());
        }
    }

    // peace模式
    public void peaceMove(Player player) {
        while (true) {
            System.out.println("请玩家[" + player.getName() + "]输入操作：");
            String input = getNextCommand();
            if (input.startsWith("playback ")) {
                handlePlaybackCommand(input);
                continue; // 继续处理队列中的下一条命令
            }
            handleInput(input,player);
            if (isValidMove||isValidChange||isValidBomb||isValidAdd) {
                break;
            }
        }


    }

    // reversi模式
    public void reversiMove(Player player) {
        while (true) {
            if (board.getValid(player).isEmpty()) {
                System.out.println("玩家[" + player.getName() + "]已无合法落子位置！若想继续本局游戏，请输入pass跳过");
            }
            System.out.println("请玩家[" + player.getName() + "]输入操作：");
            String input = getNextCommand();
            if (input.startsWith("playback ")) {
                handlePlaybackCommand(input);
                continue; // 继续处理队列中的下一条命令
            }
            handleInput(input,player);
            if (isValidMove||isValidChange||isValidBomb||isValidPass||isValidAdd) {
                break;
            }
        }
    }

    // gomoku模式
    public void gomokuMove(Player player) {
        while (true) {
        System.out.println("请玩家[" + player.getName() + "]输入操作：");
            String input = getNextCommand();
            if (input.startsWith("playback ")) {
                handlePlaybackCommand(input);
                continue; // 继续处理队列中的下一条命令
            }
        handleInput(input,player);
            if (isValidMove||isValidChange||isValidBomb||isValidAdd) {
                break;
            }
        }
    }

    //peace游戏实现
    public void peacePlay(Player player1, Player player2,Board board){
        while(!board.isBoardFull()){
            if(games.getCurrentPlayer()!=1){
                games.setCurrentPlayer(1);
                board.resetCurrentPlayer(1);
                GamePrinter.printGame();
                peaceMove(player1);
                if(getNewIndex()!= games.currentGameIndex){//用户选择换棋盘操作
                    games.switchGame(getNewIndex());
                    games.setCurrentPlayer(2);
                    break;
                }
                if(isValidAdd){//增加棋盘操作
                    games.setCurrentPlayer(2);
                    board.resetCurrentPlayer(2);
                }
            }
            else{
                games.setCurrentPlayer(2);
                board.resetCurrentPlayer(2);
                GamePrinter.printGame();
                peaceMove(player2);
                if(getNewIndex()!=games.currentGameIndex){//用户选择换棋盘操作
                    games.switchGame(getNewIndex());
                    games.setCurrentPlayer(1);
                    break;
                }
                if(isValidAdd){//增加棋盘操作
                    games.setCurrentPlayer(1);
                    board.resetCurrentPlayer(1);
                }
            }
        }
        if(board.isBoardFull()){
            Player otherPlayer=games.getCurrentPlayer()==1?player1:player2;
            if(!isValidAdd){
                otherPlayer=games.getCurrentPlayer()==1?player2:player1;
                int otherPlayerIndex=games.getCurrentPlayer()==1?2:1;
                games.setCurrentPlayer(otherPlayerIndex);
                board.resetCurrentPlayer(otherPlayerIndex);
            }
            GamePrinter.printGame();
            System.out.println("此棋盘已满！");
            System.out.println("此盘游戏结束！可输入数字切换棋盘或添加游戏棋盘");
            peaceMove(otherPlayer);
            if(getNewIndex()!=games.currentGameIndex){//用户选择换棋盘操作
                games.switchGame(getNewIndex());
                games.setCurrentPlayer(games.getCurrentPlayer()==1?2:1);

            }
        }
    }

    //reversi游戏实现
    public void reversiPlay(Player player1,Player player2,Board board){
        while(true){
            if(board.isBoardFull()||(board.getValid(player1).isEmpty()&&board.getValid(player2).isEmpty())){
                Player otherPlayer=games.getCurrentPlayer()==1?player1:player2;
                if(!isValidAdd){
                    otherPlayer=games.getCurrentPlayer()==1?player2:player1;
                    int otherPlayerIndex=games.getCurrentPlayer()==1?2:1;
                    games.setCurrentPlayer(otherPlayerIndex);
                    board.resetCurrentPlayer(otherPlayerIndex);
                }
                GamePrinter.printGame();
                System.out.println("本局游戏结束！");
                System.out.println("可输入数字切换棋盘或添加游戏棋盘");
                System.out.println("游戏结果： "+"玩家["+player1.getName()+"]"+player1.getPiece()+"得分："+board.calculateScore(player1.getPiece()));
                System.out.println("         "+"玩家["+player2.getName()+"]"+player2.getPiece()+"得分："+board.calculateScore(player2.getPiece()));
                if(board.calculateScore(BLACK)> board.calculateScore(WHITE)){
                    System.out.println("黑方:玩家["+player1.getName()+"]获胜！");
                }
                else if(board.calculateScore(BLACK)< board.calculateScore(WHITE)){
                    System.out.println("白方:玩家["+player2.getName()+"]获胜！");
                }
                else System.out.println("双方平局！");
                peaceMove(otherPlayer);
                if(getNewIndex()!=games.currentGameIndex){//用户选择换棋盘操作
                    games.switchGame(getNewIndex());
                    games.setCurrentPlayer(games.getCurrentPlayer()==1?2:1);
                    break;
                }
            }
            else{
                if(games.getCurrentPlayer()!=1){
                    games.setCurrentPlayer(1);
                    board.resetCurrentPlayer(1);
                    GamePrinter.printGame();
                    reversiMove(player1);
                    if(getNewIndex()!=games.currentGameIndex){//用户选择换棋盘操作
                        games.switchGame(getNewIndex());
                        games.setCurrentPlayer(2);
                        break;
                    }
                    if(isValidAdd){//增加棋盘操作
                        games.setCurrentPlayer(2);
                        board.resetCurrentPlayer(2);
                    }
                }
                else{
                    games.setCurrentPlayer(2);
                    board.resetCurrentPlayer(2);
                    GamePrinter.printGame();
                    reversiMove(player2);
                    if(getNewIndex()!=games.currentGameIndex){//用户选择换棋盘操作
                        games.switchGame(getNewIndex());
                        games.setCurrentPlayer(1);
                        break;
                    }
                    if(isValidAdd){//增加棋盘操作
                        games.setCurrentPlayer(1);
                        board.resetCurrentPlayer(1);
                    }
                }
            }
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

    //Gomoku游戏实现
    public void gomokuPlay(Player player1,Player player2,Board board){
        while(true){
            if(board.isGomokuBoardFull()||isGomokuEnd){
                Player currentPlayer=games.getCurrentPlayer()==1?player2:player1;
                Player otherPlayer=games.getCurrentPlayer()==1?player1:player2;
                if(!isValidAdd){
                    currentPlayer=games.getCurrentPlayer()==1?player1:player2;
                    otherPlayer=games.getCurrentPlayer()==1?player2:player1;
                    int otherPlayerIndex=games.getCurrentPlayer()==1?2:1;
                    games.setCurrentPlayer(otherPlayerIndex);
                    board.resetCurrentPlayer(otherPlayerIndex);
                }
                GamePrinter.printGame();
                if(board.isGomokuBoardFull()){
                    System.out.println("棋盘已满！");
                }
                else{
                    System.out.println("玩家["+currentPlayer.getName()+"]获胜！");
                }
                System.out.println("游戏结束！此时不可再落子，可输入数字切换棋盘或添加游戏棋盘");
                gomokuMove(otherPlayer);
                if(getNewIndex()!=games.currentGameIndex){//用户选择换棋盘操作
                    games.switchGame(getNewIndex());
                    games.setCurrentPlayer(games.getCurrentPlayer()==1?2:1);
                    break;
                }
            }
            else{
                if(games.getCurrentPlayer()!=1){
                    games.setCurrentPlayer(1);
                    board.resetCurrentPlayer(1);
                    GamePrinter.printGame();
                    gomokuMove(player1);
                    if(getNewIndex()!= games.currentGameIndex){//用户选择换棋盘操作
                        games.switchGame(getNewIndex());
                        games.setCurrentPlayer(2);
                        break;
                    }
                    if(isValidAdd){//增加棋盘操作
                        games.setCurrentPlayer(2);
                        board.resetCurrentPlayer(2);
                    }
                }
                else{
                    games.setCurrentPlayer(2);
                    board.resetCurrentPlayer(2);
                    GamePrinter.printGame();
                    gomokuMove(player2);
                    if(getNewIndex()!=games.currentGameIndex){//用户选择换棋盘操作
                        games.switchGame(getNewIndex());
                        games.setCurrentPlayer(1);
                        break;
                    }
                    if(isValidAdd){//增加棋盘操作
                        games.setCurrentPlayer(1);
                        board.resetCurrentPlayer(1);
                    }
                }
            }
        }
    }

    //游戏实现
    public void play(){
        if ("peace".equals(mode)) {
            peacePlay(player1,player2,board);
        } else if ("reversi".equals(mode)) {
            reversiPlay(player1,player2,board);
        }
        else if("gomoku".equals(mode)){
            gomokuPlay(player1,player2,board);
        }
    }
}