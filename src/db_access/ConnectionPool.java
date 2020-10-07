package db_access;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;

import container.DBConfig;
import container.EnvironmentConfigReader;
import exception.DoNotHaveDBAccessException;
import exception.NoDBAccessException;
import exception.NotBeginTransactionException;

public class ConnectionPool implements Serializable {

	/**
	* シリアルバージョンUID
	*/
	private static final long serialVersionUID = 1L;

	//コネクション保持用コレクションの作成
	private HashMap<String, DBAccess> connectionPool = new HashMap<String, DBAccess>();
	//DB設定の取得
	EnvironmentConfigReader ecr = new EnvironmentConfigReader();
	DBConfig dbc = ecr.read();
	//DBへのコネクション数
	int NUMBER_OF_DB_CONNECTIONS = dbc.getNumberOfAccess();
	//コネクションのID
	private Queue<String> connectionID = new ArrayDeque<>();
	//DBコネクションオブジェクト
	DBAccess dba;

	//コンストラクタ
	private ConnectionPool(){
		//コネクションの確立
		for (int i = 0; i < NUMBER_OF_DB_CONNECTIONS; i++) {
			DBAccess dba = new DBAccess(dbc);
			connectionPool.put("unused" + i, dba);
		}
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		System.out.println("DBへのコネクションを" + NUMBER_OF_DB_CONNECTIONS + "個確立しました");
	}

	//唯一のConnectionPoolクラスを返す
	public static ConnectionPool getInstance() {
        return ConnectionPoolInstanceHolder.INSTANCE;
    }

	//ConnectionPoolクラスの唯一のインスタンスを保持する内部クラス
    public static class ConnectionPoolInstanceHolder {
        /** 唯一のインスタンス */
        private static final ConnectionPool INSTANCE = new ConnectionPool();
    }

	//DBAccessをチェックアウトする
	public void checkoutDBAccess(String transactionID) throws NoDBAccessException {
		String connectionKey = null;
		dba = null;
		for (int i = 0; i < NUMBER_OF_DB_CONNECTIONS; i++) {
			connectionKey = "unused" + i;

			//使用されていないコネクションを取得し、トランザクションIDで格納し直す
			if (connectionPool.containsKey(connectionKey)) {
				connectionID.add(connectionKey);
				dba = connectionPool.get(connectionKey);
				connectionPool.remove(connectionKey);
				connectionPool.put(transactionID, dba);
				break;
			}
		}
		if (dba != null) {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("transactionID＝" + transactionID + "にコネクションをチェックアウトしました");
		} else {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("コネクションが全て使用中か、Tramsactionが開始されていません。");
			throw new NoDBAccessException();
		}
	}

	/*
	 * TransactionManager以外が使用する、コネクション取得メソッド
	 */
	public DBAccess getDBAccess(String transactionID) throws NotBeginTransactionException {
		dba = null;
		if(connectionPool.containsKey(transactionID)) {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("コネクションを貸し出しました");
			dba = connectionPool.get(transactionID);
		}else {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("トランザクションが開始されていない可能性があります");
			throw new NotBeginTransactionException();
		}
		return dba;
	}

	//Transaction処理以外でDBAccess使いたい場合に使用するメソッド
	public DBAccess getDBAccess() throws NoDBAccessException {
		String connectionKey = null;
		dba = null;
		for (int i = 0; i < NUMBER_OF_DB_CONNECTIONS; i++) {
			connectionKey = "unused" + i;

			//使用されていないコネクションを取得する
			if (connectionPool.containsKey(connectionKey)) {
				dba = connectionPool.get(connectionKey);
				break;
			}
		}
		if (dba != null) {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("DBAccessを取得しました");
		} else {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("コネクションが全て使用中です。");
			throw new NoDBAccessException();
		}
		return dba;
	}
	/**
	 *
	 * @param transactionID
	 * @throws DoNotHaveDBAccessException
	 */
	//DBAccessを返してもらうメソッド
	public void returnDBAccess(String transactionID) throws DoNotHaveDBAccessException {

		dba = connectionPool.put(transactionID, dba);
		if(dba == null) {
			throw new DoNotHaveDBAccessException();
		}else {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("コネクションが返却されました。");
			connectionPool.remove(transactionID);
			connectionPool.put(connectionID.poll(), dba);
		}

	}
}
