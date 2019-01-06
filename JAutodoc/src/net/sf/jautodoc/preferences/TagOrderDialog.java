/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Edit tag order dialog.
 */
public class TagOrderDialog extends Dialog {

    private final List<String> tagOrder;

    private Button addButton;
    private Button removeButton;
    private Button upButton;
    private Button downButton;
    private ListViewer listViewer;


    /**
     * Instantiates a new tag order dialog.
     *
     * @param parentShell the parent shell
     * @param tagOrder the initial tag order
     */
    public TagOrderDialog(final Shell parentShell, final List<String> tagOrder) {
        super(parentShell);
        this.tagOrder = tagOrder;
    }

    /**
     * Gets the resulting tag order.
     *
     * @return the resulting tag order
     */
    public List<String> getTagOrder() {
        return tagOrder;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Edit Tag Order");
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(340, 220));
        composite.setLayout(new GridLayout(2, false));

        createListViewerPanel(composite);
        createButtonPanel(composite);

        updateEnablement();
        addSelectionListener();

        return composite;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.CLIENT_ID, "Restore Defaults", false);
        super.createButtonsForButtonBar(parent);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    @Override
    protected void buttonPressed(final int buttonId) {
        if (buttonId == IDialogConstants.CLIENT_ID) {
            tagOrder.clear();
            tagOrder.addAll(Arrays.asList(Constants.DEFAULT_TAG_ORDER.split(",")));
            listViewer.setInput(tagOrder);
        } else {
            super.buttonPressed(buttonId);
        }
    }

    private void createListViewerPanel(final Composite parent) {
        listViewer = new ListViewer(parent, SWT.BORDER | SWT.V_SCROLL);
        listViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

        listViewer.setContentProvider(ArrayContentProvider.getInstance());
        listViewer.setInput(tagOrder);
    }

    private void createButtonPanel(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_VERTICAL));

        upButton = new Button(composite, SWT.NONE);
        upButton.setText("Up");
        upButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        downButton = new Button(composite, SWT.NONE);
        downButton.setText("Down");
        downButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addButton = new Button(composite, SWT.NONE);
        addButton.setText("Add...");
        final GridData addGridData = new GridData(GridData.FILL_HORIZONTAL);
        addGridData.verticalIndent = 20;
        addButton.setLayoutData(addGridData);

        removeButton = new Button(composite, SWT.NONE);
        removeButton.setText("Remove");
        removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    private void addSelectionListener() {
        upButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                moveUp();
            }
        });

        downButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                moveDown();
            }
        });

        addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                addElement();
            }
        });

        removeButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                removeElement();
            }
        });

        listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                updateEnablement();
            }
        });
    }

    private void moveUp() {
        final int selectedIndex = getSelectedIndex();
        final String element = tagOrder.remove(selectedIndex);
        tagOrder.add(selectedIndex - 1, element);
        listViewer.setInput(tagOrder);
        setSelectedIndex(selectedIndex - 1);
    }

    private void moveDown() {
        final int selectedIndex = getSelectedIndex();
        final String element = tagOrder.remove(selectedIndex);
        tagOrder.add(selectedIndex + 1, element);
        listViewer.setInput(tagOrder);
        setSelectedIndex(selectedIndex + 1);
    }

    private void addElement() {
        final InputDialog inputDialog = new InputDialog(getShell(), "Add Tag", "Enter Tag name:", "", new IInputValidator() {
            @Override
            public String isValid(final String newText) {
                final String newTag = prepareTag(newText);
                return newTag.length() == 1 ? "" : tagOrder.contains(newTag) ? "Tag already exists." : null;
            }
        });

        if (inputDialog.open() == Window.OK) {
            final int selectedIndex = getSelectedIndex();
            tagOrder.add(selectedIndex + 1, prepareTag(inputDialog.getValue()));
            listViewer.setInput(tagOrder);
            setSelectedIndex(selectedIndex + 1);
        }
    }

    private void removeElement() {
        final int selectedIndex = getSelectedIndex();
        tagOrder.remove(selectedIndex);
        listViewer.setInput(tagOrder);
        setSelectedIndex(selectedIndex < tagOrder.size() ? selectedIndex : selectedIndex - 1);
    }

    private String prepareTag(final String newText) {
        return newText.trim().startsWith("@") ? newText.trim() : "@" + newText.trim();
    }

    private void updateEnablement() {
        final int selectedIndex = getSelectedIndex();

        upButton.setEnabled(selectedIndex > 0);
        downButton.setEnabled(selectedIndex >= 0 && selectedIndex < tagOrder.size() - 1);
        removeButton.setEnabled(selectedIndex >= 0);
    }

    private String getSelection() {
        return listViewer.getSelection() == null ?
                null : (String) ((IStructuredSelection) listViewer.getSelection()).getFirstElement();
    }

    private int getSelectedIndex() {
        final String selection = getSelection();
        return (selection == null) ? -1 : tagOrder.indexOf(selection);
    }

    private void setSelection(final String element) {
        listViewer.setSelection((element == null) ? null : new StructuredSelection(element));
    }

    private void setSelectedIndex(final int selectedIndex) {
        setSelection(0 <= selectedIndex && selectedIndex < tagOrder.size() ? tagOrder.get(selectedIndex) : null);
    }
}
