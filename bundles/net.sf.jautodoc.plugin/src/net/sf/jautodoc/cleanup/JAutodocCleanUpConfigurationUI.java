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
    private Button cleanupJavadocButton;
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
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        createHeaderGroup(composite);
        createJavadocGroup(composite);

        updateWidgetStates();

        return composite;
    }

    /** {@inheritDoc} */
    @Override
    public int getCleanUpCount() {
        return 2;
    }

    /** {@inheritDoc} */
    @Override
    public int getSelectedCleanUpCount() {
        int count = options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION) ? 1 : 0;
        return options.isEnabled(Constants.CLEANUP_JAVADOC_OPTION) ? ++count : count;
    }

    /** {@inheritDoc} */
    @Override
    public String getPreview() {
        StringBuilder buffer = new StringBuilder();

        buffer.append(options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION)
                ? ConfigurationManager.getCurrentConfiguration().getHeaderText() + Constants.LINE_SEPARATOR
                : "");

        buffer.append(options.isEnabled(Constants.CLEANUP_JAVADOC_OPTION)
                ? Constants.CLEANUP_PREVIEW_JAVADOC_ENABLED
                : Constants.CLEANUP_PREVIEW_JAVADOC_DISABLED);

        return buffer.toString();
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

        return headerGroup;
    }

    private Composite createJavadocGroup(final Composite parent) {
        final Group javadocGroup = new Group(parent, SWT.NONE);
        javadocGroup.setFont(parent.getFont());
        javadocGroup.setText("Javadoc");

        final GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 10;
        javadocGroup.setLayout(layout);
        javadocGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        cleanupJavadocButton = new Button(javadocGroup, SWT.CHECK);
        cleanupJavadocButton.setFont(parent.getFont());
        cleanupJavadocButton.setText("Cleanup Javadoc");
        cleanupJavadocButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                options.setOption(Constants.CLEANUP_JAVADOC_OPTION,
                        cleanupJavadocButton.getSelection() ? CleanUpOptions.TRUE : CleanUpOptions.FALSE);
                updateWidgetStates();
            }
        });

        return javadocGroup;
    }

    private void updateWidgetStates() {
        addHeaderButton.setSelection(options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION));

        replaceHeaderButton.setSelection(options.isEnabled(Constants.CLEANUP_REP_HEADER_OPTION));
        replaceHeaderButton.setEnabled(options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION));

        cleanupJavadocButton.setSelection(options.isEnabled(Constants.CLEANUP_JAVADOC_OPTION));

        warnReplaceHeaderImage.setEnabled(options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION)
                && options.isEnabled(Constants.CLEANUP_REP_HEADER_OPTION));
        warnReplaceHeaderLabel.setEnabled(options.isEnabled(Constants.CLEANUP_ADD_HEADER_OPTION)
                && options.isEnabled(Constants.CLEANUP_REP_HEADER_OPTION));
    }
}
