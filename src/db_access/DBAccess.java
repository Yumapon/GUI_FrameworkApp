package db_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import container.DBConfig;

public class DBAccess {

	//メンバ
	private Connection conn = null;

	public DBAccess(DBConfig dbc) {
		try {
			Class.forName(dbc.getDriver());
			conn = DriverManager.getConnection(dbc.getUrl(), dbc.getUser(), dbc.getPassword());
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("DBに接続しました");
			//自動コミットOFF
			conn.setAutoCommit(false);
		} catch (SQLException | ClassNotFoundException e) {//コネクションの確立に失敗
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("DBとの接続に失敗しました");
			e.printStackTrace();
		}
	}

	//コネクションを配布する
	public Connection getConnection() {
		return conn;
	}

	//コネクションの破棄
	public void closeConnection() {
		try {
			conn.close();
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("DBから切断しました");
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("DBから切断できませんでした");
			e.printStackTrace();
		}
	}
}
