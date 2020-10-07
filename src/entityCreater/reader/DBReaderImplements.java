package entityCreater.reader;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

import container.DBConfig;
import container.EnvironmentConfigReader;
import db_access.DBAccess;
import entityCreater.dataTypeMapper.DataTypeMapper;
import entityCreater.info.EntityInfo;

public class DBReaderImplements implements DBReader{

	@Override
	public ArrayList<EntityInfo> read() {

		//metadata格納用
		ResultSet rs = null;

		//Table情報格納用
		ArrayList<String> tableNames = new ArrayList<>();

		//Entity情報格納用
		ArrayList<EntityInfo> entityInfos = new ArrayList<>();

		//DB設定の取得
		EnvironmentConfigReader ecr = new EnvironmentConfigReader();
		DBConfig dbc = ecr.read();

		String dbName = dbc.getDbName();

		//コネクションの確立
		DBAccess dba = new DBAccess(dbc);

		try {

			// データベースとの接続
			Connection con = dba.getConnection();

			//データベースメタデータの取得
			DatabaseMetaData dbmd = con.getMetaData();

			//Table名の取得
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("Table一覧を取得します");
			String[] types = { "TABLE", "VIEW", "SYSTEM TABLE" };
			rs = dbmd.getTables(dbName, null, "%", types);
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");

				//Table名の格納
				tableNames.add(tableName);
			}

			//Table情報の格納用クラス群
			Iterator<String> tableNameList = tableNames.iterator();
			String tableName;
			String id = null;
			ArrayList<String[]> columns;

			while (tableNameList.hasNext()) {

				//TableNameを取得
				tableName = tableNameList.next();

				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				System.out.println("Table名：" + tableName + "のEntityクラスを生成します");

				//Tableの全column情報の取得
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				System.out.println("Table名：" + tableName + "の全column情報を取得します");
				rs = dbmd.getColumns(dbName, null, tableName, "%");

				//column格納用
				columns = new ArrayList<>();
				while (rs.next()) {
					//SQLのデータタイプからJavaのデータタイプに変換
					String dataType = DataTypeMapper.dataTypeChange(rs.getString("TYPE_NAME"));

					//変数名を小文字に変換
					String columnName = rs.getString("COLUMN_NAME").toLowerCase();

					String[] column = { dataType, columnName };
					columns.add(column);
				}

				//PrimaryKeyを取得
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				System.out.println("Table名：" + tableName + "のPrimaryKeyを取得します");
				ResultSet primaryKeys = dbmd.getPrimaryKeys(dbName, null, tableName);
				if (primaryKeys.next()) {
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					System.out.println("Table名：" + tableName + "のPrimaryKeyは " + primaryKeys.getString("COLUMN_NAME") + "です");
					id = primaryKeys.getString("COLUMN_NAME").toLowerCase();
				}

				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				System.out.println("EntityInfoクラスにTable情報を格納しています");
				//Table情報格納用クラス
				EntityInfo ei = new EntityInfo();

				//Table情報格納用クラスにTableNameを設定
				ei.setTableName(tableName);
				//Table情報格納用クラスにidを設定
				ei.setId(id);
				//Table情報格納クラスにカラム情報を設定
				ei.setColumns(columns);

				entityInfos.add(ei);
			}

			rs.close();

			// データベースのクローズ
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("DBとのコネクションを破棄します");
			con.close();

		} catch (Exception e) {
			System.out.println("Exception発生");
			e.printStackTrace();
		}
		return entityInfos;
	}

}
