/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.cleanup;

import net.sf.jautodoc.preferences.Constants;

import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.ICleanUpOptionsInitializer;

/**
 * The JAutodoc cleanup options initializer.
 */
public class JAutodocCleanUpOptionsInitializer implements ICleanUpOptionsInitializer {

    /** {@inheritDoc} */
    @Override
    public void setDefaultOptions(final CleanUpOptions options) {
        options.setOption(Constants.CLEANUP_JAVADOC_OPTION, CleanUpOptions.FALSE);
        options.setOption(Constants.CLEANUP_ADD_HEADER_OPTION, CleanUpOptions.FALSE);
        options.setOption(Constants.CLEANUP_REP_HEADER_OPTION, CleanUpOptions.FALSE);
    }
}
