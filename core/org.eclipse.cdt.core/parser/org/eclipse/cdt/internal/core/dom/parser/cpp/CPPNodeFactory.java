/*******************************************************************************
 * Copyright (c) 2006, 2016 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Mike Kucera (IBM) - initial API and implementation
 *     Markus Schorn (Wind River Systems)
 *     Thomas Corbat (IFS)
 *     Anders Dahlberg (Ericsson) - bug 84144
 *     Sergey Prigogin (Google)
 *******************************************************************************/
package org.eclipse.cdt.internal.core.dom.parser.cpp;

import org.eclipse.cdt.core.dom.ast.IASTASMDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTAlignmentSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTBinaryTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTContinueStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDefaultStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTProblemExpression;
import org.eclipse.cdt.core.dom.ast.IASTProblemStatement;
import org.eclipse.cdt.core.dom.ast.IASTProblemTypeId;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTToken;
import org.eclipse.cdt.core.dom.ast.IASTTokenList;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdInitializerExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAliasDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArrayDesignator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAttribute;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTAttributeList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCapture;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCastExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCatchHandler;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTClassVirtSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConversionName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDecltypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeleteExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDesignatedInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTEnumerationSpecifier.ScopeStyle;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExplicitTemplateInstantiation;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTExpressionList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldDesignator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFieldReference;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFoldExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator.RefQualifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionWithTryBlock;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTIfStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitCapture;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerClause;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTInitializerList;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLambdaExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLinkageSpecification;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceAlias;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNaryTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNaryTypeIdExpression.Operator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNewExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTOperatorName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTPackExpansionExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTPointerToMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTRangeBasedForStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTReferenceOperator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeConstructorExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSimpleTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTStaticAssertDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTStructuredBindingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplateSpecialization;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTemplatedTypeTemplateParameter;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTryBlockStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypeTransformationSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVirtSpecifier.SpecifierKind;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPUnaryTypeTransformation;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTCompoundStatementExpression;
import org.eclipse.cdt.core.dom.ast.gnu.cpp.IGPPASTArrayRangeDesignator;
import org.eclipse.cdt.core.dom.parser.cpp.ICPPASTAttributeSpecifier;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.internal.core.dom.parser.ASTToken;
import org.eclipse.cdt.internal.core.dom.parser.ASTTokenList;
import org.eclipse.cdt.internal.core.dom.parser.IASTInactiveCompletionName;
import org.eclipse.cdt.internal.core.dom.parser.NodeFactory;
import org.eclipse.cdt.internal.core.parser.scanner.CPreprocessor;

/**
 * Abstract factory implementation that creates C++ AST nodes.
 */
public class CPPNodeFactory extends NodeFactory implements ICPPNodeFactory {
	private static final CPPNodeFactory DEFAULT_INSTANCE = new CPPNodeFactory();

	public static CPPNodeFactory getDefault() {
		return DEFAULT_INSTANCE;
	}

	@Override
	public ICPPASTAliasDeclaration newAliasDeclaration(IASTName aliasName, ICPPASTTypeId mappingTypeId) {
		return new CPPASTAliasDeclaration(aliasName, mappingTypeId);
	}

	@Override
	public IASTAlignmentSpecifier newAlignmentSpecifier(IASTExpression expression) {
		return new CPPASTAlignmentSpecifier(expression);
	}

	@Override
	public IASTAlignmentSpecifier newAlignmentSpecifier(IASTTypeId typeId) {
		return new CPPASTAlignmentSpecifier(typeId);
	}

	@Override
	public ICPPASTArrayDeclarator newArrayDeclarator(IASTName name) {
		return new CPPASTArrayDeclarator(name);
	}

	@Override
	public ICPPASTArrayDesignator newArrayDesignator(ICPPASTExpression exp) {
		return new CPPASTArrayDesignator(exp);
	}

	@Override
	public IGPPASTArrayRangeDesignator newArrayRangeDesignatorGPP(ICPPASTExpression floor, ICPPASTExpression ceiling) {
		return new CPPASTArrayRangeDesignator(floor, ceiling);
	}

	@Override
	public IASTArrayModifier newArrayModifier(IASTExpression expr) {
		return new CPPASTArrayModifier(expr);
	}

