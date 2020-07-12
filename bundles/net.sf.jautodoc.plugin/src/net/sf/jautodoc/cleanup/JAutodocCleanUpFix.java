package net.sf.jautodoc.cleanup;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.refactoring.CompilationUnitChange;
import org.eclipse.jdt.ui.cleanup.ICleanUpFix;
import org.eclipse.ltk.core.refactoring.CategorizedTextEditGroup;
import org.eclipse.ltk.core.refactoring.GroupCategory;
import org.eclipse.ltk.core.refactoring.GroupCategorySet;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.source.SourceManipulator;

/**
 * Javadoc and header clean up fix.
 */
public class JAutodocCleanUpFix implements ICleanUpFix {

    private final ICompilationUnit compUnit;

    private final boolean addHeader;
    private final boolean replaceHeader;
    private final boolean cleanUpJavadoc;

    public JAutodocCleanUpFix(ICompilationUnit compUnit, boolean addHeader, boolean replaceHeader,
            boolean cleanUpJavadoc) {
        this.compUnit = compUnit;
        this.addHeader = addHeader;
        this.replaceHeader = replaceHeader;
        this.cleanUpJavadoc = cleanUpJavadoc;
    }

    @Override
    public CompilationUnitChange createChange(IProgressMonitor progressMonitor) throws CoreException {
        try {
            SourceManipulator sm = new SourceManipulator(compUnit, getConfiguration());
            sm.setShowPreview(true);

            if (cleanUpJavadoc) {
                sm.addJavadoc(progressMonitor);
            } else {
                sm.addJavadoc(new IMember[0], progressMonitor);
            }

            CompilationUnitChange result = new CompilationUnitChange("JAutodoc", compUnit);
            result.setEdit(sm.getChanges());
            result.addTextEditGroup(new CategorizedTextEditGroup("JAutodoc",
                    new GroupCategorySet(new GroupCategory("JAutodoc", "JAutodoc", "Cleanup Javadoc and Header"))));

            return result;
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, JAutodocPlugin.PLUGIN_ID, e.getMessage(), e));
        }
    }

    private Configuration getConfiguration() {
        Configuration config = new Configuration(ConfigurationManager.getConfiguration(compUnit));
        config.setAddHeader(addHeader);
        config.setReplaceHeader(replaceHeader);
        return config;
    }
}
