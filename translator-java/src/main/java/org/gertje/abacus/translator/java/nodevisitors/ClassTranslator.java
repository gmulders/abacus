package org.gertje.abacus.translator.java.nodevisitors;

import org.gertje.abacus.context.AbacusContext;
import org.gertje.abacus.nodes.AbstractComparisonNode;
import org.gertje.abacus.nodes.AddNode;
import org.gertje.abacus.nodes.AndNode;
import org.gertje.abacus.nodes.ArrayNode;
import org.gertje.abacus.nodes.AssignmentNode;
import org.gertje.abacus.nodes.BinaryOperationNode;
import org.gertje.abacus.nodes.BooleanNode;
import org.gertje.abacus.nodes.ConcatStringNode;
import org.gertje.abacus.nodes.DateNode;
import org.gertje.abacus.nodes.DecimalNode;
import org.gertje.abacus.nodes.DivideNode;
import org.gertje.abacus.nodes.EqNode;
import org.gertje.abacus.nodes.ExpressionNode;
import org.gertje.abacus.nodes.FactorNode;
import org.gertje.abacus.nodes.FunctionNode;
import org.gertje.abacus.nodes.GeqNode;
import org.gertje.abacus.nodes.GtNode;
import org.gertje.abacus.nodes.IfNode;
import org.gertje.abacus.nodes.IntegerNode;
import org.gertje.abacus.nodes.LeqNode;
import org.gertje.abacus.nodes.LtNode;
import org.gertje.abacus.nodes.ModuloNode;
import org.gertje.abacus.nodes.MultiplyNode;
import org.gertje.abacus.nodes.NegativeNode;
import org.gertje.abacus.nodes.NeqNode;
import org.gertje.abacus.nodes.Node;
import org.gertje.abacus.nodes.NodeType;
import org.gertje.abacus.nodes.NotNode;
import org.gertje.abacus.nodes.NullNode;
import org.gertje.abacus.nodes.OrNode;
import org.gertje.abacus.nodes.PositiveNode;
import org.gertje.abacus.nodes.PowerNode;
import org.gertje.abacus.nodes.RootNode;
import org.gertje.abacus.nodes.StatementListNode;
import org.gertje.abacus.nodes.StringNode;
import org.gertje.abacus.nodes.SubtractNode;
import org.gertje.abacus.nodes.SumNode;
import org.gertje.abacus.nodes.VariableNode;
import org.gertje.abacus.nodevisitors.DefaultVisitor;
import org.gertje.abacus.nodevisitors.EvaluationException;
import org.gertje.abacus.nodevisitors.ExpressionEvaluator;
import org.gertje.abacus.nodevisitors.NodeVisitor;
import org.gertje.abacus.nodevisitors.VariableVisitor;
import org.gertje.abacus.nodevisitors.VisitingException;
import org.gertje.abacus.runtime.expression.ArithmeticOperation;
import org.gertje.abacus.runtime.expression.BooleanOperation;
import org.gertje.abacus.runtime.expression.CastHelper;
import org.gertje.abacus.runtime.expression.StringOperation;
import org.gertje.abacus.symboltable.NoSuchVariableException;
import org.gertje.abacus.symboltable.SymbolTable;
import org.gertje.abacus.symboltable.Variable;
import org.gertje.abacus.translator.java.runtime.AbacusRuntimeException;
import org.gertje.abacus.types.Type;

import org.gertje.abacus.util.JavaTypeHelper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.AASTORE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACONST_NULL;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ARRAYLENGTH;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.DUP2_X1;
import static org.objectweb.asm.Opcodes.DUP_X1;
import static org.objectweb.asm.Opcodes.DUP_X2;
import static org.objectweb.asm.Opcodes.F_SAME1;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_1;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFLT;
import static org.objectweb.asm.Opcodes.IFNE;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.IF_ICMPGE;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.POP2;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.SWAP;
import static org.objectweb.asm.Opcodes.V1_7;

public class ClassTranslator implements NodeVisitor<Void, TranslationException> {

	/**
	 * De symboltable met de variabelen en de functies.
	 */
	protected SymbolTable symbolTable;

	/**
	 * The method visitor used to create the method.
	 */
	private MethodVisitor mv;

