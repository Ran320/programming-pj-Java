# 源代码文件名称及其主要功能

```csharp
src
└── main
    ├── java
    │   └── org
    │       └── example
    │           └── pj
    │               ├── Main.java    //主入口 
    │               ├── GameController.java    //控制器
    │               └── game    //后端代码
    │                   ├── Board.java   //棋盘相关
    │                   ├── Game.java    //游戏相关
    │                   ├── GameList.java  //游戏列表控制
    │                   └── Player.java   //玩家信息
    └── resources
         └── com
             └── example
                  └── pj
                    └── main.fxml
```

## GameController

```java
		@FXML
    public void initialize()

    //初始化游戏
    public void initializeState()
    //换游戏
    public void switchGame()

    //游戏相关显示
    //显示棋盘
    public void initializeShowBoard()
    //游戏游玩-单击格子下棋
    public void handleCellClick(int row, int col)
    //演示模式棋盘相关
    public void playBack(File file) 
    // 更新棋盘显示
    public void updateBoardDisplay() 
    //调整右边三列的大小
    public void adjustSize()
    //第一列按钮的设置
    public void column1Button()
    //第一列的游戏状态打印
    public void column1State()
    //pass
    public void pass()
    //使用炸弹
    public void useBomb()
    //打印结束语
    public void endTxt()
    //更新Gamelist
    public void updateGameList()
    //演示模式
    public void showingState()

    //添加peace
    @FXML
    private void newPeaceGame() 
    //添加reversi
    @FXML
    private void newReversiGame() 
    //添加gomoku
    @FXML
    private void newGomokuGame() 
```

## GameList新增

```java
//保存游戏状态
    public void saveGameState() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("pj.game"))) {
            out.writeObject(this);
            System.out.println("现在的currentplayer"+currentPlayer);
            System.out.println("写入成功");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("保存游戏状态失败", e);
        }
    }
    //读取游戏状态
    public static GameList loadGameState() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("pj.game"))) {
            return (GameList) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
```

# 关键代码及其设计思路

## 界面调整的实现

- 参考文档，实现：
    - 宽度变化时棋盘位于左上角，大小不变，右侧三列均匀占据空间
    - 高度变化时棋盘也增大，保持位于左上角，右侧三列均匀占据空间
- 设计思路：
    - 为方便设置各模块的位置，在最底层设置和窗口始终保持一样大小的`AnchorPane`
    - 棋盘：利用`GridPane`（命名为`boardGrid`），设置其高度永远为窗口高度的75%，同时将宽度和高度绑定以保持正方形，将每个格子设置为`Button` 并设置监听
    - 右侧三列：
        - 设置为`Hbox`，其中每一列为一个`Vbox` ，用窗口宽度-棋盘宽度即为三列总宽度，除去为保证美观每两列之间设置的固定宽度，再除以3即得到每列的宽度，绑定。
        - 利用`hbox.setTranslateX(chessWidth);` 将`Hbox`绑定在棋盘右侧
