package entityCreater.info;

import java.util.List;

/**
 *
 * Entity情報格納クラス
 * @author okamotoyuuma
 *
 */
public class EntityInfo {

	//テーブル名
	private String tableName;

	//Primary Key名
	private String id;

	//すべてのカラム名と型
	private List<String[]> columns;

	/**
	 * (non-javadoc)
	 * getter setter
	 * @return
	 */

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String[]> getColumns() {
		return columns;
	}

	public void setColumns(List<String[]> columns) {
		this.columns = columns;
	}

}
