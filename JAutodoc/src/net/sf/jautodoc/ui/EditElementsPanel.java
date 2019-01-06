/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;


/**
 * Content panel for EditElementsDialog.
 */
public class EditElementsPanel extends Composite {

    static final String LABEL_DATA_KEY = "EditElementsPanel-attributeLabel";

    Button buttonAdd = null;
    Button buttonEdit = null;
    Button buttonRemove = null;
    Button buttonApply = null;
    Button buttonCancel = null;

    TableViewer tableViewer = null;

    Map<String, Text> textFields = new LinkedHashMap<String, Text>();


    public EditElementsPanel(final Composite parent, final String[] attributeLabels, final int[] columnWeights,
            final int width, final int height, final String editHint) {
        super(parent, SWT.None);
        initialize(attributeLabels, columnWeights, width, height, editHint);
    }

    private final void initialize(final String[] attributeLabels, final int[] columnWeights,
            final int width, final int height, final String editHint) {
        setLayout(new GridLayout());

        final GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = height;
        gridData.widthHint  = width;
        setLayoutData(gridData);

        createCompositeTable(attributeLabels, columnWeights);
        createGroupEdit(attributeLabels, editHint);
    }

    private void createCompositeTable(final String[] attributeLabels, final int[] columnWeights) {
        final Composite compositeTable = new Composite(this, SWT.NONE);
        compositeTable.setLayout(new GridLayout(2, false));
        compositeTable.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

        final Table table = new Table(compositeTable, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
        table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);

        final TableLayout tableLayout = new TableLayout();
        table.setLayout(tableLayout);

        for (int i = 0; i < attributeLabels.length; i++) {
            final TableColumn column = new TableColumn(table, SWT.NONE, i);
            column.setResizable(true);
            column.setText(attributeLabels[i]);
            tableLayout.addColumnData(new ColumnWeightData(columnWeights[i]));
        }
        tableViewer = new TableViewer(table);

        createCompositeTableButtons(compositeTable);
    }

    private void createGroupEdit(final String[] attributeLabels, final String editHint) {
        final Group groupEdit = new Group(this, SWT.NONE);
        groupEdit.setText(editHint);
        groupEdit.setLayout(new GridLayout(2, false));
        groupEdit.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        createCompositeEditFields(groupEdit, attributeLabels);
        createCompositeEditButtons(groupEdit);
    }

    private void createCompositeTableButtons(final Composite parent) {
        final Composite compositeTableButtons = new Composite(parent, SWT.NONE);
        compositeTableButtons.setLayout(new GridLayout());
        compositeTableButtons.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, true));

        buttonAdd = new Button(compositeTableButtons, SWT.NONE);
        buttonAdd.setText("Add");
        buttonAdd.setLayoutData(new GridData(60, SWT.DEFAULT));

        buttonEdit = new Button(compositeTableButtons, SWT.NONE);
        buttonEdit.setText("Edit");
        buttonEdit.setLayoutData(new GridData(60, SWT.DEFAULT));

        buttonRemove = new Button(compositeTableButtons, SWT.NONE);
        buttonRemove.setText("Remove");
        buttonRemove.setLayoutData(new GridData(60, SWT.DEFAULT));
    }

    private void createCompositeEditFields(final Composite parent, final String[] attributeLabels) {
        final Composite compositeEditFields = new Composite(parent, SWT.NONE);
        compositeEditFields.setLayout(new GridLayout(2, false));
        compositeEditFields.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));

        for (final String attributeLabel : attributeLabels) {
            final Label label = new Label(compositeEditFields, SWT.NONE);
            label.setText(attributeLabel + ":");

            final Text text = new Text(compositeEditFields, SWT.BORDER);
            text.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
            text.setData(LABEL_DATA_KEY, attributeLabel);
            textFields.put(attributeLabel, text);
        }
    }

    private void createCompositeEditButtons(final Composite parent) {
        final Composite compositeEditButtons = new Composite(parent, SWT.NONE);
        compositeEditButtons.setLayout(new GridLayout());
        compositeEditButtons.setLayoutData(new GridData(GridData.BEGINNING, GridData.FILL, false, false));

        buttonApply = new Button(compositeEditButtons, SWT.NONE);
        buttonApply.setText("Apply");
        buttonApply.setLayoutData(new GridData(60, SWT.DEFAULT));

        buttonCancel = new Button(compositeEditButtons, SWT.NONE);
        buttonCancel.setText("Cancel");
        buttonCancel.setLayoutData(new GridData(60, SWT.DEFAULT));
    }
}
