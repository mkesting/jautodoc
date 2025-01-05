/*******************************************************************
 * Copyright (c) 2006 - 2025, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jautodoc.IApplicationContext;
import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.templates.ITemplateManager;
import net.sf.jautodoc.templates.LogEntry;
import net.sf.jautodoc.templates.LogListener;
import net.sf.jautodoc.templates.TemplateEngineLogger;
import net.sf.jautodoc.templates.TemplateEntry;
import net.sf.jautodoc.templates.ValidationException;
import net.sf.jautodoc.templates.contentassist.ITemplateContentAssistant;
import net.sf.jautodoc.templates.contentassist.TemplateAssistProcessor;
import net.sf.jautodoc.templates.replacements.ITemplateReplacementsListener;
import net.sf.jautodoc.templates.replacements.ITemplateReplacementsProvider;
import net.sf.jautodoc.templates.replacements.TemplateReplacementsChangeEvent;
import net.sf.jautodoc.templates.rules.IRulesStrategy;
import net.sf.jautodoc.templates.rules.TemplateCodeScanner;
import net.sf.jautodoc.templates.viewer.TemplateViewerConfiguration;
import net.sf.jautodoc.utils.StringUtils;
import net.sf.jautodoc.utils.Utils;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.compiler.IScanner;
import org.eclipse.jdt.core.compiler.ITerminalSymbols;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for editing Javadoc templates.
 */
