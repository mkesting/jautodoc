/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.templates;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.preferences.PreferenceStore;
import net.sf.jautodoc.templates.ITemplateKinds;
import net.sf.jautodoc.templates.ITemplateManager;
import net.sf.jautodoc.templates.TemplateEntry;
import net.sf.jautodoc.templates.rules.IRulesStrategy;
import net.sf.jautodoc.templates.rules.TemplateCodeScanner;
import net.sf.jautodoc.templates.viewer.TemplateViewerConfiguration;
import net.sf.jautodoc.utils.StringUtils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * The templates preferences page.
 */
public class TemplatePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

    public static final String PATH =
    		"org.eclipse.jdt.ui.preferences.JavaBasePreferencePage/" +
    		"net.sf.jautodoc.preferences.MainPreferencePage/" +
    		"net.sf.jautodoc.preferences.templates.TemplatePreferencePage";

	private Map<String, String> properties;
	private TemplateTreePanel view;
	private ITemplateManager  templateManager;

	private TreeItem typeRoot;
	private TreeItem fieldRoot;
	private TreeItem methodRoot;
	private TreeItem parameterRoot;
	private TreeItem exceptionRoot;


	/**
	 * Instantiates a new template tree page.
	 */
	public TemplatePreferencePage() {
		properties = new HashMap<String, String>(((PreferenceStore) getPreferenceStore())
				.getProperties());

		templateManager = JAutodocPlugin.getContext().getTemplateManager();
	}

	/**
	 * Refresh this page.
	 */
	public void refresh() {
	    if (view != null && view.templateTree != null) {
	        view.templateTree.removeAll();
            initializeTemplateTree();
	    }
	}

	public void setProperties(final Map<String, String> properties) {
	    this.properties.clear();
	    this.properties.putAll(properties);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		setDescription(Constants.TEMPLATES_PAGE_DESCRIPTION);
		setPreferenceStore(ConfigurationManager.getPreferenceStore());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(final Composite parent) {
		view = new TemplateTreePanel(parent, SWT.NONE);

		configureTemplateViewer();

		// context menu
		view.templateTree.addListener(SWT.MenuDetect, new Listener() {
			public void handleEvent(Event event) {
				showPopupMenu(event.x, event.y);
			}
		});

		view.buttonAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
		        Rectangle bounds = view.buttonAdd.getBounds();
		        Point point = view.buttonAdd.getParent().toDisplay(bounds.x, bounds.y);
		        point.y += bounds.height;
		        showPopupMenu(point.x, point.y);
			}
		});

		view.buttonEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TreeItem item = view.templateTree.getSelection()[0];
				TemplateEntry entry = (TemplateEntry)item.getData();
				if (showEditTemplateDialog(entry) == Window.OK) {
					item.setText(StringUtils.getLastElement(entry.getName(), '.'));
					showItemData(item);
				}
				view.templateTree.setFocus();
			}
		});

		view.buttonRemove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (MessageDialog.openConfirm(view.getShell(), "Remove Template",
						"Do you really want to remove this Template?")) {

					TreeItem selectedItem = view.templateTree.getSelection()[0];
					TreeItem parentItem = selectedItem.getParentItem();
					int index = parentItem.indexOf(selectedItem);
					((List<?>)parentItem.getData()).remove(index);
					selectedItem.dispose();

					// parent List item empty?
					if (parentItem.getItemCount() == 0 &&		// empty
						parentItem.getParentItem() != null) {	// and not root item
						parentItem.dispose();
					}
				}
				view.templateTree.setFocus();
			}
		});

		view.buttonImport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				importTemplates();
			}
		});

		view.buttonExport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				exportTemplates();
			}
		});

		view.buttonProperties.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				showPropertiesDialog();
			}
		});

		view.buttonUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveItem(view.templateTree.getSelection()[0], true);
			}
		});

		view.buttonDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				moveItem(view.templateTree.getSelection()[0], false);
			}
		});

		// tree listener
		view.templateTree.addListener(SWT.Expand, new ExpandListener());

		view.templateTree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] selection = view.templateTree.getSelection();
				TreeItem selectedItem = selection.length > 0 ? selection[0] : null;
				showItemData(selectedItem);
				updateButtons(selectedItem);
			}
		});

		// init the tree
		initializeTemplateTree();

		return view;
	}

	private void configureTemplateViewer() {
		ITextHover textHover 			= JAutodocPlugin.getContext().getTemplateTextHover(properties);
		IRulesStrategy[] ruleStrategies = JAutodocPlugin.getContext().getTemplateRuleStrategies();

		TemplateViewerConfiguration configuration = new TemplateViewerConfiguration(
				new TemplateCodeScanner(ruleStrategies),
				textHover);
		view.templateViewer.configure(configuration);
	}

	private void showPopupMenu(int x, int y) {
		Menu menu = createPopupMenu();
		menu.setLocation(x, y);
		menu.setVisible(true);
		while (!menu.isDisposed() && menu.isVisible()) {
			if (!view.getDisplay().readAndDispatch())
				view.getDisplay().sleep();
		}
		menu.dispose();
	}

	private Menu createPopupMenu() {
		Menu menu = null;
		if (view.templateTree.getSelectionCount() > 0) {
			TreeItem selectedItem = view.templateTree.getSelection()[0];

			if (selectedItem.getParentItem() == null) {
				// menu for root items
				menu = createRootItemsMenu();
			}
			else {
				boolean showNestedOnly = false;
				Object data = selectedItem.getData();
				if (!(data instanceof TemplateEntry)) {
					// Types-, Fields-, Methods-,... node selected
					// -> show menu for allowed childs of the parent only
					showNestedOnly = true;
					selectedItem = selectedItem.getParentItem();
					data = selectedItem.getData();
				}

				TemplateEntry entry = (TemplateEntry)data;
				if (entry.getKind() == ITemplateKinds.TYPE) {
					menu = createTypeMenu(selectedItem, showNestedOnly);
				}
				else if (entry.getKind() == ITemplateKinds.FIELD) {
					menu = createFieldMenu(selectedItem);
				}
				else if (entry.getKind() == ITemplateKinds.METHOD) {
					menu = createMethodMenu(selectedItem, showNestedOnly);
				}
				else if (entry.getKind() == ITemplateKinds.PARAMETER) {
					menu = createParameterMenu(selectedItem);
				}
				else if (entry.getKind() == ITemplateKinds.EXCEPTION) {
					menu = createExceptionMenu(selectedItem);
				}
			}
		}

		return menu != null ? menu : createRootItemsMenu();
	}

	private Menu createRootItemsMenu() {
		// types
		Menu menu = new Menu(view.getShell(), SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
        item.setText(Constants.TEMPLATES_MENU_LABEL_TYPES);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		typeRoot, ITemplateKinds.TYPE));
        // fields
        item = new MenuItem(menu, SWT.PUSH);
        item.setText(Constants.TEMPLATES_MENU_LABEL_FIELDS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		fieldRoot, ITemplateKinds.FIELD));
        // method
        item = new MenuItem(menu, SWT.PUSH);
        item.setText(Constants.TEMPLATES_MENU_LABEL_METHODS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		methodRoot, ITemplateKinds.METHOD));
        // parameters
        item = new MenuItem(menu, SWT.PUSH);
        item.setText(Constants.TEMPLATES_MENU_LABEL_PARAMETERS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		parameterRoot, ITemplateKinds.PARAMETER));
        // exceptions
        item = new MenuItem(menu, SWT.PUSH);
        item.setText(Constants.TEMPLATES_MENU_LABEL_EXCEPTIONS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		exceptionRoot, ITemplateKinds.EXCEPTION));

		return menu;
	}

	private Menu createTypeMenu(TreeItem selectedItem, boolean showNestedOnly) {
		Menu menu = new Menu(view.getShell(), SWT.POP_UP);
		MenuItem item = null;

		// types
		if (!showNestedOnly) {
			item = new MenuItem(menu, SWT.PUSH);
			item.setText(Constants.TEMPLATES_MENU_LABEL_TYPES);
			item.addListener(SWT.Selection,	new NewTemplateListener(
					selectedItem.getParentItem(), ITemplateKinds.TYPE));
		}

		// nested elements...
		String nestedLabel = showNestedOnly ? "" : Constants.TEMPLATES_MENU_LABEL_NESTED + " ";

		// nested types
        item = new MenuItem(menu, SWT.PUSH);
		item.setText(nestedLabel + Constants.TEMPLATES_MENU_LABEL_TYPES);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		selectedItem, ITemplateKinds.TYPE));
        // nested fields
        item = new MenuItem(menu, SWT.PUSH);
        item.setText(nestedLabel + Constants.TEMPLATES_MENU_LABEL_FIELDS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		selectedItem, ITemplateKinds.FIELD));
        // nested methods
        item = new MenuItem(menu, SWT.PUSH);
        item.setText(nestedLabel + Constants.TEMPLATES_MENU_LABEL_METHODS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		selectedItem, ITemplateKinds.METHOD));

		return menu;
	}

	private Menu createFieldMenu(TreeItem selectedItem) {
		Menu menu = new Menu(view.getShell(), SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Constants.TEMPLATES_MENU_LABEL_FIELDS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		selectedItem.getParentItem(), ITemplateKinds.FIELD));

		return menu;
	}

	private Menu createMethodMenu(TreeItem selectedItem, boolean showNestedOnly) {
		Menu menu = new Menu(view.getShell(), SWT.POP_UP);
		MenuItem item = null;

		// methods
		if (!showNestedOnly) {
			item = new MenuItem(menu, SWT.PUSH);
			item.setText(Constants.TEMPLATES_MENU_LABEL_METHODS);
			item.addListener(SWT.Selection, new NewTemplateListener(
					selectedItem.getParentItem(), ITemplateKinds.METHOD));
		}

		// nested elements...
		String nestedLabel = showNestedOnly ? "" : Constants.TEMPLATES_MENU_LABEL_NESTED + " ";

		// nested parameters
        item = new MenuItem(menu, SWT.PUSH);
		item.setText(nestedLabel + Constants.TEMPLATES_MENU_LABEL_PARAMETERS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		selectedItem, ITemplateKinds.PARAMETER));
        // nested exceptions
        item = new MenuItem(menu, SWT.PUSH);
		item.setText(nestedLabel + Constants.TEMPLATES_MENU_LABEL_EXCEPTIONS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		selectedItem, ITemplateKinds.EXCEPTION));

		return menu;
	}

	private Menu createParameterMenu(TreeItem selectedItem) {
		Menu menu = new Menu(view.getShell(), SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Constants.TEMPLATES_MENU_LABEL_PARAMETERS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		selectedItem.getParentItem(), ITemplateKinds.PARAMETER));

		return menu;
	}

	private Menu createExceptionMenu(TreeItem selectedItem) {
		Menu menu = new Menu(view.getShell(), SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(Constants.TEMPLATES_MENU_LABEL_EXCEPTIONS);
        item.addListener(SWT.Selection, new NewTemplateListener(
        		selectedItem.getParentItem(), ITemplateKinds.EXCEPTION));

		return menu;
	}

	private int showEditTemplateDialog(TemplateEntry entry) {
		EditTemplateDialog templateDialog = new EditTemplateDialog(view
				.getShell(), entry, properties);

		return templateDialog.open();
	}

	private void initializeTemplateTree() {
		try {
			typeRoot	  = addRootItem(view.templateTree, Constants.TEMPLATES_LABEL_TYPES, 	 templateManager.getTypeTemplates());
			fieldRoot	  = addRootItem(view.templateTree, Constants.TEMPLATES_LABEL_FIELDS, 	 templateManager.getFieldTemplates());
			methodRoot 	  = addRootItem(view.templateTree, Constants.TEMPLATES_LABEL_METHODS, 	 templateManager.getMethodTemplates());
			parameterRoot = addRootItem(view.templateTree, Constants.TEMPLATES_LABEL_PARAMETERS, templateManager.getParameterTemplates());
			exceptionRoot = addRootItem(view.templateTree, Constants.TEMPLATES_LABEL_EXCEPTIONS, templateManager.getExceptionTemplates());
		} catch (Exception e) {
			JAutodocPlugin.getDefault().handleException(view.getShell(), e);
		}

		showItemData(null);
		updateButtons(null);
	}

	private void showItemData(TreeItem item) {
		Object data = item == null ? null : item.getData();
		if (data == null || !(data instanceof TemplateEntry)) {
			view.textPattern.setText("");
			view.textExample.setText("");
			view.templateViewer.setDocument(new Document());
		}
		else {
			TemplateEntry entry = (TemplateEntry)data;
			view.textPattern.setText(entry.getRegex());
			view.textExample.setText(entry.getExample());
			view.templateViewer.setDocument(new Document(entry.getText()));;
		}
	}

	private void updateButtons(TreeItem item) {
		if (item == null || !(item.getData() instanceof TemplateEntry)) {
			view.buttonUp.setEnabled(false);
			view.buttonDown.setEnabled(false);
			view.buttonEdit.setEnabled(false);
			view.buttonRemove.setEnabled(false);
			return;
		}

		TemplateEntry entry = (TemplateEntry)item.getData();
		if (entry.isDefaultTemplate()) {
			view.buttonUp.setEnabled(false);
			view.buttonDown.setEnabled(false);
			view.buttonEdit.setEnabled(true);
			view.buttonRemove.setEnabled(false);
			return;
		}

		TreeItem parent = item.getParentItem();
		int index = parent.indexOf(item);
		view.buttonUp.setEnabled(index > 0);
		view.buttonDown.setEnabled(index < parent.getItemCount() - 1 &&
				!getEntryFromItem(parent, index + 1).isDefaultTemplate());
		view.buttonEdit.setEnabled(true);
		view.buttonRemove.setEnabled(true);
	}

	private TemplateEntry getEntryFromItem(TreeItem parent, int index) {
		TreeItem item = parent.getItem(index);
		return (TemplateEntry)item.getData();
	}

    private void moveItem(TreeItem item, boolean up) {
		view.templateTree.setRedraw(false);

		String itemText = item.getText();
		TemplateEntry entry = (TemplateEntry)item.getData();

		TreeItem parent = item.getParentItem();
		int oldIndex = parent.indexOf(item);
		int newIndex = up ? parent.indexOf(item) - 1 : parent.indexOf(item) + 1;
		item.dispose();

		TreeItem newItem = new TreeItem(parent, SWT.NONE, newIndex);
		newItem.setText(itemText);
		newItem.setData(entry);
		if (!entry.getChildTemplates().isEmpty()) {
			new TreeItem(newItem, 0);
		}

		// move in template set
		List<TemplateEntry> list = asTemplateList(parent.getData());
		list.remove(oldIndex);
		list.add(newIndex, entry);

		view.templateTree.setRedraw(true);
		// view.templateTree.setSelection(newItem); since 3.2
		view.templateTree.setSelection(new TreeItem[] {newItem});
		view.templateTree.showSelection();
		view.templateTree.setFocus();
	}

	private static void addItem(TreeItem parent, String name, List<TemplateEntry> templateList) {
		if (!templateList.isEmpty()) {
			TreeItem item = new TreeItem(parent, SWT.NONE);
			item.setText(name);
			item.setData(templateList);
			new TreeItem(item, SWT.NONE);
		}
	}

	private static TreeItem addRootItem(Tree tree, String name, List<TemplateEntry> templateList) {
		TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setText(name);
		item.setData(templateList);
		if (!templateList.isEmpty()) {
			new TreeItem(item, SWT.NONE);
		}
		return item;
	}

	private static void ensureChildItems(TreeItem parent) {
		TreeItem[] items = parent.getItems();
		if (items.length > 0) {
			if (items[0].getData() != null) {
				return; // already loaded
			}
			// remove dummy item
			items[0].dispose();
		}

		// create child items
		Object data = parent.getData();
		if (data instanceof TemplateEntry) {
			// create items for all kinds of elements
			TemplateEntry entry = (TemplateEntry)data;
			addItem(parent, Constants.TEMPLATES_LABEL_TYPES, 	  entry.getChildTemplates().getTypeTemplates());
			addItem(parent, Constants.TEMPLATES_LABEL_FIELDS, 	  entry.getChildTemplates().getFieldTemplates());
			addItem(parent, Constants.TEMPLATES_LABEL_METHODS, 	  entry.getChildTemplates().getMethodTemplates());
			addItem(parent, Constants.TEMPLATES_LABEL_PARAMETERS, entry.getChildTemplates().getParameterTemplates());
			addItem(parent, Constants.TEMPLATES_LABEL_EXCEPTIONS, entry.getChildTemplates().getExceptionTemplates());
		}
		else if (data instanceof List<?>) {
			// create items for all elements in list
			List<TemplateEntry> templates = asTemplateList(data);
			for (int i = 0; i < templates.size(); i++) {
				TreeItem item = new TreeItem(parent, SWT.NONE);
				TemplateEntry entry = (TemplateEntry)templates.get(i);
				item.setText(StringUtils.getLastElement(entry.getName(), '.'));
				item.setData(entry);
				if (!entry.getChildTemplates().isEmpty()) {
					new TreeItem(item, SWT.NONE); // lazy loading -> add dummy item
				}
			}
		}
	}

	private static String getLabel(int templateKind) {
		String label = "";

		switch (templateKind) {
		case ITemplateKinds.TYPE:
			label = Constants.TEMPLATES_LABEL_TYPES;
			break;
		case ITemplateKinds.FIELD:
			label = Constants.TEMPLATES_LABEL_FIELDS;
			break;
		case ITemplateKinds.METHOD:
			label = Constants.TEMPLATES_LABEL_METHODS;
			break;
		case ITemplateKinds.PARAMETER:
			label = Constants.TEMPLATES_LABEL_PARAMETERS;
			break;
		case ITemplateKinds.EXCEPTION:
			label = Constants.TEMPLATES_LABEL_EXCEPTIONS;
			break;
		}
		return label;
	}

	private static TreeItem findChildItem(TreeItem parent, String label) {
		TreeItem childItem = null;

		TreeItem[] childs = parent.getItems();
		for (int i = 0; i < childs.length && childItem == null; ++i) {
			if (label.equals(childs[i].getText())) {
				childItem = childs[i];
			}
		}
		return childItem;
	}

	private void importTemplates() {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
		fileDialog.setText("Import templates");
		fileDialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
		String selectedFile = fileDialog.open();
		if (selectedFile == null) {
			return;
		}

		try {
			templateManager.loadTemplates(new File(selectedFile));
			view.templateTree.removeAll();
			initializeTemplateTree();
		} catch (Exception e) {
			MessageDialog.openError(getShell(),
					"Error", "Could not read file: " + e.getMessage());
		}
	}

	private void exportTemplates() {
		FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
		fileDialog.setText("Export templates");
		fileDialog.setFileName("jautodoc_templates.xml");
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
			templateManager.storeTemplates(file);
		} catch (Exception e) {
			MessageDialog.openError(getShell(),
					"Error", "Could not write file: " + e.getMessage());
		}
	}

	private void showPropertiesDialog() {
		Map<String, String> props = new HashMap<String, String>(properties);
		EditPropertiesDialog dialog = new EditPropertiesDialog(getShell(), props);
		if (dialog.open() == EditPropertiesDialog.OK) {
			properties.clear();
			properties.putAll(props);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performCancel()
	 */
	public boolean performCancel() {
		try {
			templateManager.loadTemplates();
			if (view != null) {
			    view.templateTree.removeAll();
			    initializeTemplateTree();
			}
			return true;
		} catch (Exception e) {
			JAutodocPlugin.getDefault().handleException(view.getShell(), e);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		try {
			templateManager.loadDefaultTemplates();
			if (view != null) {
			    view.templateTree.removeAll();
			    initializeTemplateTree();
			}
		} catch (Exception e) {
			JAutodocPlugin.getDefault().handleException(view.getShell(), e);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		try {
			templateManager.storeTemplates();
			((PreferenceStore)getPreferenceStore()).setProperties(properties);
			return true;
		} catch (Exception e) {
			JAutodocPlugin.getDefault().handleException(view.getShell(), e);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
	 */
	public IPreferenceStore getPreferenceStore() {
		// always workspace settings
		return ConfigurationManager.getPreferenceStore();
	}

	@SuppressWarnings("unchecked")
    private static List<TemplateEntry> asTemplateList(Object data) {
        return (List<TemplateEntry>)data;
    }

	// ----------------------------------------------------
	// Inner classes
	// ----------------------------------------------------

	/**
	 * ExpandListener, for lazy loading of child items.
	 */
	private static class ExpandListener implements Listener {
		public void handleEvent(final Event event) {
			final TreeItem root = (TreeItem) event.item;
			ensureChildItems(root);
		}
	}

	/**
	 * Listener for new template action.
	 */
	private class NewTemplateListener implements Listener {
		private TreeItem parentItem;
		private int templateKind = ITemplateKinds.UNKNOWN;


		/**
		 * The Constructor.
		 *
		 * @param parentItem the parent item
		 * @param templateKind the template kind
		 */
		public NewTemplateListener(TreeItem parentItem, int templateKind) {
			this.parentItem = parentItem;
			this.templateKind = templateKind;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		public void handleEvent(Event event) {
			ensureChildItems(parentItem);

			TemplateEntry parentEntry = null;
			Object data = parentItem.getData();
			if (data instanceof TemplateEntry) {
				parentEntry = (TemplateEntry)data;
			}
			else {
				// List item -> get template entry from parent item
				TreeItem parentParentItem = parentItem.getParentItem();
				if (parentParentItem != null) {
					parentEntry = (TemplateEntry)parentParentItem.getData();
				}
				// else: root item -> no parent entry
			}

			// show dialog
			TemplateEntry entry = new TemplateEntry(parentEntry, templateKind);
			if (showEditTemplateDialog(entry) != Window.OK) {
				return; // canceled
			}

			// for Entry items get the appropriate child List item.
			if (data instanceof TemplateEntry) {
				String label = getLabel(templateKind);
				TreeItem newParentItem = findChildItem(parentItem, label);
				if (newParentItem == null) {
					newParentItem = new TreeItem(parentItem, SWT.NONE);
					newParentItem.setText(label);
					newParentItem.setData(parentEntry.getChildTemplates(templateKind));
				}
				else {
					// new item -> load childs
					ensureChildItems(newParentItem);
				}
				parentItem = newParentItem;
			}

			// add new item above the selected item or at the beginning
			int index = 0;
			if (view.templateTree.getSelectionCount() > 0 &&
					view.templateTree.getSelection()[0] != parentItem) {
				index = parentItem.indexOf(view.templateTree.getSelection()[0]);
				index = index < 0 ? 0 : index;
			}

			// add new template entry to the current list
			asTemplateList(parentItem.getData()).add(index, entry);

			// add new item at the given index
			TreeItem item = new TreeItem(parentItem, SWT.NONE, index);
			item.setText(StringUtils.getLastElement(entry.getName(), '.'));
			item.setData(entry);
			// view.templateTree.setSelection(item); since 3.2
			view.templateTree.setSelection(new TreeItem[] {item});
			view.templateTree.setFocus();
			showItemData(item);
			updateButtons(item);
		}
	}
}