- 相应代码：
    
    ```java
    //显示棋盘
        public void initializeShowBoard() {
            column1State();//更新第一列游戏状态
    
            boardGrid.getChildren().clear(); // 清空棋盘
            boardGrid.getRowConstraints().clear(); // 清空旧的行约束
            boardGrid.getColumnConstraints().clear(); // 清空旧的列约束
    
            //GridPane可以拉伸大小
            boardGrid.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            boardGrid.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    
            //棋盘高度始终为窗口的80%，宽度和高度永远相等
            boardGrid.prefHeightProperty().bind(rootPane.heightProperty().multiply(0.75));
            boardGrid.prefWidthProperty().bind(boardGrid.prefHeightProperty());
    
            //令行和列均匀分布
            for (int i = 0; i < currentGame.getSize() + 1; i++) {
                boardGrid.getRowConstraints().add(new javafx.scene.layout.RowConstraints(Double.MIN_VALUE, 40, Double.MAX_VALUE, Priority.ALWAYS, javafx.geometry.VPos.CENTER, true));
                boardGrid.getColumnConstraints().add(new javafx.scene.layout.ColumnConstraints(Double.MIN_VALUE, 40, Double.MAX_VALUE, Priority.ALWAYS, javafx.geometry.HPos.CENTER, true));
            }
    
            //棋盘显示部分
            for (int i = 0; i < currentGame.getSize() + 1; i++) {
                for (int j = 0; j < currentGame.getSize() + 1; j++) {
                    //打印第一行--列字母
                    if (i == 0) {
                        Label label = new Label();
                        //设置为第一行
                        GridPane.setRowIndex(label, i);
                        //设置对应文字
                        if (j != 0) {
                            label.setText(String.valueOf((char) ('A' + j - 1)));
                            label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                            label.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
                            label.setAlignment(Pos.CENTER); // 内容居中
                            label.setTextAlignment(TextAlignment.CENTER); // 文本居中
                        } else {
                            label.setText(" ");
                        }
    
                        //格子能拉伸大小
                        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        label.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
                        boardGrid.add(label, j, i);
                    }
                    //打印第一列--行号
                    else if (j == 0 && i != 0) {
                        Label label = new Label();
                        GridPane.setColumnIndex(label, j);
                        //设置文字--行号为16进制表示
                        label.setText("   " + Integer.toHexString(i).toUpperCase());
                        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        label.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
                        label.setAlignment(Pos.CENTER); // 内容居中
                        label.setTextAlignment(TextAlignment.CENTER); // 文本居中
    
                        boardGrid.add(label, j, i);
                    }
                    //棋盘部分
                    else {
                        final int row = i - 1;
                        final int col = j - 1;
    
                        Button button = new Button();
                        buttons.add(button);
                        GridPane.setRowIndex(button, i);
                        GridPane.setColumnIndex(button, j);
    
                        char piece = currentGame.getPiece(i - 1, j - 1, currentPlayer);
                        if (piece == Board.BLACK) {
                            button.setText("●");
                        } else if (piece == Board.WHITE) {
                            button.setText("○");
                        } else if (piece == Board.ABLE) {
                            button.setText("+");
                        } else if (piece == Board.BARRIER) {
                            button.setText("#");
                            button.setDisable(true);
                        } else if (piece == Board.BOMB) {
                            button.setText("*");
                            button.setDisable(true);
                        } else {
                            button.setText(" ");
                        }
    
                        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                        button.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
                        button.setOnAction(event -> handleCellClick(row, col));
                        boardGrid.add(button, j, i);
    
                    }
                }
            }
            endTxt();
        }
    
    //调整右边三列的大小
        public void adjustSize(){
    
            // 右侧区域总宽度 = 根容器宽度 - 棋盘宽度
            double vboxSpacing = 10; // VBox之间的间距
    
            // 创建DoubleBinding来计算三等分宽度
            DoubleBinding oneThirdWidth = rootPane.widthProperty()
                    .subtract(boardGrid.prefWidthProperty())
                    .subtract(vboxSpacing * 2)
                    .divide(3);
    
            // 将计算绑定到每个VBox的宽度
                    column1.prefWidthProperty().bind(oneThirdWidth);
                    column2.prefWidthProperty().bind(oneThirdWidth);
                    column3.prefWidthProperty().bind(oneThirdWidth);
    
            // VBox高度 = 棋盘高度
            column1.prefHeightProperty().bind(boardGrid.prefHeightProperty());
            column2.prefHeightProperty().bind(boardGrid.prefHeightProperty());
            column3.prefHeightProperty().bind(boardGrid.prefHeightProperty());
    
            // hBox位置绑定
            boardGrid.widthProperty().addListener((obs, oldVal, newVal) -> {
                double chessWidth = boardGrid.getWidth();
                hbox.setTranslateX(chessWidth);
            });
    
            boardGrid.heightProperty().addListener((obs, oldVal, newVal) -> {
                double chessHeight = boardGrid.getHeight();
                txtArea.setTranslateY(chessHeight+10);
            });
            txtArea.setTranslateX(20);
            txtArea.prefWidthProperty().bind(boardGrid.prefHeightProperty());
    
            // 强制重新布局
            rootPane.requestLayout();
    
        }
    ```
    

## 不同类型对应游戏界面不同的实现

### 关于`pass`和`炸弹`

- 设计思路：
    - 对应按钮在`peace`模式下不可见，`reversi`模式下可见且点击后调用`pass`对应方法，`gomoku`模式下可见且点击后调用`useBomb`对应方法
