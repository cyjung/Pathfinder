package pathFindingPackage;

import java.awt.Color;
import java.awt.Graphics;

public class TestNode extends Node
{
	private static Color brown = new Color(255,200,150);
	
	public TestNode(int x, int y)
	{
		super(x,y);
	}
	
	public void draw(Graphics g)
	{
		super.drawFilled(g, brown);
	}
	
	public String toString()
	{
		return super.toString() + " Test";
	}
}