package pa.iscde.codeRefactoring;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
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

import pa.iscde.ExtensionPointCodeRefactoring.extensionPointrefactoring;
import pa.iscde.services.refactoringServices;
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
		registService(viewArea);
		ServiceReference<JavaEditorServices> serviceReference2 = context.getServiceReference(JavaEditorServices.class);
		javaServ = context.getService(serviceReference2);
		Text textBox = new Text(viewArea, 20);
		Button button = new Button(viewArea, SWT.PUSH);
		button.setText("Field Rename");
		File f = javaServ.getOpenedFile();
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!textBox.getText().isEmpty()) {
					ITextSelection iText = javaServ.getTextSelected(f);
					String textSelected = iText.getText();
					if (!(textSelected.isEmpty()) && !(textSelected.equals(null))) {
						writeinFile(new FieldChecker().run(f, textSelected), iText, textBox.getText());
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	private void registService(Composite area) {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = reg.getConfigurationElementsFor("trabalho.extensionPointCRefactoring");
		for (IConfigurationElement e : elements) {
			try {
				extensionPointrefactoring action = (extensionPointrefactoring) e.createExecutableExtension("class");

				action.run(area);

			} catch (CoreException e1) {
				e1.printStackTrace();
			}

		}
	}

	private void writeinFile(ArrayList<Nodeobject> list, ITextSelection iText, String text) {
		int diff = 0;
		boolean first = true;
		for (Nodeobject n : list) {
			if (!first) {
				diff = diff + text.length() - iText.getText().length();
			}
			int numberLine = n.getLineNumber();
			int startPosition = n.getstartPosition();
			int position = startPosition + numberLine - 1;
			// System.out.println(n.getLineNumber() + " || " + n.getline() + " || " +
			// n.getstartPosition());
			javaServ.insertText(javaServ.getOpenedFile(), text, position + diff, iText.getLength());
			first = false;
		}

		javaServ.saveFile(javaServ.getOpenedFile());
	}

}