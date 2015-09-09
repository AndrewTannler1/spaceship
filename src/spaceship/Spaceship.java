//Andrew Tannler
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = 420;
    static final int WINDOW_HEIGHT = 445;
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = new sound("starwars.wav");
    Image outerSpaceImage;

    Image rocketImage;
    Image rocketGIF;
    Image starImage;
    
    int starXSpeed;
    int numStars=1;
    int starXPos[];
    int starYPos[];
    
    Missile missiles[]=new Missile[Missile.num];
    Missile Active=new Missile();
    
    int rocketYSpeed;
    boolean rocketRight;
    boolean rocketIntersect[];
    int rocketXPos;
    int rocketYPos;
    
    int score;
    int scorededuction;
    int highscore;
    boolean gameover;
    
    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button

// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                    rocketYSpeed+=3;
                    if(rocketYSpeed>9)
                        rocketYSpeed=9;
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    rocketYSpeed-=3;
                    if(rocketYSpeed<-9)
                        rocketYSpeed=-9;
                } else if (e.VK_LEFT == e.getKeyCode()) {
                        starXSpeed+=5;
                        if(starXSpeed>=0)
                            starXSpeed=-1;
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                        starXSpeed-=5; 
                        if(starXSpeed<-15)
                            starXSpeed=-15;
                }else if (e.VK_SPACE == e.getKeyCode()) {
                    missiles[Missile.current].Active=true;
                    missiles[Missile.current].XPos=rocketXPos;
                    missiles[Missile.current].YPos=rocketYPos;
                    missiles[Missile.current].Right=rocketRight;
                    Missile.current++;
                    if (Missile.current >= Missile.num)
                        Missile.current = 0;
                }
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
        
                for(int index=0;index<numStars;index++){
                   
                drawStar(starImage,getX(starXPos[index]),getYNormal(starYPos[index]),0.0,1.0,1.0 );
                
                }
                if(starXSpeed==0)
                {
                if(rocketRight)
                drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
                else
                drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
                }
                  else
                {
                if(rocketRight)
                drawRocket(rocketGIF,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
                else
                drawRocket(rocketGIF,getX(rocketXPos),getYNormal(rocketYPos),0.0,-1.0,1.0 );
                }
                
                g.setColor(Color.blue);
                for(int index=0;index<missiles.length;index++)
                {
                    if(missiles[index].Active)
                        drawCircle(getX(missiles[index].XPos),getYNormal(missiles[index].YPos),.0,.3,.3);
                }
                g.setColor(Color.red);
                g.setFont(new Font("Times New Roman", Font.PLAIN, 18));
                g.drawString("Score:"+score,10,43);
                g.drawString("High Score:"+highscore,getWidth()-120,43);
                if(gameover)
                {
                    g.setFont(new Font("Times New Roman", Font.BOLD, 40));
                    g.setColor(Color.RED);
                    g.drawString("Game Over",getWidth()/4,getHeight()/2);
                }
                
            
                
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(Color.red);
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width;
        int height;
        if(starXSpeed==0)
        {
            width=rocketImage.getWidth(this);
            height=rocketImage.getHeight(this);
        }
        else
        {
        width=rocketGIF.getWidth(this);
        height=rocketGIF.getHeight(this);
        }
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
     public void drawStar(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = starImage.getWidth(this);
        int height = starImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.04;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        starXPos=new int[numStars];
        starYPos=new int[numStars];
        rocketIntersect=new boolean[numStars];

        
        for(int index=0;index<numStars;index++)
        {
        starXPos[index] = (int)(Math.random()*getWidth2());
        starYPos[index] =(int)(Math.random()*getHeight2());
        rocketIntersect[index]=false;
        }
        Missile.current=0;
        for(int index=0;index<missiles.length;index++)
        {
            missiles[index]=new Missile();
        }

        rocketYSpeed=0;
        starXSpeed=-1;
        rocketRight=true;
        
        score=0;
        scorededuction=0;
        gameover=false;
    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
                        readFile();
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.jpg");
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
            rocketGIF = Toolkit.getDefaultToolkit().getImage("./animRocket.GIF");
            starImage = Toolkit.getDefaultToolkit().getImage("./starAnim.GIF");
            reset();
            bgSound = new sound("./starwars.wav");

        }
        if(gameover)
            return;
        if(scorededuction==5)
           gameover=true;
         if(bgSound.donePlaying)
               bgSound = new sound("./starwars.wav");
         
                for(int index=0;index<numStars;index++)
                {
                    starXPos[index]+=starXSpeed;
                }
                rocketYPos+=rocketYSpeed;
                for(int index=0;index<numStars;index++)
                {
                    if(starXPos[index]<=0)
                    {
                        starYPos[index]= (int)(Math.random()*getHeight2());
                        starXPos[index]=getWidth2()-1;
                    }
                    if(starXPos[index]>=getWidth2())
                    {
                        starYPos[index]= (int)(Math.random()*getHeight2());
                        starXPos[index]=1; 
                    }
                    if(starXPos[index]-14<rocketXPos &&
                       starXPos[index]+14>rocketXPos &&
                       starYPos[index]-14<rocketYPos &&
                       starYPos[index]+14>rocketYPos &&
                       rocketIntersect[index]==false) 
                    {
                        bgSound=new sound("ouch.wav");
                        rocketIntersect[index]=true;
                        if(rocketIntersect[index])
                            scorededuction++;
                    }
               else if(starXPos[index]+14<rocketXPos ||
                       starXPos[index]-14>rocketXPos ||
                       starYPos[index]+14<rocketYPos ||
                       starYPos[index]-14>rocketYPos &&
                       rocketIntersect[index]) 
                       rocketIntersect[index]=false;
                      
                    
                }
                if(rocketYPos<=0)
                        rocketYPos=1;
                    if(rocketYPos>=getHeight2())
                        rocketYPos=getHeight2()-1;
                    if(starXSpeed<0)
                        rocketRight=true;
                    if(starXSpeed>0)
                        rocketRight=false;
                    
          
                     for(int index=0;index<missiles.length;index++)
                     {
                         if(missiles[index].Active)
                         {
                             if(missiles[index].Right)
                                 missiles[index].XPos+=3;
                             else
                                 missiles[index].XPos-=3;
                         }
                     }
             for (int i=0;i<missiles.length;i++)
        {
            for (int a=0;a<numStars;a++)
            {
            if (missiles[i].Active &&
                missiles[i].XPos+10 > starXPos[a] &&
                missiles[i].XPos-10 < starXPos[a] &&
                missiles[i].YPos+10 > starYPos[a] &&
                missiles[i].YPos-10 < starYPos[a])
                {
                     missiles[i].Active = false;
                     score+=5;
                     if(score>highscore)
                         highscore=score;
                     
                     if (rocketRight)
                     {
                         starYPos[a] = (int)(Math.random()*getHeight2());
                         starXPos[a] = (int)(Math.random()*getWidth2());
                     }
                     else
                     {
                         starYPos[a] = (int)(Math.random()*getHeight2());
                         starXPos[a] = (int)(Math.random()*getWidth2());

                     }
                }   
            }
        }
              for(int index=0;index<missiles.length;index++)
                {
                    if(missiles[index].XPos<=0)
                    {
                        missiles[index].Active=false;
                    }
                    if(missiles[index].XPos>=getWidth2())
                    {
                        missiles[index].Active=false;

                    }
                }
                         
    }

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
      public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStars = Integer.parseInt(numStarsString.trim());
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }

}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}


class Missile
{
    public static int current=0;
    public final static int num=10;
    
    public int XPos;
    public int YPos;
    public boolean Active;
    public boolean Right;
    Missile()
    {
        Active=false;
    }
    
}