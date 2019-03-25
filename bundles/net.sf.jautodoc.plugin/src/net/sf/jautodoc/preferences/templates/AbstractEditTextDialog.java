/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.templates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import net.sf.jautodoc.IApplicationContext;
import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.templates.ITemplateManager;
import net.sf.jautodoc.templates.LogEntry;
import net.sf.jautodoc.templates.LogListener;
import net.sf.jautodoc.templates.TemplateEngineLogger;
import net.sf.jautodoc.templates.ValidationException;
import net.sf.jautodoc.templates.contentassist.ITemplateContentAssistant;
import net.sf.jautodoc.templates.contentassist.TemplateAssistProcessor;
import net.sf.jautodoc.templates.rules.IRulesStrategy;
import net.sf.jautodoc.templates.rules.TemplateCodeScanner;
import net.sf.jautodoc.templates.viewer.TemplateViewer;
import net.sf.jautodoc.templates.viewer.TemplateViewerConfiguration;
import net.sf.jautodoc.utils.Utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


/**
 * Abstract base class of dialogs for template editing, providing
 * a single text area and import/export functionality.
 */
public abstract class AbstractEditTextDialog extends TitleAreaDialog implements
        LogListener {
    private static final int IMPORT_ID     = IDialogConstants.INTERNAL_ID + 1;
    private static final int EXPORT_ID     = IDialogConstants.INTERNAL_ID + 2;
    private static final int PROPERTIES_ID = IDialogConstants.INTERNAL_ID + 3;

    protected Map<String, String> properties;

    private String text;
    private TemplateViewer templateViewer;
    private ITemplateManager templateManager;

    private LogEntry logEntry;


    /**
     * Instantiates a new dialog for editing a template text.
     *
     * @param parentShell the parent shell
     * @param text the text to edit
     * @param properties the properties to use
     */
    protected AbstractEditTextDialog(final Shell parentShell, final String text, final Map<String, String> properties) {
        super(parentShell);

        setShellStyle(getShellStyle() | SWT.MAX | SWT.RESIZE);
        setHelpAvailable(false);

        TemplateEngineLogger.addLogListener(this);

        templateManager = JAutodocPlugin.getContext().getTemplateManager();
        this.text = text == null ? "" : text;
        this.properties = properties;
    }

    /**
     * Gets the edited text.
     *
     * @return the edited text
     */
    public String getText() {
        return text;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        Composite base = new Composite(parent, SWT.NONE);

        base.setLayout(new GridLayout());
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = 250;
        gridData.widthHint  = 550;
        base.setLayoutData(gridData);

        templateViewer = new TemplateViewer(base, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        templateViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

        configureTemplateViewer();

        templateViewer.setDocument(new Document(text));
        setInitialCursorPosition();

        initListener();

        setTitle(getTitle());
        setMessage(getHint());
        setTitleImage(ResourceManager.getImage(ResourceManager.JAVADOC_IMAGE_FILE));

        return base;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IMPORT_ID,     "Import...", false);
        createButton(parent, EXPORT_ID,     "Export...", false);
        createButton(parent, PROPERTIES_ID, "Properties...", false);

        ((GridLayout) parent.getLayout()).numColumns++;
        Label filler = new Label(parent, SWT.NONE);
        filler.setLayoutData(new GridData());

        // create OK and Cancel buttons by default
        super.createButtonsForButtonBar(parent);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
     */
    protected void buttonPressed(int buttonId) {
        if (buttonId == IMPORT_ID) {
            importText();
            return;
        }

        if (buttonId == EXPORT_ID) {
            exportText();
            return;
        }

        if (buttonId == PROPERTIES_ID) {
            showPropertiesDialog();
            return;
        }
        super.buttonPressed(buttonId);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
        String templateText = templateViewer.getDocument().get().trim();
        if (validate(templateText)) {
            text = templateText;
            super.okPressed();
        }
    }

    private void configureTemplateViewer() {
        IApplicationContext ctx = JAutodocPlugin.getContext();

        // edit template viewer
        ITextHover editTextHover               = ctx.getTemplateTextHover(properties);
        IRulesStrategy[] editRuleStrategies    = ctx.getTemplateRuleStrategies();
        IAutoEditStrategy[] autoEditStrategies = ctx.getTemplateAutoEditStrategies();
        ITemplateContentAssistant[] assistants = getTemplateContentAssistants();

        SourceViewerConfiguration configuration = new TemplateViewerConfiguration(
                new TemplateCodeScanner(editRuleStrategies),
                editTextHover,
                autoEditStrategies,
                new TemplateAssistProcessor(assistants));
        templateViewer.configure(configuration);
    }

    private void initListener() {
        getShell().addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {
                TemplateEngineLogger.removeLogListener(AbstractEditTextDialog.this);
            }
        });

        templateViewer.prependVerifyKeyListener(new VerifyKeyListener() {
            public void verifyKey(VerifyEvent event) {
                verifyTemplateViewerKeyPressed(event);
            }
        });
    }

    private void verifyTemplateViewerKeyPressed(VerifyEvent event) {
        if (!event.doit)
            return;

        // clear messages
        setOK();

        // CTRL pressed?
        if (event.stateMask != SWT.MOD1)
            return;

        switch (event.character) {
            // CTRL-Space
            case ' ':
                templateViewer.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
                event.doit = false;
                break;

            // CTRL-Z
            case 'z' - 'a' + 1:
                templateViewer.doOperation(ITextOperationTarget.UNDO);
                event.doit= false;
                break;

            // CTRL-Y
            case 'y' - 'a' + 1:
                templateViewer.doOperation(ITextOperationTarget.REDO);
                event.doit= false;
                break;

            // CTRL-S
            case 's' - 'a' + 1:
                okPressed();
                event.doit= false;
                break;
        }
    }

    private boolean validate(String templateText) {
        if ("".equals(templateText)) {
            return true;
        }

        if (!validateTemplate(templateText)) {
            return false;
        }

        String evaluatedText = evaluateTemplate(templateText);
        if (evaluatedText == null)  {
            return false;
        }

        return validateEvaluatedText(evaluatedText);
    }

    private boolean validateTemplate(String templateText) {
        String message = "";
        try {
            templateManager.validateTemplate(templateText);
            return true;
        } catch (ValidationException ve) {
            message = ve.getMessage();
            int offset = Utils.getDocumentOffset(templateViewer.getDocument(),
                    ve.getLine(), ve.getColumn());
            if (offset >= 0) {
                templateViewer.getTextWidget().setSelection(offset);
                templateViewer.getTextWidget().forceFocus();
            }
        } catch (Exception e) {
            message = e.getMessage();
        }

        setError("Invalid template: " + message);
        return false;
    }

    private String evaluateTemplate(String templateText) {
        logEntry = null;

        // evaluate the template
        String evaluatedText = null;
        try {
            evaluatedText = templateManager.evaluateTemplate(null,
                    templateText, "Template Text", properties);
        } catch (Exception e) {
            setError(e.getMessage());
            return null;
        }

        // check, if something was logged by the template engine
        if (logEntry == null) {
            return evaluatedText;
        }

        // error/warning occured
        if (logEntry.getSeverity() != IStatus.ERROR) {
            setInfo(logEntry.getMessage());
        } else {
            setError(logEntry.getMessage());
        }

        // try to select the affected position
        int offset = Utils.getDocumentOffset(templateViewer.getDocument(),
                logEntry.getLine(), logEntry.getColumn());
        if (offset >= 0) {
            templateViewer.getTextWidget().setSelection(offset);
            templateViewer.getTextWidget().forceFocus();
        }

        return null;
    }

    protected void setOK() {
        setMessage(getHint());
        setErrorMessage(null);
    }

    protected void setInfo(String message) {
        setMessage(message, IMessageProvider.WARNING);
    }

    protected void setError(String message) {
        setErrorMessage(message);
    }

    protected void setInitialCursorPosition() {
        try {
            IDocument document = templateViewer.getDocument();

            int xlineOffset = getLineOffset() + 1;

            int numLines = document.getNumberOfLines();
            if (numLines < xlineOffset) return;

            int line = numLines - xlineOffset;
            int lineOffset = document.getLineOffset(line);
            int lineLenght = document.getLineLength(line);
            String delim = document.getLineDelimiter(line);

            if (delim == null) {
                templateViewer.getTextWidget().setSelection(
                        lineOffset + lineLenght);
            }
            else {
                templateViewer.getTextWidget().setSelection(
                        lineOffset + lineLenght - delim.length());
            }
        } catch (BadLocationException e) {/* ignore */}
    }

    private void importText() {
        FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
        fileDialog.setText("Import");
        fileDialog.setFilterExtensions(getFilterExtensions());
        String selectedFile = fileDialog.open();
        if (selectedFile == null) {
            return;
        }

        // read file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(selectedFile));

            String delim = TextUtilities.getDefaultLineDelimiter(templateViewer.getDocument());
            StringBuffer buffer = new StringBuffer();

            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + delim);
            }

            templateViewer.setDocument(new Document(buffer.toString()));
            setInitialCursorPosition();
            setOK();
        } catch (IOException e) {
            setError("Could not read file: " + e.getMessage());
            return;
        }
        finally {
            Utils.close(reader);
        }
    }

    private void exportText() {
        FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
        fileDialog.setText("Export");
        fileDialog.setFileName(getExportFileName());
        fileDialog.setFilterExtensions(getFilterExtensions());
        String selectedFile = fileDialog.open();
        if (selectedFile == null) {
            return;
        }

        File file = new File(selectedFile);
        if (file.exists() && !MessageDialog.openQuestion(getShell(),
                "File exists", "File '" + file.getName() + "' already exists. Replace?")) {
            return;
        }

        // write file
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(templateViewer.getDocument().get());
        } catch (IOException e) {
            setError("Could not write file: " + e.getMessage());
        }
        finally {
            Utils.close(writer);
        }
    }

    private void showPropertiesDialog() {
        EditPropertiesDialog dialog = new EditPropertiesDialog(getShell(), properties);
        dialog.open();
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.templates.LogListener#messageLogged(net.sf.jautodoc.templates.LogEntry)
     */
    public void messageLogged(LogEntry logEntry) {
        this.logEntry = logEntry;
    }

    protected abstract ITemplateContentAssistant[] getTemplateContentAssistants();

    /**
     * Validate the text, that's alreday evaluated by the
     * template manager.
     *
     * @param evaluatedText the evaluated text
     *
     * @return true, if text is valid
     */
    protected abstract boolean validateEvaluatedText(String evaluatedText);

    /**
     * Gets the title of the dialog.
     *
     * @return the title
     */
    protected abstract String getTitle();

    /**
     * Gets the hint for the dialog.
     *
     * @return the hint
     */
    protected abstract String getHint();

    /**
     * Gets the default export file name.
     *
     * @return the default export file name
     */
    protected abstract String getExportFileName();

    /**
     * Gets the export/import filter extensions.
     *
     * @return the filter extensions
     */
    protected abstract String[] getFilterExtensions();

    /**
     * Gets the line offset for the initial cursor position
     * starting from end of document.
     *
     * @return the line offset
     */
    protected abstract int getLineOffset();
}
