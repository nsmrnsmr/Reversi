//reversi コンピュータと対戦
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class Stone
{
    public final static int black = 1;  //黒
    public final static int white = 2;  //白
    private int obverse;                //表面の色

    Stone()
    {
        obverse = 0;
    }
    
    //表裏の設定
    void setObverse(int color)
    {
        if(color == black || color == white) obverse = color;
        else System.out.println("黒か白でなければいけません。");
    }

    void paint(Graphics g, Point p, int rad)
    {
        if(obverse == black){
            g.setColor(Color.black);
            g.fillOval(p.x, p.y, rad, rad);
        }
        else if(obverse == white){
            g.setColor(Color.white);
            g.fillOval(p.x, p.y, rad, rad);
        }
    }
}

class Board
{
    private int WIDTH = 800;
    private int HEIGHT = 800;
    public int numGridBlack;
    public int numGridWhite;
    public int bCnt;
    public int wCnt;
    Stone[][] stone = new Stone[8][8];
    private int board[][] = new int[8][8];
    public Point direction[] = new Point[8];
    public int evalBlack[][] = new int[8][8];
    public int evalWhite[][] = new int[8][8];

    Board()
    {
        bCnt = 2; wCnt = 2;
        //初期位置の石を配置
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                stone[i][j] = new Stone();
                board[i][j] = 0;
            }
        }
        stone[3][3].setObverse(1); stone[4][3].setObverse(2);
        stone[3][4].setObverse(2); stone[4][4].setObverse(1);
        board[3][3] = 1; board[4][3] = 2; board[3][4] = 2; board[4][4] = 1;
        //方向ベクトル作成
        direction[0] = new Point(1,0);
        direction[1] = new Point(1,1);
        direction[2] = new Point(0,1);
        direction[3] = new Point(-1,1);
        direction[4] = new Point(-1,0);
        direction[5] = new Point(-1,-1);
        direction[6] = new Point(0,-1);
        direction[7] = new Point(1,-1);
    }
    ///////////////////////////////////////////////////////////////////////////
    //盤面の枠内か判定
    boolean isOnBoard(int x, int y)
    {
        if(x<0 || 7<x || y<0 || 7<y) return false;
        else return true;
    }

    //盤面(x,y)から方向dに向かって石を順番に取得
    ArrayList<Integer> getLine(int x, int y, Point d)
    {
        ArrayList<Integer> line = new ArrayList<Integer>();
        int cx = x + d.x;
        int cy = y + d.y;
        while(isOnBoard(cx, cy) && board[cx][cy]!=0){
            line.add(board[cx][cy]);
            cx += d.x;
            cy += d.y;
        }
        return line;
    }

    //盤面(x,y)に石を置いた場合に反転できる石(s)の数を数える
    int countReverseStone(int x, int y, int s)
    {
        //既に石が置かれている場合
        if(board[x][y] != 0) return -1;
        //8方向をチェック
        int cnt = 0;
        for(int d=0; d<8; d++){
            ArrayList<Integer> line = new ArrayList<Integer>();
            line = getLine(x, y, direction[d]);
            int n = 0;
            while(n<line.size() && line.get(n)!=s) n++;
            if(1<=n && n<line.size()) cnt += n;
        }
        return cnt;
    }

    //現在の盤面にある黒と白の石の数をそれぞれ数える
    void nowCntStone()
    {
        bCnt=0; wCnt=0;
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(board[i][j] == 1) bCnt++;
                if(board[i][j] == 2) wCnt++;
            }
        }
    }

    //石が升目(x,y)に置かれたときの石の反転を行う
    void reverseStone(int x, int y, int s)
    {
        for(int d=0; d<8; d++){
            int cnt = 0;
            ArrayList<Integer> line = new ArrayList<Integer>();
            line = getLine(x, y, direction[d]);
            int n = 0;
            while(n<line.size() && line.get(n)!=s) n++;
            if(1<=n && n<line.size()) cnt += n;

            if(cnt != 0){
                int i = 0;
                int cx = x + direction[d].x;
                int cy = y + direction[d].y;
                while(i<line.size() && line.get(i)!=s){
                    stone[cx][cy].setObverse(s);
                    board[cx][cy] = s;
                    cx += direction[d].x;
                    cy += direction[d].y;
                    i++;
                }
            }
        }
    }

    //升目(x,y)に石を配置
    void setStone(int x, int y, int s)
    {
        stone[x][y].setObverse(s);
        //System.out.println("("+x+","+y+")"+s);
        board[x][y] = s;
        //System.out.println(board[x][y]);
        reverseStone(x,y,s);
        evaluateBoard();
        //printEval();
        //printBoard();
    }
    ///////////////////////////////////////////////////////////////////////////
    //盤面を評価する
    void evaluateBoard()
    {
        numGridBlack = 0;
        numGridWhite = 0;
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                evalBlack[i][j] = countReverseStone(i, j, 1);
                if(evalBlack[i][j] > 0) numGridBlack++;
                evalWhite[i][j] = countReverseStone(i, j, 2);
                if(evalWhite[i][j] > 0) numGridWhite++;
            }
        }
    }

    //盤面をコンソールに表示する
    void printBoard()
    {
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                System.out.printf("%2d ", board[j][i]);
            }
            System.out.println("");
        }
    }

    //盤面の評価結果をコンソールに表示する
    void printEval()
    {
        System.out.println("Black(1):");
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                System.out.printf("%2d ", evalBlack[j][i]);
            }
            System.out.println("");
        }
        System.out.println("White(2):");
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                System.out.printf("%2d ", evalWhite[j][i]);
            }
            System.out.println("");
        }
        System.out.println("");
    }
    ///////////////////////////////////////////////////////////////////////////
    //盤面への描画
    void paint(Graphics g, int unit_size)
    {
        int wScale = WIDTH/10;
        int hScale = HEIGHT/10;
        //背景
        g.setColor(Color.black);
        g.fillRect(0,0,WIDTH, HEIGHT);
        //盤面
        g.setColor(new Color(0, 85, 0));
        g.fillRect(wScale, hScale, wScale*8, hScale*8);
        //横線
        g.setColor(Color.black);
        for(int i=0; i<9; i++){
            g.drawLine(wScale, hScale*(i+1), wScale*9, hScale*(i+1));
        }
        //縦線
        for(int i=0; i<9; i++){
            g.drawLine(wScale*(i+1), hScale, wScale*(i+1), hScale*9);
        }
        //目印
        for(int i=0; i<2; i++){
            for(int j=0; j<2; j++){
                g.fillRect(wScale*(3+4*j)-5, hScale*(3+4*i)-5, 10, 10);
            }
        }

        //それぞれの石の座標を算出し描画
        int rad = 64;
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                Point p = new Point(); p.x = wScale*(i+1)+8; p.y = hScale*(j+1)+8;
                stone[i][j].paint(g, p, rad);
            }
        }
    }
}

