/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.search.ui.text.Match;

/**
 * A match includes a list of findings with the same source range per element.
 */
public class TaskSearchMatch extends Match {

    private final List<Finding> findings = new ArrayList<Finding>();

    public TaskSearchMatch(final IJavaElement element, final int offset, final int length,
            final FindingId id, final String message) {

        super(element, offset, length);
        this.findings.add(new Finding(this, id, message));
    }

    public void addFinding(final FindingId id, final String message) {
        findings.add(new Finding(this, id, message));
    }

    public Finding[] getFindings() {
        return findings.toArray(new Finding[findings.size()]);
    }

    public Finding[] getFilteredFindings() {
        final List<Finding> filteredFindings = new ArrayList<Finding>();
        for (Finding finding : findings) {
            if (!finding.isFiltered()) {
                filteredFindings.add(finding);
            }
        }
        return filteredFindings.toArray(new Finding[filteredFindings.size()]);
    }

    public int getFindingCount() {
        return findings.size();
    }

    @Override
    public boolean isFiltered() {
        for (Finding finding : findings) {
            if (!finding.isFiltered()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setFiltered(boolean value) {
        // ignored
    }

    // ------------------------------------------------------------------------
    // inner classes
    // ------------------------------------------------------------------------

    public enum FindingId {
        MISSING_JAVADOC("Missing Javadoc"),
        MISSING_PARAM("Missing param tag"),
        INVALID_PARAM("Invalid param tag"),
        MISSING_RETURN("Missing return tag"),
        MISSING_THROWS("Missing throws tag"),
        INVALID_THROWS("Invalid throws tag"),
        MISSING_PERIOD("Missing period"),
        TODO_FOR_AUTODOC("ToDo for generated Javadoc"),
        GENERATED_JAVADOC("Generated Javadoc"),
        MISSING_HEADER("Missing file header"),
        OUTDATED_HEADER("Outdated file header"),
        MISSING_TAG("Missing or empty tag");

        private final String label;

        private FindingId(final String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public static class Finding {
        private final Match match;
        private final FindingId id;
        private final String message;

        private boolean filtered;

        public Finding(final Match match, final FindingId id, final String message) {
            this.id = id;
            this.match = match;
            this.message = message;
        }

        public FindingId getId() {
            return id;
        }

        public Match getMatch() {
            return match;
        }

        public String getMessage() {
            return message;
        }

        public boolean isFiltered() {
            return filtered;
        }

        public void setFiltered(boolean filtered) {
            this.filtered = filtered;
        }

        @Override
        public String toString() {
            return id.name()+ ": " + message;
        }
    }
}
