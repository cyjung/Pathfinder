package pathFindingPackage;

import java.awt.Color;
import java.awt.Graphics;

public class WallNode extends Node
{
	
	public WallNode(int x, int y)
	{
		super(x,y);
	}
	
	public void draw(Graphics g)
	{
		super.drawFilled(g, Color.BLUE);
	}
	
	public String toString()
	{
		return super.toString() + " Wall";
	}
}