//プレイヤー管理
class Player
{
    public final static int typeHuman = 0;
    public final static int typeCPU = 1;
    private int color;                     //石の色を保存
    private int type;                      //人間かCPUを保存

    Player(int c, int t)
    {
        if(c == Stone.black || c == Stone.white) color = c;
        else{
            System.out.println("プレイヤーの石の色は黒か白でなければいけません:" + c);
            System.exit(0);
        }
        if(t == typeHuman || t == typeCPU) type =t;
        else{
            System.out.println("プレイヤーは人間かコンピュータでなければいけません:" + t);
            System.exit(0);
        }
    }

    int getColor(){ return color; }

    int getType(){ return type; }

    //配置できるマス目からランダムに選択
    Point tactics1(Board bd)
    {
        System.out.println("tac1");
        boolean t = false;
        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                if(color == Stone.black && bd.evalBlack[i][j] > 0){
                    t = true;
                    break;
                }
                if(color == Stone.white && bd.evalWhite[i][j] > 0){
                    t = true;
                    break;
                }
            }
        }
        Random rand = new Random();
        while(t){
            int i = rand.nextInt(8);
            int j = rand.nextInt(8);
            if(color == Stone.black){
                if(bd.evalBlack[i][j] > 0){
                    return (new Point(i, j));
                }
            }else if(color == Stone.white){
                if(bd.evalWhite[i][j] > 0){
                    return (new Point(i, j));
                }
            }
        }
        return (new Point(-1, -1));      //配置不可能な場合
    }

    //ひっくり返せる石が最も多いマス目を選択
    Point tactics2(Board bd)
    {
        System.out.println("tac2");
        int maxReverse = -1;
        Point p = new Point(-1, -1);
        if(color == Stone.black){
            for(int i=0; i<8; i++){
                for(int j=0; j<8; j++){
                    if(maxReverse < bd.evalBlack[i][j]){
                        maxReverse = bd.evalBlack[i][j];
                        p = new Point(i, j);
                    }
                }
            }
        }else if(color == Stone.white){
            for(int i=0; i<8; i++){
                for(int j=0; j<8; j++){
                    if(maxReverse < bd.evalWhite[i][j]){
                        maxReverse = bd.evalWhite[i][j];
                        p = new Point(i, j);
                    }
                }
            }
        }
        return p;
    }

    //ひっくり返せる石の数と盤面を考慮して選択
    Point tactics3(Board bd)
    {
        System.out.println("tac3");
        int maxReverse = -1;
        Point p = new Point(-1, -1);
        if(color == Stone.black){
            if(bd.evalBlack[0][0] > 0) return(new Point(0, 0));
            if(bd.evalBlack[7][0] > 0) return(new Point(7, 0));
            if(bd.evalBlack[0][7] > 0) return(new Point(0, 7));
            if(bd.evalBlack[7][7] > 0) return(new Point(7, 7));
            for(int i=0; i<8; i++){
                for(int j=0; j<8; j++){
                    if(maxReverse < bd.evalBlack[i][j]){
                        maxReverse = bd.evalBlack[i][j];
                        p = new Point(i, j);
                    }
                }
            }
        }else if(color == Stone.white){
            if(bd.evalWhite[0][0] > 0) return(new Point(0, 0));
            if(bd.evalWhite[7][0] > 0) return(new Point(7, 0));
            if(bd.evalWhite[0][7] > 0) return(new Point(0, 7));
            if(bd.evalWhite[7][7] > 0) return(new Point(7, 7));
            for(int i=0; i<8; i++){
                for(int j=0; j<8; j++){
                    if(maxReverse < bd.evalWhite[i][j]){
                        maxReverse = bd.evalWhite[i][j];
                        p = new Point(i, j);
                    }
                }
            }
        }
        return p;
    }

    Point nextMove(Board bd, Point p, String tac)
    {
        if(type == typeHuman) return p;
        else if(type == typeCPU){
            if(tac.equals("1")) return tactics1(bd);
            else if(tac.equals("2")) return tactics2(bd);
            else return tactics3(bd);
        }
        return (new Point(-1, -1));       //例外（通常は起こり得ない）
    }
}

