/*******************************************************************************
 * Copyright (c) 2004, 2020 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM - Initial API and implementation
 *     Anton Leherbauer (Wind River Systems)
 *     Markus Schorn (Wind River Systems)
 *     Alexander Fedorov (ArSysOp) - Bug 561992
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.internal.core.parser.ParserMessages;

/**
 * Models problems, all problems should derive from this class.
 */
public class ASTProblem extends ASTNode implements IASTProblem {
	protected static final Map<Integer, String> errorMessages;
	static {
		errorMessages = new HashMap<>();
		errorMessages.put(Integer.valueOf(PREPROCESSOR_POUND_ERROR),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.error")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_POUND_WARNING),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.warning")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_INCLUSION_NOT_FOUND),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.inclusionNotFound")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_DEFINITION_NOT_FOUND),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.definitionNotFound")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_INVALID_MACRO_DEFN),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.invalidMacroDefn")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_INVALID_MACRO_REDEFN),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.invalidMacroRedefn")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_UNBALANCE_CONDITION),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.unbalancedConditional")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_CONDITIONAL_EVAL_ERROR),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.conditionalEval")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_MACRO_USAGE_ERROR),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.macroUsage")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_CIRCULAR_INCLUSION),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.circularInclusion")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_INVALID_DIRECTIVE),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.invalidDirective")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_MACRO_PASTING_ERROR),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.macroPasting")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_MISSING_RPAREN_PARMLIST),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.missingRParen")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_INVALID_VA_ARGS),
				ParserMessages.getString("ScannerProblemFactory.error.preproc.invalidVaArgs")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_INVALID_ESCAPECHAR),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.invalidEscapeChar")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_UNBOUNDED_STRING),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.unboundedString")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_BAD_FLOATING_POINT),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.badFloatingPoint")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_BAD_BINARY_FORMAT),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.badBinaryFormat")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_BAD_HEX_FORMAT),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.badHexFormat")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_BAD_OCTAL_FORMAT),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.badOctalFormat")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_BAD_DECIMAL_FORMAT),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.badDecimalFormat")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_ASSIGNMENT_NOT_ALLOWED),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.assignmentNotAllowed")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_DIVIDE_BY_ZERO),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.divideByZero")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_MISSING_R_PAREN),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.missingRParen")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_EXPRESSION_SYNTAX_ERROR),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.expressionSyntaxError")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_ILLEGAL_IDENTIFIER),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.illegalIdentifier")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_BAD_CONDITIONAL_EXPRESSION),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.badConditionalExpression")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_UNEXPECTED_EOF),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.unexpectedEOF")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_BAD_CHARACTER),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.badCharacter")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_CONSTANT_WITH_BAD_SUFFIX),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.constantWithBadSuffix")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SCANNER_FLOAT_WITH_BAD_PREFIX),
				ParserMessages.getString("ScannerProblemFactory.error.scanner.floatWithBadPrefix")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(PREPROCESSOR_MULTIPLE_USER_DEFINED_SUFFIXES_IN_CONCATENATION), ParserMessages
				.getString("ScannerProblemFactory.error.preproc.multipleUserDefinedLiteralSuffixesOnStringLiteral")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(SYNTAX_ERROR),
				ParserMessages.getString("ParserProblemFactory.error.syntax.syntaxError")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(MISSING_SEMICOLON),
				ParserMessages.getString("ParserProblemFactory.error.syntax.missingSemicolon")); //$NON-NLS-1$
		errorMessages.put(Integer.valueOf(TEMPLATE_ARGUMENT_NESTING_DEPTH_LIMIT_EXCEEDED), ParserMessages
				.getString("ParserProblemFactory.error.syntax.templateArgumentNestingDepthLimitExceeded")); //$NON-NLS-1$
	}

	private final int id;
	private final char[] arg;
	private boolean isError;
	private IASTProblem originalProblem = null;

	public ASTProblem(IASTNode parent, ASTNodeProperty property, int id, char[] arg, boolean isError, int startNumber,
			int endNumber) {
		setParent(parent);
		setPropertyInParent(property);
		setOffset(startNumber);
		setLength(endNumber - startNumber);

		this.id = id;
		this.arg = arg;
		this.isError = isError;
	}

	public ASTProblem(int id, char[] arg, boolean isError) {
		this.id = id;
		this.arg = arg;
		this.isError = isError;
	}

	@Override
	public ASTProblem copy() {
		return copy(CopyStyle.withoutLocations);
	}

	@Override
	public ASTProblem copy(CopyStyle style) {
		ASTProblem copy = new ASTProblem(id, arg == null ? null : arg.clone(), isError);
		return copy(copy, style);
	}

	@Override
	public int getID() {
		return id;
	}

	@Override
	public boolean isError() {
		return isError;
	}

	@Override
	public boolean isWarning() {
		return !isError;
	}

	@Override
	public String getMessageWithLocation() {
		String msg = getMessage();

		char[] file = getOriginatingFileName();
		int line = getSourceLineNumber();
		Object[] args = new Object[] { msg, new String(file), Integer.valueOf(line) };
		return ParserMessages.getFormattedString("BaseProblemFactory.problemPattern", args); //$NON-NLS-1$
	}

	private static String getMessage(int id, String arg, IASTProblem originalProblem) {
		String msg = errorMessages.get(Integer.valueOf(id));
		if (msg == null)
			msg = ""; //$NON-NLS-1$

		if (arg != null) {
			msg = MessageFormat.format(msg, new Object[] { arg });
		}
		if (originalProblem != null) {
			msg = MessageFormat.format("{0}: {1}", msg, originalProblem.getMessage()); //$NON-NLS-1$
		}
		return msg;
	}

	public static String getMessage(int id, String arg) {
		return getMessage(id, arg, null);
	}

	@Override
	public String getMessage() {
		return getMessage(id, arg == null ? null : new String(arg), originalProblem);
	}

	@Override
	public boolean checkCategory(int bitmask) {
		return (id & bitmask) != 0;
	}

	@Override
	public String[] getArguments() {
		return arg == null ? new String[0] : new String[] { new String(arg) };
	}

	public char[] getArgument() {
		return arg;
	}

	@Override
	public char[] getOriginatingFileName() {
		return getContainingFilename().toCharArray();
	}

	@Override
	public int getSourceEnd() {
		final IASTFileLocation location = getFileLocation();
		if (location != null) {
			return location.getNodeOffset() + location.getNodeLength() - 1;
		}
		return INT_VALUE_NOT_PROVIDED;
	}

	@Override
	public int getSourceLineNumber() {
		final IASTFileLocation location = getFileLocation();
		if (location != null) {
			return location.getStartingLineNumber();
		}
		return INT_VALUE_NOT_PROVIDED;
	}

	@Override
	public int getSourceStart() {
		final IASTFileLocation location = getFileLocation();
		if (location != null) {
			return location.getNodeOffset();
		}
		return INT_VALUE_NOT_PROVIDED;
	}

	@Override
	public IASTProblem getOriginalProblem() {
		return originalProblem;
	}

	@Override
	public void setOriginalProblem(IASTProblem original) {
		originalProblem = original;
	}
}
