/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.ITableEntry;
import net.sf.jautodoc.preferences.TableContentProvider;
import net.sf.jautodoc.preferences.TableLabelProvider;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Generic dialog for element editing (ADD|EDIT|REMOVE).
 *
 * @param <E> the element type
 */
public abstract class EditElementsDialog<E extends ITableEntry> extends StatusDialog {

    public enum State {
        VIEW, ADD, EDIT
    }

    private final List<E> elements;

    private State state = State.VIEW;
    private EditElementsPanel view;
    private MyStatus status = new MyStatus();


    public EditElementsDialog(final Shell parent, final Collection<E> elements) {
        super(parent);
        this.elements = new ArrayList<E>(elements);

        setShellStyle(getShellStyle() | SWT.RESIZE);
        setHelpAvailable(false);
    }

    public Collection<E> getElements() {
        return Collections.unmodifiableList(elements);
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        view = new EditElementsPanel(parent, getAttributeLabels(), getColumnWeights(),
                getWidth(), getHeight(), getEditHint());
        configureTableViewer();
        initListener();
        updateStates();
        return view;
    }

    @Override
    protected void configureShell(final Shell shell) {
        super.configureShell(shell);
        shell.setText(getDialogTitle());
    }

    @Override
    protected void okPressed() {
        if (state != State.VIEW) {
            if (view.buttonApply.isEnabled()) {
                doApply();
            } else if (!MessageDialog.openQuestion(getShell(),
                "Discard changes", "Current editing is not applied. Discard changes?")) {
                return;
            }
        }
        saveElements();
        super.okPressed();
    }

    protected abstract String getEditHint();
    protected abstract String getDialogTitle();
    protected abstract int getWidth();
    protected abstract int getHeight();
    protected abstract int[] getColumnWeights();
    protected abstract String[] getAttributeLabels();
    protected abstract String verifyText(String attributeLabel, char character);
    protected abstract boolean isValid(Map<String, Text> textFields);
    protected abstract E readElement(final Map<String, Text> textFields);
    protected abstract void showElement(E element, Map<String, Text> textFields);


