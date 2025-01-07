/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.site.provider.GroupURLProvider;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.web.internal.display.context.EditStyleBookEntryDisplayContext;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"javax.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/style_book/edit_style_book_entry"
	},
	service = MVCRenderCommand.class
)
public class EditStyleBookEntryMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		renderRequest.setAttribute(
			FrontendTokenDefinitionRegistry.class.getName(),
			_frontendTokenDefinitionRegistry);
		renderRequest.setAttribute(ItemSelector.class.getName(), _itemSelector);
		renderRequest.setAttribute(
			GroupURLProvider.class.getName(), _groupURLProvider);

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		EditStyleBookEntryDisplayContext editStyleBookEntryDisplayContext =
			new EditStyleBookEntryDisplayContext(
				httpServletRequest, renderRequest, renderResponse, _cetManager);

		renderRequest.setAttribute(
			EditStyleBookEntryDisplayContext.class.getName(),
			editStyleBookEntryDisplayContext);

		return "/edit_style_book_entry.jsp";
	}

	@Reference
	private CETManager _cetManager;

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private GroupURLProvider _groupURLProvider;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Portal _portal;

}