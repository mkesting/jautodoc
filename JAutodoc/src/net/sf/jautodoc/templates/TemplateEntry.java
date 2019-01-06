/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;

import java.util.List;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * Entry of a template set representing a single template.
 */
@XmlRootElement(name="jautodoc-template")
@XmlType(propOrder = { "name", "useSignature", "defaultTemplate", "kind", "regex", "example", "text", "childTemplates" })
public class TemplateEntry {
	private int		  		kind;
	private String	  		name;
	private String	  		regex;
	private String	  		text;
	private String	  		example;
	private TemplateSet 	childTemplates;
	private Pattern   		pattern;
	private boolean	  		useSignature;
	private boolean			defaultTemplate;
	private TemplateEntry 	parent;


	/**
	 * Instantiates a new template entry.
	 */
	public TemplateEntry() {
		this(null);
	}

	/**
	 * Instantiates a new template entry.
	 *
	 * @param parent the parent template
	 * @param kind the kind of the template
	 */
	public TemplateEntry(TemplateEntry parent, int kind) {
		this(parent);
		this.kind = kind;
	}

	/**
	 * Instantiates a new template entry.
	 *
	 * @param parent the parent template
	 */
	public TemplateEntry(TemplateEntry parent) {
		this.parent = parent;
		childTemplates = new TemplateSet();
	}

	/**
	 * Gets the template kind.
	 *
	 * @return the kind
	 */
	@XmlAttribute
	public int getKind() {
		return kind;
	}

	/**
	 * Sets the template kind.
	 *
	 * @param kind the new kind
	 */
	public void setKind(int kind) {
		this.kind = kind;
	}

	/**
	 * Gets the name of the template.
	 *
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the template.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the regular expression.
	 *
	 * @return the regular expression
	 */
	public String getRegex() {
		return regex;
	}

	/**
	 * Sets the regular expression.
	 *
	 * @param regex the new regular expression
	 */
	public void setRegex(String regex) {
		this.regex = regex;
		this.pattern = null;
	}

	/**
	 * Gets the template text.
	 *
	 * @return the template text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the template text.
	 *
	 * @param text the new template text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * Gets the example for the related regular expression.
	 *
	 * @return the example
	 */
	public String getExample() {
		return example;
	}

	/**
	 * Sets the example for the related regular expression.
	 *
	 * @param example the new example
	 */
	public void setExample(String example) {
		this.example = example;
	}

	/**
	 * Checks if signature should be used as matching target.
	 *
	 * @return true, if is use signature
	 */
	@XmlAttribute
	public boolean isUseSignature() {
		return useSignature;
	}

	/**
	 * Sets the use signature.
	 *
	 * @param useSignature the new use signature
	 */
	public void setUseSignature(boolean useSignature) {
		this.useSignature = useSignature;
	}

	/**
	 * Gets the parent template.
	 *
	 * @return the parent
	 */
	@XmlTransient
	public TemplateEntry getParent() {
		return parent;
	}

	/**
	 * Sets the parent template.
	 *
	 * @param parent the new parent
	 */
	public void setParent(TemplateEntry parent) {
		this.parent = parent;
	}

	/**
	 * Checks if is default template.
	 *
	 * @return true, if is default template
	 */
	@XmlAttribute
	public boolean isDefaultTemplate() {
		return defaultTemplate;
	}

	/**
	 * Sets the default template.
	 *
	 * @param defaultTemplate the new default template
	 */
	public void setDefaultTemplate(boolean defaultTemplate) {
		this.defaultTemplate = defaultTemplate;
	}

	/**
	 * Gets the pattern compiled from the related regular expression.
	 *
	 * @return the pattern
	 */
	@XmlTransient
	public Pattern getPattern() {
		if (pattern == null) {
			pattern = Pattern.compile(regex);
		}
		return pattern;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	@XmlTransient
	public String getDescription() {
		String description = "";
		if (isType()) {
			description = "Type";
		}
		else if (isField()) {
			description = "Field";
		}
		else if (isMethod()) {
			description = "Method";
		}
		else if (isParameter()) {
			description = "Parameter";
		}
		else if (isException()) {
			description = "Exception";
		}

		return description;
	}

	/**
	 * Gets the child templates of the given template kind.
	 *
	 * @param templateKind the template kind
	 *
	 * @return the child templates
	 */
	public List<TemplateEntry> getChildTemplates(int templateKind) {
		List<TemplateEntry> list  = null;

		switch (templateKind) {
		case ITemplateKinds.TYPE:
			list = childTemplates.getTypeTemplates();
			break;
		case ITemplateKinds.FIELD:
			list = childTemplates.getFieldTemplates();
			break;
		case ITemplateKinds.METHOD:
			list = childTemplates.getMethodTemplates();
			break;
		case ITemplateKinds.PARAMETER:
			list = childTemplates.getParameterTemplates();
			break;
		case ITemplateKinds.EXCEPTION:
			list = childTemplates.getExceptionTemplates();
			break;
		}
		return list;
	}

	/**
	 * Gets the child templates.
	 *
	 * @return the child templates
	 */
	public TemplateSet getChildTemplates() {
		return childTemplates;
	}

	/**
	 * Sets the child templates.
	 *
	 * @param children the new child templates
	 */
	public void setChildTemplates(TemplateSet children) {
		this.childTemplates = children;
	}

	/**
	 * Checks if is type.
	 *
	 * @return true, if is type
	 */
	public boolean isType() {
		return kind == ITemplateKinds.TYPE;
	}

	/**
	 * Checks if is field.
	 *
	 * @return true, if is field
	 */
	public boolean isField() {
		return kind == ITemplateKinds.FIELD;
	}

	/**
	 * Checks if is method.
	 *
	 * @return true, if is method
	 */
	public boolean isMethod() {
		return kind == ITemplateKinds.METHOD;
	}

	/**
	 * Checks if is parameter.
	 *
	 * @return true, if is parameter
	 */
	public boolean isParameter() {
		return kind == ITemplateKinds.PARAMETER;
	}

	/**
	 * Checks if is exception.
	 *
	 * @return true, if is exception
	 */
	public boolean isException() {
		return kind == ITemplateKinds.EXCEPTION;
	}

	/**
	 * Adds a child template.
	 *
	 * @param entry the new child entry
	 */
	public void addChildTemplate(TemplateEntry entry) {
		List<TemplateEntry> templates = null;
		if (entry.isType()) {
			templates = childTemplates.getTypeTemplates();
		}
		else if (entry.isField()) {
			templates = childTemplates.getFieldTemplates();
		}
		else if (entry.isMethod()) {
			templates = childTemplates.getMethodTemplates();
		}
		else if (entry.isParameter()) {
			templates = childTemplates.getParameterTemplates();
		}
		else if (entry.isException()) {
			templates = childTemplates.getExceptionTemplates();
		}

		templates.add(entry);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer tmp = new StringBuffer();
		tmp.append("name: " + getName());
		tmp.append("\nkind: " + getKind());
		tmp.append("\nregex: " + getRegex());
		tmp.append("\nexample: " + getExample());
		tmp.append("\nsignature: " + isUseSignature());
		tmp.append("\ntext:\n" + getText());

		if (!childTemplates.isEmpty()) {
			tmp.append("\n++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
		}
		tmp.append(childTemplates.toString());

		return tmp.toString();
	}
}
