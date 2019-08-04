package name.guoxm.mybatis.expand.structures;

import com.google.common.base.CaseFormat;
import lombok.Data;

/**
 * 用于存放创建表的字段信息
 * Created on 2019/5/20
 * @author guoxm
 */
@Data
public class ColumnMate {

	private String name;

	private String type;

	private int length;

	private int decimalLength;

	private boolean	notNull;

	private boolean	primaryKey;

	private boolean	autoIncrement;

	private String defaultValue;

	private Integer typeLength;

	public ColumnMate(String name, String type, int length, int decimalLength, boolean notNull, boolean primaryKey, boolean autoIncrement, String defaultValue, Integer typeLength) {
		this.name = name;
		this.type = type;
		this.length = length;
		this.decimalLength = decimalLength;
		this.notNull = notNull;
		this.primaryKey = primaryKey;
		this.autoIncrement = autoIncrement;
		this.defaultValue = defaultValue;
		this.typeLength = typeLength;
	}

	public ColumnMate(String name, String type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		ColumnMate columnParam = (ColumnMate) obj;
		return columnParam != null && CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnParam.getName()).equals(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.getName()));
	}
}
