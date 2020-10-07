package query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db_access.ConnectionPool;
import exception.NoColumnValueException;
import exception.NoDBAccessException;
import exception.NotBeginTransactionException;

public class Query {

	//ConnectionPoolを取得する
	ConnectionPool cp = ConnectionPool.getInstance();

	//TransactionID
	String transactionID;

	//Connection
	Connection conn;

	//Statement
	Statement stmt;

	//private Class<T> entityType;

	public Query() {
		//TransactionIDを取得する
		Thread currentThread = Thread.currentThread(); // 自分自身のスレッドを取得
		long threadID = currentThread.getId();
		this.transactionID = String.valueOf(threadID);
	}

	/**
	 * INSERT文生成メソッド
	 * @param qi
	 * @return
	 */
	public String createInsertSql(QueryInfo qi) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("INSERT文の生成を開始します");

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("Entityの情報を取得します");

		//SQL生成
		StringBuilder column = new StringBuilder();
		StringBuilder columnValue = new StringBuilder();
		for (String str : qi.getColumnNames()) {
			column.append(str);
			column.append(" ,");
			columnValue.append("\"");
			columnValue.append(qi.getColumnValues().get(str));
			columnValue.append("\",");
		}
		// 末尾から1文字分を削除
		column.deleteCharAt(column.length() - 1);
		columnValue.deleteCharAt(columnValue.length() - 1);

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(qi.getTableName());
		sql.append(" (");
		sql.append(column);
		sql.append(") values (");
		sql.append(columnValue);
		sql.append(");");

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("INSERT文の生成が完了しました:" + sql);
		return sql.toString();
	}

	/**
	 * UPDATE文生成メソッド
	 * @param qi
	 * @return
	 */
	public String createUpdateSql(QueryInfo qi) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("UPDATE文の生成を開始します");

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("Entityの情報を取得します");

		//SQL生成
		StringBuilder setValue = new StringBuilder();
		for (String column : qi.getColumnNames()) {
			if (column.equals(qi.getIdName()))
				continue;
			setValue.append(column);
			setValue.append(" = \"");
			setValue.append(qi.getColumnValues().get(column));
			setValue.append("\",");
		}
		// 末尾から1文字分を削除
		setValue.deleteCharAt(setValue.length() - 1);

		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE ").append(qi.getTableName()).append(" SET ").append(setValue).append(" WHERE ")
				.append(qi.getIdName()).append(" = \"").append(qi.getIdValue()).append("\";");

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("UPDATE文の生成が完了しました:" + sql);

		return sql.toString();
	}

	/**
	 * SELECT文生成メソッド
	 * @param qi
	 * @return
	 */
	public String createSelectSql(QueryInfo qi) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("SELECT文の生成を開始します");

		//SQL生成
		String idValue = qi.getIdValue();
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ").append(qi.getTableName());
		//QueryInfoに何もセットされていない場合、全件検索
		if (idValue == null && qi.getColumnValues().size() == 0)
			sql.append(" LIMIT 1000;");
		//QueryInfoにidValue以外のカラム値が設定されている場合、条件検索
		else {
			sql.append(" WHERE ");
			if (idValue != null) {
				sql.append(qi.getIdName()).append(" = \"").append(idValue).append("\";");
			} else {
				for (String columnName : qi.getColumnNames()) {
					if (qi.getColumnValues().get(columnName) == null || columnName.equals(qi.getIdName())) {
						continue;
					}
					sql.append(columnName).append(" = \"").append(qi.getColumnValues().get(columnName))
							.append("\" AND ");
				}
				sql.delete(sql.length() - 4, sql.length() - 1).append(";");
			}
		}
		return sql.toString();
	}

	/**
	 * RECORD確認用メソッド
	 * @param qi
	 * @return
	 */
	public String createCheckRecordSql(QueryInfo qi) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("COUNTCHECK用SQLの生成を開始します");

		//SQL生成
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM ").append(qi.getTableName()).append(" WHERE ").append(qi.getIdName())
				.append(" = \"")
				.append(qi.getIdValue()).append("\";");

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("COUNTCHECK用SQLの生成が完了しました:" + sql);

		return sql.toString();
	}

	/**
	 * DELETE文生成メソッド
	 * @param qi
	 * @return
	 * @throws noColumnValueException
	 */
	public String createDeleteSql(QueryInfo qi) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("DELETE文の生成を開始します");

		StringBuilder sql = new StringBuilder();

		//SQL生成
		String idValue = qi.getIdValue();
		boolean checkColumnValue = false;
		sql.append("DELETE FROM ").append(qi.getTableName()).append(" WHERE ");
		if (idValue != null)
			sql.append(qi.getIdName()).append(" = \"").append(idValue).append("\";");
		else {
			for (String columnName : qi.getColumnNames()) {
				if (qi.getColumnValues().get(columnName) == null || columnName.equals(qi.getIdName())) {
					continue;
				}
				sql.append(columnName).append(" = \"").append(qi.getColumnValues().get(columnName)).append("\";");
				checkColumnValue = true;
			}
			if (!checkColumnValue) {
				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println("カラムに何も値が入っていません");
				throw new NoColumnValueException();
			}
		}

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("DELETE文の生成が完了しました:" + sql);

		return sql.toString();
	}

	/**
	 * レコード数をチェックするメソッド
	 * @return
	 */
	public String createCheckCountSql(QueryInfo qi) {
		return "SELECT COUNT(*) FROM " + qi.getTableName() + ";";
	}

	/*SQL実行メソッド*/

	/**
	 * 更新メソッド
	 * @param sql
	 * @return
	 */
	public int executeUpdate(String sql) {
		//更新レコード数
		int i = 0;

		//Connectionの取得
		try {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//例外内容
			System.out.println("コネクションを取得し、ステートメントを生成します");
			stmt = cp.getDBAccess(transactionID).getConnection().createStatement();
			i = stmt.executeUpdate(sql);
		} catch (NotBeginTransactionException e) {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//例外内容
			System.out.println("トランザクションが開始されていません");
			e.printStackTrace();
		} catch (SQLException e) {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//例外内容
			System.out.println("SQLエラーが発生しました");
			e.printStackTrace();
		}

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("SQLを実行しました");

		return i;
	}

	/**
	 * 参照メソッド
	 * @param sql
	 * @return
	 */
	public ResultSet executeQuery(String sql) {
		//Connectionの取得
		ResultSet rs = null;
		try {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("コネクションを取得し、ステートメントを生成します");
			stmt = cp.getDBAccess().getConnection().createStatement();
			rs = stmt.executeQuery(sql);
		} catch (SQLException e) {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//例外内容
			System.out.println("SQLエラーが発生しました");
			e.printStackTrace();
		} catch (NoDBAccessException e) {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//例外内容
			System.out.println("Connectionが見つかりません");
			e.printStackTrace();
		}

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("SQLを実行しました");

		return rs;
	}

}
