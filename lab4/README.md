# 源代码文件名称及其主要功能

路径：mypackage\demo\src\main\java\com\example

## 各文件主要功能介绍

```bash

 example
   |- Board.java    //棋盘打印与初始化、判断落子合法与实现棋子翻转
   |- Game.java     //所有游戏运行相关
   |- GameList.java //游戏列表相关
   |- GamePrinter.java  //布局打印相关
   |- Player.java  //记录玩家相关
   |- Main.java     //main方法
```

## 文件内各主要方法介绍

### Board类

```java
//初始化棋盘
    public void initializeBoard() 
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

### Game类

```java
//peace读取用户输入、判断用户输入是否合法
    public void peaceMove(Player player)
//peace游戏实现
    public void peacePlay(Player player1, Player player2,Board board)
//reversi读取用户输入、判断是否合法
    public void reversiMove(Player player)
//reversi游戏实现
    public void reversiPlay(Player player1,Player player2,Board board)
//游戏实现
    public void play()
```

### GameList类

```java
//增加新游戏
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
//玩家信息部分打印
    private static List<String> getPlayerLines(Game currentGame) 
//游戏部分打印
    private static List<String> getGameListLines () 
//最终打印
    static void printGame() 
```

### Player类

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

### Main

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

---

# 关键代码及其设计思路

## 1、左中右布局打印的实现

- 实现结果
    - 左栏：棋盘与接收输入部分
    - 中栏：游戏玩家
    - 右栏：游戏列表
- 实现思路：
    - 分别将左、中、右栏的部分按行存储进`List<String> lines`
    - 为保证最终打印保持布局，计算最大行数
    - 将每行的三栏内容拼起并打印
- 相关代码

```java
static void printGame() {
            // 获取三栏内容
            List<String> leftLines = getBoardLines(gameList.getCurrentGame());
            List<String> middleLines = getPlayerLines(gameList.getCurrentGame());
            List<String> rightLines = getGameListLines();

            // 计算最大行数
            int maxLines = Math.max(leftLines.size(),
                    Math.max(middleLines.size(), rightLines.size()));

            // 逐行打印
            clearScreen();
            for (int i = 0; i < maxLines; i++) {
                String left = i < leftLines.size() ? leftLines.get(i) : "";
                String middle = i < middleLines.size() ? middleLines.get(i) : "";
                String right = i < rightLines.size() ? rightLines.get(i) : "";

                System.out.printf("%-" + LEFT_WIDTH + "s%-" +   MIDDLE_WIDTH + "s%-" +  RIGHT_WIDTH + "s%n",
                        left, middle, right);
            }
        }
```

- reversi打印部分解释

```java
//reversi模式打印内容
    public List<String> generateReversiBoardContent(Player currentPlayer) {
        List<String> lines = new ArrayList<>();
        List<int[]> validMoves = getValid(currentPlayer);//得到当前玩家所有可落子位置坐标
        StringBuilder sb = new StringBuilder();
        sb.append("  A B C D E F G H");
        lines.add(sb.toString());

        for (int i = 0; i < boardSize; i++) {
            sb = new StringBuilder();
            sb.append(i + 1 + " ");
            for (int j = 0; j < boardSize; j++) {
                boolean isValidMove = false;
                for (int[] move : validMoves) {
                    if (move[0] == i && move[1] == j) {//如果是可落子坐标
                        isValidMove = true;
                        break;
                    }
                }
                if (isValidMove) {
                    sb.append(ABLE + " ");//可落子坐标打印“+”
                } else {
                    char piece = board[i][j];
                    sb.append(piece + " ");
                }
            }
            lines.add(sb.toString());
        }
        return lines;
    }
