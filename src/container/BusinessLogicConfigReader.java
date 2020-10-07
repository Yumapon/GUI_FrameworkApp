package container;

import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;

public class BusinessLogicConfigReader implements Reader<HashMap<String, BusinessLogicDefinition>>{

	//Bean定義ファイルの名前
	String configFileName = DefaultSettingValueFile.BUSINESSLOGICCONFIGFILENAME;

	//コンストラクタ
	public BusinessLogicConfigReader() {

	}

	public BusinessLogicConfigReader(String configFileName) {
		//指定されたファイル名をセット
		if (configFileName != null) {
			this.configFileName = configFileName;
		}
	}

	//yamlファイルからビジネスロジック定義を取得するメソッド(引数なし)
	@Override
	public HashMap<String, BusinessLogicDefinition> read() {

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println(configFileName + "の読み込みを開始します。");

		//ローカル変数の定義
		HashMap<String, BusinessLogicDefinition> businessLogicDefinitions = new HashMap<String, BusinessLogicDefinition>();
		Yaml yaml = new Yaml();

		//YamlFileのデータを全て取得
		for (Object businessLogicDefinition1 : yaml.loadAll(getClass().getResourceAsStream(configFileName))) {
			//HashMap型のBean定義格納クラスにデータを格納
			businessLogicDefinitions.put(((BusinessLogicDefinition) businessLogicDefinition1).getName(),((BusinessLogicDefinition) businessLogicDefinition1));
		}

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println(configFileName + "の読み込みが完了しました。");

		return businessLogicDefinitions;
	}

	//yamlファイルからビジネスロジック定義を取得するメソッド(引数あり)※基本使用しないハズ
	@Override
	public HashMap<String, BusinessLogicDefinition> read(String configFileName) {

		//指定されたファイル名をセット
		if (configFileName != null) {
			this.configFileName = configFileName;
		}

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println(configFileName + "の読み込みを開始します。");

		//ローカル変数の定義
		HashMap<String, BusinessLogicDefinition> businessLogicDefinitions = new HashMap<String, BusinessLogicDefinition>();
		Yaml yaml = new Yaml();

		//YamlFileのデータを全て取得
		for (Object businessLogicDefinition2 : yaml.loadAll(getClass().getResourceAsStream(configFileName))) {
			//HashMap型のBean定義格納クラスにデータを格納
			businessLogicDefinitions.put(((BusinessLogicDefinition) businessLogicDefinition2).getName(),((BusinessLogicDefinition) businessLogicDefinition2));
		}

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println(configFileName + "の読み込みが完了しました。");

		return businessLogicDefinitions;
	}

}
