/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

/**
 * Selectable preference types for import and export of JAutodoc overall configuration via JAXB.
 */
public enum PreferenceType {
    OPTIONS("Options", "Mode, Visibility, Filter and other Options", "options.gif"),
    REPLACEMENTS("Replacements", "Replacements for comment from element name", "replacements.gif"),
    PROPERTIES("Properties", "User defined properties for all templates", "properties.gif"),
    TEMPLATES("Templates", "Templates for auto-generated Javadoc", "templates.gif"),
    HEADERTEXT("File Header Template", "Template for file headers", "file_header.gif"),
    PACKAGEDOCTEXT("Template for package.html", "Package Javadoc Template", "packagedoc.gif"),
    PACKAGEINFOTEXT("Template for package-info.java", "Package Javadoc Template", "packageinfo.gif");

    private final String title;
    private final String description;
    private final String imageFileName;

    private PreferenceType(String title, String description, String imageFileName) {
        this.title = title;
        this.description = description;
        this.imageFileName = imageFileName;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageFileName() {
        return imageFileName;
    }
}