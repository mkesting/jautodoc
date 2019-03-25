/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.replacements;

import net.sf.jautodoc.preferences.Constants;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * Dialog to edit replacements.
 */
public class ReplacementDialog extends Dialog {
	
	private String title;
	
	private Label shortcutLabel;
	private Label replacementLabel;
	private Label scopeLabel;
	private Label modeLabel;
	
	private Text shortcutText;
	private Text replacementText;
	private Combo scopeCombo;
	private Combo modeCombo;

	private Replacement replacement;
	

	/**
	 * Instantiates a new replacement dialog.
	 * 
	 * @param shell the shell
	 * @param title the title
	 * @param initialValue the initial value
	 */
	public ReplacementDialog(Shell shell, String title, Replacement initialValue) {
		super(shell);
		this.title = title;
		replacement = initialValue;
	}

	/**
	 * Gets the replacement.
	 * 
	 * @return the replacement
	 */
	public Replacement getReplacement() {
		return replacement;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 10;
		comp.setLayout(layout);
		
		shortcutLabel = new Label(comp, SWT.NONE);
		shortcutLabel.setText(Constants.LABEL_SHORTCUT);
		shortcutLabel.setFont(comp.getFont());
		
		shortcutText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		if (replacement != null) {
			shortcutText.setText(replacement.getShortcut());
		}
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 250;
		gd.horizontalSpan = 3;
		shortcutText.setLayoutData(gd);
		shortcutText.setFont(comp.getFont());
		shortcutText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtonStates();
			}
		});
		
		replacementLabel = new Label(comp, SWT.NONE);
		replacementLabel.setText(Constants.LABEL_REPLACEMENT);
		replacementLabel.setFont(comp.getFont());
		
		replacementText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		if (replacement != null) {
			replacementText.setText(replacement.getReplacement());
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 250;
		gd.horizontalSpan = 3;
		replacementText.setLayoutData(gd);
		replacementText.setFont(comp.getFont());
		replacementText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateButtonStates();
			}
		});		
		
		scopeLabel = new Label(comp, SWT.NONE);
		scopeLabel.setText(Constants.LABEL_SCOPE);
		scopeLabel.setFont(comp.getFont());
		
		scopeCombo = new Combo(comp, SWT.READ_ONLY);
		scopeCombo.setItems(new String[] { Constants.COLUMN_SCOPE_METHOD,
				Constants.COLUMN_SCOPE_FIELD, Constants.COLUMN_SCOPE_BOTH });
		if (replacement != null) {
			scopeCombo.select(replacement.getScope() - 1);
		}
		else {
			scopeCombo.select(Replacement.SCOPE_METHOD - 1);
		}
		
		modeLabel = new Label(comp, SWT.NONE);
		modeLabel.setText(Constants.LABEL_REPLACE_MODE);
		modeLabel.setFont(comp.getFont());
		
		modeCombo = new Combo(comp, SWT.READ_ONLY);
		modeCombo.setItems(new String[] {Constants.COLUMN_MODE_PREFIX, Constants.COLUMN_MODE_ALL});
		if (replacement != null) {
			modeCombo.select(replacement.getMode());
		}
		else {
			modeCombo.select(Replacement.MODE_PREFIX);
		}
		
		return comp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			replacement = new Replacement(shortcutText.getText().trim(),
													  replacementText.getText().trim(),
													  scopeCombo.getSelectionIndex() + 1,
													  modeCombo.getSelectionIndex());
		} else {
			replacement = null;
		}
		super.buttonPressed(buttonId);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#create()
	 */
	public void create() {
		super.create();
		updateButtonStates();
	}
	
	private void updateButtonStates() {
		getButton(IDialogConstants.OK_ID).setEnabled(shortcutText.getText().trim().length() > 0);
	}
}
