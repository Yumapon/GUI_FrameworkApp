package transactionManager;

import exception.AlreadyTransactionBeganException;

public interface TransactionManager {

	//トランザクションの開始
	void beginTransaction() throws AlreadyTransactionBeganException;

	//ロールバック
	void rollback();

	//トランザクションの終了
	void endTransaction();

	//トランザクションの状態確認
	boolean isTransaction();

	//コネクションの確保
	void getConnection(String transactionID);

	//コネクションの返却
	void returnConnection(String transactionID);

}
