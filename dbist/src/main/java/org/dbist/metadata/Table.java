/**
 * Copyright 2011-2012 the original author or authors.
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
package org.dbist.metadata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.common.util.ValueUtils;

/**
 * @author Steve M. Jung
 * @since 2011. 7. 10 (version 0.0.1)
 */
public class Table {
	private String domain;
	private String name;
	private List<String> pkColumnName;
	private List<String> titleColumnName;
	private List<String> listedColumnName;
	private List<Column> column = new ArrayList<Column>();

	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getPkColumnName() {
		return pkColumnName;
	}
	public void setPkColumnName(List<String> pkColumnName) {
		this.pkColumnName = pkColumnName;
	}
	public boolean isPkColmnName(String name) {
		return getPkColumnName().contains(name);
	}
	public List<String> getTitleColumnName() {
		populateColumnName();
		return titleColumnName;
	}
	public List<String> getListedColumnName() {
		populateColumnName();
		return listedColumnName;
	}
	private void populateColumnName() {
		if (this.titleColumnName != null)
			return;
		synchronized (this) {
			if (this.titleColumnName != null)
				return;
			pkColumnName = new ArrayList<String>(1);
			List<String> titleColumnName = new ArrayList<String>(1);
			listedColumnName = new ArrayList<String>(1);
			String titleCandidate = null;
			for (Column column : this.column) {
				if (column.isPrimaryKey())
					pkColumnName.add(column.getName());
				if (column.isTitle()) {
					titleColumnName.add(column.getName());
				} else if (column.isListed()) {
					listedColumnName.add(column.getName());
				} else if (!column.isPrimaryKey() && titleCandidate == null) {
					titleCandidate = column.getName();
				}
			}
			if (titleColumnName.isEmpty() && titleCandidate != null)
				titleColumnName.add(titleCandidate);
			this.titleColumnName = titleColumnName;
		}
	}
	public List<Column> getColumn() {
		return column;
	}
	public Column addColumn(Column column) {
		this.column.add(column);
		return column;
	}
	private Map<String, Column> columnMap;
	public Column getColumn(String name) {
		if (name == null)
			ValueUtils.assertNotNull("name", name);
		if (this.columnMap == null) {
			synchronized (this) {
				if (this.columnMap == null) {
					Map<String, Column> columnMap = new HashMap<String, Column>(this.column.size());
					for (Column column : this.column)
						columnMap.put(column.getName(), column);
					this.columnMap = columnMap;
				}
			}
		}
		return columnMap.get(name.toLowerCase());
	}
	private Map<String, String> fieldNameColumNameMap;
	public String toColumnName(String fieldName) {
		if (this.fieldNameColumNameMap == null) {
			synchronized (this) {
				if (this.fieldNameColumNameMap == null) {
					Map<String, String> fieldNameColumnNameMap = new HashMap<String, String>(this.column.size());
					for (Column column : this.column)
						fieldNameColumnNameMap.put(column.getField().getName(), column.getName());
					this.fieldNameColumNameMap = fieldNameColumnNameMap;
				}
			}
		}
		return fieldNameColumNameMap.get(fieldName);
	}
	public String toFieldName(String columnName) {
		Column column = getColumn(columnName);
		return column == null ? null : column.getField().getName();
	}
}
