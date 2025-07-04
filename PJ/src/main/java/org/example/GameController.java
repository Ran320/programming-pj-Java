package org.example;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.game.*;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameController {


    @FXML private Button showState;
    @FXML private Button quitButton;
    @FXML private Label boardIndex;
    @FXML private Label player1State;
    @FXML private Label player2State;
    @FXML private Label gomokuRound;
    @FXML private Button button;
    @FXML private HBox hbox;
    @FXML private AnchorPane rootPane;
    @FXML private Label txtArea;
    @FXML private VBox column1;
    @FXML private VBox column2;
    @FXML private VBox column3;


    @FXML private GridPane boardGrid;
    @FXML private ListView<String> gameListView;

    private GameList gameList;
    private Game currentGame;
    private Player currentPlayer;
    Player player1,player2;
    private List<Button> buttons = new ArrayList<>();

    @FXML
    public void initialize(){
        initializeState();
        updateGameList();
        switchGame();
        adjustSize();
        column1Button();
        showingState();
        quit();
    }

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

    //游戏相关显示
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


    //游戏游玩-单击格子下棋
    public void handleCellClick(int row, int col){
        if(!currentGame.getIsGameEnd()){
            int otherPlayer = gameList.getCurrentPlayer()==1?2:1;
            Game game = gameList.getCurrentGame();
            //使用炸弹
            if(currentGame.isBombMode()){
                game.handleBombCommand(currentPlayer,row,col);
                if(!currentGame.isValidBomb){           //炸弹使用不合法
                    currentGame.setBombMode(false);
                }
            }
            //正常落子
            else{
                game.handleMove(currentPlayer,row,col);
            }
            //如果是合法落子或炸弹，则换玩家操作
            if(game.getIsValidMove()||game.isValidBomb){
                gameList.setCurrentPlayer(otherPlayer);
                currentPlayer = gameList.getCurrentPlayer()==1?player1:player2;
                currentGame.isValidMove = false;
            }
            updateBoardDisplay();
        }
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
                currentGame.handleInput(input, currentPlayer);

                if(currentGame.getIsValidMove()||currentGame.getIsValidPass()||currentGame.isValidBomb){
                    gameList.setCurrentPlayer(otherPlayer);
                    currentPlayer = gameList.getCurrentPlayer()==1?player1:player2;
                }

                // 确保在 JavaFX Application Thread 中更新棋盘显示
                Platform.runLater(() -> {
                   // System.out.println("updateBoardDisplay called inside Platform.runLater");
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

    // 更新棋盘显示
    public void updateBoardDisplay() {
        column1State();//更新第一列游戏状态
        //System.out.println("updateboard");
        for (int i = 1; i < currentGame.getSize()+1; i++) {
            for (int j = 1; j < currentGame.getSize()+1; j++) {
                Button button = (Button) boardGrid.getChildren().get(i * (currentGame.getSize()+1) + j);
                final int row = i-1;
                final int col = j-1;
                char piece = currentGame.getPiece(i-1, j-1,currentPlayer);
                if (piece == Board.BLACK) {
                    button.setText("●");
                } else if (piece == Board.WHITE) {
                    button.setText("○");
                } else if (piece == Board.ABLE) {
                    button.setText("+");
                }else if (piece == Board.BARRIER) {
                    button.setText("#");
                    button.setDisable(true);
                } else if (piece == Board.BOMB) {
                    button.setText("*");
                    button.setDisable(true);
                } else {
                    button.setText(" ");
                }
                button.setOnAction(event -> handleCellClick(row, col));

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

    //quit
    public void quit(){
        quitButton.setOnAction(event -> {
            gameList.saveGameState();
            javafx.application.Platform.exit();
        });
    }

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

    //更新Gamelist
    public void updateGameList(){
        ObservableList<String> gameItems = FXCollections.observableArrayList();
        for (Game game : gameList.getGames()) {
            gameItems.add(game.getIndex()+1 + " " + game.getMode());
        }
        gameListView.setItems(gameItems);
    }

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

    //添加peace
    @FXML
    private void newPeaceGame() {
        gameList.addNewGame("peace");
        updateGameList();
    }

    //添加reversi
    @FXML
    private void newReversiGame() {
        gameList.addNewGame("reversi");
        updateGameList();
    }

    //添加gomoku
    @FXML
    private void newGomokuGame() {
        gameList.addNewGame("gomoku");
        updateGameList();
    }

}
