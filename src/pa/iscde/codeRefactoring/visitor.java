package pa.iscde.codeRefactoring;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;

public class visitor extends ASTVisitor {

	private String textSelected;
	private ArrayList<Nodeobject> nodelist;
	private int classStartPosition;
	private boolean changeClass;

	public visitor(String textSelected, ArrayList<Nodeobject> nodelist) {
		changeClass = false;
		this.textSelected = textSelected;
		this.nodelist = nodelist;
	}

	public boolean getClassChange() {
		return changeClass;
	}

	private void addNode(String name, int lineNumber, int startPosition) {

		if (textSelected.equals(name)) {
			boolean found = false;
			for (Nodeobject node : nodelist) {
				if (node.getLineNumber() == lineNumber && node.getstartPosition() == startPosition) {
					found = true;
				}
			}
			if (!found) {
				// if (classStartPosition == startPosition) {
				// changeClass = true;
				// }
				nodelist.add(new Nodeobject(lineNumber, startPosition));
			}
		}
	}

	private static int sourceLine(ASTNode node) {
		return ((CompilationUnit) node.getRoot()).getLineNumber(node.getStartPosition());
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		// classStartPosition = node.getName().getStartPosition();
		// addNode(node.getName().toString(), sourceLine(node),
		// node.getName().getStartPosition());

		return super.visit(node);
	}

	@Override
	public boolean visit(SingleVariableDeclaration node) {
		String temp = node.getName().toString();
		addNode(temp, sourceLine(node), node.getName().getStartPosition());
		return true;
	}

	@Override
	public boolean visit(VariableDeclarationFragment node) {
		String name = node.getName().toString();

		addNode(name, sourceLine(node), node.getName().getStartPosition());

		if ((!(node.getInitializer() == null))) {
			Expression e = node.getInitializer();
			class ExpressionVisitor extends ASTVisitor {

				@Override
				public boolean visit(SimpleName node) {
					addNode(node.toString(), sourceLine(node), node.getStartPosition());
					return super.visit(node);
				}
			}

			ExpressionVisitor visitExpr = new ExpressionVisitor();
			e.accept(visitExpr);
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {

		addNode(node.getName().toString(), sourceLine(node.getName()), node.getName().getStartPosition());

		class AssignVisitor extends ASTVisitor {

			// visits assignments (=, +=, etc)
			@Override
			public boolean visit(Assignment node) {

				Expression leftSide = node.getLeftHandSide();
				Expression rightSide = node.getRightHandSide();

				String left = leftSide.toString().replaceAll("this.", "");
				int diff = 0;
				if (leftSide.toString().contains("this.")) {
					diff = 5;
				}

				addNode(left, sourceLine(leftSide), leftSide.getStartPosition() + diff);

				addNode(rightSide.toString(), sourceLine(rightSide), rightSide.getStartPosition());
				return true;
			}

			// visits post increments/decrements (i++, i--)
			@Override
			public boolean visit(PostfixExpression node) {
				String variable = node.getOperand().toString();
				addNode(variable, sourceLine(node), node.getOperand().getStartPosition());
				return true;
			}

			// visits pre increments/decrements (++i, --i)
			@Override
			public boolean visit(PrefixExpression node) {
				String variable = node.getOperand().toString();
				addNode(variable, sourceLine(node), node.getOperand().getStartPosition());
				return true;
			}

			// visits if statements
			@Override
			public boolean visit(IfStatement node) {
				String temp = node.getExpression().toString();
				addNode(temp, sourceLine(node), node.getExpression().getStartPosition());

				return super.visit(node);
			}

			// visits while statements
			@Override
			public boolean visit(WhileStatement node) {
				String temp = node.getExpression().toString();
				addNode(temp, sourceLine(node), node.getExpression().getStartPosition());
				return super.visit(node);
			}

			// visits enhanced for
			@Override
			public boolean visit(EnhancedForStatement node) {

				Expression ex = node.getExpression();
				SingleVariableDeclaration svd = node.getParameter();
				addNode(ex.toString(), sourceLine(ex), ex.getStartPosition());
				addNode(svd.getName().toString(), sourceLine(svd.getName()), svd.getName().getStartPosition());

				return super.visit(node);
			}

			@Override
			public boolean visit(InfixExpression node) {
				Expression left = node.getLeftOperand();
				Expression right = node.getRightOperand();

				class ExpressionVisitor extends ASTVisitor {

					@Override
					public boolean visit(SimpleName node) {
						addNode(node.toString(), sourceLine(node), node.getStartPosition());
						return super.visit(node);
					}
				}

				ExpressionVisitor visitExpr = new ExpressionVisitor();
				left.accept(visitExpr);
				right.accept(visitExpr);
				return super.visit(node);
			}

		}

		AssignVisitor assignVisitor = new AssignVisitor();
		node.accept(assignVisitor);
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		
		if ((!(node.getName() == null))) {
			addNode(node.getName().toString(), sourceLine(node.getName()), node.getName().getStartPosition());
		}

		if ((!(node.getExpression() == null))) {
			addNode(node.getExpression().toString(), sourceLine(node), node.getExpression().getStartPosition());
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayAccess node) {
		addNode(node.getArray().toString(), sourceLine(node.getArray()), node.getArray().getStartPosition());
		addNode(node.getIndex().toString(), sourceLine(node), node.getIndex().getStartPosition());
		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayCreation node) {
		for (Object o : node.dimensions()) {
			Expression e = (Expression) o;
			if (e != null) {
				addNode(e.toString(), sourceLine(e), e.getStartPosition());

			}
			
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(ArrayInitializer node) {
		for (Object o : node.expressions()) {
			Expression e = (Expression) o;
			if (e != null) {
				addNode(e.toString(), sourceLine(e), e.getStartPosition());

			}
		}

		return super.visit(node);
	}

}
