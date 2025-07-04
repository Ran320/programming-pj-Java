package com.example;

import java.util.Scanner;

import static com.example.Board.BLACK;
import static com.example.Board.WHITE;

//游戏运行

public class Game {
    Player player1;
    Player player2;
    Board board;
    String mode;
    GameList games;
    GamePrinter gamePrinter;
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

    public Player getCurrentPlayer(){
        if(games.getCurrentPlayer()==1){
            return player1;
        }
        else{
            return player2;
        }
    }

    //peace读取用户输入、判断用户输入是否合法
    public void peaceMove(Player player){
        while(true){
            newIndex=games.currentGameIndex;
            System.out.print("请玩家["+player.getName()+"]输入操作：");
            Scanner sc=new Scanner(System.in);
            String input=sc.nextLine();
            int length = input.length();
            if(input.equals("quit")){
                System.exit(0);
            }
            else if(input.equals("peace")){
                games.addNewGame("peace");
                GamePrinter.printGame();
            }
            else if(input.equals("reversi")){
                games.addNewGame("reversi");
                GamePrinter.printGame();
            }
            else if(length>2){
                System.out.println("输入格式有误，请重新输入");
            }
            else if(length==1){
                int ch = input.charAt(0) - '0';
                if(ch>0&&ch<=games.gameListSize()){
                    newIndex = ch-1;
                    break;
                }
                else {
                    System.out.println("输入格式有误，请重新输入");
                }
            }
            else if(length==2){
                int row=input.charAt(0)-'1';
                int col=input.charAt(1)-'a';
                int num1=input.charAt(1)-'0';//个位
                int num2=input.charAt(0)-'0';//十位
                //当棋盘数到达两位数时换棋盘
                if(num1>=0&&num1<10) {
                    int num = num2 * 10 + num1;
                    if (num <= games.gameListSize()) {
                        newIndex = num - 1;
                        break;
                    }
                }
                if(row<0||row>7||col<0||col>7){
                    System.out.println("落子位置有误，请重新输入");
                }
                else if(board.board[row][col]==Board.EMPTY) {
                    board.board[row][col] = player.getPiece();
                    break;
                }
                else{
                    System.out.println("["+input+"]已经有棋子了！");
                }
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
            }
        }
        if(board.isBoardFull()){
            Player otherPlayer=games.getCurrentPlayer()==1?player2:player1;
            int otherPlayerIndex=games.getCurrentPlayer()==1?2:1;
            games.setCurrentPlayer(otherPlayerIndex);
            board.resetCurrentPlayer(otherPlayerIndex);
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

    //reversi读取用户输入、判断是否合法
    public void reversiMove(Player player){
        newIndex=games.currentGameIndex;
        while(true){
            if(board.getValid(player).isEmpty()){
                System.out.println("玩家["+player.getName()+"]已无合法落子位置！若想继续本局游戏，请输入pass跳过");
            }
            System.out.print("请玩家["+player.getName()+"]输入操作：");
            Scanner sc=new Scanner(System.in);
            String input=sc.nextLine();
            int length = input.length();
            if(input.equals("pass")){
                if(board.getValid(player).isEmpty()){
                    break;
                }
                else{
                    System.out.println("不允许放弃本轮行棋！");
                }
            }
            else if(input.equals("peace")){
                games.addNewGame("peace");
                GamePrinter.printGame();
            }
            else if(input.equals("reversi")){
                games.addNewGame("reversi");
                GamePrinter.printGame();
            }
            else if(input.equals("quit")){
                System.exit(0);
            }
            else if(length>2){
                System.out.println("输入格式有误，请重新输入");
            }
            else if(length==1){
                int ch = input.charAt(0)- '0';
                if(ch>0&&ch<=games.gameListSize()){
                    newIndex = ch-1;
                    break;
                }
                else {
                    System.out.println("输入格式有误，请重新输入");
                }
            }
            else if(length==2){
                int row=input.charAt(0)-'1';
                int col=input.charAt(1)-'a';
                int num1=input.charAt(1)-'0';//个位
                int num2=input.charAt(0)-'0';//十位
                //当棋盘数到达两位数时换棋盘
                if(num1>=0&&num1<10){
                    int num=num2*10+num1;
                    if(num<= games.gameListSize()){
                        newIndex=num-1;
                        break;
                    }
                }
                boolean isValidMove = false;
                for (int[] move : board.getValid(player)) {
                    if (move[0] == row && move[1] == col) {
                        isValidMove = true;
                        break;
                    }
                }
                if(isValidMove) {
                    board.board[row][col] = player.getPiece();
                    board.flipPiece(row,col,player);
                    break;
                }
                else if(row<0||row>7||col<0||col>7){
                    System.out.println("落子位置有误，请重新输入");
                }
                else if(board.board[row][col]==Board.EMPTY){
                    System.out.println("["+input+"]不是合法落子位置！");
                }
                else{
                    System.out.println("["+input+"]已经有棋子了！");
                }
            }
        }
    }

    //reversi游戏实现
    public void reversiPlay(Player player1,Player player2,Board board){
        while(true){
            if(board.isBoardFull()||(board.getValid(player1).isEmpty()&&board.getValid(player2).isEmpty())){
                Player otherPlayer=games.getCurrentPlayer()==1?player2:player1;
                int otherPlayerIndex=games.getCurrentPlayer()==1?2:1;
                games.setCurrentPlayer(otherPlayerIndex);
                board.resetCurrentPlayer(otherPlayerIndex);
                GamePrinter.printGame();
                System.out.println("本局游戏结束！");
                System.out.println("可输入数字切换棋盘或添加游戏棋盘");
                System.out.println("游戏结果： "+"玩家["+player1.getName()+"]"+player1.getPiece()+"得分："+board.calculateScore(player1.getPiece()));
                System.out.println("         "+"玩家["+player2.getName()+"]"+player2.getPiece()+"得分："+board.calculateScore(player2.getPiece()));
                if(board.calculateScore(BLACK)> board.calculateScore(WHITE)){
                    System.out.println("黑方获胜！");
                }
                else if(board.calculateScore(BLACK)< board.calculateScore(WHITE)){
                    System.out.println("白方获胜！");
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
    }
}
