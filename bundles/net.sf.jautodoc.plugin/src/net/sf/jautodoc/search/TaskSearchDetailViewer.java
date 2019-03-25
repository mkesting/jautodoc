/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.Collections;

import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.search.TaskSearchMatch.Finding;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;

/**
 * Shows all findings of an selected element in search result view.
 */
public class TaskSearchDetailViewer extends TableViewer {
    private static final String COLUMN_HEADERS[] = { "", "Finding" };

    private static final ColumnLayoutData COLUMN_LAYOUTS[] = {
            new ColumnPixelData(18, false, true),
            new ColumnWeightData(100) };

    private final TaskSearchResultPage searchResultPage;


    public TaskSearchDetailViewer(final Composite parent, final TaskSearchResultPage searchResultPage) {
        super(createTable(parent));
        this.searchResultPage = searchResultPage;

        createColumns();
        createOpenAction();
        setContentProvider(new ArrayContentProvider());
        setLabelProvider(new DetailLabelProvider());
        setComparator(new FindingComparator());
        setInput(Collections.EMPTY_LIST);
    }

    void clearViewer() {
        setInput(""); //$NON-NLS-1$
    }

    private static Table createTable(Composite parent) {
        return new Table(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
    }

    private void createColumns() {
        final TableLayout layout = new TableLayout();
        getTable().setLayout(layout);
        getTable().setHeaderVisible(true);
        for (int i = 0; i < COLUMN_HEADERS.length; i++) {
            layout.addColumnData(COLUMN_LAYOUTS[i]);
            final TableColumn tc = new TableColumn(getTable(), SWT.NONE, i);
            tc.setResizable(COLUMN_LAYOUTS[i].resizable);
            tc.setText(COLUMN_HEADERS[i]);
        }
    }

    private void createOpenAction() {
        final OpenFindingAction openFindingAction = new OpenFindingAction();

        initContextMenu(new IMenuListener() {
            public void menuAboutToShow(IMenuManager menu) {
                menu.add(new GroupMarker(IContextMenuConstants.GROUP_SHOW));
                menu.appendToGroup(IContextMenuConstants.GROUP_SHOW, openFindingAction);
            }
        });

        addOpenListener(new IOpenListener() {
            public void open(OpenEvent event) {
                if (openFindingAction.isEnabled()) {
                    openFindingAction.run((StructuredSelection)TaskSearchDetailViewer.this.getSelection());
                }
            }
        });
    }

    private void initContextMenu(final IMenuListener menuListener) {
        final MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(menuListener);

        final Menu menu = menuMgr.createContextMenu(getControl());
        getControl().setMenu(menu);
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    private static class DetailLabelProvider extends LabelProvider implements ITableLabelProvider {
        private static final int COLUMN_ICON = 0;
        private static final int COLUMN_FINDING = 1;


        public String getText(Object element) {
            return getColumnText(element, COLUMN_FINDING);
        }

        public Image getImage(Object element) {
            return getColumnImage(element, COLUMN_ICON);
        }

        public Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == COLUMN_ICON) {
                return ResourceManager.getImage(ResourceManager.MATCH_DETAIL_IMAGE);
            }
            return null;
        }

        public String getColumnText(final Object element, final int columnIndex) {
            if (columnIndex == COLUMN_ICON || !(element instanceof Finding)) {
                return "";
            }
            return ((Finding)element).getMessage();
        }
    }

    private static class FindingComparator extends ViewerComparator {
        @Override
        @SuppressWarnings("unchecked")
        public int compare(final Viewer viewer, final Object e1, final Object e2) {
            if (e1 instanceof Finding && e2 instanceof Finding) {
                final Finding f1 = (Finding)e1;
                final Finding f2 = (Finding)e2;

                int rc = f1.getMatch().getOffset() - f2.getMatch().getOffset();
                if (rc != 0) {
                    return rc;
                }

                rc = f1.getId().ordinal() - f2.getId().ordinal();
                if (rc != 0) {
                    return rc;
                }

                rc = f1.getMessage().length() - f2.getMessage().length();
                if (rc != 0) {
                    return rc;
                }
                return getComparator().compare(f1.getMessage(), f2.getMessage());
            }
            return getComparator().compare(e1.toString(), e2.toString());
        }
    }

    private class OpenFindingAction extends SelectionDispatchAction {

        public OpenFindingAction() {
            super(searchResultPage.getSite());
            setText("Open");
            setToolTipText("Open Finding");
        }


        @Override
        public boolean isEnabled() {
            return checkEnabled((IStructuredSelection)getSelection());
        }

        /* (non-Javadoc)
         * @see org.eclipse.jdt.ui.actions.SelectionDispatchAction#getSelection()
         */
        public ISelection getSelection() {
            return TaskSearchDetailViewer.this.getSelection();
        }

        /* (non-Javadoc)
         * Method declared on SelectionDispatchAction.
         */
        public void run(IStructuredSelection selection) {
            if (checkEnabled(selection)) {
                final Finding finding = (Finding)selection.getFirstElement();
                showMatch(finding.getMatch());
            }
        }

        private boolean checkEnabled(IStructuredSelection selection) {
            return selection.size() == 1 && selection.getFirstElement() instanceof Finding;
        }

        private void showMatch(final Match match) {
            final ISafeRunnable runnable = new ISafeRunnable() {
                public void handleException(Throwable exception) {
                    if (exception instanceof PartInitException) {
                        final PartInitException pie = (PartInitException) exception;
                        ErrorDialog.openError(getSite().getShell(), "Show Match",
                                "Could not find an editor for the current match", pie.getStatus());
                    }
                }

                public void run() throws Exception {
                    searchResultPage.showMatch(match, match.getOffset(), match.getLength(), true);
                }
            };
            SafeRunner.run(runnable);
        }
    }
}
