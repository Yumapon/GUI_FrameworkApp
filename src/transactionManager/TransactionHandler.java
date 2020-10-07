package transactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import annotation.Transactional;

public class TransactionHandler implements InvocationHandler {

	//BusinessLogicの実装クラス
	private final Object target;

	/*
	 * BusinessLogicの前後にトランザクション管理を行うための
	 * トランザクション管理クラス
	 */
	private TransactionManager tm = new DefaultTransactionManager();

	/*
	 * トランザクション管理ID
	 */
	String transactionID;

	/*
	 * コンストラクタ
	 */
	public TransactionHandler(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		System.out.println("Proxyクラスの処理を開始します。");

		//トランザクションの開始
		if(target.getClass().isAnnotationPresent(Transactional.class) || target.getClass().getMethod(method.getName(), method.getParameterTypes()).isAnnotationPresent(Transactional.class)) {
			tm.beginTransaction();
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("トランザクションは開始されました。");

			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("データベースの確保を行いました。");
		}else {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("トランザクションは開始されていません。");
		}
		Object ret = null;
		try {
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("ビジネスロジックのメソッドを実行します。");
			ret = method.invoke(target, args);
			return ret;
		} finally {
			if(tm.isTransaction()) {
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				System.out.println("トランザクションを終了し、コネクションも返却します。");
				tm.endTransaction();
			}

			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			System.out.println("Proxyクラスの処理を終了します。");
		}
	}

}
