/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jautodoc.preferences.templates.EditHeaderDialog;
import net.sf.jautodoc.preferences.templates.EditPackageJavadocDialog;

import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;


/**
 * Creates a composite for JAutodoc options.
 */
public class OptionsBlock {

    private final MainPreferencePage preferencePage;

    private Composite basePanel;

    protected Button completeButton;
    protected Button keepButton;
    protected Button replaceButton;

    protected Button publicButton;
    protected Button protectedButton;
    protected Button packageButton;
    protected Button privateButton;

    protected Button filterTypesButton;
    protected Button filterFieldsButton;
    protected Button filterMethodsButton;
    protected Button filterGetterSetterButton;
    protected Button filterExcludeGetterSetterButton;
    protected Button filterExcludeOverridingButton;

    protected Button todoButton;
    protected Button dummyDocButton;
    protected Button singleLineButton;
    protected Button useFormatterButton;
    protected Button getSetFromFieldButton;
    protected Button getSetFromFieldEditButton;
    protected Button getSetFromFieldFirstButton;
    protected Button getSetFromFieldReplaceButton;
    protected Button includeSubPackagesButton;

    protected Button addHeaderButton;
    protected Button editHeaderButton;
    protected Button replaceHeaderButton;
    protected Button multiHeaderButton;

    protected Button editPackageDocButton;
    protected Button usePackageInfoButton;

    protected Button editTagOrderButton;

    protected String headerText = "";
    protected String packageDocText = "";
    protected String packageInfoText = "";

    protected List<String> tagOrder;
    protected Map<String, String> properties;
    protected Set<GetSetFromFieldReplacement> getSetFromFieldReplacements;


    public OptionsBlock() {
        this(null);
    }

    public OptionsBlock(final MainPreferencePage preferencePage) {
        this.preferencePage = preferencePage;
    }

    protected Control createContents(Composite parent) {
        basePanel = new Composite(parent, SWT.NONE);
        GridLayout baseLayout = new GridLayout();
        baseLayout.marginWidth  = 0;
        baseLayout.marginHeight = 0;
        basePanel.setLayout(baseLayout);
        basePanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // -----------------------------------
        // top composite
        // -----------------------------------
        Composite topComposite = new Composite(basePanel, SWT.NONE);
        GridLayout topLayout = new GridLayout();
        topLayout.numColumns   = 3;
        topLayout.marginWidth  = 0;
        topLayout.marginHeight = 0;
        topComposite.setLayout(topLayout);
        topComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // modes
        createModeGroup(topComposite);

        // visibility
        createVisibilityGroup(topComposite);

        // filter
        createFilterGroup(topComposite);

        // -----------------------------------
        // bottom composite
        // -----------------------------------
        Composite botComposite = new Composite(basePanel, SWT.NONE);
        GridLayout botLayout = new GridLayout();
        botLayout.numColumns   = 2;
        botLayout.marginWidth  = 0;
        botLayout.marginHeight = 0;
        botLayout.makeColumnsEqualWidth = false;
        botComposite.setLayout(botLayout);
        botComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // -> left side

        // options
        createOptionsGroup(botComposite);

        // -> right side
        Composite rightComposite = new Composite(botComposite, SWT.NONE);
        GridLayout rightLayout = new GridLayout(2, false);
        rightLayout.marginWidth  = 0;
        rightLayout.marginHeight = 0;
        rightComposite.setLayout(rightLayout);
        rightComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

        // header
        createHeaderGroup(rightComposite);

        // tag order
        createTagOrderGroup(rightComposite);

        // package javadoc
        createPackageDocGroup(rightComposite);

        // -----------------------------------

        return basePanel;
    }

    protected void createModeGroup(Composite parent) {
        Group modeGroup = new Group(parent, SWT.NONE);
        GridLayout modeLayout = new GridLayout();
        modeLayout.marginTop       = 12;
        modeLayout.verticalSpacing = 20;
        modeGroup.setLayout(modeLayout);
        modeGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        modeGroup.setText(Constants.LABEL_MODE);

        completeButton = new Button(modeGroup, SWT.RADIO);
        completeButton.setText(Constants.LABEL_MODE_COMPLETE);

        keepButton = new Button(modeGroup, SWT.RADIO);
        keepButton.setText(Constants.LABEL_MODE_KEEP);

        replaceButton = new Button(modeGroup, SWT.RADIO);
        replaceButton.setText(Constants.LABEL_MODE_REPLACE);
    }

