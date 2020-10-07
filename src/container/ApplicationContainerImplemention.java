/**
 *ApplicationContainerクラス
 */
package container;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import annotation.FormInjection;
import annotation.Service;

/**
 * ApplicationContainerクラス
 * @author okamotoyuuma
 */
public class ApplicationContainerImplemention implements ApplicationContainer{

	//生成するインスタンスの場所
	private String instancePlace;

	//Bean定義取得クラス
	BeanConfigReader bdr = new BeanConfigReader();
	//Bean定義
	HashMap<String, String> beanDefinitions;
	//Bean
	//Object bean;

	//Action定義取得クラス
	ActionConfigReader acr;
	//Action定義
	HashMap<String, ActionDefinition> actionDefinitions;

	//BusinessLogic定義取得ファイル
	BusinessLogicConfigReader blcr ;
	//BusinessLogic定義
	HashMap<String, BusinessLogicDefinition> businessLogicDefinitios;

	//コンストラクタ
	//引数なし（設定ファイルの名前がデフォルトで良い場合）
	/**
	 * デフォルトコンストラクタ
	 */
	public ApplicationContainerImplemention(){
		bdr = new BeanConfigReader();
		beanDefinitions = bdr.read();

		acr = new ActionConfigReader();
		actionDefinitions = acr.read();

		blcr = new BusinessLogicConfigReader();
		businessLogicDefinitios = blcr.read();
	}

	//引数あり（設定ファイルの名前を独自に設定された場合）
	/**
	 * 引数ありコンストラクタ
	 * @param instancePlace
	 */
	public ApplicationContainerImplemention(String beanConfigFileName, String actionConfigFileName, String businessLogicConfigName){
		bdr = new BeanConfigReader(beanConfigFileName);
		beanDefinitions = bdr.read();

		acr = new ActionConfigReader(actionConfigFileName);
		actionDefinitions = acr.read();

		blcr = new BusinessLogicConfigReader(businessLogicConfigName);
		businessLogicDefinitios = blcr.read();
	}

	//リロードメソッド 生成の後にBean定義ファイル名を変更する
	/**
	 * 設定ファイル名を変更し、Bean定義ファイルを再度取得する
	 * @param configFileName
	 */
	@Override
	public void beanDefinitionReload(String configFileName) {
		beanDefinitions = bdr.read(configFileName);
	}

	//インスタンス生成メソッド(引数あり)
	/**
	 * Beanの生成(インスタンス名から生成)
	 * @param instanceName
	 * @return Bean
	 */
	@Override
	public Object generator(String instanceName) {

		//生成するインスタンス
		Object obj = null;
		//インスタンス名からクラスパスを取得
		instancePlace = beanDefinitions.get(instanceName);

		try {
			//インスタンスの作成
			Class<?> instanceClass = Class.forName(instancePlace);
			obj = instanceClass.getDeclaredConstructor().newInstance();
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("指定されたクラスパスのBean生成が完了しました。");
		} catch (ClassNotFoundException e) {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//例外内容
			System.out.println("<error>指定されたクラスパスが見つかりませんでした。（" + instancePlace + "が見つかりません。）");
			//スタックトレース
			e.printStackTrace();
		}catch (Exception e) {
			//スタックトレース
			e.printStackTrace();
		}
		return obj;
	}


	//インスタンス生成メソッド(引数なし)
	/**
	 * Beanの生成(クラスパスを指定して生成する場合)
	 * @return Bean
	 */
		@Override
		public Object generator() {

			//生成するインスタンス
			Object obj = null;

			try {
				//インスタンスの作成
				Class<?> instanceClass = Class.forName(instancePlace);
				obj = instanceClass.getDeclaredConstructor().newInstance();
				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println( "指定されたクラスパスのBean生成が完了しました。");
			} catch (ClassNotFoundException e) {
				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//例外内容
				if(instancePlace == null) {
					System.out.println("<error>クラスパスが指定されていません。");
				}else {
					System.out.println("<error>指定されたクラスパスが見つかりませんでした。（" + instancePlace + "が見つかりません。）");
				}
			}catch (Exception e) {
				//スタックトレース
				e.printStackTrace();
			}
			return obj;
		}