	/**
	 * The current line number we are processing.
	 */
	private int lineNumber = -1;

	/**
	 * A list containing all the variables that are read at some time.
	 */
	private List<Variable> readVariableList;

	/**
	 * A list containing all the variables to which an assignment is made at some time.
	 */
	private List<Variable> storeVariableList;

	/**
	 * A map identifier --> index of the variable.
	 */
	private Map<String, Integer> methodVariableIndexes;

	/**
	 * The name of the class that is generated.
	 */
	private String generatedClassName;

	/**
	 * Constructor.
	 */
	public ClassTranslator(AbacusContext abacusContext) {
		this.symbolTable = abacusContext.getSymbolTable();
	}

	/**
	 * Translates the node to byte code.
	 * @param node The node to be translated.
	 * @param extendedClass The class that needs to be extended by the generated class.
	 * @param generatedClassName The name of the class that will be generated.
	 * @return A byte array containing the byte code.
	 * @throws TranslationException
	 */
	public byte[] translate(Node node, Class extendedClass, String generatedClassName) throws TranslationException {
		initialize(node);
		this.generatedClassName = generatedClassName;
		return buildClass(node, extendedClass);
	}

	/**
	 * Initializes the translator for translating the given node.
	 * @param node The node to be translated.
	 * @throws TranslationException
	 */
	private void initialize(Node node) throws TranslationException {
		readMethodVariables(node);
	}

	/**
	 * Reads all variables that are read or written in the node and adds them to {@link #readVariableList},
	 * {@link #storeVariableList} and {@link #methodVariableIndexes}.
	 * @param node The node.
	 * @throws TranslationException
	 */
	private void readMethodVariables(Node node) throws TranslationException {
		VariableVisitor variableVisitor = new VariableVisitor();
		try {
			node.accept(variableVisitor);
		} catch (VisitingException e) {
			throw new TranslationException(e.getMessage(), node, e);
		}

		readVariableList = new ArrayList<>(variableVisitor.getReadVariables());
		storeVariableList = new ArrayList<>(variableVisitor.getStoreVariables());

		methodVariableIndexes = new HashMap<>();

		// Join both lists to get one set with all
		Set<Variable> variableSet = new HashSet<>(readVariableList);
		variableSet.addAll(storeVariableList);

		// Start counting at 1, because 'this' has index 0.
		int i = 1;
		for (Variable variable : variableSet) {
			methodVariableIndexes.put(variable.getIdentifier(), i++);
		}
	}

	/**
	 * Builds tha class from the node.
	 * @param node The node.
	 * @param extendedClass The class that needs to be extended by the generated class.
	 * @return A byte array containing the byte code.
	 * @throws TranslationException
	 */
	private byte[] buildClass(Node node, Class extendedClass) throws TranslationException {

		ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, determineClassName(generatedClassName), null, determineClassName(extendedClass), null);

		cw.visitSource(null, null);

		// Append the constructor.
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		Label l0 = new Label();
		mv.visitLabel(l0);
		mv.visitLineNumber(25, l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, determineClassName(extendedClass), "<init>", "()V", false);
		mv.visitInsn(RETURN);
		Label l1 = new Label();
		mv.visitLabel(l1);
		mv.visitLocalVariable("this", "L" + determineClassName(generatedClassName) + ";", null, l0, l1, 0);
		mv.visitMaxs(0, 0);
		mv.visitEnd();


		// Append the function f.
		mv = cw.visitMethod(ACC_PUBLIC, "f", determineMethodDescriptor(Object.class), null, null);
		mv.visitCode();

		appendReadVariableValues();

		node.accept(this);

		appendStoreVariableValues();

		mv.visitInsn(ARETURN);

		mv.visitMaxs(0, 1);
		mv.visitEnd();

		cw.visitEnd();

