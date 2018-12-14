package pa.iscde.codeRefactoring;

import org.eclipse.jdt.core.dom.SimpleName;

public class Node {

	private SimpleName sName;
	private int line, type;

	public Node(SimpleName sName, int line, int type) {
		this.sName = sName;
		this.line = line;
		this.type = type;
	}

	public SimpleName getNode() {
		return sName;
	}

	public int getLine() {
		return line;
	}

	public int getType() {
		return type;
	}

}
