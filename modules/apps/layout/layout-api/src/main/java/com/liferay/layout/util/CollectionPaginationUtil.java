/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util;

import com.liferay.info.pagination.Pagination;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.Objects;

/**
 * @author Eudaldo Alonso
 */
// LPD-110533 FINE (Paged collections and Asset Publisher): They page at the
// source. Pages past 500 are empty, and a page that straddles 500 comes back
// short, while the paginator shows the full count.
public class CollectionPaginationUtil {

	public static final String PAGINATION_TYPE_NONE = "none";

	public static final String PAGINATION_TYPE_NUMERIC = "numeric";

	public static final String PAGINATION_TYPE_REGULAR = "regular";

	public static final String PAGINATION_TYPE_SIMPLE = "simple";

	public static Pagination getPagination(
		int activePage, boolean displayAllItems, int numberOfItems,
		int numberOfItemsPerPage, String paginationType) {

		if (isPaginationEnabled(paginationType)) {
			if ((numberOfItemsPerPage <= 0) ||
				(numberOfItemsPerPage >
					PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA)) {

				// LPD-110533 DECIDE (Collections "Display All"): Shows at most
				// 500 while the count shows the real total. Object collections
				// are affected only on the index-backed path. Includes
				// LPD-112650. Fix: Product decision.
				numberOfItemsPerPage =
					PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA;
			}

			if (activePage < 1) {
				activePage = 1;
			}

			int start = (activePage - 1) * numberOfItemsPerPage;

			return Pagination.of(start + numberOfItemsPerPage, start);
		}

		if (displayAllItems) {
			return Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		}

		return Pagination.of(numberOfItems, 0);
	}

	public static int getTotalNumberOfItems(
		int count, boolean displayAllPages, boolean displayAllItems,
		int numberOfItems, int numberOfItemsPerPage, int numberOfPages,
		String paginationType) {

		if (!isPaginationEnabled(paginationType)) {
			if (displayAllItems) {
				return count;
			}

			return Math.min(count, numberOfItems);
		}

		if (displayAllPages || (numberOfPages <= 0)) {
			return count;
		}

		if ((numberOfItemsPerPage <= 0) ||
			(numberOfItemsPerPage >
				PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA)) {

			numberOfItemsPerPage = PropsValues.SEARCH_CONTAINER_PAGE_MAX_DELTA;
		}

		return Math.min(count, numberOfPages * numberOfItemsPerPage);
	}

	public static boolean isPaginationEnabled(String paginationType) {
		if (Objects.equals(
				paginationType,
				CollectionPaginationUtil.PAGINATION_TYPE_NUMERIC) ||
			Objects.equals(
				paginationType,
				CollectionPaginationUtil.PAGINATION_TYPE_REGULAR) ||
			Objects.equals(
				paginationType,
				CollectionPaginationUtil.PAGINATION_TYPE_SIMPLE)) {

			return true;
		}

		return false;
	}

}