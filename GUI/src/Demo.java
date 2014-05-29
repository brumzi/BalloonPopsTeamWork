import javax.swing.*;

import org.w3c.dom.css.Counter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;


public  class Demo extends JFrame implements MouseListener, ActionListener, Runnable
{
	private static final ImageObserver ImageObserver = null;

	private Point point = new Point(200, 300);
	
	// Size of the field
	private int sizeX = 10;
	private int sizeY = 10;
	private Balloon[][] field = new Balloon[sizeX][sizeY];
	private boolean [][] mask = new boolean[sizeX][sizeY];
	private boolean [][] lastDestroydBalloos = new boolean[sizeX][sizeY];
		

	
	// Balloon's start coordinate
	private int fieldX = 50;
	private int fieldY = 50;
	private Graphics graphics;
	private Graphics2D g2d;
	private Image dbg;
	
	//Backbuffer image for the animation
	private BufferedImage backbuffer;
	private Image sprite;
	
	// Coordinates of the mouse
	private int x;
	private int y;
	
	// game background image
	private Image background;
	
	// Flower
	private int flowerHeight = 260;
	
	// Leaves
	private String puprpleLeave;
	private int purpleDestroyed;
	private int purpleBalloons;
	private Image purpleFlower;
	private int puprleLeaveX =915;
	private int puprleLeaveY = 415;
	
	private String blueLeave;
	private int blueDestroyed;
	private int blueBalloons;
	private Image blueFlower;
	private int blueLeaveX = 1025;
	private int blueLeaveY = 415;
	
	private String greenLeave;
	private int greenDestroyed;
	private int greenBalloons;
	private Image greenFlower;
	private int greenLeaveX = 1225;
	private int greenLeaveY = 415;
	
	private String redLeave;
	private int redDestroyed;
	private int redBalloons;
	private Image redFlower;
	private int redLeaveX = 1120;
	private int redLeaveY = 415;
	
	// Score
	private int Score;
	private Image score;
	private int scoreX=1100;
	private int scoreY = 100;
	
	// Timer
	private Timer timer;
	private int counter = 60;
	private int delay = 0;
	private int timerX =850;
	private int timerY = 75;
	
	// Thread
	Thread gameloop;
	
	//animation variables
	Animator animator;
	
	//check if mouse cursor is on blank balloon
	boolean isBlank;
	
	public Demo()
	{
		super("Balloon Game");
		setSize(1000, 775);
		setVisible(true);
		setDefaultLookAndFeelDecorated(true);
		
		//create the back buffer for smooth graphics
		backbuffer = new BufferedImage(1000, 775,BufferedImage.TYPE_INT_RGB);
		g2d = backbuffer.createGraphics();
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		this.background = tk.getImage(getURL("gamebackground2.png"));
		this.purpleFlower = tk.getImage(getURL("purpleleave.png"));
		this.blueFlower = tk.getImage(getURL("blueleave.png"));
		this.redFlower = tk.getImage(getURL("redleave.png"));
		this.greenFlower = tk.getImage(getURL("leave.png"));
		this.sprite = tk.getImage(getURL("sprites.png"));
		
		setFocusable(true);
		addMouseListener(this);
		
		GenerateBalloonField();

		
		//timer
		this.timer = new Timer(1000, updateClockAction);
		timer.start();

		init();
	}	

	// Initizlize  animation
	private void init()
	{
		BufferedImageLoader loader = new BufferedImageLoader();
		BufferedImage spriteSheet = null;
		try {
			spriteSheet = loader.loadImage("sprites3.png");
		} catch (IOException e) {
			
		}
		
		SpriteSheet ss = new SpriteSheet(spriteSheet);
		ArrayList<BufferedImage> sprites = new ArrayList<BufferedImage>();
		sprites.add(ss.grabSprite(0,0, 74, 66));
		sprites.add(ss.grabSprite(74,0, 74, 66));
		sprites.add(ss.grabSprite(148,0, 74, 66));
		sprites.add(ss.grabSprite(222,0, 74, 66));
		sprites.add(ss.grabSprite(296,0, 74, 66));
		sprites.add(ss.grabSprite(370,0, 74, 66));
		sprites.add(ss.grabSprite(444,0, 74, 66));
		sprites.add(ss.grabSprite(518,0, 74, 66));
		sprites.add(ss.grabSprite(592,0, 74, 66));
		sprites.add(ss.grabSprite(666,0, 74, 66));
		sprites.add(ss.grabSprite(740,0, 74, 66));
		sprites.add(ss.grabSprite(814,0, 74, 66));
		sprites.add(ss.grabSprite(888,0, 74, 66));
		
		this.animator = new Animator(sprites);
		animator.setSpeed(100);
		animator.play();
	}
	
