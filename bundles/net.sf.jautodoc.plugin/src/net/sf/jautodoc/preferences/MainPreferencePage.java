/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.preferences.ProjectSelectionDialog;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.replacements.ReplacementBlock;
import net.sf.jautodoc.preferences.templates.TemplatePreferencePage;


/**
 * Main preferences and project property page.
 */
@SuppressWarnings("restriction")
public class MainPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage, IWorkbenchPropertyPage, Constants {

    private PreferenceManager preferenceManager;

    private IProject project; // project or null

    private Link scopeLink;
    private Button projectSettingsButton;
    private Composite basePanel;
    private ControlEnableState enableState;
    private ControlEnableState btnEnableState;

    private OptionsBlock     ob;
    private ReplacementBlock rb;

    private Composite buttonBar;

    private boolean enableScopeLink = true;

    /** Hack for Eclipse 4.5 RC3 bug. */ 
    private GridLayout contributeButtonsLayout;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(Composite parent){
        super.createControl(parent);
        /* Hack for Eclipse 4.5 RC3 bug. numColumns is overridden after calling contributeButtons()*/
        if (contributeButtonsLayout != null && contributeButtonsLayout.numColumns < 3) {
            contributeButtonsLayout.numColumns += 3;
        }
        updateControls();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        basePanel = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth  = 0;
        layout.marginHeight = 0;
        basePanel.setLayout(layout);

        // -----------------------------------
        // options
        // -----------------------------------
        ob = new OptionsBlock(this);
        ob.createContents(basePanel);

        // -----------------------------------
        // replacements
        // -----------------------------------
        rb = new ReplacementBlock();
        rb.createContents(basePanel);

        // -----------------------------------

        Dialog.applyDialogFont(basePanel);
        return basePanel;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createDescriptionLabel(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Label createDescriptionLabel(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth  = 0;
        layout.numColumns   = 2;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        projectSettingsButton = new Button(composite, SWT.CHECK);
        projectSettingsButton.setText(LABEL_PROJECT_SETTINGS);
        projectSettingsButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        projectSettingsButton.setVisible(isProjectPropertyPage());
        projectSettingsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                updateControlStates();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                updateControlStates();
            }
        });

        createScopeLink(composite);

        if (isProjectPropertyPage()) {
            Label horizontalLine= new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
            horizontalLine.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
            horizontalLine.setFont(composite.getFont());
        }

