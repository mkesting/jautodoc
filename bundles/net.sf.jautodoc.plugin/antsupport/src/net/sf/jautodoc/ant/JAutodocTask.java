/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.ant.core.AntCorePlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.source.JavadocFormatter;
import net.sf.jautodoc.source.SourceManipulator;
import net.sf.jautodoc.utils.Utils;

/**
 * JAutodoc Ant task.
 */
public class JAutodocTask extends Task {
	private static final String DEFAULT_SRCDIR  = "src";
	private static final String DEFAULT_INCLUDE = "**/*.java";

	private static final String COMPLETE = "complete";
	private static final String KEEP	 = "keep";
	private static final String REPLACE	 = "replace";

	private List<FileSet> filesets = new ArrayList<FileSet>();

	private Configuration config;

	private String srcdir;
	private String includes;
	private String excludes;

	private boolean verbose = false;

	private String mode;

	private Boolean commentPublic;
	private Boolean commentPackage;
	private Boolean commentProtected;
	private Boolean commentPrivate;

	private Boolean commentTypes;
	private Boolean commentFields;
	private Boolean commentMethods;
	private Boolean getsetOnly;
	private Boolean excludeGetset;

	private Boolean todo;
	private Boolean comment;
	private Boolean single;
	private Boolean format;
	private Boolean getsetFromField;
	private Boolean getsetFromFieldFirst;
	private Boolean getsetFromFieldReplace;
	private Boolean includeSubpackages;
	private Boolean header;
	private Boolean replaceHeader;
	private Boolean multiHeader;

	private boolean headerOnly;


	public void setSrcdir(String srcdir) {
		this.srcdir = srcdir;
	}

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setPublic(Boolean commentPublic) {
		this.commentPublic = commentPublic;
	}

	public void setPackage(Boolean commentPackage) {
		this.commentPackage = commentPackage;
	}

	public void setProtected(Boolean commentProtected) {
		this.commentProtected = commentProtected;
	}

	public void setPrivate(Boolean commentPrivate) {
		this.commentPrivate = commentPrivate;
	}

	public void setTypes(Boolean commentTypes) {
		this.commentTypes = commentTypes;
	}

	public void setFields(Boolean commentFields) {
		this.commentFields = commentFields;
	}

	public void setMethods(Boolean commentMethods) {
		this.commentMethods = commentMethods;
	}

	public void setGetsetOnly(Boolean getsetOnly) {
		this.getsetOnly = getsetOnly;
	}

	public void setExcludeGetset(Boolean excludeGetset) {
        this.excludeGetset = excludeGetset;
    }

    public void setTodo(Boolean todo) {
		this.todo = todo;
	}

	public void setComment(Boolean comment) {
		this.comment = comment;
	}

	public void setSingle(Boolean single) {
		this.single = single;
	}

	public void setFormat(Boolean format) {
		this.format = format;
	}

	public void setGetsetFromField(Boolean getsetFromField) {
        this.getsetFromField = getsetFromField;
    }

    public void setGetsetFromFieldFirst(Boolean getsetFromFieldFirst) {
        this.getsetFromFieldFirst = getsetFromFieldFirst;
    }

    public void setGetsetFromFieldReplace(Boolean getsetFromFieldReplace) {
        this.getsetFromFieldReplace = getsetFromFieldReplace;
    }

    public void setIncludeSubpackages(Boolean includeSubpackages) {
        this.includeSubpackages = includeSubpackages;
    }

    public void setHeader(Boolean header) {
		this.header = header;
	}

	public void setReplaceHeader(Boolean replaceHeader) {
		this.replaceHeader = replaceHeader;
	}

	public void setMultiHeader(Boolean multiHeader) {
		this.multiHeader = multiHeader;
	}

	public void setHeaderOnly(boolean headerOnly) {
		this.headerOnly = headerOnly;
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.Task#execute()
	 */
	public void execute() throws BuildException {
		validateProperties();

		log("Creating Javadoc...");

		try {
			ICompilationUnit[] compUnits = findCompilationUnits();
			if (compUnits.length == 0) {
				return;
			}

			IProgressMonitor monitor = null;
			Hashtable<?, ?> references = getProject().getReferences();
			if (references != null) {
				monitor = (IProgressMonitor) references.get(AntCorePlugin.ECLIPSE_PROGRESS_MONITOR);
			}
			monitor = (monitor == null ? new NullProgressMonitor() : monitor);

			createJavadoc(compUnits, new ProgressMonitor(monitor, 1));

			log("Done.");
		} catch (Throwable e) {
			throw new BuildException(e);
        }
	}

	private ICompilationUnit[] findCompilationUnits() {
		List<ICompilationUnit> compUnits = new ArrayList<ICompilationUnit>();

		String[] includedFiles = getIncludedFiles();
		for (int i = 0; i < includedFiles.length; ++i) {
			IPath path = new Path(includedFiles[i]);
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
			ICompilationUnit compUnit = JavaCore.createCompilationUnitFrom(file);
			if (compUnit != null) {
				compUnits.add(compUnit);
			}
		}

		return (ICompilationUnit[])compUnits.toArray(new ICompilationUnit[compUnits.size()]);
	}

	private String[] getIncludedFiles() {
		Set<String> includedFiles = new HashSet<String>();

		if (srcdir != null || filesets.size() == 0) {
			FileSet fileSet = new FileSet();
			fileSet.setDir(getProject().resolveFile(srcdir != null ? srcdir : DEFAULT_SRCDIR));
			fileSet.setIncludes(includes != null ? includes : DEFAULT_INCLUDE);
			fileSet.setExcludes(excludes);
			filesets.add(fileSet);
		}

		Iterator<FileSet> iter = filesets.iterator();
		while (iter.hasNext()) {
			FileSet fileSet = iter.next();
			DirectoryScanner ds = fileSet.getDirectoryScanner(getProject());
			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; ++i) {
				includedFiles.add(new File(ds.getBasedir(), files[i]).getAbsolutePath());
			}
		}

		return includedFiles.toArray(new String[includedFiles.size()]);
	}

