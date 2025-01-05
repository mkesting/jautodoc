/*******************************************************************
 * Copyright (c) 2006 - 2025, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class with string utilities.
 */
public final class StringUtils {
	private static final Pattern PREFIX_PATTERN = Pattern.compile("[^A-Z][\\sA-Z]");
	private static final Pattern CONSTANT_PATTERN  = Pattern.compile("[0-9A-Z_]+");
	private static final Pattern UPPER_CASE_PATTERN  = Pattern.compile("[A-Z]+");

	private StringUtils() {/* no instantiation */}

	/**
	 * Converts first char to lower.
	 *
	 * @param string the input string
	 * @return the modified string
	 */
	public static String firstToLower(final String string) {
		if (string == null || string.length() == 0) {
			return "";
		} else if (string.length() == 1) {
			return string.toLowerCase();
		}
		return string.substring(0, 1).toLowerCase() + string.substring(1);
	}

	/**
	 * Converts first to upper.
	 *
	 * @param string the input string
	 * @return the modified string
	 */
    public static String firstToUpper(final String string) {
        if (string == null || string.length() == 0) {
            return "";
        } else if (string.length() == 1) {
            return string.toUpperCase();
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

	/**
     * Split string, where characters change from lower to upper case or on digits.
     * <br/>
     * Example: getIDFromProdukt => get ID From Produkt
     * <br/>
     * Example: update4Invoice => update 4 Invoice
	 *
	 * @param string the input string
	 * @return the modified string
	 */
	public static String split(final String string) {
		final String[] strings = splitByUpperCase(string);

		final StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < strings.length; ++i) {
			if (i > 0) {
				buffer.append(" ");
			}
			buffer.append(strings[i]);
		}
		return buffer.toString();
	}

	/**
	 * Split string, where characters change from lower to upper case or on digits.
	 * <br/>
	 * Example: getIDFromProdukt => get ID From Produkt
	 * <br/>
	 * Example: update4Invoice => update 4 Invoice
	 *
	 * @param string the input string
	 * @return the resulting string array
	 */
	public static String[] splitByUpperCase(final String string) {
		return CamelCaseSplitter.split(string);
	}

	/**
	 * Splits the given string by space.
	 *
	 * @param string the string
	 * @return the resulting string array
	 */
	public static String[] splitBySpace(final String string) {
		return string.split("\\s");
	}

	/**
	 * Checks for spaces in the given string.
	 *
	 * @param string the string
	 * @return true, if spaces exists
	 */
	public static boolean hasSpaces(final String string) {
		return string.indexOf(' ') >= 0;
	}

	/**
     * Checks if the given string matches [A-Z_]+.
     *
     * @param string the string
     * @return true, if string matches
     */
    public static boolean isConstant(final String string) {
        return CONSTANT_PATTERN.matcher(string).matches();
    }

    /**
     * Checks if the given string matches [A-Z]+.
     *
     * @param string the string
     * @return true, if string matches
     */
    public static boolean isUpperCase(final String string) {
        return UPPER_CASE_PATTERN.matcher(string).matches();
    }

	/**
	 * Puts all strings to lower case.
	 *
	 * @param strings the strings
	 * @param isConstant false, if the first string should not be modified
	 * @return the resulting string array
	 */
	public static String[] allToLower(final String[] strings, final boolean isConstant) {
		final int start = isConstant ? 0 : 1;
		for (int i = start; i < strings.length; ++i) {
		    if (isConstant || !isUpperCase(strings[i])) {
                strings[i] = strings[i].toLowerCase();
            }
		}
		return strings;
	}

	/**
	 * Gets the substring up to the first upper case letter.
	 *
	 * @param string the string
	 * @return the prefix
	 */
	public static String getPrefix(final String string) {
		final Matcher matcher = PREFIX_PATTERN.matcher(string); // "[^A-Z][\sA-Z]"

		int start = 0;
		String prefix = null;
		if (matcher.find()) {
			start = matcher.start() + 1;
			prefix = string.substring(0, start).trim();
		}
		return prefix;
	}

	/**
	 * Compose name from prefix, base name and suffix.
	 *
	 * @param prefix the prefix
	 * @param baseName the base name
	 * @param suffix the suffix
	 * @return the composed name
	 */
    public static String composeName(final String prefix, final String baseName, final String suffix) {
        return !isEmpty(prefix) && Character.isLetter(prefix.charAt(prefix.length() - 1)) ?
                prefix + firstToUpper(baseName) + suffix : prefix + baseName + suffix;
    }

	/**
	 * Gets the last element of the given path.
	 *
	 * @param path the path
	 * @param separator the element separator
	 * @return the last element
	 */
	public static String getLastElement(final String path, final char separator) {
		final int index = path.lastIndexOf(separator);
		if (index < 0) {
			return path;
		}

		if (index >= path.length() - 1) {
			return "";
		}
		return path.substring(index + 1);
	}

	/**
	 * Checks, if the string starts with the given regular expression.
	 * String.startsWith() does not work with regular expressions.
	 *
	 * @param string the string
	 * @param regexp the regular expression
	 * @return true, if starts with
	 */
	public static boolean startsWith(final String string, final String regexp) {
		return Pattern.compile("^" + regexp).matcher(string).find();
	}

	/**
	 * Returns empty string in case of null.
	 *
	 * @param string the string
	 * @return the string
	 */
	public static String checkNull(final String string) {
		return string == null ? "" : string;
	}

	/**
	 * Checks for empty string.
	 *
	 * @param string the string
	 * @return true, if is empty
	 */
	public static boolean isEmpty(final String string) {
        return string == null || string.trim().isEmpty();
    }

	/**
	 * Get string value of the given object.
	 *
	 * @param object the object
	 * @return the string
	 */
	public static String valueOf(final Object object) {
	    return object == null ? "" : object.toString();
	}

	/**
	 * Get start of string with the given length.
	 *
	 * @param string the string
	 * @param length the length
	 * @return start of the string
	 */
	public static String startOf(final String string, final int length) {
        return string == null || string.length() == 0 ? "" : string.substring(0, Math.min(string.length(), length));
    }

	/**
	 * Infer the indentation level based on the given reference indentation
	 * and tab size.
	 *
	 * @param tabSize the tab size
	 * @param reference the reference indentation
	 * @return the inferred indentation level
	 */
	public static int inferIndentationLevel(final String reference, final int tabSize) {
		final StringBuilder expanded = new StringBuilder(StringUtils.expandTabs(reference, tabSize));

		final int referenceWidth = expanded.length();
		if (tabSize == 0) {
			return referenceWidth;
		}

		int level = referenceWidth / tabSize;
		if (referenceWidth % tabSize > 0) {
			level++;
		}
		return level;
	}

	/**
	 * Expands the given string's tabs according to the given tab size.
	 *
	 * @param string the string
	 * @param tabSize the tab size
	 * @return the expanded string
	 */
	public static String expandTabs(final String string, final int tabSize) {
		final StringBuilder expanded = new StringBuilder();

		for (int i = 0, n = string.length(), chars = 0; i < n; i++) {
			final char ch = string.charAt(i);
			if (ch == '\t') {
				for (; chars < tabSize; chars++) {
					expanded.append(' ');
				}
				chars = 0;
			} else {
				expanded.append(ch);
				chars++;
				if (chars >= tabSize) {
					chars = 0;
				}
			}
		}
		return expanded.toString();
	}
}