        return super.createDescriptionLabel(parent);
    }

    @Override
    protected void contributeButtons(final Composite parent) {
        this.buttonBar = parent;

        contributeButtonsLayout = (GridLayout) parent.getLayout();
        contributeButtonsLayout.numColumns += 3;
        parent.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));

        Button importButton = createButton(parent, "Import All...");
        importButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performImport();
            }
        });

        Button exportButton = createButton(parent, "Export All...");
        exportButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performExport();
            }
        });

        final Label filler = new Label(parent, SWT.NONE);
        filler.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    }

    private Button createButton(final Composite parent, final String text) {
        final Button button = new Button(parent, SWT.PUSH);
        Dialog.applyDialogFont(button);
        button.setText(text);

        final int widthHint = convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
        final Point minButtonSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);

        final GridData data = new GridData();
        data.widthHint = Math.max(widthHint, minButtonSize.x);
        button.setLayoutData(data);
        return button;
    }

    /**
     * Create link to change between project specific and
     * workspace settings.
     *
     * @param parent the parent composite
     */
    private void createScopeLink(Composite parent) {
        String text = isProjectPropertyPage() ? LABEL_CONFIGURE_WOKSPACE
                : LABEL_CONFIGURE_PROJECT;

        scopeLink = new Link(parent, SWT.NONE);
        scopeLink.setFont(parent.getFont());
        scopeLink.setText("<A>" + text + "</A>");  //$NON-NLS-1$//$NON-NLS-2$
        scopeLink.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                scopeLinkSelected();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                scopeLinkSelected();
            }
        });
    }

    /**
     * Change between project specific and workspace settings.
     */
    private void scopeLinkSelected() {
        if (isProjectPropertyPage()) {
            openWorkspacePreferences();
            return;
        }

        // open project property page
        Set<IJavaProject> projectsWithSpecifics = new HashSet<IJavaProject>();
        try {
            IJavaProject[] projects = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()).getJavaProjects();
            for (int i = 0; i < projects.length; i++) {
                IJavaProject curr = projects[i];
                if (hasProjectSpecificSettings(curr.getProject())) {
                    projectsWithSpecifics.add(curr);
                }
            }
        } catch (JavaModelException e) {/* ignore */}

        ProjectSelectionDialog dialog = new ProjectSelectionDialog(getShell(), projectsWithSpecifics);
        if (dialog.open() == Window.OK) {
            IJavaProject res = (IJavaProject)dialog.getFirstResult();
            openProjectProperties(res.getProject());
        }
    }

    /**
     * Enable/Disable controls.
     */
    private void updateControlStates() {
        updateEnableStates();
        updateScopeLinkState();
    }

    /**
     * For project property page enable controls, if project
     * specific settings is selected.
     */
    private void updateEnableStates() {
        if (!isProjectPropertyPage()) return;

        if (projectSettingsButton.getSelection()) {
            if (enableState != null) {
                enableState.restore();
                btnEnableState.restore();
                enableState = null;
            }
        } else {
            if (enableState == null) {
                enableState = ControlEnableState.disable(basePanel);
                btnEnableState = ControlEnableState.disable(buttonBar);
            }
        }
    }

    /**
     * Disable scope link, if project specific settings
     * is selected.
     */
    private void updateScopeLinkState() {
        if (isProjectPropertyPage()) {
            scopeLink.setEnabled(isEnableScopeLink() && !projectSettingsButton.getSelection());
        }
        else {
            scopeLink.setEnabled(isEnableScopeLink());
        }
    }

    /**
     * Checks for project specific settings.
     *
     * @param aProject the project
     * @return true, if project has specific settings
     */
    private boolean hasProjectSpecificSettings(IProject aProject) {
        return aProject != null
                && ConfigurationManager.getPreferenceStore(aProject)
                        .getBoolean(PROJECT_SPECIFIC);
    }

    /**
     * Sets the enable scope link flag.
     *
     * @param enable the new enable scope link flag
     */
    private void setEnableScopeLink(boolean enable) {
        enableScopeLink = enable;
    }

    /**
     * Checks if scope link should be enabled.
     *
     * @return true, if scope link should be enabled
     */
    private boolean isEnableScopeLink() {
        return enableScopeLink;
    }

    /**
     * Checks if this is a project property page.
     *
     * @return true, if this is a project property page
     */
    private boolean isProjectPropertyPage() {
        return project != null;
    }

    /**
     * Open workspace preferences dialog.
     */
    private void openWorkspacePreferences() {
        String id = ID_PREFERENCE_PAGE;
        Object data = Boolean.FALSE;
        PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { id }, data).open();
    }

    /**
     * Open project properties dialog.
     *
     * @param project the project
     */
    private void openProjectProperties(IProject project) {
        String id = ID_PROPERTY_PAGE;
        Object data = Boolean.FALSE;
        PreferencesUtil.createPropertyDialogOn(getShell(), project, id, new String[] { id }, data).open();
    }

    /**
     * Update controls with data from preferences store.
     */
    private void updateControls() {
        PreferenceStore prefStore = null;

        if (isProjectPropertyPage() && hasProjectSpecificSettings(project)) {
            projectSettingsButton.setSelection(true);
            prefStore = ConfigurationManager.getPreferenceStore(project);
        }
        else {
            projectSettingsButton.setSelection(false);
            prefStore = ConfigurationManager.getPreferenceStore();
        }

        String mode = prefStore.getString(MODE);
        if (MODE_COMPLETE.equals(mode)) {
            ob.completeButton.setSelection(true);
        }
        else if (MODE_KEEP.equals(mode)) {
            ob.keepButton.setSelection(true);
        }
        else if (MODE_REPLACE.equals(mode)) {
            ob.replaceButton.setSelection(true);
        }

        ob.publicButton.setSelection(prefStore.getBoolean(VISIBILITY_PUBLIC));
        ob.protectedButton.setSelection(prefStore.getBoolean(VISIBILITY_PROTECTED));
        ob.packageButton.setSelection(prefStore.getBoolean(VISIBILITY_PACKAGE));
        ob.privateButton.setSelection(prefStore.getBoolean(VISIBILITY_PRIVATE));

        ob.filterTypesButton.setSelection(prefStore.getBoolean(FILTER_TYPES));
        ob.filterFieldsButton.setSelection(prefStore.getBoolean(FILTER_FIELDS));
        ob.filterMethodsButton.setSelection(prefStore.getBoolean(FILTER_METHODS));
        ob.filterGetterSetterButton.setSelection(prefStore.getBoolean(FILTER_GETSET));
        ob.filterExcludeGetterSetterButton.setSelection(prefStore.getBoolean(FILTER_EXCLGETSET));
        ob.filterExcludeOverridingButton.setSelection(prefStore.getBoolean(FILTER_EXCLOVERRID));

        ob.todoButton.setSelection(prefStore.getBoolean(ADD_TODO));
        ob.dummyDocButton.setSelection(prefStore.getBoolean(CREATE_DUMMY_DOC));
        ob.singleLineButton.setSelection(prefStore.getBoolean(SINGLE_LINE));
        ob.useFormatterButton.setSelection(prefStore.getBoolean(USE_FORMATTER));
        ob.getSetFromFieldButton.setSelection(prefStore.getBoolean(GET_SET_FROM_FIELD));
        ob.includeSubPackagesButton.setSelection(prefStore.getBoolean(INCLUDE_SUBPACKAGES));

        ob.getSetFromFieldFirstButton.setSelection(prefStore.getBoolean(GET_SET_FROM_FIELD_FIRST));
        ob.getSetFromFieldReplaceButton.setSelection(prefStore.getBoolean(GET_SET_FROM_FIELD_REPLACE));

        ob.addHeaderButton.setSelection(prefStore.getBoolean(ADD_HEADER));
        ob.replaceHeaderButton.setSelection(prefStore.getBoolean(REPLACE_HEADER));
        ob.multiHeaderButton.setSelection(prefStore.getBoolean(MULTI_HEADER));
        ob.usePackageInfoButton.setSelection(prefStore.getBoolean(USE_PKG_INFO));

        ob.headerText      = prefStore.getString(HEADER_TEXT);
        ob.packageDocText  = prefStore.getString(PKG_DOC_TEXT);
        ob.packageInfoText = prefStore.getString(PKG_INFO_TEXT);

        ob.tagOrder = new ArrayList<String>(prefStore.getTagOrder());
        ob.properties = new HashMap<String, String>(prefStore.getProperties());
        ob.getSetFromFieldReplacements = new TreeSet<GetSetFromFieldReplacement>(prefStore.getGetSetFromFieldReplacements());

        ob.updateButtonStates();

        rb.setReplacements(prefStore.getReplacements());

        updateControlStates();
    }

    private void performImport() {
        final List<PreferenceType> preferenceTypes = new ArrayList<PreferenceType>(
                Arrays.asList(PreferenceType.values()));
        if (isProjectPropertyPage()) {
            preferenceTypes.remove(PreferenceType.TEMPLATES);
        }

        final ImportExportDialog ied = new ImportExportDialog(getShell(),
                ImportExportDialog.Type.IMPORT, preferenceTypes);
        if (ied.open() == Window.OK) {
            try {
                PreferenceSerializer.doImport(ob, rb, ied.getSelectedPreferenceTypes(), ied.getSelectedFileName());
                ob.updateButtonStates();
                refreshTemplatePreferencePage();
            } catch (Exception e) {
                JAutodocPlugin.getDefault().handleException(getShell(), e);
            }
        }
    }

    private void refreshTemplatePreferencePage() {
        if (preferenceManager != null) {
            final IPreferenceNode pn = preferenceManager.find(TemplatePreferencePage.PATH);
            if (pn != null && pn.getPage() == null) {
                pn.createPage();
            }

            final TemplatePreferencePage tpp = (TemplatePreferencePage)pn.getPage();
            if (tpp != null) {
                tpp.setProperties(ob.properties);
                tpp.refresh();
            }
        }
    }

    private void performExport() {
        final List<PreferenceType> preferenceTypes = new ArrayList<PreferenceType>(
                Arrays.asList(PreferenceType.values()));
        if (isProjectPropertyPage()) {
            preferenceTypes.remove(PreferenceType.TEMPLATES);
        }

        final ImportExportDialog ied = new ImportExportDialog(getShell(),
                ImportExportDialog.Type.EXPORT, preferenceTypes);
        if (ied.open() == Window.OK) {
            try {
                PreferenceSerializer.doExport(ob, rb, ied.getSelectedPreferenceTypes(), ied.getSelectedFileName());
            } catch (Exception e) {
                JAutodocPlugin.getDefault().handleException(getShell(), e);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        if (isProjectPropertyPage() && !projectSettingsButton.getSelection()) {
            return;
        }

        PreferenceStore prefStore = (PreferenceStore)getPreferenceStore();

        // mode
        String mode = prefStore.getDefaultString(MODE);
        if (MODE_COMPLETE.equals(mode)) {
            ob.completeButton.setSelection(true);
            ob.keepButton.setSelection(false);
            ob.replaceButton.setSelection(false);
        }
        else if (MODE_KEEP.equals(mode)) {
            ob.completeButton.setSelection(false);
            ob.keepButton.setSelection(true);
            ob.replaceButton.setSelection(false);
        }
        else if (MODE_REPLACE.equals(mode)) {
            ob.completeButton.setSelection(false);
            ob.keepButton.setSelection(false);
            ob.replaceButton.setSelection(true);
        }

        // visibility
        ob.publicButton.setSelection(prefStore.getDefaultBoolean(VISIBILITY_PUBLIC));
        ob.protectedButton.setSelection(prefStore.getDefaultBoolean(VISIBILITY_PROTECTED));
        ob.packageButton.setSelection(prefStore.getDefaultBoolean(VISIBILITY_PACKAGE));
        ob.privateButton.setSelection(prefStore.getDefaultBoolean(VISIBILITY_PRIVATE));

        // filter
        ob.filterTypesButton.setSelection(prefStore.getDefaultBoolean(FILTER_TYPES));
        ob.filterFieldsButton.setSelection(prefStore.getDefaultBoolean(FILTER_FIELDS));
        ob.filterMethodsButton.setSelection(prefStore.getDefaultBoolean(FILTER_METHODS));
        ob.filterGetterSetterButton.setSelection(prefStore.getDefaultBoolean(FILTER_GETSET));
        ob.filterExcludeGetterSetterButton.setSelection(prefStore.getDefaultBoolean(FILTER_EXCLGETSET));
        ob.filterExcludeOverridingButton.setSelection(prefStore.getDefaultBoolean(FILTER_EXCLOVERRID));

        // options
        ob.todoButton.setSelection(prefStore.getDefaultBoolean(ADD_TODO));
        ob.dummyDocButton.setSelection(prefStore.getDefaultBoolean(CREATE_DUMMY_DOC));
        ob.singleLineButton.setSelection(prefStore.getDefaultBoolean(SINGLE_LINE));
        ob.useFormatterButton.setSelection(prefStore.getDefaultBoolean(USE_FORMATTER));
        ob.getSetFromFieldButton.setSelection(prefStore.getDefaultBoolean(GET_SET_FROM_FIELD));
        ob.includeSubPackagesButton.setSelection(prefStore.getDefaultBoolean(INCLUDE_SUBPACKAGES));

        ob.getSetFromFieldFirstButton.setSelection(prefStore.getDefaultBoolean(GET_SET_FROM_FIELD_FIRST));
        ob.getSetFromFieldReplaceButton.setSelection(prefStore.getDefaultBoolean(GET_SET_FROM_FIELD_REPLACE));

        ob.addHeaderButton.setSelection(prefStore.getDefaultBoolean(ADD_HEADER));
        ob.replaceHeaderButton.setSelection(prefStore.getDefaultBoolean(REPLACE_HEADER));
        ob.multiHeaderButton.setSelection(prefStore.getDefaultBoolean(MULTI_HEADER));
        ob.usePackageInfoButton.setSelection(prefStore.getDefaultBoolean(USE_PKG_INFO));

        // keep header text
        // ob.headerText = prefStore.getDefaultString(HEADER_TEXT);

        ob.tagOrder.clear();
        ob.tagOrder.addAll(Arrays.asList(Constants.DEFAULT_TAG_ORDER.split(",")));

        ob.getSetFromFieldReplacements.clear();
        ob.getSetFromFieldReplacements.addAll(prefStore.getDefaultGetSetFromFieldReplacements());

        ob.updateButtonStates();

        // replacements
        rb.setReplacements(prefStore.getDefaultReplacements());

        updateControlStates();

        super.performDefaults();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        PreferenceStore prefStore = (PreferenceStore)getPreferenceStore();

        if (isProjectPropertyPage()) {
            prefStore.setValue(PROJECT_SPECIFIC, projectSettingsButton.getSelection());
            if (!projectSettingsButton.getSelection()) {
                return super.performOk();
            }
        }

        if (ob.completeButton.getSelection()) {
            prefStore.setValue(MODE, MODE_COMPLETE);
        }
        else if (ob.keepButton.getSelection()) {
            prefStore.setValue(MODE, MODE_KEEP);
        }
        else if (ob.replaceButton.getSelection()) {
            prefStore.setValue(MODE, MODE_REPLACE);
        }

        prefStore.setValue(VISIBILITY_PUBLIC,    ob.publicButton.getSelection());
        prefStore.setValue(VISIBILITY_PROTECTED, ob.protectedButton.getSelection());
        prefStore.setValue(VISIBILITY_PACKAGE,   ob.packageButton.getSelection());
        prefStore.setValue(VISIBILITY_PRIVATE,   ob.privateButton.getSelection());

        prefStore.setValue(FILTER_TYPES,         ob.filterTypesButton.getSelection());
        prefStore.setValue(FILTER_FIELDS,        ob.filterFieldsButton.getSelection());
        prefStore.setValue(FILTER_METHODS,       ob.filterMethodsButton.getSelection());
        prefStore.setValue(FILTER_GETSET,        ob.filterGetterSetterButton.getSelection());
        prefStore.setValue(FILTER_EXCLGETSET,    ob.filterExcludeGetterSetterButton.getSelection());
        prefStore.setValue(FILTER_EXCLOVERRID,   ob.filterExcludeOverridingButton.getSelection());

        prefStore.setValue(ADD_TODO,             ob.todoButton.getSelection());
        prefStore.setValue(CREATE_DUMMY_DOC,     ob.dummyDocButton.getSelection());
        prefStore.setValue(SINGLE_LINE,          ob.singleLineButton.getSelection());
        prefStore.setValue(USE_FORMATTER,        ob.useFormatterButton.getSelection());
        prefStore.setValue(GET_SET_FROM_FIELD,   ob.getSetFromFieldButton.getSelection());
        prefStore.setValue(INCLUDE_SUBPACKAGES,  ob.includeSubPackagesButton.getSelection());

        prefStore.setValue(GET_SET_FROM_FIELD_FIRST,   ob.getSetFromFieldFirstButton.getSelection());
        prefStore.setValue(GET_SET_FROM_FIELD_REPLACE, ob.getSetFromFieldReplaceButton.getSelection());

        prefStore.setValue(ADD_HEADER,           ob.addHeaderButton.getSelection());
        prefStore.setValue(REPLACE_HEADER,       ob.replaceHeaderButton.getSelection());
        prefStore.setValue(MULTI_HEADER,         ob.multiHeaderButton.getSelection());
        prefStore.setValue(USE_PKG_INFO,         ob.usePackageInfoButton.getSelection());
        prefStore.setValue(HEADER_TEXT,          ob.headerText);
        prefStore.setValue(PKG_DOC_TEXT,         ob.packageDocText);
        prefStore.setValue(PKG_INFO_TEXT,        ob.packageInfoText);

        prefStore.setTagOrder(ob.tagOrder);
        prefStore.setProperties(ob.properties);
        prefStore.setGetSetFromFieldReplacements(ob.getSetFromFieldReplacements);

        prefStore.setReplacements(rb.getReplacements());

        return super.performOk();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    @Override
    public void init(IWorkbench workbench) {
        preferenceManager = workbench.getPreferenceManager();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#getPreferenceStore()
     */
    @Override
    public IPreferenceStore getPreferenceStore() {
        if (isProjectPropertyPage()) {
            return ConfigurationManager.getPreferenceStore(project);
        } else {
            return ConfigurationManager.getPreferenceStore();
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#applyData(java.lang.Object)
     */
    @Override
    public void applyData(Object data) {
        if (data instanceof Boolean) {
            setEnableScopeLink(((Boolean)data).booleanValue());
        }
        updateScopeLinkState();
    }

    // ----------------------------------------------------
    // IWorkbenchPropertyPage
    // ----------------------------------------------------

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPropertyPage#getElement()
     */
    @Override
    public IAdaptable getElement() {
        return project;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
     */
    @Override
    public void setElement(IAdaptable element) {
        project = (IProject)element.getAdapter(IResource.class);
    }
}
