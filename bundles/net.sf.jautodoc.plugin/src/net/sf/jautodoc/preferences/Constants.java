/*******************************************************************
 * Copyright (c) 2006 - 2025, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

/**
 * Constants for this Plugin.
 */
public interface Constants {

    public static final String LINE_SEPARATOR  = System.getProperty("line.separator");

    public static final String DOT = System.getProperty("jautodoc.dot", ".");

    public static final String NON_JAVADOC_TAG = "(non-Javadoc)";
    public static final String INHERIT_DOC_TAG = "{@inheritDoc}";

    public static final String ID_PROPERTY_PAGE   = "net.sf.jautodoc.properties.MainPreferencePage";
    public static final String ID_PREFERENCE_PAGE = "net.sf.jautodoc.preferences.MainPreferencePage";

    public static final String CLEANUP_PREF_PAGE_ID= "org.eclipse.jdt.ui.preferences.CleanUpPreferencePage";
    public static final String CLEANUP_PROP_PAGE_ID= "org.eclipse.jdt.ui.propertyPages.CleanUpPreferencePage";

    public static final String SAVEPART_PREF_PAGE_ID = "org.eclipse.jdt.ui.preferences.SaveParticipantPreferencePage";
    public static final String SAVEPART_PROP_PAGE_ID = "org.eclipse.jdt.ui.propertyPages.SaveParticipantPreferencePage";

    // --------------------------------
    // keys for preferences store
    // --------------------------------

    public static final String MODE          = "mode";
    public static final String MODE_COMPLETE = "mode_complete";
    public static final String MODE_KEEP     = "mode_keep";
    public static final String MODE_REPLACE  = "mode_replace";

    public static final String VISIBILITY_PUBLIC    = "visibility_public";
    public static final String VISIBILITY_PROTECTED = "visibility_protected";
    public static final String VISIBILITY_PACKAGE   = "visibility_package";
    public static final String VISIBILITY_PRIVATE   = "visibility_private";

    public static final String FILTER_TYPES       = "filter_types";
    public static final String FILTER_FIELDS      = "filter_fields";
    public static final String FILTER_METHODS     = "filter_methods";
    public static final String FILTER_GETSET      = "filter_getset";
    public static final String FILTER_EXCLGETSET  = "filter_exclgetset";
    public static final String FILTER_EXCLOVERRID = "filter_excloverriding";

    public static final String ADD_TODO            = "add_todo";
    public static final String CREATE_DUMMY_DOC    = "create_dummy_doc";
    public static final String REPLACEMENTS        = "replacements";
    public static final String FIELDS              = "fields";
    public static final String SINGLE_LINE         = "single_line_comment";
    public static final String USE_FORMATTER       = "use_internal_formatter";
    public static final String GET_SET_FROM_FIELD  = "get_set_from_field";
    public static final String INCLUDE_SUBPACKAGES = "include_subpackages";
    public static final String USE_MARKDOWN        = "use_markdown";
    public static final String SWITCH_DOC_STYLE    = "switch_doc_style";

    public static final String GET_SET_FROM_FIELD_FIRST   = "get_set_from_field_first";
    public static final String GET_SET_FROM_FIELD_REPLACE = "get_set_from_field_replace";

    public static final String GET_SET_FROM_FIELD_REPLACEMENTS = "get_set_from_field_replacements";

    public static final String ADD_HEADER     = "add_header";
    public static final String REPLACE_HEADER = "replace_header";
    public static final String MULTI_HEADER   = "multi_header";
    public static final String HEADER_TEXT    = "header_text";
    public static final String USE_PKG_INFO   = "use_pkg_info";
    public static final String PKG_DOC_TEXT   = "package_doc_text";
    public static final String PKG_INFO_TEXT  = "package_info_text";
    public static final String PROPERTIES     = "properties";
    public static final String TAG_ORDER      = "tag_order";