public class Reversi_cpu extends JPanel
{
    public final static int UNIT_SIZE = 80; //升目の大きさ
    private int WIDTH = 800;                //ウィンドウの幅
    private int HEIGHT = 800;               //ウィンドウの高さ
    private int turn = 0;                  //手番の色
    
    private String strTurn;
    private String strStone;

    private Board board = new Board();
    private Player[] player = new Player[2];

    private static String tac = "1";

    public Reversi_cpu()
    {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        turn = Stone.black;
        strTurn = "黒の手番です";
        strStone = "[黒:"+board.bCnt+",白:"+board.wCnt+"]";
        addMouseListener(new MouseProc());
        player[0] = new Player(Stone.black, Player.typeHuman);
        player[1] = new Player(Stone.white, Player.typeCPU);
    }

    //画面描画
    public void paintComponent(Graphics g)
    {
        board.paint(g, UNIT_SIZE);
        g.setColor(Color.white);
        if(turn == 1) strTurn = "黒の手番です";
        else strTurn = "白の手番です";
        g.drawString(strTurn, 80, 50);
        strStone = "[黒:"+board.bCnt+",白:"+board.wCnt+"]";
        g.drawString(strStone, 80, 750);
    }

    //起動
    public static void main(String[] args)
    {
        tac = args[0];
        JFrame f = new JFrame();
        f.getContentPane().setLayout(new FlowLayout());
        f.getContentPane().add(new Reversi_cpu());
        f.pack();
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }

