package pa.iscde.codeRefactoring;

public class Nodeobject {

	private int lineNumber, startPosition;
	private String line;

	public Nodeobject(String line, int lineNumber, int startPosition) {
		this.line = line;
		this.lineNumber = lineNumber;
		this.startPosition = startPosition;
	}

	public String getline() {
		return line;
	}
	
	public int getstartPosition() {
		return startPosition;
	}

	public int getLineNumber() {
		return lineNumber;
	}

}