    public static final String PROJECT_SPECIFIC = "project_specific_settings";

    public static final String CLEANUP_JAVADOC_OPTION    = "jautodoc.cleanup.javadoc";
    public static final String CLEANUP_ADD_HEADER_OPTION = "jautodoc.cleanup.add_header";
    public static final String CLEANUP_REP_HEADER_OPTION = "jautodoc.cleanup.replace_header";


    // --------------------------------
    // default values
    // --------------------------------

    public static final String  DEFAULT_MODE = MODE_COMPLETE;

    public static final boolean DEFAULT_VISIBILITY_PUBLIC    = true;
    public static final boolean DEFAULT_VISIBILITY_PROTECTED = true;
    public static final boolean DEFAULT_VISIBILITY_PACKAGE   = true;
    public static final boolean DEFAULT_VISIBILITY_PRIVATE   = true;

    public static final boolean DEFAULT_FILTER_TYPES       = true;
    public static final boolean DEFAULT_FILTER_FIELDS      = true;
    public static final boolean DEFAULT_FILTER_METHODS     = true;
    public static final boolean DEFAULT_FILTER_GETSET      = false;
    public static final boolean DEFAULT_FILTER_EXCLGETSET  = false;
    public static final boolean DEFAULT_FILTER_EXCLOVERRID = false;

    public static final boolean DEFAULT_CREATE_DUMMY_DOC    = true;
    public static final boolean DEFAULT_ADD_TODO            = true;
    public static final boolean DEFAULT_SINGLE_LINE         = true;
    public static final boolean DEFAULT_USE_FORMATTER       = false;
    public static final boolean DEFAULT_GET_SET_FROM_FIELD  = false;
    public static final boolean DEFAULT_INCLUDE_SUBPACKAGES = false;
    public static final boolean DEFAULT_USE_MARKDOWN        = false;
    public static final boolean DEFAULT_SWITCH_DOC_STYLE    = false;

    public static final boolean DEFAULT_GET_SET_FROM_FIELD_FIRST   = true;
    public static final boolean DEFAULT_GET_SET_FROM_FIELD_REPLACE = false;

    public static final String DEFAULT_GET_SET_FROM_FIELD_REPLACEMENTS = "The|the|the new,|the|the new";

    public static final boolean DEFAULT_ADD_HEADER     = false;
    public static final boolean DEFAULT_REPLACE_HEADER = false;
    public static final boolean DEFAULT_MULTI_HEADER   = false;
    public static final boolean DEFAULT_USE_PKG_INFO   = false;

    public static final boolean DEFAULT_PROJECT_SPECIFIC = false;

    public static final String DEFAULT_TAG_ORDER =
            "@author,"      +
            "@version,"     +
            "@param,"       +
            "@return,"      +
            "@throws,"      +
            "@see,"         +
            "@since,"       +
            "@serial,"      +
            "@serialField," +
            "@serialData,"  +
            "@deprecated";

    public static final String DEFAULT_HEADER_TEXT = "/*"  + LINE_SEPARATOR +
                                                     " * " + LINE_SEPARATOR +
                                                     " */";

    public static final String DEFAULT_PKG_DOC_TEXT =
        "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">" + LINE_SEPARATOR +
        "<html>"        + LINE_SEPARATOR +
        "<head></head>" + LINE_SEPARATOR +
        "<body>"        + LINE_SEPARATOR +
        "  Provides..." + LINE_SEPARATOR +
        "</body>"       + LINE_SEPARATOR +
        "</html>";

    public static final String DEFAULT_PKG_INFO_TEXT =
        "/**"            + LINE_SEPARATOR +
        " * Provides..." + LINE_SEPARATOR +
        " */"            + LINE_SEPARATOR +
        "package ${package_name};";

    public static final String EMPTY_JAVADOC = "/**" + Constants.LINE_SEPARATOR +
                                               " * " + Constants.LINE_SEPARATOR +
                                               " */";

