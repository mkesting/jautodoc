/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.source;

import org.eclipse.jdt.core.ISourceRange;

/**
 * Implementation of ISourceRange.
 */
public class SourceRange implements ISourceRange {

    private final int offset;
    private final int length;

    /**
     * Instantiates a new source range.
     *
     * @param offset the offset
     * @param length the length
     */
    public SourceRange(final int offset, final int length) {
        this.offset = offset;
        this.length = length;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.ISourceRange#getLength()
     */
    public int getLength() {
        return this.length;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.core.ISourceRange#getOffset()
     */
    public int getOffset() {
        return this.offset;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.length ^ this.offset;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object obj) {
        if (!(obj instanceof ISourceRange)) {
            return false;
        }
        final ISourceRange sourceRange = (ISourceRange) obj;
        return sourceRange.getOffset() == this.offset && sourceRange.getLength() == this.length;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[offset="); //$NON-NLS-1$
        buffer.append(this.offset);
        buffer.append(", length="); //$NON-NLS-1$
        buffer.append(this.length);
        buffer.append("]"); //$NON-NLS-1$
        return buffer.toString();
    }
}
