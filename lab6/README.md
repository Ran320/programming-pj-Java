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

### Board类（新增了对gomoku模式棋盘已满的判断）

```java
**//检查gomoku模式下棋盘是否已满（新增部分）
		public boolean isGomokuBoardFull()**
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
// getFlip：得到下在某一点处所有可翻转棋子的坐标
    public List<int[]> getFlip(int x, int y, Player currentPlayer) 
// getValid：得到所有可落子位置坐标
    public List<int[]> getValid(Player currentPlayer)
//翻转棋子
    public void flipPiece(int x, int y,Player currentPlayer)
```

### Game类（新增读取文件命令部分，以及重构部分代码）

```java
**//获取炸弹数量
    public int getBomb(Player currentPlayer)
//获取输入/命令
    private String getNextCommand()
// 处理用户输入和命令
		public void handleInput(String input,Player player) 
// 处理单字符输入：换棋盘
		private void handleSingleCharInput(String input) 
// 处理双字符输入：换棋盘或落子
		private void handleTwoCharInput(Player player, String input)
// 处理炸弹命令
    private void handleBombCommand(Player player, String input) 
// 处理落子逻辑
    private void handleMove(Player player, String input) 
//读取文件命令
    private void handlePlaybackCommand(String input)** 
// peace模式
    public void peaceMove(Player player)
// reversi模式
    public void reversiMove(Player player)
// gomoku模式
    public void gomokuMove(Player player) 
//peace游戏实现
    public void peacePlay(Player player1, Player player2,Board board)
//reversi游戏实现
    public void reversiPlay(Player player1,Player player2,Board board)
//gomoku游戏结束判定
    public boolean ifGomokuEnd(int x, int y, Player currentPlayer) 
//Gomoku游戏实现
    public void gomokuPlay(Player player1,Player player2,Board board)
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
//玩家信息部分打印（新增gomoku模式下打印round）
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
        Player player1 = new Player("Tom", Board.BLACK);
        Player player2 = new Player("Jerry", Board.WHITE);
//创建游戏
        GameList gameList = new GameList(player1, player2);
//开始游戏
        while(true) {
            gameList.startGame();
        }
```

# 关键代码及其设计思路

## Game类代码思路

![image.png](attachment:74368399-856d-4c4a-a02b-a1fd0fe21edb:image.png)

![image.png](attachment:6ae1a132-3740-435c-916f-3dbe68f5737d:image.png)

## 关键代码思路

- gomoku棋盘扩大的实现：
    - 在 `Board` 类中增加 `gomokuBoard` `gomokuBoardSize` ，与gomoku模式有关的方法均用此来区分棋盘
- 读取文件命令的实现：
    
    ```c
    private Queue<String> commandQueue = new LinkedList<>();
    ```
    
    - 当识别到类似 `playback test1.cmd` 的输入时，将文件中的命令全部一次性读取并存储进 `commandQueue` 中，之后每次读取一行来实现操作
- `private String getNextCommand()`

```c
//获取输入/读取命令
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

```

- `public void handleInput(String input,Player player)`

```csharp
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
```

- `private void handleSingleCharInput(String input)`

```c
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
```

- `private void handleTwoCharInput(Player player, String input)`

```java
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

```

- `private void handleBombCommand(Player player, String input)`

```c
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
                System.out.println("此处不可放置炸弹！请重新输入");//只能在对方落子处放置炸弹
            }
        } else {
            System.out.println("你已无可用炸弹！请输入其他操作");
        }
    }

```

- `private void handleMove(Player player, String input)`

```c
// 处理落子逻辑
    private void handleMove(Player player, String input) {
        int row = Character.digit(input.charAt(0), 16) - 1;//应对gomoku16进制的棋盘
        int col = Character.toUpperCase(input.charAt(1)) - 'A';//防止大小写造成误判
        // 检查落子位置是否有效（是否越界）
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

```

- `private void handlePlaybackCommand(String input)`

```clojure
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
```

修改后的相应 `move` 方法：

```c
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
```

