/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;


/**
 * Operation for adding new files to the workspace.
 */
public class NewFileOperation extends WorkspaceModifyOperation {
	
	private IFile[] files;
	private String[] contents;
	

	/**
	 * Instantiates a new file operation.
	 * 
	 * @param files the files
	 */
	public NewFileOperation(IFile[] files) {
		this(files, null);
	}
	
	/**
	 * Instantiates a new new file operation.
	 * 
	 * @param files the files
	 * @param contents the contents
	 */
	public NewFileOperation(IFile[] files, String[] contents) {
		this.files = files;
		this.contents = contents;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.WorkspaceModifyOperation#execute(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void execute(IProgressMonitor monitor) throws CoreException,
			InvocationTargetException, InterruptedException {
		monitor.beginTask("Add Package Javadoc", files.length * 100);
		
		for (int i = 0; i < files.length; ++i) {
			if (monitor.isCanceled()) throw new InterruptedException();
			
			if (files[i].exists()) {
				monitor.worked(100);
				continue;
			}
			createFile(files[i], contents[i], new SubProgressMonitor(monitor, 100));
		}
	}

	private void createFile(IFile file, String content, IProgressMonitor monitor)
			throws CoreException {
		try {
			file.create(getInputStream(content), false, monitor);
		} catch (CoreException e) {
			// If the file already existed locally, just refresh to get contents
			int code = e.getStatus().getCode();
			if (code == IResourceStatus.EXISTS_LOCAL ||
				code == IResourceStatus.FAILED_WRITE_LOCAL) {
				file.refreshLocal(IResource.DEPTH_ZERO, null);
			} else {
				throw e;
			}
		}
	}
	
	private InputStream getInputStream(String content) {
		if (content == null) return null;
		
		return new ByteArrayInputStream(content.getBytes());
	}
}
