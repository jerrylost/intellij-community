package com.siyeh.ig.jdk;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.siyeh.ig.*;

public class ForeachStatementInspection extends StatementInspection{
    public String getDisplayName(){
        return "Extended 'for' statement";
    }

    public String getGroupDisplayName(){
        return GroupNames.JDK_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location){
        return "'Extended #ref' statement #loc";
    }

    private static class ForEachFix extends InspectionGadgetsFix{
        public String getName(){
            return "Replace with old-style 'for' statement";
        }

        public void applyFix(Project project, ProblemDescriptor descriptor){
            if(isQuickFixOnReadOnlyFile(project, descriptor)) return;
            final PsiForeachStatement statement =
                    (PsiForeachStatement) descriptor.getPsiElement();

            StringBuffer newStatement = new StringBuffer();
            final PsiExpression iteratedValue = statement.getIteratedValue();
            if(iteratedValue.getType() instanceof PsiArrayType){
                newStatement.append("for(int i = 0;i<")
                        .append(iteratedValue.getText()).append(".length;i++)");
                newStatement.append("{ ")
                        .append(statement.getIterationParameter().getType()
                                        .getPresentableText())
                        .append(" ").append(statement.getIterationParameter())
                        .append(" = ").append(iteratedValue.getText())
                        .append("[i];");
                final PsiStatement body = statement.getBody();
                if(body instanceof PsiBlockStatement){
                    final PsiElement[] children = body.getChildren();
                    for(int i = 1; i < children.length - 1;
                        i++){//skip the braces
                        PsiElement child = children[i];
                        newStatement.append(child.getText());
                    }
                } else{
                    newStatement.append(body.getText());
                }
                newStatement.append("}");
            } else{
                newStatement.append("for(Iterator it = ")
                        .append(iteratedValue.getText())
                        .append(".iterator;it.hasNext();)");
                newStatement.append("{");

                final PsiStatement body = statement.getBody();
                if(body instanceof PsiBlockStatement){
                    final PsiElement[] children = body.getChildren();
                    for(int i = 1; i < children.length - 1;
                        i++){//skip the braces
                        PsiElement child = children[i];
                        newStatement.append(child.getText());
                    }
                } else{
                    newStatement.append(body.getText());
                }
                newStatement.append("}");
            }
            replaceStatement(project, statement, newStatement.toString());
        }
    }

    public BaseInspectionVisitor createVisitor(InspectionManager inspectionManager,
                                               boolean onTheFly){
        return new ForeachStatementVisitor(this, inspectionManager, onTheFly);
    }

    private static class ForeachStatementVisitor extends BaseInspectionVisitor{
        private ForeachStatementVisitor(BaseInspection inspection,
                                        InspectionManager inspectionManager,
                                        boolean isOnTheFly){
            super(inspection, inspectionManager, isOnTheFly);
        }

        public void visitForeachStatement(PsiForeachStatement statement){
            super.visitForeachStatement(statement);
            registerStatementError(statement);
        }
    }
}