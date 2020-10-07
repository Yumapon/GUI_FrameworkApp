package container;

//インスタンス情報格納用Bean
public class BeanDefinition {

	//インスタンス名（コンテナからBean取得する際のKey）
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
