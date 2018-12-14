package pa.iscde.codeRefactoring;

import java.io.File;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class FileExplorer extends ASTVisitor {

	private File f;
	private String textSelected;

	public FileExplorer(File f, String textSelected) {
		this.f = f;
		this.textSelected = textSelected;
	}

	public boolean isField() {
		boolean isField = true;
		
		return isField;
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		System.out.println("Class: " + node.getName());
		return true;

	}

	@Override
	public boolean visit(MethodDeclaration node) {
		System.out.println("Method: " + node.getName());
		return true;
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		for (Object o : node.fragments()) {
			VariableDeclarationFragment var = (VariableDeclarationFragment) o;
			String name = var.getName().toString();
			System.out.println("Field: " + name);
		}
		return true;
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		System.out.println("SingleVariable: " + node.getName());

		class AssignVisitor extends ASTVisitor {
			// visits assignments (=, +=, etc)
			@Override
			public boolean visit(Assignment node) {
				String varName = node.getLeftHandSide().toString();
				System.out.println("Assignment: " + varName);
				return true;
			}

			// visits post increments/decrements (i++, i--)
			@Override
			public boolean visit(PostfixExpression node) {
				String varName = node.getOperand().toString();
				System.out.println("PostfixExpression: " + varName);
				return true;
			}

			// visits pre increments/decrements (++i, --i)
			@Override
			public boolean visit(PrefixExpression node) {
				String varName = node.getOperand().toString();
				System.out.println("PrefixExpression: " + varName);
				return true;
			}

		}
		AssignVisitor assignVisitor = new AssignVisitor();
		node.getParent().accept(assignVisitor);
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		System.out.println("VariableDeclarationFragment: " + node.getName());

		return true;
	}

	@Override
	public boolean visit(FieldAccess node) {
		System.out.println("FieldAccess: " + node.getName());

		return true;
	}

	@Override
	public boolean visit(MethodInvocation node) {
		System.out.println("MethodInvocation: " + node.getExpression());
		return super.visit(node);
	}
}