```

## 2、非法输入的判定

### peace模式

- 判定逻辑
    - 先判定特殊字符（不可利用长度进行判定的输入）：`peace` 、`reversi` 、`quit`
    
    ```java
    		String input=sc.nextLine();
    		int length = input.length();//输入长度
    		if(input.equals("quit")){
    		  System.exit(0);
    		}
    		else if(input.equals("peace")){
    		  games.addNewGame("peace");//增加新游戏
    		  GamePrinter.printGame();
    		}
    		else if(input.equals("reversi")){
    		  games.addNewGame("reversi");//增加新游戏
    		  GamePrinter.printGame();
    		
    ```
    
    - 除特殊字符外，其余长度大于2的输入均为非法
    
    ```java
    else if(length>2){
    		  System.out.println("输入格式有误，请重新输入");
    		}
    ```
    
    - 长度等于1的输入：
        - 若输入的数字在gamelist中，则切换棋盘
        
        ```java
        int ch = input.charAt(0) - '0';
        		  if(ch>0&&ch<=games.gameListSize()){
        		      newIndex = ch-1;
        		      break;
        		  }
        ```
        
        - 否则为非法输入
    - 长度等于2的输入：
        - 若gamelist已有两位数的游戏且输入了两位数数字，则切换棋盘
        - 若输入为行号＋列号
            - 是空白处，则落子
            - 否则显示落子处已有棋子
        
        ```java
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
        ```
        

### reversi模式

- 判定逻辑
    - 判定此玩家无合法落子位置
        - `board.getValid(player)` 是空，即
        
        ```java
        if(board.getValid(player).isEmpty()){
                        System.out.println("玩家["+player.getName()+"]已无合法落子位置..");
                    } 
        ```
        
    - `pass` 的判定与实现
        
        ```java
        if(input.equals("pass")){
                        if(board.getValid(player).isEmpty()){
                            break;
                        }
                        else{
                            System.out.println("不允许放弃本轮行棋！");
                        }
        ```
        
    - 落子位置是否为合法落子位置的判定
    
    ```java
    
    else if(length==2){
        int row=input.charAt(0)-'1';
        int col=input.charAt(1)-'a';
        ...
        boolean isValidMove = false;//是否为合法落子位置
        for (int[] move : board.getValid(player)) {
            if (move[0] == row && move[1] == col) {
                isValidMove = true;//在getValid中，是合法落子位置
                break;
            }
        }
        if(isValidMove) {
            board.board[row][col] = player.getPiece();//落子
            board.flipPiece(row,col,player);//翻转棋子
            break;
        }
        else if(row<0||row>7||col<0||col>7){//落子位置超出棋盘
            System.out.println("落子位置有误，请重新输入");
        }
        else if(board.board[row][col]==Board.EMPTY){//下在空地处（非合法落子位置）
            System.out.println("["+input+"]不是合法落子位置！");
        }
        else{
            System.out.println("["+input+"]已经有棋子了！");
        }
    }
    ```
    
    - 其余判定与peace模式相同

## 3、reversi模式相关

### “+”的显示的实现

- 实现逻辑：
    - `getFlip`
        - 从一空白点开始，向八个方向进行遍历寻找对方棋子，并在结尾处有己方棋子的点为落子位置。每个方向的可翻转棋子坐标记入`lineFlips`，将所有可翻转的棋子坐标记入`allFlips`
        - 得到下在某一点处所有可翻转棋子的坐标
    - `getValid`
        - 遍历整个棋盘，查找所有满足`getFlip`中`allFlips`不为空的点，即为所有可落子位置，所有位置以二维数组形式记入`ValidMoves`
        - 得到所有可落子位置坐标

```java
public List<int[]> getFlip(int x, int y, Player currentPlayer) {
        Player otherPlayer = (currentPlayer.getPiece() == BLACK) ? player2 : player1;
        List<int[]> allFlips = new ArrayList<>();
        for (int[] dir : DIRECTIONS) {
            int dx = dir[0];
            int dy = dir[1];
            int nx = x + dx;
            int ny = y + dy;
            List<int[]> lineFlips = new ArrayList<>();
            //查找连续对方棋子
            while (nx >= 0 && nx < boardSize && ny >= 0 && ny < boardSize) {
                if (board[nx][ny] == otherPlayer.getPiece()) {
                    lineFlips.add(new int[]{nx, ny});
                    nx += dx;
                    ny += dy;
                } else {
                    break;
                }
            }
            //若终点处是己方棋子，则代表此点为可落子位置，所有可翻转棋子记入allFlips
	if (nx >= 0 && nx < boardSize && ny >= 0 && ny < boardSize && board[nx][ny] == 
	currentPlayer.getPiece()) {
	  allFlips.addAll(lineFlips);
	}
        }
        return allFlips;
    }

    public List<int[]> getValid(Player currentPlayer){
        List<int[]> ValidMoves = new ArrayList<>();
        for(int i=0;i<boardSize;i++){
            for(int j=0;j<boardSize;j++){
                if(board[i][j]==EMPTY){
                    List<int[]> flips=getFlip(i,j,currentPlayer);
                    if(!flips.isEmpty()){
                        ValidMoves.add(new int[]{i,j});
                    }
                }
            }
        }
        return ValidMoves;
    }
```

### 翻转棋子的实现

- 实现逻辑
    - 遍历棋盘，调用`getFlip` ，得到所有可翻转棋子坐标
    - 将这些坐标上的棋子换为当前玩家的棋子

```java
public void flipPiece(int x, int y,Player currentPlayer){
        for(int i=0;i<boardSize;i++){
            for(int j=0;j<boardSize;j++){
                for(int[] move:getFlip(x,y,currentPlayer)){
                    if(move[0]==i&&move[1]==j){
                        board[i][j]= currentPlayer.getPiece();
                        break;
                    }
                }
            }
        }
    }
```

### 分数计算

- 实现逻辑：遍历棋盘，找寻当前玩家的棋子，并计数

```java
public int calculateScore(char piece) {
        int score = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == piece) {
                    score++;
                }
            }
        }
        return score;
    }
