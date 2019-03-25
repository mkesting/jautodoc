/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.Collection;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sf.jautodoc.ui.EditElementsDialog;


/**
 * Editor dialog for GetSetFromFieldReplacements.
 */
public class GetSetFromFieldReplacementsDialog extends EditElementsDialog<GetSetFromFieldReplacement> {

    private final String LABEL_FIELD_PREFIX = "Field";
    private final String LABEL_RETURN_PREFIX = "Getter Return";
    private final String LABEL_PARAMETER_PREFIX = "Setter Parameter";

    public GetSetFromFieldReplacementsDialog(final Shell parent, final Collection<GetSetFromFieldReplacement> elements) {
        super(parent, elements);
    }

    @Override
    protected String getEditHint() {
        return getDialogTitle();
    }

    @Override
    protected String getDialogTitle() {
        return "Edit Comment Prefixes";
    }

    @Override
    protected int getWidth() {
        return 420;
    }

    @Override
    protected int getHeight() {
        return 260;
    }

    @Override
    protected int[] getColumnWeights() {
        return new int[] {28, 36, 36};
    }

    @Override
    protected String[] getAttributeLabels() {
        return new String[] {LABEL_FIELD_PREFIX, LABEL_RETURN_PREFIX, LABEL_PARAMETER_PREFIX};
    }

    @Override
    protected String verifyText(final String attributeLabel, final char character) {
        return null; // nothing to verify -> return null
    }

    @Override
    protected boolean isValid(final Map<String, Text> textFields) {
        return !textFields.get(LABEL_FIELD_PREFIX).getText().trim().isEmpty()
                || !textFields.get(LABEL_RETURN_PREFIX).getText().trim().isEmpty()
                || !textFields.get(LABEL_PARAMETER_PREFIX).getText().trim().isEmpty();
    }

    @Override
    protected void showElement(final GetSetFromFieldReplacement element, final Map<String, Text> textFields) {
        textFields.get(LABEL_FIELD_PREFIX).setText(element.getFieldPrefix());
        textFields.get(LABEL_RETURN_PREFIX).setText(element.getReturnPrefix());
        textFields.get(LABEL_PARAMETER_PREFIX).setText(element.getParameterPrefix());
    }

    @Override
    protected GetSetFromFieldReplacement readElement(final Map<String, Text> textFields) {
        final GetSetFromFieldReplacement element = new GetSetFromFieldReplacement(
                textFields.get(LABEL_FIELD_PREFIX).getText().trim(),
                textFields.get(LABEL_RETURN_PREFIX).getText().trim(),
                textFields.get(LABEL_PARAMETER_PREFIX).getText().trim());
        return element;
    }
}
