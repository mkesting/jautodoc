/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a Javadoc tag.
 */
public class JavadocTag {

    public static final String TAG_TYPE_PARAM     = "@param";
    public static final String TAG_TYPE_RETURN    = "@return";
    public static final String TAG_TYPE_THROWS    = "@throws";
    public static final String TAG_TYPE_EXCEPTION = "@exception";

    private static final List<String> NAMED_TAG_TYPES = Arrays.asList(TAG_TYPE_PARAM, TAG_TYPE_THROWS, TAG_TYPE_EXCEPTION);


    private final String type;
    private final String name;

    private final List<String> comments;


    /**
     * Instantiates a new javadoc tag.
     *
     * @param type the type
     * @param name the name
     */
    public JavadocTag(final String type, final String name) {
        this(type, name, new ArrayList<String>(0));
    }

    /**
     * Instantiates a new javadoc tag.
     *
     * @param type the type
     * @param name the name
     * @param comments the comments
     */
    public JavadocTag(final String type, final String name, final List<String> comments) {
        this.type = type;
        this.name = name;
        this.comments = comments;
    }

    public static boolean isNamedTag(final String type) {
        return NAMED_TAG_TYPES.contains(type);
    }

    public boolean isParamTag() {
        return TAG_TYPE_PARAM.equals(getType());
    }

    public boolean isReturnTag() {
        return TAG_TYPE_RETURN.equals(getType());
    }

    public boolean isThrowsTag() {
        return TAG_TYPE_THROWS.equals(getType()) || TAG_TYPE_EXCEPTION.equals(getType());
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getTypeName() {
        return type.substring(1);
    }

    public List<String> getComments() {
        return comments;
    }

    public void addToJavadocString(final StringBuilder javadoc, final String indent, final String lineSeparator) {
        startNewLine(javadoc, indent);
        javadoc.append(type).append(" ");
        if (name != null && name.length() > 0) {
            javadoc.append(name).append(" ");
        }
        if (comments.size() > 0) {
            javadoc.append(comments.get(0));
        }
        javadoc.append(lineSeparator);

        for (int i = 1; i < comments.size(); ++i) {
            startNewLine(javadoc, indent);
            javadoc.append(comments.get(i));
            javadoc.append(lineSeparator);
        }
    }


    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("JavadocTag [type=");
        builder.append(type);
        builder.append(", name=");
        builder.append(name);
        builder.append(", comments=");
        builder.append(comments);
        builder.append("]");
        return builder.toString();
    }

    private void startNewLine(final StringBuilder javadoc, final String indent) {
        javadoc.append(indent);
        javadoc.append(" * ");
    }

    // ------------------------------------------------------------------------
    // Inner classes
    // ------------------------------------------------------------------------

    public static class TagComparator implements Comparator<JavadocTag> {

        private final List<String> tagOrder;
        private final List<String> paramOrder;
        private final List<String> throwsOrder;


        public TagComparator(final List<String> tagOrder, final List<String> paramOrder, final List<String> throwsOrder) {
            this.tagOrder = tagOrder;
            this.paramOrder = paramOrder;
            this.throwsOrder = throwsOrder;
        }

        @Override
        public int compare(final JavadocTag tag1, final JavadocTag tag2) {
            if (tag1.isParamTag() && tag2.isParamTag()) {
                return getIndex(tag1.getName(), paramOrder) - getIndex(tag2.getName(), paramOrder);
            }

            if (tag1.isThrowsTag() && tag2.isThrowsTag()) {
                return getIndex(tag1.getName(), throwsOrder) - getIndex(tag2.getName(), throwsOrder);
            }
            return getIndex(tag1.getType(), tagOrder) - getIndex(tag2.getType(), tagOrder);
        }

        private int getIndex(final String value, final List<String> valueOrder) {
            final int index = valueOrder.indexOf(value);
            return (index < 0) ? Integer.MAX_VALUE : index;
        }
    }
}
