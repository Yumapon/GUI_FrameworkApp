/**
 * Action定義クラス
 *
 */
package container;

/**
 * Action定義クラス
 * @author okamotoyuuma
 *
 */
public class ActionDefinition {

	//インスタンス名（コンテナから取得する際のKey）
		String name;

		//インスタンスのクラスパス
		String type;


		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}


}
