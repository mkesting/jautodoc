/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.viewsupport.JavaElementImageProvider;
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.MultiStateTextFileChange;
import org.eclipse.ltk.core.refactoring.TextEditBasedChange;
import org.eclipse.ltk.core.refactoring.TextEditBasedChangeGroup;
import org.eclipse.ltk.ui.refactoring.LanguageElementNode;
import org.eclipse.ltk.ui.refactoring.TextEditChangeNode;
import org.eclipse.text.edits.TextEdit;

/**
 * Used in change tree of preview page.
 * <p>
 * Adaptation of org.eclipse.jdt.internal.ui.refactoring.CompilationUnitChangeNode
 * </p>
 */
@SuppressWarnings("restriction")
public class CompilationUnitChangeNode extends TextEditChangeNode {

    static final ChildNode[] EMPTY_CHILDREN = new ChildNode[0];

    private static class JavaLanguageNode extends LanguageElementNode {

        private static JavaElementImageProvider imageProvider = new JavaElementImageProvider();

        private final IJavaElement javaElement;


        public JavaLanguageNode(final TextEditChangeNode parent, final IJavaElement javaElement) {
            super(parent);
            this.javaElement = javaElement;
            Assert.isNotNull(javaElement);
        }

        public JavaLanguageNode(final ChildNode parent, final IJavaElement javaElement) {
            super(parent);
            this.javaElement = javaElement;
            Assert.isNotNull(javaElement);
        }

        @Override
        public String getText() {
            return JavaElementLabels.getElementLabel(javaElement, JavaElementLabels.ALL_DEFAULT);
        }

        @Override
        public ImageDescriptor getImageDescriptor() {
            return imageProvider.getJavaImageDescriptor(
                javaElement,
                JavaElementImageProvider.OVERLAY_ICONS | JavaElementImageProvider.SMALL_ICONS);
        }

        @Override
        public IRegion getTextRange() throws CoreException {
            final ISourceRange range= ((ISourceReference)javaElement).getSourceRange();
            return new Region(range.getOffset(), range.getLength());
        }
    }

    public CompilationUnitChangeNode(final TextEditBasedChange change) {
        super(change);
    }

    @Override
    protected ChildNode[] createChildNodes() {
        final TextEditBasedChange change = getTextEditBasedChange();
        if (change instanceof MultiStateTextFileChange) {
            // no edit preview & edit disabling possible in the MultiStateTextFileChange
            // (edits must be applied in sequence)
            return EMPTY_CHILDREN;
        }

        final ICompilationUnit compUnit = (ICompilationUnit) change.getAdapter(ICompilationUnit.class);
        if (compUnit != null) {
            final List<ChildNode> children = new ArrayList<ChildNode>(5);
            final Map<IJavaElement, JavaLanguageNode> map = new HashMap<IJavaElement, JavaLanguageNode>(20);
            final TextEditBasedChangeGroup[] changes = getSortedChangeGroups(change);

            for (int i = 0; i < changes.length; i++) {
                final TextEditBasedChangeGroup tec = changes[i];
                try {
                    final IJavaElement element = getModifiedJavaElement(tec, compUnit);
                    if (element.equals(compUnit)) {
                        children.add(createTextEditGroupNode(this, tec));
                    } else {
                        final JavaLanguageNode pjce = getChangeElement(map, element, children, this);
                        pjce.addChild(createTextEditGroupNode(pjce, tec));
                    }
                } catch (JavaModelException e) {
                    children.add(createTextEditGroupNode(this, tec));
                }
            }
            return children.toArray(new ChildNode[children.size()]);
        } else {
            return EMPTY_CHILDREN;
        }
    }

    private static class OffsetComparator implements Comparator<TextEditBasedChangeGroup> {
        public int compare(final TextEditBasedChangeGroup g1, final TextEditBasedChangeGroup g2) {
            final int p1 = getOffset(g1);
            final int p2 = getOffset(g2);
            if (p1 < p2) {
                return -1;
            }
            if (p1 > p2) {
                return 1;
            }
            return 0; // same offset
        }
        private int getOffset(final TextEditBasedChangeGroup edit) {
            return edit.getRegion().getOffset();
        }
    }

    private TextEditBasedChangeGroup[] getSortedChangeGroups(final TextEditBasedChange change) {
        final TextEditBasedChangeGroup[] edits = change.getChangeGroups();
        final List<TextEditBasedChangeGroup> result= new ArrayList<TextEditBasedChangeGroup>(edits.length);
        for (int i= 0; i < edits.length; i++) {
            if (!edits[i].getTextEditGroup().isEmpty())
                result.add(edits[i]);
        }

        Collections.sort(result, new OffsetComparator());
        return result.toArray(new TextEditBasedChangeGroup[result.size()]);
    }

    private IJavaElement getModifiedJavaElement(final TextEditBasedChangeGroup edit, final ICompilationUnit compUnit)
            throws JavaModelException {
        final IRegion range = edit.getRegion();
        if (range.getOffset() == 0 && range.getLength() == 0) {
            return compUnit;
        }

        IJavaElement result = compUnit.getElementAt(range.getOffset() + 1);
        if (result == null) {
            return compUnit;
        }

        try {
            while(true) {
                final ISourceReference ref = (ISourceReference)result;
                final IRegion sRange = new Region(ref.getSourceRange().getOffset(), ref.getSourceRange().getLength());
                if (result.getElementType() == IJavaElement.COMPILATION_UNIT || result.getParent() == null
                        || coveredBy(edit, sRange)) {
                    break;
                }
                result = result.getParent();
            }
        } catch(JavaModelException e) {
            // Do nothing, use old value.
        } catch(ClassCastException e) {
            // Do nothing, use old value.
        }
        return result;
    }

    private JavaLanguageNode getChangeElement(final Map<IJavaElement, JavaLanguageNode> map,
            final IJavaElement element, final List<ChildNode> children, final TextEditChangeNode cunitChange) {

        JavaLanguageNode result = map.get(element);
        if (result != null) {
            return result;
        }

        final IJavaElement parent = element.getParent();
        if (parent instanceof ICompilationUnit) {
            result = new JavaLanguageNode(cunitChange, element);
            children.add(result);
            map.put(element, result);
        } else {
            final JavaLanguageNode parentChange = getChangeElement(map, parent, children, cunitChange);
            result = new JavaLanguageNode(parentChange, element);
            parentChange.addChild(result);
            map.put(element, result);
        }
        return result;
    }

    private boolean coveredBy(final TextEditBasedChangeGroup group, final IRegion sourceRegion) {
        final int sLength = sourceRegion.getLength();
        if (sLength == 0) {
            return false;
        }

        final int sOffset = sourceRegion.getOffset();
        final int sEnd = sOffset + sLength - 1;

        final TextEdit[] edits = group.getTextEdits();
        for (int i= 0; i < edits.length; i++) {
            final TextEdit edit = edits[i];
            if (edit.isDeleted()) {
                return false;
            }

            final int rOffset = edit.getOffset();
            final int rLength = edit.getLength();
            final int rEnd = rOffset + rLength - 1;

            if (rLength == 0) {
                if (!(sOffset <= rOffset && rOffset <= sEnd)) {
                    return false;
                }
            } else {
                if (!(sOffset <= rOffset && rEnd <= sEnd)) {
                    return false;
                }
            }
        }
        return true;
    }
}
