package pathFindingPackage;

import java.awt.Color;
import java.awt.Graphics;

public class EndNode extends Node
{
	
	public EndNode(int x, int y)
	{
		super(x,y);
	}
	
	public void draw(Graphics g)
	{
		super.drawBold(g, Color.RED);
	}
	
	public String toString()
	{
		return super.toString() + " End";
	}
}
