package internal;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jface.text.ITextSelection;

import pa.iscde.codeRefactoring.Activator;
import pa.iscde.codeRefactoring.Nodeobject;
import pa.iscde.codeRefactoring.controlerVisitor;
import pa.iscde.services.refactoringServices;
import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;

public class implementRename implements refactoringServices {

	private JavaEditorServices javaServ;

	@Override
	public void rename(String newName) {

		javaServ = Activator.getJavaEditorServices();
		File f = javaServ.getOpenedFile();

		ITextSelection iText = javaServ.getTextSelected(f);

		controlerVisitor controler = new controlerVisitor(f, iText);
		writeinFile(controler.getList(), iText, newName);

		// controler.changeClass(newName);

	}

	private void writeinFile(ArrayList<Nodeobject> list, ITextSelection iText, String text) {

		int diff = 0;
		int itextLength = iText.getText().length();
		int textLength = text.length();
		boolean first = true;

		for (Nodeobject n : list) {
			if (!first) {
				diff = diff + textLength - itextLength;
			}
			// int position = n.getstartPosition() + n.getLineNumber() - 1;
			int position = n.getstartPosition();
			javaServ.insertText(javaServ.getOpenedFile(), text, position + diff, iText.getLength());
			first = false;
		}

		javaServ.saveFile(javaServ.getOpenedFile());
	}

}
