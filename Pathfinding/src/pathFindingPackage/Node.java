package pathFindingPackage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public abstract class Node implements Drawable
{
	static int size;
	int x, y;
	int width, height;
	Rectangle rect;
	Node parent;
	int score, gScore, hScore;
	boolean found;
	
	private static Color gold = new Color(255,215,0);
	
	public Node(int x, int y)
	{
		this.x = x;
		this.y = y;
		width = size;
		height = size;
		rect = new Rectangle(x*size, y*size, width, height);
		found = false;
	}

	public boolean intersects(Node n)
	{
		if (n != null && rect.intersects(n.getRect()))
			return true;
		
		return false;
	}
	public boolean intersects(ArrayList<Node> a)
	{
		for (int i = 0; i < a.size(); i++)
		{
			if (rect.intersects(a.get(i).getRect()))
				return true;
		}		
		return false;
	}

	public void draw(Graphics g, Color type)
	{
		g.setColor(type);
		if (found)
			g.setColor(gold);
		g.drawRect(x*size, y*size, width, height);
	}

	public void drawBold(Graphics g, Color type)
	{
		g.setColor(type);
		if (found)
			g.setColor(gold);
		for (int i = 1; i < size/5+1; i++)
		g.drawRect(x*size+i, y*size+i, width-2*i, height-2*i);
		for (int i = 4*size/10+1; i < size/2+1; i++)
		g.drawRect(x*size+i, y*size+i, width-2*i, height-2*i);
	}

	public void drawFilled(Graphics g, Color type)
	{
		g.setColor(type);
		if (found)
			g.setColor(gold);	
		g.fillRect(x*size+1, y*size+1, width-2, height-2);
	}

	public void drawDetailed(Graphics g, Color type)
	{
		g.setColor(type);
		if (found)
			g.setColor(gold);	
		g.fillRect(x*size+1, y*size+1, width-2, height-2);
		
		g.setColor(Color.BLACK);

		g.setFont(new Font("TimesNewRoman", Font.BOLD, size/5));
		g.drawString(Integer.toString(gScore), x*size+size/10, y*size+size/5);
		g.drawString(Integer.toString(hScore), x*size+(int)(size/1.8), y*size+size/5);
		g.drawString(Integer.toString(score), x*size+3*size/10, y*size+size/2);
		
//		g.setFont(new Font("TimesNewRoman", Font.BOLD, size/5));
//		g.drawString(Integer.toString(gScore), x*size+size/10-1, y*size+size/5-1);
//		g.drawString(Integer.toString(hScore), x*size+(int)(size/1.8)-1, y*size+size/5-1);
//		g.drawString(Integer.toString(score), x*size+3*size/10-1, y*size+size/2-1);
//
//		g.drawString(Integer.toString(gScore), x*size+size/10+1, y*size+size/5+1);
//		g.drawString(Integer.toString(hScore), x*size+(int)(size/1.8)+1, y*size+size/5+1);
//		g.drawString(Integer.toString(score), x*size+3*size/10+1, y*size+size/2+1);
//		
//		g.setColor(Color.WHITE);
//		g.setFont(new Font("TimesNewRoman", Font.BOLD, size/5));
//		g.drawString(Integer.toString(gScore), x*size+size/10, y*size+size/5);
//		g.drawString(Integer.toString(hScore), x*size+(int)(size/1.8), y*size+size/5);
//		g.drawString(Integer.toString(score), x*size+3*size/10, y*size+size/2);
	}

	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}
	
	public static void setSize(int theSize)
	{
		size =	theSize;	
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

	public Rectangle getRect() {
		return rect;
	}

	public void setRect(Rectangle rect) {
		this.rect = rect;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getGScore() {
		return gScore;
	}

	public void setGScore(int gScore) {
		this.gScore = gScore;
	}

	public int getHScore() {
		return hScore;
	}

	public void setHScore(int hScore) {
		this.hScore = hScore;
	}

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}
	
}
