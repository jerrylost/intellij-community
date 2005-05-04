package com.intellij.lang.properties;

import com.intellij.codeInspection.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.PsiReferenceProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.lang.properties.psi.Property;
import com.intellij.util.SmartList;
import com.intellij.util.IncorrectOperationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.impl.ModuleUtil;
import com.intellij.openapi.module.Module;

import java.util.List;

/**
 * @author cdr
 */
public class UnusedPropertyInspection extends LocalInspectionTool {
  private static final Logger LOG = Logger.getInstance("#com.intellij.lang.properties.UnusedPropertyInspection");
  private static final RemovePropertyLocalFix QUICK_FIX = new RemovePropertyLocalFix();

  public String getGroupDisplayName() {
    return "Properties Files";
  }

  public String getDisplayName() {
    return "Unused Property";
  }

  public String getShortName() {
    return "Unused Property";
  }

  public ProblemDescriptor[] checkFile(PsiFile file, InspectionManager manager, boolean isOnTheFly) {
    if (!(file instanceof PropertiesFile)) return null;
    List<ProblemDescriptor> descriptors = new SmartList<ProblemDescriptor>();
    PsiSearchHelper searchHelper = file.getManager().getSearchHelper();
    List<Property> properties = ((PropertiesFile)file).getProperties();
    Module module = ModuleUtil.findModuleForPsiElement(file);
    if (module == null) return null;
    GlobalSearchScope searchScope = GlobalSearchScope.moduleWithDependenciesScope(module);
    for (Property property : properties) {
      PsiReferenceProcessor.FindElement processor = new PsiReferenceProcessor.FindElement();
      searchHelper.processReferences(processor, property, searchScope, true);
      if (!processor.isFound()) {
        ProblemDescriptor descriptor = manager.createProblemDescriptor(property, "Unused property", QUICK_FIX, ProblemHighlightType.LIKE_UNUSED_SYMBOL);
        descriptors.add(descriptor);
      }
    }
    return descriptors.toArray(new ProblemDescriptor[descriptors.size()]);
  }

  private static class RemovePropertyLocalFix implements LocalQuickFix {
    public String getName() {
      return "Remove Property";
    }

    public void applyFix(Project project, ProblemDescriptor descriptor) {
      Property property = (Property)descriptor.getPsiElement();
      try {
        new RemovePropertyFix(property).invoke(project, null, property.getContainingFile());
      }
      catch (IncorrectOperationException e) {
        LOG.error(e);
      }
    }
  }
}