	private void validateProperties() throws BuildException {
		if (mode != null && !COMPLETE.equals(mode)
				&& !KEEP.equals(mode) && !REPLACE.equals(mode)) {
			throw new BuildException("Invalid mode: " + mode
					+ ". Use complete, keep or replace");
		}

		if (getsetOnly != null && getsetOnly.booleanValue() && excludeGetset != null && excludeGetset.booleanValue()) {
		    throw new BuildException("Use only one of these flags: getsetOnly && excludeGetset");
		}
	}

	private void createJavadoc(ICompilationUnit[] compUnits,
			IProgressMonitor monitor) throws Throwable {
		if (compUnits.length == 0) return;

		monitor.beginTask(Constants.TITLE_JDOC_TASK, compUnits.length);

		try {
			for (int i = 0; i < compUnits.length; i++) {
				ICompilationUnit compUnit = (ICompilationUnit) compUnits[i];
				monitor.subTask(compUnit.getElementName());
				if (monitor.isCanceled()) {
					break;
				}

				Configuration config = getConfiguration(compUnit);
				if (config.isUseEclipseFormatter()) {
					JavadocFormatter.getInstance().startFormatting(compUnit);
				}

				IEditorPart editor = findEditor(compUnit);
				ICompilationUnit workingCopy = getWorkingCopy(compUnit, editor);

				SourceManipulator sm = new SourceManipulator(workingCopy, config);

				AddJavadocRunner runner = new AddJavadocRunner(sm);
				if (editor == null) {
					runner.run();
					runner.checkSuccess();

					// not open in editor -> commit + discard
					workingCopy.commitWorkingCopy(false, null);
					workingCopy.discardWorkingCopy();
				}
				else {
					// open in editor -> use gui thread
					Display.getDefault().syncExec(runner);
					runner.checkSuccess();
				}

				monitor.worked(1);
			}
		}
		finally {
			JavadocFormatter.getInstance().stopFormatting();
			monitor.done();
		}
	}

	private IEditorPart findEditor(ICompilationUnit compUnit) throws Throwable {
		IEditorPart editor = null;
		try {
		    if (PlatformUI.isWorkbenchRunning()) {
                editor = Utils.findEditor(compUnit);
            }
		} catch (SWTException e) {
			// open in editor -> use gui thread
			EditorFinder ef = new EditorFinder(compUnit);
			Display.getDefault().syncExec(ef);
			ef.checkSuccess();
			editor = ef.getEditor();
		}
		return editor;
	}

	private ICompilationUnit getWorkingCopy(ICompilationUnit compUnit,
			IEditorPart editor) throws Throwable {
		WorkingCopyCreator wcc = new WorkingCopyCreator(compUnit, editor);
		if (editor == null) {
			wcc.run();
			wcc.checkSuccess();
		}
		else {
			// open in editor -> use gui thread
			Display.getDefault().syncExec(wcc);
			wcc.checkSuccess();
		}
		return wcc.getWorkingCopy();
	}