    public static final String EMPTY_PARAMDOC = "/**"             + Constants.LINE_SEPARATOR +
                                                " * @param ${e} " + Constants.LINE_SEPARATOR +
                                                " */";

    public static final String EMPTY_THROWSDOC = "/**"              + Constants.LINE_SEPARATOR +
                                                 " * @throws ${e} " + Constants.LINE_SEPARATOR +
                                                 " */";

    // --------------------------------
    // todo string
    // --------------------------------

    public static final String TODO_FOR_AUTODOC = PreferenceMessages.getString("preferences.source.todo"); //$NON-NLS-1$

    // --------------------------------
    // labels for main preferences page
    // --------------------------------

    public static final String PAGE_DESCRIPTION = PreferenceMessages.getString("preferences.page.description"); //$NON-NLS-1$

    public static final String LABEL_MODE          = PreferenceMessages.getString("preferences.label.mode"); //$NON-NLS-1$
    public static final String LABEL_MODE_COMPLETE = PreferenceMessages.getString("preferences.label.mode.complete"); //$NON-NLS-1$
    public static final String LABEL_MODE_KEEP     = PreferenceMessages.getString("preferences.label.mode.keep"); //$NON-NLS-1$
    public static final String LABEL_MODE_REPLACE  = PreferenceMessages.getString("preferences.label.mode.replace"); //$NON-NLS-1$

    public static final String LABEL_MARKDOWN        = PreferenceMessages.getString("preferences.label.markdown"); //$NON-NLS-1$
    public static final String LABEL_MARKDOWN_USE    = PreferenceMessages.getString("preferences.label.markdown.use"); //$NON-NLS-1$
    public static final String LABEL_MARKDOWN_SWITCH = PreferenceMessages.getString("preferences.label.markdown.switch"); //$NON-NLS-1$

    public static final String LABEL_VISIBILITY           = PreferenceMessages.getString("preferences.label.visibility"); //$NON-NLS-1$
    public static final String LABEL_VISIBILITY_PUBLIC    = PreferenceMessages.getString("preferences.label.visibility.public"); //$NON-NLS-1$
    public static final String LABEL_VISIBILITY_PROTECTED = PreferenceMessages.getString("preferences.label.visibility.protected"); //$NON-NLS-1$
    public static final String LABEL_VISIBILITY_PACKAGE   = PreferenceMessages.getString("preferences.label.visibility.package"); //$NON-NLS-1$
    public static final String LABEL_VISIBILITY_PRIVATE   = PreferenceMessages.getString("preferences.label.visibility.private"); //$NON-NLS-1$

    public static final String LABEL_FILTER             = PreferenceMessages.getString("preferences.label.filter"); //$NON-NLS-1$
    public static final String LABEL_FILTER_TYPES       = PreferenceMessages.getString("preferences.label.filter.types"); //$NON-NLS-1$
    public static final String LABEL_FILTER_FIELDS      = PreferenceMessages.getString("preferences.label.filter.fields"); //$NON-NLS-1$
    public static final String LABEL_FILTER_METHODS     = PreferenceMessages.getString("preferences.label.filter.methods"); //$NON-NLS-1$
    public static final String LABEL_FILTER_GETSET      = PreferenceMessages.getString("preferences.label.filter.getset"); //$NON-NLS-1$
    public static final String LABEL_FILTER_EXCLGETSET  = PreferenceMessages.getString("preferences.label.filter.exclgetset"); //$NON-NLS-1$
    public static final String LABEL_FILTER_EXCLOVERRID = PreferenceMessages.getString("preferences.label.filter.excloverriding"); //$NON-NLS-1$

