/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.templates;

import net.sf.jautodoc.templates.viewer.TemplateViewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

/**
 * Panel for the TemplateTreePage.
 */
public class TemplateTreePanel extends Composite {

	Composite compositeButtons = null;
	Composite compositeTemplates = null;
	Button buttonUp = null;
	Button buttonDown = null;
	Button buttonImport = null;
	Button buttonExport = null;
	Button buttonProperties = null;
	Button buttonAdd = null;
	Button buttonEdit = null;
	Button buttonRemove = null;
	Label labelPattern = null;
	Label labelExample = null;
	Text textPattern = null;
	Text textExample = null;
	Composite compositeTemplateText = null;
	TemplateViewer templateViewer = null;
	Tree templateTree = null;
	Label labelTemplateText = null;

	
	/**
	 * Instantiates a new template tree panel.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 */
	public TemplateTreePanel(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.setLayout(gridLayout);
		createCompositeTemplates();
		createCompositeButtons();
		createCompositeTemplateText();
		setSize(new Point(512, 494));
	}

	/**
	 * This method initializes compositeTemplates.	
	 *
	 */
	private void createCompositeTemplates() {
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.grabExcessVerticalSpace = true;
		compositeTemplates = new Composite(this, SWT.NONE);
		compositeTemplates.setLayoutData(gridData1);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		compositeTemplates.setLayout(gridLayout);
		GridData gridData3 = new GridData();
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.verticalAlignment = GridData.FILL;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.horizontalSpan = 2;
		templateTree = new Tree(compositeTemplates, SWT.BORDER);
		templateTree.setLayoutData(gridData3);
		labelPattern = new Label(compositeTemplates, SWT.NONE);
		labelPattern.setText("Pattern:");
		labelPattern.setLayoutData(new GridData());
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalAlignment = GridData.FILL;
		textPattern = new Text(compositeTemplates, SWT.BORDER | SWT.READ_ONLY);
		textPattern.setLayoutData(gridData2);
		labelExample = new Label(compositeTemplates, SWT.NONE);
		labelExample.setText("Example:");
		labelExample.setLayoutData(new GridData());
		GridData gridData2a = new GridData();
		gridData2a.grabExcessHorizontalSpace = true;
		gridData2a.horizontalAlignment = GridData.FILL;
		textExample = new Text(compositeTemplates, SWT.BORDER | SWT.READ_ONLY);
		textExample.setLayoutData(gridData2a);
	}

	/**
	 * This method initializes compositeButtons	
	 *
	 */
	private void createCompositeButtons() {
		GridData gridData12 = new GridData(-1, 5);
		GridData gridData11 = new GridData(-1, 5);
		GridData gridData10 = new GridData();
		gridData10.horizontalAlignment = GridData.FILL;
		gridData10.verticalAlignment = GridData.CENTER;
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = GridData.FILL;
		gridData9.verticalAlignment = GridData.CENTER;
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.FILL;
		gridData8.verticalAlignment = GridData.CENTER;
		GridData gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.FILL;
		gridData7.verticalAlignment = GridData.CENTER;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.verticalAlignment = GridData.CENTER;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.verticalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.verticalAlignment = GridData.CENTER;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.verticalAlignment = GridData.CENTER;
		GridData gridData2 = new GridData();
		gridData2.grabExcessVerticalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.horizontalAlignment = GridData.BEGINNING;
		compositeButtons = new Composite(this, SWT.NONE);
		compositeButtons.setLayout(new GridLayout());
		compositeButtons.setLayoutData(gridData2);
		buttonImport = new Button(compositeButtons, SWT.NONE);
		buttonImport.setText("Import...");
		buttonImport.setLayoutData(gridData8);
		buttonExport = new Button(compositeButtons, SWT.NONE);
		buttonExport.setText("Export...");
		buttonExport.setLayoutData(gridData9);
		buttonProperties = new Button(compositeButtons, SWT.NONE);
		buttonProperties.setText("Properties...");
		buttonProperties.setLayoutData(gridData10);		
		Label filler1 = new Label(compositeButtons, SWT.NONE);
		filler1.setLayoutData(gridData11);
		buttonUp = new Button(compositeButtons, SWT.NONE);
		buttonUp.setText("Up");
		buttonUp.setLayoutData(gridData6);
		buttonDown = new Button(compositeButtons, SWT.NONE);
		buttonDown.setText("Down");
		buttonDown.setLayoutData(gridData7);
		Label filler2 = new Label(compositeButtons, SWT.NONE);
		filler2.setLayoutData(gridData12);
		buttonAdd = new Button(compositeButtons, SWT.NONE);
		buttonAdd.setText("Add...");
		buttonAdd.setLayoutData(gridData4);
		buttonEdit = new Button(compositeButtons, SWT.NONE);
		buttonEdit.setText("Edit...");
		buttonEdit.setLayoutData(gridData5);
		buttonRemove = new Button(compositeButtons, SWT.NONE);
		buttonRemove.setText("Remove");
		buttonRemove.setLayoutData(gridData3);
	}

	/**
	 * This method initializes compositeTemplateText	
	 *
	 */
	private void createCompositeTemplateText() {
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.FILL;
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.grabExcessVerticalSpace = true;
		gridData11.heightHint = 150;
		gridData11.verticalAlignment = GridData.FILL;
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		compositeTemplateText = new Composite(this, SWT.NONE);
		compositeTemplateText.setLayout(new GridLayout());
		compositeTemplateText.setLayoutData(gridData);
		labelTemplateText = new Label(compositeTemplateText, SWT.NONE);
		labelTemplateText.setText("Template text:");
		templateViewer = new TemplateViewer(compositeTemplateText, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		templateViewer.setEditable(false);
		templateViewer.getControl().setLayoutData(gridData11);
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
