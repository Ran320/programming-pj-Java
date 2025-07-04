## 源代码文件名称及其主要功能

### 整体代码结构

```java
class Board{...}              //打印棋盘相关
class Player{...}             //创建玩家相关
class Game{...}               //游戏运行逻辑相关
public class Main{...}        //程序运行
```

### Board类主要功能

```java
//更新currentPlayer
public void resetCurrentPlayer(int currentPlayer)
//清屏
public void clearScreen(){...}
//初始化棋盘
public void initializeBoard(){...}
//打印棋盘
public void printBoard(){...}
//判断棋盘是否已满
public boolean isBoardFull(){...}
```

### Player类主要功能

```java
	//创建玩家
	private String name；
  private char piece;
  //获取名字
  public String getName(){...}
  //获取棋子
  public char getPiece(){...}
```

### Game类主要功能

```java
//完成下棋功能
public void Move(Player player){...}
```

## 运行结果截图

![image.png](attachment:5969d715-34bf-4cfa-a98a-77043577ed40:image.png)

![image.png](attachment:d1d19714-a7ad-4ebb-82e9-14928d5ba3da:image.png)

![image.png](attachment:74745d3b-6ebb-448f-b6ce-701751ebc0d9:image.png)

![image.png](attachment:791f5e04-7eb1-441a-9336-1b056d93ccad:image.png)
