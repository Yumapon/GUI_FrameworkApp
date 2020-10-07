/*
 *
 * ActionConfigReader(Action定義ファイル読み込みクラス)
 *
 * 設定ファイルの形式はyaml限定。
 */
package container;

import java.util.HashMap;

import org.yaml.snakeyaml.Yaml;

/**
 * ActionConfigReader
 *
 * @author okamoto yuma
 *
 */
public class ActionConfigReader implements Reader<HashMap<String, ActionDefinition>>{

	//Bean定義ファイルの名前
		String configFileName = DefaultSettingValueFile.ACTIONCONFIGFILENAME;

		//コンストラクタ
		public ActionConfigReader() {

		}
		public ActionConfigReader(String configFileName) {
			//指定されたファイル名をセット
			if(configFileName != null) {
				this.configFileName = configFileName;
			}
		}

		//yamlファイルからBean定義を取得するメソッド(引数なし)
		/**
		 * @return アクション設定ファイルの中身
		 */
		@Override
		public HashMap<String, ActionDefinition> read() {

			//ローカル変数の定義
			HashMap<String, ActionDefinition> actionDefinitions = new HashMap<String, ActionDefinition>();
			Yaml yaml = new Yaml();

			//YamlFileのデータを全て取得
			for(Object actionDefinition : yaml.loadAll(getClass().getResourceAsStream(configFileName))){
				//HashMap型のBean定義格納クラスにデータを格納
				actionDefinitions.put(((ActionDefinition) actionDefinition).getName(), ((ActionDefinition) actionDefinition));
			}

			return actionDefinitions;
		}

		//yamlファイルからBean定義を取得するメソッド(引数あり)※基本使用しないハズ
		/**
		 * @param configFileName
		 * @return アクション設定ファイルの中身
		 */
		@Override
		public HashMap<String, ActionDefinition> read(String configFileName) {

			//指定されたファイル名をセット
			if(configFileName != null) {
				this.configFileName = configFileName;
			}

			//ローカル変数の定義
			HashMap<String, ActionDefinition> actionDefinitions = new HashMap<String, ActionDefinition>();
			Yaml yaml = new Yaml();

			//YamlFileのデータを全て取得
			for(Object actionDefinition : yaml.loadAll(getClass().getResourceAsStream(configFileName))){
				//HashMap型のBean定義格納クラスにデータを格納
				actionDefinitions.put(((ActionDefinition) actionDefinition).getName(), ((ActionDefinition) actionDefinition));
			}

			return actionDefinitions;
		}

}