- 代码：
    
    ```java
    	//第一列按钮的设置
        public void column1Button(){
            if(currentGame.getMode().equals("peace")){
                button.setVisible(false);
            }
            else if(currentGame.getMode().equals("reversi")){
                button.setVisible(true);
                button.setText("Pass");
                button.setOnAction(event -> pass());
            }
            else{
                button.setVisible(true);
                button.setText("使用炸弹");
                button.setOnAction(event -> useBomb());
            }
        }
    
    //pass
        public void pass(){
            if(currentGame.board.getValid(currentPlayer).isEmpty()){
                int otherPlayer = gameList.getCurrentPlayer()==1?2:1;
                gameList.setCurrentPlayer(otherPlayer);
                currentPlayer = gameList.getCurrentPlayer()==1?player1:player2;
                updateBoardDisplay();
            }
        }
    
        //使用炸弹
        public void useBomb(){
            currentGame.setBombMode(true);
            updateBoardDisplay();
        }
    ```
    

### 关于玩家状态的显示

- 设计思路：
    - 三行均用`Label`
    - 最上侧显示棋盘编号，直接调用即可
    - 玩家1、2的状态均在后端获取，形成字符串返回到前端
    - `gomoku`模式下轮数`Label`才可见，并从后端获取当前轮数
- 代码：
    
    ```java
    //第一列的游戏状态打印
        public void column1State(){
            int index = currentGame.getIndex()+1;
            //棋盘序号
            gomokuRound.setVisible(false);
            boardIndex.setText("棋盘"+index);
    
            //玩家1状态打印
            player1State.setText(currentGame.getPlayer1State());
    
            //玩家2状态打印
            player2State.setText(currentGame.getPlayer2State());
    
            //gomoku模式显示轮数
            if(currentGame.getMode().equals("gomoku")){
                gomokuRound.setVisible(true);
                gomokuRound.setText("当前轮数："+currentGame.getCurrentRound());
            }
    
        }
    ```
    
    ```java
        public String getPlayer1State(){
            StringBuilder sb = new StringBuilder();
            sb.append("玩家["+player1.getName()+"]:"+(games.currentPlayer==1?player1.getPiece():"  "));
            if(mode.equals("reversi")){
                sb.append(board.calculateScore(player1.getPiece()));
            }
            if(mode.equals("gomoku")){
                sb.append(" bomb*"+bombNum1);
            }
            player1State = sb.toString();
            return player1State;
        }
    
        public String getPlayer2State(){
            StringBuilder sb = new StringBuilder();
            sb.append("玩家["+player2.getName()+"]:"+(games.currentPlayer==2?player2.getPiece():"  "));
            if(mode.equals("reversi")){
                sb.append(board.calculateScore(player2.getPiece()));
            }
            if(mode.equals("gomoku")){
                sb.append(" bomb*"+bombNum2);
            }
            player2State = sb.toString();
            return player2State;
        }
    
        public int getCurrentRound(){
            if(round%2==0){
                return round/2;
            }
            else{
                return (round+1)/2;
            }
        }
    ```
    
- 结束时提示语的实现与此类似，均是从后端获取对应字符串在前端显示
    
    ```java
    //打印结束语
        public void endTxt(){
            if(currentGame.getIsGameEnd()){
                txtArea.setVisible(true);
                txtArea.setText(currentGame.getEndTxt());
            }
            else{
                txtArea.setVisible(false);
            }
        }
        
        public String getEndTxt(){
            if(mode.equals("peace")){
                endTxt = "此棋盘已满！\n请更换棋盘游玩！";
            }
            else if(mode.equals("reversi")){
                StringBuilder sb = new StringBuilder();
                sb.append("本局游戏结束！\n可输入数字切换棋盘或添加游戏棋盘\n游戏结果： " + "玩家[").append(player1.getName()).append("]").append(player1.getPiece()).append("得分：").append(board.calculateScore(player1.getPiece())).append("\n").append("\t\t").append("玩家[").append(player2.getName()).append("]").append(player2.getPiece()).append("得分：").append(board.calculateScore(player2.getPiece())).append("\n");
                if(board.calculateScore(BLACK)> board.calculateScore(WHITE)){
                    sb.append("黑方:玩家[").append(player1.getName()).append("]获胜！");
                }
                else if(board.calculateScore(BLACK)< board.calculateScore(WHITE)){
                    sb.append("白方:玩家[").append(player2.getName()).append("]获胜！");
                }
                else sb.append("双方平局！");
                endTxt = sb.toString();
            }
            else{
                StringBuilder stringBuilder = new StringBuilder();
                Player winPlayer = games.getCurrentPlayer()==1?player2:player1;
                if(board.isGomokuBoardFull()){
                    stringBuilder.append("棋盘已满！\n");
                }
                else{
                    stringBuilder.append("玩家["+winPlayer.getName()+"]获胜！\n");
                }
                stringBuilder.append("游戏结束！此时不可再落子，可切换棋盘或添加游戏棋盘");
                endTxt = stringBuilder.toString();
            }
            return endTxt;
        }
    ```
    

