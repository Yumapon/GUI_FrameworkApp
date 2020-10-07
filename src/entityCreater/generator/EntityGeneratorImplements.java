package entityCreater.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entityCreater.info.EntityInfo;
import entityCreater.reader.DBReaderImplements;

public class EntityGeneratorImplements implements EntityGenerator{

	//Fileを保存する場所
	String filePlace;

	/**
	 * コンストラクタ
	 */
	public EntityGeneratorImplements(String filePlace) {
		this.filePlace = filePlace;
	}

	/**
	 * Entity作成メソッド
	 */
	@Override
	public void generateEntity() {
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		System.out.println("EntityFile作成メソッドを開始します");

		ArrayList<EntityInfo> entityInfos = new DBReaderImplements().read();
		for(EntityInfo ei : entityInfos) {
			createFile(ei);
		}
	}


	/**
	 * EntityFile作成メソッド
	 * @param entityInfo
	 */
	private void createFile(EntityInfo entityInfo) {
    	//DBのテーブル情報を取得
    	String tableName = entityInfo.getTableName();
    	String id = entityInfo.getId();
    	List<String[]> columns = entityInfo.getColumns();

    	//クラス名を生成
    	String className = tableName.substring(0, 1).toUpperCase() + tableName.substring(1).toLowerCase();

		//EntityFileクラス
		File entityFile = new File(filePlace + "/" + className + ".java");

		try{
			if (entityFile.createNewFile()){
				System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		    	System.out.println("ファイルの作成に成功しました。");
		    }else{
		    	System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
		    	System.out.println("ファイルの作成に失敗しました");
		    }
		}catch(IOException e){
		    System.out.println(e);
		}

		//Fileの中身を作成する
		System.out.print(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
    	System.out.println("ファイルの中身を作成します。");

    	try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(entityFile, true)));) {

    		//Annotationのインポート文
    		pw.println("import annotation.Entity;");
    		pw.println("import annotation.Table;");
    		pw.println("import annotation.id;");
    		pw.println("import annotation.column;");
    		pw.println("");

    		//Class宣言(開始)
    		pw.println("@Entity");
    		pw.println("@Table(\"" + tableName + "\")");
    		pw.println("public class " + className + " {");
    		pw.println("");

    		//カラム宣言
    		Iterator<String[]> it = columns.iterator();

    		while(it.hasNext()) {
    			String[] colum = it.next();

    			//Columnの宣言
    			if(colum[1].equals(id)) {
    	    		pw.println("	@id");
    			}
    			pw.println("	@column");
        		pw.println("	private " + colum[0] + " " + colum[1] + ";");
        		pw.println("");

    		}

    		//Setter.Getter宣言
    		//Iteratorを先頭に戻す
    		it = columns.iterator();

    		while(it.hasNext()) {
    			String[] colum = it.next();

    			//Setter宣言
    			pw.println("	public void set" + colum[1].substring(0, 1).toUpperCase() + colum[1].substring(1).toLowerCase() + "(" + colum[0] + " " + colum[1] + ") {");
    			pw.println("		this." + colum[1] + " = " + colum[1] + ";");
    			pw.println(" 	}");
    			pw.println("");

    			//Getter宣言
    			pw.println("	public " + colum[0] + " get" + colum[1].substring(0, 1).toUpperCase() + colum[1].substring(1).toLowerCase() + "() {");
    			pw.println("		return this." + colum[1] + ";");
    			pw.println(" 	}");
    			pw.println("");
    		}

    		//Class宣言(終了)
    		pw.println("}");

			//終了表示
			System.out.println(Thread.currentThread().getStackTrace()[1].getClassName() + ":");
	    	System.out.println("*************************************************************");
	    	System.out.println("***    SUCCESS   パッケージ宣言、多重度の宣言を追記してください。 ***");
	    	System.out.println("*************************************************************");

		} catch (IOException e) {
			e.printStackTrace();
		}finally {

		}
	}


}
