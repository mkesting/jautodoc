/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.search.TaskSearchMatch.Finding;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.dnd.EditorInputTransferDragAdapter;
import org.eclipse.jdt.internal.ui.dnd.JdtViewerDragAdapter;
import org.eclipse.jdt.internal.ui.dnd.ResourceTransferDragAdapter;
import org.eclipse.jdt.internal.ui.packageview.SelectionTransferDragAdapter;
import org.eclipse.jdt.internal.ui.search.JavaSearchEditorOpener;
import org.eclipse.jdt.internal.ui.viewsupport.DecoratingJavaLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResultPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * The search result view page.
 */
@SuppressWarnings("restriction")
public class TaskSearchResultPage extends AbstractTextSearchViewPage implements ISearchResultPage, ISelectionChangedListener {
    private static final int DEFAULT_ELEMENT_LIMIT = 1000;

    private static final String KEY_LIMIT = "net.sf.jautodoc.search.resultpage.limit"; //$NON-NLS-1$
    private static final String KEY_SORTING = "net.sf.jautodoc.search.resultpage.sorting"; //$NON-NLS-1$
    private static final String KEY_GROUPING = "net.sf.jautodoc.search.resultpage.grouping"; //$NON-NLS-1$

    private static final String KEY_SPLITTER_WEIGHT1 = "net.sf.jautodoc.search.resultpage.weight1"; //$NON-NLS-1$
    private static final String KEY_SPLITTER_WEIGHT2 = "net.sf.jautodoc.search.resultpage.weight2"; //$NON-NLS-1$

    private static final String GROUP_GROUPING= "net.sf.jautodoc.search.resultpage.grouping"; //$NON-NLS-1$
    private static final String GROUP_FILTERING = "net.sf.jautodoc.search.resultpage.filtering"; //$NON-NLS-1$

    private SashForm viewSplitter;
    private int[] splitterWeights = new int[] {3, 1};

    private TaskSearchDetailViewer detailViewer;

    //private TaskSearchViewActionGroup actionGroup;

    private TaskSearchSortAction sortByNameAction;
    private TaskSearchSortAction sortByParentName;
    private TaskSearchSortAction sortByPathAction;

    private TaskSearchGroupAction groupFileAction;
    private TaskSearchGroupAction groupPackageAction;
    private TaskSearchGroupAction groupProjectAction;

    private int currentGrouping;
    private int currentSortOrder;

    private TaskSearchContentProvider contentProvider;
    private TaskSearchSortingLabelProvider sortingLabelProvider;

    private JavaSearchEditorOpener editorOpener= new JavaSearchEditorOpener();


    public TaskSearchResultPage() {
        initSortActions();
        initGroupingActions();
        setElementLimit(Integer.valueOf(DEFAULT_ELEMENT_LIMIT));
    }

    public IJavaElement[] getFilteredJavaElements() {
        final List<IJavaElement> javaElements = new ArrayList<IJavaElement>();
        final TaskSearchResult taskSearchResult = (TaskSearchResult)getInput();

        final Object[] elements = taskSearchResult.getElements();
        for (Object element : elements) {
            if (element instanceof IJavaElement && taskSearchResult.getFilteredFindingCount(element) > 0) {
                javaElements.add((IJavaElement)element);
            }
        }
        return javaElements.toArray(new IJavaElement[javaElements.size()]);
    }

    @Override
    protected TableViewer createTableViewer(final Composite parent) {
        disposeCurrentView();
        viewSplitter = new SashForm(parent, SWT.HORIZONTAL);
        final TableViewer tableViewer = super.createTableViewer(viewSplitter);
        tableViewer.addSelectionChangedListener(this);
        createDetailViewer(viewSplitter);
        viewSplitter.setWeights(splitterWeights);
        return tableViewer;
    }