    public static final String LABEL_OPTIONS            = PreferenceMessages.getString("preferences.label.options"); //$NON-NLS-1$
    public static final String LABEL_DUMMY_DOC          = PreferenceMessages.getString("preferences.label.options.dummydoc"); //$NON-NLS-1$
    public static final String LABEL_ADD_TODO           = PreferenceMessages.getString("preferences.label.options.addtoto"); //$NON-NLS-1$
    public static final String LABEL_SINGLE_LINE        = PreferenceMessages.getString("preferences.label.options.singleline"); //$NON-NLS-1$
    public static final String LABEL_USE_FORMATTER      = PreferenceMessages.getString("preferences.label.options.useformatter"); //$NON-NLS-1$
    public static final String LABEL_GET_SET_FROM_FIELD = PreferenceMessages.getString("preferences.label.options.getsetfromfield"); //$NON-NLS-1$
    public static final String LABEL_INCL_SUBPACKAGES   = PreferenceMessages.getString("preferences.label.options.inclsubpackages"); //$NON-NLS-1$

    public static final String LABEL_GET_SET_FROM_FIELD_EDIT   = PreferenceMessages.getString("preferences.label.options.getsetfromfieldedit"); //$NON-NLS-1$
    public static final String LABEL_GET_SET_FROM_FIELD_FIRST   = PreferenceMessages.getString("preferences.label.options.getsetfromfieldfirst"); //$NON-NLS-1$
    public static final String LABEL_GET_SET_FROM_FIELD_REPLACE = PreferenceMessages.getString("preferences.label.options.getsetfromfieldreplace"); //$NON-NLS-1$

    public static final String LABEL_HEADER         = PreferenceMessages.getString("preferences.label.header"); //$NON-NLS-1$
    public static final String LABEL_ADD_HEADER     = PreferenceMessages.getString("preferences.label.header.addheader"); //$NON-NLS-1$
    public static final String LABEL_EDIT_HEADER    = PreferenceMessages.getString("preferences.label.header.editheader"); //$NON-NLS-1$
    public static final String LABEL_REPLACE_HEADER = PreferenceMessages.getString("preferences.label.header.replaceheader"); //$NON-NLS-1$
    public static final String LABEL_MULTI_HEADER   = PreferenceMessages.getString("preferences.label.header.multiheader"); //$NON-NLS-1$
    public static final String LABEL_SAVE_ACTION    = PreferenceMessages.getString("preferences.label.header.saveaction"); //$NON-NLS-1$

    public static final String LABEL_PKGDOC         = PreferenceMessages.getString("preferences.label.pkgdoc"); //$NON-NLS-1$
    public static final String LABEL_EDIT_PKGDOC    = PreferenceMessages.getString("preferences.label.pkgdoc.edit"); //$NON-NLS-1$
    public static final String LABEL_PKGDOC_USEINFO = PreferenceMessages.getString("preferences.label.pkgdoc.useinfo"); //$NON-NLS-1$
    public static final String LABEL_TAG_ORDER      = PreferenceMessages.getString("preferences.label.tagorder"); //$NON-NLS-1$
    public static final String LABEL_EDIT_TAG_ORDER = PreferenceMessages.getString("preferences.label.tagorder.edit"); //$NON-NLS-1$

    public static final String LABEL_PROJECT_SETTINGS   = PreferenceMessages.getString("preferences.label.project.settings"); //$NON-NLS-1$
    public static final String LABEL_CONFIGURE_PROJECT  = PreferenceMessages.getString("preferences.label.configure.project"); //$NON-NLS-1$
    public static final String LABEL_CONFIGURE_WOKSPACE = PreferenceMessages.getString("preferences.label.configure.workspace"); //$NON-NLS-1$

    // --------------------------------------
    // labels for templates preferences page
    // --------------------------------------

    public static final String TEMPLATES_PAGE_DESCRIPTION = PreferenceMessages.getString("preferences.templates.page.description"); //$NON-NLS-1$

