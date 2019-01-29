package pa.iscde.codeRefactoring;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import internal.implementRename;
import pa.iscde.services.refactoringServices;
import pt.iscte.pidesco.javaeditor.service.JavaEditorServices;
import pt.iscte.pidesco.projectbrowser.service.ProjectBrowserServices;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private static JavaEditorServices javaServ;
	private static ProjectBrowserServices projServ;
	private static refactoringServices refService;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

		bundleContext.registerService(refactoringServices.class, new implementRename(), null);

		ServiceReference<ProjectBrowserServices> servicereference = context
				.getServiceReference(ProjectBrowserServices.class);
		projServ = context.getService(servicereference);

		ServiceReference<JavaEditorServices> serviceReference2 = context.getServiceReference(JavaEditorServices.class);
		javaServ = context.getService(serviceReference2);

		ServiceReference<refactoringServices> serviceRefactoring = context
				.getServiceReference(refactoringServices.class);
		refService = context.getService(serviceRefactoring);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	public static ProjectBrowserServices getProjectBrowserServices() {
		return projServ;
	}

	public static JavaEditorServices getJavaEditorServices() {
		return javaServ;
	}

	public static refactoringServices getRefService() {
		return refService;
	}

}
