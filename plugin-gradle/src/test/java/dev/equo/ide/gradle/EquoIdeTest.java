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

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import java.io.IOException;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SnapshotExtension.class})
public class EquoIdeTest extends GradleHarness {
	@Test
	public void tasks(Expect expect) throws IOException {
		setFile("build.gradle").toLines("plugins { id 'dev.equo.ide' }", "equoIde {", "}");
		runAndMatchSnapshot(
				expect, Pattern.compile("IDE tasks(.*)To see all tasks", Pattern.DOTALL), "tasks");
	}

	@Test
	public void p2repoArgCheck() throws IOException {
		setFile("build.gradle")
				.toLines(
						"plugins { id 'dev.equo.ide' }",
						"equoIde {",
						"  p2repo 'https://somerepo'",
						"  install 'org.eclipse.swt'",
						"}");
		runFailAndAssert("equoIde")
				.contains(
						"> Must end with /\n"
								+ "  p2repo(\"https://somerepo\")   <- WRONG\n"
								+ "  p2repo(\"https://somerepo/\")  <- CORRECT");
		setFile("build.gradle")
				.toLines(
						"plugins { id 'dev.equo.ide' }",
						"equoIde {",
						"  p2repo 'https://somerepo//'",
						"  install 'org.eclipse.swt'",
						"}");
		runFailAndAssert("equoIde")
				.contains(
						"> Must end with a single /\n"
								+ "  p2repo(\"https://somerepo//\")  <- WRONG\n"
								+ "  p2repo(\"https://somerepo/\")   <- CORRECT");
	}

	@Test
	public void equoIdeTestOnly() throws IOException {
		setFile("build.gradle")
				.toLines(
						"plugins { id 'dev.equo.ide' }",
						"equoIde {",
						"  p2repo 'https://download.eclipse.org/eclipse/updates/4.26/'",
						"  install 'org.eclipse.swt'",
						"}");
		runAndAssert("equoIde", "-PequoTestOnly=true", "--stacktrace")
				.contains("exit code: 0")
				.matches("(?s)(.*)stdout: Loaded (\\d+) bundles(.*)");
	}

	@Test
	@Disabled
	public void equoIde() throws IOException {
		setFile("build.gradle")
				.toLines(
						"plugins { id 'dev.equo.ide' }",
						"equoIde {",
						"  release '4.26'",
						"}",
						"// (placeholder so GPJ formats this nicely)");
		gradleRunner().withArguments("equoIde", "--stacktrace").forwardOutput().build();
	}
}