    public static final String TEMPLATES_LABEL_TYPES      = PreferenceMessages.getString("preferences.label.templates.types"); //$NON-NLS-1$
    public static final String TEMPLATES_LABEL_FIELDS     = PreferenceMessages.getString("preferences.label.templates.fields"); //$NON-NLS-1$
    public static final String TEMPLATES_LABEL_METHODS    = PreferenceMessages.getString("preferences.label.templates.methods"); //$NON-NLS-1$
    public static final String TEMPLATES_LABEL_PARAMETERS = PreferenceMessages.getString("preferences.label.templates.parameters"); //$NON-NLS-1$
    public static final String TEMPLATES_LABEL_EXCEPTIONS = PreferenceMessages.getString("preferences.label.templates.exceptions"); //$NON-NLS-1$

    public static final String TEMPLATES_MENU_LABEL_NESTED     = PreferenceMessages.getString("preferences.label.templates.menu.nested"); //$NON-NLS-1$
    public static final String TEMPLATES_MENU_LABEL_TYPES      = PreferenceMessages.getString("preferences.label.templates.menu.types"); //$NON-NLS-1$
    public static final String TEMPLATES_MENU_LABEL_FIELDS     = PreferenceMessages.getString("preferences.label.templates.menu.fields"); //$NON-NLS-1$
    public static final String TEMPLATES_MENU_LABEL_METHODS    = PreferenceMessages.getString("preferences.label.templates.menu.methods"); //$NON-NLS-1$
    public static final String TEMPLATES_MENU_LABEL_PARAMETERS = PreferenceMessages.getString("preferences.label.templates.menu.parameters"); //$NON-NLS-1$
    public static final String TEMPLATES_MENU_LABEL_EXCEPTIONS = PreferenceMessages.getString("preferences.label.templates.menu.exceptions"); //$NON-NLS-1$

    // --------------------------------
    // labels edit template dialog
    // --------------------------------

    public static final String TEMPLATES_DLG_HEADING_GROUPS       = PreferenceMessages.getString("preferences.templatedlg.headline.groups"); //$NON-NLS-1$
    public static final String TEMPLATES_DLG_HEADING_PARENTGROUPS = PreferenceMessages.getString("preferences.templatedlg.headline.parentgroups"); //$NON-NLS-1$
    public static final String TEMPLATES_DLG_NOMATCH              = PreferenceMessages.getString("preferences.templatedlg.nomatch"); //$NON-NLS-1$

    // --------------------------------
    // labels header dialog
    // --------------------------------

    public static final String  HEADER_TITLE       = PreferenceMessages.getString("preferences.headerdlg.title"); //$NON-NLS-1$
    public static final String  HEADER_HINT        = PreferenceMessages.getString("preferences.headerdlg.hint"); //$NON-NLS-1$
    public static final String  HEADER_ERROR_TITLE = PreferenceMessages.getString("preferences.headerdlg.error.title"); //$NON-NLS-1$
    public static final String  HEADER_ERROR_MSG   = PreferenceMessages.getString("preferences.headerdlg.error.message"); //$NON-NLS-1$

    // --------------------------------
    // labels package doc dialog
    // --------------------------------

    public static final String  PKG_DOC_TITLE = PreferenceMessages.getString("preferences.pkgdocdlg.title"); //$NON-NLS-1$
    public static final String  PKG_DOC_HINT  = PreferenceMessages.getString("preferences.pkgdocdlg.hint"); //$NON-NLS-1$

    // --------------------------------
    // labels for replacement block
    // --------------------------------

