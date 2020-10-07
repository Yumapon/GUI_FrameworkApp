package dbMapper;

import java.util.ArrayList;
import java.util.Optional;

//TにはEntityクラスの型を、IDにはPrimaryKeyを記載する
public interface Repository<T, ID>{

	/*
	 * 単一テーブルへの操作
	 */

	//指定されたエンティティを保存します。
	void save(T entity);

	//指定された ID で識別されるエンティティを返します。
	Optional<T> findById(ID primaryKey);

	//すべてのエンティティを返します。
	ArrayList<T> findAll();

	//指定された条件のエンティティを返します。
	ArrayList<T> findAll(T entity);

	//エンティティの数を返します。
	int count();

	//指定されたエンティティを削除します。
	void delete(T entity);

	//指定された ID のエンティティが存在するかどうかを示します。
	boolean existsById(ID primaryKey);

	/*
	 * 複数テーブルへの操作
	 */

	//指定された ID で識別されるエンティティを返します。
	Optional<T> multiFindById(ID primaryKey);/*@ManyToManyに関しては未実装*/

	//すべてのエンティティを返します。
	Iterable<T> multifindAll();/*未実装*/

}
