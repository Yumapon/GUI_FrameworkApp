package transactionManager;

import java.sql.Connection;
import java.sql.SQLException;

import db_access.ConnectionPool;
import db_access.DBAccess;
import exception.AlreadyTransactionBeganException;
import exception.DoNotHaveDBAccessException;
import exception.NoDBAccessException;
import exception.NotBeginTransactionException;

public class DefaultTransactionManager implements TransactionManager {

	//Transactionの状態
	boolean transactionStatus = false;

	//コネクション
	Connection conn = null;
	DBAccess dba = null;

	//コネクションプール
	ConnectionPool cp = ConnectionPool.getInstance();

	//タイムアウト値
	final int SLEEPTIME = 300;
	boolean repeat = false;

	//トランザクションID
	String transactionID = null;

	@Override
	//トランザクションの開始メソッド
	public void beginTransaction() throws AlreadyTransactionBeganException {
		//すでにトランザクション開始されている場合は、実行時にエラーを吐く
		if (transactionStatus) {
			throw new AlreadyTransactionBeganException();
		}
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		System.out.println("トランザクションを開始します");
		//TransactionStatusを開始状態に
		transactionStatus = true;
		//トランザクションIDの発行
		Thread currentThread = Thread.currentThread(); // 自分自身のスレッドを取得
		long threadID = currentThread.getId();
		transactionID = String.valueOf(threadID);

		getConnection(transactionID);
	}

	@Override
	//ロールバックメソッド
	public void rollback() {
		try {
			conn.rollback();
			dba.closeConnection();
			transactionStatus = false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	//トランザクションの状態管理メソッド
	public boolean isTransaction() {
		return transactionStatus;
	}

	@Override
	//トランザクションの終了メソッド
	public void endTransaction() {
		if(this.isTransaction()) {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("トランザクションを終了します");
			try {
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				System.out.println("DBへ結果をCOMMITします");
				cp.getDBAccess(transactionID).getConnection().commit();
			} catch (NotBeginTransactionException | SQLException e) {
				e.printStackTrace();
			}
		}
		//TransactionStatusを終了状態に
		transactionStatus = false;
		//コネクションの返却
		this.returnConnection(transactionID);

	}

	@Override
	//コネクションを確保するメソッド（ここを動かす動かさないでコネクションの確保確保しないは管理できる）
	public void getConnection(String transactionID) {
		if (this.isTransaction()) {
			//Connectionの確保
			try {
				cp.checkoutDBAccess(transactionID);
			} catch (NoDBAccessException e) {
				if (!repeat) {
					//時間をおいてもう一度アクセス
					try {
						System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
						System.out.println("コネクションの取得に再チャレンジします。");
						Thread.sleep(SLEEPTIME);
						repeat = true;
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				} else {
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					System.out.println("処理中のトランザクションの数が設定値を超えています。");
				}
			}
		} else {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("トランザクションを開始してください");
		}
	}

	@Override
	public void returnConnection(String transactionID) {
		//コネクションの返却
		try {
			cp.returnDBAccess(transactionID);
		} catch (DoNotHaveDBAccessException e) {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("DBAccessを所持していません。");
			e.printStackTrace();
		}
		dba = null;
	}

}