    public static final String  COLUMN_SHORTCUT     = PreferenceMessages.getString("preferences.column.shortcut"); //$NON-NLS-1$
    public static final String  COLUMN_REPLACEMENT  = PreferenceMessages.getString("preferences.column.replacement"); //$NON-NLS-1$
    public static final String  COLUMN_SCOPE        = PreferenceMessages.getString("preferences.column.scope"); //$NON-NLS-1$
    public static final String  COLUMN_SCOPE_METHOD = PreferenceMessages.getString("preferences.column.scope.method"); //$NON-NLS-1$
    public static final String  COLUMN_SCOPE_FIELD  = PreferenceMessages.getString("preferences.column.scope.field"); //$NON-NLS-1$
    public static final String  COLUMN_SCOPE_BOTH   = PreferenceMessages.getString("preferences.column.scope.both"); //$NON-NLS-1$
    public static final String  COLUMN_MODE         = PreferenceMessages.getString("preferences.column.mode"); //$NON-NLS-1$
    public static final String  COLUMN_MODE_PREFIX  = PreferenceMessages.getString("preferences.column.mode.prefix"); //$NON-NLS-1$
    public static final String  COLUMN_MODE_ALL     = PreferenceMessages.getString("preferences.column.mode.all"); //$NON-NLS-1$

    public static final String  LABEL_SHORTCUT     = PreferenceMessages.getString("preferences.label.shortcut"); //$NON-NLS-1$
    public static final String  LABEL_REPLACEMENT  = PreferenceMessages.getString("preferences.label.replacement"); //$NON-NLS-1$
    public static final String  LABEL_SCOPE        = PreferenceMessages.getString("preferences.label.scope"); //$NON-NLS-1$
    public static final String  LABEL_REPLACE_MODE = PreferenceMessages.getString("preferences.label.replmode"); //$NON-NLS-1$
    public static final String  LABEL_TABLE        = PreferenceMessages.getString("preferences.label.table"); //$NON-NLS-1$

    public static final String  TITLE_ADD_REPLACEMENT       = PreferenceMessages.getString("preferences.label.title.addreplacement"); //$NON-NLS-1$
    public static final String  TITLE_EDIT_REPLACEMENT      = PreferenceMessages.getString("preferences.label.title.editreplacement"); //$NON-NLS-1$
    public static final String  TITLE_OVERWRITE_REPLACEMENT = PreferenceMessages.getString("preferences.label.title.overwritereplacement"); //$NON-NLS-1$

    public static final String  TITLE_JDOC_TASK   = PreferenceMessages.getString("preferences.label.title.jdoctask"); //$NON-NLS-1$
    public static final String  TITLE_HEADER_TASK = PreferenceMessages.getString("preferences.label.title.headertask"); //$NON-NLS-1$

    public static final String  QUESTION_OVERWRITE_REPLACEMENT =
        PreferenceMessages.getString("preferences.label.question.overwritereplacement"); //$NON-NLS-1$

    public static final String  BUTTON_ADD    = PreferenceMessages.getString("preferences.button.add"); //$NON-NLS-1$
    public static final String  BUTTON_EDIT   = PreferenceMessages.getString("preferences.button.edit"); //$NON-NLS-1$
    public static final String  BUTTON_REMOVE = PreferenceMessages.getString("preferences.button.remove"); //$NON-NLS-1$

    // --------------------------------
    // javadoc fragments
    // --------------------------------

    public static final String  JDOC_CLASS       = PreferenceMessages.getString("preferences.javadoc.class"); //$NON-NLS-1$
    public static final String  JDOC_INTERFACE   = PreferenceMessages.getString("preferences.javadoc.interface"); //$NON-NLS-1$
    public static final String  JDOC_ENUM        = PreferenceMessages.getString("preferences.javadoc.enum"); //$NON-NLS-1$
    public static final String  JDOC_CONSTRUCTOR = PreferenceMessages.getString("preferences.javadoc.constructor"); //$NON-NLS-1$
    public static final String  JDOC_MAIN        = PreferenceMessages.getString("preferences.javadoc.main"); //$NON-NLS-1$
    public static final String  JDOC_CONSTANT    = PreferenceMessages.getString("preferences.javadoc.constant"); //$NON-NLS-1$
    public static final String  JDOC_THE_UPPER   = PreferenceMessages.getString("preferences.javadoc.the.upper"); //$NON-NLS-1$
    public static final String  JDOC_THE_LOWER   = PreferenceMessages.getString("preferences.javadoc.the.lower"); //$NON-NLS-1$
    public static final String  JDOC_TRUE_IF     = PreferenceMessages.getString("preferences.javadoc.true.if"); //$NON-NLS-1$

