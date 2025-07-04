package com.example;
import java.util.ArrayList;
import java.util.List;

/* currentGameIndex:当前棋盘索引 是棋盘号码-1
 * newIndex：新选择的棋盘索引
 * index：棋盘所对应的索引（新增棋盘时用到 后面不变且不再会用到）
 * 游戏操作均为startgame 即换棋盘的关键是换currentGameIndex的值
 * 用户输入的值-1是newIndex*/

public class GameList {
    public List<Game>games;
    public int currentGameIndex = 0;
    Player player1;
    Player player2;
    int currentPlayer=0;

    public GameList(Player player1,Player player2) {
        this.games = new ArrayList<>();
        this.player1=player1;
        this.player2=player2;
        initializeGameList();
    }


    public void addNewGame(String mode){
        int index=games.size();
        if("peace".equals(mode)){
            games.add(new Game(player1,player2,"peace",index,this));
            games.get(index).board.initializeBoard();
        }
        else if("reversi".equals(mode)){
            games.add(new Game(player1,player2,"reversi",index,this));
            games.get(index).board.initializeBoard();
        }
        else if("gomoku".equals(mode)){
            games.add(new Game(player1,player2,"gomoku",index,this));
            games.get(index).board.emptyBoard();
        }
    }

    public int gameListSize(){
        return games.size();
    }

    public void setCurrentPlayer(int currentPlayer){
        this.currentPlayer=currentPlayer;
    }

    public int getCurrentPlayer(){
        return currentPlayer;
    }

    public void initializeGameList(){
        addNewGame("peace");
        addNewGame("reversi");
        addNewGame("gomoku");
    }

    public void switchGame(int newIndex){
        if(newIndex>=0&&newIndex<=games.size()){
            currentGameIndex=newIndex;
        }
    }

    public Game getCurrentGame(){
        return games.get(currentGameIndex);
    }

    public Game getGame(int index){
        return games.get(index);
    }

    public void startGame(){
        games.get(currentGameIndex).play();
    }


}
