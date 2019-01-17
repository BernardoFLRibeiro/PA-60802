package pa.iscde.codeRefactoring;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

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

		private void addNode(String name, int lineNumber, int startPosition) {
			if (textSelected.equals(name)) {
				boolean found = false;
				for (Nodeobject node : nodelist) {
					int nlineNumber = node.getLineNumber();
					int nStartPosition = node.getstartPosition();
					if (nlineNumber == lineNumber && nStartPosition == startPosition) {
						found = true;
					}
				}
				if (!found) {
					nodelist.add(new Nodeobject(lineNumber, startPosition));
				}
			}
		}

		private void dealWithRightSide(Expression e) {
			String rightSide = e.toString();
			if (rightSide.length() != 0) {
				String[] array = rightSide.split(" ");
				if (array.length == 1) {
					addNode(rightSide, sourceLine(e), e.getStartPosition());

				} else {
					int offset = 0;
					for (String s : array) {
						addNode(s, sourceLine(e), e.getStartPosition() + offset);
						offset = offset + 1 + s.length();
					}
				}
			}
		}

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
				String init = e.toString();
				String[] array = init.replaceAll("[^a-zA-Z0-9]+", " ").split(" ");
				int position = e.getStartPosition();
				for (String s : array) {
					addNode(s, sourceLine(node), position);
					position = position + s.length() + 1;
				}
			}
			return super.visit(node);
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			String name = node.getName().toString();
			class AssignVisitor extends ASTVisitor {

				// visits assignments (=, +=, etc)
				@Override
				public boolean visit(Assignment node) {
					String varName = node.getLeftHandSide().toString();

					String left = node.getLeftHandSide().toString().replaceAll("this.", "");
					int diff = 0;
					if (node.getLeftHandSide().toString().contains("this.")) {
						diff = 5;
					}

					addNode(left, sourceLine(node.getLeftHandSide()), node.getLeftHandSide().getStartPosition() + diff);
					addNode(node.getRightHandSide().toString(), sourceLine(node.getRightHandSide()),
							node.getRightHandSide().getStartPosition());
					return true;
				}

				// visits post increments/decrements (i++, i--)
				@Override
				public boolean visit(PostfixExpression node) {
					String variable = node.getOperand().toString();
					addNode(variable, sourceLine(node), node.getStartPosition());
					return true;
				}

				// visits pre increments/decrements (++i, --i)
				@Override
				public boolean visit(PrefixExpression node) {
					Expression operand = node.getOperand();
					addNode(operand.toString(), sourceLine(node), operand.getStartPosition());
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
					addNode(left.toString(), sourceLine(left), left.getStartPosition());
					addNode(right.toString(), sourceLine(right), right.getStartPosition());

					return super.visit(node);
				}

			}

			AssignVisitor assignVisitor = new AssignVisitor();
			node.accept(assignVisitor);
			return super.visit(node);
		}

		public boolean visit(MethodInvocation node) {
			String temp = node.getExpression().toString();
			addNode(temp, sourceLine(node), node.getExpression().getStartPosition());

			return super.visit(node);
		}

	}

}
