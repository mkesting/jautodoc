/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.HashMap;
import java.util.Map;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.preferences.replacements.ReplacementManager;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jdt.core.ICompilationUnit;


/**
 * Manager for Workspace and Project specific Configurations.
 */
public class ConfigurationManager implements Constants {

    private static Map<IProject, Configuration> configurations = new HashMap<IProject, Configuration>();
    private static Map<Object, Configuration> configurationCache = new HashMap<Object, Configuration>();

    private static Configuration currentConfiguration   = null;
    private static Configuration workspaceConfiguration = null;

    static {
        workspaceConfiguration = new Configuration(
                (PreferenceStore)JAutodocPlugin.getDefault().getPreferenceStore());
        currentConfiguration = workspaceConfiguration;
    }

    // prevent instantiation
    private ConfigurationManager() {
    }

    /**
     * Gets the preference store for the Workspace Configuration.
     *
     * @return the preference store
     */
    public static PreferenceStore getPreferenceStore() {
        return getWorkspaceConfiguration().getPreferenceStore();
    }

    /**
     * Gets the preference store for the given Project.
     *
     * @param project the project
     *
     * @return the preference store
     */
    public static PreferenceStore getPreferenceStore(IProject project) {
        return getConfiguration(project, true, false).getPreferenceStore();
    }

    /**
     * Gets the configuration for the given Project.
     *
     * @param project the project
     *
     * @return the configuration
     */
    public static Configuration getConfiguration(IProject project) {
        return getConfiguration(project, true, true);
    }

    /**
     * Gets the configuration for the given Project.
     *
     * @param project the project
     * @param readonly true, if configuration is used readonly
     *
     * @return the configuration
     */
    public static Configuration getConfiguration(IProject project,
            boolean readonly) {
        return getConfiguration(project, readonly, true);
    }

    /**
     * Gets the configuration for the given Project.
     *
     * @param project the project
     * @param readonly true, if configuration is used readonly
     * @param effective true, if configuration should be effective
     *
     * @return the configuration
     */
    public static Configuration getConfiguration(IProject project,
            boolean readonly, boolean effective) {
        if (project == null) {
            return checkReadonly(getWorkspaceConfiguration(), readonly);
        }

        Configuration configuration = (Configuration)configurations.get(project);
        if (configuration == null) {
            PreferenceStore prefStore = new PreferenceStore(new ProjectScope(project),
                    JAutodocPlugin.getDefault().getBundle().getSymbolicName());
            configuration = new Configuration(prefStore, project);
            currentConfiguration = configuration;
            configurations.put(project, configuration);
        }

        if (!effective ||
                configuration.getPreferenceStore().getBoolean(PROJECT_SPECIFIC)) {
            currentConfiguration = configuration;
            return checkReadonly(configuration, readonly);
        }
        else {
            return checkReadonly(getWorkspaceConfiguration(), readonly);
        }
    }

    /**
     * Gets the configuration for the given compilation unit.
     *
     * @param compUnit the compilation unit
     *
     * @return the configuration
     */
    public static Configuration getConfiguration(ICompilationUnit compUnit) {
        return getConfiguration(compUnit, true);
    }

    /**
     * Gets the configuration for the given compilation unit.
     *
     * @param project the compilation unit
     * @param readonly true, if configuration is used readonly
     *
     * @return the configuration
     */
    public static Configuration getConfiguration(ICompilationUnit compUnit,
            boolean readonly) {
        return getConfiguration(compUnit, readonly, true);
    }

    /**
     * Gets the configuration for the given compilation unit.
     *
     * @param project the compilation unit
     * @param readonly true, if configuration is used readonly
     * @param effective true, if configuration should be effective
     *
     * @return the configuration
     */
    public static Configuration getConfiguration(ICompilationUnit compUnit,
            boolean readonly, boolean effective) {
        return getConfiguration(compUnit.getJavaProject().getProject(), readonly, effective);
    }

    /**
     * Cache the configuration for the given object.
     *
     * @param object the object
     * @param config the configuration to cache
     */
    public static void cacheConfiguration(Object object, Configuration config) {
        configurationCache.put(object, config);
    }

    /**
     * Gets the cached configuration for the given object.
     *
     * @param object the object
     *
     * @return the cached configuration
     */
    public static Configuration getCachedConfiguration(Object object) {
        Configuration configuration = (Configuration)configurationCache.get(object);
        if (configuration != null) {
            currentConfiguration = configuration;
        }
        return configuration;
    }

    /**
     * Checks for a cached configuration of the given object.
     *
     * @param object the object
     *
     * @return true, if a cached configuration is available
     */
    public static boolean hasCachedConfiguration(Object object) {
        return configurationCache.containsKey(object);
    }

    /**
     * Removes the cached configuration of the given object.
     *
     * @param object the object
     */
    public static void removeCachedConfiguration(Object object) {
        configurationCache.remove(object);
    }

    /**
     * Gets the replacement manager from the current configuration.
     *
     * @return the replacement manager
     */
    public static ReplacementManager getReplacementManager() {
        return getCurrentConfiguration().getReplacementManager();
    }

    /**
     * Gets the last used configuration.
     *
     * @return the current configuration
     */
    public static Configuration getCurrentConfiguration() {
        return currentConfiguration != null ? currentConfiguration
                : workspaceConfiguration;
    }

    /**
     * Gets the workspace configuration.
     *
     * @return the workspace configuration
     */
    private static Configuration getWorkspaceConfiguration() {
        currentConfiguration = workspaceConfiguration;
        return workspaceConfiguration;
    }

    /**
     * Creates a writeable configuration, if the readonly flag isn't set.
     *
     * @param config the configuration to check
     * @param readonly the readonly flag
     *
     * @return the configuration
     */
    private static Configuration checkReadonly(Configuration config, boolean readonly) {
        return readonly ? config : new Configuration(config);
    }
}
