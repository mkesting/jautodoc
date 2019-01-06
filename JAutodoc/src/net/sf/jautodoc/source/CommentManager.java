/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.source;

import net.sf.jautodoc.preferences.Configuration;
import net.sf.jautodoc.preferences.replacements.Replacement;
import net.sf.jautodoc.preferences.replacements.ReplacementManager;
import net.sf.jautodoc.utils.StringUtils;

/**
 * Manages creation of comments from element name.
 */
public class CommentManager {
	public static final int NONE		= 0;

	public static final int TYPE		= 1;
	public static final int FIELD		= 2;
	public static final int METHOD		= 3;
	public static final int PARAMETER	= 4;
	public static final int RETURN		= 5;
	public static final int EXCEPTION	= 6;

	public static final int FIRST_TO_LOWER = 1;
	public static final int FIRST_TO_UPPER = 2;


	/**
	 * Creates the comment.
	 *
	 * @param config the configuration to use
	 * @param string the element name
	 * @param scope the scope
	 * @param split true, to split the string
	 * @param replace true, to do shortcut replacements
	 * @return the comment
	 */
    public static String createComment(final Configuration config, final String string, final int scope,
            final boolean split, final boolean replace) {
        return createComment(config, string, scope, split, replace, NONE);
    }

	/**
	 * Creates the comment.
	 *
	 * @param config the configuration to use
	 * @param string the element name
	 * @param scope the scope
	 * @param split true, to split the string
	 * @param replace true, to do shortcut replacements
	 * @param mode the mode to use (FIRST_TO_LOWER | FIRST_TO_UPPER)
	 * @return the comment
	 */
    public static String createComment(final Configuration config, final String string, final int scope,
            final boolean split, final boolean replace, final int mode) {
		if (!split && !replace) return applyMode(string, mode);

		final boolean hasSpaces = StringUtils.hasSpaces(string);
		final boolean isConstant = StringUtils.isConstant(string);

		String[] strings = isConstant ? string.split("_") : StringUtils.splitByUpperCase(string);

		final int rScope = getReplacementScope(scope);
		if (replace && rScope >= 0) {
			ReplacementManager rm = config.getReplacementManager();
			strings = rm.doReplacements(strings, rScope);
		}

		if (split) {
			strings = StringUtils.allToLower(strings, isConstant);
		}

		final StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < strings.length; ++i) {
			if ((split || hasSpaces) && i > 0) {
				buffer.append(" ");
			}
			buffer.append(strings[i]);
		}

		return applyMode(buffer.toString(), mode);
	}

	private static String applyMode(final String string, final int mode) {
        if (mode == FIRST_TO_LOWER) {
            // only, if it does not start with two upper case letters
            if (string.length() < 2 || !Character.isUpperCase(string.charAt(1))) {
                return StringUtils.firstToLower(string);
            }
        } else if (mode == FIRST_TO_UPPER) {
            return StringUtils.firstToUpper(string);
        }
        return string;
	}

	private static int getReplacementScope(final int scope) {
		if (scope == FIELD || scope == PARAMETER) {
			return Replacement.SCOPE_FIELD;
		}
		else if (scope == METHOD || scope == RETURN) {
			return Replacement.SCOPE_METHOD;
		}
		else {
			return -1;
		}
	}
}
