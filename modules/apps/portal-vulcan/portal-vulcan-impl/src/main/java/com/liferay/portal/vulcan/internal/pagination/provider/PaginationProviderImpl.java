/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.pagination.provider;

import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.pagination.provider.PaginationProvider;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio Jiménez del Coso
 */
@Component(service = PaginationProvider.class)
public class PaginationProviderImpl implements PaginationProvider {

	@Override
	public Pagination getPagination(
		long companyId, Integer page, Integer pageSize) {

		return _getPagination(
			_getPageSizeLimit(companyId), GetterUtil.getInteger(page, 1),
			GetterUtil.getInteger(pageSize, 20));
	}

	private int _getPageSizeLimit(long companyId) {
		try {
			HeadlessAPICompanyConfiguration headlessAPICompanyConfiguration =
				_configurationProvider.getCompanyConfiguration(
					HeadlessAPICompanyConfiguration.class, companyId);

			return headlessAPICompanyConfiguration.pageSizeLimit();
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException.getMessage());
		}
	}

	// LPD-110533 DECIDE (Headless REST and GraphQL paging): A page is at most
	// 500, but page 2 at 500 per page starts at item 500 and comes back empty,
	// while totalCount advertises more. No client can read past item 500. This
	// is the batch export failure outside the batch engine. Fix: Product
	// decision; probably the biggest one.
	private Pagination _getPagination(
		int pageSizeLimit, int requestPage, int requestPageSize) {

		if (_isUnlimited(requestPage) || _isUnlimited(requestPageSize)) {
			if (_isUnlimited(pageSizeLimit)) {
				return Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
			}

			return Pagination.of(1, pageSizeLimit);
		}

		if (_isUnlimited(pageSizeLimit)) {
			return Pagination.of(requestPage, requestPageSize);
		}

		return Pagination.of(
			requestPage, Math.min(requestPageSize, pageSizeLimit));
	}

	private boolean _isUnlimited(int value) {
		if (value <= 0) {
			return true;
		}

		return false;
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}