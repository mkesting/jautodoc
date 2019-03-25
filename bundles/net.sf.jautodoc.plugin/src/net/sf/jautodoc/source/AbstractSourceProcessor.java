/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.source;

import java.util.ArrayList;
import java.util.List;

import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.preferences.IMemberFilter;
import net.sf.jautodoc.utils.SourceUtils;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jface.text.IDocument;

/**
 * Abstract base class for source manipulator and task search engine.
 */
public abstract class AbstractSourceProcessor {
    protected IDocument document;
    protected IScanner commentScanner;

    protected final ICompilationUnit compUnit;
    protected final Configuration  config;
    protected final JavadocCreator javadocCreator;

    protected AbstractSourceProcessor(final ICompilationUnit compUnit) {
        this(compUnit, null);
    }

    protected AbstractSourceProcessor(final ICompilationUnit compUnit, final Configuration config) {
        this.config = config == null ? ConfigurationManager.getConfiguration(compUnit) : config;
        this.compUnit = compUnit;
        this.javadocCreator = new JavadocCreator(this.config);
    }

    protected void doProcessing(final IMemberFilter filter, final IProgressMonitor monitor) throws Exception {
        final IType type = compUnit.findPrimaryType();
        if (type != null) {
            final List<IMember> members = new ArrayList<IMember>();
            if (SourceUtils.isMatchingType(type, filter)) {
                members.add(type);
            }
            SourceUtils.getMembers(type, members, filter);
            doProcessing(members.toArray(new IMember[members.size()]), monitor);
        }
        // else i.e. package-info.java -> ignore
    }

    protected void doProcessing(IMember[] members, IProgressMonitor monitor) throws Exception {
        if (monitor == null) monitor = new NullProgressMonitor();

        compUnit.reconcile(ICompilationUnit.NO_AST, false, null, null);

        final ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
        final IPath path = compUnit.getPath();

        manager.connect(path, LocationKind.NORMALIZE, null);
        try {
            document = manager.getTextFileBuffer(path, LocationKind.NORMALIZE).getDocument();

            commentScanner = ToolFactory.createScanner(true, false, false, true);
            commentScanner.setSource(document.get().toCharArray());

            monitor.beginTask(getTaskName(), members.length + 5);

            startProcessing();
            monitor.worked(1);

            processFileHeader();
            monitor.worked(1);

            processTodoForAutodoc(members);
            monitor.worked(1);

            processMembers(SourceUtils.sortMembers(members), monitor);
            monitor.worked(1);

            stopProcessing();
            monitor.worked(1);
        } finally {
            manager.disconnect(path, LocationKind.NORMALIZE, null);
        }
    }

    protected abstract void startProcessing() throws Exception;
    protected abstract void processFileHeader() throws Exception;
    protected abstract void processTodoForAutodoc(IMember[] members) throws Exception;
    protected abstract void processMember(IMember member) throws Exception;
    protected abstract void stopProcessing() throws Exception;

    protected abstract String getTaskName();

    private void processMembers(final IMember[] members, final IProgressMonitor monitor) throws Exception {
        for (int i = 0; i < members.length; ++i) {
            if (monitor.isCanceled()) {
                break;
            }

            final IMember member = members[i];
            monitor.subTask(member.getElementName());

            processMember(member);

            monitor.worked(1);
        }
    }
}
