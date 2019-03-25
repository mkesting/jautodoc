/*******************************************************************
 * Copyright (c) 2006 - 2019, Martin Kesting, All rights reserved.
 *
 * This software is licenced under the Eclipse Public License v1.0,
 * see the LICENSE file or http://www.eclipse.org/legal/epl-v10.html
 * for details.
 *******************************************************************/
package net.sf.jautodoc.search;

import org.eclipse.jdt.internal.ui.actions.CompositeActionGroup;
import org.eclipse.jdt.ui.actions.GenerateActionGroup;
import org.eclipse.jdt.ui.actions.JavaSearchActionGroup;
import org.eclipse.jdt.ui.actions.OpenEditorActionGroup;
import org.eclipse.jdt.ui.actions.OpenViewActionGroup;
import org.eclipse.jdt.ui.actions.RefactorActionGroup;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionGroup;

/**
 * Additional actions for search result viewer. Currently unused.
 */
@SuppressWarnings("restriction")
public class TaskSearchViewActionGroup extends CompositeActionGroup {

    public TaskSearchViewActionGroup(final IViewPart part) {
        OpenViewActionGroup openViewActionGroup;
        setGroups(new ActionGroup[]{
            new OpenEditorActionGroup(part),
            openViewActionGroup= new OpenViewActionGroup(part),
            new GenerateActionGroup(part),
            new RefactorActionGroup(part),
            new JavaSearchActionGroup(part)
        });
        openViewActionGroup.containsShowInMenu(false);
    }
}