- 增加炸弹显示
    
    ```c
    //玩家信息部分打印
        private static List<String> getPlayerLines(Game currentGame) {
            List<String> lines = new ArrayList<>();
            String mode = currentGame.getMode();
            int index = currentGame.index+1;
            String p1Line = String.format("玩家[%s]: %s %s   %s",
                    currentGame.player1.getName(),
                    (gameList.currentPlayer == 1 ? currentGame.player1.getPiece()+" " :"  "),
                    (mode.equals("reversi") ? currentGame.board.calculateScore(currentGame.player1.getPiece())+" " : ""),
                    **(mode.equals("gomoku") ?currentGame.getBomb(currentGame.player1):" ")**
            );
    
            String p2Line = String.format("玩家[%s]: %s %s   %s",
                    currentGame.player2.getName(),
                    (gameList.currentPlayer == 2 ? currentGame.player2.getPiece()+" " :"  "),
                    (mode.equals("reversi") ? currentGame.board.calculateScore(currentGame.player2.getPiece())+" " : ""),
                    **(mode.equals("gomoku") ?currentGame.getBomb(currentGame.player2):" ")**
            );
            **if(mode.equals("gomoku")){
                lines.add("棋盘"+index+"          Bombs");
            }**
            else{
                lines.add("棋盘"+index);
            }
            lines.add(p1Line);
            lines.add(p2Line);
            if(mode.equals("gomoku")){
                lines.add("Current Round:"+gameList.getCurrentGame().getCurrentRound());
            }
    
            return lines;
        }
    ```
    

# 运行过程截图

## 五子棋部分

### 初始棋盘

![屏幕截图 2025-05-25 231920.png](attachment:83a0c517-d6cc-446d-8bcd-6a16b62fde64:屏幕截图_2025-05-25_231920.png)

### 落子

![image.png](attachment:5cab05ba-82d1-4438-bbf9-96a7cfb61fc2:image.png)

### 障碍无法落子

![屏幕截图 2025-05-25 231920.png](attachment:2539987b-20d2-4ce4-96fc-ba0967cd827a:屏幕截图_2025-05-25_231920.png)

![屏幕截图 2025-05-25 231934.png](attachment:b15ef87c-dca4-4672-9dd9-b19761100b64:屏幕截图_2025-05-25_231934.png)

### 使用炸弹

![image.png](attachment:8ca9fa2e-49c6-45a8-b5b9-3711caf363f0:image.png)

### 炸弹合法判断

![image.png](attachment:3ac24c1f-f0d3-4197-bb4d-203a202004ae:image.png)

![image.png](attachment:e944b78d-c147-46db-bb01-8f0c47d724be:image.png)

## demo模式部分

注：demo模式自行测试时用的cmd文件也包含在压缩包里，路径：mypackage\demo\src\main\java

test1：

![image.png](attachment:fe3231aa-2824-4722-9eea-c957f22cebb4:image.png)

![image.png](attachment:e1b6f9ba-d8ef-4c13-961f-a9656fdceb05:image.png)

demo运行结束后键盘输入操作依旧正常：

![image.png](attachment:c88625b8-1df2-406c-99ec-3fef44bfb5cf:image.png)

![image.png](attachment:7ec44983-1ed3-4c71-85a5-39b0a420ffc7:image.png)

test2：

![image.png](attachment:3c4b66d1-c677-474b-9ae1-63ff69a59e12:image.png)

![image.png](attachment:515458f6-3c97-4567-877e-c1a0e8dde23e:image.png)

键盘输入正常：

![image.png](attachment:0c542303-78ad-4cec-8a8b-0af0326e04bb:image.png)

![image.png](attachment:dc29c2a6-41e3-445e-8e33-ed7f59f2cde1:image.png)

test3：

![image.png](attachment:79f22b0c-53ef-4116-830b-df5a1206ca8a:image.png)

![image.png](attachment:4dce1e65-58f2-4c52-a15a-0bf797233d50:image.png)

键盘输入正常：

![image.png](attachment:c9472cdc-af81-49c8-914c-683267b865bf:image.png)

![image.png](attachment:d9143254-ca89-4295-9016-efb73de2d7d8:image.png)