	private Configuration getConfiguration(ICompilationUnit compUnit) {
		if (config != null) return config;

		config = ConfigurationManager.getConfiguration(compUnit, false);

		if (mode != null) {
			if (COMPLETE.equals(mode)) {
				config.setCompleteExistingJavadoc(true);
				config.setKeepExistingJavadoc(false);
				config.setReplaceExistingJavadoc(false);
			}
			else if (KEEP.equals(mode)) {
				config.setCompleteExistingJavadoc(false);
				config.setKeepExistingJavadoc(true);
				config.setReplaceExistingJavadoc(false);
			}
			else if (REPLACE.equals(mode)) {
				config.setCompleteExistingJavadoc(false);
				config.setKeepExistingJavadoc(false);
				config.setReplaceExistingJavadoc(true);
			}
		}

		if (commentPublic != null) {
			config.setVisibilityPublic(commentPublic.booleanValue());
		}
		if (commentPackage != null) {
			config.setVisibilityPackage(commentPackage.booleanValue());
		}
		if (commentProtected != null) {
			config.setVisibilityProtected(commentProtected.booleanValue());
		}
		if (commentPrivate != null) {
			config.setVisibilityPrivate(commentPrivate.booleanValue());
		}

		if (commentTypes != null) {
			config.setCommentTypes(commentTypes.booleanValue());
		}
		if (commentFields != null) {
			config.setCommentFields(commentFields.booleanValue());
		}
		if (commentMethods != null) {
			config.setCommentMethods(commentMethods.booleanValue());
		}
		if (getsetOnly != null) {
			if (getsetOnly.booleanValue()) {
				config.setCommentMethods(true);
			}
			config.setGetterSetterOnly(getsetOnly.booleanValue());
		}
		if (excludeGetset != null) {
            config.setExcludeGetterSetter(excludeGetset.booleanValue());
        }
		if (todo != null) {
			config.setAddTodoForAutodoc(todo.booleanValue());
		}
		if (comment != null) {
			config.setCreateDummyComment(comment.booleanValue());
		}
		if (single != null) {
			config.setSingleLineComment(single.booleanValue());
		}
		if (format != null) {
			config.setUseEclipseFormatter(format.booleanValue());
		}
		if (getsetFromField != null) {
            config.setGetterSetterFromField(getsetFromField.booleanValue());
        }
		if (getsetFromFieldFirst != null) {
            config.setGetterSetterFromFieldFirst(getsetFromFieldFirst.booleanValue());
        }
		if (getsetFromFieldReplace != null) {
            config.setGetterSetterFromFieldReplace(getsetFromFieldReplace.booleanValue());
        }
		if (includeSubpackages != null) {
            config.setIncludeSubPackages(includeSubpackages.booleanValue());
        }
		if (header != null) {
			config.setAddHeader(header.booleanValue());
		}
		if (replaceHeader != null) {
			if (replaceHeader.booleanValue()) {
				config.setAddHeader(true);
			}
			config.setReplaceHeader(replaceHeader.booleanValue());
		}
		if (multiHeader != null) {
			config.setMultiCommentHeader(multiHeader.booleanValue());
		}

		return config;
	}

	/* (non-Javadoc)
	 * @see org.apache.tools.ant.ProjectComponent#getProject()
	 */
	public Project getProject() {
		if (super.getProject() == null) {
			// in debug environment the task is not correctly
			// initialized -> use dummy project
			super.setProject(new DummyProject());
		}
		return super.getProject();
	}

	// ----------------------------------------------------
	// inner classes
	// ----------------------------------------------------

	private class ProgressMonitor extends SubProgressMonitor {

		public ProgressMonitor(IProgressMonitor monitor, int ticks) {
			super(monitor, ticks);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.SubProgressMonitor#subTask(java.lang.String)
		 */
		public void subTask(String name) {
			if (verbose && name != null && name.length() > 0) {
				log(name);
			}
			super.subTask(name);
		}
	}

	private class AddJavadocRunner implements Runnable {
		private SourceManipulator sm;
		private Throwable error;

		public AddJavadocRunner(SourceManipulator sm) {
			this.sm = sm;
		}

		public void run() {
			try {
				if (!headerOnly) {
					sm.addJavadoc(null);
				}
				else {
					sm.setForceAddHeader(headerOnly);
					sm.addJavadoc(new IMember[0], null);
				}
			} catch (Exception e) {
				error = e;
			}
		}

		public void checkSuccess() throws Throwable {
			if (error != null) throw error;
		}
	}

	private class WorkingCopyCreator implements Runnable {
		private IEditorPart editor;
		private ICompilationUnit compUnit;
		private ICompilationUnit workingCopy;
		private Throwable error;

		public WorkingCopyCreator(ICompilationUnit compUnit, IEditorPart editor) {
			this.editor = editor;
			this.compUnit = compUnit;
		}

		public void run() {
			try {
				workingCopy = Utils.getWorkingCopy(compUnit, editor);
			} catch (Exception e) {
				error = e;
			}
		}

		public void checkSuccess() throws Throwable {
			if (error != null) throw error;
		}

		public ICompilationUnit getWorkingCopy() {
			return workingCopy;
		}
	}

	private class EditorFinder implements Runnable {
		private Throwable error;
		private IEditorPart editor;
		private ICompilationUnit compUnit;

		public EditorFinder(ICompilationUnit compUnit) {
			this.compUnit = compUnit;
		}

		public void run() {
			try {
				editor = Utils.findEditor(compUnit);
			} catch (Exception e) {
				error = e;
			}
		}

		public void checkSuccess() throws Throwable {
			if (error != null) throw error;
		}

		public IEditorPart getEditor() {
			return editor;
		}
	}

	/**
	 * Dummy project for debug environment.
	 */
	private static class DummyProject extends Project {

		public DummyProject() {
			setBasedir("D:/Work/workspaces/runtime-EclipseApplication/TestAntTask");
		}
		public String getName() {
			return "TestAntTask";
		}

		public void log(String message) {
			System.out.println(message);
		}

		public void log(String message, int msgLevel) {
			log(message);
		}

		public void log(Target target, String message, int msgLevel) {
			log(message);
		}

		public void log(Task task, String message, int msgLevel) {
			log(message);
		}
	}
}
