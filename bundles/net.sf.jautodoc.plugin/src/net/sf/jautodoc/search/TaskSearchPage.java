/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.HashSet;
import java.util.Set;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.Constants;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;

/**
 * Page with search parameters.
 */
public class TaskSearchPage extends DialogPage implements ISearchPage {

    private TaskSearchPattern searchPattern;
    private ISearchPageContainer container;
    private IDialogSettings dialogSettings;

    private Button missingJavadocButton;
    private Button missingParamTagButton;
    private Button missingReturnTagButton;
    private Button missingThrowsTagButton;

    private Button missingPeriodsButton;
    private Button generatedJavadocButton;
    private Button todoForGeneratedButton;

    private Button missingHeader;
    private Button outdatedHeader;

    private Button publicButton;
    private Button protectedButton;
    private Button packageButton;
    private Button privateButton;

    private Button filterTypesButton;
    private Button filterFieldsButton;
    private Button filterMethodsButton;
    private Button filterGetSetOnlyButton;
    private Button filterExcludeGetSetButton;

    private Button missingTagsButton;
    private Text missingTagsText;


    public void setContainer(ISearchPageContainer container) {
        this.container = container;
    }

    public void createControl(final Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);

        createSearchForGroup(composite);
        createVisibilityAndFilterGroups(composite);
        createHeaderGroup(composite);
        createMissingTagGroup(composite);

