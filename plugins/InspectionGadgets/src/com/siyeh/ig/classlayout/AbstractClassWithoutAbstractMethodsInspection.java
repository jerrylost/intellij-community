package com.siyeh.ig.classlayout;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.util.PsiSuperMethodUtil;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ClassInspection;
import com.siyeh.ig.GroupNames;

import java.util.HashSet;
import java.util.Set;

public class AbstractClassWithoutAbstractMethodsInspection extends ClassInspection {

    public String getDisplayName() {
        return "Abstract class without abstract methods";
    }

    public String getGroupDisplayName() {
        return GroupNames.CLASSLAYOUT_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        return "Class #ref is declared 'abstract', and has no 'abstract' methods #loc";
    }

    public BaseInspectionVisitor createVisitor(InspectionManager inspectionManager, boolean onTheFly) {
        return new AbstractClassWithoutAbstractMethodsVisitor(this, inspectionManager, onTheFly);
    }

    private static class AbstractClassWithoutAbstractMethodsVisitor extends BaseInspectionVisitor {
        private AbstractClassWithoutAbstractMethodsVisitor(BaseInspection inspection,
                                                           InspectionManager inspectionManager, boolean isOnTheFly) {
            super(inspection, inspectionManager, isOnTheFly);
        }

        public void visitClass(PsiClass aClass) {
            // no call to super, so that it doesn't drill down to inner classes
            if (aClass.isInterface() || aClass.isAnnotationType()) {
                return;
            }
            if (!aClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
                return;
            }
            if (hasAbstractMethods(aClass)) {
                return;
            }
            registerClassError(aClass);
        }

        private static boolean hasAbstractMethods(PsiClass aClass) {
            final PsiMethod[] methods = aClass.getMethods();
            final Set overriddenMethods = calculateOverriddenMethods(methods);
            final PsiMethod[] allMethods = aClass.getAllMethods();
            for (int i = 0; i < allMethods.length; i++) {
                final PsiMethod method = allMethods[i];
                if (method.hasModifierProperty(PsiModifier.ABSTRACT) &&
                        !overriddenMethods.contains(method)) {
                    return true;
                }
            }
            return false;
        }

        private static Set calculateOverriddenMethods(PsiMethod[] methods) {
            final Set overriddenMethods = new HashSet(methods.length);
            for (int i = 0; i < methods.length; i++) {
                final PsiMethod method = methods[i];
                calculateOverriddenMethods(method, overriddenMethods);
            }
            return overriddenMethods;
        }

        private static void calculateOverriddenMethods(PsiMethod method, Set overriddenMethods) {
            final PsiMethod[] superMethods = PsiSuperMethodUtil.findSuperMethods(method);
            for (int j = 0; j < superMethods.length; j++) {
                final PsiMethod superMethod = superMethods[j];
                overriddenMethods.add(superMethod);
            }
        }

    }

}