    @Override
    protected TreeViewer createTreeViewer(final Composite parent) {
        disposeCurrentView();
        viewSplitter = new SashForm(parent, SWT.HORIZONTAL);
        final TreeViewer treeViewer = super.createTreeViewer(viewSplitter);
        treeViewer.addSelectionChangedListener(this);
        createDetailViewer(viewSplitter);
        viewSplitter.setWeights(splitterWeights);
        return treeViewer;
    }

    private void createDetailViewer(final Composite parent) {
        detailViewer = new TaskSearchDetailViewer(parent, this);
    }

    @Override
    public void gotoNextMatch() {
        super.gotoNextMatch();
        if (getViewer() instanceof TableViewer) {
            // workaround, because no SelectionChangedEvent is fired
            handleSelectionChanged(getViewer().getSelection());
        }
    }

    @Override
    public void gotoPreviousMatch() {
        super.gotoPreviousMatch();
        if (getViewer() instanceof TableViewer) {
            // workaround, because no SelectionChangedEvent is fired
            handleSelectionChanged(getViewer().getSelection());
        }
    }

    public void selectionChanged(SelectionChangedEvent e) {
        if (e.getSelectionProvider() != getViewer()) {
            return;
        }
        handleSelectionChanged(e.getSelection());
    }

    @Override
    public void setViewPart(ISearchResultViewPart part) {
        super.setViewPart(part);
        //actionGroup = new TaskSearchViewActionGroup(part);
    }

