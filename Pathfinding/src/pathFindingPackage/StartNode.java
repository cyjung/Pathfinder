package pathFindingPackage;

import java.awt.Color;
import java.awt.Graphics;

public class StartNode extends Node
{
	
	public StartNode(int x, int y)
	{
		super(x,y);
	}
	
	public void draw(Graphics g)
	{
		super.drawBold(g, Color.GREEN);
	}
	
	public String toString()
	{
		return super.toString() + " Start";
	}
}