        updateControlStates();
        updateEnablements();
        initListener();
        setControl(composite);
    }

    public boolean performAction() {
        final TaskSearchQuery query = new TaskSearchQuery(getCompilationUnits(), getUpdatedSearchPattern());
        NewSearchUI.runQueryInBackground(query);
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        updateOKStatus();
        super.setVisible(visible);
    }

    @Override
    public void dispose() {
        getSearchPattern().store(getDialogSettings());
        super.dispose();
    }

    private void updateOKStatus() {
        container.setPerformActionEnabled(isValidSearchPattern());
    }

    private boolean isValidSearchPattern() {
        return missingJavadocButton.getSelection()
                || missingParamTagButton.getSelection()
                || missingReturnTagButton.getSelection()
                || missingThrowsTagButton.getSelection()
                || missingPeriodsButton.getSelection()
                || generatedJavadocButton.getSelection()
                || todoForGeneratedButton.getSelection()
                || missingHeader.getSelection()
                || outdatedHeader.getSelection()
                || missingTagsButton.getSelection() && missingTagsText.getText().trim().length() > 0;
    }

    private void initListener() {
        final UpdateOKListener listener = new UpdateOKListener();
        missingJavadocButton.addSelectionListener(listener);
        missingParamTagButton.addSelectionListener(listener);
        missingReturnTagButton.addSelectionListener(listener);
        missingThrowsTagButton.addSelectionListener(listener);
        missingPeriodsButton.addSelectionListener(listener);
        generatedJavadocButton.addSelectionListener(listener);
        todoForGeneratedButton.addSelectionListener(listener);
        missingHeader.addSelectionListener(listener);
        outdatedHeader.addSelectionListener(listener);
        missingTagsButton.addSelectionListener(listener);
        missingTagsText.addModifyListener(listener);
    }

    private void createVisibilityAndFilterGroups(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

        createVisibilityGroup(composite);
        createFilterGroup(composite);
    }

    private void createSearchForGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        group.setText("Javadoc");

        missingJavadocButton = new Button(group, SWT.CHECK);
        missingJavadocButton.setText("Missing Javadoc");
        missingParamTagButton = new Button(group, SWT.CHECK);
        missingParamTagButton.setText("Missing / Invalid @param tag");
        missingReturnTagButton = new Button(group, SWT.CHECK);
        missingReturnTagButton.setText("Missing / Invalid @return tag");
        missingThrowsTagButton = new Button(group, SWT.CHECK);
        missingThrowsTagButton.setText("Missing / Invalid @throws tag");

        generatedJavadocButton = new Button(group, SWT.CHECK);
        generatedJavadocButton.setText("Generated Javadoc");
        todoForGeneratedButton = new Button(group, SWT.CHECK);
        todoForGeneratedButton.setText("ToDo for generated Javadoc");
        missingPeriodsButton = new Button(group, SWT.CHECK);
        missingPeriodsButton.setText("Missing period on first sentence");
    }

    private void createHeaderGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
        group.setText("File Header");

        missingHeader = new Button(group, SWT.CHECK);
        missingHeader.setText("Missing file header");
        outdatedHeader = new Button(group, SWT.CHECK);
        outdatedHeader.setText("Outdated file header");
    }

    private void createVisibilityGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_BOTH));
        group.setText(Constants.LABEL_VISIBILITY);

        publicButton = new Button(group, SWT.CHECK);
        publicButton.setText(Constants.LABEL_VISIBILITY_PUBLIC);

        protectedButton = new Button(group, SWT.CHECK);
        protectedButton.setText(Constants.LABEL_VISIBILITY_PROTECTED);

        packageButton = new Button(group, SWT.CHECK);
        packageButton.setText(Constants.LABEL_VISIBILITY_PACKAGE);

        privateButton = new Button(group, SWT.CHECK);
        privateButton.setText(Constants.LABEL_VISIBILITY_PRIVATE);
    }

    private void createFilterGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Constants.LABEL_FILTER);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL_BOTH));

        filterTypesButton = new Button(group, SWT.CHECK);
        filterTypesButton.setText("Types");

        filterFieldsButton = new Button(group, SWT.CHECK);
        filterFieldsButton.setText("Fields");

        filterMethodsButton = new Button(group, SWT.CHECK);
        filterMethodsButton.setText("Methods");
        filterMethodsButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                updateEnablements();
            }
        });

        filterGetSetOnlyButton = new Button(group, SWT.CHECK);
        filterGetSetOnlyButton.setText(Constants.LABEL_FILTER_GETSET);
        GridData getterOnlyGridData = new GridData();
        getterOnlyGridData.horizontalIndent = 18;
        filterGetSetOnlyButton.setLayoutData(getterOnlyGridData);
        filterGetSetOnlyButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (filterGetSetOnlyButton.getSelection()) {
                    filterExcludeGetSetButton.setSelection(false);
                }
            }
        });

        filterExcludeGetSetButton = new Button(group, SWT.CHECK);
        filterExcludeGetSetButton.setText("Exclude [G,S]etter");
        GridData excludeGetterGridData = new GridData();
        excludeGetterGridData.horizontalIndent = 18;
        filterExcludeGetSetButton.setLayoutData(excludeGetterGridData);
        filterExcludeGetSetButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (filterExcludeGetSetButton.getSelection()) {
                    filterGetSetOnlyButton.setSelection(false);
                }
            }
        });
    }

    private void createMissingTagGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        group.setText("Search For Missing Tags");

        missingTagsButton = new Button(group, SWT.CHECK);
        missingTagsButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                updateEnablements();
            }
        });
        missingTagsText = new Text(group, SWT.BORDER);
        missingTagsText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label description = new Label(group, SWT.NONE);
        description.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        description.setText("Comma separated (tag = missing and empty, tag- = only missing, tag+ = only empty)");
    }

    private void updateEnablements() {
        filterGetSetOnlyButton.setEnabled(filterMethodsButton.getSelection());
        filterExcludeGetSetButton.setEnabled(filterMethodsButton.getSelection());
        missingTagsText.setEnabled(missingTagsButton.getSelection());
    }

    private void updateControlStates() {
        final TaskSearchPattern pattern = getSearchPattern();

        missingJavadocButton.setSelection(pattern.isMissingJavadoc());
        missingParamTagButton.setSelection(pattern.isMissingParamTag());
        missingReturnTagButton.setSelection(pattern.isMissingReturnTag());
        missingThrowsTagButton.setSelection(pattern.isMissingThrowsTag());

        missingPeriodsButton.setSelection(pattern.isMissingPeriods());
        generatedJavadocButton.setSelection(pattern.isGeneratedJavadoc());
        todoForGeneratedButton.setSelection(pattern.isTodoForGenerated());

        missingHeader.setSelection(pattern.isMissingHeader());
        outdatedHeader.setSelection(pattern.isOutdatedHeader());

        publicButton.setSelection(pattern.isIncludePublic());
        protectedButton.setSelection(pattern.isIncludeProtected());
        packageButton.setSelection(pattern.isIncludePackage());
        privateButton.setSelection(pattern.isIncludePrivate());

        filterTypesButton.setSelection(pattern.isIncludeTypes());
        filterFieldsButton.setSelection(pattern.isIncludeFields());
        filterMethodsButton.setSelection(pattern.isIncludeMethods());
        filterGetSetOnlyButton.setSelection(pattern.isGetterSetterOnly());
        filterExcludeGetSetButton.setSelection(pattern.isExcludeGetterSetter());

        missingTagsButton.setSelection(pattern.isSearchMissingTags());
        missingTagsText.setText(pattern.getMissingTagString());
    }

    private TaskSearchPattern getUpdatedSearchPattern() {
        final TaskSearchPattern pattern = getSearchPattern();

        pattern.setMissingJavadoc(missingJavadocButton.getSelection());
        pattern.setMissingParamTag(missingParamTagButton.getSelection());
        pattern.setMissingReturnTag(missingReturnTagButton.getSelection());
        pattern.setMissingThrowsTag(missingThrowsTagButton.getSelection());

        pattern.setMissingPeriods(missingPeriodsButton.getSelection());
        pattern.setGeneratedJavadoc(generatedJavadocButton.getSelection());
        pattern.setTodoForGenerated(todoForGeneratedButton.getSelection());

        pattern.setMissingHeader(missingHeader.getSelection());
        pattern.setOutdatedHeader(outdatedHeader.getSelection());

        pattern.setVisibilityPublic(publicButton.getSelection());
        pattern.setVisibilityProtected(protectedButton.getSelection());
        pattern.setVisibilityPackage(packageButton.getSelection());
        pattern.setVisibilityPrivate(privateButton.getSelection());

        pattern.setFilterTypes(filterTypesButton.getSelection());
        pattern.setFilterFields(filterFieldsButton.getSelection());
        pattern.setFilterMethods(filterMethodsButton.getSelection());
        pattern.setFilterGetSetOnly(filterGetSetOnlyButton.getSelection());
        pattern.setFilterExcludeGetSet(filterExcludeGetSetButton.getSelection());

        pattern.setSearchMissingTags(missingTagsButton.getSelection());
        pattern.setMissingTagString(missingTagsText.getText().trim());

        return pattern;
    }

    private TaskSearchPattern getSearchPattern() {
        if (searchPattern == null) {
            searchPattern = TaskSearchPattern.create(getDialogSettings());
        }
        return searchPattern;
    }

    private IDialogSettings getDialogSettings() {
        if (dialogSettings == null) {
            dialogSettings = JAutodocPlugin.getDefault().getDialogSettingsSection("TaskSearchPage");
        }
        return dialogSettings;
    }

    private ICompilationUnit[] getCompilationUnits() {
        final Set<ICompilationUnit> cus = new HashSet<ICompilationUnit>();

        try {
            switch (container.getSelectedScope()) {
            case ISearchPageContainer.WORKSPACE_SCOPE:
                TaskSearchHelper.collectCompilationUnitsOnWorkspace(cus);
                break;
            case ISearchPageContainer.SELECTION_SCOPE:
                final IStructuredSelection selection = (IStructuredSelection)container.getSelection();
                TaskSearchHelper.collectCompilationUnitsOnSelection(selection, cus);
                break;
            case ISearchPageContainer.SELECTED_PROJECTS_SCOPE:
                final String[] projectNames = container.getSelectedProjectNames();
                TaskSearchHelper.collectCompilationUnitsOnSelectedProjects(projectNames, cus);
                break;
            case ISearchPageContainer.WORKING_SET_SCOPE:
                final IWorkingSet[] workingSets = container.getSelectedWorkingSets();
                TaskSearchHelper.collectCompilationUnitsOnWorkingSets(workingSets, cus);
            }
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(getShell(), e);
        }
        return cus.toArray(new ICompilationUnit[cus.size()]);
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    private class UpdateOKListener extends SelectionAdapter implements ModifyListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            updateOKStatus();
        }

        public void modifyText(ModifyEvent e) {
            updateOKStatus();
        }
    }
}
