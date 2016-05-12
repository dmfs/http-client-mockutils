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
import org.dmfs.httpclient.HttpStatus;
import org.dmfs.httpclient.OnRedirectCallback;
import org.dmfs.httpclient.OnResponseCallback;
import org.dmfs.httpclient.exceptions.ProtocolError;
import org.dmfs.httpclient.exceptions.ProtocolException;
import org.dmfs.httpclient.exceptions.RedirectionException;
import org.dmfs.httpclient.exceptions.UnexpectedStatusException;


/**
 * A simple mock server {@link HttpRequestExecutor} that always throws an exception when executing a request.
 * 
 * @author Marten Gajda <marten@dmfs.org>
 */
public class ExceptionMockServerExecutor implements HttpRequestExecutor
{

	private final Exception mException;


	/**
	 * Creates an {@link HttpRequestExecutor} that always throws the given Exception. Note that only {@link IOException}s, {@link ProtocolError}s,
	 * {@link ProtocolException}s and their subclasses are supported. Every other {@link Exception} will result in a {@link RuntimeException}.
	 * 
	 * @param exception
	 *            The {@link Exception} to throw when executing a request.
	 */
	public ExceptionMockServerExecutor(Exception exception)
	{
		mException = exception;
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
		try
		{
			throw mException;
		}
		catch (IOException | ProtocolError | ProtocolException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new RuntimeException("Illegal Exception type passed", e);
		}
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
		callback.onError(uri, mException);
	}

}