	@Override
	public ICPPASTArraySubscriptExpression newArraySubscriptExpression(IASTExpression arrayExpr,
			IASTExpression subscript) {
		return new CPPASTArraySubscriptExpression(arrayExpr, subscript);
	}

	@Override
	public ICPPASTArraySubscriptExpression newArraySubscriptExpression(IASTExpression arrayExpr,
			IASTInitializerClause subscript) {
		return new CPPASTArraySubscriptExpression(arrayExpr, subscript);
	}

	@Override
	public IASTASMDeclaration newASMDeclaration(String assembly) {
		return new CPPASTASMDeclaration(assembly);
	}

	@Override
	public ICPPASTAttribute newAttribute(char[] name, IASTToken argumentClause) {
		return newAttribute(name, null, argumentClause, false);
	}

	@Override
	public ICPPASTAttribute newAttribute(char[] name, char[] scope, IASTToken argumentClause, boolean packExpansion) {
		return new CPPASTAttribute(name, scope, argumentClause, packExpansion);
	}

	@Deprecated
	@Override
	public ICPPASTAttributeSpecifier newAttributeSpecifier() {
		return new CPPASTAttributeList();
	}

	@Override
	public ICPPASTAttributeList newAttributeList() {
		return new CPPASTAttributeList();
	}

	@Override
	@Deprecated
	public ICPPASTBaseSpecifier newBaseSpecifier(IASTName name, int visibility, boolean isVirtual) {
		return new CPPASTBaseSpecifier((ICPPASTName) name, visibility, isVirtual);
	}

	@Override
	public ICPPASTBaseSpecifier newBaseSpecifier(ICPPASTNameSpecifier nameSpecifier, int visibility,
			boolean isVirtual) {
		return new CPPASTBaseSpecifier(nameSpecifier, visibility, isVirtual);
	}

	@Override
	public ICPPASTBinaryExpression newBinaryExpression(int op, IASTExpression expr1, IASTExpression expr2) {
		return new CPPASTBinaryExpression(op, expr1, expr2);
	}

	@Override
	public ICPPASTBinaryExpression newBinaryExpression(int op, IASTExpression expr1, IASTInitializerClause expr2) {
		return new CPPASTBinaryExpression(op, expr1, expr2);
	}

	@Override
	public IASTExpression newBinaryTypeIdExpression(IASTBinaryTypeIdExpression.Operator op, IASTTypeId type1,
			IASTTypeId type2) {
		return new CPPASTBinaryTypeIdExpression(op, type1, type2);
	}

	@Override
	public IASTBreakStatement newBreakStatement() {
		return new CPPASTBreakStatement();
	}

	@Override
	public ICPPASTCapture newCapture() {
		return new CPPASTCapture();
	}

	@Override
	public ICPPASTInitCapture newInitCapture(ICPPASTDeclarator declarator) {
		return new CPPASTInitCapture(declarator);
	}

	@Override
	public IASTCaseStatement newCaseStatement(IASTExpression expr) {
		return new CPPASTCaseStatement(expr);
	}

	@Override
	public ICPPASTCastExpression newCastExpression(int operator, IASTTypeId typeId, IASTExpression operand) {
		return new CPPASTCastExpression(operator, typeId, operand);
	}

	@Override
	public ICPPASTCatchHandler newCatchHandler(IASTDeclaration decl, IASTStatement body) {
		return new CPPASTCatchHandler(decl, body);
	}

	@Override
	public ICPPASTClassVirtSpecifier newClassVirtSpecifier(ICPPASTClassVirtSpecifier.SpecifierKind kind) {
		// Don't use the 'kind' argument as there is currently only one kind.
		assert kind == ICPPASTClassVirtSpecifier.SpecifierKind.Final;
		return new CPPASTClassVirtSpecifier();
	}

	@Override
	public ICPPASTCompositeTypeSpecifier newCompositeTypeSpecifier(int key, IASTName name) {
		return new CPPASTCompositeTypeSpecifier(key, name);
	}

	@Override
	public IASTCompoundStatement newCompoundStatement() {
		return new CPPASTCompoundStatement();
	}

	@Override
	public IASTConditionalExpression newConditionalExpession(IASTExpression condition, IASTExpression positive,
			IASTExpression negative) {
		return new CPPASTConditionalExpression(condition, positive, negative);
	}

