package pathFindingPackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Pathfinder extends JPanel implements MouseListener, MouseMotionListener, KeyListener
{
	private final int NODESIZE = 1800/70;
	private final int SCREENWIDTH = 1800, SCREENHEIGHT = 1000;
	//private final int FINDSPEED = 1;
	private MainThread thread = new MainThread();
	private Image dbImage;
	private Graphics dbg;
	
	private boolean cutCorners;
	
	private final int goKey = KeyEvent.VK_SPACE, resetKey = KeyEvent.VK_BACK_SPACE, startKey = KeyEvent.VK_Z, endKey = KeyEvent.VK_X, wallKey = KeyEvent.VK_C, removeKey = KeyEvent.VK_R;
	private final int playerKey = KeyEvent.VK_V, upKey = KeyEvent.VK_UP, downKey = KeyEvent.VK_DOWN, leftKey = KeyEvent.VK_LEFT, rightKey = KeyEvent.VK_RIGHT;
	private final int showPathKey = KeyEvent.VK_SHIFT;
	private boolean goOn, resetOn, startOn, endOn, wallOn, removeOn;
	private boolean playerOn, playerMoving, upOn, downOn, leftOn, rightOn;
	private boolean showPath;
	private boolean pause, freeze;
	private final int brush1 = KeyEvent.VK_1, brush2 = KeyEvent.VK_2, brush3 = KeyEvent.VK_3, brush4 = KeyEvent.VK_4, brush5 = KeyEvent.VK_5;
	private int brushSize;

	//private NodeList nodes = new NodeList();
	private NodeList empty = new NodeList();
	private NodeList walls = new NodeList();
	private NodeList open = new NodeList();
	private NodeList closed = new NodeList();
	
	private StartNode start;
	private EndNode end;
	
	private int lowScore;
	private Node lowNode;
	private Node nextNode;

	private int chaseSpeed;
	private int chaseTimer;
	
	private Player player;

	public Pathfinder()
	{
		thread.start();
	    //-----------------------------------------------------------
	    setFocusable(true);
	    addKeyListener(this);
	    addMouseMotionListener(this);
	    addMouseListener(this);
	    setBackground(Color.black);
	    setPreferredSize(new Dimension(SCREENWIDTH, SCREENHEIGHT));
	    //------------------------------------------------------------ 
	    
	    cutCorners = false; //does the path allow for diagonal movements?
	    
	    goOn = false;
	    startOn = false;
	    endOn = false;
	    wallOn = false;
	    removeOn = false;
	    pause = false;
	    freeze = false;
	    
	    brushSize = 1;
	    
	    chaseSpeed = 150;
	    chaseTimer = 0;
	    
	    showPath = false;

	    Node.setSize(NODESIZE);
	    Player.setSize(NODESIZE);
	    
	    for (int column = 0; column < SCREENHEIGHT/NODESIZE; column++)
	    {
		    for (int row = 0; row < SCREENWIDTH/NODESIZE; row++)
		    {
		    	EmptyNode blank = new EmptyNode(row, column);
		    	//nodes.add(blank);
		    	empty.add(blank);
		    }
	    }
	    //start = new StartNode(0,0);
	    //end = new EndNode(SCREENWIDTH/NODESIZE-1, SCREENHEIGHT/NODESIZE-1);
	    
	}
	
	public class MainThread extends Thread
	{
		public void run()
		{     
			try
			{
				while(true)
				{
					Thread.sleep(50);
					if (end != null && !pause && !freeze)
					{
						if (playerMoving)
						{
							movePlayer();
						}
						if (nextNode != null && !nextNode.intersects(end))
						{
							chaseTimer++;
							if (chaseTimer%(int)(1000/chaseSpeed) == 0)
							{
								start = new StartNode(nextNode.getX(), nextNode.getY());
								updatePath();
							}
							if (chaseTimer%(25) == 0)
							{
								chaseSpeed++;
							}
						}
					}
//					if (goOn)
//					{
//						if (start != null && end != null)
//						{
//							//nodes.add(start);
//							open.add(start);
//							findPath(start);
//							while (lowNode.getParent() != null)
//							{
//								lowNode.setFound(true);
//								lowNode = lowNode.getParent();
//							}
//						}
//					}
					else if (resetOn)
						reset();
					
					repaint();
				}
			}
			catch(Exception e){e.printStackTrace();}    
		}
	}
	
	//create the double buffer image to load on the screen and paint it
	public void paint(Graphics gc)
	{
		dbImage = createImage(SCREENWIDTH, SCREENHEIGHT);
		dbg = dbImage.getGraphics();
		paintComponent(dbg);
		gc.drawImage(dbImage, 0, 0, this);
	} 
	//paint all the components
	public void paintComponent(Graphics g)
	{
		g.setColor(Color.black);
		g.fillRect(0, 0, SCREENWIDTH, SCREENHEIGHT);
		for (Node n : empty)
			n.draw(g);
		for (Node n : walls)
			n.draw(g);
		if (start != null)
			start.draw(g);
		if (end != null)
			end.draw(g);
		if (showPath)
		{
			for (int i = 0; i < closed.size(); i++)
				closed.get(i).draw(g);
			if (lowNode != null)
				lowNode.draw(g);
		}
		if (player != null)
			player.draw(g);
	}
	
	public void findPath(Node i)
	{
		if (lowNode!= null && i.getHScore() == 0)
			return;
		if (open.size() == 0)
		{
			lowNode = null;
			return;
		}
		//repaint();
		//try {Thread.sleep(FINDSPEED);} catch (InterruptedException e) {e.printStackTrace();}
		searchAround(i);
		for (Node n : open)
		{
			findScore(n);
		}
		lowScore = 999999;
		for (Node n : open)
		{
			if (n.getScore() < lowScore)
			{
				lowScore = n.getScore();
				lowNode = n;
			}
		}
		//updateEfficiency(cutCorners);
		findPath(lowNode);
	}
	
	public void searchAround(Node parent)
	{
		open.remove(parent);
		closed.add(parent);
		
		if (cutCorners)
		{
			for (int column = parent.getX() - 1; column <= parent.getX() + 1; column++)
			{
				for (int row = parent.getY() - 1; row <= parent.getY() + 1; row++)
				{
					if (column != parent.getX() || row != parent.getY())
					{
						TestNode test = new TestNode(column, row);
						if (test.intersects(empty) && !test.intersects(closed) && !test.intersects(walls))
						{
							if (test.intersects(open))
							{
								if (parent.getGScore() < open.at(column, row).getParent().getGScore())
									open.at(column, row).setParent(parent);
							}
							else
							{
								open.add(test);
								test.setParent(parent);
							}
						}
					}
				}
			}
		}
		else
		{
			int o = 0;
			for (int column = parent.getX() - 1; column <= parent.getX() + 1; column++)
			{
				for (int row = parent.getY() - 1; row <= parent.getY() + 1; row++)
				{
					o++;
					if (o%2 == 0)
					{
						TestNode test = new TestNode(column, row);
						if (test.intersects(empty) && !test.intersects(closed) && !test.intersects(walls))
						{
							if (test.intersects(open))
							{
								if (parent.getGScore() < open.at(column, row).getParent().getGScore())
									open.at(column, row).setParent(parent);
							}
							else
							{
								open.add(test);
								test.setParent(parent);
							}
						}
					}
				}
			}
		}
	}
	
	public void findScore(Node n)
	{
		if (n.getParent() != null)
		{
			if (Math.abs(n.getX()-n.getParent().getX()) == 1 && Math.abs(n.getY()-n.getParent().getY()) == 1)
			{
				n.setGScore(n.getParent().getGScore() + 14);
			}
			else
			{
			n.setGScore(n.getParent().getGScore() + 10);
			}
		}
		else
		{
			n.setGScore(0);
		}

		n.setHScore((int)(Math.sqrt(Math.pow(Math.abs(n.getX()-end.getX())*15, 2) + Math.pow(Math.abs(n.getY()-end.getY())*15, 2))));
		
		n.setScore(n.getGScore() + n.getHScore());
	}
	
	public void updateEfficiency(boolean cuttingCorners)
	{
		if (cuttingCorners)
		{
			for (Node n : closed)
			{
				for (int column = n.getX() - 1; column <= n.getX() + 1; column++)
				{
					for (int row = n.getY() - 1; row <= n.getY() + 1; row++)
					{
						if (column != n.getX() || row != n.getY() && n.getParent() != null)
						{
							TestNode newTest = new TestNode(row, column);
							for (Node c : closed)
							{
								if (newTest.intersects(c) && c.getGScore() < n.getParent().getGScore())
								{
									n.setParent(c);
								}
							}
						}
					}
				}
			}
		}
		else
		{
			int o = 0;
			for (Node n : closed)
			{
				for (int column = n.getX() - 1; column <= n.getX() + 1; column++)
				{
					for (int row = n.getY() - 1; row <= n.getY() + 1; row++)
					{
						o++;
						if (o%2 == 0 && n.getParent() != null)
						{
							TestNode newTest = new TestNode(row, column);
							for (Node c : closed)
							{
								if (newTest.intersects(c) && c.getGScore() < n.getParent().getGScore())
								{
									n.setParent(c);
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void movePlayer()
	{
		if (cutCorners)
		{
			if (upOn)
			{
				TestNode test = new TestNode(player.getX(), player.getY() - 1);
				if (test.intersects(empty) && !test.intersects(walls) && !test.intersects(start))
					player.setY(player.getY() - 1);
			}
			if (downOn)
			{
				TestNode test = new TestNode(player.getX(), player.getY() + 1);
				if (test.intersects(empty) && !test.intersects(walls) && !test.intersects(start))
					player.setY(player.getY() + 1);
			}
			if (leftOn)
			{
				TestNode test = new TestNode(player.getX() - 1, player.getY());
				if (test.intersects(empty) && !test.intersects(walls) && !test.intersects(start))
					player.setX(player.getX() - 1);
			}
			if (rightOn)
			{
				TestNode test = new TestNode(player.getX() + 1, player.getY());
				if (test.intersects(empty) && !test.intersects(walls) && !test.intersects(start))
					player.setX(player.getX() + 1);
			}
		}
		else
		{
			if (upOn)
			{
				TestNode test = new TestNode(player.getX(), player.getY() - 1);
				if (test.intersects(empty) && !test.intersects(walls) && !test.intersects(start))
					player.setY(player.getY() - 1);
			}
			else if (downOn)
			{
				TestNode test = new TestNode(player.getX(), player.getY() + 1);
				if (test.intersects(empty) && !test.intersects(walls) && !test.intersects(start))
					player.setY(player.getY() + 1);
			}
			else if (leftOn)
			{
				TestNode test = new TestNode(player.getX() - 1, player.getY());
				if (test.intersects(empty) && !test.intersects(walls) && !test.intersects(start))
					player.setX(player.getX() - 1);
			}
			else if (rightOn)
			{
				TestNode test = new TestNode(player.getX() + 1, player.getY());
				if (test.intersects(empty) && !test.intersects(walls) && !test.intersects(start))
					player.setX(player.getX() + 1);
			}
		}
		end = new EndNode(player.getX(), player.getY());
		updatePath();
	}
	
	public void updatePath()
	{
		if (start != null)
		{
			reset();
			open.add(start);
			findPath(start);
			while ( lowNode != null && lowNode.getParent() != null)
			{
				lowNode.setFound(true);
				nextNode = lowNode;
				lowNode = lowNode.getParent();
			}
		}
	}
	
	public void reset()
	{
		//nodes.clear();
		open.clear();
		closed.clear();
		if (wallOn)
			walls.clear();
		lowNode = null;
		//nodes.addAll(empty);
	}
	
	public static void main(String args[])
	  {
	    JFrame frame = new JFrame("Pathfinder");
	    frame.getContentPane().add(new Pathfinder());
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.pack(); 
	    frame.setResizable(false);
	    frame.setVisible(true);     
	  }
	@Override
	public void keyPressed(KeyEvent e) 
	{
		switch (e.getKeyCode())
		{
		case goKey:
			if (freeze == true)
				freeze = false;
			else
				freeze = true;
			goOn= true;
			break;
		case resetKey:
			pause = true;
			resetOn= true;
			break;
		case startKey:
			pause = true;
			startOn= true;
			break;
		case endKey:
			pause = true;
			endOn= true;
			break;
		case wallKey:
			pause = true;
			wallOn= true;
			break;
		case removeKey:
			pause = true;
			removeOn= true;
			break;
		case playerKey:
			playerOn= true;
			break;
		case upKey:
			playerMoving = true;
			upOn = true;
			break;
		case downKey:
			playerMoving = true;
			downOn = true;
			break;
		case leftKey:
			playerMoving = true;
			leftOn = true;
			break;
		case rightKey:
			playerMoving = true;
			rightOn = true;
			break;
		case brush1:
			brushSize = 1;
			break;
		case brush2:
			brushSize = 2;
			break;
		case brush3:
			brushSize = 3;
			break;
		case brush4:
			brushSize = 4;
			break;
		case brush5:
			brushSize = 10;
			break;
		case showPathKey:
			if (showPath)
				showPath = false;
			else
				showPath = true;
		default:
			break;
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode())
		{
		case goKey:
			goOn= false;
			break;
		case resetKey:
			pause = false;
			resetOn= false;
			break;
		case startKey:
			pause = false;
			startOn= false;
			break;
		case endKey:
			pause = false;
			endOn= false;
			break;
		case wallKey:
			pause = false;
			wallOn= false;
			break;
		case removeKey:
			pause = false;
			removeOn= false;
			break;
		case playerKey:
			playerOn= false;
			break;
		case upKey:
			playerMoving = false;
			upOn = false;
			break;
		case downKey:
			playerMoving = false;
			downOn = false;
			break;
		case leftKey:
			playerMoving = false;
			leftOn = false;
			break;
		case rightKey:
			playerMoving = false;
			rightOn = false;
			break;
		default:
			break;
		}
		
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseDragged(MouseEvent e) 
	{
		if (startOn)
		{
			//nodes.remove(start);
			open.clear();
			closed.clear();
			nextNode = new TestNode(e.getX()/NODESIZE, e.getY()/NODESIZE);
			//nodes.add(start);
			start = new StartNode(e.getX()/NODESIZE, e.getY()/NODESIZE);
			open.add(start);
		}
		
		else if (endOn)
		{
			//nodes.remove(end);
			open.clear();
			closed.clear();
			end = new EndNode(e.getX()/NODESIZE, e.getY()/NODESIZE);
			//nodes.add(end);
		}

		else if (wallOn)
		{
			for (int i = 0; i < brushSize; i++)
			{
				for (int j = 0; j < brushSize; j++)
				{
					if (walls.at(e.getX()/NODESIZE+i, e.getY()/NODESIZE+j) == null)
					{
						WallNode w = new WallNode(e.getX()/NODESIZE+i, e.getY()/NODESIZE+j);
						//nodes.add(w);
						walls.add(w);
					}
				}
			}
		}
		
		else if (removeOn)
		{
			for (int i = 0; i < brushSize; i++)
			{
				for (int j = 0; j < brushSize; j++)
				{
					WallNode w = (WallNode)walls.at(e.getX()/NODESIZE+i, e.getY()/NODESIZE+j);
					if (walls.at(e.getX()/NODESIZE+i, e.getY()/NODESIZE+j) != null)
					{
					//nodes.remove(w);
					walls.remove(w);
					}
				}
			}
		}
		
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) 
	{
		if (startOn)
		{
			//nodes.remove(start);
			open.clear();
			closed.clear();
			nextNode = new TestNode(e.getX()/NODESIZE, e.getY()/NODESIZE);
			//nodes.add(start);
			start = new StartNode(e.getX()/NODESIZE, e.getY()/NODESIZE);
			open.add(start);
		}
		
		else if (endOn)
		{
			//nodes.remove(end);
			open.clear();
			closed.clear();
			end = new EndNode(e.getX()/NODESIZE, e.getY()/NODESIZE);
			//nodes.add(end);
		}
		
		else if (wallOn)
		{
			for (int i = 0; i < brushSize; i++)
			{
				for (int j = 0; j < brushSize; j++)
				{
					if (walls.at(e.getX()/NODESIZE+i, e.getY()/NODESIZE+j) == null)
					{
						WallNode w = new WallNode(e.getX()/NODESIZE+i, e.getY()/NODESIZE+j);
						//nodes.add(w);
						walls.add(w);
					}
				}
			}
		}
		
		else if (removeOn)
		{
			for (int i = 0; i < brushSize; i++)
			{
				for (int j = 0; j < brushSize; j++)
				{
					WallNode w = (WallNode)walls.at(e.getX()/NODESIZE+i, e.getY()/NODESIZE+j);
					if (walls.at(e.getX()/NODESIZE+i, e.getY()/NODESIZE+j) != null)
					{
					//nodes.remove(w);
					walls.remove(w);
					}
				}
			}
		}
		else if (playerOn)
		{
			player = new Player(e.getX()/NODESIZE, e.getY()/NODESIZE);
			movePlayer();
		}
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}  

}