    protected void createVisibilityGroup(Composite parent) {
        Group visibilityGroup = new Group(parent, SWT.NONE);
        GridLayout visibilityLayout = new GridLayout();
        visibilityLayout.marginTop       = 7;
        visibilityLayout.verticalSpacing = 15;
        visibilityGroup.setLayout(visibilityLayout);
        visibilityGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        visibilityGroup.setText(Constants.LABEL_VISIBILITY);

        publicButton = new Button(visibilityGroup, SWT.CHECK);
        publicButton.setText(Constants.LABEL_VISIBILITY_PUBLIC);

        packageButton = new Button(visibilityGroup, SWT.CHECK);
        packageButton.setText(Constants.LABEL_VISIBILITY_PACKAGE);

        protectedButton = new Button(visibilityGroup, SWT.CHECK);
        protectedButton.setText(Constants.LABEL_VISIBILITY_PROTECTED);

        privateButton = new Button(visibilityGroup, SWT.CHECK);
        privateButton.setText(Constants.LABEL_VISIBILITY_PRIVATE);
    }

    protected void createFilterGroup(Composite parent) {
        Group filterGroup = new Group(parent, SWT.NONE);
        filterGroup.setText(Constants.LABEL_FILTER);
        filterGroup.setLayout(new GridLayout());
        filterGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        filterTypesButton = new Button(filterGroup, SWT.CHECK);
        filterTypesButton.setText(Constants.LABEL_FILTER_TYPES);

        filterFieldsButton = new Button(filterGroup, SWT.CHECK);
        filterFieldsButton.setText(Constants.LABEL_FILTER_FIELDS);

        filterMethodsButton = new Button(filterGroup, SWT.CHECK);
        filterMethodsButton.setText(Constants.LABEL_FILTER_METHODS);
        filterMethodsButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                updateButtonStates();
            }
        });

        filterGetterSetterButton = new Button(filterGroup, SWT.CHECK);
        filterGetterSetterButton.setText(Constants.LABEL_FILTER_GETSET);
        filterGetterSetterButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (filterGetterSetterButton.getSelection()) {
                    filterExcludeGetterSetterButton.setSelection(false);
                }
            }
        });

        GridData getterGridData = new GridData();
        getterGridData.horizontalIndent = 18;
        filterGetterSetterButton.setLayoutData(getterGridData);

        filterExcludeGetterSetterButton = new Button(filterGroup, SWT.CHECK);
        filterExcludeGetterSetterButton.setText(Constants.LABEL_FILTER_EXCLGETSET);
        filterExcludeGetterSetterButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                if (filterExcludeGetterSetterButton.getSelection()) {
                    filterGetterSetterButton.setSelection(false);
                }
            }
        });

        GridData exclGetterGridData = new GridData();
        exclGetterGridData.horizontalIndent = 18;
        filterExcludeGetterSetterButton.setLayoutData(exclGetterGridData);


        filterExcludeOverridingButton = new Button(filterGroup, SWT.CHECK);
        filterExcludeOverridingButton.setText(Constants.LABEL_FILTER_EXCLOVERRID);

        GridData exclOverridGridData = new GridData();
        exclOverridGridData.horizontalIndent = 18;
        filterExcludeOverridingButton.setLayoutData(exclOverridGridData);
    }

    protected void createOptionsGroup(Composite parent) {
        Group optionsGroup = new Group(parent, SWT.NONE);
        optionsGroup.setText(Constants.LABEL_OPTIONS);
        GridLayout optionsLayout = new GridLayout(2, false);
        optionsLayout.marginTop       = 4;
        optionsLayout.verticalSpacing = 8;
        optionsGroup.setLayout(optionsLayout);
        optionsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        todoButton = new Button(optionsGroup, SWT.CHECK);
        todoButton.setText(Constants.LABEL_ADD_TODO);
        todoButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));

        dummyDocButton = new Button(optionsGroup, SWT.CHECK);
        dummyDocButton.setText(Constants.LABEL_DUMMY_DOC);
        dummyDocButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));

        singleLineButton = new Button(optionsGroup, SWT.CHECK);
        singleLineButton.setText(Constants.LABEL_SINGLE_LINE);
        singleLineButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));

        useFormatterButton = new Button(optionsGroup, SWT.CHECK);
        useFormatterButton.setText(Constants.LABEL_USE_FORMATTER);
        useFormatterButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));

        getSetFromFieldButton = new Button(optionsGroup, SWT.CHECK);
        getSetFromFieldButton.setText(Constants.LABEL_GET_SET_FROM_FIELD);
        getSetFromFieldButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                updateButtonStates();
            }
        });

        getSetFromFieldEditButton = new Button(optionsGroup, SWT.PUSH);
        getSetFromFieldEditButton.setText(Constants.LABEL_GET_SET_FROM_FIELD_EDIT);
        getSetFromFieldEditButton.setLayoutData(new GridData(55, SWT.DEFAULT));
        getSetFromFieldEditButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                editGetSetFromFieldPrefix();
            }
        });

        getSetFromFieldFirstButton = new Button(optionsGroup, SWT.CHECK);
        getSetFromFieldFirstButton.setText(Constants.LABEL_GET_SET_FROM_FIELD_FIRST);

        GridData getterFirstGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
        getterFirstGridData.horizontalIndent = 18;
        getterFirstGridData.verticalIndent = -3;
        getSetFromFieldFirstButton.setLayoutData(getterFirstGridData);

        getSetFromFieldReplaceButton = new Button(optionsGroup, SWT.CHECK);
        getSetFromFieldReplaceButton.setText(Constants.LABEL_GET_SET_FROM_FIELD_REPLACE);

        GridData getterReplaceGridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
        getterReplaceGridData.horizontalIndent = 18;
        getterReplaceGridData.verticalIndent = -3;
        getSetFromFieldReplaceButton.setLayoutData(getterReplaceGridData);

        includeSubPackagesButton = new Button(optionsGroup, SWT.CHECK);
        includeSubPackagesButton.setText(Constants.LABEL_INCL_SUBPACKAGES);
        includeSubPackagesButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
    }

    protected void createHeaderGroup(final Composite parent) {
        final Group headerGroup = new Group(parent, SWT.NONE);
        headerGroup.setText(Constants.LABEL_HEADER);

        final GridLayout headerLayout = new GridLayout(2, false);
        headerLayout.marginHeight = 5;
        headerGroup.setLayout(headerLayout);

        final GridData headerLayoutData = new GridData(GridData.FILL_BOTH);
        headerLayoutData.horizontalSpan = 2;
        headerGroup.setLayoutData(headerLayoutData);

        addHeaderButton = new Button(headerGroup, SWT.CHECK);
        addHeaderButton.setText(Constants.LABEL_ADD_HEADER);
        addHeaderButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                updateButtonStates();
            }
        });

        editHeaderButton = new Button(headerGroup, SWT.PUSH);
        editHeaderButton.setText(Constants.LABEL_EDIT_HEADER);
        editHeaderButton.setLayoutData(new GridData(55, SWT.DEFAULT));
        editHeaderButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                editHeaderText();
            }
        });

        replaceHeaderButton = new Button(headerGroup, SWT.CHECK);
        replaceHeaderButton.setText(Constants.LABEL_REPLACE_HEADER);

        final GridData gridData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
        gridData.horizontalIndent = 18;
        replaceHeaderButton.setLayoutData(gridData);

        multiHeaderButton = new Button(headerGroup, SWT.CHECK);
        multiHeaderButton.setText(Constants.LABEL_MULTI_HEADER);

        final GridData gridData2 = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
        gridData2.horizontalIndent = 18;
        multiHeaderButton.setLayoutData(gridData2);

        final Link saveActionLink = new Link(headerGroup, SWT.NONE);
        saveActionLink.setText(Constants.LABEL_SAVE_ACTION);
        saveActionLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                final IPreferencePageContainer ppContainer = preferencePage.getContainer();
                if (ppContainer instanceof IWorkbenchPreferenceContainer) {
                    final IWorkbenchPreferenceContainer wpContainer = (IWorkbenchPreferenceContainer)ppContainer;
                    wpContainer.openPage(getPrefOrPropPageId(e.text, isOnPropertyPage()), null);
                } else {
                    PreferencesUtil.createPreferenceDialogOn(preferencePage.getShell(),
                            getPrefOrPropPageId(e.text, false), null, null);
                }
            }
        });
        saveActionLink.setEnabled(isOnPreferenceOrPropertyPage());

        final GridData gridData3 = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);
        gridData3.verticalIndent = 7;
        saveActionLink.setLayoutData(gridData3);
    }

    private boolean isOnPreferenceOrPropertyPage() {
        return preferencePage != null;
    }

    private boolean isOnPropertyPage() {
        return preferencePage != null && preferencePage.getElement() != null;
    }

    private String getPrefOrPropPageId(final String linkSelection, final boolean propertyPage) {
        if ("Clean Up".equals(linkSelection)) {
            return propertyPage ? Constants.CLEANUP_PROP_PAGE_ID : Constants.CLEANUP_PREF_PAGE_ID;
        }
        return propertyPage ? Constants.SAVEPART_PROP_PAGE_ID : Constants.SAVEPART_PREF_PAGE_ID;
    }

    protected void createPackageDocGroup(Composite parent) {
        Group pkgdocGroup = new Group(parent, SWT.NONE);
        pkgdocGroup.setText(Constants.LABEL_PKGDOC);
        GridLayout pkgdocLayout = new GridLayout();
        pkgdocLayout.marginHeight = 5;
        pkgdocGroup.setLayout(pkgdocLayout);
        pkgdocGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        editPackageDocButton = new Button(pkgdocGroup, SWT.PUSH);
        GridData gridData = new GridData();
        gridData.verticalIndent = 5;
        editPackageDocButton.setLayoutData(gridData);
        editPackageDocButton.setText(Constants.LABEL_EDIT_PKGDOC);
        editPackageDocButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                editPackageJavadoc();
            }
        });

        usePackageInfoButton = new Button(pkgdocGroup, SWT.CHECK);
        GridData gridData2 = new GridData();
        gridData2.verticalIndent = 7;
        usePackageInfoButton.setLayoutData(gridData2);
        usePackageInfoButton.setText(Constants.LABEL_PKGDOC_USEINFO);
    }

    protected void createTagOrderGroup(final Composite parent) {
        final Group tagOrderGroup = new Group(parent, SWT.NONE);
        tagOrderGroup.setText(Constants.LABEL_TAG_ORDER);

        final GridLayout tagOrderLayout = new GridLayout();
        tagOrderLayout.marginHeight = 5;
        tagOrderGroup.setLayout(tagOrderLayout);
        tagOrderGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

        final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.verticalIndent = 5;
        editTagOrderButton = new Button(tagOrderGroup, SWT.PUSH);
        editTagOrderButton.setLayoutData(gridData);
        editTagOrderButton.setText(Constants.LABEL_EDIT_TAG_ORDER);
        editTagOrderButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                editTagOrder();
            }
        });
    }

    protected void updateButtonStates() {
        // always enabled since header could be added by contex menu
        // editHeaderButton.setEnabled(addHeaderButton.getSelection());
        // replaceHeaderButton.setEnabled(addHeaderButton.getSelection());

        filterGetterSetterButton.setEnabled(filterMethodsButton.getSelection());
        filterExcludeGetterSetterButton.setEnabled(filterMethodsButton.getSelection());
        filterExcludeOverridingButton.setEnabled(filterMethodsButton.getSelection());

        getSetFromFieldEditButton.setEnabled(getSetFromFieldButton.getSelection());
        getSetFromFieldFirstButton.setEnabled(getSetFromFieldButton.getSelection());
        getSetFromFieldReplaceButton.setEnabled(getSetFromFieldButton.getSelection());
    }

    protected void editHeaderText() {
        final EditHeaderDialog headerDialog = new EditHeaderDialog(basePanel.getShell(), headerText, properties);
        if (headerDialog.open() == Window.OK) {
            headerText = headerDialog.getText();
        }
    }

    protected void editPackageJavadoc() {
        final String pkgDocText = usePackageInfoButton.getSelection() ? packageInfoText : packageDocText;
        final EditPackageJavadocDialog packageDialog = new EditPackageJavadocDialog(basePanel.getShell(), pkgDocText,
                properties, usePackageInfoButton.getSelection());
        if (packageDialog.open() == Window.OK) {
            if (usePackageInfoButton.getSelection()) {
                packageInfoText = packageDialog.getText();
            }
            else {
                packageDocText = packageDialog.getText();
            }
        }
    }

    protected void editTagOrder() {
        final TagOrderDialog tagOrderDialog = new TagOrderDialog(basePanel.getShell(), new ArrayList<String>(tagOrder));
        if (tagOrderDialog.open() == Window.OK) {
            tagOrder.clear();
            tagOrder.addAll(tagOrderDialog.getTagOrder());
        }
    }

    protected void editGetSetFromFieldPrefix() {
        final GetSetFromFieldReplacementsDialog dialog = new GetSetFromFieldReplacementsDialog(
                basePanel.getShell(), getSetFromFieldReplacements);
        if (dialog.open() == Window.OK) {
            getSetFromFieldReplacements.clear();
            getSetFromFieldReplacements.addAll(dialog.getElements());
        }
    }
}
