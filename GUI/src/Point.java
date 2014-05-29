
public class Point {

	int pointX;
    int pointY;
	
	public int GetPointXCoord(){ return this.pointX;};
	public int GetPointYCoord(){ return this.pointY;};
	
	// Initialise point object and setting up its coordinates
	public Point(int x, int y)
	{
		this.pointX = x;
		this.pointY = y;
	}
}
