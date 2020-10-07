package container;

/*
 * 各種設定ファイルリーダーのスーパーインターフェース
 */
public interface Reader <T>{

	T read();

	T read(String fileName);

}
