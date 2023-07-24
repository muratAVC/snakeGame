import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WITH=600;
    static final int SCREEN_HEIGHT=600;
    static final int UNIT_SIZE=10;
    static final int GAME_UNITS=(SCREEN_WITH*SCREEN_HEIGHT)/UNIT_SIZE;
    static final int DELAY=50;//yılanın ilerleme hızını belirler(ters orantılı)
    final int[] x =new int[GAME_UNITS];
    final int[] y =new int[GAME_UNITS];

    int bodyParts=6;//yılanın büyüklüğünü tutar
    int appleEaten;//yenen elmaların sayısını tutar
    int appleX;//yemin konumu
    int appleY;//yemin konumu
    char direction='R';//yılanın yönü (varsayılan yön sağa doğru)
    boolean running=false;//oyunun çalışık çaloşmadığını tutar
    Timer timer;
    Random random;

    ImageIcon appleImage;
    ImageIcon snakeImageR;
    ImageIcon snakeImageL;
    ImageIcon snakeImageU;
    ImageIcon snakeImageD;
    ImageIcon snakeBody;


    GamePanel(){
        random=new Random();
        this.setPreferredSize(new Dimension(SCREEN_WITH,SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());


            //image= ImageIO.read(new File("\u202AC:\\Users\\murat\\OneDrive\\resimler\\apple.png"));
            appleImage=new ImageIcon("D:\\Projects\\SnakeGame\\apple.png");
            snakeImageR=new ImageIcon("D:\\Projects\\SnakeGame\\snakeR.png");
            snakeImageL=new ImageIcon("D:\\Projects\\SnakeGame\\snakeL.png");
            snakeImageU=new ImageIcon("D:\\Projects\\SnakeGame\\snakeU.png");
            snakeImageD=new ImageIcon("D:\\Projects\\SnakeGame\\snakeD.png");
            snakeBody=new ImageIcon("D:\\Projects\\SnakeGame\\snakeBody.png");


        startGame();
    }

    public void startGame(){
        newApple();
        running=true;
        timer=new Timer(DELAY,this);
        timer.start();//yılanın sürekli hareketi için her an çalışmasını başlatır
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);

    }

    public void draw(Graphics g){
        g.setColor(new Color(47, 97, 204));
        g.setFont(new Font("Ink Free",Font.ITALIC,27));
        g.drawString("Skore = "+appleEaten,SCREEN_WITH/2-20,20);

        if (running) {
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {//ekranı karelere bölüyor
                g.setColor(new Color(17, 17, 17, 255));
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WITH, i * UNIT_SIZE);
            }

            //g.setColor(Color.red);//ekrana rastgele konuma bir yem yerleştiriyor
            //g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);//elmanın konumu appleX ve appleY ile belirliyor

            g.drawImage(appleImage.getImage(),appleX,appleY,UNIT_SIZE,UNIT_SIZE,null);


            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    //g.setColor(Color.GREEN);
                    //g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                    switch (direction){
                        case 'R':
                            g.drawImage(snakeImageR.getImage(),x[i],y[i],UNIT_SIZE,UNIT_SIZE,null);
                            break;
                        case 'L':
                            g.drawImage(snakeImageL.getImage(),x[i],y[i],UNIT_SIZE,UNIT_SIZE,null);
                            break;
                        case 'U':
                            g.drawImage(snakeImageU.getImage(),x[i],y[i],UNIT_SIZE,UNIT_SIZE,null);
                            break;
                        case 'D':
                            g.drawImage(snakeImageD.getImage(),x[i],y[i],UNIT_SIZE,UNIT_SIZE,null);
                            break;
                    };


                } else {
                    g.drawImage(snakeBody.getImage(),x[i],y[i],UNIT_SIZE,UNIT_SIZE,null);
                    //g.setColor(new Color(45, 180, 0));
                    //g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
        }else {
            gameOver(g);
        }

    }


    public void newApple(){

        appleX=random.nextInt((int)(SCREEN_WITH/UNIT_SIZE))*UNIT_SIZE;
        appleY=random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
        //rasthele olarak elmanın yerini tayin eder
        for (int i = 0; i < bodyParts; i++) {
            if (x[i]==appleX && y[i] == appleY ){
                newApple();//bu for döngüsü yeni elmanın yılanın altında olmasını engelliyor
            }
        }

    }

    public void move(){//yılanın yerini değiştirir
        for (int i = bodyParts; i >0 ; i--) {
            x[i]=x[i-1];
            y[i]=y[i-1];
        }
        switch (direction){
            case 'U':
                y[0]=y[0]-UNIT_SIZE;
                break;
            case 'D':
                y[0]=y[0]+UNIT_SIZE;
                break;
            case 'R':
                x[0]=x[0]+UNIT_SIZE;
                break;
            case 'L':
                x[0]=x[0]-UNIT_SIZE;
                break;
        }
    }

    public void checkApple(){//yılan elmayı yemiş ise yılanın boyunu ve skoru 1 artırır
        if (x[0]==appleX && y[0]==appleY){
            bodyParts++;
            appleEaten++;
            newApple();
        }
    }

    public void checkCollisions(){
        //yılanın kendi bedenine çarpmasını kontrol eder
        for (int i = bodyParts; i >0 ; i--) {
            if (x[0]==x[i] && y[0]==y[i]){
                running=false;
            }
        }
        //yılanın sol duvara çarpmasını kontrol eder
        if (x[0]<0){
            running=false;
        }
        //yılanın ust duvara çarpmasını konrtol eder
        if (y[0]<0){
            running=false;
        }
        //yılanın sağ duvara çarpmasını kontrol eder
        if (x[0]>SCREEN_WITH){
            running=false;
        }
        //yılanın alt duvara çarpmasını kontrol eder
        if (y[0]>SCREEN_HEIGHT){
            running=false;
        }
        //yılan bir yere çarpmışsa yılanı durdurur
        if (!running){
            timer.stop();
        }
    }

    public void gameOver(Graphics g){//oyunun bittiğini ve skoru ekrana yazar
        g.setColor(new Color(238, 30, 30));
        g.setFont(new Font("Ink Free",Font.BOLD,75));
        g.drawString("Game Over",SCREEN_WITH/6,SCREEN_HEIGHT/2);

        g.setColor(new Color(234, 21, 14));
        g.setFont(new Font("Ink Free",Font.ITALIC,27));
        g.drawString("Skore = "+appleEaten,SCREEN_WITH/2-20,20);
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
//timer nesnesi ile belirtilen zamana göre tekrarlı olarak çalıştırır
        if (running){
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter{

        @Override//klavyeden yılanın yönünü tayin eden yön tuşlarını algılar
        public void keyPressed(KeyEvent e){
            switch (e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if (direction!='R'){
                        direction='L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction!='L'){
                        direction='R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction!='D'){
                        direction='U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction!='U'){
                        direction='D';
                    }
                    break;
            }

        }
    }
}
