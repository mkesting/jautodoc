/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.ResourceManager;
import net.sf.jautodoc.utils.SimpleTreeContentProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog for import and export of overall preferences of JAutodoc.
 */
public class ImportExportDialog extends TitleAreaDialog implements Listener {
    private static final String DESTINATIONS = "DESTINATIONS";
    private static final String PREFERENCETYPES = "PREFERENCETYPES";
    private static final String OVERWRITEEXISTING = "OVERWRITEEXISTING";

    private Text descriptionText;
    private CheckboxTreeViewer treeViewer;
    private Button destinationBrowseButton;
    private Button overwriteExistingFilesCheckbox;
    private Combo destinationNameField;
    private String currentMessage;

    private final Type type;
    private final List<PreferenceType> preferenceTypes;

    private String selectedFileName;
    private List<PreferenceType> selectedPreferenceTypes;


    /**
     * Instantiates a new import export dialog.
     *
     * @param parentShell the parent shell
     * @param type IMPORT or EXPORT
     * @param preferenceTypes the selectable preference types
     */
    public ImportExportDialog(final Shell parentShell, final Type type, final List<PreferenceType> preferenceTypes) {
        super(parentShell);
        setHelpAvailable(false);

        this.type = type;
        this.preferenceTypes = preferenceTypes;
    }

    public String getSelectedFileName() {
        return selectedFileName;
    }

    public List<PreferenceType> getSelectedPreferenceTypes() {
        return selectedPreferenceTypes;
    }

    @Override
    protected void okPressed() {
        selectedFileName = getDestinationValue();
        selectedPreferenceTypes = new ArrayList<PreferenceType>();
        for (final Object element : treeViewer.getCheckedElements()) {
            selectedPreferenceTypes.add((PreferenceType)element);
        }
        saveWidgetValues();
        super.okPressed();
    }

    @Override
    protected void configureShell(final Shell shell) {
          super.configureShell(shell);
          shell.setText(type.getTitle());
    }

    protected Control createContents(final Composite parent) {
        Control composite = super.createContents(parent);
        setPageComplete(validateDestinationGroup() && validateOptionsGroup());
        return composite;
    }

