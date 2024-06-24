import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

public class Flybirds extends JPanel implements ActionListener,KeyListener {
   int bwidth=300;
   int bheight=600;

   //Images
   Image backgroundImg;
   Image birdImg;
   Image topPipeImg;
   Image bottomPipeImg;
   
   //Bird
   int birdX=bwidth/8;
   int birdY=bheight/2;
   int birdwidth=34;
   int birdheight=24;
   

   


   class Bird{
    int x=birdX;
    int y=birdY;
    int width=birdwidth;
    int height=birdheight;
    Image img;
    Bird(Image img)
    {
        this.img=img;
    }
   }
   //pipes
   int pipeX=bwidth;
   int pipeY=0;
   int pipewidth=64; //scaled by 1/6
   int pipeheight=512;
 
   class Pipe{
    int x=pipeX;
    int y=pipeY;
    int width=pipewidth;
    int height=pipeheight;
    Image img;
    boolean passed=false;
    Pipe(Image img)
    {
        this.img=img;
    }
   }
   



   //game log
   Bird bird;
   int velocityX=-4 ; //move pipes to the left speed(simulates the bird moving right)
   int velocityY=0; //movebird up and down speed
   int gravity=1;

   ArrayList<Pipe> pipes;
  Random random=new Random(); 

   Timer gameLoop;
  int delay=1000/60;
  Timer placePipesTimer;

  boolean gameOver=false;
  double score=0;
   Flybirds()
   {
    setPreferredSize(new Dimension(bwidth,bheight));
   //setBackground(Color.blue);
   setFocusable(true);
   addKeyListener(this);

    //load images
    backgroundImg=new ImageIcon(getClass().getResource("./flybg.png")).getImage();
    birdImg=new ImageIcon(getClass().getResource("./flybird.png")).getImage();
    topPipeImg=new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
    bottomPipeImg=new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
   //bird
    bird=new Bird(birdImg);
    pipes=new ArrayList<Pipe>();

    //pipes timer
    placePipesTimer=new Timer(1500,new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            placePipes();
        }
    });
    placePipesTimer.start();

    //game time
     
    gameLoop=new Timer(delay, this); //1000/60=16.6sec 
    gameLoop.start();
    

   }
   public void placePipes()
   {
    //(0-1)*pipeheight/2->0-256
    //128
    //0-128-(0-256)-->pipeheight/4->3/4 pipeheight
    int randomPipeY=(int)(pipeY-pipeheight/4-Math.random()*(pipeheight/2));
    int openingSpace=bheight/4;

  
    Pipe topPipe=new Pipe(topPipeImg);
    topPipe.y=randomPipeY;
    pipes.add(topPipe);

    Pipe bottomPipe=new Pipe(bottomPipeImg);
    bottomPipe.y=topPipe.y+pipeheight+openingSpace;
    pipes.add(bottomPipe);
   }

   public void paintComponent(Graphics g)
   {
    super. paintComponent(g);
    draw(g);
   }

public void draw(Graphics g)
{
   
    //background
    g.drawImage(backgroundImg, 0,0,bwidth,bheight,null);
    //bird
    g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

    //pipes
    for(int i=0;i<pipes.size();i++)
    {
        Pipe pipe=pipes.get(i);
        g.drawImage(pipe.img,pipe.x,pipe.y,pipe.width,pipe.height,null);
    }

    //score
    g.setColor(Color.white);
    g.setFont(new Font("Arial", Font.PLAIN, 32));
    if(gameOver)
    {
        g.drawString("Game Over:"+String.valueOf((int) score),10,35);
    }
    else{
        g.drawString(String.valueOf((int) score),10,35);
    }
}
public void move()
{
    //bird
    velocityY+=gravity;
    bird.y+=velocityY;
    bird.y=Math.max(bird.y,0);
    //pipes
    for(int i=0;i<pipes.size();i++)
    {
        Pipe pipe=pipes.get(i);
        pipe.x+=velocityX;
        if(!pipe.passed && bird.x > pipe.x + pipe.width)
        {
            pipe.passed=true;
            score+=0.5; //2pipes 0.5*2=1
        }

        if(collision(bird, pipe))
        {
            gameOver=true;
        }
    }
    if(bird.y>bheight)
    {
        gameOver=true;
    }
}
public boolean collision(Bird a,Pipe b)
{
    return a.x < b.x + b.width && //a's top left corner doesn't reach b's top right corner
           a.x + a.width > b.x && //a's top right corner passes  b's top left corner
           a.y < b.y + b.height && //a's top left corner doesn't reach b's bottom right corner
           a.y + a.height > b.y;   //a's bottom left corner passes  b's top left corner
    
}
@Override
public void actionPerformed(ActionEvent e) {
    move();
   repaint();
   if(gameOver)
   {
    placePipesTimer.stop();
    gameLoop.stop();
   }
}

@Override
public void keyPressed(KeyEvent e) {
   if(e.getKeyCode()==KeyEvent.VK_SPACE)
   {
    velocityY=-9;
    if(gameOver)
    {
        //reset the game by resetting the conditions
        bird.y=bird.y;
        velocityY=0;
        pipes.clear();
        score=0;
        gameOver=false;
        gameLoop.start();
        placePipesTimer.start();
    }
   }
}
@Override
public void keyTyped(KeyEvent e) {
   
}
@Override
public void keyReleased(KeyEvent e) {
   
}
}
