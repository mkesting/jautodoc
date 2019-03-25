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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;


/**
 * Panel for the EditTemplateDialog.
 */
public class EditTemplatePanel extends Composite {

	Group groupGeneral = null;
	Group groupTarget = null;
	Composite compositeTemplate = null;
	Composite compositeButtons = null;
	Label labelName = null;
	Text textName = null;
	Label labelRegex = null;
	Text textRegex = null;
	Button radioElementName = null;
	Button radioSignature = null;
	TabFolder tabFolder = null;
	TemplateViewer templateViewer;
	SashForm sashForm = null;
	StyledText textAreaPreviewGroups = null;
	TemplateViewer previewViewer = null;
	Group groupParent = null;
	Label labelParentRegex = null;
	Text textParentRegex = null;
	Label labelParentExample = null;
	Text textParentExample = null;
	Label labelParentName = null;
	Text textParentName = null;
	Label labelExample = null;
	Text textExample = null;
	Button buttonPreview = null;

	
	/**
	 * Instantiates a new edit template panel.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 */
	public EditTemplatePanel(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		GridData gridData18 = new GridData();
		gridData18.horizontalAlignment = GridData.FILL;
		gridData18.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		setLayout(gridLayout);
		setLayoutData(new GridData(GridData.FILL_BOTH));
		createGroupParent();
		createGroupGeneral();
		createGroupTarget();
		buttonPreview = new Button(this, SWT.NONE);
		buttonPreview.setText("Preview");
		buttonPreview.setLayoutData(gridData18);
		createCompositeTemplate();
	}
	
	/**
	 * This method initializes groupParent	
	 *
	 */
	private void createGroupParent() {
		GridData gridData16 = new GridData();
		gridData16.grabExcessHorizontalSpace = true;
		gridData16.verticalAlignment = GridData.CENTER;
		gridData16.horizontalAlignment = GridData.FILL;
		GridData gridData15 = new GridData();
		gridData15.grabExcessHorizontalSpace = true;
		gridData15.verticalAlignment = GridData.CENTER;
		gridData15.horizontalAlignment = GridData.FILL;
		GridData gridData14 = new GridData();
		gridData14.grabExcessHorizontalSpace = true;
		gridData14.verticalAlignment = GridData.CENTER;
		gridData14.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.numColumns = 2;
		GridData gridData13 = new GridData();
		gridData13.grabExcessHorizontalSpace = true;
		gridData13.verticalAlignment = GridData.CENTER;
		gridData13.verticalSpan = 2;
		gridData13.horizontalAlignment = GridData.FILL;
		groupParent = new Group(this, SWT.NONE);
		groupParent.setLayoutData(gridData13);
		groupParent.setLayout(gridLayout4);
		groupParent.setText("Parent Template");
		labelParentName = new Label(groupParent, SWT.NONE);
		labelParentName.setText("Name:");
		textParentName = new Text(groupParent, SWT.BORDER);
		textParentName.setEnabled(false);
		textParentName.setLayoutData(gridData16);
		labelParentRegex = new Label(groupParent, SWT.NONE);
		labelParentRegex.setText("Pattern:");
		textParentRegex = new Text(groupParent, SWT.BORDER);
		textParentRegex.setEnabled(false);
		textParentRegex.setLayoutData(gridData14);
		labelParentExample = new Label(groupParent, SWT.NONE);
		labelParentExample.setText("Example:");
		textParentExample = new Text(groupParent, SWT.BORDER);
		textParentExample.setEnabled(false);
		textParentExample.setLayoutData(gridData15);
	}

