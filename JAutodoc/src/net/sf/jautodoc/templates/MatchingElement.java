/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.templates;

import java.util.regex.Matcher;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.templates.wrapper.AnnotationWrapper;
import net.sf.jautodoc.templates.wrapper.CharacterWrapper;
import net.sf.jautodoc.templates.wrapper.IMemberWrapper;
import net.sf.jautodoc.templates.wrapper.StringWrapper;

import org.eclipse.jdt.core.JavaModelException;


/**
 * Class for matching elements. A matching element combines the current
 * member, the related template entry and the corresponding matchers.
 */
public class MatchingElement {
    private MatchingElement parent;
    private Matcher matcher;
    private IMemberWrapper member;
    private TemplateEntry entry;


    /**
     * Instantiates a new matching element.
     *
     * @param member the member
     * @param entry the entry
     * @param matcher the matcher
     * @param parent the parent matching element
     */
    public MatchingElement( IMemberWrapper member,
                            TemplateEntry entry,
                            Matcher matcher,
                            MatchingElement parent) {
        this.member = member;
        this.entry = entry;
        this.parent = parent;
        this.matcher = matcher;
    }

    /**
     * Gets the matching group with the given index.
     *
     * @param i the index
     * @return the string wrapper for this group
     */
    public StringWrapper group(int i) {
        if (0 <= i && i <= matcher.groupCount()) {
            return new StringWrapper(removeGenericPart(matcher.group(i)),
                    entry != null ? entry.getKind() : ITemplateKinds.UNKNOWN);
        }
        return null;
    }

    /**
     * Gets the parent of this matching element.
     *
     * @return the parent matching element
     */
    public MatchingElement parent() {
        return parent;
    }

    /**
     * Gets the type of the related member.
     *
     * @return the type
     */
    public StringWrapper getType() {
        String type = "<type>";
        try {
            if (member != null) {
                type = member.getType();
            }
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(e);
        }

        return new StringWrapper(type, ITemplateKinds.UNKNOWN);
    }

    /**
     * Gets the declaring type of the related member.
     *
     * @return the declaring type
     */
    public StringWrapper getDeclaringType() {
        String declaringType = "<declaring type>";
        try {
            if (member != null) {
                declaringType = member.getDeclaringType();
            }
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(e);
        }

        return new StringWrapper(declaringType, ITemplateKinds.UNKNOWN);
    }


    /**
     * Checks if the related member is static.
     *
     * @return true, if is static
     */
    public boolean isStatic() {
        try {
            if (member != null) {
                return member.isStatic();
            }
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(e);
        }

        return false;
    }

    /**
     * Checks if the related member is final.
     *
     * @return true, if is final
     */
    public boolean isFinal() {
        try {
            if (member != null) {
                return member.isFinal();
            }
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(e);
        }

        return false;
    }

    /**
     * Checks if the related member is a constructor.
     *
     * @return true, if is constructor
     */
    public boolean isConstructor() {
        try {
            if (member != null) {
                return member.isConstructor();
            }
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(e);
        }

        return false;
    }

    /**
     * Checks if the related member is the main method.
     *
     * @return true, if is main method
     */
    public boolean isMainMethod() {
        try {
            if (member != null) {
                return member.isMainMethod();
            }
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(e);
        }

        return false;
    }

    /**
     * Checks if annotation whith the given name exists.
     *
     * @param name the name
     * @return true, if annotation exists
     */
    public boolean hasAnnotation(final String name) {
        return getAnnotation(name).exists();
    }

    /**
     * Gets the annotation whith the given name.
     *
     * @param name the name
     * @return the annotation
     */
    public AnnotationWrapper getAnnotation(final String name) {
        try {
            if (member != null) {
                return member.getAnnotation(name);
            }
        } catch (JavaModelException e) {
            JAutodocPlugin.getDefault().handleException(e);
        }
        return new AnnotationWrapper();
    }

    /**
     * Gets the related template entry.
     *
     * @return the template entry
     */
    public TemplateEntry getEntry() {
        return entry;
    }

    /**
     * Get child templates of the related template entry.
     *
     * @return the child templates
     */
    public TemplateSet getChildTemplates() {
        return entry.getChildTemplates();
    }

    /**
     * Gets the related member.
     *
     * @return the member
     */
    public IMemberWrapper getMember() {
        return member;
    }

    /**
     * Get char of matching string at the given index.
     *
     * @param index the index
     *
     * @return the character wrapper
     */
    public CharacterWrapper charAt(int index) {
        return g(0).charAt(index);
    }

    /**
     * Get length of the matching string.
     *
     * @return the int
     */
    public int length() {
        return g(0).length();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return matcher.group();
    }

    // shortcuts

    /**
     * Gets the matching group with the given index.
     *
     * @param i the index
     * @return the string wrapper for this group
     */
    public StringWrapper g(int i) {
        return group(i);
    }

    /**
     * Gets the parent of this matching element.
     *
     * @return the parent matching element
     */
    public MatchingElement p() {
        return parent();
    }

    /**
     * First of the matching string to lower.
     *
     * @return the modified string
     */
    public StringWrapper fl() {
        return g(0).firstToLower();
    }

    /**
     * First of the matching string to upper.
     *
     * @return the modified string
     */
    public StringWrapper fu() {
        return g(0).firstToUpper();
    }

    /**
     * Matching string to upper.
     *
     * @return the modified string
     */
    public StringWrapper u() {
        return g(0).toUpper();
    }

    /**
     * Matching string to lower.
     *
     * @return the modified string
     */
    public StringWrapper l() {
        return g(0).toLower();
    }

    /**
     * Split matching string.
     *
     * @return the modified string
     */
    public StringWrapper s() {
        return g(0).s();
    }

    /**
     * Split matching string and first to upper.
     *
     * @return the modified string
     */
    public StringWrapper sfu() {
        return g(0).sfu();
    }

    /**
     * Split matching string and first to lower.
     *
     * @return the modified string
     */
    public StringWrapper sfl() {
        return g(0).sfl();
    }

    /**
     * Replace prefix of the matching string.
     *
     * @return the modified string
     */
    public StringWrapper r() {
        return g(0).r();
    }

    /**
     * Replace prefix and split matching string.
     *
     * @return the modified string
     */
    public StringWrapper rs() {
        return g(0).rs();
    }

    /**
     * Replace prefix, split matching string and first to upper.
     *
     * @return the modified string
     */
    public StringWrapper rsfu() {
        return g(0).rsfu();
    }

    /**
     * Replace prefix, split matching string and first to lower.
     *
     * @return the modified string
     */
    public StringWrapper rsfl() {
        return g(0).rsfl();
    }

    private String removeGenericPart(final String string) {
        return string == null ? "" : string.replaceAll("<.*>", "");
    }
}
