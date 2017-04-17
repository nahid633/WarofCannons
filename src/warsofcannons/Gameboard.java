package warsofcannons;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Gameboard extends JPanel implements Runnable {

    private ArrayList<String> names = new ArrayList<>();
    BufferedReader in;
    PrintWriter out;
    String name = "";
    MouseDetector mouse;
    Player player;
    Bullet[] bullets;
    Bullet[] enemyBullets;
    Enemy[] stationaryEnemy;
    Thread thread;
    int numberOfEnemyBullets = 15;
    int numberOfHearts = 10;
    int speedOfBullets = 6;
    int numberOfBullets = 25;
    int numberOfStationaryEnemies = 3;
    int timeForBullet = 1;
    int seconds = 90;
    int countEnemy = numberOfStationaryEnemies;
    double theta;
    HealthBar[] hearts;
    int [] lifeEnemy ;
    boolean resultPanel;
    boolean gameOver;
    JFrame frame = new JFrame("Wars of Cannons");
    BufferedImage backGround;
    BufferedImage opacity;
    volatile boolean play;
    boolean inited = false;
    private long diff, start = System.currentTimeMillis();

    public Gameboard() throws IOException {
        setSize(900, 500);
        frame.setSize(900, 500);
        frame.setResizable(false);
        setFocusable(true);
        frame.setLocationRelativeTo(null);
        setBackground(Color.BLACK);
        frame.add(this);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        File background = new File("C:\\Users\\Nahid\\Documents\\NetBeansProjects\\Cannons\\src\\warsofcannons\\images\\bga.png");
        try {
            backGround = (ImageIO.read(background))
                    .getSubimage(0, 0, 900, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
        frame.setVisible(true);
        @SuppressWarnings("resource")
        Socket socket = new Socket(getServerAddress(), 9001);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        lifeEnemy = new int [numberOfStationaryEnemies];
    }

    public void run() {
        try {
            while (true) {
                String line = in.readLine();
                if (line.startsWith("SUBMITNAME")) {
                    out.println(getName());
                } else if (line.startsWith("NameAccepted")) {
                    name = line.substring(12);
                } else if (line.startsWith("Wait")) {
                    System.out.println("Wait opponents");
                } else if (line.startsWith("Start")) {
                    System.out.println("Start");
                    numberOfStationaryEnemies = Integer.parseInt(line.substring(5)) - 1;
                    init();
                    out.println("salam");
                } else if (line.startsWith("KnowEach")) {
                    String arr = line.substring(8);
                    System.out.println("KNowEachOther:" + arr);
                    if (!arr.equals(name)) {
                        names.add(arr);
                    }
                    if (names.size() == numberOfStationaryEnemies) {
                        out.println();
                        System.out.println(names.get(0) + " " + names.get(1));
                    }
                } else if (line.startsWith("MESSAGE")) {
                    String message = line.substring(8);
                    System.out.println(">>Message is " + line);
                        out.println(names.get(0)+"/"+lifeEnemy[0] + "|" + names.get(1)+"/"+lifeEnemy[1]);

                    if(message.length()>9){
                    String []aa =message.split("\\|");
                    String [] enemy0=aa[0].split("\\/");
                    String [] enemy1=aa[1].split("\\/");
                   if(name.equals(enemy0[0])){
                       numberOfHearts=Integer.parseInt(enemy0[1]);
                       if(Integer.parseInt(enemy0[1])==0){
                           play=false;
                       }
                   } else if(name.equals(enemy1[0])){
                       numberOfHearts=Integer.parseInt(enemy1[1]);
                       if(Integer.parseInt(enemy1[1])==0){
                           play=false;
                   }
                    }
                   }
                    play();
                }

            }
        } catch (Exception ex) {
        }
    }

    private String getServerAddress() {
        return JOptionPane.showInputDialog(
                frame,
                "Enter IP Address of the server: ",
                "Welcome to the center",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    @Override
    public String getName() {
        return JOptionPane.showInputDialog(
                frame,
                "Choose a name",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE
        );
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        if (inited) {
            
            g2d.drawImage(backGround, 0, 0, null);
            theta = Math.atan2(mouse.getY() - (player.y + 40), mouse.getX() - (player.x + 50)) + Math.PI * 2;
            //  g2d.rotate(theta);
            //player.draw(g2d);
            //   g2d.drawImage(player.image, player.x+50 , player.y+40 , null);
            for (int i = 0; i < numberOfBullets; i++) {
                if (bullets[i].fired) {
                    g2d.drawImage(bullets[i].getImage(), bullets[i].x, bullets[i].y, null);
                }
            }
            for (int v = 0; v < numberOfStationaryEnemies; v++) {
                if (stationaryEnemy[v].isAlive) {
                    g2d.drawImage(stationaryEnemy[v].getImage(),
                            stationaryEnemy[v].x, stationaryEnemy[v].y,
                            null);
                }
//                for (int i = 0; i < numberOfEnemyBullets; i++) {
//                    if (enemyBullets[i].fired) {
//                        g2d.drawImage(enemyBullets[i].getImage(), enemyBullets[i].x,
//                                enemyBullets[i].y, null);
//                    }
//                }
            }            
            player.draw(g2d, theta);
            
            for (int i = 0; i < numberOfHearts; i++)
                g2d.drawImage(hearts[i].getImage(), hearts[i].x, 10, null);
        }
    }

    public void play() throws IOException {
        while (play) {
//            for (int i = 0; i < numberOfStationaryEnemies; i++) {
//                if (stationaryEnemy[i].isAlive
//                        && remainingHearts != 0
//                        && stationaryEnemy[i].getBounds().intersects(
//                                player.getBounds())) {
//                    stationaryEnemy[i].isAlive = false;
//                    countEnemy--;
//                    //hearts[--remainingHearts].setImage();
//                }
//            }
            for (int i = 0; i < numberOfBullets; i++) {
                if (bullets[i].fired) {
                    for (int v = 0; v < numberOfStationaryEnemies; v++) {
                        if (stationaryEnemy[v].isAlive) {
                            if (stationaryEnemy[v].getBounds().intersects(
                                    bullets[i].getBounds())) {
                                lifeEnemy[v]--;
                                if (lifeEnemy[v] != 0) {
                                     //hearts[--numberOfHearts].setImage();
                                    stationaryEnemy[v].isAlive = true;
                                      bullets[i].fired = false; 
                                }
                                else{
                                  stationaryEnemy[v].isAlive = false;
                                    countEnemy--;
                                     bullets[i].fired = false; 
                                }
                            }
                        }

                    }
                    if (bullets[i].x < -100 || bullets[i].x > 1180
                            || bullets[i].y < -100 || bullets[i].y > 760) {
                        bullets[i].fired = false;
                    } else {
                        bullets[i].y = bullets[i].y + (int) (speedOfBullets * bullets[i].sin);

                        bullets[i].x = bullets[i].x + (int) (speedOfBullets * bullets[i].cos);
                    }
                }
            }

//            for (int i = 0; i < numberOfEnemyBullets; i++) {
//                if (enemyBullets[i].fired) {
//                    if (player.getBounds().intersects(
//                            enemyBullets[i].getBounds())) {
//                        //   hearts[--remainingHearts].setImage();
//                        enemyBullets[i].fired = false;
//                    }
//
//                    if (enemyBullets[i].x < -100 || enemyBullets[i].x > 500
//                            || enemyBullets[i].y < -100 || enemyBullets[i].y > 900) {
//                        enemyBullets[i].fired = false;
//                    } else {
//                        enemyBullets[i].y = enemyBullets[i].y
//                                + (int) (speedOfBullets * enemyBullets[i].sin);
//
//                        enemyBullets[i].x = enemyBullets[i].x
//                                + (int) (speedOfBullets * enemyBullets[i].cos);
//                    }
//                }
//            }
//            if (timeForBullet % seconds == 0) {
//                //  int rand = (int) (Math.random() * numberOfStationaryEnemies);
//                for (int i = 0; i < numberOfStationaryEnemies; i++) {
//                    if (stationaryEnemy[i].isAlive) {
//                        addEnemyBullet(stationaryEnemy[i]);
//                    }
//                    //   System.out.println("salam");
//                }
//            }
//                if (remainingHearts == 0) {
//                    //result.won = false;
//                    play = false;
//                    gameOver = true;
//                    resultPanel = true;
//                }
//                if (countEnemy == 0) {
//                  //  result.won = true;
//                    play = false;
//                    gameOver = true;
//                }
            repaint();
            sleep(80);
            timeForBullet++;
            break;
        }

    }

    public void addBullet(int x) {
        if (x == 1) {
            for (int i = 0; i < numberOfBullets; i++) {
                if (!bullets[i].fired) {
                    bullets[i].x = player.x + 50;
                    bullets[i].y = player.y + 25;
                    bullets[i].theta = theta;
                    bullets[i].sin = Math.sin(bullets[i].theta - Math.PI * 2);
                    bullets[i].cos = Math.cos(bullets[i].theta - Math.PI * 2);
                    bullets[i].fired = true;
                    break;
                }
            }
        }
    }

//    public void addEnemyBullet(Enemy sEnemy) {
//        for (int i = 0; i < numberOfEnemyBullets; i++) {
//            if (!bullets[i].fired) {
//                // System.out.println("");
//                enemyBullets[i].x = sEnemy.x + 25;
//                enemyBullets[i].y = sEnemy.y + 25;
//                enemyBullets[i].theta = Math.atan2((player.y + 40) - (sEnemy.y), (player.x + 50) - (sEnemy.x));
//                enemyBullets[i].sin = Math.sin(enemyBullets[i].theta); //- Math.PI/ 2)
//                enemyBullets[i].cos = Math.cos(enemyBullets[i].theta);
//                enemyBullets[i].fired = true;
//                break;
//            }
//        }
//    }

    public void init() {
        mouse = new MouseDetector(this);    
       // remainingHearts = numberOfHearts;
        hearts = new HealthBar[numberOfHearts];
        for (int i = 0; i < numberOfHearts; i++) {
            hearts[i] = new HealthBar();
            hearts[i].x = 40 * i;
        }
        
        // System.out.println("x +: " + mouse.getX() + "   y   :" + mouse.getY());
        gameOver = true;

        player = new Player();

        bullets = new Bullet[numberOfBullets];
        for (int i = 0; i < numberOfBullets; i++) {
            bullets[i] = new Bullet(210, 200, 0);
        }
        enemyBullets = new Bullet[numberOfEnemyBullets];
        for (int i = 0; i < numberOfEnemyBullets; i++) {
            enemyBullets[i] = new Bullet(5);
            
        }
        stationaryEnemy = new Enemy[numberOfStationaryEnemies];
        //example
        for (int i = 0; i < numberOfStationaryEnemies; i++) {
            stationaryEnemy[i] = new Enemy((i + 1)*320,((i+1)*100)+200);
            lifeEnemy[i]=10;
        }
        play = true;
        inited = true;

    }

    public void sleep(int fps) {
        if (fps > 0) {
            diff = System.currentTimeMillis() - start;
            long targetDelay = 1000 / fps;
            if (diff < targetDelay) {
                try {
                    Thread.sleep(targetDelay - diff);
                } catch (InterruptedException e) {
                }
            }
            start = System.currentTimeMillis();
        }
    }

    public static void main(String[] args) throws Exception {
        Gameboard gameboard = new Gameboard();
        new Thread(gameboard).start();

    }
}