	// Double buffering
	public void paint(Graphics g)
	{
		this.dbg = createImage(getWidth(), getHeight());
		graphics = this.dbg.getGraphics();

		paintComponent(graphics);
		g.drawImage(dbg, 0,0,this);
	}
	
	// Main paint method
	public void paintComponent(Graphics g)
	{
        super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
						
		//draws the Balloons on screen
		g.drawImage(background,0,0,this);
		DrawField(g2d);
		
		// Setting up the score
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		Font textFont = new Font("Arial", Font.BOLD,26);
		g2d.setFont(textFont);
		g2d.drawString(Integer.toString(Score)+"\u2665", scoreX, scoreY);
		
		// Leaves
		g2d.drawImage(purpleFlower,puprleLeaveX,puprleLeaveY,this);
		g2d.drawImage(blueFlower,blueLeaveX,blueLeaveY,this);
		g2d.drawImage(greenFlower,greenLeaveX,greenLeaveY,this);
		g2d.drawImage(redFlower,redLeaveX,redLeaveY,this);
		
		// Timing
		if (this.counter > 0) {
			g2d.drawString(":"+Integer.toString(counter), timerX, timerY);
		}
		
		// Sets up places of animation
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				
				if (lastDestroydBalloos[i][j]== true ) {
					
					if (animator != null) {
						animator.update(System.currentTimeMillis());
						g2d.drawImage(animator.sprite,field[i][j].balloonX,field[i][j].balloonY,74,66,null);
					}
					repaint();
				}
			}
		}

		//
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				
				lastDestroydBalloos[i][j] = false;
			}
		}
	}
	
	// Setting up the path to background file (.png)
	private URL getURL(String filename) {
			URL url = null;
			try {
			url = this.getClass().getResource(filename);
			}
			catch (Exception e) { }
			return url;
		}
	
	// Populate the field with balloons
	private void GenerateBalloonField()
	{
		for (int i = 0; i < field.length; i++) {
			for (int j = 0; j < field[i].length; j++) {
				Balloon balloon = new Balloon();
				field[i][j] = balloon;
			}
		}
		
		CountNumberOfEachColor();
		
		// Timing
		//timer = new Timer(delay,  this);
		//timer.start();
	}
	
	// Draws balloons on screen based on fieldX and fieldY coords
	private void DrawField(Graphics2D g2d)
	{
		//Initial coordinates

		int y = this.fieldY;
		
		// New coordinates for each balloon
		int deltaX = field[0][0].image.getWidth(ImageObserver);
		int deltaY = field[0][0].image.getHeight(ImageObserver);
		
		for (int i = 0; i < field.length; i++) {
			int x = this.fieldX;
			for (int j = 0; j < field[i].length; j++) {
				
				field[i][j].SetX(x);
				field[i][j].SetY(y);
				//balloon
				if(field[i][j].color != "blank")
				{
					g2d.drawImage(field[i][j].image, x, y, this);
					
					int value = field[i][j].GetValue();
					String points = Integer.toString(value);
					g2d.setColor(Color.BLACK);
					g2d.drawString(points, x + deltaX/2 -5, y+deltaY/2);
				}
				x+=deltaX;
			}
			y+=deltaY;
		}
	}
	
	// Capture mouse click for destroying balloons
	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) { 
		 x = e.getX();
		 y = e.getY();
		
		 run();
		 
		// Calculating field borders
		int imageSizeX = field[0][0].image.getWidth(ImageObserver);
		int imageSizeY = field[0][0].image.getHeight(ImageObserver);
		
		int borderTop = imageSizeX*sizeX;
		int borderSide = imageSizeY*sizeY;
		
		// Is point within the play field
		if( x >=fieldX && x <=fieldX+borderTop && y>=fieldY && y <=fieldY+borderSide)
		{
			// Calculate which balloon point is on top ( balloonX and balloonY) coords
			int checkX = x - (x-fieldX)%imageSizeX;
			int checkY = y - (y-fieldY)%imageSizeY;
			
			int valX = 0;
			int valY = 0;
			String balColor = "";
			boolean balloonFound = false;
			
			// Remove balloons with above coordinates
			for (int i = 0; i < field.length; i++) {
				for (int j = 0; j < field[i].length; j++) {
					
					// Balloon's coordinates
					int balX = field[i][j].GetBalloonXCoord();
					int balY = field[i][j].GetBalloonYCoord();
					
					
					//Removing balloon
					if (balX == checkX && balY== checkY) {
						
						valX = i;
						valY = j;
						balColor = field[i][j].GetBalloonColor();
						
						if (field[i][j].GetBalloonColor() =="blank") {
							isBlank = true;
						}
						
						field[i][j].color="blank";
						mask[i][j]= true;
						balloonFound = true;
					}
					
					if (balloonFound) {
						break;
					}
				}
				
				if (balloonFound) {
					break;
				}
			}
			
			if (isBlank == false) {
				DestroyCells(valX, valY, balColor);
				
			}
			FallDown();
			repaint();
			
			// zeroes values for the next destruction
	    	this.redDestroyed = 0;
	    	this.blueDestroyed = 0;
	    	this.greenDestroyed = 0;
	    	this.purpleDestroyed = 0;
	    	isBlank = false;
		}
		
	}

	// Removing balloons
	private void DestroyRight(int x, int y, String val)
    {
        if (x < 0 || x>= this.sizeX  || y<0 || y>=this.sizeY)
        {
            return;
        }

        for (int j = y; j >= 0; j--)
        {
            if (this.field[x][j].color != val)
            {
            	CountDestroyedCellsOfCertainColor(val);
                return;
            }

            CountDestroyedCellsOfCertainColor(val);
            this.field[x][j].color = "blank";
            Score+=this.field[x][j].GetValue();
            this.mask[x][j] = true;
            this.lastDestroydBalloos[x][j] = true;
        }
    }

    private void DestroyLeft(int x, int y, String val)
    {
        if (x < 0 || x >= this.sizeX|| y < 0 || y >= this.sizeY)
        {
        	CountDestroyedCellsOfCertainColor(val);
            return;
        }

        for (int j = y; j < this.sizeY; j++)
        {
            if (this.field[x][j].color != val)
            {
                return;
            }

            CountDestroyedCellsOfCertainColor(val);
            this.field[x][j].color = "blank";
            Score+=this.field[x][j].GetValue();
            this.mask[x][j] = true;
            this.lastDestroydBalloos[x][j] = true;
        }
    }

    
    private void DestroyUp(int x, int y, String val)
    {
        if (x < 0 || x >= this.sizeX || y < 0 || y >= this.sizeY)
        {
        	CountDestroyedCellsOfCertainColor(val);
            return;
        }

        for (int i = x; i >=0; i--)
        {
            if (this.field[i][y].color != val)
            {
                return;
            }

            CountDestroyedCellsOfCertainColor(val);
            this.field[i][y].color = "blank";
            Score+=this.field[i][y].GetValue();
           
            this.mask[i][y] = true;
            this.lastDestroydBalloos[i][y] = true;
        }
    }
    

    private void DestroyDown(int x, int y, String val)
    {
        if (x < 0 || x >= this.sizeX || y < 0 || y >= this.sizeY)
        {
            return;
        }

        for (int i = x; i < this.sizeX; i++)
        {
            
            if (this.field[i][y].color != val)
            {
            	CountDestroyedCellsOfCertainColor(val);
                return;
            }

            CountDestroyedCellsOfCertainColor(val);
            this.field[i][y].color = "blank";
            Score+=this.field[i][y].GetValue();
            this.mask[i][y] = true;
            
            this.lastDestroydBalloos[i][y] = true;

        }            
    }

    private void DestroyCells(int x, int y, String val)
    {
    	DestroyDown(x+1, y, val);
    	DestroyUp(x-1, y, val);
    	DestroyLeft(x, y+1, val);
    	DestroyRight(x, y-1, val);

    	Score+=this.field[x][y].GetValue();
    	this.lastDestroydBalloos[x][y] = true;
    	
    	// Moves each leave
    	MoveLeaves();
    	
    	
    }

    private void DestroyAjacentToEmptyCellsLeftOrRight(String val)
    {
        for (int i = 0; i < this.sizeX; i++)
        {
            for (int j = 0; j < this.sizeY; j++)
            {
                if (this.field[i][j].color =="blank")
                {
                    DestroyRight(i,j+1,val);
                    DestroyLeft(i, j -1, val);
                }
            }
        }
    }

    private void DestroyAjacentCellsUpOrDown(String val)
    {
        for (int i = 0; i < this.sizeX; i++)
        {
            for (int j = 0; j < this.sizeY; j++)
            {
                if (this.field[i][j].color == "blank")
                {
                    DestroyUp(i-1, j , val);
                    DestroyDown(i+1, j, val);
                }
            }
        }
    }
    
    public void FallDown()
    {
        for (int j = 0; j < this.sizeY; j++)
        {
            for (int i = 0; i < this.sizeX; i++)
            {
                if (this.field[i][j].color =="blank")
                {
                    for (int k = i; k >0; k--)
                    {
                        Balloon temp = this.field[k][j];
                        this.field[k][j] =this.field[k-1][j];
                        this.field[k - 1][j] = temp;
                    }
                }
            }
        }
    }
   
    private void CountNumberOfEachColor()
    {
    	
    	for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				
				String color = field[i][j].GetBalloonColor();
				switch (color) {
				case "red":
					this.redBalloons++;
					break;

				case "green":
					this.greenBalloons++;
					break;
				
				case "blue":
					this.blueBalloons++;
					break;
				case "purple":
					this.purpleBalloons++;
					break;
				}
			}
		}
    	
    	
    }
    
    private void CountDestroyedCellsOfCertainColor(String color)
    {
    	switch (color) {
		case "red":
			this.redDestroyed++;
			break;

		case "green":
			this.greenDestroyed++;
			break;
		
		case "blue":
			this.blueDestroyed++;
			break;
		case "purple":
			this.purpleDestroyed++;
			break;
		}
    	

    }
    
    private void MoveLeaves()
    {
    	//purple
    	if (purpleDestroyed != 0) {
    		int deltaPurple = flowerHeight/purpleBalloons;
        	this.puprleLeaveY +=(purpleDestroyed*deltaPurple)/1.5;
        	
        	// Reached bottom of the flower
        	if (this.puprleLeaveY > 690) {
				this.puprleLeaveY = 690;
			}
		}
    	
    	//red
    	if (redDestroyed !=0) {
    		int deltaRed= flowerHeight/redBalloons;
        	this.redLeaveY +=(redDestroyed*deltaRed)/1.5;
        	
        	// Reached bottom of the flower
        	if (this.redLeaveY > 690) {
				this.redLeaveY = 690;
			}
		}
    	
    	//blue
    	if (blueDestroyed !=0) {
    		int deltaBlue= flowerHeight/blueBalloons;
        	this.blueLeaveY +=(blueDestroyed*deltaBlue)/1.5;
        	
        	// Reached bottom of the flower
        	if (this.blueLeaveY > 690) {
				this.blueLeaveY = 690;
			}
		}
    	
    	//red
    	if (greenDestroyed != 0) {
    		int deltaGreen= flowerHeight/greenBalloons;
        	this.greenLeaveY +=(greenDestroyed*deltaGreen)/1.5;
        	
        	// Reached bottom of the flower
        	if (this.greenLeaveY > 690) {
				this.greenLeaveY = 690;
			}
		}
    }
    
	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) { }
	@Override
	public void mouseExited(java.awt.event.MouseEvent e) { }
	@Override
	public void mousePressed(java.awt.event.MouseEvent e) { }
	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) { }
	
	public static void main(String[] args)
	{
		new Demo();
	}

	
	ActionListener updateClockAction  = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			counter--;
			repaint();
		}
	};

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void update(){
		init();
		if (animator != null) {
			animator.update(System.currentTimeMillis());
			g2d.drawImage(animator.sprite,700,650,74,66,null);
		}
		//init();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Thread t = Thread.currentThread();
		while (t == gameloop) {
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
			update();
		}
	}


		
}
	
	


	

