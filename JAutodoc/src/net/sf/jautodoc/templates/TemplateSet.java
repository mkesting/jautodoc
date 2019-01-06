/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Set of template entries.
 */
@XmlType(propOrder = { "typeTemplates", "fieldTemplates", "methodTemplates", "parameterTemplates", "exceptionTemplates" })
public class TemplateSet {
	private List<TemplateEntry> typeTemplates;
	private List<TemplateEntry> methodTemplates;
	private List<TemplateEntry> fieldTemplates;
	private List<TemplateEntry> parameterTemplates;
	private List<TemplateEntry> exceptionTemplates;
	
	
	/**
	 * Instantiates a new template set.
	 */
	public TemplateSet() {
		typeTemplates   	= new ArrayList<TemplateEntry>();
		methodTemplates 	= new ArrayList<TemplateEntry>();
		fieldTemplates  	= new ArrayList<TemplateEntry>();
		parameterTemplates  = new ArrayList<TemplateEntry>();
		exceptionTemplates  = new ArrayList<TemplateEntry>();
	}
	
	/**
	 * Gets the type templates.
	 * 
	 * @return the type templates
	 */
    @XmlElement(name = "typeTemplate")
	public List<TemplateEntry> getTypeTemplates() {
		return typeTemplates;
	}

	/**
	 * Gets the field templates.
	 * 
	 * @return the field templates
	 */
    @XmlElement(name = "fieldTemplate")
	public List<TemplateEntry> getFieldTemplates() {
		return fieldTemplates;
	}

	/**
	 * Gets the method templates.
	 * 
	 * @return the method templates
	 */
    @XmlElement(name = "methodTemplate")
	public List<TemplateEntry> getMethodTemplates() {
		return methodTemplates;
	}
	
	/**
	 * Gets the parameter templates.
	 * 
	 * @return the parameter templates
	 */
    @XmlElement(name = "parameterTemplate")
	public List<TemplateEntry> getParameterTemplates() {
		return parameterTemplates;
	}
	
	/**
	 * Gets the exception templates.
	 * 
	 * @return the exception templates
	 */
    @XmlElement(name = "exceptionTemplate")
	public List<TemplateEntry> getExceptionTemplates() {
		return exceptionTemplates;
	}

	/**
	 * Sets the field templates.
	 * 
	 * @param fieldTemplates the new field templates
	 */
	public void setFieldTemplates(List<TemplateEntry> fieldTemplates) {
		this.fieldTemplates = fieldTemplates;
	}

	/**
	 * Sets the method templates.
	 * 
	 * @param methodTemplates the new method templates
	 */
	public void setMethodTemplates(List<TemplateEntry> methodTemplates) {
		this.methodTemplates = methodTemplates;
	}

	/**
	 * Sets the type templates.
	 * 
	 * @param typeTemplates the new type templates
	 */
	public void setTypeTemplates(List<TemplateEntry> typeTemplates) {
		this.typeTemplates = typeTemplates;
	}
	
	/**
	 * Sets the parameter templates.
	 * 
	 * @param parameterTemplates the new parameter templates
	 */
	public void setParameterTemplates(List<TemplateEntry> parameterTemplates) {
		this.parameterTemplates = parameterTemplates;
	}
	
	/**
	 * Sets the exception templates.
	 * 
	 * @param exceptionTemplates the new exception templates
	 */
	public void setExceptionTemplates(List<TemplateEntry> exceptionTemplates) {
		this.exceptionTemplates = exceptionTemplates;
	}

	/**
	 * Adds a template entry.
	 * 
	 * @param entry the new template entry
	 */
	public void addTemplate(TemplateEntry entry) {
		List<TemplateEntry> templates = null;
		if (entry.isType()) {
			templates = getTypeTemplates();
		}
		else if (entry.isField()) {
			templates = getFieldTemplates();
		}
		else if (entry.isMethod()) {
			templates = getMethodTemplates();
		}
		else if (entry.isParameter()) {
			templates = getParameterTemplates();
		}
		else if (entry.isException()) {
			templates = getExceptionTemplates();
		}
		
		templates.add(entry);
	}
	
	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return  typeTemplates.isEmpty() &&
				fieldTemplates.isEmpty() &&
				methodTemplates.isEmpty() &&
				parameterTemplates.isEmpty() &&
				exceptionTemplates.isEmpty();
	}
	
	public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (parent instanceof TemplateEntry) {
            initParent(typeTemplates, (TemplateEntry)parent);
            initParent(fieldTemplates, (TemplateEntry)parent);
            initParent(methodTemplates, (TemplateEntry)parent);
            initParent(parameterTemplates, (TemplateEntry)parent);
            initParent(exceptionTemplates, (TemplateEntry)parent);
        }
    }
	
	private void initParent(List<TemplateEntry> templates, TemplateEntry parent) {
	    for (TemplateEntry template : templates) {
	        template.setParent(parent);
        }
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer tmp = new StringBuffer();
		tmp.append("\n------------------------------------------------------------\n");
		for (int i = 0; i < typeTemplates.size(); ++i) {
			tmp.append(typeTemplates.get(i));
			tmp.append("\n------------------------------------------------------------\n");
		}
		
		tmp.append("\n************************************************************\n");
		for (int i = 0; i < fieldTemplates.size(); ++i) {
			tmp.append(fieldTemplates.get(i));
			tmp.append("\n************************************************************\n");
		}
		
		tmp.append("\n############################################################\n");
		for (int i = 0; i < methodTemplates.size(); ++i) {
			tmp.append(methodTemplates.get(i));
			tmp.append("\n############################################################\n");
		}
		
		tmp.append("\n............................................................\n");
		for (int i = 0; i < parameterTemplates.size(); ++i) {
			tmp.append(parameterTemplates.get(i));
			tmp.append("\n............................................................\n");
		}
		
		tmp.append("\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n");
		for (int i = 0; i < exceptionTemplates.size(); ++i) {
			tmp.append(exceptionTemplates.get(i));
			tmp.append("\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n");
		}
		
		return tmp.toString();
	}
}