	/**
	 * This method initializes groupGeneral	
	 *
	 */
	private void createGroupGeneral() {
		GridData gridData17 = new GridData();
		gridData17.grabExcessHorizontalSpace = true;
		gridData17.verticalAlignment = GridData.CENTER;
		gridData17.horizontalAlignment = GridData.FILL;
		GridData gridData5 = new GridData();
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.verticalAlignment = GridData.CENTER;
		gridData5.horizontalAlignment = GridData.FILL;
		GridData gridData4 = new GridData();
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.verticalAlignment = GridData.CENTER;
		gridData4.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.verticalSpan = 2;
		gridData.horizontalAlignment = GridData.FILL;
		groupGeneral = new Group(this, SWT.NONE);
		groupGeneral.setLayoutData(gridData);
		groupGeneral.setLayout(gridLayout1);
		groupGeneral.setText("Template");
		labelName = new Label(groupGeneral, SWT.NONE);
		labelName.setText("Name:");
		textName = new Text(groupGeneral, SWT.BORDER);
		textName.setLayoutData(gridData4);
		labelRegex = new Label(groupGeneral, SWT.NONE);
		labelRegex.setText("Pattern:");
		textRegex = new Text(groupGeneral, SWT.BORDER);
		textRegex.setLayoutData(gridData5);
		labelExample = new Label(groupGeneral, SWT.NONE);
		labelExample.setText("Example:");
		textExample = new Text(groupGeneral, SWT.BORDER);
		textExample.setLayoutData(gridData17);
	}

	/**
	 * This method initializes groupTarget	
	 *
	 */
	private void createGroupTarget() {
		GridData gridData12 = new GridData();
		gridData12.horizontalAlignment = GridData.CENTER;
		gridData12.verticalAlignment = GridData.CENTER;
		groupTarget = new Group(this, SWT.NONE);
		groupTarget.setLayout(new GridLayout());
		groupTarget.setLayoutData(gridData12);
		groupTarget.setText("Target");
		radioElementName = new Button(groupTarget, SWT.RADIO);
		radioElementName.setText("Element name");
		radioElementName.setSelection(true);
		radioSignature = new Button(groupTarget, SWT.RADIO);
		radioSignature.setText("Signature");
	}

	/**
	 * This method initializes compositeTemplate	
	 *
	 */
	private void createCompositeTemplate() {
		GridData gridData3 = new GridData();
		gridData3.horizontalSpan = 3;
		gridData3.verticalAlignment = GridData.FILL;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.horizontalAlignment = GridData.FILL;
		compositeTemplate = new Composite(this, SWT.NONE);
		compositeTemplate.setLayout(new GridLayout());
		createTabFolder();
		compositeTemplate.setLayoutData(gridData3);
	}

	/**
	 * This method initializes tabFolder	
	 *
	 */
	private void createTabFolder() {
		GridData gridData11 = new GridData();
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.horizontalAlignment = GridData.FILL;
		gridData11.verticalAlignment = GridData.FILL;
		gridData11.grabExcessVerticalSpace = true;
		gridData11.widthHint= 675;
		gridData11.heightHint= 300;
		tabFolder = new TabFolder(compositeTemplate, SWT.NONE);
		tabFolder.setLayoutData(gridData11);
		TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
		tabItem.setText("Template");
		TabItem tabItem1 = new TabItem(tabFolder, SWT.NONE);
		tabItem1.setText("Preview");
		templateViewer = new TemplateViewer(tabFolder, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		tabItem.setControl(templateViewer.getControl());
		createSashForm();
		tabItem1.setControl(sashForm);
	}

	/**
	 * This method initializes sashForm	
	 *
	 */
	private void createSashForm() {
		sashForm = new SashForm(tabFolder, SWT.NONE);
		previewViewer = new TemplateViewer(sashForm, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		previewViewer.setEditable(false);
		
		textAreaPreviewGroups = new StyledText(sashForm, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		textAreaPreviewGroups.setForeground(previewViewer.getControl().getForeground());
		textAreaPreviewGroups.setFont(previewViewer.getControl().getFont());
		textAreaPreviewGroups.setEditable(false);
		sashForm.setWeights(new int[]{3, 1});
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