	@Override
	@Deprecated
	public ICPPASTConstructorChainInitializer newConstructorChainInitializer(IASTName id, IASTExpression expression) {
		ICPPASTConstructorChainInitializer result = new CPPASTConstructorChainInitializer(id, null);
		result.setInitializerValue(expression);
		return result;
	}

	@Override
	public ICPPASTConstructorChainInitializer newConstructorChainInitializer(IASTName id, IASTInitializer init) {
		return new CPPASTConstructorChainInitializer(id, init);
	}

	@Override
	@Deprecated
	public ICPPASTConstructorInitializer newConstructorInitializer(IASTExpression exp) {
		ICPPASTConstructorInitializer result = new CPPASTConstructorInitializer(null);
		result.setExpression(exp);
		return result;
	}

	@Override
	public ICPPASTConstructorInitializer newConstructorInitializer(IASTInitializerClause[] args) {
		return new CPPASTConstructorInitializer(args);
	}

	@Override
	public IASTContinueStatement newContinueStatement() {
		return new CPPASTContinueStatement();
	}

	@Override
	public ICPPASTConversionName newConversionName(IASTTypeId typeId) {
		return new CPPASTConversionName(typeId);
	}

	@Override
	public IASTDeclarationStatement newDeclarationStatement(IASTDeclaration declaration) {
		return new CPPASTDeclarationStatement(declaration);
	}

	@Override
	public ICPPASTDeclarator newDeclarator(IASTName name) {
		return new CPPASTDeclarator(name);
	}

	@Override
	public ICPPASTDecltypeSpecifier newDecltypeSpecifier(ICPPASTExpression decltypeExpression) {
		return new CPPASTDecltypeSpecifier(decltypeExpression);
	}

	@Override
	public IASTDefaultStatement newDefaultStatement() {
		return new CPPASTDefaultStatement();
	}

	@Override
	public ICPPASTDeleteExpression newDeleteExpression(IASTExpression operand) {
		return new CPPASTDeleteExpression(operand);
	}

	@Override
	public ICPPASTDesignatedInitializer newDesignatedInitializer(ICPPASTInitializerClause initializer) {
		return new CPPASTDesignatedInitializer(initializer);
	}

	@Override
	public IASTDoStatement newDoStatement(IASTStatement body, IASTExpression condition) {
		return new CPPASTDoStatement(body, condition);
	}

	@Override
	public ICPPASTElaboratedTypeSpecifier newElaboratedTypeSpecifier(int kind, IASTName name) {
		return new CPPASTElaboratedTypeSpecifier(kind, name);
	}

	@Override
	@Deprecated
	public ICPPASTEnumerationSpecifier newEnumerationSpecifier(boolean isScoped, IASTName name,
			ICPPASTDeclSpecifier baseType) {
		return new CPPASTEnumerationSpecifier(isScoped, name, baseType);
	}

	@Override
	public ICPPASTEnumerationSpecifier newEnumerationSpecifier(ScopeStyle scopeStyle, IASTName name,
			ICPPASTDeclSpecifier baseType) {
		return new CPPASTEnumerationSpecifier(scopeStyle, name, baseType);
	}

	@Override
	public ICPPASTEnumerationSpecifier newEnumerationSpecifier(IASTName name) {
		return new CPPASTEnumerationSpecifier(ScopeStyle.NONE, name, null);
	}

	@Override
	public IASTEnumerator newEnumerator(IASTName name, IASTExpression value) {
		return new CPPASTEnumerator(name, value);
	}

	@Override
	public IASTEqualsInitializer newEqualsInitializer(IASTInitializerClause initClause) {
		return new CPPASTEqualsInitializer(initClause);
	}

	@Override
	public ICPPASTExplicitTemplateInstantiation newExplicitTemplateInstantiation(IASTDeclaration declaration) {
		return new CPPASTExplicitTemplateInstantiation(declaration);
	}

	@Override
	public ICPPASTExpressionList newExpressionList() {
		return new CPPASTExpressionList();
	}

	@Override
	public IASTExpressionStatement newExpressionStatement(IASTExpression expression) {
		return new CPPASTExpressionStatement(expression);
	}

	@Override
	public ICPPASTFieldDeclarator newFieldDeclarator(IASTName name, IASTExpression bitFieldSize) {
		return new CPPASTFieldDeclarator(name, bitFieldSize);
	}

	@Override
	public ICPPASTFieldDesignator newFieldDesignator(IASTName name) {
		return new CPPASTFieldDesignator(name);
	}

