package pa.iscde.codeRefactoring;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class FieldChecker {

	private ArrayList<Node> nodelist;

	public FieldChecker() {
		nodelist = new ArrayList<Node>();
	}

	private static int sourceLine(ASTNode node) {
		return ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
	}

	public ArrayList<Node> run(File file, String textSelected) {

		javaParser.parse(file, new Refactorv1(textSelected));
		return getList();
	}

	private ArrayList<Node> getList() {
		return nodelist;
	}

	class Refactorv1 extends ASTVisitor {
		String textSelected;

		public Refactorv1(String textSelected) {
			this.textSelected = textSelected;
		}

		@Override
		public boolean visit(SimpleName node) {
			if (textSelected.equals(node.toString())) {
				nodelist.add(new Node(node, sourceLine(node), 0));
			}

			return super.visit(node);
		}

	}

	class FieldCheckerAST extends ASTVisitor {
		String textSelected;

		public FieldCheckerAST(String textSelected) {
			this.textSelected = textSelected;
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			if (textSelected.equals(node.getName().toString())) {

				String name = node.getName().toString();
				System.out.println("MD: " + name);

			}
			return super.visit(node);
		}

//ENCONTRA AS VARIAVEIS DECLARADAS
		@Override
		public boolean visit(VariableDeclarationFragment node) {
			if (textSelected.equals(node.getName().toString())) {

				System.out.println("VDid: " + node.getName().getIdentifier() + "name: " + node.getName().toString());
				System.out.println(sourceLine(node));

			}

			return true;
		}

//ENCONTRA AS VARIAVEIS QUE INVOCAM UM METODO
		public boolean visit(MethodInvocation node) {

			if (textSelected.equals(node.getExpression().toString())) {

				System.out.println(
						"MIid: " + node.getName().getIdentifier() + "name: " + node.getExpression().toString());
				System.out.println(sourceLine(node));
				// nodelist.add(node);
			}
			return super.visit(node);
		}

//ENCONTRA OS ATRIBUTOS DE UM METODO/CONSTRUTOR
		public boolean visit(SingleVariableDeclaration node) {
			if (textSelected.equals(node.getName().toString())) {
				System.out.println("SVDid: " + node.getName().getIdentifier() + "name: " + node.getName().toString());
				System.out.println(sourceLine(node));

				class AssignVisitor extends ASTVisitor {

					// visits assignments (=, +=, etc)
					@Override
					public boolean visit(Assignment node) {
						String varName = node.getLeftHandSide().toString();
						System.out.println("=: " + varName + " - " + node.getRightHandSide());
						System.out.println(sourceLine(node));
						return true;
					}

					// visits post increments/decrements (i++, i--)
					@Override
					public boolean visit(PostfixExpression node) {
						String varName = node.getOperand().toString();
						System.out.println("+: " + varName);
						System.out.println(sourceLine(node));
						return true;
					}

					// visits pre increments/decrements (++i, --i)
					@Override
					public boolean visit(PrefixExpression node) {
						String varName = node.getOperand().toString();
						System.out.println("++: " + varName);
						System.out.println(sourceLine(node));
						return true;
					}
				}
				AssignVisitor assignVisitor = new AssignVisitor();
				node.getParent().accept(assignVisitor);
			}
			return true;
		}

		@Override
		public boolean visit(FieldAccess node) {
			if (textSelected.equals(node.getName().toString())) {

				System.out.println("FAid: " + node.getName().getIdentifier() + "name: " + node.getName().toString());
				System.out.println(sourceLine(node));
			}
			return super.visit(node);

		}

	}
}