## playback模式的实现

- 设计思路：读取文件，在后端执行指令，前端同时进行显示
- 代码
    
    ```java
    //演示模式
        public void showingState(){
            showState.setOnAction(event -> {
                // 创建一个新的窗口
                Stage fileChooserStage = new Stage();
                fileChooserStage.initModality(Modality.APPLICATION_MODAL); // 设置为模态窗口
                fileChooserStage.setTitle("文件选择器");
    
                // 创建一个文件选择器
                FileChooser fileChooser = new FileChooser();
    
                // 设置文件选择器的标题
                fileChooser.setTitle("选择文件");
    
                // 显示文件选择对话框
                File selectedFile = fileChooser.showOpenDialog(fileChooserStage);
    
                // 检查是否选择了文件
                if (selectedFile != null) {
                    // 调用读取文件的函数
                    playBack(selectedFile);
                }
            });
            updateBoardDisplay();
        }
        
        //演示模式棋盘相关
        public void playBack(File file) {
            currentGame.handlePlaybackCommand(file);
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    
            executor.execute(() -> {
                String input;
                while (true) {
                    input = currentGame.getNextCommand();
                    int otherPlayer = gameList.getCurrentPlayer()==1?2:1;
                    if (input == null) {
                        break;
                    }
                    //后端处理输入
                    currentGame.handleInput(input, currentPlayer);
    								//有效操作
                    if(currentGame.getIsValidMove()||currentGame.getIsValidPass()||currentGame.isValidBomb){
                        gameList.setCurrentPlayer(otherPlayer);
                        currentPlayer = gameList.getCurrentPlayer()==1?player1:player2;
                    }
    
                    // 确保在 JavaFX Application Thread 中更新棋盘显示
                    Platform.runLater(() -> {
                        updateBoardDisplay();
                    });
    
                    // 延时 1 秒
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                executor.shutdown(); // 关闭线程池
            });
        }
        
    ```
    

