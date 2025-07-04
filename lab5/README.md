# 源代码文件名称及其主要功能

路径：mypackage\demo\src\main\java\com\example

## 各文件主要功能介绍

```bash

 example
   |- Board.java    //棋盘打印与初始化、判断落子合法与实现棋子翻转
   |- Game.java     //所有游戏运行相关（三种模式）
   |- GameList.java //游戏列表相关
   |- GamePrinter.java  //布局打印相关
   |- Player.java  //记录玩家相关
   |- Main.java     //main方法
```

## 文件内各主要方法介绍

### Board类（新增gomoku初始化）

```java
//初始化棋盘
    public void initializeBoard() 
//五子棋模式初始化棋盘
    public void emptyBoard()
//peace模式打印内容
    public List<String> generatePeaceBoardContent() 
//reversi模式打印内容
    public List<String> generateReversiBoardContent(Player currentPlayer) 
//reversi模式分数计算
    public int calculateScore(char piece) 
//检查棋盘是否已满
    public boolean isBoardFull() 
//reversi显示"+"的实现：
// getFlip：得到下在某一点处所有可翻转棋子的坐标
    public List<int[]> getFlip(int x, int y, Player currentPlayer) 
// getValid：得到所有可落子位置坐标
    public List<int[]> getValid(Player currentPlayer)
//翻转棋子
    public void flipPiece(int x, int y,Player currentPlayer)
```

### Game类（新增gomoku模式，更改输入判断）

```java
//peace/Gomoku读取用户输入、判断用户输入是否合法（更改部分）
    public int regularMove(Player player,int res)
//peace游戏实现
    public void peacePlay(Player player1, Player player2,Board board)
//reversi读取用户输入、判断是否合法
    public void reversiMove(Player player)
//reversi游戏实现
    public void reversiPlay(Player player1,Player player2,Board board)
//Gomoku游戏结束判断（新增）
    public boolean ifGomokuEnd(int x, int y, Player currentPlayer)
//Gomoku游戏实现（新增）
		public void gomokuPlay(Player player1,Player player2,Board board)
//游戏实现（新增gomoku模式）
    public void play()
```

### GameList类

```java
//增加新游戏（略有改动）
    public void addNewGame(String mode)
//初始化
    public void initializeGameList()
//换游戏
    public void switchGame(int newIndex)
//游戏开始
    public void startGame()
```

### GamePrinter类

```java
//清屏
    public static void clearScreen() 
//棋盘部分打印
    private static List<String> getBoardLines(Game currentGame) 
//玩家信息部分打印（新增gomoku模式下打印round）
    private static List<String> getPlayerLines(Game currentGame) 
//游戏部分打印
    private static List<String> getGameListLines () 
//最终打印
    static void printGame() 
```

### Player类（与lab4相同）

```java
//Player类包含玩家的名字与对应棋子
public class Player {
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
```

### Main（与lab4相同）

```java
//创建玩家
        Player player1 = new Player(name1, Board.BLACK);
        Player player2 = new Player(name2, Board.WHITE);
//创建游戏
        GameList gameList = new GameList(player1, player2);
//开始游戏
        while(true) {
            gameList.startGame();
        }
```

# 关键代码及其设计思路

## gomoku初始化

- 将所有位置初始化为 `EMPTY`
    
    ```java
    public void emptyBoard(){
            for(int i=0;i<boardSize;i++){
                for(int j=0;j<boardSize;j++){
                    board[i][j]=EMPTY;
                }
            }
        }
    ```
    

## gomoku计算与输出轮数

- 利用 `round` 进行计算，每当**有玩家落子**时， `round++` ;
    - 判断玩家选择的操作是否为落子：
        - `regularMove()` 设置返回值为 `int` 类型，若用户**可正常落子且选择了落子操作**，则返回4；
        - 相关代码：
        
        ```java
        else if(length==2){
                        ...
                            else if(board.board[row][col]==Board.EMPTY) {
                                board.board[row][col] = player.getPiece();//用户正常落子
                                ...
                                }
                                return 4;
                            }
        ```
        
- 新增 `setNewRound()` , `getCurrentRound()` ，以改变 `round` 、获取当前轮数
    - `getCurrentRound()` ：若 `round` 为奇数，则输出 `(round+1)/2` ;否则输出 `round/2` ；
    
    ```java
    public int getCurrentRound(){
            if(round%2==0){
                return round/2;
            }
            else{
                return (round+1)/2;
            }
        }
    ```
    
- `gomoku` 实际运行时代码：

```java
int res=0;//保存返回值
while(true){
						...
						res = regularMove(player1,0);
            ...//相关实现代码
            res = regularMove(player2,0);
            ...
            if(res==4){
                round++;
                setNewRound(round);
            }
        }
```

- 轮数输出：在 `GamePrinter` 中 `getPlayerLines` 添加：

```java
if(mode.equals("gomoku")){
            lines.add("Current Round:"+gameList.getCurrentGame().getCurrentRound());
        }
```

## 五子连线判断逻辑

