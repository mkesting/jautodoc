/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import net.sf.jautodoc.JAutodocPlugin;
import net.sf.jautodoc.search.TaskSearchMatch.Finding;
import net.sf.jautodoc.search.TaskSearchMatch.FindingId;

import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchFilter;

/**
 * Enables filtering of the search result view by type of findings.
 */
public class TaskSearchMatchFilter extends MatchFilter {
    public static MatchFilter[] NO_FILTERS = new TaskSearchMatchFilter[0];

    private static final String SETTINGS_LAST_USED_FILTERS= "filters_last_used";  //$NON-NLS-1$

    private static final TaskSearchMatchFilter[] ALL_FILTERS = {
        new TaskSearchMatchFilter(FindingId.MISSING_JAVADOC),
        new TaskSearchMatchFilter(FindingId.MISSING_PARAM),
        new TaskSearchMatchFilter(FindingId.INVALID_PARAM),
        new TaskSearchMatchFilter(FindingId.MISSING_RETURN),
        new TaskSearchMatchFilter(FindingId.MISSING_THROWS),
        new TaskSearchMatchFilter(FindingId.INVALID_THROWS),
        new TaskSearchMatchFilter(FindingId.MISSING_PERIOD),
        new TaskSearchMatchFilter(FindingId.TODO_FOR_AUTODOC),
        new TaskSearchMatchFilter(FindingId.GENERATED_JAVADOC),
        new TaskSearchMatchFilter(FindingId.MISSING_HEADER),
        new TaskSearchMatchFilter(FindingId.OUTDATED_HEADER),
        new TaskSearchMatchFilter(FindingId.MISSING_TAG)
    };

    private final FindingId findingId;


    public TaskSearchMatchFilter(FindingId findingId) {
        this.findingId = findingId;
    }

    public static TaskSearchMatchFilter[] getAllFilters() {
        return ALL_FILTERS;
    }

    public static MatchFilter[] getLastUsedFilters() {
        final String string = JAutodocPlugin.getDefault().getDialogSettings().get(SETTINGS_LAST_USED_FILTERS);
        if (string != null && string.length() > 0) {
            return decodeFilters(string);
        }
        return NO_FILTERS;
    }

    public static void setLastUsedFilters(MatchFilter[] filters) {
        String encoded = encodeFilters(filters);
        JAutodocPlugin.getDefault().getDialogSettings().put(SETTINGS_LAST_USED_FILTERS, encoded);
    }

    @Override
    public boolean filters(final Match match) {
        // match is filtered, when all findings are filtered
        final TaskSearchMatch taskSearchMatch = (TaskSearchMatch)match;
        return taskSearchMatch.isFiltered();
    }

    public boolean filters(final Finding finding) {
        return finding.getId() == findingId;
    }

    @Override
    public String getID() {
        return findingId.name();
    }

    @Override
    public String getName() {
        return findingId.getLabel();
    }

    @Override
    public String getActionLabel() {
        return findingId.getLabel();
    }

    @Override
    public String getDescription() {
        return findingId.getLabel();
    }

    private static String encodeFilters(final MatchFilter[] enabledFilters) {
        final StringBuffer buf = new StringBuffer();
        for (MatchFilter matchFilter : enabledFilters) {
            buf.append(matchFilter.getID());
            buf.append(';');
        }
        return buf.toString();
    }

    private static TaskSearchMatchFilter[] decodeFilters(final String encodedString) {
        final Set<TaskSearchMatchFilter> result = new HashSet<TaskSearchMatchFilter>();
        final StringTokenizer tokenizer = new StringTokenizer(encodedString, String.valueOf(';'));

        while (tokenizer.hasMoreTokens()) {
            final TaskSearchMatchFilter curr = findMatchFilter(tokenizer.nextToken());
            if (curr != null) {
                result.add(curr);
            }
        }
        return result.toArray(new TaskSearchMatchFilter[result.size()]);
    }

    private static TaskSearchMatchFilter findMatchFilter(final String id) {
        for (int i = 0; i < ALL_FILTERS.length; i++) {
            final TaskSearchMatchFilter matchFilter = ALL_FILTERS[i];
            if (matchFilter.getID().equals(id))
                return matchFilter;
        }
        return null;
    }
}
