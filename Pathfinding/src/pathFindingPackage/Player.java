package pathFindingPackage;

import java.awt.Color;
import java.awt.Graphics;

public class Player 
{
	static int size;
	private int x, y;
	private int width, height;
	private Node node;
	public Player(int x, int y) 
	{
		this.x = x;
		this.y = y;
		width = size;
		height = size;
	}
	
	public void draw(Graphics g)
	{
		g.setColor(Color.ORANGE);
		g.drawOval(x*size, y*size, width, height);
	}
	
	public static int getSize() {
		return size;
	}

	public static void setSize(int size) {
		Player.size = size;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Node getNode() {
		return node;
	}
	public void setNode(Node node) {
		this.node = node;
	}

}
