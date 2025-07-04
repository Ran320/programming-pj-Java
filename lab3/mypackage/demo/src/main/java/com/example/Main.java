package com.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int currentPlayer=0;
        final int boardNumber=3;
        Scanner sc=new Scanner(System.in);
        System.out.print("请输入玩家1的名字：");
        String name1=sc.nextLine();
        System.out.print("请输入玩家2的名字：");
        String name2=sc.nextLine();
        //创建玩家
        Player player1=new Player(name1,Board.BLACK);
        Player player2=new Player(name2,Board.WHITE);

        Board boards[]=new Board[boardNumber];
        Game games[]=new Game[boardNumber];
        for(int i=0;i<boardNumber;i++){
            boards[i]=new Board(player1,player2,currentPlayer,i+1);
            boards[i].initializeBoard();
            games[i]=new Game(player1, player2, boards[i].board, i+1);
        }
        BoardGame boardGame=new BoardGame(games[0],currentPlayer);
        boolean res1=false,res2=false,res3=false;
        
        while(true){
            if(boardGame.getChoice()==1){
                res1=boardGame.BoardPlay(player1,player2,games[0],boards[0]);
            }else if(boardGame.getChoice()==2){
                res2=boardGame.BoardPlay(player1,player2,games[1],boards[1]);
            }else if(boardGame.getChoice()==3){
                res3=boardGame.BoardPlay(player1,player2,games[2],boards[2]);
            }
            if((res1||res2||res3)==true){ 
                break;
            }
        }
    }
}