后端复用：(`Game`类中）

```java
 //获取输入/命令
    public String getNextCommand(){
        if (!commandQueue.isEmpty()) {
            return commandQueue.poll();
        } else {
            Scanner sc = new Scanner(System.in);
            return sc.nextLine();
        }
    }
    
    //读取文件命令
    public void handlePlaybackCommand(File file) {
        //System.out.println("handleplaybackcommand");
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
            //System.out.println("duquzhilingwancheng");
        } catch (IOException e) {
            System.out.println("读取文件失败: " + e.getMessage());
        }
    }

```

## Gamelist与换棋盘

- 设计思路：
    - 监听`GameList`，解析字符串获取新游戏的索引进行切换，同时更新棋盘、右侧第一列的显示
- 代码
    
    ```java
    //更新Gamelist
        public void updateGameList(){
            ObservableList<String> gameItems = FXCollections.observableArrayList();
            for (Game game : gameList.getGames()) {
                gameItems.add(game.getIndex()+1 + " " + game.getMode());
            }
            gameListView.setItems(gameItems);
        }
    
    //换游戏
        public void switchGame(){
            // 监听GameList-换棋盘
            gameListView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    // 解析字符串以获取游戏的索引
                    String[] parts = newItem.split(" ");
                    int newIndex = Integer.parseInt(parts[0]) - 1;
                    gameList.switchGame(newIndex);
                    currentGame = gameList.getCurrentGame();
                    initializeShowBoard();
                    column1Button();
                }
            });
        }
    ```
    

## `quit`

- 使所有类序列化，在点击`quit`时调用相应保存状态的方法，保存至`pj.game`并退出
- 在初始加载游戏状态时，先找寻目录下是否有 `pj.game`
    - 有：调用对应加载状态的方法
    - 无：新建`gameList`
- 代码：
    
    ```java
    //初始化游戏
        public void initializeState(){
            player1 = new Player("Tom", Board.BLACK);
            player2 = new Player("Jerry", Board.WHITE);
            // 加载游戏状态
            File gameStateFile = new File("pj.game");
            if (gameStateFile.exists()) {
                System.out.println("继续上一盘游戏");
                gameList = GameList.loadGameState();
                currentGame = gameList.getCurrentGame();
                currentPlayer = gameList.getCurrentPlayer()==1?player1:player2;
            } else {
                System.out.println("开启新游戏");
                gameList = new GameList(player1, player2);
                currentGame = gameList.getCurrentGame();//进入时是棋盘1-peace模式
                currentPlayer = player1;
                gameList.setCurrentPlayer(1);//初始状态玩家1先手
            }
    
            initializeShowBoard();
        }
        
       //quit
        public void quit(){
            quitButton.setOnAction(event -> {
                gameList.saveGameState();
                javafx.application.Platform.exit();
            });
        }
    ```
    
    ```java
    //保存游戏状态
        public void saveGameState() {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("pj.game"))) {
                out.writeObject(this);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("保存游戏状态失败", e);
            }
        }
        //读取游戏状态
        public static GameList loadGameState() {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("pj.game"))) {
                return (GameList) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    ```
    

# Lab6和PJ代码的复用情况

## lab6代码特点

1. 界面利用`GamePrinter`类，各个类获取需要打印的部分并传递过来，GamePrinter负责整合并分块打印到控制台上（在`pj`中是通过`javafx`实现的）
- `GamePrinter`的代码：
    
    ![image.png](attachment:73649a01-150a-4d21-a8a7-1ca983a77f71:image.png)
    
1. `Game`中的方法既负责`UI`，也负责游戏逻辑执行，前后端耦合在一起
    - `Game`的代码：
    
    ![image.png](attachment:9e86dff6-a91c-46f2-bb70-765fc11f13cd:image.png)
    
    ![image.png](attachment:74368399-856d-4c4a-a02b-a1fd0fe21edb:image.png)
    
- 复用思路：将`UI`和游戏逻辑拆分，前端负责`UI`，后端负责游戏逻辑
- 即前端获取坐标/功能/命令，之后调用后端的方法，让后端对其进行处理

## 复用情况

1. **结束语的判定和输出拆分、复用：**
    - `lab6`中：以`peace`模式为例
    
    ```java
    //peace游戏实现
        public void peacePlay(Player player1, Player player2,Board board){
            while(!board.isBoardFull()){
                //游戏逻辑
            }
            //结束语判定和输出
            if(board.isBoardFull()){
                Player otherPlayer=games.getCurrentPlayer()==1?player1:player2;
                if(!isValidAdd){
                    otherPlayer=games.getCurrentPlayer()==1?player2:player1;
                    int otherPlayerIndex=games.getCurrentPlayer()==1?2:1;
                    games.setCurrentPlayer(otherPlayerIndex);
                    board.resetCurrentPlayer(otherPlayerIndex);
                }
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
    ```
    
    - `pj`中：结束判定统一以`isGameEnd`为标志返回到前端，**同时复用之前的结束语，将所有结束语整合到利用`getEndTxt`一起返回到前端**
2. **对于落子/功能的处理**
    - `lab6`中由于是以输入方式操作，会出现越界、键盘输入混乱等问题，需要**先判定输入是否合规、判定输入操作**（落子、换棋盘、炸弹等），再进行游戏逻辑处理
    - `pj`中由于操作方式换为点击，不存在以上问题，判定输入操作的代码可以删去或简化，同时游戏逻辑**可以完全复用**lab6中对逻辑处理的代码
    - 同时由于`pj`依旧保留`playback`模式，有读取需求，所以判定输入是否合法（越界或无法输入等）的逻辑依旧存在，只是可以把报错内容删去
    - 以`handleMove`为例：(其余操作同理）
        - lab6：
            
            ```java
            // 处理落子逻辑
                private void handleMove(Player player, String input) {
                    int row = Character.digit(input.charAt(0), 16) - 1;
                    int col = Character.toUpperCase(input.charAt(1)) - 'A';
                    // 检查落子位置是否有效
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
            
        - pj:
            
            ```java
            // 处理落子逻辑
                public void handleMove(Player player, int row, int col) {
                    isValidMove = false;//返回到前端，处理其余操作
                    if ("gomoku".equals(mode)) {
                        if(row >= 0 && row < gomokuBoardSize && col >= 0 && col < gomokuBoardSize){
                            if (board.gomokuBoard[row][col] == Board.EMPTY) {
                                board.gomokuBoard[row][col] = player.getPiece();
                                isValidMove = true;
                                if (ifGomokuEnd(row, col, player)) {
                                    isGomokuEnd = true;
                                }
                                else{
                                    round++;
                                }
                            }
                        }
                    } else if("reversi".equals(mode)){
                        if(row >= 0 && row < boardSize && col >= 0 && col < boardSize){
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
                        }
                    } else{
                        if(row >= 0 && row < boardSize && col >= 0 && col < boardSize){
                            if (board.board[row][col] == Board.EMPTY) {
                                board.board[row][col] = player.getPiece();
                                isValidMove=true;
                            }
                        }
                    }
                } 
            ```
            
3. **`playback`**
    - 逻辑几乎一致，只是获取文件这一步变为前端获取，其余处理逻辑与`lab6`相同，可以复用（可见前关键代码部分）

# 新增游戏的分析（2048为例）

## 初步设计

![image.png](attachment:011a158c-5eb2-4b8d-b3d1-7276ee584adb:image.png)

### 调整：

1. **新增2048游戏类，继承自`Game`**

```java
public class Game2048 extends Game {
    private int score;
    private boolean isGameOver;

    public Game2048() {
        super(new Board2048(), List.of(new Player("2048 Player")));
        this.players.get(0).setPiece(Piece.NONE); // 设置棋子类型为NONE
    }

    @Override
    public void makeMove(Object moveData) {
        Direction direction = (Direction) moveData;
        Board2048 board = (Board2048) this.board;
        board.slideTiles(direction);
        board.mergeTiles();
        this.score += board.calculateScore();
        // 生成新方块逻辑
    }
}

```

1. **新增2048棋盘类，继承自`Board`**

```java
public class Board2048 extends Board {
    public int[][] grid;
    public Board2048() {
        super(4);
        this.grid = new int[4][4];
    }
    public void slideTiles(Direction direction) {
        // 滑动逻辑
    }
    public void mergeTiles() {
        // 合并逻辑
    }
}

```

1. **扩展`Player`类支持棋子类型**

```java
public class Player {
    private String name;
    private Piece piece; // 新增棋子属性

    public Player(String name) {
        this.name = name;
        this.piece = Piece.NONE; // 默认值
    }

    // Getter/Setter
    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
}

```

1. **新增方向枚举**

```java
public enum Direction {
    UP, DOWN, LEFT, RIGHT
}

```

1. 新增`GameController2048`类，继承自`GameController`

```java
public class GameController2048 extends GameController {

    public void handleSwipe(Direction direction) {
        ((Game2048) currentGame).move(direction);
        updateUI();
    }

    @Override
    public void updateUI() {
        renderGrid();
        updateScoreDisplay();
    }

    private void renderGrid() {
        // 渲染2048网格
    }
}

```

- `Direction`枚举：定义移动方向
- `Board2048`：实现滑动/合并
- `Game2048`：在`makeMove()`方法中将Object转换为Direction
- `GameController2048`：处理滑动

# 运行结果截图

## 初始界面、换棋盘、新增棋盘

![image.png](attachment:2cd8be84-169e-4824-938e-087d3b35234e:image.png)

![image.png](attachment:a7e4f0fe-ce1e-4084-b298-b845e16de529:image.png)

![image.png](attachment:bd4b59e6-dec1-4433-82c9-45d5a2ffd07a:image.png)

![image.png](attachment:15e4ae4f-8973-4c60-a003-41a450af6f78:image.png)

## 界面调整

![image.png](attachment:2cd8be84-169e-4824-938e-087d3b35234e:image.png)

![image.png](attachment:50f17176-c8d2-4081-b14e-093cecc45e26:image.png)

![image.png](attachment:231580c6-9c8f-45cd-b8bf-058d5c0c561b:image.png)

## 退出游戏

退出前：

![image.png](attachment:3ea1324e-c446-4205-9c38-bd5afabd846f:image.png)

再次进入：

![image.png](attachment:876dbfd5-548d-4466-a7cd-0b019fbf89b6:image.png)

## 演示模式及结束

![image.png](attachment:b008cabd-bd87-4fe0-b55e-ee5f9d718c63:image.png)

![image.png](attachment:f3ad3f1d-2686-4565-883e-7ba0d1b8f831:image.png)

![image.png](attachment:683235ca-5da8-411c-ba9b-cff840e25804:image.png)

添加游戏、换游戏正常

![image.png](attachment:2d52509c-152e-4ca9-9c43-48ef155180b7:image.png)
