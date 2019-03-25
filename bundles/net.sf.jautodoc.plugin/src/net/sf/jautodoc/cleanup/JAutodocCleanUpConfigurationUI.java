/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.cleanup;

import net.sf.jautodoc.preferences.ConfigurationManager;
import net.sf.jautodoc.preferences.Constants;

import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.ICleanUpConfigurationUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * The JAutodoc cleanup configuration UI.
 */
public class JAutodocCleanUpConfigurationUI implements ICleanUpConfigurationUI {

    private Button addHeaderButton;
    private Button replaceHeaderButton;
    private Label warnReplaceHeaderImage;
    private Label warnReplaceHeaderLabel;

    private CleanUpOptions options;


    /** {@inheritDoc} */
    @Override
    public void setOptions(final CleanUpOptions options) {
        this.options = options;
    }

    /** {@inheritDoc} */
    @Override
    public Composite createContents(final Composite parent) {
        return createHeaderGroup(parent);
    }

    /** {@inheritDoc} */
    @Override
    public int getCleanUpCount() {
        return 1;
    }

    /** {@inheritDoc} */
    @Override
    public int getSelectedCleanUpCount() {
        return options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION) ? 1 : 0;
    }

    /** {@inheritDoc} */
    @Override
    public String getPreview() {
        return options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION) ?
                ConfigurationManager.getCurrentConfiguration().getHeaderText() : "";
    }

    private Composite createHeaderGroup(final Composite parent) {
        final Group headerGroup = new Group(parent, SWT.NONE);
        headerGroup.setFont(parent.getFont());
        headerGroup.setText(Constants.LABEL_HEADER);

        final GridLayout headerLayout = new GridLayout(2, false);
        headerLayout.marginHeight = 10;
        headerGroup.setLayout(headerLayout);
        headerGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        addHeaderButton = new Button(headerGroup, SWT.CHECK);
        addHeaderButton.setFont(parent.getFont());
        addHeaderButton.setText(Constants.LABEL_ADD_HEADER);
        addHeaderButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                options.setOption(Constants.CLEANUP_ADD_HEADER_OPTION,
                        addHeaderButton.getSelection() ? CleanUpOptions.TRUE : CleanUpOptions.FALSE);
                updateWidgetStates();
            }
        });

        final GridData addBtnGridData = new GridData();
        addBtnGridData.horizontalSpan = 2;
        addHeaderButton.setLayoutData(addBtnGridData);

        replaceHeaderButton = new Button(headerGroup, SWT.CHECK);
        replaceHeaderButton.setFont(parent.getFont());
        replaceHeaderButton.setText(Constants.LABEL_REPLACE_HEADER);
        replaceHeaderButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                options.setOption(Constants.CLEANUP_REP_HEADER_OPTION,
                        replaceHeaderButton.getSelection() ? CleanUpOptions.TRUE : CleanUpOptions.FALSE);
                updateWidgetStates();
            }
        });

        final GridData repBtnGridData = new GridData();
        repBtnGridData.verticalIndent = 7;
        repBtnGridData.horizontalSpan = 2;
        repBtnGridData.horizontalIndent = 18;
        replaceHeaderButton.setLayoutData(repBtnGridData);

        warnReplaceHeaderImage = new Label(headerGroup, SWT.NONE);
        warnReplaceHeaderImage.setImage(Dialog.getImage(Dialog.DLG_IMG_MESSAGE_WARNING));

        final GridData warnImageGridData = new GridData();
        warnImageGridData.horizontalIndent = 18;
        warnReplaceHeaderImage.setLayoutData(warnImageGridData);

        warnReplaceHeaderLabel = new Label(headerGroup, SWT.WRAP);
        warnReplaceHeaderLabel.setFont(parent.getFont());
        warnReplaceHeaderLabel.setText(Constants.CLEANUP_REP_HEADER_WARN_LABEL);

        final GridData warnLabelGridData = new GridData(GridData.FILL_HORIZONTAL);
        warnReplaceHeaderLabel.setLayoutData(warnLabelGridData);

        final Label hintLabel = new Label(headerGroup, SWT.WRAP);
        hintLabel.setFont(parent.getFont());
        hintLabel.setText(Constants.CLEANUP_ADD_HEADER_HINT_LABEL);

        final GridData hintLabelGridData = new GridData(GridData.FILL_HORIZONTAL);
        hintLabelGridData.verticalIndent = 15;
        hintLabelGridData.horizontalSpan = 2;
        hintLabel.setLayoutData(hintLabelGridData);

        updateWidgetStates();

        return headerGroup;
    }

    private void updateWidgetStates() {
        addHeaderButton.setSelection(options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION));

        replaceHeaderButton.setSelection(options.isEnabled(Constants.CLEANUP_REP_HEADER_OPTION));
        replaceHeaderButton.setEnabled(options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION));

        warnReplaceHeaderImage.setEnabled(options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION)
                && options.isEnabled(Constants.CLEANUP_REP_HEADER_OPTION));
        warnReplaceHeaderLabel.setEnabled(options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION)
                && options.isEnabled(Constants.CLEANUP_REP_HEADER_OPTION));
    }
}
