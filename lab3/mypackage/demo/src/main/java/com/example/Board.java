package com.example;

import java.io.IOException;
import java.lang.ProcessBuilder;

//打印棋盘
public class Board {
    char [][] board=new char[boardSize][boardSize];
    Player player1;
    Player player2;
    int currentPlayer=0;
    int choice=0;
    public Board(Player player1,Player player2,int currentPlayer,int choice){
        this.player1=player1;
        this.player2=player2;
        this.currentPlayer=currentPlayer;
        this.choice=choice;
    }
    public void resetCurrentPlayer(int currentPlayer){
        this.currentPlayer=currentPlayer;
    }
    public static final char EMPTY='·';
    public static final char BLACK='●';
    public static final char WHITE='○';
    public static final int boardSize=8;
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
        for(int i=0;i<boardSize;i++){
            for(int j=0;j<boardSize;j++) {
                board[i][j] =EMPTY;
            }
        }
    }
    //打印棋盘
    public void printBoard(){
        clearScreen();
        System.out.println("  A B C D E F G H");
        for(int i=0;i<boardSize;i++){
            System.out.print(i+1+" ");
            for(int j=0;j<boardSize;j++){
                System.out.print(board[i][j]+" ");
            }
            if(i==2){
                System.out.print("   棋盘"+choice);
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
        for(int i=0;i<boardSize;i++){
            for(int j=0;j<boardSize;j++){
                if(board[i][j]==EMPTY){
                    return false;
                }
            }
        }
        return true;
    }
}