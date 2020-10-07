package container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import transactionManager.TransactionHandler;

/**
 * ビジネスロジック生成メソッド
 * @param businessLogicName
 * @return Object
 */
public class BusinessLogicFactory{

	//BusinessLogic定義
	HashMap<String, BusinessLogicDefinition> businessLogicDefinitios;

	/*
	 * コンストラクタ
	 */
    public BusinessLogicFactory() {
    	//BusinessLogic定義取得ファイル
    	BusinessLogicConfigReader blcr ;
    	blcr = new BusinessLogicConfigReader();
		businessLogicDefinitios = blcr.read();
    }

    public BusinessLogicFactory(HashMap<String, BusinessLogicDefinition> businessLogicDefinitios) {
    	this.businessLogicDefinitios = businessLogicDefinitios;
    }

	public Object getBusinessLogic(String businessLogicName) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("getBusinessLogicメソッドを実行します。");

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("getBusinessLogicのクラスパスを取得します。");
		String businessLogicPlace = businessLogicDefinitios.get(businessLogicName).getType();

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("getBusinessLogicのインターフェースを取得します。");
		String businessLogicInterface = businessLogicDefinitios.get(businessLogicName).getInterfaceClass();

		//BusinessLogic格納クラス
		Object bl = null;

		try {

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("BusinessLogicFactoryクラスから、BusinessLogicクラスを取得します。");

			Class<?> businessLogicClazz = Class.forName(businessLogicPlace);
			Class<?> businessLogicInterfaceClazz = Class.forName(businessLogicInterface);

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("BusinessLogicInterfaceのクラスローダーの取得");
			ClassLoader classLoader = businessLogicClazz.getClassLoader();

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("BusinessLogicInterfaceの取得");
			Class<?>[] interfaces = new Class[] { businessLogicInterfaceClazz };

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("ハンドラーの取得");
			InvocationHandler hundler = new TransactionHandler(businessLogicClazz.getDeclaredConstructor().newInstance());

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("プロキシクラスの取得");
			bl = Proxy.newProxyInstance(classLoader, interfaces, hundler);

		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalArgumentException | SecurityException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return bl;
	}
}

