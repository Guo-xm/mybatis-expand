package org.guoxm.mybatis.expand.structures;

import com.google.common.base.CaseFormat;

/**
 * 用于存放创建表的字段信息
 * Created on 2019/5/20
 * @author guoxm
 */

public class WeColumn {

	private String name;

	private String type;

	private int length;

	private int decimalLength;

	private boolean	isNull;

	private boolean	isKey;

	private boolean	isAutoIncrement;

	private String defaultValue;

	private int typeLength;

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public int getLength() {
		return length;
	}

	public int getDecimalLength() {
		return decimalLength;
	}

	public boolean isNull() {
		return isNull;
	}

	public boolean isKey() {
		return isKey;
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public int getTypeLength() {
		return typeLength;
	}

	public WeColumn(String name, String type, int length, int decimalLength, boolean isNull, boolean isKey, boolean isAutoIncrement, String defaultValue, int typeLength) {
		this.name = name;
		this.type = type;
		this.length = length;
		this.decimalLength = decimalLength;
		this.isNull = isNull;
		this.isKey = isKey;
		this.isAutoIncrement = isAutoIncrement;
		this.defaultValue = defaultValue;
		this.typeLength = typeLength;
	}

	public WeColumn(String name, String type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		WeColumn columnParam = (WeColumn) obj;
		return columnParam != null && CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnParam.getName()).equals(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.getName()));
	}
}