	@Override
	public ICPPASTFieldReference newFieldReference(IASTName name, IASTExpression owner) {
		return new CPPASTFieldReference(name, owner);
	}

	@Override
	public ICPPASTForStatement newForStatement() {
		return new CPPASTForStatement();
	}

	@Override
	public ICPPASTForStatement newForStatement(IASTStatement init, IASTDeclaration condition,
			IASTExpression iterationExpression, IASTStatement body) {
		return new CPPASTForStatement(init, condition, iterationExpression, body);
	}

	@Override
	public ICPPASTForStatement newForStatement(IASTStatement init, IASTExpression condition,
			IASTExpression iterationExpr, IASTStatement body) {
		return new CPPASTForStatement(init, condition, iterationExpr, body);
	}

	@Override
	@Deprecated
	public ICPPASTFunctionCallExpression newFunctionCallExpression(IASTExpression idExpr, IASTExpression argList) {
		CPPASTFunctionCallExpression result = new CPPASTFunctionCallExpression(idExpr, null);
		if (argList == null) {
			result.setArguments(null);
		} else if (argList instanceof ICPPASTExpressionList) {
			result.setArguments(((ICPPASTExpressionList) argList).getExpressions());
		} else {
			result.setArguments(new IASTExpression[] { argList });
		}
		return result;
	}

	@Override
	public ICPPASTFunctionCallExpression newFunctionCallExpression(IASTExpression idExpr,
			IASTInitializerClause[] arguments) {
		return new CPPASTFunctionCallExpression(idExpr, arguments);
	}

	@Override
	public ICPPASTFunctionDeclarator newFunctionDeclarator(IASTName name) {
		return new CPPASTFunctionDeclarator(name);
	}

	@Override
	public ICPPASTFunctionDefinition newFunctionDefinition(IASTDeclSpecifier declSpecifier,
			IASTFunctionDeclarator declarator, IASTStatement bodyStatement) {
		return new CPPASTFunctionDefinition(declSpecifier, declarator, bodyStatement);
	}

	@Override
	public ICPPASTFunctionWithTryBlock newFunctionTryBlock(IASTDeclSpecifier declSpecifier,
			IASTFunctionDeclarator declarator, IASTStatement bodyStatement) {
		return new CPPASTFunctionWithTryBlock(declSpecifier, declarator, bodyStatement);
	}

	@Override
	public IGNUASTCompoundStatementExpression newGNUCompoundStatementExpression(
			IASTCompoundStatement compoundStatement) {
		return new CPPASTCompoundStatementExpression(compoundStatement);
	}

	@Override
	public IASTGotoStatement newGotoStatement(IASTName name) {
		return new CPPASTGotoStatement(name);
	}

	@Override
	public IASTStatement newGotoStatement(IASTExpression expression) {
		return new GNUCPPASTGotoStatement(expression);
	}

	@Override
	public IASTIdExpression newIdExpression(IASTName name) {
		return new CPPASTIdExpression(name);
	}

	@Override
	public ICPPASTIfStatement newIfStatement() {
		return new CPPASTIfStatement();
	}

	@Override
	public ICPPASTIfStatement newIfStatement(IASTDeclaration condition, IASTStatement then, IASTStatement elseClause) {
		return new CPPASTIfStatement(condition, then, elseClause);
	}

	@Override
	public ICPPASTIfStatement newIfStatement(IASTExpression condition, IASTStatement then, IASTStatement elseClause) {
		return new CPPASTIfStatement(condition, then, elseClause);
	}

	@Override
	public IASTInactiveCompletionName newInactiveCompletionName(char[] name, IASTTranslationUnit ast) {
		return new CPPASTInactiveCompletionName(name, ast);
	}

	@Override
	@Deprecated
	public org.eclipse.cdt.core.dom.ast.IASTInitializerExpression newInitializerExpression(IASTExpression expression) {
		return new CPPASTInitializerExpression(expression);
	}

	@Override
	public ICPPASTInitializerList newInitializerList() {
		return new CPPASTInitializerList();
	}

	@Override
	public IASTLabelStatement newLabelStatement(IASTName name, IASTStatement nestedStatement) {
		return new CPPASTLabelStatement(name, nestedStatement);
	}

	@Override
	public ICPPASTLambdaExpression newLambdaExpression() {
		return new CPPASTLambdaExpression();
	}

