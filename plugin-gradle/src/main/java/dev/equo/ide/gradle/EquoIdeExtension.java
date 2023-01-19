/*******************************************************************************
 * Copyright (c) 2022 EquoTech, Inc. and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     EquoTech, Inc. - initial API and implementation
 *******************************************************************************/
package dev.equo.ide.gradle;

import dev.equo.solstice.BrandingIdeHook;
import dev.equo.solstice.IdeHookState;
import dev.equo.solstice.p2.P2Client;
import dev.equo.solstice.p2.P2Model;
import dev.equo.solstice.p2.P2Query;
import org.gradle.api.Project;

/** The DSL inside the equoIde block. */
public class EquoIdeExtension extends P2ModelDsl {
	private final Project project;
	public boolean useAtomos = true;
	private final IdeHookState.List ideHooks = new IdeHookState.List();
	private final BrandingIdeHook branding = new BrandingIdeHook();

	public EquoIdeExtension(Project project) {
		this.project = project;
		useAtomos = true;
		ideHooks.add(branding);
	}

	private static void setToDefault(P2Model model) {
		model.addP2Repo("https://download.eclipse.org/eclipse/updates/4.26/");
		model.getInstall().add("org.eclipse.releng.java.languages.categoryIU");
		model.getInstall().add("org.eclipse.platform.ide.categoryIU");
	}

	IdeHookState.List getIdeHooks() {
		return ideHooks;
	}

	P2Query performQuery(P2Client.Caching caching) throws Exception {
		var modelToQuery = model;
		if (modelToQuery.isEmpty()) {
			modelToQuery = modelToQuery.deepCopy();
			setToDefault(modelToQuery);
		}
		modelToQuery.applyNativeFilterIfNoPlatformFilter();
		return modelToQuery.query(caching);
	}
}
