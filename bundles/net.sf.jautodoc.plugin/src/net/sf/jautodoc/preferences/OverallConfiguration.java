/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.preferences;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import net.sf.jautodoc.preferences.replacements.Replacement;
import net.sf.jautodoc.templates.TemplateSet;

/**
 * Overall configuration values of JAutodoc. Used for import and export via JAXB.
 */
@XmlRootElement(name="jautodoc-preferences")
@XmlType(propOrder = { "configuration", "getSetFromFieldReplacements", "tagOrder", "properties",
        "replacements", "templates", "headerText", "packageDocText", "packageInfoText" })
public class OverallConfiguration {

    private Configuration configuration;

    private String headerText;
    private String packageDocText;
    private String packageInfoText;

    private List<String> tagOrder;
    private Map<String, String> properties;
    private List<Replacement> replacements;
    private Set<GetSetFromFieldReplacement> getSetFromFieldReplacements;

    private TemplateSet templates;

    @XmlElement(name="options")
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getPackageDocText() {
        return packageDocText;
    }

    public void setPackageDocText(String packageDocText) {
        this.packageDocText = packageDocText;
    }

    public String getPackageInfoText() {
        return packageInfoText;
    }

    public void setPackageInfoText(String packageInfoText) {
        this.packageInfoText = packageInfoText;
    }

    @XmlElementWrapper(name = "tagOrders")
    public List<String> getTagOrder() {
        return tagOrder;
    }

    public void setTagOrder(List<String> tagOrder) {
        this.tagOrder = tagOrder;
    }

    @XmlElement(name = "getSetFromFieldReplacement")
    @XmlElementWrapper(name = "getSetFromFieldReplacements")
    public Set<GetSetFromFieldReplacement> getGetSetFromFieldReplacements() {
        return getSetFromFieldReplacements;
    }

    public void setGetSetFromFieldReplacements(Set<GetSetFromFieldReplacement> getSetFromFieldReplacements) {
        this.getSetFromFieldReplacements = getSetFromFieldReplacements;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @XmlElement(name = "replacement")
    @XmlElementWrapper(name = "replacements")
    public List<Replacement> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<Replacement> replacements) {
        this.replacements = replacements;
    }

    public TemplateSet getTemplates() {
        return templates;
    }

    public void setTemplates(TemplateSet templates) {
        this.templates = templates;
    }
}
