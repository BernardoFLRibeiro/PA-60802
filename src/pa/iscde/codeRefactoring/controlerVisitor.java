package pa.iscde.codeRefactoring;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.jface.text.ITextSelection;

public class controlerVisitor {

	private ArrayList<Nodeobject> nodelist;

	public controlerVisitor(File file, ITextSelection textSelected) {
		nodelist = new ArrayList<Nodeobject>();
		run(file, textSelected);
	}

	private void run(File file, ITextSelection textSelected) {
		
		javaParser.parse(file, new visitor(textSelected.getText(), nodelist));

		Collections.sort(nodelist, new Comparator<Nodeobject>() {

			@Override
			public int compare(Nodeobject arg0, Nodeobject arg1) {
				return arg0.getstartPosition() - arg1.getstartPosition();
			}
		});

	}

	public ArrayList<Nodeobject> getList() {
		return nodelist;
	}
	
	
	
	
	
	

}
