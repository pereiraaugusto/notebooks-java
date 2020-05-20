/*
 *  Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.twosigma.beakerx.groovy.autocomplete;

import com.twosigma.beakerx.autocomplete.AutocompleteCandidate;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationClauseContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationElementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationElementPairContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationParamArrayExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationParamBoolExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationParamClassExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationParamDecimalExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationParamIntegerExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationParamNullExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationParamPathExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AnnotationParamStringExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ArgumentDeclarationContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ArgumentDeclarationListContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ArgumentListContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.AssignmentExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.BinaryExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.BlockStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.BoolExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.CallExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.CaseStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.CatchBlockContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ClassBodyContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ClassDeclarationContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ClassInitializerContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ClassMemberContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ClassModifierContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ClassNameExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ClassicForStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ClosureExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ClosureExpressionRuleContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.CmdExpressionRuleContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.CommandExpressionStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.CompilationUnitContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ConstantDecimalExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ConstantExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ConstantIntegerExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ConstructorDeclarationContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ControlStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.DeclarationExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.DeclarationRuleContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.DeclarationStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.EnumDeclarationContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.EnumMemberContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ExpressionStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ExtendsClauseContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.FieldAccessExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.FieldDeclarationContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.FinallyBlockContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ForColonStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ForInStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.GenericClassNameExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.GenericDeclarationListContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.GenericListContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.GenericsConcreteElementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.GenericsDeclarationElementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.GenericsWildcardElementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.GstringContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.GstringExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.GstringPathExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.IfStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ImplementsClauseContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ImportStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ListConstructorContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.MapConstructorContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.MapEntryContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.MemberModifierContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.MethodBodyContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.MethodCallExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.MethodDeclarationContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.NewArrayExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.NewArrayRuleContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.NewArrayStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.NewInstanceExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.NewInstanceRuleContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.NewInstanceStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.NullExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ObjectInitializerContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.PackageDefinitionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ParenthesisExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.PathExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.PostfixExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.PrefixExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ReturnStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.StatementBlockContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.SwitchStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ThrowStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.ThrowsClauseContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.TryBlockContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.TryCatchFinallyStatementContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.TypeDeclarationContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.UnaryExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.VariableExpressionContext;
import com.twosigma.beakerx.groovy.autocomplete.GroovyParser.WhileStatementContext;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

public class GroovyAbstractListener implements GroovyParserListener {
  protected List<AutocompleteCandidate> query;
  private int startIndex;
  
  protected void addQuery(AutocompleteCandidate c,int startIndex) {
      if(c==null)
          return;
      if (query==null)
          query = new ArrayList<AutocompleteCandidate>();
      this.startIndex = startIndex;
      query.add(c);
  }

  public int getStartIndex() {
    return startIndex;
  }

  public List<AutocompleteCandidate> getQuery() { return query; }

  @Override
  public void enterEveryRule(ParserRuleContext arg0) {

    
  }

  @Override
  public void exitEveryRule(ParserRuleContext arg0) {
    
    
  }

  @Override
  public void visitErrorNode(ErrorNode arg0) {
    
    
  }

  @Override
  public void visitTerminal(TerminalNode arg0) {
    
    
  }

  @Override
  public void enterNewArrayRule(NewArrayRuleContext ctx) {
    
    
  }

  @Override
  public void exitNewArrayRule(NewArrayRuleContext ctx) {
    
    
  }

  @Override
  public void enterEnumMember(EnumMemberContext ctx) {
    
    
  }

  @Override
  public void exitEnumMember(EnumMemberContext ctx) {
    
    
  }

  @Override
  public void enterNewArrayStatement(NewArrayStatementContext ctx) {
    
    
  }

  @Override
  public void exitNewArrayStatement(NewArrayStatementContext ctx) {
    
    
  }

  @Override
  public void enterGstringExpression(GstringExpressionContext ctx) {
    
    
  }

  @Override
  public void exitGstringExpression(GstringExpressionContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationElementPair(AnnotationElementPairContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationElementPair(AnnotationElementPairContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationParamArrayExpression(
      AnnotationParamArrayExpressionContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationParamArrayExpression(
      AnnotationParamArrayExpressionContext ctx) {
    
    
  }

  @Override
  public void enterConstantDecimalExpression(
      ConstantDecimalExpressionContext ctx) {
    
    
  }

  @Override
  public void exitConstantDecimalExpression(ConstantDecimalExpressionContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationParamPathExpression(
      AnnotationParamPathExpressionContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationParamPathExpression(
      AnnotationParamPathExpressionContext ctx) {
    
    
  }

  @Override
  public void enterVariableExpression(VariableExpressionContext ctx) {
    
    
  }

  @Override
  public void exitVariableExpression(VariableExpressionContext ctx) {
    
    
  }

  @Override
  public void enterGenericsConcreteElement(GenericsConcreteElementContext ctx) {
    
    
  }

  @Override
  public void exitGenericsConcreteElement(GenericsConcreteElementContext ctx) {
    
    
  }

  @Override
  public void enterArgumentList(ArgumentListContext ctx) {
    
    
  }

  @Override
  public void exitArgumentList(ArgumentListContext ctx) {
    
    
  }

  @Override
  public void enterGenericList(GenericListContext ctx) {
    
    
  }

  @Override
  public void exitGenericList(GenericListContext ctx) {
    
    
  }

  @Override
  public void enterFieldAccessExpression(FieldAccessExpressionContext ctx) {
    
    
  }

  @Override
  public void exitFieldAccessExpression(FieldAccessExpressionContext ctx) {
    
    
  }

  @Override
  public void enterNullExpression(NullExpressionContext ctx) {
    
    
  }

  @Override
  public void exitNullExpression(NullExpressionContext ctx) {
    
    
  }

  @Override
  public void enterTryCatchFinallyStatement(TryCatchFinallyStatementContext ctx) {
    
    
  }

  @Override
  public void exitTryCatchFinallyStatement(TryCatchFinallyStatementContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationParamBoolExpression(
      AnnotationParamBoolExpressionContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationParamBoolExpression(
      AnnotationParamBoolExpressionContext ctx) {
    
    
  }

  @Override
  public void enterExpressionStatement(ExpressionStatementContext ctx) {
    
    
  }

  @Override
  public void exitExpressionStatement(ExpressionStatementContext ctx) {
    
    
  }

  @Override
  public void enterNewArrayExpression(NewArrayExpressionContext ctx) {
    
    
  }

  @Override
  public void exitNewArrayExpression(NewArrayExpressionContext ctx) {
    
    
  }

  @Override
  public void enterUnaryExpression(UnaryExpressionContext ctx) {
    
    
  }

  @Override
  public void exitUnaryExpression(UnaryExpressionContext ctx) {
    
    
  }

  @Override
  public void enterGenericsWildcardElement(GenericsWildcardElementContext ctx) {
    
    
  }

  @Override
  public void exitGenericsWildcardElement(GenericsWildcardElementContext ctx) {
    
    
  }

  @Override
  public void enterConstantIntegerExpression(
      ConstantIntegerExpressionContext ctx) {
    
    
  }

  @Override
  public void exitConstantIntegerExpression(ConstantIntegerExpressionContext ctx) {
    
    
  }

  @Override
  public void enterCatchBlock(CatchBlockContext ctx) {
    
    
  }

  @Override
  public void exitCatchBlock(CatchBlockContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationParamStringExpression(
      AnnotationParamStringExpressionContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationParamStringExpression(
      AnnotationParamStringExpressionContext ctx) {
    
    
  }

  @Override
  public void enterClassMember(ClassMemberContext ctx) {
    
    
  }

  @Override
  public void exitClassMember(ClassMemberContext ctx) {
    
    
  }

  @Override
  public void enterBoolExpression(BoolExpressionContext ctx) {
    
    
  }

  @Override
  public void exitBoolExpression(BoolExpressionContext ctx) {
    
    
  }

  @Override
  public void enterDeclarationRule(DeclarationRuleContext ctx) {
    
    
  }

  @Override
  public void exitDeclarationRule(DeclarationRuleContext ctx) {
    
    
  }

  @Override
  public void enterForColonStatement(ForColonStatementContext ctx) {
    
    
  }

  @Override
  public void exitForColonStatement(ForColonStatementContext ctx) {
    
    
  }

  @Override
  public void enterParenthesisExpression(ParenthesisExpressionContext ctx) {
    
    
  }

  @Override
  public void exitParenthesisExpression(ParenthesisExpressionContext ctx) {
    
    
  }

  @Override
  public void enterAssignmentExpression(AssignmentExpressionContext ctx) {
    
    
  }

  @Override
  public void exitAssignmentExpression(AssignmentExpressionContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationParamIntegerExpression(
      AnnotationParamIntegerExpressionContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationParamIntegerExpression(
      AnnotationParamIntegerExpressionContext ctx) {
    
    
  }

  @Override
  public void enterNewInstanceExpression(NewInstanceExpressionContext ctx) {
    
    
  }

  @Override
  public void exitNewInstanceExpression(NewInstanceExpressionContext ctx) {
    
    
  }

  @Override
  public void enterCommandExpressionStatement(
      CommandExpressionStatementContext ctx) {
    
    
  }

  @Override
  public void exitCommandExpressionStatement(
      CommandExpressionStatementContext ctx) {
    
    
  }

  @Override
  public void enterDeclarationStatement(DeclarationStatementContext ctx) {
    
    
  }

  @Override
  public void exitDeclarationStatement(DeclarationStatementContext ctx) {
    
    
  }

  @Override
  public void enterBinaryExpression(BinaryExpressionContext ctx) {
    
    
  }

  @Override
  public void exitBinaryExpression(BinaryExpressionContext ctx) {
    
    
  }

  @Override
  public void enterCompilationUnit(CompilationUnitContext ctx) {
    
    
  }

  @Override
  public void exitCompilationUnit(CompilationUnitContext ctx) {
    
    
  }

  @Override
  public void enterControlStatement(ControlStatementContext ctx) {
    
    
  }

  @Override
  public void exitControlStatement(ControlStatementContext ctx) {
    
    
  }

  @Override
  public void enterCallExpression(CallExpressionContext ctx) {
    
    
  }

  @Override
  public void exitCallExpression(CallExpressionContext ctx) {
    
    
  }

  @Override
  public void enterClosureExpression(ClosureExpressionContext ctx) {
    
    
  }

  @Override
  public void exitClosureExpression(ClosureExpressionContext ctx) {
    
    
  }

  @Override
  public void enterExtendsClause(ExtendsClauseContext ctx) {
    
    
  }

  @Override
  public void exitExtendsClause(ExtendsClauseContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationElement(AnnotationElementContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationElement(AnnotationElementContext ctx) {
    
    
  }

  @Override
  public void enterCmdExpressionRule(CmdExpressionRuleContext ctx) {
    
    
  }

  @Override
  public void exitCmdExpressionRule(CmdExpressionRuleContext ctx) {
    
    
  }

  @Override
  public void enterArgumentDeclaration(ArgumentDeclarationContext ctx) {
    
    
  }

  @Override
  public void exitArgumentDeclaration(ArgumentDeclarationContext ctx) {
    
    
  }

  @Override
  public void enterMethodDeclaration(MethodDeclarationContext ctx) {
    
    
  }

  @Override
  public void exitMethodDeclaration(MethodDeclarationContext ctx) {
    
    
  }

  @Override
  public void enterMethodBody(MethodBodyContext ctx) {
    
    
  }

  @Override
  public void exitMethodBody(MethodBodyContext ctx) {
    
    
  }

  @Override
  public void enterClassModifier(ClassModifierContext ctx) {
    
    
  }

  @Override
  public void exitClassModifier(ClassModifierContext ctx) {
    
    
  }

  @Override
  public void enterImportStatement(ImportStatementContext ctx) {
    
    
  }

  @Override
  public void exitImportStatement(ImportStatementContext ctx) {
    
    
  }

  @Override
  public void enterCaseStatement(CaseStatementContext ctx) {
    
    
  }

  @Override
  public void exitCaseStatement(CaseStatementContext ctx) {
    
    
  }

  @Override
  public void enterGstringPathExpression(GstringPathExpressionContext ctx) {
    
    
  }

  @Override
  public void exitGstringPathExpression(GstringPathExpressionContext ctx) {
    
    
  }

  @Override
  public void enterStatementBlock(StatementBlockContext ctx) {
    
    
  }

  @Override
  public void exitStatementBlock(StatementBlockContext ctx) {
    
    
  }

  @Override
  public void enterThrowsClause(ThrowsClauseContext ctx) {
    
    
  }

  @Override
  public void exitThrowsClause(ThrowsClauseContext ctx) {
    
    
  }

  @Override
  public void enterMethodCallExpression(MethodCallExpressionContext ctx) {
    
    
  }

  @Override
  public void exitMethodCallExpression(MethodCallExpressionContext ctx) {
    
    
  }

  @Override
  public void enterTypeDeclaration(TypeDeclarationContext ctx) {
    
    
  }

  @Override
  public void exitTypeDeclaration(TypeDeclarationContext ctx) {
    
    
  }

  @Override
  public void enterReturnStatement(ReturnStatementContext ctx) {
    
    
  }

  @Override
  public void exitReturnStatement(ReturnStatementContext ctx) {
    
    
  }

  @Override
  public void enterFinallyBlock(FinallyBlockContext ctx) {
    
    
  }

  @Override
  public void exitFinallyBlock(FinallyBlockContext ctx) {
    
    
  }

  @Override
  public void enterSwitchStatement(SwitchStatementContext ctx) {
    
    
  }

  @Override
  public void exitSwitchStatement(SwitchStatementContext ctx) {
    
    
  }

  @Override
  public void enterMapEntry(MapEntryContext ctx) {
    
    
  }

  @Override
  public void exitMapEntry(MapEntryContext ctx) {
    
    
  }

  @Override
  public void enterClassDeclaration(ClassDeclarationContext ctx) {
    
    
  }

  @Override
  public void exitClassDeclaration(ClassDeclarationContext ctx) {
    
    
  }

  @Override
  public void enterTryBlock(TryBlockContext ctx) {
    
    
  }

  @Override
  public void exitTryBlock(TryBlockContext ctx) {
    
    
  }

  @Override
  public void enterGstring(GstringContext ctx) {
    
    
  }

  @Override
  public void exitGstring(GstringContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationClause(AnnotationClauseContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationClause(AnnotationClauseContext ctx) {
    
    
  }

  @Override
  public void enterPrefixExpression(PrefixExpressionContext ctx) {
    
    
  }

  @Override
  public void exitPrefixExpression(PrefixExpressionContext ctx) {
    
    
  }

  @Override
  public void enterGenericClassNameExpression(
      GenericClassNameExpressionContext ctx) {
    
    
  }

  @Override
  public void exitGenericClassNameExpression(
      GenericClassNameExpressionContext ctx) {
    
    
  }

  @Override
  public void enterNewInstanceStatement(NewInstanceStatementContext ctx) {
    
    
  }

  @Override
  public void exitNewInstanceStatement(NewInstanceStatementContext ctx) {
    
    
  }

  @Override
  public void enterObjectInitializer(ObjectInitializerContext ctx) {
    
    
  }

  @Override
  public void exitObjectInitializer(ObjectInitializerContext ctx) {
    
    
  }

  @Override
  public void enterGenericDeclarationList(GenericDeclarationListContext ctx) {
    
    
  }

  @Override
  public void exitGenericDeclarationList(GenericDeclarationListContext ctx) {
    
    
  }

  @Override
  public void enterClassBody(ClassBodyContext ctx) {
    
    
  }

  @Override
  public void exitClassBody(ClassBodyContext ctx) {
    
    
  }

  @Override
  public void enterClassNameExpression(ClassNameExpressionContext ctx) {
    
    
  }

  @Override
  public void exitClassNameExpression(ClassNameExpressionContext ctx) {
    
    
  }

  @Override
  public void enterEnumDeclaration(EnumDeclarationContext ctx) {
    
    
  }

  @Override
  public void exitEnumDeclaration(EnumDeclarationContext ctx) {
    
    
  }

  @Override
  public void enterPostfixExpression(PostfixExpressionContext ctx) {
    
    
  }

  @Override
  public void exitPostfixExpression(PostfixExpressionContext ctx) {
    
    
  }

  @Override
  public void enterPackageDefinition(PackageDefinitionContext ctx) {
    
    
  }

  @Override
  public void exitPackageDefinition(PackageDefinitionContext ctx) {
    
    
  }

  @Override
  public void enterGenericsDeclarationElement(
      GenericsDeclarationElementContext ctx) {
    
    
  }

  @Override
  public void exitGenericsDeclarationElement(
      GenericsDeclarationElementContext ctx) {
    
    
  }

  @Override
  public void enterDeclarationExpression(DeclarationExpressionContext ctx) {
    
    
  }

  @Override
  public void exitDeclarationExpression(DeclarationExpressionContext ctx) {
    
    
  }

  @Override
  public void enterBlockStatement(BlockStatementContext ctx) {
    
    
  }

  @Override
  public void exitBlockStatement(BlockStatementContext ctx) {
    
    
  }

  @Override
  public void enterClosureExpressionRule(ClosureExpressionRuleContext ctx) {
    
    
  }

  @Override
  public void exitClosureExpressionRule(ClosureExpressionRuleContext ctx) {
    
    
  }

  @Override
  public void enterListConstructor(ListConstructorContext ctx) {
    
    
  }

  @Override
  public void exitListConstructor(ListConstructorContext ctx) {
    
    
  }

  @Override
  public void enterConstantExpression(ConstantExpressionContext ctx) {
    
    
  }

  @Override
  public void exitConstantExpression(ConstantExpressionContext ctx) {
    
    
  }

  @Override
  public void enterThrowStatement(ThrowStatementContext ctx) {
    
    
  }

  @Override
  public void exitThrowStatement(ThrowStatementContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationParamNullExpression(
      AnnotationParamNullExpressionContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationParamNullExpression(
      AnnotationParamNullExpressionContext ctx) {
    
    
  }

  @Override
  public void enterFieldDeclaration(FieldDeclarationContext ctx) {
    
    
  }

  @Override
  public void exitFieldDeclaration(FieldDeclarationContext ctx) {
    
    
  }

  @Override
  public void enterClassInitializer(ClassInitializerContext ctx) {
    
    
  }

  @Override
  public void exitClassInitializer(ClassInitializerContext ctx) {
    
    
  }

  @Override
  public void enterIfStatement(IfStatementContext ctx) {
    
    
  }

  @Override
  public void exitIfStatement(IfStatementContext ctx) {
    
    
  }

  @Override
  public void enterConstructorDeclaration(ConstructorDeclarationContext ctx) {
    
    
  }

  @Override
  public void exitConstructorDeclaration(ConstructorDeclarationContext ctx) {
    
    
  }

  @Override
  public void enterImplementsClause(ImplementsClauseContext ctx) {
    
    
  }

  @Override
  public void exitImplementsClause(ImplementsClauseContext ctx) {
    
    
  }

  @Override
  public void enterWhileStatement(WhileStatementContext ctx) {
    
    
  }

  @Override
  public void exitWhileStatement(WhileStatementContext ctx) {
    
    
  }

  @Override
  public void enterNewInstanceRule(NewInstanceRuleContext ctx) {
    
    
  }

  @Override
  public void exitNewInstanceRule(NewInstanceRuleContext ctx) {
    
    
  }

  @Override
  public void enterArgumentDeclarationList(ArgumentDeclarationListContext ctx) {
    
    
  }

  @Override
  public void exitArgumentDeclarationList(ArgumentDeclarationListContext ctx) {
    
    
  }

  @Override
  public void enterMapConstructor(MapConstructorContext ctx) {
    
    
  }

  @Override
  public void exitMapConstructor(MapConstructorContext ctx) {
    
    
  }

  @Override
  public void enterClassicForStatement(ClassicForStatementContext ctx) {
    
    
  }

  @Override
  public void exitClassicForStatement(ClassicForStatementContext ctx) {
    
    
  }

  @Override
  public void enterForInStatement(ForInStatementContext ctx) {
    
    
  }

  @Override
  public void exitForInStatement(ForInStatementContext ctx) {
    
    
  }

  @Override
  public void enterMemberModifier(MemberModifierContext ctx) {
    
    
  }

  @Override
  public void exitMemberModifier(MemberModifierContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationParamDecimalExpression(
      AnnotationParamDecimalExpressionContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationParamDecimalExpression(
      AnnotationParamDecimalExpressionContext ctx) {
    
    
  }

  @Override
  public void enterPathExpression(PathExpressionContext ctx) {
    
    
  }

  @Override
  public void exitPathExpression(PathExpressionContext ctx) {
    
    
  }

  @Override
  public void enterAnnotationParamClassExpression(
      AnnotationParamClassExpressionContext ctx) {
    
    
  }

  @Override
  public void exitAnnotationParamClassExpression(
      AnnotationParamClassExpressionContext ctx) {
    
    
  }

}
