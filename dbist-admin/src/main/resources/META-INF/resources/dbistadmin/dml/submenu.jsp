<?xml version="1.0" encoding="UTF-8" ?>
<%@page import="org.dbist.admin.ParameterUtils"%>
<%@page import="org.dbist.dml.Dml"%>
<%@page import="net.sf.common.util.BeanUtils"%>
<%@page import="net.sf.common.util.ReflectionUtils"%>
<%@page import="org.springframework.util.StringUtils"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="net.sf.common.util.ValueUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	class Select {
		String value;
		List<String> options = new ArrayList<String>();
	}

	response.setDateHeader("Expires", 0);
	response.setHeader("Pragma", "no-cache");
	response.setHeader("Cache-Control", request.getProtocol().equals("HTTP/1.1") ? "no-cache" : "no-store");

	// contextName for BeanUtils
	String contextName = StringUtils.replace(ValueUtils.toString(request.getContextPath(), "dbist"), "/", "");
	BeanUtils beanUtils = BeanUtils.getInstance(contextName);

	// DML bean to use
	String dml = ParameterUtils.get(request, "_dml");
	// DML beans usable
	String[] dmls = beanUtils.getNames(Dml.class);

	// By
	String by = ParameterUtils.get(request, "_by");

	// tableName
	String table = ValueUtils.toNotNull(ParameterUtils.get(request, "_table"));

	// prefix of data model class
	String prefix = StringUtils.replace(ValueUtils.toNotNull(ParameterUtils.get(request, "_prefix")).trim(), "/", ".");
	List<Select> selectList = new ArrayList<Select>();
	if (!ValueUtils.isEmpty(prefix) && !prefix.contains("*")) {
		String[] paths = ParameterUtils.getValues(request, "_path");

		int pathSize = ValueUtils.isEmpty(paths) ? 0 : paths.length;
		int i = 0;
		// prefix removed className list
		List<String> classNameList = null;
		{
			String prefixReplace = prefix + (prefix.endsWith(".") ? "" : ".");
			String prevPrefix = StringUtils.replace(ValueUtils.toNotNull(ParameterUtils.get(request, "_prevPrefix")).trim(), "/", ".");
			if (prefix.equals(prevPrefix))
				classNameList = (List<String>) request.getSession().getAttribute("dml.submenu.classNameList");
			if (classNameList == null) {
				classNameList = new ArrayList<String>();
				String location = "classpath*:" + StringUtils.replace(prefix, ".", "/") + (prefix.endsWith(".") ? "**" : "/**");
				List<String> list = ReflectionUtils.getClassNameList(location, null);
				StringBuffer buf = new StringBuffer();
				for (String className : list) {
					String name = className.replaceFirst(prefixReplace, "");
					classNameList.add(name);
					buf.append(buf.length() == 0 ? "" : ",").append(name);
				}
				request.getSession().setAttribute("dml.submenu.classNameList", classNameList);
			}
		}
		while (!ValueUtils.isEmpty(classNameList)) {
			Select select = new Select();
			selectList.add(select);
			List<String> nextNameList = new ArrayList<String>();
			String path = pathSize > i ? paths[i++] : null;
			String pathDot = path + ".";
			for (String className : classNameList) {
				if (className.contains(".")) {
					String name = className.substring(0, className.indexOf("."));
					if (!select.options.contains(name))
						select.options.add(name);
					if (className.startsWith(pathDot)) {
						select.value = path;
						nextNameList.add(className.substring(className.indexOf(".") + 1));
					}
				} else {
					select.options.add(className);
					if (className.equals(path))
						select.value = path;
				}
			}
			classNameList = nextNameList;
		}
	}
%>
<form name="submenuForm" method="post">
	<input name="_menu" type="hidden" value="<%=ValueUtils.toNotNull(ParameterUtils.get(request, "_menu"))%>" /> <input name="_prevPrefix" type="hidden"
		value="<%=prefix%>" /> <input name="_pageIndex" type="hidden" value="0" />
	<table>
		<tr>
			<td>dml: <select name="_dml" onchange="submit()">
					<option value=""></option>
					<%
						int dmlSize = dmls.length;
						for (String d : dmls) {
					%>
					<option value="<%=d%>" <%=dmlSize == 1 || d.equals(dml) ? "selected=\"selected\"" : ""%>><%=d%></option>
					<%
						}
					%>
			</select> &nbsp;&nbsp;by: <select name="_by" onchange="submit()">
					<option value="class" <%="class".equals(by) ? "selected=\"selected\"" : ""%>>class</option>
					<option value="table" <%="table".equals(by) ? "selected=\"selected\"" : ""%>>table</option>
			</select> <%
 	if ("table".equals(by)) {
 %>&nbsp;&nbsp;table: <input id="_table" name="_table" type="text" value="<%=table%>" onchange="submit()" /> <%
 	} else {
 %>&nbsp;&nbsp;class: <input id="dmlSubmenuPrefix" name="_prefix" type="text" value="<%=prefix%>" onchange="submit()" /> <%
 	for (Select select : selectList) {
 %> <select name="_path" onchange="submit()">
					<option value=""></option>
					<%
						for (String option : select.options) {
					%>
					<option value="<%=option%>" <%=option.equals(select.value) ? "selected=\"selected\"" : ""%>><%=option%></option>
					<%
						}
					%>
			</select> <%
 	}
 	}
 %> <input type="submit" value="OK" class="button" /></td>
		</tr>
	</table>
</form>