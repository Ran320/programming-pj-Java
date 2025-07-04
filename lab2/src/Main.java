import java.io.IOException;
import java.lang.ProcessBuilder;
import java.util.Scanner;

//打印棋盘
class Board {
    char [][] board=new char[8][8];
    Player player1;
    Player player2;
    int currentPlayer=0;
    public Board(Player player1,Player player2,int currentPlayer){
        this.player1=player1;
        this.player2=player2;
        this.currentPlayer=currentPlayer;
    }
    public void resetCurrentPlayer(int currentPlayer){
        this.currentPlayer=currentPlayer;
    }
    public static final char EMPTY='·';
    public static final char BLACK='●';
    public static final char WHITE='○';
    //清屏
    public void clearScreen(){
        try {
            new ProcessBuilder("cmd", "/c", "cls").start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
    //初始化棋盘
    public void initializeBoard(){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++) {
                board[i][j] =EMPTY;
            }
        }
    }
    //打印棋盘
    public void printBoard(){
        clearScreen();
        System.out.println("  A B C D E F G H");
        for(int i=0;i<8;i++){
            System.out.print(i+1+" ");
            for(int j=0;j<8;j++){
                System.out.print(board[i][j]+" ");
            }
            if(i==3){
                System.out.print("   玩家["+player1.getName()+"]:"+(currentPlayer==1?player1.getPiece():" "));
            }
            if(i==4){
                System.out.print("   玩家["+player2.getName()+"]:"+(currentPlayer==2?player2.getPiece():" "));
            }
            System.out.println();
        }
    }
    //检查棋盘是否已满
    public boolean isBoardFull(){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                if(board[i][j]==EMPTY){
                    return false;
                }
            }
        }
        return true;
    }
}

//创建玩家
class Player{
    private String name;
    private char piece;
    public Player(String name,char piece){
        this.name=name;
        this.piece=piece;
    }
    public String getName(){
        return name;
    }
    public char getPiece(){
        return piece;
    }
}

//游戏运行
class Game{
    Player player1;
    Player player2;
    char [][] board=new char[8][8];
    public Game(Player player1,Player player2,char[][] board){
        this.player1=player1;
        this.player2=player2;
        this.board=board;
    }
    public void Move(Player player){
        while(true){
            System.out.print("请玩家["+player.getName()+"]输入落子位置：");
            Scanner sc=new Scanner(System.in);
            String input=sc.nextLine();
            int row=input.charAt(0)-'1';
            int col=input.charAt(1)-'a';
            if(row<0||row>7||col<0||col>7){
                System.out.println("落子位置有误，请重新输入");
            }
            else if(board[row][col]==Board.EMPTY) {
                board[row][col] = player.getPiece();
                break;
            }
            else{
                System.out.println("["+input+"]已经有棋子了！");
            }
        }
    }
}

//Main方法
public class Main {
    public static void main(String[] args) {
        int currentPlayer=0;
        Scanner sc=new Scanner(System.in);
        System.out.print("请输入玩家1的名字：");
        String name1=sc.nextLine();
        System.out.print("请输入玩家2的名字：");
        String name2=sc.nextLine();
        Player player1=new Player(name1,Board.BLACK);
        Player player2=new Player(name2,Board.WHITE);
        Board board=new Board(player1,player2,currentPlayer);
        Game game=new Game(player1,player2,board.board);
        board.initializeBoard();
        while(!board.isBoardFull()){
            currentPlayer=1;
            board.resetCurrentPlayer(currentPlayer);
            board.printBoard();
            game.Move(player1);
            currentPlayer=2;
            board.resetCurrentPlayer(currentPlayer);
            board.printBoard();
            game.Move(player2);
        }
        board.printBoard();
        System.out.println("棋盘已满！");
        System.out.println("游戏结束！");
    }
}