	/**
	 * Actionクラス生成メソッド
	 * @param actionName
	 * @return Object
	 */
	@Override
	public Object getAction(String actionName) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("getActionメソッドを実行します。");

		Object actionObj = null;

		//actionNameからクラスの場所を取得
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("actionNameからactionPlaceを取得します。");
		String actionPlace = actionDefinitions.get(actionName).getType();
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		System.out.println("actionPlaceの取得が完了しました。actionPlace = " + actionPlace);

		try {
			//Actionクラスの生成
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("クラスの取得を行います。");
			Class<?> actionClazz = Class.forName(actionPlace);

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("インスタンスの生成を行います");
			actionObj = actionClazz.getDeclaredConstructor().newInstance();

			//Fieldの取得と、ビジネスロジックのインジェクション
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("フィールドの取得を行います");
			Field[] fields = actionClazz.getDeclaredFields();
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("フィールドの取得が完了しました。 Fieldの数：" + fields.length);

			//取得したField一覧の表示
			int i = 1;
			for(Field field : fields) {
				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println(i + "：" + field.getName());
				i++;
			}

			for(Field f : fields) {
				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println("フィールドのアノテーションを確認中");
				//@Serviceがついている場合、ビジネスロジックを取得し、インジェクト
				if(f.isAnnotationPresent(Service.class)) {
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println(f.getName() + "に@Serviceが付与されているのを確認しました。");

					//ビジネスロジックを取得
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("ビジネスロジックを取得します。");
					Object blObject = new BusinessLogicFactory(businessLogicDefinitios).getBusinessLogic(f.getName());
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("ビジネスロジックの取得が完了しました。");

					//ビジネスロジックをセット
					f.setAccessible(true);//無理やり書き込む。
					f.set(actionObj, blObject);
					f.setAccessible(false);

	            }else if(f.isAnnotationPresent(FormInjection.class)){
	            	//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println(f.getName() + "に@FormInjectionが付与されているのを確認しました。");

	            	//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println(f.getName() + "のFormインスタンスを生成します。");
					Object bean = generator(f.getName());

					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("Formをインジェクトします。");
					f.setAccessible(true);//無理やり書き込む。
					f.set(actionObj, bean);
					f.setAccessible(false);

	            }else {
	            	//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println(f.getName() + "@Injectionも@Serviceも付与されていない。");
	            }
	        }

		} catch (ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException | SecurityException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return actionObj;
	}



	//getter・setter
	/**
	 * Beanクラスパスの取得
	 * @return Beanのクラスパス
	 */
	public String getInstancePlace() {
		return instancePlace;
	}

	/**
	 * Beanクラスパスの設定
	 * @param instancePlace
	 */
	public void setInstancePlace(String instancePlace) {
		this.instancePlace = instancePlace;
	}

	//test用
	public Object getbl(String bl1) {
		BusinessLogicFactory blf = new BusinessLogicFactory(businessLogicDefinitios);
		Object obj = blf.getBusinessLogic(bl1);
		return obj;
	}

}

//バインディング用オブジェクト取得メソッド
/*
public InstanceAndClassObjectforServlet getCAMS(String instanceName) {
	//ログ発生箇所
	System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
	//処理内容
	System.out.println("getCAMSメソッドを実行します。");

	InstanceAndClassObjectforServlet cams = new InstanceAndClassObjectforServlet();
	//インスタンス名からクラスパスを取得
	instancePlace = beanDefinitions.get(instanceName);

	try {
		//インスタンスの作成
		Class<?> instanceClass = Class.forName(instancePlace);
		bean = instanceClass.getDeclaredConstructor().newInstance();
		//camsにインスタンスを格納
		cams.setObj(bean);
		//camsにメソッドを格納
		cams.setClazz(instanceClass);
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("指定されたクラスパスのBean生成が完了しました。");
	} catch (ClassNotFoundException e) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//例外内容
		if(instancePlace == null) {
			System.out.println("<error>クラスパスが指定されていません。");
		}else {
			System.out.println("<error>指定されたクラスパスが見つかりませんでした。（" + instancePlace + "が見つかりません。）");
		}
	}catch (Exception e) {
		//スタックトレース
		e.printStackTrace();
	}

	return cams;
}
*/

