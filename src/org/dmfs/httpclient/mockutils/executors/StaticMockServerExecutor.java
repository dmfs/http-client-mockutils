/*
 * Copyright (C) 2016 Marten Gajda <marten@dmfs.org>
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dmfs.httpclient.mockutils.executors;

import java.io.IOException;
import java.net.URI;

import org.dmfs.httpclient.HttpRequest;
import org.dmfs.httpclient.HttpRequestExecutor;
import org.dmfs.httpclient.HttpResponse;
import org.dmfs.httpclient.HttpStatus;
import org.dmfs.httpclient.OnRedirectCallback;
import org.dmfs.httpclient.OnResponseCallback;
import org.dmfs.httpclient.exceptions.ProtocolError;
import org.dmfs.httpclient.exceptions.ProtocolException;
import org.dmfs.httpclient.exceptions.RedirectionException;
import org.dmfs.httpclient.exceptions.UnexpectedStatusException;
import org.dmfs.httpclient.mockutils.responses.CustomUrisMockResponse;


/**
 * A simple {@link HttpRequestExecutor} to test response handling. This class emulates a server that responds to all URLs and with the same response each time.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class StaticMockServerExecutor implements HttpRequestExecutor
{

	private final HttpResponse mResponse;


	/**
	 * Creates a mock server {@link HttpRequestExecutor} that always responds with the same response.
	 * 
	 * @param response
	 *            The response to return.
	 */
	public StaticMockServerExecutor(HttpResponse response)
	{
		mResponse = response;
	}


	@Override
	public <T> T execute(URI uri, HttpRequest<T> request) throws IOException, ProtocolError, ProtocolException, RedirectionException, UnexpectedStatusException
	{
		return execute(uri, request, new OnRedirectCallback()
		{
			@Override
			public boolean followRedirect(HttpStatus status, URI redirectingLocation, URI newLocation)
			{
				return false;
			}
		});
	}


	@Override
	public <T> T execute(URI uri, HttpRequest<T> request, OnRedirectCallback redirectionCallback) throws IOException, ProtocolError, ProtocolException,
		RedirectionException, UnexpectedStatusException
	{
		HttpResponse response = new CustomUrisMockResponse(mResponse, uri, uri);
		return request.responseHandler(response).handleResponse(response);
	}


	@Override
	public <T> void execute(URI uri, HttpRequest<T> request, OnResponseCallback<T> callback)
	{
		execute(uri, request, callback, new OnRedirectCallback()
		{
			@Override
			public boolean followRedirect(HttpStatus status, URI redirectingLocation, URI newLocation)
			{
				return false;
			}
		});
	}


	@Override
	public <T> void execute(URI uri, HttpRequest<T> request, OnResponseCallback<T> callback, OnRedirectCallback redirectionCallback)
	{
		try
		{
			HttpResponse response = new CustomUrisMockResponse(mResponse, uri, uri);
			callback.onResponse(response.requestUri(), response.responseUri(), request.responseHandler(response).handleResponse(response));
		}
		catch (Exception e)
		{
			callback.onError(uri, e);
		}
	}

}
