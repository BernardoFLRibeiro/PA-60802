package pa.iscde.codeRefactoring;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class FieldChecker {

	private ArrayList<Nodeobject> nodelist;

	public FieldChecker() {
		nodelist = new ArrayList<Nodeobject>();
	}

	private static int sourceLine(ASTNode node) {
		return ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
	}

	public ArrayList<Nodeobject> run(File file, String textSelected) {
		javaParser.parse(file, new FieldCheckerAST(textSelected));
		return getList();
	}

	private ArrayList<Nodeobject> getList() {
		return nodelist;
	}

	class FieldCheckerAST extends ASTVisitor {

		private String textSelected;

		public FieldCheckerAST(String textSelected) {

			this.textSelected = textSelected;
		}

		private void addNode(String line, int lineNumber, int startPosition) {

			boolean found = false;
			for (Nodeobject node : nodelist) {
				int nlineNumber = node.getLineNumber();
				int nStartPosition = node.getstartPosition();
				if (nlineNumber == lineNumber && nStartPosition == startPosition) {
					found = true;
				}

			}
			if (!found) {
				nodelist.add(new Nodeobject(line, lineNumber, startPosition));
			}
		}

		private boolean checkName(String name) {
			return textSelected.equals(name);
		}

		@Override
		public boolean visit(VariableDeclarationFragment node) {
			String name = node.getName().toString();

			if (checkName(name)) {
				addNode(name, sourceLine(node), node.getStartPosition());
			}

			if ((!(node.getInitializer() == null))) {
				Expression e = node.getInitializer();
				String init = e.toString();
				String[] array = init.replaceAll("[^a-zA-Z0-9]+", " ").split(" ");
				int position = e.getStartPosition();
				for (String s : array) {
					if (checkName(s)) {
						addNode(node.getName().toString(), sourceLine(node), position);
					}
					position = position + s.length() + 1;
				}
			}
			return super.visit(node);
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			String name = node.getName().toString();
			class AssignVisitor extends ASTVisitor {

				// (composite e imagemap)
				@Override
				public boolean visit(SingleVariableDeclaration node) {

					return super.visit(node);
				}

				// visits assignments (=, +=, etc)
				@Override
				public boolean visit(Assignment node) {
					String varName = node.getLeftHandSide().toString();
					String left = node.getLeftHandSide().toString().replaceAll("this.", "");
					if (checkName(left)) {
						int diff = 0;
						if (left.length() != varName.length()) {
							// diff = 5;
						}

						addNode(node.toString(), sourceLine(node.getLeftHandSide()),
								node.getLeftHandSide().getStartPosition() + diff);
					}

					if (checkName(node.getRightHandSide().toString())) {
						addNode(node.getRightHandSide().toString(), sourceLine(node.getRightHandSide()),
								node.getRightHandSide().getStartPosition());
					}

					return true;
				}

				// visits post increments/decrements (i++, i--)
				@Override
				public boolean visit(PostfixExpression node) {
					String varName = node.getOperand().toString();
					// System.out.println(sourceLine(node) + " // " + varName + "++");
					return true;
				}

				// visits pre increments/decrements (++i, --i)
				@Override
				public boolean visit(PrefixExpression node) {
					String varName = node.getOperand().toString();
					// System.out.println(sourceLine(node) + " // " + " ++" + varName);
					return true;
				}
			}

			AssignVisitor assignVisitor = new AssignVisitor();
			node.accept(assignVisitor);
			return super.visit(node);
		}

		public boolean visit(MethodInvocation node) {
			String temp = node.getExpression().toString();
			if (checkName(temp)) {
				addNode(node.toString(), sourceLine(node), node.getExpression().getStartPosition());
			}
			return super.visit(node);
		}

		public boolean visit(SingleVariableDeclaration node) {

			String temp = node.getName().toString();
			if (checkName(temp)) {
				System.out.println(temp);
				addNode(temp, sourceLine(node), node.getName().getStartPosition());
			}
			return true;
		}

	}

}
