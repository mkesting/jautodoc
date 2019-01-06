/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc;

import net.sf.jautodoc.preferences.PreferenceStore;
import net.sf.jautodoc.templates.velocity.VelocityApplicationContext;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * The main plugin class to be used in the desktop.
 */
public class JAutodocPlugin extends AbstractUIPlugin {

    public static final String PLUGIN_ID = "net.sf.jautodoc";

    /** The shared instance. */
    private static JAutodocPlugin plugin;

    /** The preference store for this plugin. */
    private PreferenceStore preferenceStore;

    /** The application context. */
    private IApplicationContext context;


    /**
     * Instantiates this plugin.
     */
    public JAutodocPlugin() {
        plugin = this;
        initContext();
    }

    /**
     * Returns the shared instance.
     *
     * @return the JAutodoc plugin
     */
    public static JAutodocPlugin getDefault() {
        return plugin;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#getPreferenceStore()
     */
    public IPreferenceStore getPreferenceStore() {
        // Create the preference store lazily.
        if (preferenceStore == null) {
            preferenceStore = new PreferenceStore(InstanceScope.INSTANCE, getBundle().getSymbolicName());

        }
        return preferenceStore;
    }

    /**
     * Returns a section in this plugin's dialog settings. If the section doesn't exist yet, it is
     * created.
     *
     * @param name the name of the section
     * @return the section of the given name
     */
    public IDialogSettings getDialogSettingsSection(String name) {
        IDialogSettings dialogSettings = getDialogSettings();
        IDialogSettings section = dialogSettings.getSection(name);
        if (section == null) {
            section = dialogSettings.addNewSection(name);
        }
        return section;
    }

    /**
     * Handles an exception.
     *
     * @param shell the parent shell
     * @param compUnit the current compilation unit
     * @param e the exception
     */
    public void handleException(Shell shell, ICompilationUnit compUnit, Throwable e) {
        String reason = (e.getMessage() != null ?
                e.getMessage() : e.getClass().getName());
        Status status = new Status(
                IStatus.ERROR,
                JAutodocPlugin.PLUGIN_ID,
                0,
                compUnit.getElementName() + ": " + reason,
                e);

        ErrorDialog.openError(
                shell,
                "Error Occurred",
                "Error while processing " + compUnit.getElementName() +
                "\n\nSee Error Log for Details.",
                status);

        JAutodocPlugin.getDefault().getLog().log(status);
    }

    /**
     * Handles an exception.
     *
     * @param shell the parent shell
     * @param e the exception to handle
     */
    public void handleException(Shell shell, Throwable e) {
        String reason = (e.getMessage() != null ?
                e.getMessage() : e.getClass().getName());
        Status status = new Status(
                IStatus.ERROR,
                JAutodocPlugin.PLUGIN_ID,
                0,
                reason,
                e);

        ErrorDialog.openError(
                shell,
                "Error Occurred",
                null,
                status);

        JAutodocPlugin.getDefault().getLog().log(status);
    }

    /**
     * Handles an exception.
     *
     * @param je the current java element
     * @param e the exception
     */
    public void handleException(IJavaElement je, Throwable e) {
        String reason = (e.getMessage() != null ?
                e.getMessage() : e.getClass().getName());
        Status status = new Status(
                IStatus.ERROR,
                JAutodocPlugin.PLUGIN_ID,
                0,
                je.getElementName() + ": " + reason,
                e);

        JAutodocPlugin.getDefault().getLog().log(status);
    }

    /**
     * Handles an exception.
     *
     * @param e the exception
     */
    public void handleException(Exception e) {
        String reason = (e.getMessage() != null ?
                e.getMessage() : e.getClass().getName());
        Status status = new Status(
                IStatus.ERROR,
                JAutodocPlugin.PLUGIN_ID,
                0,
                reason,
                e);

        JAutodocPlugin.getDefault().getLog().log(status);
    }

    /**
     * Gets the application context for this plugin.
     *
     * @return the application context
     */
    public static IApplicationContext getContext() {
        return JAutodocPlugin.getDefault().context;
    }

    /**
     * Inits the application context.
     */
    private void initContext() {
        context = new VelocityApplicationContext();
    }
}