- 大循环：八个方向遍历
- 小循环：在一个方向上，数落子位置开始己方棋子的个数 `count`
    
    相关代码：
    

```java
//Gomoku游戏结束判断
    public boolean ifGomokuEnd(int x, int y, Player currentPlayer){
        for (int[] dir : DIRECTIONS) {
            int dx = dir[0];
            int dy = dir[1];
            int count = 1; // 初始计数为1（落子点本身）
            for (int i = 1; i < 5; i++) {
                int nx = x + dx * i;
                int ny = y + dy * i;
                if (nx >= 0 && nx < boardSize && ny >= 0 && ny < boardSize) {
                    if (board.board[nx][ny] == currentPlayer.getPiece()) {
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
                return true;
            }
        }
        return false;
    }
```

## gomoku结束逻辑

- 利用 `res` 作为标志， `res==1` 时代表gomoku游戏结束
    - 在 `regularMove()`中利用 `ifGomokuEnd()` 来判断是否游戏应该结束
        - 相关代码：
    
    ```java
    ...
          else if(board.board[row][col]==Board.EMPTY) {
              board.board[row][col] = player.getPiece();
              if(games.getCurrentGame().getMode().equals("Gomoku")){
                  if(games.getCurrentGame().ifGomokuEnd(row,col,player)){
                      return 1;//返回1，代表已有五子连线
                  }
              }
              return 4;//选择落子操作（计算轮数时用到）
          }
    ...
    ```
    
- 在 `gomokuPlay()` 中， 游戏结束的条件是`board.isBoardFull()||res==1`
- 游戏结束时相关代码：
    - 向 `regularMove()`中传入 `res` 的值以控制落子操作的执行
    - 相关代码：
    
    ```java
    else if(length==2){
    							...//转换为两位数的棋盘
                    if(res==1){
                        return 3;//gomoku游戏已经截止，不执行落子操作
                    }
                    ...//落子操作的执行
                    }
    ```
    
    ```java
     //gomokuPlay()中游戏结束部分：
     if(board.isBoardFull()||res==1){
              Player currentPlayer=games.getCurrentPlayer()==1?player1:player2;
              Player otherPlayer=games.getCurrentPlayer()==1?player2:player1;
              int otherPlayerIndex=games.getCurrentPlayer()==1?2:1;
              games.setCurrentPlayer(otherPlayerIndex);
              board.resetCurrentPlayer(otherPlayerIndex);
              GamePrinter.printGame();
              if(board.isBoardFull()){//无玩家获胜但棋盘已满
                  System.out.println("棋盘已满！");
                  res=1;
              }
              if(res==1&&!board.isBoardFull()){//游戏结束且棋盘未满->有玩家获胜
                  System.out.println("玩家["+currentPlayer.getName()+"]获胜！");
              }
              System.out.println("游戏结束！此时不可再落子，可输入数字切换棋盘或添加游戏棋盘");
              int flag=regularMove(otherPlayer,1);
              if(flag==3){
                  System.out.println("不可再落子，请重新输入");
                  games.setCurrentPlayer(games.getCurrentPlayer()==1?2:1);
              }
              if(getNewIndex()!=games.currentGameIndex){//用户选择换棋盘操作
                  games.switchGame(getNewIndex());
                  games.setCurrentPlayer(games.getCurrentPlayer()==1?2:1);
                  break;
              }
       }
    ```
    

# 运行截图

## gomoku模式：

![image.png](attachment:b51edb27-7800-4f39-8c0a-9fa715e92b17:image.png)

![image.png](attachment:69b2a60f-ac99-4e8b-9c16-11ede9b76498:image.png)

判定非法输入：

![image.png](attachment:635229f0-951b-4b87-a45e-0e3d65780bdd:image.png)

游戏结束：

注：由于输入提示选择全部放在右方，左侧只有“请玩家输入操作”一种输入提示，因此Gomoku在结束时**再次落子会单独提示**”不可再落子，请重新输入“

![image.png](attachment:5e9633f4-447a-486b-8abb-3ae851a9ea8d:image.png)

![image.png](attachment:e72f8bca-0720-489d-8ad5-e0f1dfc9c68c:image.png)

![image.png](attachment:4b4db5d9-838e-4c44-acbf-afd6a654353c:image.png)

棋盘满：

![image.png](attachment:91c0adf4-84cf-4228-b404-4d63b0c93ce7:image.png)

![image.png](attachment:7a0183e3-f782-4611-9118-c90dc85c8451:image.png)

![image.png](attachment:3b26d6ee-6ead-4650-b1c9-057c4922bbb2:image.png)

![image.png](attachment:dbfe85a2-1e3a-467a-85be-3b790ad8255f:image.png)

切换棋盘查看结果：

![image.png](attachment:e64f860a-d081-469b-bfa6-9dd736292f72:image.png)

![image.png](attachment:168d59d6-e148-4d91-b9a9-fe79964dc26b:image.png)

![image.png](attachment:600a268e-949a-496f-a2bb-2d028a71609a:image.png)
