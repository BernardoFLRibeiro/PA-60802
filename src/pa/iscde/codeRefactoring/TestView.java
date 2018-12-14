package pa.iscde.codeRefactoring;

import java.awt.TextField;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JOptionPane;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import pt.iscte.pidesco.extensibility.PidescoView;
import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;
import pt.iscte.pidesco.projectbrowser.model.SourceElement;
import pt.iscte.pidesco.projectbrowser.service.ProjectBrowserListener;
import pt.iscte.pidesco.projectbrowser.service.ProjectBrowserServices;

public class TestView implements PidescoView {

	private JavaEditorServices javaServ;

	@Override
	public void createContents(Composite viewArea, Map<String, Image> imageMap) {
		viewArea.setLayout(new RowLayout(SWT.VERTICAL));

		BundleContext context = Activator.getContext();

		ServiceReference<ProjectBrowserServices> servicereference = context
				.getServiceReference(ProjectBrowserServices.class);

		ProjectBrowserServices projServ = context.getService(servicereference);

		projServ.addListener(new ProjectBrowserListener.Adapter() {
			@Override
			public void doubleClick(SourceElement element) {
				// TODO Auto-generated method stub
				new Label(viewArea, SWT.NONE).setText(element.getName());
				viewArea.layout();
				super.doubleClick(element);
			}
		});

		ServiceReference<JavaEditorServices> serviceReference2 = context.getServiceReference(JavaEditorServices.class);

		javaServ = context.getService(serviceReference2);

		Text textBox = new Text(viewArea, 20);

		Button button = new Button(viewArea, SWT.PUSH);
		button.setText("Field Rename");

		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!textBox.getText().isEmpty()) {
					File f = javaServ.getOpenedFile();
					ITextSelection iText = javaServ.getTextSelected(f);
					String textSelected = iText.getText();
					if (!(textSelected.isEmpty()) && !(textSelected.equals(null))) {

						writeinFile(new FieldChecker().run(f, textSelected), iText, textBox.getText());

						/*
						 * int dialogButton = JOptionPane.YES_NO_OPTION; int dialogResult =
						 * JOptionPane.showConfirmDialog(null, "Quer Trocar o textSelected pelo Y?",
						 * "Warning", dialogButton); if (dialogResult == JOptionPane.YES_OPTION) {
						 * 
						 * }
						 */
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	}

	private void writeinFile(ArrayList<Node> list, ITextSelection iText, String text) {
		System.out.println("Select: " + iText.getText() + " : " + text);
		int diff = 0;
		boolean first = true;

		for (Node n : list) {
			System.out.println(n.getLine());
			if (!first) {
				diff = diff + text.length() - iText.getText().length();
			}
			int numberLine = n.getLine();
			int startPosition = n.getNode().getStartPosition();
			int position = startPosition + numberLine - 1;
			javaServ.insertText(javaServ.getOpenedFile(), text, position + diff, iText.getLength());
			first = false;
			System.out.println("inseri");
		}

		javaServ.saveFile(javaServ.getOpenedFile());
	}

//	static class PrintVisitor implements JavaFileVisitor {
//		public boolean visitPackage(String packageName) {
//			System.out.print(packageName);
//			System.out.println("");
//
//			return true;
//		}
//
//		public void visit(Class<?> clazz) {
//			System.out.print(clazz.getSimpleName());
//			System.out.println("");
//		}
//
//	}
}
