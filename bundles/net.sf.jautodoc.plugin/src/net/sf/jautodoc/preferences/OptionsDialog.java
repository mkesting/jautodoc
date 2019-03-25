/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for maintaining JAutodoc options. It's used to temporary
 * override Workspace and Project specific setting for a single
 * compilation unit.
 */
public class OptionsDialog extends Dialog {

    public static final int PREVIEW_ID = IDialogConstants.CLIENT_ID;

    private Button globalSettingsButton;
    private Control optionPanel;
    private OptionsBlock ob;
    private ControlEnableState enableState;

    private String title;
    private Configuration config;
    private ICompilationUnit compUnit;


    /**
     * Instantiates a new options dialog.
     *
     * @param parent the parent shell
     * @param title the title
     * @param compUnit the current compilation unit
     */
    public OptionsDialog(Shell parent, String title, ICompilationUnit compUnit) {
        super(parent);

        this.title = title;
        this.compUnit = compUnit;

        config = ConfigurationManager.getCachedConfiguration(compUnit);
        if (config == null) {
            config = ConfigurationManager.getConfiguration(compUnit, false);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite basePanel = new Composite(parent, SWT.NONE);
        basePanel.setLayout(new GridLayout());

        Composite upperPanel = new Composite(basePanel, SWT.NONE);
        upperPanel.setLayout(new GridLayout());
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        upperPanel.setLayoutData(gridData);

        globalSettingsButton = new Button(upperPanel, SWT.CHECK);
        globalSettingsButton.setText("Use project/workspace settings");
        globalSettingsButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        globalSettingsButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                updateControlStates();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                updateControlStates();
            }
        });

        Label horizontalLine= new Label(upperPanel, SWT.SEPARATOR | SWT.HORIZONTAL);
        horizontalLine.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        ob = new OptionsBlock();
        optionPanel = ob.createContents(basePanel);

        updateControls();

        Dialog.applyDialogFont(basePanel);
        return basePanel;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, PREVIEW_ID, "Preview", false);
        super.createButtonsForButtonBar(parent);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (PREVIEW_ID == buttonId) {
            previewPressed();
        } else {
            super.buttonPressed(buttonId);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(title);
    }

    /**
     * Initialize control values.
     */
    private void updateControls() {
        ob.completeButton.setSelection(config.isCompleteExistingJavadoc());
        ob.keepButton.setSelection(config.isKeepExistingJavadoc());
        ob.replaceButton.setSelection(config.isReplaceExistingJavadoc());

        ob.publicButton.setSelection(config.isIncludePublic());
        ob.protectedButton.setSelection(config.isIncludeProtected());
        ob.packageButton.setSelection(config.isIncludePackage());
        ob.privateButton.setSelection(config.isIncludePrivate());

        ob.filterTypesButton.setSelection(config.isIncludeTypes());
        ob.filterFieldsButton.setSelection(config.isIncludeFields());
        ob.filterMethodsButton.setSelection(config.isIncludeMethods());
        ob.filterGetterSetterButton.setSelection(config.isGetterSetterOnly());
        ob.filterExcludeGetterSetterButton.setSelection(config.isExcludeGetterSetter());
        ob.filterExcludeOverridingButton.setSelection(config.isExcludeOverriding());

        ob.todoButton.setSelection(config.isAddTodoForAutodoc());
        ob.dummyDocButton.setSelection(config.isCreateDummyComment());
        ob.singleLineButton.setSelection(config.isSingleLineComment());
        ob.useFormatterButton.setSelection(config.isUseEclipseFormatter());
        ob.getSetFromFieldButton.setSelection(config.isGetterSetterFromField());

        ob.getSetFromFieldFirstButton.setSelection(config.isGetterSetterFromFieldFirst());
        ob.getSetFromFieldReplaceButton.setSelection(config.isGetterSetterFromFieldReplace());

        ob.addHeaderButton.setSelection(config.isAddHeader());
        ob.replaceHeaderButton.setSelection(config.isReplaceHeader());
        ob.multiHeaderButton.setSelection(config.isMultiCommentHeader());
        ob.usePackageInfoButton.setSelection(config.isUsePackageInfo());

        ob.headerText      = config.getHeaderText();
        ob.packageDocText  = config.getPackageDocText();
        ob.packageInfoText = config.getPackageInfoText();

        ob.tagOrder = new ArrayList<String>(config.getTagOrder());
        ob.properties = new HashMap<String, String>(config.getProperties());
        ob.getSetFromFieldReplacements = new TreeSet<GetSetFromFieldReplacement>(config.getGetSetFromFieldReplacements());

        ob.updateButtonStates();

        ob.editPackageDocButton.setEnabled(false);
        ob.usePackageInfoButton.setEnabled(false);
    }

    protected void previewPressed() {
        applyConfig();
        setReturnCode(PREVIEW_ID);
        close();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
        applyConfig();
        super.okPressed();
    }

    private void applyConfig() {
        if (globalSettingsButton.getSelection()) {
            // use global settings -> remove cached version
            ConfigurationManager.removeCachedConfiguration(compUnit);
        }
        else {
            config.setCompleteExistingJavadoc(ob.completeButton.getSelection());
            config.setKeepExistingJavadoc(ob.keepButton.getSelection());
            config.setReplaceExistingJavadoc(ob.replaceButton.getSelection());

            config.setVisibilityPublic(ob.publicButton.getSelection());
            config.setVisibilityProtected(ob.protectedButton.getSelection());
            config.setVisibilityPackage(ob.packageButton.getSelection());
            config.setVisibilityPrivate(ob.privateButton.getSelection());

            config.setCommentTypes(ob.filterTypesButton.getSelection());
            config.setCommentFields(ob.filterFieldsButton.getSelection());
            config.setCommentMethods(ob.filterMethodsButton.getSelection());
            config.setGetterSetterOnly(ob.filterGetterSetterButton.getSelection());
            config.setExcludeGetterSetter(ob.filterExcludeGetterSetterButton.getSelection());
            config.setExcludeOverriding(ob.filterExcludeOverridingButton.getSelection());

            config.setAddTodoForAutodoc(ob.todoButton.getSelection());
            config.setCreateDummyComment(ob.dummyDocButton.getSelection());
            config.setSingleLineComment(ob.singleLineButton.getSelection());
            config.setUseEclipseFormatter(ob.useFormatterButton.getSelection());
            config.setGetterSetterFromField(ob.getSetFromFieldButton.getSelection());
            config.setIncludeSubPackages(ob.includeSubPackagesButton.getSelection());

            config.setGetterSetterFromFieldFirst(ob.getSetFromFieldFirstButton.getSelection());
            config.setGetterSetterFromFieldReplace(ob.getSetFromFieldReplaceButton.getSelection());

            config.setGetSetFromFieldReplacements(ob.getSetFromFieldReplacements);

            config.setAddHeader(ob.addHeaderButton.getSelection());
            config.setReplaceHeader(ob.replaceHeaderButton.getSelection());
            config.setMultiCommentHeader(ob.multiHeaderButton.getSelection());
            config.setUsePackageInfo(ob.usePackageInfoButton.getSelection());

            config.setHeaderText(ob.headerText);
            config.setPackageDocText(ob.packageDocText);
            config.setPackageInfoText(ob.packageInfoText);

            config.setTagOrder(ob.tagOrder);
            config.setProperties(ob.properties);
            config.setGetSetFromFieldReplacements(ob.getSetFromFieldReplacements);

            ConfigurationManager.cacheConfiguration(compUnit, config);
        }
    }

    /**
     * Disable controls, if global settings should be used.
     */
    private void updateControlStates() {
        if (!globalSettingsButton.getSelection()) {
            if (enableState != null) {
                enableState.restore();
                enableState = null;
            }
        } else {
            if (enableState == null) {
                enableState = ControlEnableState.disable(optionPanel);
            }
        }
    }
}
