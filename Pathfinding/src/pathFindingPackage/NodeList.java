package pathFindingPackage;

import java.util.ArrayList;

public class NodeList extends ArrayList<Node>
{

	public NodeList()
	{
		super();
	}

	public Node at(int x, int y)
	{
		for (Node n : this)
		{
			if (n.getX() == x && n.getY() == y)
				return n;
		}
		return null;
	}
}