    //手番を交代する
    void changeTurn()
    {
        if(turn == 1) turn = Stone.white;
        else turn = Stone.black;
    }

    //ゲームの終了判定
    void endMessageDialog()
    {
        String num = "[黒:"+board.bCnt+",白:"+board.wCnt+"]";
        String str = "で引き分け";
        if(board.bCnt < board.wCnt) str = "で白の勝ち";
        else if(board.wCnt < board.bCnt) str = "で黒の勝ち";
        num += str;
        JOptionPane.showMessageDialog(this, num, "ゲーム終了", JOptionPane.INFORMATION_MESSAGE);
        //if(board.bCnt + board.wCnt == 64) System.exit(0);
        System.exit(0);
    }

    //手番の管理ダイアログ
    void MessageDialog(String str)
    {
        JOptionPane.showMessageDialog(this, str, "情報", JOptionPane.INFORMATION_MESSAGE);
    }

    class MouseProc extends MouseAdapter
    {

        public void mouseClicked(MouseEvent me)
        {
            int wScale = WIDTH/10;
            int hScale = HEIGHT/10;

            Point point = me.getPoint();
            int btn = me.getButton();
            int x = 0, y =0;                 //石の座標
            int s = 0;                       //石の色
            int cnt = 0;                     //ひっくり返せる石の数
            //System.out.println("("+point.x+","+point.y+")");
            if(point.x >= wScale && point.x <= 9*wScale && point.y > hScale && point.y <= 9*hScale){
                removeMouseListener(this);
                //プレイヤーの手番
                if(player[turn-1].getType() == Player.typeHuman){
                    if(player[turn-1].getColor() == Stone.black){
                        for(int i = 2; i<=9; i++){
                            if(point.x <= i*wScale){
                                x = i-2;
                                break;
                            }
                        }
                        for(int i = 2; i<=9; i++){
                            if(point.y <= i*hScale){
                                y = i-2;
                                break;
                            }
                        }
                        s = 1;
                        cnt = board.countReverseStone(x,y,s);
                        if(cnt > 0) board.setStone(x,y,s);
                    }
                    else if(player[turn-1].getColor() == Stone.white){
                        for(int i = 2; i<=9; i++){
                            if(point.x <= i*wScale){
                                x = i-2;
                                break;
                            }
                        }
                        for(int i = 2; i<=9; i++){
                            if(point.y <= i*hScale){
                                y = i-2;
                                break;
                            }
                        }
                        s = 2;
                        cnt = board.countReverseStone(x,y,s);
                        if(cnt > 0) board.setStone(x,y,s);
                    }
                    if(cnt > 0){
                        board.nowCntStone();
                        //changeTurn();
                        repaint();
                        if(board.numGridBlack == 0 && board.numGridWhite == 0) endMessageDialog();
                        else if(board.numGridBlack == 0 && turn == Stone.white || board.numGridWhite == 0 && turn == Stone.black) MessageDialog("あなたはパスです");
                        else changeTurn();
                    }
                    //対人の場合
                    if(player[turn-1].getType() == Player.typeHuman)
                    {
                        addMouseListener(this);
                    }
                }
                //コンピュータの手番
                if(player[turn-1].getType() == Player.typeCPU){
                    Thread th = new TacticsThread();
                    th.start();
                }
            }
        }
    }

    //コンピュータとの対戦用スレッド
    class TacticsThread extends Thread
    {
        public void run()
        {
            //System.out.println(tac);
            try{
                Thread.sleep(2000);  //二秒間待機
                Point nm = player[turn-1].nextMove(board, new Point(-1, -1), tac);
                if(nm.x == -1 && nm.y == -1){
                    MessageDialog("相手はパスです");
                }else{
                    board.setStone(nm.x, nm.y, player[turn-1].getColor());
                    board.nowCntStone();
                }
                repaint();
                addMouseListener(new MouseProc());
                //ゲーム終了確認
                if(board.numGridBlack == 0 && board.numGridWhite == 0) endMessageDialog();
                else if(board.numGridBlack == 0 && turn == Stone.white || board.numGridWhite == 0 && turn == Stone.black) MessageDialog("相手はパスです");
                else changeTurn();
            }catch(InterruptedException ie){
                ie.printStackTrace();
            }
        }
    }
}
