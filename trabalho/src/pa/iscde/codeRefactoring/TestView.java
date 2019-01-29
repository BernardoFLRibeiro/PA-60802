package pa.iscde.codeRefactoring;

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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import pa.iscde.ExtensionPointCodeRefactoring.extensionPointrefactoring;
import pt.iscte.pidesco.extensibility.PidescoView;
import pt.iscte.pidesco.projectbrowser.model.SourceElement;
import pt.iscte.pidesco.projectbrowser.service.ProjectBrowserListener;

public class TestView implements PidescoView {

	@Override
	public void createContents(Composite viewArea, Map<String, Image> imageMap) {
		viewArea.setLayout(new RowLayout(SWT.VERTICAL));

		Activator.getProjectBrowserServices().addListener(new ProjectBrowserListener.Adapter() {

			@Override
			public void doubleClick(SourceElement element) {
				new Label(viewArea, SWT.NONE).setText(element.getName());
				viewArea.layout();
				super.doubleClick(element);
			}
		});

		registService(viewArea);


		Label labelRename = new Label(viewArea, 0);
		labelRename.setText("Rename");

		Text textBox = new Text(viewArea, 20);

		Button button = new Button(viewArea, SWT.PUSH);
		button.setText("GO");
		button.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				ITextSelection iText = Activator.getJavaEditorServices()
						.getTextSelected(Activator.getJavaEditorServices().getOpenedFile());
				String textSelected = iText.getText();

				boolean iTextisValid = (!(textSelected.isEmpty()) && !(textSelected.equals(null)));
				boolean textisNotEmpty = !(textBox.getText().isEmpty());
				String error = "";
				
				if (textisNotEmpty) {
					if (iTextisValid) {
						Activator.getRefService().rename(textBox.getText());
					} else {
						error = "Não selecionou corretamente!";
					}
				} else {
					error = "Não escreveu uma palavra!";

				}

				if (!(error.isEmpty())) {
					MessageBox box = new MessageBox(viewArea.getShell(), SWT.OK);
					box.setText("Error");
					box.setMessage(error);
					box.open();
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

}