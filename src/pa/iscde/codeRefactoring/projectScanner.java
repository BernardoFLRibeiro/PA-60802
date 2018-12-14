package pa.iscde.codeRefactoring;

import java.io.File;
import java.util.ArrayList;

public class projectScanner {

	private String pathProject = "../pt.iscte.pidesco.demo/src";
	private File basedir;

	public projectScanner() {
		basedir = new File(pathProject);
		assert basedir.exists() && basedir.isDirectory();

	}

	public void accept(JavaFileVisitor v) {

		File[] flist = basedir.listFiles();
		runList(getListOfFiles(flist));
	}

	private void runList(ArrayList<File> list) {
		for (File f : list) {
			if (f.isDirectory()) {
				System.out.println("D: " + f.getName());
				runList(getListOfFiles(f.listFiles()));
			} else {
				System.out.println("F: " + f.getName());
			}
		}

	}

	private ArrayList<File> getListOfFiles(File[] array) {
		ArrayList<File> temp = new ArrayList<File>();

		for (int i = 0; i < array.length; i++) {
			temp.add(array[i]);
		}
		return temp;

	}

	/*
	 * private static void visit(File dir, JavaFileVisitor v) { if (dir.isFile()) {
	 * try { Class<?> clazz = Class.forName(stack.packageName().replaceAll(".java",
	 * "")); v.visit(clazz); Field[] listFields = clazz.getDeclaredFields(); if
	 * (listFields.length != 0) { if (clazz.isEnum()) {
	 * 
	 * System.out.print("<ENUM> "); for (Field f : listFields) { if
	 * (f.isEnumConstant()) { System.out.print(f.getName() + " "); } }
	 * System.out.println(""); } else { for (Field f : listFields) { if
	 * (!Modifier.isStatic(f.getModifiers())) { System.out.print("-" + f.getName());
	 * System.out.println("");
	 * 
	 * }
	 * 
	 * } } } System.out.println("");
	 * 
	 * } catch (ClassNotFoundException e) { e.printStackTrace(); } } else {
	 * 
	 * }
	 * 
	 * File[] list = dir.listFiles(); for (File f : list) {
	 * 
	 * }
	 * 
	 * }
	 */

}
