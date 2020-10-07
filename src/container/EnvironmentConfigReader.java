package container;

import org.yaml.snakeyaml.Yaml;

public class EnvironmentConfigReader implements Reader<DBConfig>{

	//DBの設定ファイル
	static String configFileName = "DBProfile.yaml";

	//yamlファイルからDB設定を取得するメソッド(引数なし)
	@Override
	public DBConfig read() {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println(configFileName + "の読み込みを開始します。");

		Yaml yaml = new Yaml();
		DBConfig dbc = (DBConfig) yaml.load(getClass().getResourceAsStream(configFileName));

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println(configFileName + "の読み込みが完了しました。");

		return dbc;
	}

	//yamlファイルからDB設定を取得するメソッド(引数あり)
	@Override
	public DBConfig read(String setConfigFileName) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println(configFileName + "の読み込みを開始します。");

		//指定されたファイル名をセット
		if (configFileName != null) {
			configFileName = setConfigFileName;
		}

		Yaml yaml = new Yaml();
		DBConfig dbc = (DBConfig) yaml.load(getClass().getResourceAsStream(configFileName));

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println(configFileName + "の読み込みが完了しました。");

		return dbc;
	}

}
