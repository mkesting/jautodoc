/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.templates;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;


/**
 * Panel for the EditPropertiesDialog.
 */
public class EditPropertiesPanel extends Composite {

	private Composite compositeTable = null;
	private Group groupEdit = null;
	private Composite compositeTableButtons = null;
	private Composite compositeEditFields = null;
	private Composite compositeEditButtons = null;
	
	private Label labelName = null;
	private Label labelValue = null;
	
	TableViewer tableViewer = null;
	
	Text textName = null;
	Text textValue = null;

	Button buttonAdd = null;
	Button buttonEdit = null;
	Button buttonRemove = null;
	Button buttonApply = null;
	Button buttonCancel = null;

	
	/**
	 * Instantiates a new edit properties panel.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 */
	public EditPropertiesPanel(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		createCompositeTable();
		createGroupEdit();
		setLayout(new GridLayout());
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 240;
		gridData.widthHint  = 370;
		setLayoutData(gridData);
	}

	/**
	 * This method initializes compositeTable	
	 *
	 */
	private void createCompositeTable() {
		GridData gridData11 = new GridData();
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.horizontalAlignment = GridData.FILL;
		gridData11.verticalAlignment = GridData.FILL;
		gridData11.grabExcessVerticalSpace = true;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		compositeTable = new Composite(this, SWT.NONE);
		compositeTable.setLayout(gridLayout);
		compositeTable.setLayoutData(gridData);
		Table table = new Table(compositeTable, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		table.setHeaderVisible(true);
		table.setLayoutData(gridData11);
		table.setLinesVisible(true);
		createCompositeTableButtons();
		tableViewer = new TableViewer(table);
	}

	/**
	 * This method initializes groupEdit	
	 *
	 */
	private void createGroupEdit() {
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.CENTER;
		gridData1.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		groupEdit = new Group(this, SWT.NONE);
		groupEdit.setText("Edit Property");
		groupEdit.setLayout(gridLayout1);
		createCompositeEditFields();
		groupEdit.setLayoutData(gridData1);
		createCompositeEditButtons();
	}

	/**
	 * This method initializes compositeTableButtons	
	 *
	 */
	private void createCompositeTableButtons() {
		GridData gridData12 = new GridData();
		gridData12.widthHint = 60;
		gridData12.verticalAlignment = GridData.END;
		gridData12.horizontalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.CENTER;
		gridData4.grabExcessVerticalSpace = true;
		gridData4.verticalAlignment = GridData.END;
		gridData4.widthHint = 60;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridData3.widthHint = 60;
		GridData gridData2 = new GridData();
		gridData2.grabExcessVerticalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.horizontalAlignment = GridData.BEGINNING;
		compositeTableButtons = new Composite(compositeTable, SWT.NONE);
		compositeTableButtons.setLayout(new GridLayout());
		compositeTableButtons.setLayoutData(gridData2);
		buttonAdd = new Button(compositeTableButtons, SWT.NONE);
		buttonAdd.setText("Add");
		buttonAdd.setLayoutData(gridData4);
		buttonEdit = new Button(compositeTableButtons, SWT.NONE);
		buttonEdit.setText("Edit");
		buttonEdit.setLayoutData(gridData3);
		buttonRemove = new Button(compositeTableButtons, SWT.NONE);
		buttonRemove.setText("Remove");
		buttonRemove.setLayoutData(gridData12);
	}

	/**
	 * This method initializes compositeEditFields	
	 *
	 */
	private void createCompositeEditFields() {
		GridData gridData7 = new GridData();
		gridData7.grabExcessHorizontalSpace = true;
		gridData7.verticalAlignment = GridData.CENTER;
		gridData7.horizontalAlignment = GridData.FILL;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		GridData gridData5 = new GridData();
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.verticalAlignment = GridData.CENTER;
		gridData5.horizontalAlignment = GridData.FILL;
		compositeEditFields = new Composite(groupEdit, SWT.NONE);
		compositeEditFields.setLayoutData(gridData5);
		compositeEditFields.setLayout(gridLayout2);
		labelName = new Label(compositeEditFields, SWT.NONE);
		labelName.setText("Name:");
		textName = new Text(compositeEditFields, SWT.BORDER);
		textName.setLayoutData(gridData6);
		labelValue = new Label(compositeEditFields, SWT.NONE);
		labelValue.setText("Value:");
		textValue = new Text(compositeEditFields, SWT.BORDER);
		textValue.setLayoutData(gridData7);
	}

	/**
	 * This method initializes compositeEditButtons	
	 *
	 */
	private void createCompositeEditButtons() {
		GridData gridData10 = new GridData();
		gridData10.widthHint = 60;
		GridData gridData9 = new GridData();
		gridData9.widthHint = 60;
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.BEGINNING;
		gridData8.verticalAlignment = GridData.FILL;
		compositeEditButtons = new Composite(groupEdit, SWT.NONE);
		compositeEditButtons.setLayout(new GridLayout());
		compositeEditButtons.setLayoutData(gridData8);
		buttonApply = new Button(compositeEditButtons, SWT.NONE);
		buttonApply.setText("Apply");
		buttonApply.setLayoutData(gridData9);
		buttonCancel = new Button(compositeEditButtons, SWT.NONE);
		buttonCancel.setText("Cancel");
		buttonCancel.setLayoutData(gridData10);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
