package pa.iscde.codeRefactoring;

public class Nodeobject {

	private int lineNumber, startPosition;

	public Nodeobject(int lineNumber, int startPosition) {
		this.lineNumber = lineNumber;
		this.startPosition = startPosition;
	}

	public int getstartPosition() {
		return startPosition;
	}

	public int getLineNumber() {
		return lineNumber;
	}

}
