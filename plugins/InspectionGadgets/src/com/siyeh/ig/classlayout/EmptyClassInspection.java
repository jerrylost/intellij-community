package com.siyeh.ig.classlayout;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ClassInspection;
import com.siyeh.ig.GroupNames;

public class EmptyClassInspection extends ClassInspection {

    public String getDisplayName() {
        return "Empty class";
    }

    public String getGroupDisplayName() {
        return GroupNames.CLASSLAYOUT_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        return "Class #ref is empty #loc";
    }

    public BaseInspectionVisitor createVisitor(InspectionManager inspectionManager, boolean onTheFly) {
        return new EmptyClassVisitor(this, inspectionManager, onTheFly);
    }

    private static class EmptyClassVisitor extends BaseInspectionVisitor {
        private EmptyClassVisitor(BaseInspection inspection, InspectionManager inspectionManager, boolean isOnTheFly) {
            super(inspection, inspectionManager, isOnTheFly);
        }

        public void visitClass(PsiClass aClass) {
            //don't call super, to prevent drilldown
            if (aClass.isInterface() || aClass.isEnum() || aClass.isAnnotationType()) {
                return;
            }
            final PsiMethod[] constructors = aClass.getConstructors();
            if (constructors != null && constructors.length > 0) {
                return;
            }
            final PsiMethod[] methods = aClass.getMethods();
            if (methods != null && methods.length > 0) {
                return;
            }
            final PsiField[] fields = aClass.getFields();
            if (fields != null && fields.length > 0) {
                return;
            }
            registerClassError(aClass);
        }
    }
}