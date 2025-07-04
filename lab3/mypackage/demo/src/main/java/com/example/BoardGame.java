package com.example;

//游戏运行
public class BoardGame {
    Game game;
    int choice;
    int currentPlayer;
    public BoardGame(Game game,int currentPlayer){
        this.game=game;
        choice=game.getChoice();
        this.currentPlayer=currentPlayer;
    }
    int getChoice(){
        return choice;
    }
    
    //实现落子、换棋盘功能
    public boolean BoardPlay(Player player1,Player player2,Game game,Board board){
        while(!board.isBoardFull()){
            if(currentPlayer!=1){
                currentPlayer=1;
                board.resetCurrentPlayer(currentPlayer);
                board.printBoard();
                game.Move(player1);
                if(game.getCurrentChoice()!=choice){//用户选择换棋盘操作
                    choice=game.getCurrentChoice();
                    break;
                }
            }
            else{
                currentPlayer=2;
                board.resetCurrentPlayer(currentPlayer);
                board.printBoard();
                game.Move(player2);
                if(game.getCurrentChoice()!=choice){
                    choice=game.getCurrentChoice();
                    break;
                }
            }
        }if(board.isBoardFull()){
            board.printBoard();
            System.out.println("有棋盘已满！");
            System.out.println("游戏结束！");
            return true;
        }
        else{
            return false;
        }
    }
}