    @Override
    public void init(IPageSite site) {
        super.init(site);

        final IMenuManager menuManager = site.getActionBars().getMenuManager();
        menuManager.insertBefore(IContextMenuConstants.GROUP_PROPERTIES, new Separator(GROUP_FILTERING));

        //actionGroup.fillActionBars(site.getActionBars());

        menuManager.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, new Action("Preferences...") {
            public void run() {
                String pageId= "org.eclipse.search.preferences.SearchPreferencePage"; //$NON-NLS-1$
                PreferencesUtil.createPreferenceDialogOn(JavaPlugin.getActiveWorkbenchShell(), pageId, null, null).open();
            }
        });
    }

    @Override
    public void dispose() {
        //actionGroup.dispose();
        super.dispose();
    }

    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);

        memento.putInteger(KEY_SORTING, currentSortOrder);
        memento.putInteger(KEY_GROUPING, currentGrouping);

        final int limit= getElementLimit().intValue();
        memento.putInteger(KEY_LIMIT, limit);

        memento.putInteger(KEY_SPLITTER_WEIGHT1, viewSplitter.getWeights()[0]);
        memento.putInteger(KEY_SPLITTER_WEIGHT2, viewSplitter.getWeights()[1]);
    }

    @Override
    public void restoreState(IMemento memento) {
        super.restoreState(memento);

        if (memento == null) {
            setElementLimit(DEFAULT_ELEMENT_LIMIT);
            currentGrouping = TaskSearchTreeContentProvider.LEVEL_PACKAGE;
            currentSortOrder = TaskSearchSortingLabelProvider.SHOW_ELEMENT_CONTAINER;
        }
        else {
            setElementLimit(getInt(memento, KEY_LIMIT, DEFAULT_ELEMENT_LIMIT));
            currentGrouping = getInt(memento, KEY_GROUPING, TaskSearchTreeContentProvider.LEVEL_PACKAGE);
            currentSortOrder = getInt(memento, KEY_SORTING, TaskSearchSortingLabelProvider.SHOW_ELEMENT_CONTAINER);

            splitterWeights[0] = getInt(memento, KEY_SPLITTER_WEIGHT1, splitterWeights[0]);
            splitterWeights[1] = getInt(memento, KEY_SPLITTER_WEIGHT2, splitterWeights[1]);
        }
    }

    public int getDisplayedFindingsCount(final Object element) {
        final TaskSearchResult searchResult = (TaskSearchResult) getInput();
        if (searchResult.getActiveMatchFilters() == null) {
            return searchResult.getFindingCount(element);
        }
        else {
            return searchResult.getFilteredFindingCount(element);
        }
    }

    public void setSortOrder(int order) {
        if (sortingLabelProvider != null) {
            currentSortOrder = order;
            sortingLabelProvider.setOrder(order);
            getViewer().refresh();
        }
    }

    public void setGrouping(int grouping) {
        currentGrouping = grouping;
        final TaskSearchTreeContentProvider cp = (TaskSearchTreeContentProvider) getViewer().getContentProvider();
        cp.setLevel(grouping);
        updateGroupingActions();
        getViewPart().updateLabel();
    }

    @Override
    protected void clear() {
        if (contentProvider != null) {
            contentProvider.clear();
        }
    }

    @Override
    protected void configureTableViewer(final TableViewer viewer) {
        viewer.setUseHashlookup(true);
        sortingLabelProvider = new TaskSearchSortingLabelProvider(this);
        viewer.setLabelProvider(new DecoratingJavaLabelProvider(sortingLabelProvider, false));
        contentProvider = new TaskSearchTableContentProvider(this);
        viewer.setContentProvider(contentProvider);
        viewer.setComparator(new DecoratorIgnoringViewerSorter(sortingLabelProvider));
        setSortOrder(currentSortOrder);
        addDragAdapters(viewer);

		// TODO: HoverInformation
        //HoverInformationControlManager hicm = new HoverInformationControlManager(viewer);
        //MyInformationControlReplacer icr = new MyInformationControlReplacer(viewer);
        //hicm.installInformationControlReplacer(icr);
    }

    @Override
    protected void configureTreeViewer(TreeViewer viewer) {
        viewer.setUseHashlookup(true);
        TaskSearchPostfixLabelProvider postfixLabelProvider= new TaskSearchPostfixLabelProvider(this);
        viewer.setLabelProvider(new DecoratingJavaLabelProvider(postfixLabelProvider, false));
        contentProvider= new TaskSearchTreeContentProvider(this, currentGrouping);
        viewer.setContentProvider(contentProvider);
        viewer.setComparator(new DecoratorIgnoringViewerSorter(postfixLabelProvider));
        addDragAdapters(viewer);

		// TODO: HoverInformation
        //HoverInformationControlManager hicm = new HoverInformationControlManager(viewer);
        //MyInformationControlReplacer icr = new MyInformationControlReplacer(viewer);
        //hicm.installInformationControlReplacer(icr);
    }

    @Override
    protected void elementsChanged(Object[] objects) {
        final TaskSearchResult taskSearchResult = (TaskSearchResult)getInput();
        if (taskSearchResult != null) {
            taskSearchResult.applyFilterState(objects);
        }

        if (contentProvider != null) {
            contentProvider.elementsChanged(objects);
        }

        // update detail viewer
        handleSelectionChanged(getViewer().getSelection());
    }

    @Override
    protected StructuredViewer getViewer() {
        // override so that it's visible in the package.
        return super.getViewer();
    }

    @Override
    protected void fillToolbar(IToolBarManager tbm) {
        super.fillToolbar(tbm);
        if (getLayout() == FLAG_LAYOUT_TREE)
            addGroupActions(tbm);
    }

    @Override
    protected void fillContextMenu(IMenuManager mgr) {
        super.fillContextMenu(mgr);
        addSortActions(mgr);

        //actionGroup.setContext(new ActionContext(getSite().getSelectionProvider().getSelection()));
        //actionGroup.fillContextMenu(mgr);
    }

    @Override
    protected void handleOpen(OpenEvent event) {
        final Object firstElement = ((IStructuredSelection) event.getSelection()).getFirstElement();
        if (firstElement instanceof ICompilationUnit || firstElement instanceof IClassFile
                || firstElement instanceof IMember) {
            if (getDisplayedMatchCount(firstElement) == 0) {
                try {
                    editorOpener.openElement(firstElement);
                } catch (CoreException e) {
                    JAutodocPlugin.getDefault().handleException(getSite().getShell(), e);
                }
                return;
            }
        }
        super.handleOpen(event);
    }

    @Override
    protected void showMatch(final Match match, final int offset, final int length, final boolean activate)
            throws PartInitException {
        final IEditorPart editor = editorOpener.openMatch(match);

        if (editor != null && activate) {
            editor.getEditorSite().getPage().activate(editor);
        }

        final Object element = match.getElement();
        if (editor instanceof ITextEditor) {
            final ITextEditor textEditor = (ITextEditor) editor;
            textEditor.selectAndReveal(offset, length);
        }
        else if (editor != null && element instanceof IFile) {
            final IFile file = (IFile) element;
            showWithMarker(editor, file, offset, length);
        }
    }

    private void showWithMarker(final IEditorPart editor, final IFile file, final int offset, final int length)
            throws PartInitException {
        try {
            final IMarker marker = file.createMarker(NewSearchUI.SEARCH_MARKER);
            final Map<String, Integer> attributes = new HashMap<String, Integer>(4);
            attributes.put(IMarker.CHAR_START, Integer.valueOf(offset));
            attributes.put(IMarker.CHAR_END, Integer.valueOf(offset + length));
            marker.setAttributes(attributes);
            IDE.gotoMarker(editor, marker);
            marker.delete();
        } catch (CoreException e) {
            throw new PartInitException("Could not create marker", e);
        }
    }

    private void addGroupActions(IToolBarManager mgr) {
        mgr.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, new Separator(GROUP_GROUPING));
        mgr.appendToGroup(GROUP_GROUPING, groupProjectAction);
        mgr.appendToGroup(GROUP_GROUPING, groupPackageAction);
        mgr.appendToGroup(GROUP_GROUPING, groupFileAction);

        updateGroupingActions();
    }

    private void initGroupingActions() {
        groupProjectAction= new TaskSearchGroupAction("Project", "Group by Project", this, TaskSearchTreeContentProvider.LEVEL_PROJECT);
        JavaPluginImages.setLocalImageDescriptors(groupProjectAction, "prj_mode.gif"); //$NON-NLS-1$
        groupPackageAction= new TaskSearchGroupAction("Package", "Group by Package", this, TaskSearchTreeContentProvider.LEVEL_PACKAGE);
        JavaPluginImages.setLocalImageDescriptors(groupPackageAction, "package_mode.gif"); //$NON-NLS-1$
        groupFileAction= new TaskSearchGroupAction("File", "Group by File", this, TaskSearchTreeContentProvider.LEVEL_FILE);
        JavaPluginImages.setLocalImageDescriptors(groupFileAction, "file_mode.gif"); //$NON-NLS-1$
    }

    private void updateGroupingActions() {
        groupProjectAction.setChecked(currentGrouping == TaskSearchTreeContentProvider.LEVEL_PROJECT);
        groupPackageAction.setChecked(currentGrouping == TaskSearchTreeContentProvider.LEVEL_PACKAGE);
        groupFileAction.setChecked(currentGrouping == TaskSearchTreeContentProvider.LEVEL_FILE);
    }

    private void initSortActions() {
        sortByPathAction= new TaskSearchSortAction("Path", this, TaskSearchSortingLabelProvider.SHOW_PATH);
        sortByNameAction= new TaskSearchSortAction("Name", this, TaskSearchSortingLabelProvider.SHOW_ELEMENT_CONTAINER);
        sortByParentName= new TaskSearchSortAction("Parent Name", this, TaskSearchSortingLabelProvider.SHOW_CONTAINER_ELEMENT);
    }

    private void addSortActions(IMenuManager mgr) {
        if (getLayout() == FLAG_LAYOUT_FLAT) {
            MenuManager sortMenu= new MenuManager("Sort By");
            sortMenu.add(sortByNameAction);
            sortMenu.add(sortByPathAction);
            sortMenu.add(sortByParentName);

            sortByNameAction.setChecked(currentSortOrder == sortByNameAction.getSortOrder());
            sortByPathAction.setChecked(currentSortOrder == sortByPathAction.getSortOrder());
            sortByParentName.setChecked(currentSortOrder == sortByParentName.getSortOrder());

            mgr.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, sortMenu);
        }
    }

    private void addDragAdapters(final StructuredViewer viewer) {
        final Transfer[] transfers= new Transfer[] { LocalSelectionTransfer.getTransfer(), ResourceTransfer.getInstance() };
        final int ops = DND.DROP_COPY | DND.DROP_LINK;

        final JdtViewerDragAdapter dragAdapter= new JdtViewerDragAdapter(viewer);
        dragAdapter.addDragSourceListener(new SelectionTransferDragAdapter(viewer));
        dragAdapter.addDragSourceListener(new EditorInputTransferDragAdapter(viewer));
        dragAdapter.addDragSourceListener(new ResourceTransferDragAdapter(viewer));
        viewer.addDragSupport(ops, transfers, dragAdapter);
    }

    private void handleSelectionChanged(final ISelection selection) {
        final IStructuredSelection sselection = (IStructuredSelection)selection;
        if (sselection.size() == 1) {
            final TaskSearchResult searchResult = (TaskSearchResult)getInput();
            final Object element = sselection.getFirstElement();
            final Match[] matches = searchResult.getMatches(element);

            final List<Finding> findings = new ArrayList<Finding>();
            for (Match match : matches) {
                final TaskSearchMatch taskSearchMatch = (TaskSearchMatch)match;
                findings.addAll(Arrays.asList(taskSearchMatch.getFilteredFindings()));
            }
            detailViewer.setInput(findings);
        }
        else {
            detailViewer.clearViewer();
        }
    }

    private void disposeCurrentView() {
        if (detailViewer != null) {
            detailViewer.getControl().dispose();
            detailViewer = null;
        }

        if (viewSplitter != null) {
            viewSplitter.dispose();
            viewSplitter = null;
        }
    }

    private int getInt(final IMemento memento, final String key, final int defaultValue) {
        final Integer value = memento.getInteger(key);
        return value == null ? defaultValue : value.intValue();
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    public static class DecoratorIgnoringViewerSorter extends ViewerComparator {

        private final ILabelProvider labelProvider;

        public DecoratorIgnoringViewerSorter(final ILabelProvider labelProvider) {
            this. labelProvider= labelProvider;
        }

        @SuppressWarnings("unchecked")
        public int compare(final Viewer viewer, final Object e1, final Object e2) {

            if (e1 instanceof ICompilationUnit && e2 instanceof IMember) {
                final ICompilationUnit cu1 = (ICompilationUnit)e1;
                final ICompilationUnit cu2 = ((IMember)e2).getCompilationUnit();

                final int rc = compare(viewer, cu1, cu2);
                if (rc != 0) {
                    return rc;
                }
                return -1;
            }
            else if (e1 instanceof IMember && e2 instanceof ICompilationUnit) {
                final ICompilationUnit cu1 = ((IMember)e1).getCompilationUnit();
                final ICompilationUnit cu2 = (ICompilationUnit)e2;

                final int rc = compare(viewer, cu1, cu2);
                if (rc != 0) {
                    return rc;
                }
                return 1;
            }
            else if (e1 instanceof IMember && e2 instanceof IMember) {
                final IMember m1 = (IMember)e1;
                final IMember m2 = (IMember)e2;
                final int rc = compare(viewer, m1.getCompilationUnit(), m2.getCompilationUnit());
                if (rc != 0) {
                    return rc;
                }
                return getOffset(m1) - getOffset(m2);
            }

            final String name1= noNull(labelProvider.getText(e1));
            final String name2= noNull(labelProvider.getText(e2));
            return getComparator().compare(name1, name2);
        }

        private String noNull(final String string) {
            return string == null ? "" : string;
        }

        private int getOffset(final IMember member) {
            try {
                return member.getNameRange().getOffset();
            } catch (JavaModelException e) {
                return -1;
            }
        }
    }
}
