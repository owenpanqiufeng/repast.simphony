/**
 * 
 */
package repast.simphony.statecharts.sheets;

import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;

import repast.simphony.statecharts.editor.CodeUpdateStrategy;
import repast.simphony.statecharts.editor.JavaSourceViewer;

/**
 * Support class that makes it easier to bind features to
 * widgets.
 * 
 * @author Nick Collier
 */
public class BindingSupport {
  
  private EMFDataBindingContext context;
  private EObject eObject;
  /**
   * Creates a BindingSupport that will bind the specified EObject
   * using the specified context.
   * 
   * @param context
   * @param eObject
   */
  public BindingSupport(EMFDataBindingContext context, EObject eObject) {
    this.context = context;
    this.eObject = eObject;
  }
  
  /**
   * Binds the specified feature to the specified viewer.
   * 
   * @param feature
   * @param viewer
   */
  public void bind(EStructuralFeature feature, JavaSourceViewer viewer) {
    context
    .bindValue(
        WidgetProperties.text(new int[] { SWT.Modify }).observe(viewer.getTextWidget()),
        EMFEditProperties.value(TransactionUtil.getEditingDomain(eObject), feature).observe(eObject), null,
            new CodeUpdateStrategy(viewer));
  }
}