    private void configureTableViewer() {
        view.tableViewer.setLabelProvider(new TableLabelProvider());
        view.tableViewer.setContentProvider(new TableContentProvider());

        view.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(final SelectionChangedEvent event) {
                showSelected();
                updateStates();
            }
        });
        view.tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(final DoubleClickEvent event) {
                if (view.buttonEdit.isEnabled()) {
                    doEdit();
                }
            }
        });
        view.tableViewer.getControl().addKeyListener(new KeyAdapter() {
            public void keyPressed(final KeyEvent event) {
                if (view.buttonRemove.isEnabled() && event.keyCode == SWT.DEL  && event.stateMask == 0) {
                    doRemove();
                } else if (view.buttonAdd.isEnabled() && event.keyCode == SWT.INSERT && event.stateMask == 0) {
                    doAdd();
                }
            }
        });
        view.tableViewer.setInput(elements.toArray());
        updateStates();
    }

    private void initListener() {
        view.buttonAdd.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                doAdd();
            }
        });

        view.buttonEdit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                doEdit();
            }
        });

        view.buttonRemove.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                doRemove();
            }
        });

        view.buttonApply.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                doApply();
            }
        });

        view.buttonCancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                doCancel();
            }
        });

        for (final Text text : view.textFields.values()) {
            initTextListener(text);
        }
    }

    private void initTextListener(final Text text) {
        text.addVerifyListener(new VerifyListener() {
            public void verifyText(final VerifyEvent e) {
                final String result = EditElementsDialog.this.verifyText(
                        (String) e.widget.getData(EditElementsPanel.LABEL_DATA_KEY), e.character);
                if (result == null) {
                    setOK();
                } else {
                    setWarning(result);
                    e.doit = false;
                }
            }
        });
        text.addKeyListener(new KeyAdapter() {
            public void keyPressed(final KeyEvent e) {
                validateApply();
            }
        });
    }

    private void updateStates() {
        final IStructuredSelection selection = (IStructuredSelection)view.tableViewer.getSelection();
        if (state == State.VIEW) {
            view.tableViewer.getControl().setEnabled(true);
            view.buttonAdd.setEnabled(true);
            view.buttonEdit.setEnabled(selection.size() == 1);
            view.buttonRemove.setEnabled(selection.size() > 0);
            view.buttonApply.setEnabled(false);
            view.buttonCancel.setEnabled(false);
            for (final Text text : view.textFields.values()) {
                text.setEnabled(false);
            }
            view.tableViewer.getControl().forceFocus();
        } else {
            view.tableViewer.getControl().setEnabled(false);
            view.buttonAdd.setEnabled(false);
            view.buttonEdit.setEnabled(false);
            view.buttonRemove.setEnabled(false);
            view.buttonApply.setEnabled(true);
            view.buttonCancel.setEnabled(true);
            for (final Text text : view.textFields.values()) {
                text.setEnabled(true);
            }
            view.textFields.values().iterator().next().forceFocus();
            validateApply();
        }
    }

    private void setOK() {
        status.setSeverity(IStatus.OK);
        status.setMessage("");
        updateStatus(status);
    }

    private void setWarning(String message) {
        status.setSeverity(IStatus.WARNING);
        status.setMessage(message);
        updateStatus(status);
    }

    private void validateApply() {
        view.buttonApply.setEnabled(state != State.VIEW && isValid(view.textFields));
    }

    private void doAdd() {
        for (final Text text : view.textFields.values()) {
            text.setText("");
        }
        state = State.ADD;
        updateStates();
    }

    private void doEdit() {
        state = State.EDIT;
        updateStates();
    }

    private void doRemove() {
        ((TableContentProvider) view.tableViewer.getContentProvider()).remove(
                (IStructuredSelection) view.tableViewer.getSelection());
        updateStates();
    }

    private void doApply() {
        final E element = readElement(view.textFields);

        boolean ok = false;
        if (state == State.ADD) {
            if (checkOverwrite(element)) {
                ((TableContentProvider) view.tableViewer.getContentProvider()).add(element);
                ok = true;
            }
        } else if (state == State.EDIT) {
            final E selectedElement = getSelectedElement();

            if (element.equals(selectedElement) || checkOverwrite(element)) {
                ((TableContentProvider) view.tableViewer.getContentProvider()).remove(selectedElement);
                ((TableContentProvider) view.tableViewer.getContentProvider()).add(element);
                ok = true;
            }
        }

        if (ok) {
            state = State.VIEW;
            updateStates();
        } else {
            view.textFields.values().iterator().next().forceFocus();
        }
    }

    private void doCancel() {
        state = State.VIEW;
        showSelected();
        updateStates();
    }

    @SuppressWarnings("unchecked")
    private void saveElements() {
        final Object[] obj = ((TableContentProvider) view.tableViewer.getContentProvider()).getElements(null);

        elements.clear();
        for (final Object object : obj) {
            elements.add((E) object);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean checkOverwrite(final E newElement) {
        final TableContentProvider cp = (TableContentProvider) view.tableViewer.getContentProvider();
        final Object[] elements = cp.getElements(null);

        for (int i = 0; i < elements.length; ++i) {
            final E element = (E) elements[i];
            if (!element.equals(newElement)) {
                continue;
            }
            if (!MessageDialog.openQuestion(getShell(), "Overwrite exists", "Element already exists. Replace?")) {
                return false;
            }
            cp.remove(element); // else overwrite
            break;
        }
        return true;
    }

    private void showSelected() {
        final E element = getSelectedElement();
        if (element == null) {
            for (final Text text : view.textFields.values()) {
                text.setText("");
            }
        } else {
            showElement(element, view.textFields);
        }
    }

    @SuppressWarnings("unchecked")
    private E getSelectedElement() {
        final IStructuredSelection selection = (IStructuredSelection) view.tableViewer.getSelection();
        if (selection.size() == 0) {
            return null;
        }
        return (E) selection.getFirstElement();
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    private static class MyStatus extends Status {
        public MyStatus() {
            super(IStatus.OK, JAutodocPlugin.PLUGIN_ID, 0, "", null);
        }

        public void setMessage(final String message) {
            super.setMessage(message);
        }

        public void setSeverity(final int severity) {
            super.setSeverity(severity);
        }
    }
}
