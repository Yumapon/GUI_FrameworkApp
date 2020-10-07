package query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class QueryInfo {

	//TableName
	String tableName;

	//PrimaryKey
	String idName;

	//ColumnNames
	ArrayList<String> columnNames;

	//ここは各メソッドで使い回すので、一回一回空にしてあげないといけない。
	//ColumnValues
	Map<String, String> columnValues = new HashMap<>();

	//PrimaryKeyの値を取得
	public String getIdValue() {
		return columnValues.get(idName);
	}

	//columnValuesの初期化
	public void clearQueryInfo() {
		columnValues.clear();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getIdName() {
		return idName;
	}

	public void setIdName(String idName) {
		this.idName = idName;
	}

	public ArrayList<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames = columnNames;
	}

	public Map<String, String> getColumnValues() {
		return columnValues;
	}

	public void setColumnValues(Map<String, String> columnValues) {
		this.columnValues = columnValues;
	}

}
