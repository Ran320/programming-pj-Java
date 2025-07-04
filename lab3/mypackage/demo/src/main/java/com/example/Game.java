package com.example;

import java.util.Scanner;

//游戏实现
public class Game {
    public static final int boardSize=8;
    Player player1;
    Player player2;
    char [][] board=new char[boardSize][boardSize];
    int choice,currentChoice;//分别表示当前棋盘与用户新选择的棋盘
    public Game(Player player1,Player player2,char[][] board,int choice){
        this.player1=player1;
        this.player2=player2;
        this.board=board;
        this.choice=choice;
    }
    public int getChoice() {
        return this.choice;
    }
    public int getCurrentChoice() {
        return currentChoice;
    }
    //读取用户输入、判断用户输入
    public void Move(Player player){
        currentChoice=choice;
        while(true){
            System.out.print("请玩家["+player.getName()+"]输入落子位置：");
            Scanner sc=new Scanner(System.in);
            String input=sc.nextLine();
            int length = input.length();
            if(length==1){
                char ch = input.charAt(0); 
                currentChoice = Character.getNumericValue(ch);
                break;
            }
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