	@Override
	public IASTExpression newFoldExpressionToken() {
		return new CPPASTFoldExpressionToken();
	}

	@Override
	public ICPPASTFoldExpression newFoldExpression(int operator, boolean fIsComma, IASTExpression lhs,
			IASTExpression rhs) {
		return new CPPASTFoldExpression(operator, fIsComma, lhs, rhs);
	}

	@Override
	public ICPPASTLinkageSpecification newLinkageSpecification(String literal) {
		return new CPPASTLinkageSpecification(literal);
	}

	@Override
	public ICPPASTLiteralExpression newLiteralExpression(int kind, String rep) {
		return new CPPASTLiteralExpression(kind, rep.toCharArray(), false);
	}

	@Override
	public ICPPASTLiteralExpression newLiteralExpression(int kind, String rep, boolean useChar8Type) {
		return new CPPASTLiteralExpression(kind, rep.toCharArray(), useChar8Type);
	}

	@Override
	public ICPPASTLiteralExpression newLiteralExpression(int kind, String rep, char[] numericCompilerSuffixes) {
		return new CPPASTLiteralExpression(kind, rep.toCharArray(), numericCompilerSuffixes, false);
	}

	@Override
	public ICPPASTName newName() {
		return new CPPASTName();
	}

	@Override
	public ICPPASTName newName(char[] name) {
		return new CPPASTName(name);
	}

	@Override
	public ICPPASTName newName(String name) {
		return newName(name.toCharArray());
	}

	@Override
	public ICPPASTTemplateName newTemplateName(char[] templateName) {
		return new CPPASTTemplateName(templateName);
	}

	@Override
	public ICPPASTNamedTypeSpecifier newNamedTypeSpecifier(IASTName name) {
		return new CPPASTNamedTypeSpecifier(name);
	}

	@Override
	public ICPPASTNamespaceAlias newNamespaceAlias(IASTName alias, IASTName qualifiedName) {
		return new CPPASTNamespaceAlias(alias, qualifiedName);
	}

	@Override
	public ICPPASTNamespaceDefinition newNamespaceDefinition(IASTName name) {
		return new CPPASTNamespaceDefinition(name);
	}

	@Override
	public ICPPASTNaryTypeIdExpression newNaryTypeIdExpression(Operator operator, ICPPASTTypeId[] operands) {
		return new CPPASTNaryTypeIdExpression(operator, operands);
	}

	@Override
	@Deprecated
	public ICPPASTNewExpression newNewExpression(IASTExpression placement, IASTExpression initializer,
			IASTTypeId typeId) {
		final ICPPASTNewExpression result = new CPPASTNewExpression(null, null, typeId);
		result.setNewPlacement(placement);
		result.setNewInitializer(initializer);
		return result;
	}

	@Override
	public ICPPASTNewExpression newNewExpression(IASTInitializerClause[] placement, IASTInitializer initializer,
			IASTTypeId typeId) {
		return new CPPASTNewExpression(placement, initializer, typeId);
	}

	@Override
	public IASTNullStatement newNullStatement() {
		return new CPPASTNullStatement();
	}

	@Override
	public ICPPASTOperatorName newOperatorName(char[] name) {
		return new CPPASTOperatorName(name);
	}

	@Override
	public ICPPASTPackExpansionExpression newPackExpansionExpression(IASTExpression pattern) {
		return new CPPASTPackExpansionExpression(pattern);
	}

	@Override
	public ICPPASTParameterDeclaration newParameterDeclaration(IASTDeclSpecifier declSpec, IASTDeclarator declarator) {
		return new CPPASTParameterDeclaration(declSpec, declarator);
	}

	@Override
	public IASTPointer newPointer() {
		return new CPPASTPointer();
	}

	@Override
	public ICPPASTPointerToMember newPointerToMember(IASTName name) {
		return new CPPASTPointerToMember(name);
	}

	@Override
	public IASTProblem newProblem(int id, char[] arg, boolean error) {
		return new CPPASTProblem(id, arg, error);
	}

	@Override
	public IASTProblemDeclaration newProblemDeclaration(IASTProblem problem) {
		return new CPPASTProblemDeclaration(problem);
	}

	@Override
	public IASTProblemExpression newProblemExpression(IASTProblem problem) {
		return new CPPASTProblemExpression(problem);
	}

	@Override
	public IASTProblemStatement newProblemStatement(IASTProblem problem) {
		return new CPPASTProblemStatement(problem);
	}

