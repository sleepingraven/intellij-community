/*
 * User: anna
 * Date: 20-Aug-2008
 */
package com.intellij.refactoring;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.refactoring.wrapreturnvalue.WrapReturnValueProcessor;
import org.jetbrains.annotations.NonNls;

public class WrapReturnValueTest extends MultiFileTestCase{
  protected String getTestRoot() {
    return "/refactoring/wrapReturnValue/";
  }

  private void doTest(final boolean existing) throws Exception {
    doTest(existing, null);
  }

  private void doTest(final boolean existing, @NonNls String exceptionMessage) throws Exception {
    doTest(existing, exceptionMessage, false);
  }

  private void doTest(final boolean existing, String exceptionMessage, final boolean createInnerClass) throws Exception {
    try {
      doTest(new PerformAction() {
        public void performAction(final VirtualFile rootDir, final VirtualFile rootAfter) throws Exception {
          PsiClass aClass = myJavaFacade.findClass("Test", GlobalSearchScope.projectScope(getProject()));

          assertNotNull("Class Test not found", aClass);

          final PsiMethod method = aClass.findMethodsByName("foo", false)[0];



          @NonNls final String wrapperClassName = "Wrapper";

          final PsiClass wrapperClass = myJavaFacade.findClass(wrapperClassName, GlobalSearchScope.projectScope(getProject()));

          assertTrue(!existing || wrapperClass != null);
          final PsiField delegateField = existing ? wrapperClass.findFieldByName("myField", false) : null;
          WrapReturnValueProcessor processor = new WrapReturnValueProcessor(wrapperClassName, "", method, existing, createInnerClass,
                                                                            delegateField);
          processor.run();
          /*LocalFileSystem.getInstance().refresh(false);
          FileDocumentManager.getInstance().saveAllDocuments();*/
        }
      });
    }
    catch (RuntimeException e) {
      if (exceptionMessage != null) {
        assertEquals(exceptionMessage, e.getMessage());
        return;
      }
      throw e;
    }
    if (exceptionMessage != null) {
      fail("Conflict was not found");
    }
  }

  public void testSimple() throws Exception {
    doTest(false);
  }

  public void testGenerics() throws Exception {
    doTest(false);
  }

  public void testInconsistentWrapper() throws Exception {
    doTest(true, "Existing class does not have getter for selected field");
  }

  public void testWrapper() throws Exception {
    doTest(true);
  }

  public void testStrip() throws Exception {
    doTest(true);
  }

  public void testNoConstructor() throws Exception {
    doTest(true, "Existing class does not have appropriate constructor");
  }

  public void testInnerClass() throws Exception {
    doTest(false, null, true);
  }

  public void testHierarchy() throws Exception {
    doTest(false, null, true);
  }
}