```

## 4、游戏列表相关

### 添加游戏

- 实现逻辑

```java
public void addNewGame(String mode){
        int index=games.size();//棋盘本身的index
        if("peace".equals(mode)){
            games.add(new Game(player1,player2,"peace",index,this));//在gamelist里添加游戏
            games.get(index).board.initializeBoard();//初始化
        }
        else if("reversi".equals(mode)){
            games.add(new Game(player1,player2,"reversi",index,this));
        }
    }
```

### 切换游戏

- 实现逻辑
    - currentGameIndex：当前棋盘索引 是棋盘号码-1
    - newIndex：新选择的棋盘索引
    - index：棋盘所对应的索引（新增棋盘时用到 后面不变且不再会用到）
    - 开始游戏均为startGame（），即切换游戏代表换currentGameIndex的值
    
    ```java
    public void switchGame(int newIndex){
            if(newIndex>=0&&newIndex<=games.size()){
                currentGameIndex=newIndex;
            }
        }
     public void startGame(){
            games.get(currentGameIndex).play();
        }
    ```
    

# 运行结果截图及简单说明

注：由于所使用界面为黑夜模式，导致黑子会显示成全白，白子会显示成白框黑子

## 初始界面

![image.png](attachment:e85f7ac2-a88f-40f4-a410-7eda904e8558:image.png)

![image.png](attachment:5e8e4e60-40fd-48b5-9f1a-9946327b46ba:image.png)

![image.png](attachment:73c274bc-6cd8-4812-b139-9d7d401bbf90:image.png)

![image.png](attachment:bc316e46-6abb-428e-adc3-f6060b903988:image.png)

![image.png](attachment:5046889f-6d4d-46cd-b179-ff6210252b8e:image.png)

## peace模式判定非法输入

![image.png](attachment:3c7243d4-c8a1-4e8f-93e9-03d195618796:image.png)

## reversi模式运行

![image.png](attachment:b33fdfc8-b4eb-48a3-81ab-ca038aaa3d9a:image.png)

![image.png](attachment:86b32d80-fba4-4afd-9cf3-01bfbe4b2f86:image.png)

![image.png](attachment:19ca9254-ba09-4aef-b42b-55bf0738f32d:image.png)

![image.png](attachment:8a9dec8a-2116-485f-940e-19f29ac61bec:image.png)

## reversi模式判定非法输入

![image.png](attachment:0aa59f82-0f3a-4c78-bd83-692d1d567337:image.png)

## reversi模式：实现与判定pass

![image.png](attachment:9344b4bd-ab07-4fa6-a2f3-bfc42328d859:image.png)

![image.png](attachment:6aa038c4-1e2c-4121-b36e-9c268204d543:image.png)

![image.png](attachment:99beba19-5090-4f47-923e-ce9a95178596:image.png)

![image.png](attachment:c882805d-205a-421b-8f2c-4750924461e6:image.png)

![image.png](attachment:efaddddd-1eac-44ad-ad08-a05e078e7c26:image.png)

## 添加新游戏

### reversi模式添加新游戏

![image.png](attachment:96f669d2-d1fa-4103-ba1e-e177325db6b1:image.png)

![image.png](attachment:caff8f9c-1ed7-4eb5-9339-1e807ec1ec98:image.png)

### peace模式添加新游戏

![image.png](attachment:7735788f-d1c6-40fb-96f9-9a96664df7f2:image.png)

![image.png](attachment:5e4a4096-38e1-4e20-8198-ff47cb36970c:image.png)

## 切换游戏

### 现有游戏切换棋盘

![image.png](attachment:14983b8a-5fa2-44df-a0a7-947c243a96ef:image.png)

![image.png](attachment:5e8e4e60-40fd-48b5-9f1a-9946327b46ba:image.png)

### peace模式切换到新增棋盘

![image.png](attachment:1139811e-3c6e-4f0c-b970-a576b92aa318:image.png)

![image.png](attachment:1915aa3a-993f-4316-a00b-11f913a7d279:image.png)

### reversi模式切换到新增棋盘

![image.png](attachment:50f32adb-0f52-4381-9867-2d7b5f7638c8:image.png)

![image.png](attachment:f458665c-e861-4d90-987a-cbfdd0444741:image.png)

### 再切回现有棋盘

![image.png](attachment:d0e14692-fe61-4a9a-848e-afd9ebb61a83:image.png)

![image.png](attachment:e0070547-01bf-440a-9bda-e21abedbf437:image.png)

![image.png](attachment:462c7bfe-2cfb-48ff-9c8f-e584d150cfb7:image.png)

![image.png](attachment:4c6fa1b3-79c8-44aa-be84-91688823be0b:image.png)

## 游戏结束

### peace模式

![image.png](attachment:025f641f-9ad0-4f53-b5d9-7e26513b76be:image.png)

![image.png](attachment:6d766c9a-770d-4888-ab59-68ee68951d79:image.png)

### reversi模式

- 双方均无合法落子位置

![image.png](attachment:7de9f410-7aec-4fa7-ae2c-daa1fd64bb3b:image.png)

![image.png](attachment:843fec81-adba-4486-994c-8b8e08e4c73d:image.png)

- 棋盘下满

![image.png](attachment:79b48406-1255-4ff0-bcfe-e033ea2c86f5:image.png)

![image.png](attachment:56b8ecf6-d4ea-4f05-b914-ca4e0405b43b:image.png)

## 游戏结束后切换棋盘查看结果

![image.png](attachment:46a1123f-3ddd-4462-848e-fe5a48eec215:image.png)

![image.png](attachment:0049df7d-6991-46d4-8c8d-8e3137715d75:image.png)

![image.png](attachment:f7a8dc4b-db5e-4851-9fb7-d9737f6a04a1:image.png)

![image.png](attachment:af7235f3-7bd7-4d95-9cc1-23d6dbf9d54b:image.png)

## 退出程序

![image.png](attachment:c1822981-f3a7-4b7f-b5e2-dacfd9b5ebfd:image.png)
