/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.templates;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


/**
 * Dialog for editing properties.
 */
public class EditPropertiesDialog extends StatusDialog {
    private static final int STATE_VIEW = 0;
    private static final int STATE_ADD     = 1;
    private static final int STATE_EDIT = 2;

    private static final String[] tableColumnHeaders= {
        "Name",
        "Value"
    };

    private static final ColumnLayoutData[] tableColumnLayouts= {
            new ColumnWeightData(40),
            new ColumnWeightData(60)
    };

    private Map<String, String> properties;

    private EditPropertiesPanel view;
    private int state = STATE_VIEW;
    private MyStatus status = new MyStatus();


    /**
     * Instantiates a new edit properties dialog.
     *
     * @param parent the parent shell
     * @param properties the properties to use
     */
    public EditPropertiesDialog(Shell parent, Map<String, String> properties) {
        super(parent);

        this.properties = properties;

        setShellStyle(getShellStyle() | SWT.RESIZE);
        setHelpAvailable(false);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        view = new EditPropertiesPanel(parent, SWT.NONE);

        configureTableViewer();
        initData();
        initListener();
        updateStates();

        return view;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.StatusDialog#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText("Edit Properties");
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
        if (state != STATE_VIEW) {
            if (view.buttonApply.isEnabled()) {
                doApply();
            }
            else if (!MessageDialog.openQuestion(getShell(),
                "Discard changes", "Current editing is not applied. Discard changes?")) {
                return;
            }
        }

        saveProperties();
        super.okPressed();
    }