		return cw.toByteArray();
	}

	@Override
	public Void visit(AddNode node) throws TranslationException {
		return null;
	}

	@Override
	public Void visit(AndNode node) throws TranslationException {
		appendLineNumberLabel(node);

		appendOrAnd(node, false);

		return null;
	}

	@Override
	public Void visit(ArrayNode node) throws TranslationException {
		appendLineNumberLabel(node);

		unreferenceArray(node, null, null);

		return null;
	}

	@Override
	public Void visit(AssignmentNode node) throws TranslationException {
		appendLineNumberLabel(node);

		ValueAssigner valueAssigner = new ValueAssigner();
		valueAssigner.assign(node.getLhs(), node.getRhs(), node.getType());

		return null;
	}

	@Override
	public Void visit(BooleanNode node) throws TranslationException {
		appendLineNumberLabel(node);

		if (node.getValue() == null) {
			mv.visitInsn(ACONST_NULL);
			return null;
		}

		mv.visitLdcInsn(node.getValue());
		mv.visitMethodInsn(INVOKESTATIC, determineClassName(Boolean.class), "valueOf", "(Z)Ljava/lang/Boolean;", false);

		return null;
	}

	@Override
	public Void visit(ConcatStringNode node) throws TranslationException {
		appendLineNumberLabel(node);

		node.getLhs().accept(this);
		node.getRhs().accept(this);

		mv.visitMethodInsn(INVOKESTATIC, determineClassName(StringOperation.class), "concat",
				determineMethodDescriptor(String.class, String.class, String.class), false);

		return null;
	}

	@Override
	public Void visit(DateNode node) throws TranslationException {
		appendLineNumberLabel(node);

		if (node.getValue() == null) {
			mv.visitInsn(ACONST_NULL);
			return null;
		}

		mv.visitTypeInsn(NEW, determineClassName(Date.class));
		mv.visitInsn(DUP);
		mv.visitLdcInsn(node.getValue().getTime());
		mv.visitMethodInsn(INVOKESPECIAL, determineClassName(Date.class), "<init>", "(J)V", false);

		return null;
	}

	@Override
	public Void visit(DecimalNode node) throws TranslationException {
		appendLineNumberLabel(node);

		if (node.getValue() == null) {
			mv.visitInsn(ACONST_NULL);
			return null;
		}

		mv.visitTypeInsn(NEW, determineClassName(BigDecimal.class));
		mv.visitInsn(DUP);
		mv.visitLdcInsn(node.getValue().toString());
		mv.visitMethodInsn(INVOKESPECIAL, determineClassName(BigDecimal.class), "<init>",
				determineMethodDescriptor(null, String.class), false);

		return null;
	}

	@Override
	public Void visit(DivideNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendBinaryArithmeticOperation(node, "divide");
		return null;
	}

	@Override
	public Void visit(EqNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendComparisonNode(node, "EQUALS");
		return null;
	}

	@Override
	public Void visit(FactorNode node) throws TranslationException {
		node.getArgument().accept(this);
		return null;
	}

	@Override
	public Void visit(FunctionNode node) throws TranslationException {
		mv.visitVarInsn(ALOAD, 0);

		ArrayList<Class> classes = new ArrayList<>();

		for (ExpressionNode parameter : node.getParameters()) {
			parameter.accept(this);
			classes.add(determineJavaClass(parameter.getType()));
		}

		mv.visitMethodInsn(INVOKEVIRTUAL, determineClassName(generatedClassName), "function_" + node.getIdentifier(),
				determineMethodDescriptor(determineJavaClass(node.getType()), classes.toArray(new Class[classes.size()])),
				false);

		return null;
	}

	@Override
	public Void visit(GeqNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendComparisonNode(node, "GREATER_THAN_EQUALS");
		return null;
	}

	@Override
	public Void visit(GtNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendComparisonNode(node, "GREATER_THAN");
		return null;
	}

	@Override
	public Void visit(IfNode node) throws TranslationException {
		appendLineNumberLabel(node);

		node.getCondition().accept(this);

		mv.visitInsn(DUP);                              // place the condition twice on the stack

		Label l1 = new Label();
		mv.visitJumpInsn(IFNULL, l1);                   // if condition is null --> jump to 1
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
		Label l2 = new Label();
		mv.visitJumpInsn(IFEQ, l2);                     // if condition is false --> jump to 2

		node.getIfBody().accept(this);
		appendCast(node.getIfBody().getType(), node.getType());

		Label l3 = new Label();

		mv.visitJumpInsn(GOTO, l3);                     // goto label 3

		mv.visitLabel(l2);                              // define label 2

		node.getElseBody().accept(this);
		appendCast(node.getElseBody().getType(), node.getType());

		mv.visitJumpInsn(GOTO, l3);                     // goto label 3

		mv.visitLabel(l1);                              // define label 1
		mv.visitInsn(POP);                              // pop the last condition from the stack, we don't need it
		mv.visitInsn(ACONST_NULL);                      // place null on the stack

		mv.visitLabel(l3);                              // define label 3
		return null;
	}

	@Override
	public Void visit(IntegerNode node) throws TranslationException {
		appendLineNumberLabel(node);

		if (node.getValue() == null) {
			mv.visitInsn(ACONST_NULL);
			return null;
		}
		mv.visitLdcInsn(node.getValue());
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
		return null;
	}

	@Override
	public Void visit(LeqNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendComparisonNode(node, "LESS_THAN_EQUALS");
		return null;
	}

	@Override
	public Void visit(LtNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendComparisonNode(node, "LESS_THAN");
		return null;
	}

	@Override
	public Void visit(ModuloNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendBinaryArithmeticOperation(node, "modulo");
		return null;
	}

	@Override
	public Void visit(MultiplyNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendBinaryArithmeticOperation(node, "multiply");
		return null;
	}

	@Override
	public Void visit(NegativeNode node) throws TranslationException {
		appendLineNumberLabel(node);
		node.getArgument().accept(this);

		mv.visitMethodInsn(INVOKESTATIC,
				determineClassName(ArithmeticOperation.class),
				"negate",
				determineMethodDescriptor(
						determineJavaClass(node.getType()),
						determineJavaClass(node.getArgument().getType())
				),
				false);
		return null;
	}

	@Override
	public Void visit(NeqNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendComparisonNode(node, "NOT_EQUALS");
		return null;
	}

	@Override
	public Void visit(NotNode node) throws TranslationException {
		appendLineNumberLabel(node);
		node.getArgument().accept(this);

		mv.visitMethodInsn(INVOKESTATIC,
				determineClassName(BooleanOperation.class),
				"not",
				determineMethodDescriptor(Boolean.class, Boolean.class),
				false);
		return null;
	}

	@Override
	public Void visit(NullNode node) throws TranslationException {
		mv.visitInsn(ACONST_NULL);
		return null;
	}

	@Override
	public Void visit(OrNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendOrAnd(node, true);
		return null;
	}

	@Override
	public Void visit(PositiveNode node) throws TranslationException {
		return null;
	}

	@Override
	public Void visit(PowerNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendBinaryArithmeticOperation(node, "power");
		return null;
	}

	@Override
	public Void visit(RootNode node) throws TranslationException {
		return null;
	}

	@Override
	public Void visit(StatementListNode node) throws TranslationException {
		Iterator<Node> it = node.iterator();
		while (it.hasNext()) {
			Node subNode = it.next();

			subNode.accept(this);

			if (it.hasNext() || subNode.getNodeType() != NodeType.EXPRESSION) {
				mv.visitInsn(POP);
			}
		}
		return null;
	}

	@Override
	public Void visit(StringNode node) throws TranslationException {
		appendLineNumberLabel(node);
		if (node.getValue() == null) {
			mv.visitInsn(ACONST_NULL);
			return null;
		}
		mv.visitLdcInsn(node.getValue());
		return null;
	}

	@Override
	public Void visit(SubtractNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendBinaryArithmeticOperation(node, "subtract");
		return null;
	}

	@Override
	public Void visit(SumNode node) throws TranslationException {
		appendLineNumberLabel(node);
		appendBinaryArithmeticOperation(node, "sum");
		return null;
	}

	@Override
	public Void visit(VariableNode node) throws TranslationException {
		appendLineNumberLabel(node);
		mv.visitVarInsn(ALOAD, methodVariableIndexes.get(node.getIdentifier()));
		return null;
	}

	/**
	 * Assigns a value to a variable or to an index.
	 */
	private class ValueAssigner extends DefaultVisitor<Void, TranslationException> {

		/**
		 * The value to assign.
		 */
		private ExpressionNode value;

		/**
		 * The type of the assignment.
		 */
		private Type type;

		public ValueAssigner() {
			// Don't visit the child nodes.
			visitChildNodes = false;
		}

		/**
		 * Assigns the value to the correct variable or array-index.
		 * @param node The node that determines where to assign the value to.
		 * @param value The value to assign.
		 * @throws TranslationException
		 */
		public void assign(ExpressionNode node, ExpressionNode value, Type type) throws TranslationException {
			this.value = value;
			this.type = type;
			node.accept(this);
		}

		@Override
		public Void visit(ArrayNode node) throws TranslationException {

			unreferenceArray(node, value, type);

			return null;
		}

		@Override
		public Void visit(VariableNode node) throws TranslationException {
			// Get the value.
			value.accept(ClassTranslator.this);
			// Cast the value to the correct type.
			appendCast(value.getType(), type);
			// Duplicate the value.
			mv.visitInsn(DUP);
			// Store the value in the variable with index.
			mv.visitVarInsn(ASTORE, methodVariableIndexes.get(node.getIdentifier()));

			return null;
		}
	}

	/**
	 * Unreferences an array and either returns the value, or assigns a new value.
	 * @param node The array node to unreference.
	 * @param assignValue The value to assign.
	 * @param type The type of the assignment.
	 * @throws TranslationException
	 */
	private void unreferenceArray(ArrayNode node, ExpressionNode assignValue, Type type) throws TranslationException {
		Label labelPop1 = new Label();
		Label labelPop2 = new Label();
		Label labelPop4 = new Label();
		Label labelEnd = new Label();

		node.getIndex().accept(this);                   // Get the index.
		node.getArray().accept(this);                   // Get the array.

		mv.visitInsn(DUP_X1);                           // Duplicate the index
		mv.visitJumpInsn(IFNULL, labelPop2);            // If null, jump to pop2

		mv.visitInsn(DUP);                              // Duplicate the array
		mv.visitJumpInsn(IFNULL, labelPop2);            // If null, jump to pop1

		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "intValue", "()I", false);
		mv.visitInsn(DUP_X1);                           // Duplicate the index
		mv.visitInsn(DUP2_X1);                          // Duplicate the index and the array and push to the bottom

		mv.visitJumpInsn(IFLT, labelPop4);              // If the index is less then 0, jump to pop4

		mv.visitInsn(ARRAYLENGTH);                      // Get the length of the array
		mv.visitJumpInsn(IF_ICMPGE, labelPop2);         // If the length is LE to the index jump to pop2

		// Assign the value if one was passed to the method.
		if (assignValue != null) {
			// Determine the value to assign.
			assignValue.accept(this);
			// Cast the value to the correct type.
			appendCast(assignValue.getType(), type);
			// Duplicate the value.
			mv.visitInsn(DUP_X2);
			// Assign the value.
			mv.visitInsn(AASTORE);

		// Otherwise, we need to load the value.
		} else {
			mv.visitInsn(AALOAD);
		}

		mv.visitJumpInsn(GOTO, labelEnd);               // Done! Go to the end!

		mv.visitLabel(labelPop4);                       // We need to pop 4 items
		mv.visitInsn(POP2);                             // Pop the first 2 items
		mv.visitLabel(labelPop2);                       // Now we only need to pop 2 items
		mv.visitInsn(POP);                              // Pop the item
		mv.visitLabel(labelPop1);                       // Now we only need to pop 1 item
		mv.visitInsn(POP);                              // Pop the last item

		mv.visitInsn(ACONST_NULL);                      // Set null on the stack

		mv.visitLabel(labelEnd);                        // The end!
	}

	/**
	 * Appends a cast from one type to another.
	 * @param fromType The type to cast from.
	 * @param toType The type to cast to.
	 */
	private void appendCast(Type fromType, Type toType) {
		if (Type.equals(toType, fromType) || fromType == null) {
			return;
		}

		String operation;

		if (Type.equals(fromType, Type.DECIMAL) && Type.equals(toType, Type.INTEGER)) {
			operation = "toInteger";
		} else if (Type.equals(fromType, Type.INTEGER) && Type.equals(toType, Type.DECIMAL)) {
			operation = "toDecimal";
		} else {
			throw new IllegalArgumentException("Unsupported types: " + fromType + ", " + toType);
		}

		mv.visitMethodInsn(INVOKESTATIC,
				determineClassName(CastHelper.class),
				operation,
				determineMethodDescriptor(
						determineJavaClass(toType),
						determineJavaClass(fromType)
				),
				false);
	}

	/**
	 * Appends the or-operation, or appends the and-operation.
	 * @param node The node to append, which is an binary operation node since both the OrNode and the AndNode are.
	 * @param isOr if {@code true} then the operation to append is the or-operation, otherwise it is the and-operation.
	 * @throws TranslationException
	 */
	private void appendOrAnd(BinaryOperationNode node, boolean isOr) throws TranslationException {
		node.getLhs().accept(this);

		mv.visitInsn(DUP);                              // place three lhs es on the stack
		mv.visitInsn(DUP);

		Label l1 = new Label();
		mv.visitJumpInsn(IFNULL, l1);                   // if lhs is null --> jump to 1
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
		Label l2 = new Label();
		mv.visitJumpInsn(isOr ? IFEQ : IFNE, l2);       // if lhs is 0 --> jump to 2
		mv.visitInsn(POP);                              // pop the last lhs from the stack (we don't need it).
		mv.visitInsn(isOr ? ICONST_1 : ICONST_0);       // place integer 1 on the stack
		Label l3 = new Label();
		mv.visitJumpInsn(GOTO, l3);                     // goto label 3

		mv.visitLabel(l1);                              // same as label 2, but performs an extra pop
		mv.visitInsn(POP);                              // pop an lhs from the stack, after this one lhs is left
		mv.visitLabel(l2);

		node.getRhs().accept(this);

		mv.visitInsn(DUP);                              // place three rhs es on the stack
		mv.visitInsn(DUP);

		Label l4 = new Label();
		mv.visitJumpInsn(IFNULL, l4);                   // if rhs is null --> jump to 4
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
		Label l5 = new Label();
		mv.visitJumpInsn(isOr ? IFEQ : IFNE, l5);       // if rhs is 0 --> jump to 5
		mv.visitInsn(POP);                              // pop the last rhs from the stack (we don't need it)
		mv.visitInsn(POP);                              // pop the last lhs from the stack (we don't need it)
		mv.visitInsn(isOr ? ICONST_1 : ICONST_0);       // place integer 1 on the stack
		mv.visitJumpInsn(GOTO, l3);                     // goto label 3

		mv.visitLabel(l4);
		mv.visitInsn(POP);                              // pop an rhs from the stack, after this one rhs and one lhs are left
		mv.visitLabel(l5);

		Label l6 = new Label();
		mv.visitJumpInsn(IFNULL, l6);                   // if rhs is null --> jump to 6
		Label l7 = new Label();
		mv.visitJumpInsn(IFNULL, l7);                   // if lhs is null --> jump to 7

		mv.visitInsn(isOr ? ICONST_0 : ICONST_1);       // place integer 0 on the stack
		mv.visitJumpInsn(GOTO, l3);                     // goto label 3

		mv.visitLabel(l6);
		mv.visitInsn(POP);                              // pop the last lhs from the stack (we don't need it)
		mv.visitLabel(l7);
		mv.visitInsn(ACONST_NULL);                      // place null on the stack
		Label l8 = new Label();
		mv.visitJumpInsn(GOTO, l8);

		mv.visitLabel(l3);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);

		mv.visitLabel(l8);
	}

	/**
	 * Appends a comparison node.
	 * @param node The node to append.
	 * @param comparator The string name of the operation (the name of the static
	 * {@link BooleanOperation.ComparisonEvaluator} in {@link BooleanOperation}.
	 * @throws TranslationException
	 */
	private void appendComparisonNode(AbstractComparisonNode node, String comparator) throws TranslationException {
		node.getLhs().accept(this);
		node.getRhs().accept(this);

		mv.visitFieldInsn(GETSTATIC,
				determineClassName(BooleanOperation.class),
				comparator,
				determineFieldDescriptor(BooleanOperation.ComparisonEvaluator.class));

		Type lhsType = node.getLhs().getType() != null ? node.getLhs().getType() : node.getRhs().getType();
		Type rhsType = node.getRhs().getType() != null ? node.getRhs().getType() : node.getLhs().getType();

		mv.visitMethodInsn(INVOKESTATIC,
				determineClassName(BooleanOperation.class),
				"compare",
				determineMethodDescriptor(
						Boolean.class,
						determineJavaClass(lhsType),
						determineJavaClass(rhsType),
						BooleanOperation.ComparisonEvaluator.class),
				false);
	}

	/**
	 * Appends a binary arithmetic operation.
	 * @param node A binary operation node.
	 * @param operation The name of the operation (the name of the method in {@link ArithmeticOperation}.
	 * @throws TranslationException
	 */
	private void appendBinaryArithmeticOperation(BinaryOperationNode node, String operation) throws TranslationException {
		node.getLhs().accept(this);
		node.getRhs().accept(this);

		if (Type.equals(node.getType(), Type.DECIMAL)) {
			appendGetMathContext();
		}

		mv.visitMethodInsn(INVOKESTATIC,
				determineClassName(ArithmeticOperation.class),
				operation,
				determineMethodDescriptor(
						determineJavaClass(node.getType()),
						determineJavaClass(node.getLhs().getType()),
						determineJavaClass(node.getRhs().getType()),
						Type.equals(node.getType(), Type.DECIMAL) ? MathContext.class : null
				),
				false);
	}

	/**
	 * Appends a call to {@link AbacusContext#getMathContext()}. Effectively pushing the math context on the stack.
	 */
	private void appendGetMathContext() {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, determineClassName(generatedClassName), "abacusContext", determineFieldDescriptor(AbacusContext.class));
		mv.visitMethodInsn(INVOKEINTERFACE, determineClassName(AbacusContext.class), "getMathContext",
				determineMethodDescriptor(MathContext.class), true);
	}

	/**
	 * Appends a call to {@link AbacusContext#getSymbolTable()}. Effectively pushing the symbol table on the stack.
	 */
	private void appendGetSymbolTable() {
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, determineClassName(generatedClassName), "abacusContext", determineFieldDescriptor(AbacusContext.class));
		mv.visitMethodInsn(INVOKEINTERFACE, determineClassName(AbacusContext.class), "getSymbolTable",
				determineMethodDescriptor(SymbolTable.class), true);
	}

	/**
	 * Reads all variables that are read at any time from the symbol table and place them in the method variable array,
	 * using the index that was generated in {@link #readMethodVariables(Node)}.
	 */
	private void appendReadVariableValues() {
		if (readVariableList.isEmpty()) {
			return;
		}

		Label startLabel = new Label();
		Label endLabel = new Label();
		Label handlerLabel = new Label();
		mv.visitTryCatchBlock(startLabel, endLabel, handlerLabel, determineClassName(NoSuchVariableException.class));
		mv.visitLabel(startLabel);

		for (Variable variable : readVariableList) {
			appendGetSymbolTable();

			mv.visitLdcInsn(variable.getIdentifier());
			mv.visitMethodInsn(INVOKEINTERFACE,
					determineClassName(SymbolTable.class),
					"getVariableValue",
					determineMethodDescriptor(Object.class, String.class),
					true);
			mv.visitTypeInsn(CHECKCAST, determineClassName(variable.getType()));
			mv.visitVarInsn(ASTORE, methodVariableIndexes.get(variable.getIdentifier()));
		}

		mv.visitLabel(endLabel);
		Label successLabel = new Label();
		mv.visitJumpInsn(GOTO, successLabel);
		mv.visitLabel(handlerLabel);
		mv.visitFrame(F_SAME1, 0, null, 1, new Object[]{determineClassName(NoSuchVariableException.class)});
		mv.visitTypeInsn(NEW, determineClassName(AbacusRuntimeException.class));
		mv.visitInsn(DUP_X1);
		mv.visitInsn(SWAP);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKEVIRTUAL,
				determineClassName(Throwable.class),
				"getMessage",
				determineMethodDescriptor(String.class),
				false);
		mv.visitInsn(SWAP);
		mv.visitInsn(ICONST_0);
		mv.visitInsn(SWAP);
		mv.visitInsn(ICONST_0);
		mv.visitInsn(SWAP);
		mv.visitMethodInsn(INVOKESPECIAL, determineClassName(AbacusRuntimeException.class), "<init>",
				"(Ljava/lang/String;IILjava/lang/Exception;)V", false);
		mv.visitInsn(ATHROW);
		mv.visitLabel(successLabel);
	}

	/**
	 * Stores all variables that are written at any time frin the method variable array to the symbol table.
	 */
	private void appendStoreVariableValues() {
		if (storeVariableList.isEmpty()) {
			return;
		}

		Label startLabel = new Label();
		Label endLabel = new Label();
		Label handlerLabel = new Label();
		mv.visitTryCatchBlock(startLabel, endLabel, handlerLabel, determineClassName(NoSuchVariableException.class));
		mv.visitLabel(startLabel);

		for (Variable variable : storeVariableList) {
			appendGetSymbolTable();

			mv.visitLdcInsn(variable.getIdentifier());
			mv.visitVarInsn(ALOAD, methodVariableIndexes.get(variable.getIdentifier()));

			mv.visitMethodInsn(INVOKEINTERFACE,
					determineClassName(SymbolTable.class),
					"setVariableValue",
					determineMethodDescriptor(null, String.class, Object.class),
					true);
		}

		mv.visitLabel(endLabel);
		Label successLabel = new Label();
		mv.visitJumpInsn(GOTO, successLabel);
		mv.visitLabel(handlerLabel);
		mv.visitFrame(F_SAME1, 0, null, 1, new Object[]{determineClassName(Exception.class)});
		mv.visitTypeInsn(NEW, determineClassName(AbacusRuntimeException.class));
		mv.visitInsn(DUP_X1);
		mv.visitInsn(SWAP);
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKEVIRTUAL,
				determineClassName(Throwable.class),
				"getMessage",
				determineMethodDescriptor(String.class),
				false);
		mv.visitInsn(SWAP);
		mv.visitInsn(ICONST_0);
		mv.visitInsn(SWAP);
		mv.visitInsn(ICONST_0);
		mv.visitInsn(SWAP);
		mv.visitMethodInsn(INVOKESPECIAL, determineClassName(AbacusRuntimeException.class), "<init>",
				"(Ljava/lang/String;IILjava/lang/Exception;)V", false);
		mv.visitInsn(ATHROW);
		mv.visitLabel(successLabel);
	}

	/**
	 * Determines the Java type from the {@link Type}.
	 * @param type The {@link Type} to translate into a Java type.
	 * @return The Java type.
	 */
	private static Class determineJavaClass(Type type) {
		return JavaTypeHelper.determineJavaType(type);
	}

	/**
	 * Appends a label with the line number of the token from which the node was created.
	 */
	private void appendLineNumberLabel(Node node) {
		if (lineNumber == node.getToken().getLineNumber()) {
			return;
		}

		lineNumber = node.getToken().getLineNumber();

		Label label = new Label();
		mv.visitLabel(label);
		mv.visitLineNumber(lineNumber, label);
	}

	/**
	 * Determines the name of the class in the format with slashes.
	 * @param name The class name.
	 * @return The class name, where dots are replaced with slashes.
	 */
	private static String determineClassName(String name) {
		return name.replaceAll("\\.", "/");
	}

	/**
	 * Determines the name of the class from the given class.
	 * @param clazz The class.
	 * @return The name of the class.
	 */
	private static String determineClassName(Class clazz) {
		return determineClassName(clazz.getName());
	}

	/**
	 * Determines the name of the class that represents the given type.
	 * @param type The type.
	 * @return The name of the class.
	 */
	private static String determineClassName(Type type) {
		return determineClassName(determineJavaClass(type));
	}

	/**
	 * Determines the field descriptor representing the given class.
	 * @param clazz The class.
	 * @return The field descriptor.
	 */
	private static String determineFieldDescriptor(Class clazz) {
		return "L" + determineClassName(clazz) + ";";
	}

	/**
	 * Determines the method descriptor represented by the given classes.
	 * @param returnClass The type of the object returned by the method.
	 * @param argumentsClasses The types of the arguments of the method.
	 * @return The method descriptor.
	 */
	private static String determineMethodDescriptor(Class returnClass, Class... argumentsClasses) {
		StringBuilder descriptor = new StringBuilder();
		descriptor.append("(");
		for (Class clazz : argumentsClasses) {
			if (clazz == null) {
				continue;
			}
			descriptor.append(determineFieldDescriptor(clazz));
		}
		descriptor.append(")");
		if (returnClass == null) {
			return descriptor.append("V").toString();
		}
		return descriptor.append(determineFieldDescriptor(returnClass)).toString();
	}
}