	@Override
	public IASTProblemTypeId newProblemTypeId(IASTProblem problem) {
		return new CPPASTProblemTypeId(problem);
	}

	@Override
	public ICPPASTQualifiedName newQualifiedName(ICPPASTName name) {
		return new CPPASTQualifiedName(name);
	}

	@Override
	@Deprecated
	public ICPPASTQualifiedName newQualifiedName() {
		return new CPPASTQualifiedName();
	}

	@Override
	public ICPPASTQualifiedName newQualifiedName(String[] nameQualifiers, String name) {
		ICPPASTQualifiedName qualifiedName = newQualifiedName(newName(name));
		for (String qualifier : nameQualifiers) {
			qualifiedName.addNameSpecifier(newName(qualifier));
		}
		return qualifiedName;
	}

	@Override
	public ICPPASTRangeBasedForStatement newRangeBasedForStatement() {
		return new CPPASTRangeBasedForStatement();
	}

	@Override
	public ICPPASTReferenceOperator newReferenceOperator() {
		return new CPPASTReferenceOperator(false);
	}

	@Override
	public ICPPASTReferenceOperator newReferenceOperator(boolean isRValueReference) {
		return new CPPASTReferenceOperator(isRValueReference);
	}

	@Override
	public IASTReturnStatement newReturnStatement(IASTExpression retValue) {
		return new CPPASTReturnStatement(retValue);
	}

	@Override
	public IASTReturnStatement newReturnStatement(IASTInitializerClause retValue) {
		return new CPPASTReturnStatement(retValue);
	}

	@Override
	public IASTSimpleDeclaration newSimpleDeclaration(IASTDeclSpecifier declSpecifier) {
		return new CPPASTSimpleDeclaration(declSpecifier);
	}

	@Override
	public ICPPASTSimpleDeclSpecifier newSimpleDeclSpecifier() {
		return new CPPASTSimpleDeclSpecifier();
	}

	@Override
	public ICPPASTSimpleTypeConstructorExpression newSimpleTypeConstructorExpression(ICPPASTDeclSpecifier declSpec,
			IASTInitializer initializer) {
		return new CPPASTSimpleTypeConstructorExpression(declSpec, initializer);
	}

	@Override
	public ICPPASTSimpleTypeTemplateParameter newSimpleTypeTemplateParameter(int type, IASTName name,
			IASTTypeId typeId) {
		return new CPPASTSimpleTypeTemplateParameter(type, name, typeId);
	}

	@Override
	public ICPPASTStaticAssertDeclaration newStaticAssertion(IASTExpression condition,
			ICPPASTLiteralExpression message) {
		return new CPPASTStaticAssertionDeclaration(condition, message);
	}

	@Override
	public ICPPASTStaticAssertDeclaration newStaticAssertion(IASTExpression condition) {
		return newStaticAssertion(condition, null);
	}

	@Override
	public ICPPASTSwitchStatement newSwitchStatement() {
		return new CPPASTSwitchStatement();
	}

	@Override
	public ICPPASTSwitchStatement newSwitchStatement(IASTDeclaration controller, IASTStatement body) {
		return new CPPASTSwitchStatement(controller, body);
	}

	@Override
	public ICPPASTSwitchStatement newSwitchStatement(IASTExpression controller, IASTStatement body) {
		return new CPPASTSwitchStatement(controller, body);
	}

	@Override
	public ICPPASTTemplateDeclaration newTemplateDeclaration(IASTDeclaration declaration) {
		return new CPPASTTemplateDeclaration(declaration);
	}

	@Override
	public ICPPASTTemplatedTypeTemplateParameter newTemplatedTypeTemplateParameter(IASTName name,
			IASTExpression defaultValue) {
		return new CPPASTTemplatedTypeTemplateParameter(name, defaultValue);
	}

	@Override
	public ICPPASTTemplatedTypeTemplateParameter newTemplatedTypeTemplateParameter(int type, IASTName name,
			IASTExpression defaultValue) {
		return new CPPASTTemplatedTypeTemplateParameter(type, name, defaultValue);
	}

	@Override
	public ICPPASTTemplateId newTemplateId(IASTName templateName) {
		return new CPPASTTemplateId(templateName);
	}

	@Override
	public ICPPASTTemplateSpecialization newTemplateSpecialization(IASTDeclaration declaration) {
		return new CPPASTTemplateSpecialization(declaration);
	}

