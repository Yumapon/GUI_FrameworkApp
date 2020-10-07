package container;

import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;

public class BeanConfigReader implements Reader<HashMap<String, String>>{

	//Bean定義ファイルの名前
	String configFileName = DefaultSettingValueFile.BEANCONFIGFILENAME;

	//コンストラクタ
	public BeanConfigReader() {

	}
	public BeanConfigReader(String configFileName) {
		//指定されたファイル名をセット
		if(configFileName != null) {
			this.configFileName = configFileName;
		}
	}

	//yamlファイルからBean定義を取得するメソッド(引数なし)
	@Override
	public HashMap<String, String> read() {

		//ローカル変数の定義
		HashMap<String, String> beanDefinitions = new HashMap<String, String>();
		Yaml yaml = new Yaml();

		//YamlFileのデータを全て取得
		for(Object beanDefinition : yaml.loadAll(getClass().getResourceAsStream(configFileName))){
			//HashMap型のBean定義格納クラスにデータを格納
			beanDefinitions.put(((BeanDefinition) beanDefinition).getName(), ((BeanDefinition) beanDefinition).getType());
		}

		return beanDefinitions;
	}

	//yamlファイルからBean定義を取得するメソッド(引数あり)※基本使用しないハズ
	@Override
	public HashMap<String, String> read(String configFileName) {

		//指定されたファイル名をセット
		if(configFileName != null) {
			this.configFileName = configFileName;
		}

		//ローカル変数の定義
		HashMap<String, String> beanDefinitions = new HashMap<String, String>();
		Yaml yaml = new Yaml();

		//YamlFileのデータを全て取得
		for(Object beanDefinition : yaml.loadAll(getClass().getResourceAsStream(configFileName))){
			//HashMap型のBean定義格納クラスにデータを格納
			beanDefinitions.put(((BeanDefinition) beanDefinition).getName(), ((BeanDefinition) beanDefinition).getType());
		}

		return beanDefinitions;
	}

}