public class EditTemplateDialog extends TitleAreaDialog implements
        ITemplateReplacementsProvider, LogListener {

    private EditTemplatePanel view;
    private TemplateEntry entry;
    private String templateName;
    private Pattern pattern;
    private Matcher matcher;
    private Matcher parentMatcher;
    private Map<String, String> properties;
    private Set<ITemplateReplacementsListener> resultListener = new HashSet<ITemplateReplacementsListener>();
    private ITemplateManager templateManager;

    private LogEntry logEntry;


    /**
     * Instantiates a new edit template dialog.
     *
     * @param parent the parent shell
     * @param entry the entry
     * @param properties the properties to use
     */
    public EditTemplateDialog(Shell parent, TemplateEntry entry, Map<String, String> properties) {
        super(parent);

        this.entry = entry;
        this.properties = properties;

        templateManager = JAutodocPlugin.getContext().getTemplateManager();

        setShellStyle(getShellStyle() | SWT.MAX | SWT.RESIZE);
        setHelpAvailable(false);

        TemplateEngineLogger.addLogListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    protected Control createDialogArea(Composite parent) {
        view = new EditTemplatePanel(parent, SWT.NONE);

        configureTemplateViewer();

        initData();
        initListener();

        setTitle("Javadoc Template");
        setMessage(createTitleMessage());
        setTitleImage(ResourceManager.getImage(ResourceManager.JAVADOC_IMAGE_FILE));

        return view;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#okPressed()
     */
    protected void okPressed() {
        if (validate(false) != null) {
            commitData();
            super.okPressed();
        }
    }

    /**
     * Configure the template viewers (template and preview).
     */
    private void configureTemplateViewer() {
        IApplicationContext ctx = JAutodocPlugin.getContext();

        // edit template viewer
        ITextHover editTextHover                 = ctx.getTemplateTextHover(properties);
        IRulesStrategy[] editRuleStrategies     = ctx.getTemplateRuleStrategies();
        IAutoEditStrategy[] autoEditStrategies     = ctx.getTemplateAutoEditStrategies();
        ITemplateContentAssistant[] assistants     = ctx.getTemplateContentAssistants(properties);

        SourceViewerConfiguration configuration = new TemplateViewerConfiguration(
                new TemplateCodeScanner(editRuleStrategies),
                editTextHover,
                autoEditStrategies,
                new TemplateAssistProcessor(assistants));
        view.templateViewer.configure(configuration);

        // preview template viewer (read only -> no content assists)
        ITextHover         previewTextHover        = ctx.getTemplateTextHover(properties);
        IRulesStrategy[] previewRuleStrategies    = ctx.getTemplatePreviewRuleStrategies(this);

        configuration = new TemplateViewerConfiguration(
                new TemplateCodeScanner(previewRuleStrategies),
                previewTextHover);
        view.previewViewer.configure(configuration);
    }

    private void initData() {
        view.groupGeneral.setText(entry.getDescription() + " Template");
        view.textName.setText(StringUtils.getLastElement(StringUtils.checkNull(entry.getName()), '.'));
        view.textRegex.setText(StringUtils.checkNull(entry.getRegex()));
        view.textExample.setText(StringUtils.checkNull(entry.getExample()));
        view.radioElementName.setSelection(!entry.isUseSignature());
        view.radioSignature.setSelection(entry.isUseSignature());

        if (entry.isDefaultTemplate()) {
            view.textName.setEditable(false);
            view.textRegex.setEditable(false);
        }

        IDocument document = null;
        String text = StringUtils.checkNull(entry.getText());
        if (text.length() == 0) {
            if (entry.isParameter()) {
                document= new Document(Constants.EMPTY_PARAMDOC);
            }
            else if (entry.isException()) {
                document= new Document(Constants.EMPTY_THROWSDOC);
            }
            else {
                document= new Document(Constants.EMPTY_JAVADOC);
            }
        }
        else {
            document= new Document(text);
        }
        view.templateViewer.setDocument(document);

        view.textAreaPreviewGroups.setText("");
        view.previewViewer.setDocument(new Document(""));
        view.tabFolder.setSelection(0);

        TemplateEntry parent = entry.getParent();
        if (parent != null) {
            view.groupParent.setText("Parent " + parent.getDescription() + " Template");
            view.textParentName.setText(StringUtils.getLastElement(parent.getName(), '.'));
            view.textParentRegex.setText(parent.getRegex());
            view.textParentExample.setText(parent.getExample());
        }
        else {
            view.textParentName.setText("");
            view.textParentRegex.setText("");
            view.textParentExample.setText("");
        }
    }

    private void initListener() {
        view.buttonPreview.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                showPreview();
            }
        });

        view.tabFolder.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                if (view.tabFolder.getSelectionIndex() == 1) {
                    showPreview();
                }
            }
        });

        VerifyListener verifyListener = new VerifyListener() {
            public void verifyText(VerifyEvent e) {
                if (e.getSource() == view.textName && e.character == '.') {
                    setInfo("Please don't use dots for the name.");
                    e.doit = false;
                    return;
                }

                setOK(); // clear messages
            }
        };

        view.textName.addVerifyListener(verifyListener);
        view.textRegex.addVerifyListener(verifyListener);
        view.textExample.addVerifyListener(verifyListener);

        view.templateViewer.prependVerifyKeyListener(new VerifyKeyListener() {
            public void verifyKey(VerifyEvent event) {
                verifyTemplateViewerKeyPressed(event);
            }
        });

        view.previewViewer.prependVerifyKeyListener(new VerifyKeyListener() {
            public void verifyKey(VerifyEvent e) {
                if (!e.doit) return;

                if (e.stateMask == SWT.MOD1 && e.character == 't' - 'a' + 1) {
                    view.tabFolder.setSelection(0);
                    view.templateViewer.getControl().forceFocus();
                    e.doit = false;
                    return;
                }
                else if (e.keyCode != SWT.MOD1) {
                    setInfo("Please change to the Template tab for editing.");
                }
            }
        });

        view.getShell().addShellListener(new ShellAdapter() {
            public void shellClosed(ShellEvent e) {
                TemplateEngineLogger.removeLogListener(EditTemplateDialog.this);
            }
        });

        addShortcutListener();
    }

    private void addShortcutListener() {
        ShortcutListener listener = new ShortcutListener();

        view.textName.addKeyListener(listener);
        view.textRegex.addKeyListener(listener);
        view.textExample.addKeyListener(listener);

        view.templateViewer.prependVerifyKeyListener(listener);
        view.previewViewer.prependVerifyKeyListener(listener);
    }

    private void verifyTemplateViewerKeyPressed(VerifyEvent event) {
        if (!event.doit) return;

        // clear messages
        setOK();

        // CTRL pressed?
        if (event.stateMask != SWT.MOD1)
            return;

        switch (event.character) {
            // CTRL-Space
            case ' ':
                view.templateViewer.doOperation(ISourceViewer.CONTENTASSIST_PROPOSALS);
                event.doit = false;
                break;

            // CTRL-Z
            case 'z' - 'a' + 1:
                view.templateViewer.doOperation(ITextOperationTarget.UNDO);
                event.doit= false;
                break;

            // CTRL-Y
            case 'y' - 'a' + 1:
                view.templateViewer.doOperation(ITextOperationTarget.REDO);
                event.doit= false;
                break;
        }
    }

    private void commitData() {
        entry.setName(templateName);
        entry.setRegex(view.textRegex.getText().trim());
        entry.setExample(view.textExample.getText().trim());
        entry.setText(view.templateViewer.getTextWidget().getText().trim());
        entry.setUseSignature(view.radioSignature.getSelection());
        templateManager.putTemplate(entry);
    }

    private String createTitleMessage() {
        String title = entry.getName() == null ? "Add" : "Edit";
        title += " " + entry.getDescription();
        title += " " + "Template";

        return title;
    }

    private void showPreview() {
        String templateText = validate(true);
        if (templateText == null) return;

        fireTemplateResultChangeEvent();
        setPreviewGroupsText(matcher, parentMatcher);

        view.previewViewer.setDocument(new Document(templateText));
        if (view.tabFolder.getSelectionIndex() != 1) {
            view.tabFolder.setSelection(1);
            view.previewViewer.getControl().forceFocus();
        }
    }

    private String evaluateTemplate(String templateText) {
        String text = null;
        try {
            logEntry = null;
            text = templateManager.evaluateTemplate(matcher, parentMatcher,
                    templateText, entry, properties);
            if (logEntry != null) {
                if (logEntry.getSeverity() != IStatus.ERROR) {
                    setInfo(logEntry.getMessage());
                }
                else {
                    setError(logEntry.getMessage());
                }

                int offset = Utils.getDocumentOffset(view.templateViewer.getDocument(),
                        logEntry.getLine(), logEntry.getColumn());
                if (offset >= 0) {
                    view.templateViewer.getTextWidget().setSelection(offset);
                    view.templateViewer.getTextWidget().forceFocus();
                }

                text = null;
            }
        } catch (Exception e) {
            setError(e.getMessage());
        }
        return text;
    }

    private void setOK() {
        setMessage(createTitleMessage());
        setErrorMessage(null);
    }

    private void setInfo(String message) {
        setMessage(message, IMessageProvider.WARNING);
    }

    private void setError(String message) {
        setErrorMessage(message);
    }

    private String validate(boolean preview) {
        if (!preview && !validateText(view.textName,
                            "Please insert a name for the template.", true)        ||
            !validateText(view.textRegex,
                            "Please insert a valid regular expression.", true) ||
            !validateText(view.textExample,
                            "Please insert an example that matches the regular expression.", true)    ||
            !validateText(view.templateViewer.getTextWidget(),
                            "Please insert a text for the template.", true)) {
            return null;
        }

        if (!validatePattern() || !validateTemplate() || !preview && !validateName()) {
            return null;
        }

        matcher       = pattern.matcher(view.textExample.getText());
        parentMatcher = entry.getParent() != null ? getParentMatcher() : null;

        if (!matcher.matches()) {
            setError("The example doesn't match the regular expression.");
            view.textExample.forceFocus();
            return null;
        }

        String evaluatedText = evaluateTemplate(view.templateViewer.getTextWidget().getText());
        if (evaluatedText == null) {
            view.tabFolder.setSelection(0);
            view.templateViewer.getTextWidget().forceFocus();
            return null;
        }

        if (!evaluatedText.endsWith(view.templateViewer.getTextWidget().getLineDelimiter())) {
            evaluatedText += view.templateViewer.getTextWidget().getLineDelimiter();
        }

        return validateComment(evaluatedText) ? evaluatedText : null;
    }

    private boolean validateComment(String evaluatedText) {
        IScanner commentScanner = ToolFactory.createScanner(true, false, false, false);
        commentScanner.setSource(evaluatedText.toCharArray());

        try {
            int token = commentScanner.getNextToken();
            if (Utils.isJavadocComment(token) || Utils.isMarkdownComment(token)) {
                token = commentScanner.getNextToken();
                if (token == ITerminalSymbols.TokenNameEOF) {
                    return true;
                }
            }
        } catch (InvalidInputException e) {/* ignore */}

        setError("Please insert a valid Javadoc comment.");
        return false;
    }

    private boolean validateText(Text text, String message, boolean error) {
        return validateText(text, text.getText(), message, error);
    }

    private boolean validateText(StyledText text, String message, boolean error) {
        return validateText(text, text.getText(), message, error);
    }

    private boolean validateText(Control control, String text, String message, boolean error) {
        if (text.trim().length() == 0) {
            if (!error)    setInfo(message);
            else         setError(message);

            control.forceFocus();
            return false;
        }

        setOK();
        return true;
    }

    private boolean validatePattern() {
        pattern = compilePattern(view.textRegex.getText(), false);
        return pattern != null;
    }

    private boolean validateName() {
        templateName = entry.getParent() == null ? "" : entry.getParent().getName() + ".";
        templateName += entry.getDescription() + "." + view.textName.getText().trim();

        if ((entry.getName() == null || !entry.getName().equals(templateName)) &&
                templateManager.existsTemplate(templateName)) {
            setError("A template with this name already exists.");
            view.textName.forceFocus();
            return false;
        }
        else {
            setOK();
            return true;
        }
    }

    private boolean validateTemplate() {
        String message = "";
        String templateText = view.templateViewer.getTextWidget().getText();
        try {
            templateManager.validateTemplate(templateText);
            return true;
        } catch (ValidationException ve) {
            message = ve.getMessage();
            int offset = Utils.getDocumentOffset(view.templateViewer.getDocument(),
                    ve.getLine(), ve.getColumn());
            if (offset >= 0) {
                view.templateViewer.getTextWidget().setSelection(offset);
                view.templateViewer.getTextWidget().forceFocus();
            }
        } catch (Exception e) {
            message = e.getMessage();
        }

        setError("Invalid template: " + message);
        view.tabFolder.setSelection(0);
        view.templateViewer.getTextWidget().forceFocus();
        return false;
    }

    private Matcher getParentMatcher() {
        Matcher matcher = null;
        String example = view.textParentExample.getText().trim();
        if (example.length() > 0) {
            Pattern pattern = compilePattern(entry.getParent().getRegex(), true);
            if (pattern != null) {
                matcher = pattern.matcher(view.textParentExample.getText());
            }
        }
        return matcher;
    }

    private Pattern compilePattern(String regex, boolean silent) {
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(regex);
            if (!silent) {
                setOK();
            }
        } catch (Exception e) {
            if (!silent) {
                int index = e.getMessage().indexOf(Constants.LINE_SEPARATOR);
                String message = index < 0 ? e.getMessage() : e.getMessage().substring(0, index);
                setError("Invalid regular expression: " + message);
            }
        }
        return pattern;
    }

    private void setPreviewGroupsText(Matcher matcher, Matcher parentMatcher) {
        List<StyleRange> ranges = new ArrayList<StyleRange>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(Constants.TEMPLATES_DLG_HEADING_GROUPS + Constants.LINE_SEPARATOR);
        TextAttribute ta = ResourceManager.getTextAttribute(ResourceManager.HEADING);
        ranges.add(new StyleRange(0, buffer.length(),
                ta.getForeground(),    ta.getBackground(), ta.getStyle()));
        createPreviewGroupsText(matcher, buffer, ranges);

        if (parentMatcher != null) {
            int startIndex = buffer.length();
            buffer.append(Constants.LINE_SEPARATOR +
                    Constants.TEMPLATES_DLG_HEADING_PARENTGROUPS + Constants.LINE_SEPARATOR);
            ranges.add(new StyleRange(startIndex, buffer.length() - startIndex,
                    ta.getForeground(),    ta.getBackground(), ta.getStyle()));
            createPreviewGroupsText(parentMatcher, buffer, ranges);
        }

        view.textAreaPreviewGroups.setText(buffer.toString());
        view.textAreaPreviewGroups.setStyleRanges(ranges.toArray(new StyleRange[ranges.size()]));
    }

    private void createPreviewGroupsText(Matcher matcher, StringBuffer buffer, List<StyleRange> ranges) {
        int startIndex = buffer.length();

        if (!matcher.matches()) {
            buffer.append(Constants.TEMPLATES_DLG_NOMATCH + Constants.LINE_SEPARATOR);
            TextAttribute ta = ResourceManager.getTextAttribute(ResourceManager.NOMATCH);
            ranges.add(new StyleRange(startIndex, buffer.length() - startIndex,
                    ta.getForeground(),    ta.getBackground(), ta.getStyle()));
            return;
        }

        TextAttribute taNormal = ResourceManager.getTextAttribute(ResourceManager.NORMAL);
        TextAttribute taGroup  = ResourceManager.getTextAttribute(ResourceManager.GROUP);

        for (int i = 0; i <= matcher.groupCount(); ++i) {
            buffer.append("" + i + ": ");
            ranges.add(new StyleRange(startIndex, buffer.length() - startIndex,
                    taNormal.getForeground(), taNormal.getBackground(), taNormal.getStyle()));
            startIndex = buffer.length();
            buffer.append(matcher.group(i) + Constants.LINE_SEPARATOR);
            ranges.add(new StyleRange(startIndex, buffer.length() - startIndex,
                    taGroup.getForeground(), taGroup.getBackground(), taGroup.getStyle()));
            startIndex = buffer.length();
        }
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.templates.replacements.ITemplateReplacementsProvider#getReplacements()
     */
    public Collection<String> getReplacements() {
        if (matcher == null || !matcher.matches()) {
            return new TreeMap<Integer, String>().values();
        }

        Map<Integer, String> map = new TreeMap<Integer, String>();
        for (int i = 0; i <= matcher.groupCount(); ++i) {
            String group = matcher.group(i) != null ? matcher.group(i).trim() : "";
            if (group.length() > 0) {
                putReplacementEntry(map, group);
                putReplacementEntry(map, StringUtils.split(group));
            }
        }

        if (parentMatcher == null || !parentMatcher.matches()) {
            return map.values();
        }

        for (int i = 0; i <= parentMatcher.groupCount(); ++i) {
            String group = parentMatcher.group(i).trim();
            if (group.length() > 0) {
                putReplacementEntry(map, group);
                putReplacementEntry(map, StringUtils.split(group));
            }
        }

        return map.values();
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.templates.replacements.ITemplateReplacementsProvider#addTemplateReplacementsListener(net.sf.jautodoc.templates.replacements.ITemplateReplacementsListener)
     */
    public void addTemplateReplacementsListener(ITemplateReplacementsListener listener) {
        resultListener.add(listener);
    }

    private void putReplacementEntry(Map<Integer, String> map, String string) {
        map.put(Integer.valueOf(-1 * string.length()), string);
    }

    private void fireTemplateResultChangeEvent() {
        Iterator<ITemplateReplacementsListener> iter = resultListener.iterator();
        while (iter.hasNext()) {
            iter.next().templateReplacementsChange(new TemplateReplacementsChangeEvent(getReplacements()));
        }
    }

    /* (non-Javadoc)
     * @see net.sf.jautodoc.templates.LogListener#messageLogged(net.sf.jautodoc.templates.LogEntry)
     */
    public void messageLogged(LogEntry logEntry) {
        this.logEntry = logEntry;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
     */
    protected IDialogSettings getDialogBoundsSettings() {
        String sectionName= getClass().getName() + "_dialogBounds"; //$NON-NLS-1$
        IDialogSettings settings= JAutodocPlugin.getDefault().getDialogSettings();
        IDialogSettings section= settings.getSection(sectionName);
        if (section == null)
            section= settings.addNewSection(sectionName);
        return section;
    }

    // ----------------------------------------------------
    // inner classes
    // ----------------------------------------------------

    private class ShortcutListener implements KeyListener, VerifyKeyListener {

        public void keyPressed(KeyEvent e) {
            handleKeyEvent(e);
        }

        public void keyReleased(KeyEvent e) {
        }

        public void verifyKey(VerifyEvent e) {
            handleKeyEvent(e);
        }

        private void handleKeyEvent(KeyEvent e) {
            if (e.stateMask != SWT.MOD1) return;

            if (e.character == 's' - 'a' + 1) {
                showPreview(); // CTRL-S (also Preview)
                e.doit = false;
            }
            else if (e.character == 'p' - 'a' + 1) {
                showPreview(); // CTRL-P
                e.doit = false;
            }
        }
    }
}
