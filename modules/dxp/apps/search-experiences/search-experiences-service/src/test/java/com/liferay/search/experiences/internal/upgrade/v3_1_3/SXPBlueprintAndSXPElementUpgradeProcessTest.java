/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.upgrade.v3_1_3;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Selena Aungst
 */
public class SXPBlueprintAndSXPElementUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());
	}

	@Test
	public void testUpgradeSXPBlueprintWithUnknownLegacyField()
		throws Exception {

		String elementInstancesJSON =
			"[{\"sxpElement\":{\"externalReferenceCode\":\"OTHER\"," +
				"\"label\":\"legacy-label\"}}]";

		PreparedStatement preparedStatement = Mockito.mock(
			PreparedStatement.class);

		ResultSet resultSet = Mockito.mock(ResultSet.class);

		Mockito.when(
			resultSet.getString("elementInstancesJSON")
		).thenReturn(
			elementInstancesJSON
		);

		SXPBlueprintAndSXPElementUpgradeProcess
			sxpBlueprintAndSXPElementUpgradeProcess =
				new SXPBlueprintAndSXPElementUpgradeProcess(
					Mockito.mock(GroupLocalService.class),
					new JSONFactoryImpl());

		ReflectionTestUtil.invoke(
			sxpBlueprintAndSXPElementUpgradeProcess, "_upgradeSXPBlueprint",
			new Class<?>[] {PreparedStatement.class, ResultSet.class},
			preparedStatement, resultSet);

		Mockito.verifyNoInteractions(preparedStatement);
	}

}
