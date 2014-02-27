package pathFindingPackage;

import java.awt.Color;
import java.awt.Graphics;

public class EmptyNode extends Node
{
	private static Color green = new Color(20,50,20);
	
	public EmptyNode(int x, int y)
	{
		super(x,y);
	}
	
	public void draw(Graphics g)
	{
		super.draw(g, green);
	}

	public String toString()
	{
		return super.toString() + " Empty";
	}
}