    protected Control createDialogArea(final Composite parent) {
        setTitle(type.getTitle());
        setMessage(type.getMessage());
        setTitleImage(ResourceManager.getImage(type.getImageFileName()));

        initializeDialogUnits(parent);

        final Composite base = new Composite(parent, SWT.NONE);
        base.setLayout(new GridLayout());
        final GridData gridData = new GridData(GridData.FILL_BOTH);
        //gridData.heightHint = 550;
        gridData.widthHint  = 250;
        base.setLayoutData(gridData);

        final Group group = new Group(base, SWT.NONE);
        group.setLayout(new GridLayout());
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        treeViewer = new CheckboxTreeViewer(group, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        treeViewer.getControl().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        treeViewer.setContentProvider(new SimpleTreeContentProvider(preferenceTypes));
        treeViewer.setLabelProvider(new TreeLabelProvider());
        treeViewer.setInput(PreferenceType.values());

        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                updateDescription();
            }
        });

        treeViewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                updatePageCompletion();
            }
        });

        final Label description = new Label(group, SWT.NONE);
        description.setText("Description:");
        description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        descriptionText = new Text(group, SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER | SWT.WRAP);
        final GridData descriptionData = new GridData(GridData.FILL_BOTH);
        descriptionData.heightHint = convertHeightInCharsToPixels(3);
        descriptionText.setLayoutData(descriptionData);

        addSelectionButtons(group);
        createDestinationGroup(base);
        createOptionsGroup(base);

        restoreWidgetValues();

        return base;
    }

    private void addSelectionButtons(final Composite composite) {
        final Composite buttonComposite = new Composite(composite, SWT.NONE);
        buttonComposite.setLayout(new GridLayout(2, false));
        buttonComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false));

        final Button selectAllButton = createButton(buttonComposite,
                IDialogConstants.SELECT_ALL_ID, "&Select All", false);

        final Button deselectAllButton = createButton(buttonComposite,
                IDialogConstants.DESELECT_ALL_ID, "&Deselect All", false);

        final SelectionListener listener = new SelectionAdapter() {
            @SuppressWarnings("deprecation")
            public void widgetSelected(SelectionEvent e) {
                treeViewer.setAllChecked(e.widget == selectAllButton);
                updatePageCompletion();
            }
        };

        selectAllButton.addSelectionListener(listener);
        deselectAllButton.addSelectionListener(listener);
    }

    private void createDestinationGroup(final Composite parent) {
        final Composite destinationSelectionGroup = new Composite(parent, SWT.NONE);
        destinationSelectionGroup.setLayout(new GridLayout(3, false));
        destinationSelectionGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));

        final Label dest = new Label(destinationSelectionGroup, SWT.NONE);
        dest.setText(type.getFileDescription());

        destinationNameField = new Combo(destinationSelectionGroup, SWT.SINGLE  | SWT.BORDER);
        destinationNameField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        destinationNameField.addListener(SWT.Modify, this);
        destinationNameField.addListener(SWT.Selection, this);

        destinationBrowseButton = new Button(destinationSelectionGroup, SWT.PUSH);
        destinationBrowseButton.setText("B&rowse...");
        setButtonLayoutData(destinationBrowseButton);
        destinationBrowseButton.addListener(SWT.Selection, this);
    }

    private void createOptionsGroup(final Composite parent) {
        if (type == Type.EXPORT) {
            final Composite optionsGroup = new Composite(parent, SWT.NONE);
            GridLayout layout = new GridLayout();
            layout.marginHeight = 0;
            optionsGroup.setLayout(layout);
            optionsGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

            overwriteExistingFilesCheckbox = new Button(optionsGroup, SWT.CHECK);
            overwriteExistingFilesCheckbox.setText("&Overwrite existing files without warning");
        }
    }

    private void handleDestinationBrowseButtonPressed() {
        final FileDialog dialog = new FileDialog(getShell(), type.getFileDialogStyle());
        dialog.setText(type.getFileDialogTitle());
        if (type == Type.EXPORT && getDestinationValue().isEmpty()) {
            dialog.setFileName("jautodoc_preferences.xml");
        } else {
            dialog.setFileName(getDestinationValue());
        }
        dialog.setFilterExtensions(new String[] { "*.xml" ,"*.*"});
        dialog.setOverwrite(type == Type.EXPORT && !overwriteExistingFilesCheckbox.getSelection());

        final String selectedFileName = dialog.open();
        if (selectedFileName != null) {
            setDestinationValue(selectedFileName);
        }
    }

    private void updateDescription() {
        String desc = ""; //$NON-NLS-1$
        final ISelection selection = treeViewer.getSelection();
        if (!selection.isEmpty()) {
            final Object element = ((IStructuredSelection) selection).getFirstElement();
            if ((element instanceof PreferenceType)) {
                desc = ((PreferenceType) element).getDescription();
            }
        }
        descriptionText.setText(desc);
    }

    private void updatePageCompletion() {
        setPageComplete(determinePageCompletion());
    }

    private boolean determinePageCompletion() {
        final boolean complete = validateDestinationGroup() && validateOptionsGroup();
        if (complete) {
            setErrorMessage(null);
        } else {
            setErrorMessage(currentMessage);
        }
        return complete;
    }

    private boolean validateDestinationGroup() {
        final File file = new File(getDestinationValue());
        if (!isValidFile(file)) {
            currentMessage = type.getInvalidDestinationMessage();
            return false;
        }
        return true;
    }

    private boolean isValidFile(final File file) {
        if (type == Type.IMPORT) {
            return file.exists() && !file.isDirectory();
        } else {
            return file.getPath().length() > 0 && !file.isDirectory();
        }
    }

    private boolean validateOptionsGroup() {
        boolean isValid = true;
        final Object[] checkedElements = treeViewer.getCheckedElements();
        if (checkedElements == null || checkedElements.length == 0) {
            currentMessage = "No specific preferences are selected.";
            isValid = false;
        }
        return isValid;
    }

    private void saveWidgetValues() {
        final IDialogSettings settings = getDialogSettingsSection();

        final Object[] elements = treeViewer.getCheckedElements();
        final String[] preferenceIds = new String[elements.length];
        for (int i = 0; i < elements.length; i++) {
            final PreferenceType element = (PreferenceType) elements[i];
            preferenceIds[i] = element.name();
        }
        settings.put(PREFERENCETYPES, preferenceIds);

        final String destination = getDestinationValue();
        if (!destination.isEmpty()) {
            final List<String> destinations = new ArrayList<String>(Arrays.asList(destinationNameField.getItems()));
            destinations.remove(destination);
            destinations.add(0, destination);
            if (destinations.size() > 5) {
                destinations.remove(5);
            }
            settings.put(DESTINATIONS, destinations.toArray(new String[destinations.size()]));
        }

        if (overwriteExistingFilesCheckbox != null) {
            settings.put(OVERWRITEEXISTING, overwriteExistingFilesCheckbox.getSelection());
        }
    }

    @SuppressWarnings("deprecation")
    private void restoreWidgetValues() {
        final IDialogSettings settings = getDialogSettingsSection();

        final String[] preferenceIds = settings.getArray(PREFERENCETYPES);
        if (preferenceIds != null && preferenceIds.length > 0) {
            final PreferenceType[] preferenceTypes = PreferenceType.values();
            for (int i = 0; i < preferenceTypes.length; i++) {
                for (int j = 0; j < preferenceIds.length; j++) {
                    if (preferenceTypes[i].name().equals(preferenceIds[j])) {
                        treeViewer.setChecked(preferenceTypes[i], true);
                        break;
                    }
                }
            }
        } else {
            treeViewer.setAllChecked(true);
        }

        final String[] destinations = settings.getArray(DESTINATIONS);
        if (destinations != null && destinations.length > 0) {
            for (int i = 0; i < destinations.length; i++) {
                destinationNameField.add(destinations[i]);
            }
            setDestinationValue(destinations[0]);
        }

        if (overwriteExistingFilesCheckbox != null) {
            overwriteExistingFilesCheckbox.setSelection(settings.getBoolean(OVERWRITEEXISTING));
        }
    }

    private IDialogSettings getDialogSettingsSection() {
        return JAutodocPlugin.getDefault().getDialogSettingsSection("ImportExportDialog-" + type.name());
    }

    private void setPageComplete(final boolean complete) {
        final Button okButton = getButton(IDialogConstants.OK_ID);
        if (okButton != null) {
            okButton.setEnabled(complete);
        }
    }

    private String getDestinationValue() {
        return destinationNameField.getText().trim();
    }

    private void setDestinationValue(final String value) {
        destinationNameField.setText(value);
    }

    @Override
    public void handleEvent(Event e) {
        if (e.widget == destinationBrowseButton) {
            handleDestinationBrowseButtonPressed();
        }
        updatePageCompletion();
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    public enum Type {
        IMPORT("Import JAutodoc Preferences", "Import preferences from the local file system.",
                ResourceManager.IMPORT_IMAGE_FILE, "From preference file:", "Import preferences from File",
                "Preference file does not exist or is a directory.", SWT.OPEN),
        EXPORT("Export JAutodoc Preferences", "Export preferences to the local file system.",
                ResourceManager.EXPORT_IMAGE_FILE, "To preference file:", "Export preferences to File",
                "Preference file not set or is not a normal file.", SWT.SAVE);

        private final String title;
        private final String message;
        private final String imageFileName;
        private final String fileDescription;
        private final String fileDialogTitle;
        private final String invalidDestinationMessage;
        private final int fileDialogStyle;

        private Type(final String title, final String message, final String imageFileName, final String fileDescription,
                final String fileDialogTitle, final String invalidDestinationMessage, final int fileDialogStyle) {
            this.title = title;
            this.message = message;
            this.imageFileName = imageFileName;
            this.fileDescription = fileDescription;
            this.fileDialogTitle = fileDialogTitle;
            this.fileDialogStyle = fileDialogStyle;
            this.invalidDestinationMessage = invalidDestinationMessage;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public String getImageFileName() {
            return imageFileName;
        }

        public String getFileDescription() {
            return fileDescription;
        }

        public String getFileDialogTitle() {
            return fileDialogTitle;
        }

        public int getFileDialogStyle() {
            return fileDialogStyle;
        }

        public String getInvalidDestinationMessage() {
            return invalidDestinationMessage;
        }
    }

    private static final class TreeLabelProvider extends LabelProvider {

        @Override
        public Image getImage(Object element) {
            return ResourceManager.getImage(((PreferenceType) element).getImageFileName());
        }

        @Override
        public String getText(Object element) {
            return ((PreferenceType) element).getTitle();
        }
    }
}