    // --------------------------------
    // labels for cleanup/save actions
    // --------------------------------

    public static final String CLEANUP_JAVADOC_STEP_LABEL    = PreferenceMessages.getString("cleanup.label.javadoc_step"); //$NON-NLS-1$
    public static final String CLEANUP_ADD_HEADER_STEP_LABEL = PreferenceMessages.getString("cleanup.label.add_header_step"); //$NON-NLS-1$
    public static final String CLEANUP_REP_HEADER_STEP_LABEL = PreferenceMessages.getString("cleanup.label.replace_header_step"); //$NON-NLS-1$
    public static final String CLEANUP_ADD_HEADER_HINT_LABEL = PreferenceMessages.getString("cleanup.label.add_header_hint"); //$NON-NLS-1$
    public static final String CLEANUP_REP_HEADER_WARN_LABEL = PreferenceMessages.getString("cleanup.label.replace_header_warn"); //$NON-NLS-1$

    public static final String CLEANUP_PREVIEW_JAVADOC_DISABLED =
        "package net.sf.jautodoc.example;" + LINE_SEPARATOR +
        LINE_SEPARATOR +
        "public class Example {" + LINE_SEPARATOR +
        LINE_SEPARATOR +
        "    private int numberOfQuestions;" + LINE_SEPARATOR +
        LINE_SEPARATOR +
        "    public int getNumberOfQuestions() {" + LINE_SEPARATOR +
        "        return numberOfQuestions;" + LINE_SEPARATOR +
        "    }" + LINE_SEPARATOR +
        LINE_SEPARATOR +
        "    public void setNumberOfQuestions(int numberOfQuestions) {" + LINE_SEPARATOR +
        "        this.numberOfQuestions = numberOfQuestions;" + LINE_SEPARATOR +
        "    }" + LINE_SEPARATOR +
        "}";

    public static final String CLEANUP_PREVIEW_JAVADOC_ENABLED =
        "package net.sf.jautodoc.example;" + LINE_SEPARATOR +
        LINE_SEPARATOR +
        "/**" + LINE_SEPARATOR +
        " * The Example class." + LINE_SEPARATOR +
        " */" + LINE_SEPARATOR +
        "public class Example {" + LINE_SEPARATOR +
        LINE_SEPARATOR +
        "    /** The number of questions. */" + LINE_SEPARATOR +
        "    private int numberOfQuestions;" + LINE_SEPARATOR +
        LINE_SEPARATOR +
        "    /**" + LINE_SEPARATOR +
        "     * Gets the number of questions." + LINE_SEPARATOR +
        "     *" + LINE_SEPARATOR +
        "     * @return the number of questions" + LINE_SEPARATOR +
        "     */" + LINE_SEPARATOR +
        "    public int getNumberOfQuestions() {" + LINE_SEPARATOR +
        "        return numberOfQuestions;" + LINE_SEPARATOR +
        "    }" + LINE_SEPARATOR +
        LINE_SEPARATOR +
        "    /**" + LINE_SEPARATOR +
        "     * Sets the number of questions." + LINE_SEPARATOR +
        "     *" + LINE_SEPARATOR +
        "     * @param numberOfQuestions the new number of questions" + LINE_SEPARATOR +
        "     */" + LINE_SEPARATOR +
        "    public void setNumberOfQuestions(int numberOfQuestions) {" + LINE_SEPARATOR +
        "        this.numberOfQuestions = numberOfQuestions;" + LINE_SEPARATOR +
        "    }" + LINE_SEPARATOR +
        "}";
}
