import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Random;


public class Balloon {

	// Fields
	Image image;
	int balloonX;
	int balloonY;
	String color;
	int value;
	
	//Access methods to fields
	public Image GetBalloonImage(){ return this.image;};
	public int GetBalloonXCoord(){ return this.balloonX;};
	public int GetBalloonYCoord(){ return this.balloonY;};
	public String GetBalloonColor(){ return this.color;};
	public void SetBalloonColor( String color){ this.color = color;};
	public void SetX(int x){this.balloonX =x;};
	public void SetY(int y){this.balloonY =y;};
	public int GetValue(){return this.value;};
	
	public Balloon()
	{
		GenerateBalloon();
	}
	
	// Initialise Baloon object with a random color
	private void GenerateBalloon()
	{
		Random rand = new Random();
		int color = rand.nextInt(4);
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		
		// Setting the image color
		switch (color) {
		case 0:  this.image = tk.getImage(getURL("red.png"));  break;
		case 1:  this.image = tk.getImage(getURL("green.png"));  break;
		case 2:  this.image = tk.getImage(getURL("blue.png"));  break;
		case 3:  this.image = tk.getImage(getURL("lilavo.png"));  break;
		}
		
		if (color ==0) {
			this.color = "red";
		}
		if (color ==1) {
			this.color = "green";
		}
		if (color ==2) {
			this.color = "blue";
		}
		if (color ==3) {
			this.color = "purple";
		}
		
		// Setting up the value
		this.value = rand.nextInt(10);
	}
	
	// Settin up the path to color file (.png)
	private URL getURL(String filename) {
		URL url = null;
		try {
		url = this.getClass().getResource(filename);
		}
		catch (Exception e) { }
		return url;
		}
}