    private void configureTableViewer() {
        view.tableViewer.setLabelProvider(new TableLabelProvider());
        view.tableViewer.setContentProvider(new TableContentProvider());

        view.tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                showSelected();
                updateStates();
            }
        });
        view.tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                if (view.buttonEdit.isEnabled()) {
                    doEdit();
                }
            }
        });

        view.tableViewer.getControl().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (view.buttonRemove.isEnabled()
                        && event.keyCode == SWT.DEL
                        && event.stateMask == 0) {
                    doRemove();
                }
                else if (view.buttonAdd.isEnabled()
                        && event.keyCode == SWT.INSERT
                        && event.stateMask == 0) {
                    doAdd();
                }
            }
        });

        // set layout
        Table table = view.tableViewer.getTable();

        TableLayout tableLayout = new TableLayout();
        table.setLayout(tableLayout);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        for (int i = 0; i < tableColumnHeaders.length; i++) {
            tableLayout.addColumnData(tableColumnLayouts[i]);
            TableColumn column = new TableColumn(table, SWT.NONE, i);
            column.setResizable(tableColumnLayouts[i].resizable);
            column.setText(tableColumnHeaders[i]);
        }
    }

    private void initListener() {
        view.buttonAdd.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                doAdd();
            }
        });

        view.buttonEdit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                doEdit();
            }
        });

        view.buttonRemove.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                doRemove();
            }
        });

        view.buttonApply.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                doApply();
            }
        });

        view.buttonCancel.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                doCancel();
            }
        });

        view.textName.addVerifyListener(new VerifyListener() {
            public void verifyText(VerifyEvent e) {
                if (e.character == '.') {
                    setWarning("No dots allowed.");
                    e.doit = false;
                }
                else {
                    setOK();
                }
            }
        });

        view.textName.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                validateApply();
            }
        });

        view.textValue.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                validateApply();
                setOK();
            }
        });
    }

    private void initData() {
        List<Property> list = new ArrayList<Property>();

        Iterator<String> keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String name = (String)keys.next();
            list.add(new Property(name, properties.get(name)));
        }

        view.tableViewer.setInput(list.toArray(new Property[list.size()]));
        updateStates();
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
        view.buttonApply.setEnabled(
                state != STATE_VIEW &&
                view.textName.getText().trim().length() > 0    &&
                view.textValue.getText().trim().length() > 0);
    }

    private void doAdd() {
        view.textName.setText("");
        view.textValue.setText("");
        state = STATE_ADD;
        updateStates();
    }

    private void doEdit() {
        state = STATE_EDIT;
        updateStates();
    }

    private void doRemove() {
        ((TableContentProvider) view.tableViewer.getContentProvider())
                .remove((IStructuredSelection) view.tableViewer.getSelection());
        updateStates();
    }

    private void doApply() {
        String name  = view.textName.getText().trim();
        String value = view.textValue.getText().trim();

        boolean ok = false;
        if (state == STATE_ADD) {
            Property property = new Property(name, value);
            if (checkOverwrite(property)) {
                ((TableContentProvider) view.tableViewer.getContentProvider())
                        .add(property);
                ok = true;
            }
        }
        else if (state == STATE_EDIT) {
            Property property = getSelectedProperty();

            Property newProperty = new Property(name, value);
            if (property.equals(new Property(name, value)) ||
                    checkOverwrite(newProperty)) {
                property.setName(name);
                property.setValue(value);
                view.tableViewer.refresh();
                ok = true;
            }
        }

        if (ok) {
            state = STATE_VIEW;
            updateStates();
        }
        else {
            view.textName.forceFocus();
        }
    }

    private void doCancel() {
        state = STATE_VIEW;
        showSelected();
        updateStates();
    }

    private void showSelected() {
        Property property = getSelectedProperty();
        if (property != null) {
            view.textName.setText(property.getName());
            view.textValue.setText(property.getValue());
        }
        else {
            view.textName.setText("");
            view.textValue.setText("");
        }
    }

    private void saveProperties() {
        Object[] obj = ((TableContentProvider)
                view.tableViewer.getContentProvider()).getElements(null);

        properties.clear();
        for (int i = 0; i < obj.length; ++i) {
            Property property = (Property)obj[i];
            properties.put(property.getName(), property.getValue());
        }
    }

    private boolean checkOverwrite(Property newProperty) {
        TableContentProvider cp = (TableContentProvider) view.tableViewer
                .getContentProvider();

        Object[] properties = cp.getElements(null);
        for (int i = 0; i < properties.length; ++i) {
            Property property = (Property)properties[i];
            if (!property.equals(newProperty)) continue;

            if (!MessageDialog.openQuestion(getShell(),
                    "Overwrite exists", "Property already exists. Replace?")) {
                return false;
            }
            cp.remove(property); // else overwrite
            break;
        }

        return true;
    }

    private void updateStates() {
        IStructuredSelection selection = (IStructuredSelection)view.tableViewer.getSelection();
        if (state == STATE_VIEW) {
            view.tableViewer.getControl().setEnabled(true);
            view.buttonAdd.setEnabled(true);
            view.buttonEdit.setEnabled(selection.size() == 1);
            view.buttonRemove.setEnabled(selection.size() > 0);
            view.buttonApply.setEnabled(false);
            view.buttonCancel.setEnabled(false);
            view.textName.setEnabled(false);
            view.textValue.setEnabled(false);
            view.tableViewer.getControl().forceFocus();
        }
        else {
            view.tableViewer.getControl().setEnabled(false);
            view.buttonAdd.setEnabled(false);
            view.buttonEdit.setEnabled(false);
            view.buttonRemove.setEnabled(false);
            view.buttonApply.setEnabled(true);
            view.buttonCancel.setEnabled(true);
            view.textName.setEnabled(true);
            view.textValue.setEnabled(true);
            view.textName.forceFocus();
            validateApply();
        }
    }

    private Property getSelectedProperty() {
        IStructuredSelection selection = (IStructuredSelection)view.tableViewer.getSelection();
        if (selection.size() == 0) return null;

        return (Property)selection.getFirstElement();
    }

    private static class Property implements ITableEntry, Comparable<Property> {
        private String name;
        private String value;

        public Property(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        /* (non-Javadoc)
         * @see net.sf.jautodoc.preferences.ITableEntry#getColumnText(int)
         */
        public String getColumnText(int columnIndex) {
            if (columnIndex == 0) return name;
            if (columnIndex == 1) return value;
            return null;
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Property other) {
            return getName().compareTo(other.getName());
        }

        /* (non-Javadoc)
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object other) {
            if (!(other instanceof Property)) return false;
            return getName().equals(((Property)other).getName());
        }

        /* (non-Javadoc)
         * @see java.lang.Object#hashCode()
         */
        public int hashCode() {
            return getName().hashCode();
        }

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "Name: " + getName() + ", Value: " + getValue();
        }
    }

    private static class MyStatus extends Status {
        public MyStatus() {
            super(IStatus.OK, JAutodocPlugin.PLUGIN_ID, 0, "", null);
        }

        public void setMessage(String message) {
            super.setMessage(message);
        }

        public void setSeverity(int severity) {
            super.setSeverity(severity);
        }

    }
}
