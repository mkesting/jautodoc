/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.replacements;

import java.io.File;
import java.text.MessageFormat;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.preferences.TableContentProvider;
import net.sf.jautodoc.preferences.TableLabelProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


/**
 * Replacement composite.
 */
public class ReplacementBlock {
	
	private final String[] tableColumnHeaders= {
			Constants.COLUMN_SHORTCUT,
			Constants.COLUMN_REPLACEMENT,
			Constants.COLUMN_SCOPE,
			Constants.COLUMN_MODE
	};
	
	private final ColumnLayoutData[] tableColumnLayouts= {
	        new ColumnWeightData(24),
	        new ColumnWeightData(42),
	        new ColumnWeightData(17),
	        new ColumnWeightData(17)
	};
	
	private TableViewer  tableViewer;
	
	private Button editButton;
	private Button removeButton;
	private Button addButton;
	private Button importButton;
	private Button exportButton;
	
	
	/**
	 * Get configured replacements.
	 * 
	 * @return the replacements
	 */
	public Replacement[] getReplacements() {
		Object[] obj = ((TableContentProvider)
						tableViewer.getContentProvider()).getElements(null);
		Replacement[] prs = new Replacement[obj.length];
		for (int i = 0; i < obj.length; ++i) {
			prs[i] = (Replacement)obj[i];
		}
		return prs;
	}
	
	/**
	 * Set current replacements.
	 * 
	 * @param replacements the replacements
	 */
	public void setReplacements(Replacement[] replacements) {
		tableViewer.setInput(replacements);
		updateButtonStates();
	}
	