	@Override
	public IASTToken newToken(int tokenType, char[] tokenImage) {
		return new ASTToken(tokenType, tokenImage);
	}

	@Override
	public IASTTokenList newTokenList() {
		return new ASTTokenList();
	}

	@Override
	public ICPPASTTranslationUnit newTranslationUnit(IScanner scanner) {
		CPPASTTranslationUnit tu = new CPPASTTranslationUnit();

		if (scanner != null) {
			tu.setLocationResolver(scanner.getLocationResolver());
			if (scanner instanceof CPreprocessor) {
				CPreprocessor cPreprocessor = (CPreprocessor) scanner;
				tu.setIsForContentAssist(cPreprocessor.isContentAssistMode());
				tu.setOriginatingTranslationUnit(cPreprocessor.getTranslationUnit());
				tu.setIsHeaderUnit(!cPreprocessor.isSource());
			}
		}
		tu.setASTNodeFactory(this);
		return tu;
	}

	@Override
	public ICPPASTTryBlockStatement newTryBlockStatement(IASTStatement body) {
		return new CPPASTTryBlockStatement(body);
	}

	@Override
	public ICPPASTNamedTypeSpecifier newTypedefNameSpecifier(IASTName name) {
		return new CPPASTNamedTypeSpecifier(name);
	}

	@Override
	public ICPPASTTypeId newTypeId(IASTDeclSpecifier declSpecifier, IASTDeclarator declarator) {
		return new CPPASTTypeId(declSpecifier, declarator);
	}

	@Override
	public ICPPASTTypeIdExpression newTypeIdExpression(int operator, IASTTypeId typeId) {
		return new CPPASTTypeIdExpression(operator, typeId);
	}

	@Override
	public IASTTypeIdInitializerExpression newTypeIdInitializerExpression(IASTTypeId typeId,
			IASTInitializer initializer) {
		return new CPPASTTypeIdInitializerExpression(typeId, initializer);
	}

	@Override
	@Deprecated
	public org.eclipse.cdt.core.dom.ast.cpp.ICPPASTTypenameExpression newTypenameExpression(IASTName qualifiedName,
			IASTExpression expr, boolean isTemplate) {
		return new CPPASTTypenameExpression(qualifiedName, expr);
	}

	@Override
	public ICPPASTTypeTransformationSpecifier newTypeTransformationSpecifier(
			ICPPUnaryTypeTransformation.Operator operator, ICPPASTTypeId operand) {
		return new CPPASTTypeTransformationSpecifier(operator, operand);
	}

	@Override
	public ICPPASTUnaryExpression newUnaryExpression(int operator, IASTExpression operand) {
		return new CPPASTUnaryExpression(operator, operand);
	}

	@Override
	public ICPPASTUsingDeclaration newUsingDeclaration(IASTName name) {
		return new CPPASTUsingDeclaration(name);
	}

	@Override
	public ICPPASTUsingDirective newUsingDirective(IASTName name) {
		return new CPPASTUsingDirective(name);
	}

	@Override
	public ICPPASTVisibilityLabel newVisibilityLabel(int visibility) {
		return new CPPASTVisibilityLabel(visibility);
	}

	@Override
	public ICPPASTVirtSpecifier newVirtSpecifier(SpecifierKind kind) {
		return new CPPASTVirtSpecifier(kind);
	}

	@Override
	public ICPPASTWhileStatement newWhileStatement() {
		return new CPPASTWhileStatement();
	}

	@Override
	public ICPPASTWhileStatement newWhileStatement(IASTDeclaration condition, IASTStatement body) {
		return new CPPASTWhileStatement(condition, body);
	}

	@Override
	public ICPPASTWhileStatement newWhileStatement(IASTExpression condition, IASTStatement body) {
		return new CPPASTWhileStatement(condition, body);
	}

	@Override
	public ICPPASTStructuredBindingDeclaration newStructuredBindingDeclaration() {
		return new CPPASTStructuredBindingDeclaration();
	}

	@Override
	public ICPPASTStructuredBindingDeclaration newStructuredBindingDeclaration(ICPPASTSimpleDeclSpecifier declSpecifier,
			RefQualifier refQualifier, IASTName[] names, IASTInitializer initializer) {
		return new CPPASTStructuredBindingDeclaration(declSpecifier, refQualifier, names, initializer);
	}
}
