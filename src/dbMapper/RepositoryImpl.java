package dbMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import annotation.ManyToMany;
import annotation.ManyToOne;
import annotation.OneToMany;
import annotation.OneToOne;
import annotation.Table;
import annotation.column;
import annotation.id;
import query.Query;
import query.QueryInfo;

public class RepositoryImpl<T, ID> implements Repository<T, ID> {

	//Queryクラス
	Query query = new Query();
	//Query用オブジェクト
	QueryInfo qi = new QueryInfo();

	//Entity情報
	private String tableName;
	private String idName;
	private ArrayList<String> columnNames = new ArrayList<>();
	private Class<T> entityType;

	/**
	 * コンストラクタ
	 * QueryInfoにEntityの情報を格納する
	 * @param t
	 */
	@SuppressWarnings("unchecked")
	public RepositoryImpl(T... t) {
		//EntityのTypeを取得する
		Class<T> entityType = (Class<T>) t.getClass().getComponentType();
		this.entityType = entityType;

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		System.out.println(entityType);

		//Entity情報を取得
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("Entityクラス情報の取得を開始します");
		try {
			T entity = this.entityType.getDeclaredConstructor().newInstance();

			//Entityのテーブル名を取得
			if (entity == null)
				System.out.println("nullです");
			else if (entity.getClass().isAnnotationPresent(Table.class)) {
				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println("@Tableを発見 テーブル名を取得します");
				this.tableName = entity.getClass().getAnnotation(Table.class).value();
			} else {
				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.print(entity.getClass().getName());
				System.out.println("@Tableが付与されていません、付与してください");
			}

			//EntityからPrimaryKeyのカラム名を取り出す
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("Entityクラスのカラム名を取得します");
			for (Field f : entity.getClass().getDeclaredFields()) {
				//カラム名の取得
				if (f.isAnnotationPresent(column.class))
					columnNames.add(f.getName());
				//@idが付与されているメンバを探索
				if (f.isAnnotationPresent(id.class)) {
					try {
						//ログ発生箇所
						System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
						//処理内容
						System.out.println("EntityクラスのPrimaryKey名を取得します");
						//Field名を取得する
						idName = f.getName();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("Entityクラス情報の取得が完了しました");

		//Query用オブジェクトにEntity情報を格納
		qi.setTableName(tableName);
		qi.setIdName(idName);
		qi.setColumnNames(columnNames);
	}

	/**
	 * Entity格納メソッド
	 * @param T entity
	 * @return 更新レコード数
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void save(T entity) {
		//QueryInfoの初期化
		qi.clearQueryInfo();

		//PrimaryKeyの値
		ID idValue = null;

		try {
			//entityのカラム値を取得
			for (Field f : entity.getClass().getDeclaredFields()) {
				try {
					if(!f.isAnnotationPresent(column.class)) {
						continue;
					}
					System.out.println(f.getName());
					f.setAccessible(true);
					qi.getColumnValues().put(f.getName(), f.get(entity).toString());
					f.setAccessible(false);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			//EntityからPrimaryKeyの値を取り出す
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("EntityクラスのPrimaryKeyの値を取得する");

			Field primaryIdField = entity.getClass().getDeclaredField(idName);
			primaryIdField.setAccessible(true);
			idValue = (ID) primaryIdField.get(entity);
			primaryIdField.setAccessible(false);

		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}

		//すでにDBに格納されているEntityかどうかを確認する。
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("Entityがすでに格納されているか確認します");
		if (existsById(idValue)) {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("Entityがすでに格納されているため、上書きを行います");

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("SQLを発行します");
			//SQL文を発行
			String sql = query.createUpdateSql(qi);

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("SQLを実行します");
			//上書き処理を行う
			query.executeUpdate(sql);
		} else {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("Entityは格納されていないため、DBへ新規登録を行います");

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("SQLを発行します");
			//SQL文を発行
			String sql = query.createInsertSql(qi);

			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("SQLを実行します");
			//登録処理を行う
			query.executeUpdate(sql);
		}
	}

	/**
	 * 主キーでEntityを探索するメソッド
	 */
	//TODO
	@Override
	public Optional<T> findById(ID primaryKey) {
		//QueryInfoの初期化
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("QueryInfoのListを初期化します");
		qi.clearQueryInfo();

		//return値
		Optional<T> entityOpt = null;
		T entity = null;

		//SQL文を発行
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("SQL文を生成します");
		qi.getColumnValues().put(idName, primaryKey.toString());
		String sql = query.createSelectSql(qi);

		System.out.println(sql);

		//SQLを実行
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("SQL文を実行します");
		ResultSet result = query.executeQuery(sql);

		//resultから値を取得
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("結果からEntity情報を取得します");
		try {
			entity = this.entityType.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		Field f;
		try {
			//ResultSetのカーソルを先頭に持ってくる
			result.beforeFirst();
			if (result.next()) {
				for (String column : columnNames) {
					Object columnValue = result.getObject(column);
					f = entity.getClass().getDeclaredField(column);
					f.setAccessible(true);
					f.set(entity, columnValue);
					f.setAccessible(false);
				}

				//Entityをオプショナル型に変換
				entityOpt = Optional.of(entity);
			} else {
				entityOpt = Optional.empty();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return entityOpt;
	}

	@Override
	public ArrayList<T> findAll() {
		//QueryInfoの初期化
		qi.clearQueryInfo();

		//Entity格納用
		ArrayList<T> list = new ArrayList<>();

		//Entity
		T entity = null;

		//SQL文を発行
		String sql = query.createSelectSql(qi);

		//SQLを実行
		ResultSet result = query.executeQuery(sql);

		//resultから値を取得
		Field f;
		try {
			while (result.next()) {
				entity = this.entityType.getDeclaredConstructor().newInstance();
				for (String column : columnNames) {
					Object columnValue = result.getObject(column);
					f = entity.getClass().getDeclaredField(column);
					f.setAccessible(true);
					f.set(entity, columnValue);
					f.setAccessible(false);
				}
				list.add(entity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}

		//return result;
		return list;
	}

	@Override
	public ArrayList<T> findAll(T entity) {
		//QueryInfoの初期化
		qi.clearQueryInfo();

		//Entity格納用
		ArrayList<T> list = new ArrayList<>();

		//Entityから検索条件を取得する
		for (Field f : entity.getClass().getDeclaredFields()) {
			try {
				f.setAccessible(true);
				if (f.get(entity) == null || f.getName().equals(idName))
					continue;
				qi.getColumnValues().put(f.getName(), f.get(entity).toString());
				f.setAccessible(false);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		//SQL文を発行
		String sql = query.createSelectSql(qi);

		//SQLを実行
		ResultSet result = query.executeQuery(sql);

		//resultから値を取得
		Field f;
		try {
			while (result.next()) {
				entity = this.entityType.getDeclaredConstructor().newInstance();
				for (String column : columnNames) {
					Object columnValue = result.getObject(column);
					f = entity.getClass().getDeclaredField(column);
					f.setAccessible(true);
					f.set(entity, columnValue);
					f.setAccessible(false);
				}
				list.add(entity);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}

		//return result;
		return list;
	}

	@Override
	public int count() {
		//QueryInfoの初期化
		qi.clearQueryInfo();

		//SQL文の生成
		String sql = query.createCheckCountSql(qi);

		//SQL文の実行
		ResultSet rs = query.executeQuery(sql);

		//ResultSetからレコード数を受け取る
		int i = 0;
		try {
			rs.next();
			i = rs.getInt("COUNT(*)");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return i;
	}

	/**
	 * Entity削除用メソッド
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void delete(T entity) {
		//QueryInfoの初期化
		qi.clearQueryInfo();

		//PrimaryKeyの値
		ID idValue;

		//EntityからPrimaryKeyの値を取り出す
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("EntityクラスのPrimaryKeyの値を取得します");
		Field primaryIdField;
		try {
			primaryIdField = entity.getClass().getDeclaredField(idName);
			primaryIdField.setAccessible(true);
			idValue = (ID) primaryIdField.get(entity);
			primaryIdField.setAccessible(false);

			//主キーが格納されていない場合
			if (idValue == null) {
				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println("主キーに何もパラメータがセットされていません、カラムの値を取得します");
				//entityのカラム値を取得
				for (Field f : entity.getClass().getDeclaredFields()) {
					try {
						f.setAccessible(true);
						if (f.get(entity) == null || f.getName().equals(idName))
							continue;
						qi.getColumnValues().put(f.getName(), f.get(entity).toString());
						f.setAccessible(false);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			} else {
				//ログ発生箇所
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
				//処理内容
				System.out.println("主キーにパラメータがセットされています");
				qi.getColumnValues().put(idName, idValue.toString());
			}
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		//SQLの生成
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("SQLを生成します");
		String sql = query.createDeleteSql(qi);

		//SQLの実行
		int i = query.executeUpdate(sql);
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println(i + "件更新しました");
	}

	/**
	 *主キーでEntityが格納されているか確認するメソッド
	 *@param ID
	 *@return boolean
	 */
	@Override
	public boolean existsById(ID primaryKey) {
		//SQLを発行する
		qi.getColumnValues().put(idName, primaryKey.toString());
		String sql = query.createCheckRecordSql(qi);

		//SQLの実行
		ResultSet rs = query.executeQuery(sql);

		//ResultSetからレコード数を受け取る
		int i = 0;
		try {
			rs.next();
			i = rs.getInt("COUNT(*)");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (i <= 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Optional<T> multiFindById(ID primaryKey){
		//まずは@columnに値をセットする
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("@columnに値をセットします");
		//return用Object
		Optional<T> entityOpt = findById(primaryKey);

		//親Entity
		T entity;
		//リレーションアノテーションが設定されているカラムの情報を取得する
		if (entityOpt.isPresent()) {
			entity = entityOpt.get();
			//親EntityクラスのFieldを取得する
			for (Field f : entity.getClass().getDeclaredFields()) {
				//リレーション情報が付与されているFieldを取得
				if (f.isAnnotationPresent(column.class))
					continue;
				//@OneToOneがついている場合
				else if (f.isAnnotationPresent(OneToOne.class)) {
					//子Entityの生成
					Object childEntity = getEntityObj(f);

					//親EntityのPrimaryKeyで子Entityを検索
					//子Entityのテーブル名を取得
					String childTableName = getTableName(childEntity);

					//子Entityからカラム名を取り出す
					String childIdName = null;
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("子EntityクラスのPrimaryKeyを取得します");
					for (Field f1 : childEntity.getClass().getDeclaredFields()) {
						//@idが付与されているメンバを探索
						if (f1.isAnnotationPresent(id.class)) {
							try {
								//ログ発生箇所
								System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
								//処理内容
								System.out.println("EntityクラスのPrimaryKey名を取得します");
								//Field名を取得する
								childIdName = f1.getName();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							}
						}
					}

					//QueryInfoオブジェクトの発行
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("子EntityクラスのQueryInfoを作成します");
					QueryInfo qi2 = new QueryInfo();
					qi2.setTableName(childTableName);
					qi2.setIdName(childIdName);
					qi2.getColumnValues().put(childIdName, primaryKey.toString());

					//SQL文の生成(子Entity)
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("SQLを生成します（子クラス）");
					String sql1 = query.createSelectSql(qi2);

					//SQL文の実行(子Entity)
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("SQLを実行します（子クラス）");
					ResultSet rs1 = query.executeQuery(sql1);

					//子クラスに結果を代入
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("結果をセットします（子クラス）");
					try {
						if (rs1.next()) {
							for (Field f2 : childEntity.getClass().getDeclaredFields()) {
								if (!f2.isAnnotationPresent(column.class))
									continue;
								f2.setAccessible(true);
								f2.set(childEntity, rs1.getObject(f2.getName()));
								f2.setAccessible(false);
							}
						}
					} catch (SecurityException | IllegalArgumentException | IllegalAccessException | SQLException e1) {
						e1.printStackTrace();
					}

					//生成した子クラスを親EntityのFieldにSET
					f.setAccessible(true);
					try {
						f.set(entity, childEntity);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					f.setAccessible(false);

					continue;

					//@ManyToOneがついている場合
				} else if (f.isAnnotationPresent(ManyToOne.class)) {
					//子Entityの生成
					Object childEntity = getEntityObj(f);

					//子Entityのテーブル名を取得
					String childTableName = getTableName(childEntity);

					//子Entityからカラム名を取り出す
					String childIdName = null;
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("子EntityクラスのPrimaryKeyを取得します");
					for (Field f1 : childEntity.getClass().getDeclaredFields()) {
						//@idが付与されているメンバを探索
						if (f1.isAnnotationPresent(id.class)) {
							try {
								//ログ発生箇所
								System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
								//処理内容
								System.out.println("EntityクラスのPrimaryKey名を取得します");
								//Field名を取得する
								childIdName = f1.getName();
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							}
						}
					}

					//QueryInfoオブジェクトの発行
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("子EntityクラスのQueryInfoを作成します");
					String idValue = null;
					try {
						Field f1 = entity.getClass().getDeclaredField(idName);
						f1.setAccessible(true);
						idValue = (String) f1.get(entity);
						f1.setAccessible(false);
					} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
							| SecurityException e) {
						e.printStackTrace();
					}
					QueryInfo qi2 = new QueryInfo();
					qi2.setTableName(childTableName);
					qi2.setIdName(childIdName);
					qi2.getColumnValues().put(childIdName, idValue);

					//SQL文の生成(子Entity)
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("SQLを生成します（子クラス）");
					String sql1 = query.createSelectSql(qi2);

					//SQL文の実行(子Entity)
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("SQLを実行します（子クラス）");
					ResultSet rs1 = query.executeQuery(sql1);

					//子クラスに結果を代入
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("結果をセットします（子クラス）");
					try {
						if (rs1.next()) {
							for (Field f2 : childEntity.getClass().getDeclaredFields()) {
								if (!f2.isAnnotationPresent(column.class))
									continue;
								f2.setAccessible(true);
								f2.set(childEntity, rs1.getObject(f2.getName()));
								f2.setAccessible(false);
							}
						}
					} catch (SecurityException | IllegalArgumentException | IllegalAccessException | SQLException e1) {
						e1.printStackTrace();
					}

					//生成した子クラスを親EntityのFieldにSET
					f.setAccessible(true);
					try {
						f.set(entity, childEntity);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					f.setAccessible(false);

					continue;

					//@OneToManyがついている場合
				} else if (f.isAnnotationPresent(OneToMany.class)) {
					//子Entityの生成
					Object childEntity = null;
					Type superType = f.getGenericType();
					System.out.println(superType.getTypeName());
					Type[] types = ((ParameterizedType)superType).getActualTypeArguments();
					try {
						Class<?> clazz = Class.forName(types[0].getTypeName());
						childEntity = clazz.getDeclaredConstructor().newInstance();
					} catch (ClassNotFoundException e2) {
						e2.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}

					//子Entityのテーブル名を取得
					String childTableName = getTableName(childEntity);

					//@OneToManyから連結カラム名を取り出す
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("@OneToManyから連結カラム名を取得します");
					String[] mapping = f.getAnnotation(OneToMany.class).mappingBy();

					//検索条件を取得
					Map<String, String> columnValues = new HashMap<>();
					Field f2 = null;
					String columnValue = null;
					for(String columnName : mapping) {
						try {
							f2 = entity.getClass().getDeclaredField(columnName);
							f2.setAccessible(true);
							columnValue = f2.get(entity).toString();
							f2.setAccessible(false);
						} catch (NoSuchFieldException | SecurityException e) {
							e.printStackTrace();
						}catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
						columnValues.put(columnName, columnValue);
					}

					QueryInfo qi2 = new QueryInfo();
					qi2.setTableName(childTableName);
					qi2.setColumnValues(columnValues);

					//主キー検索でないので、カラム名情報を取得する
					ArrayList<String> columnNames = new ArrayList<>();
					for(String columnName : mapping) {
						columnNames.add(columnName);
					}

					qi2.setColumnNames(columnNames);

					//SQL文の生成(子Entity)
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("SQLを生成します（子クラス）");
					String sql1 = query.createSelectSql(qi2);

					//SQL文の実行(子Entity)
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("SQLを実行します（子クラス）");
					ResultSet rs1 = query.executeQuery(sql1);

					//子クラスに結果を代入
					//ログ発生箇所
					System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
					//処理内容
					System.out.println("結果をセットします（子クラス）");
					List<Object> list = new ArrayList<>();
					try {
						//子クラスに検索結果の値をセットし、リストに格納する。
						while (rs1.next()) {
							for (Field f3 : childEntity.getClass().getDeclaredFields()) {
								if (!f3.isAnnotationPresent(column.class))
									continue;
								f3.setAccessible(true);
								f3.set(childEntity, rs1.getObject(f3.getName()));
								f3.setAccessible(false);
							}
							list.add(childEntity);
						}
					} catch (SecurityException | IllegalArgumentException | SQLException | IllegalAccessException e1) {
						e1.printStackTrace();
					}

					//生成した子クラスを親EntityのFieldにSET
					f.setAccessible(true);
					try {
						f.set(entity, list);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					f.setAccessible(false);

					continue;

					//@ManyToManyがついている場合
				} else if (f.isAnnotationPresent(ManyToMany.class)) {
					//TODO
					//今回は未実装
				}
			}
		} else {
			return entityOpt;
		}
		return entityOpt;
	}

	@Override
	public Iterable<T> multifindAll() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	/**
	 *インスタンスのテーブル名を取得するメソッド
	 * @param childEntity
	 * @return
	 */
	private String getTableName(Object childEntity) {
		String childTableName = null;
		//子Entityクラス用のQueryInfoの生成
		if (childEntity.getClass().isAnnotationPresent(Table.class)) {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.println("子Entityの@Tableを発見 テーブル名を取得します");
			childTableName = childEntity.getClass().getAnnotation(Table.class).value();
		} else {
			//ログ発生箇所
			System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
			//処理内容
			System.out.print(childEntity.getClass().getName());
			System.out.println("子Entityに@Tableが付与されていません、付与してください");
		}
		return childTableName;

	}

	/**
	 * Single用子Entityメソッド
	 * @param f
	 * @return
	 */
	private Object getEntityObj(Field f) {
		//ログ発生箇所
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		//処理内容
		System.out.println("Entityを生成します（子クラス）");
		//子Entityの生成
		Object childEntity = null;
		try {
			Class<?> clazz = Class.forName(f.getType().getName());
			childEntity = clazz.getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return childEntity;
	}

	/*
	private Map<String, Class<?>> getRelationInfo(T entity) {
		Map<String, Class<?>> relationColumns = new HashMap<>();
		//リレーション情報を取得する
		for (Field f : entity.getClass().getDeclaredFields()) {
			//カラム名の取得
			if (f.isAnnotationPresent(OneToMany.class) || f.isAnnotationPresent(OneToOne.class)
					|| f.isAnnotationPresent(ManyToOne.class) || f.isAnnotationPresent(ManyToMany.class))
				relationColumns.put(f.getName(), f.getDeclaringClass());
		}
		return relationColumns;
	}
	*/

}
