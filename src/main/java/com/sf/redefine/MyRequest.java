package com.sf.redefine;

import java.io.BufferedReader;

public class MyRequest {
	public static String getParameter(Object request,String name) throws Exception
	{
		java.lang.reflect.Method x = request.getClass().getMethod("getParameter", String.class);
		x.setAccessible(true);
		return (String)x.invoke(request, name);
	}
	public static Object getServletContext(Object request) throws Exception
	{
		return request.getClass().getMethod("getServletContext", null).invoke(request, new Object[] {});
	}
	public static String getHeader(Object request,String name) throws Exception
	{
		return (String)request.getClass().getMethod("getHeader", String.class).invoke(request, name);
	}

	public static Object getHeaderNames(Object request) throws Exception
	{
		return request.getClass().getMethod("getHeaderNames").invoke(request);
	}
	
	public static Object getSession(Object request) throws Exception
	{
		return request.getClass().getMethod("getSession",  null).invoke(request, new Object[] {});
	}
	public static int getContentLength(Object request) throws Exception
	{
		return Integer.parseInt(request.getClass().getMethod("getContentLength",  null).invoke(request, new Object[] {}).toString());
	}
	
	public static Object getInputStream(Object request) throws Exception
	{
		return request.getClass().getMethod("getInputStream", null).invoke(request, new Object[] {});
	}

	public static BufferedReader getReader(Object request) throws Exception {
		return (BufferedReader) request.getClass().getMethod("getReader", null).invoke(request,new Object[] {});
	}

	public static String getMethod(Object request) throws Exception {
		return (String) request.getClass().getMethod("getMethod", null).invoke(request,new Object[] {});
	}
}