	/**
	 * Create contents for replacement composite.
	 * 
	 * @param parent the parent composite
	 * 
	 * @return the created control
	 */
	public Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		
		GridLayout layout = new GridLayout();
		layout.numColumns	= 2;
		layout.marginWidth  = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH)); 
		
		Label label = new Label(composite, SWT.NONE);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
		label.setFont(parent.getFont());
		label.setText(Constants.LABEL_TABLE);

		createTableViewer(composite);
		createButtonGroup(composite);

		return composite;
	}
	
	/**
	 * Show dialog to edit a new replacement.
	 */
	private void addReplacement() {
		ReplacementDialog dialog = new ReplacementDialog(
				tableViewer.getControl().getShell(), Constants.TITLE_ADD_REPLACEMENT, null);
		if (dialog.open() == Window.CANCEL) {
			return;
		}
		
		Replacement r = dialog.getReplacement();
		if (!checkOverwriteReplacement(r)) {
			return;
		}
		
		((TableContentProvider)tableViewer.getContentProvider()).add(r);
	}
	
	/**
	 * Show dialog to edit an existing replacement.
	 */
	private void editReplacement() {
		IStructuredSelection selection= (IStructuredSelection) tableViewer.getSelection();
		Replacement oldPr = (Replacement) selection.getFirstElement();
		Replacement newPr =
			new Replacement(oldPr.getShortcut(), oldPr.getReplacement(),
					oldPr.getScope(), oldPr.getMode());
		
		ReplacementDialog dialog = new ReplacementDialog(
				tableViewer.getControl().getShell(),
				Constants.TITLE_EDIT_REPLACEMENT, newPr);
		if (dialog.open() == Window.CANCEL) {
			return;
		}

		newPr = dialog.getReplacement();
		if (!oldPr.equals(newPr) &&
			!checkOverwriteReplacement(newPr)){
			return;
		}
		
		oldPr.setShortcut(newPr.getShortcut());
		oldPr.setReplacement(newPr.getReplacement());
		oldPr.setScope(newPr.getScope());
		oldPr.setMode(newPr.getMode());
		tableViewer.refresh();
	}

	/**
	 * Remove the selected replacement.
	 */
	private void removeReplacement() {
		TableContentProvider contentProvider =
			(TableContentProvider)tableViewer.getContentProvider();
		IStructuredSelection sel = (IStructuredSelection) tableViewer.getSelection();
		contentProvider.remove(sel);
	}
	
	/**
	 * Check, if replacement should be overridden.
	 * 
	 * @param newReplacement the new replacement
	 * 
	 * @return true, if user confirmed overriding
	 */
	private boolean checkOverwriteReplacement(Replacement newReplacement) {
		Replacement[] replacements = getReplacements();
		for (int i = 0; i < replacements.length; i++) {
			Replacement r = replacements[i];
			if (r.equals(newReplacement)) {
				boolean overWrite= MessageDialog.openQuestion(
						tableViewer.getControl().getShell(),
						Constants.TITLE_OVERWRITE_REPLACEMENT,
						MessageFormat.format(Constants.QUESTION_OVERWRITE_REPLACEMENT,
								r.getShortcut(), r.getScopeLabel()));
				if (!overWrite) {
					return false;
				}
				((TableContentProvider)tableViewer.getContentProvider()).remove(r);
				break;
			}					
		}
		return true;
	}
	
	/**
	 * Creates the table viewer.
	 * 
	 * @param parent the parent composite
	 */
	private void createTableViewer(Composite parent) {
		Table table = new Table(parent, SWT.MULTI | SWT.FULL_SELECTION | SWT.BORDER);
		GridData data= new GridData(GridData.FILL_BOTH);
		data.widthHint = IDialogConstants.ENTRY_FIELD_WIDTH;
		table.setLayoutData(data);
		table.setFont(parent.getFont());
		
		tableViewer= new TableViewer(table);
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new TableLabelProvider());
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateButtonStates();
			}
		});
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (!event.getSelection().isEmpty() && editButton.isEnabled()) {
					editReplacement();
				}
			}
		});
		tableViewer.getTable().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.getSource() == tableViewer) {
					if (removeButton.isEnabled() &&
						event.character == SWT.DEL &&
						event.stateMask == 0) {
						removeReplacement();
					}
				}
			}	
		});	
        
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
	
	/**
	 * Creates the button group.
	 * 
	 * @param parent the parent composite
	 */
	private void createButtonGroup(Composite parent) {
		Composite buttonGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonGroup.setLayout(layout);
		buttonGroup.setLayoutData(new GridData(
				GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL));
		buttonGroup.setFont(parent.getFont());

		importButton= createPushButton(buttonGroup, "Import...");
		exportButton= createPushButton(buttonGroup, "Export...");

		/*Label filler =*/new Label(buttonGroup, SWT.NONE);
		
		addButton= createPushButton(buttonGroup, Constants.BUTTON_ADD);
		editButton= createPushButton(buttonGroup, Constants.BUTTON_EDIT);
		removeButton= createPushButton(buttonGroup, Constants.BUTTON_REMOVE);
	}
	
	/**
	 * Creates a push button.
	 * 
	 * @param parent the parent composite
	 * @param buttonText the button text
	 * 
	 * @return the created button
	 */
	private Button createPushButton(Composite parent, String buttonText) {
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		button.setText(buttonText);
		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (event.widget == addButton) {
					addReplacement();
				} else if (event.widget == editButton) {
					editReplacement();
				} else if (event.widget == removeButton) {
					removeReplacement();
				} else if (event.widget == importButton) {
					importReplacements();
				} else if (event.widget == exportButton) {
					exportReplacements();
				}
				
			}
		});
		
		GridData gridData = new GridData(
				GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL);
		button.setLayoutData(gridData);
		
		return button;
	}
	
	/**
	 * Update button states.
	 */
	private void updateButtonStates() {
		IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
		editButton.setEnabled(selection.size() == 1);
		removeButton.setEnabled(selection.size() > 0);
	}
	
	private void importReplacements() {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
		fileDialog.setText("Import");
		fileDialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
		String selectedFile = fileDialog.open();
		if (selectedFile == null) {
			return;
		}
		
		try {
			Replacement[] rs = ReplacementSerializer.deserialize(new File(selectedFile));
			setReplacements(rs);
		} catch (Exception e) {
			JAutodocPlugin.getDefault().handleException(getShell(), e);
		}
	}
	
	private void exportReplacements() {
		FileDialog fileDialog = new FileDialog(tableViewer.getControl().getShell(), SWT.SAVE);
		fileDialog.setText("Export");
		fileDialog.setFileName("jautodoc_replacements.xml");
		fileDialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
		String selectedFile = fileDialog.open();
		if (selectedFile == null) {
			return;
		}
		
		File file = new File(selectedFile);
		if (file.exists() && !MessageDialog.openQuestion(getShell(),
				"File exists", "File '" + file.getName() + "' already exists. Replace?")) {
			return;
		}
		
		try {
			ReplacementSerializer.serialize(getReplacements(), file);
		} catch (Exception e) {
			JAutodocPlugin.getDefault().handleException(getShell(), e);
		}
	}
	
	private Shell getShell() {
		return tableViewer.getControl().getShell();
	}
}
