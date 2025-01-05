/*******************************************************************
 * Copyright (c) 2006 - 2025, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.jautodoc.preferences.Constants;
import net.sf.jautodoc.utils.StringUtils;
import net.sf.jautodoc.utils.Utils;

/**
 * Parses a given Javadoc string and provides the parsed informations.
 */
public class JavadocInfo {

    private enum State {
        STATE_TEXT, STATE_TAGS
    }

    private State state = State.STATE_TEXT;

    private boolean markdown;

    private String type = "";
    private String name = "";

    private List<String> currentText = new ArrayList<String>();

    private final List<String> comment      = new ArrayList<String>();
    private final List<String> returnDoc    = new ArrayList<String>();
    private final List<JavadocTag> otherDoc = new ArrayList<JavadocTag>();

    private final Map<String, JavadocTag> paramDoc  = new LinkedHashMap<String, JavadocTag>();
    private final Map<String, JavadocTag> throwsDoc = new LinkedHashMap<String, JavadocTag>();


    public boolean isMarkdown() {
        return markdown;
    }

    public void setMarkdown(boolean markdown) {
        this.markdown = markdown;
    }

    /**
     * Get all tag comments.
     *
     * @param parameterNames all valid parameter names
     * @param exceptionTypes all valid exception types
     * @return all tag comments
     */
    public List<JavadocTag> getAllTagComments(final String[] parameterNames, final String[] exceptionTypes) {
        final List<JavadocTag> tagComments = new ArrayList<JavadocTag>();

        for (final String paramName : parameterNames) {
            final JavadocTag paramTag = paramDoc.get(paramName);
            if (paramTag != null) {
                tagComments.add(paramTag);
            }
        }

        if (!returnDoc.isEmpty()) {
            tagComments.add(new JavadocTag(JavadocTag.TAG_TYPE_RETURN, null, returnDoc));
        }

        for (final String exceptionType : exceptionTypes) {
            final JavadocTag throwsTag = throwsDoc.get(exceptionType);
            if (throwsTag != null) {
                tagComments.add(throwsTag);
            }
        }

        tagComments.addAll(otherDoc);

        return tagComments;
    }

    /**
     * Gets the exception Javadoc.
     *
     * @return the exception Javadoc
     */
    public Map<String, JavadocTag> getThrowsDoc() {
        return throwsDoc;
    }

    /**
     * Gets the parameter Javadoc.
     *
     * @return the parameter Javadoc
     */
    public Map<String, JavadocTag> getParamDoc() {
        return paramDoc;
    }

    /**
     * Gets the return Javadoc.
     *
     * @return the return Javadoc
     */
    public List<String> getReturnDoc() {
        return returnDoc;
    }

    /**
     * Gets the Javadoc comment.
     *
     * @return the comment as list
     */
    public List<String> getComment() {
        return comment;
    }

    /**
     * Gets the Javadoc of the other tags.
     *
     * @return Javadoc of the other tags
     */
    public List<JavadocTag> getOtherDoc() {
        return otherDoc;
    }

    /**
     * Checks if text contains the inheritDoc tag.
     *
     * @return true, if is inherited Javadoc
     */
    public boolean isInheritDoc() {
        boolean isInheritDoc = false;
        final Iterator<String> iter = comment.iterator();
        while (iter.hasNext()) {
            final String line = iter.next();
            if (line.indexOf(Constants.INHERIT_DOC_TAG) != -1) {
                isInheritDoc = true;
                break;
            }
        }
        return isInheritDoc;
    }

    /**
     * Checks if Javadoc is empty.
     *
     * @return true, if is empty
     */
    public boolean isEmpty() {
        return comment.isEmpty()   &&
               paramDoc.isEmpty()  &&
               returnDoc.isEmpty() &&
               throwsDoc.isEmpty() &&
               otherDoc.isEmpty();
    }

    /**
     * Checks if this doc has tags.
     *
     * @return true, if there are tags
     */
    public boolean hasTags() {
        return !paramDoc.isEmpty()  ||
               !returnDoc.isEmpty() ||
               !throwsDoc.isEmpty() ||
               !otherDoc.isEmpty();
    }

    /**
     * Checks if comment exists.
     *
     * @return true, if comment exists
     */
    public boolean hasComment() {
        return !comment.isEmpty();
    }

    public boolean containsPeriod() {
        for (final String string : comment) {
            if (string.indexOf(Constants.DOT) > -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses the Javadoc of the given buffer.
     *
     * @param buffer the buffer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public void parseJavadoc(final String buffer) throws IOException {
        final String input = buffer.replaceFirst("/\\*\\*[\\t ]*", "")  // remove /**
                                   .replaceFirst("\\s*\\*/\\s*",   "")  // remove */
                                   .replaceAll  ("\\n\\s*\\* ?", "\n")  // remove starting *
                                   .replaceAll  ("[\\t ]*/// ?",   ""); // remove starting ///
        String line = null;
        state = State.STATE_TEXT;
        markdown = buffer.startsWith("///");

        final BufferedReader br = new BufferedReader(new StringReader(input));
        while ((line = br.readLine()) != null) {
            if (!line.trim().startsWith("@")) {
                currentText.add(line);
            } else {
                endTag();
                startTag(line.trim());
            }
        }
        endTag();
    }

    /**
     * Merge this with the given Javadoc. Only non-existing comments will be added.
     *
     * @param jdi the jdi
     * @return this as the resulting javadoc info
     */
    public JavadocInfo merge(final JavadocInfo jdi) {
        merge(comment,   jdi.comment);
        merge(returnDoc, jdi.returnDoc);
        merge(otherDoc,  jdi.otherDoc);
        merge(paramDoc,  jdi.paramDoc);
        merge(throwsDoc, jdi.throwsDoc);
        return this;
    }

    private <E >void merge(final List<E> first, final List<E> second) {
        if (first.isEmpty() && !second.isEmpty()) {
            first.addAll(second);
        }
    }

    private void merge(final Map<String, JavadocTag> first, final Map<String, JavadocTag> second) {
        for (final JavadocTag secondTag : second.values()) {
            JavadocTag firstTag = first.get(secondTag.getName());
            if (firstTag == null) {
                firstTag = new JavadocTag(secondTag.getType(), secondTag.getName());
                first.put(firstTag.getName(), firstTag);
            }
            merge(firstTag.getComments(), secondTag.getComments());
        }
    }

    private void startTag(final String line) {
        name = "";
        state = State.STATE_TAGS;
        currentText = new ArrayList<String>();

        String[] tokens = line.split("\\s", 2);
        type = tokens[0];

        if (tokens.length > 1 && JavadocTag.isNamedTag(type)) {
            tokens = tokens[1].split("\\s", 2);
            name = tokens[0];
        }

        if (tokens.length > 1) {
            currentText.add(tokens[1]);
        }
    }

    private void endTag() {
        currentText = Utils.trimStringList(currentText);

        if (state == State.STATE_TEXT) {
            comment.addAll(currentText);

        } else {
            final JavadocTag javadocTag = new JavadocTag(type, name, currentText);

            if (javadocTag.isParamTag()) {
                if (!StringUtils.isEmpty(name)) {
                    paramDoc.put(name, javadocTag);
                }
            } else if (javadocTag.isThrowsTag()) {
                if (!StringUtils.isEmpty(name)) {
                    throwsDoc.put(name, javadocTag);
                }
            } else if (javadocTag.isReturnTag()) {
                returnDoc.addAll(currentText);

            } else {
                otherDoc.add(new JavadocTag(type, name, currentText));
            }
        }
    }
}
