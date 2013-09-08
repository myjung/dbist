/**
 * Copyright 2011-2013 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dbist.dml.jdbc;

import java.util.Map;

import org.dbist.dml.Lock;

/**
 * @author Steve M. Jung
 * @since 2013. 9. 7. (version 2.0.3)
 */
public class QueryMapperSqlserver extends AbstractQueryMapper {

	public String getDbType() {
		return "sqlserver";
	}

	public boolean isSupportedPaginationQuery() {
		return false;
	}

	public boolean isSupportedLockTimeout() {
		return false;
	}

	public String applyPagination(String sql, Map<String, ?> paramMap, int pageIndex, int pageSize, int firstResultIndex, int maxResultSize) {
		boolean pagination = pageIndex >= 0 && pageSize > 0;
		boolean fragment = firstResultIndex > 0 || maxResultSize > 0;
		if (!pagination && !fragment)
			return sql;
		if (!pagination) {
			pageIndex = 0;
			pageSize = 0;
		}
		if (firstResultIndex < 0)
			firstResultIndex = 0;
		if (maxResultSize < 0)
			maxResultSize = 0;

		String lowerSql = sql.toLowerCase();
		int selectIndex = lowerSql.indexOf("select");
		int distinctIndex = lowerSql.indexOf("distinct");
		int topIndex = distinctIndex > 0 && distinctIndex < selectIndex + 13 ? distinctIndex + 8 : selectIndex + 6;
		int top = (pageIndex + 1) * pageSize + firstResultIndex;
		return new StringBuffer(sql).insert(topIndex, " top " + top).toString();
	}

	public String toLockForFrom(Lock lock) {
		return "with (updlock, rowlock)";
	}

	public String getFunctionLowerCase() {
		return "lower";
	}

	public String getQueryCountTable() {
		return "select count(*) from ${domain}.sysobjects where xtype = 'U' and lower(name) = ?";
	}

	public String getQueryPkColumnNames() {
		return "select lower(col.name) name from ${domain}.sysobjects tbl, ${domain}.syscolumns col"
				+ " where tbl.xtype = 'U' and lower(tbl.name) = ? and col.id = tbl.id and col.typestat = 3 order by colorder";
	}

	public String getQueryColumnNames() {
		return "select lower(col.name) name, lower(type.name) dataType from ${domain}.sysobjects tbl, ${domain}.syscolumns col, ${domain}.systypes type"
				+ " where tbl.xtype = 'U' and lower(tbl.name) = ? and col.id = tbl.id and col.xusertype = type.xusertype";
	}

	public String getQueryColumnName() {
		return "select lower(col.name) name, lower(type.name) dataType from ${domain}.sysobjects tbl, ${domain}.syscolumns col, ${domain}.systypes type"
				+ " where tbl.xtype = 'U' and lower(tbl.name) = ? and col.id = tbl.id and col.xusertype = type.xusertype and lower(col.name) = ?";
	}

	public String getQueryCountIdentity() {
		return "";
	}

	public String getQueryCountSequence() {
		return "";
	}

}