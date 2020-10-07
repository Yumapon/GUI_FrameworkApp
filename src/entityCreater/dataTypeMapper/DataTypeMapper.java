package entityCreater.dataTypeMapper;

public class DataTypeMapper {

	/**
	 * SQLのデータタイプからJavaのデータタイプに変換するクラス
	 * @param sqlDataType
	 * @return
	 */
	public static String dataTypeChange(String sqlDataType) {

		String javaDataType = null;

		switch (sqlDataType) {
		case "CHAR":
		case "VARCHAR":
		case "LONGVARCHAR":
			javaDataType = "String";
			break;
		case "NUMERIC":
		case "DECIMAL":
			javaDataType = "java.math.BigDecimal";
			break;
		case "BIT":
			javaDataType = "boolean";
			break;
		case "TINYINT":
			javaDataType = "byte";
			break;
		case "SMALLINT":
			javaDataType = "short";
			break;
		case "INTEGER":
		case "INT":
			javaDataType = "Integer";
			break;
		case "BIGINT":
			javaDataType = "long";
			break;
		case "REAL":
			javaDataType = "float";
			break;
		case "FLOAT":
		case "DOUBLE":
			javaDataType = "double";
			break;
		case "BINARY":
		case "VARBINARY":
		case "LONGVARBINARY":
			javaDataType = "byte[]";
			break;
		case "DATE":
			javaDataType = "java.sql.Date";
			break;
		case "TIME":
			javaDataType = "java.sql.Time";
			break;
		case "TIMESTAMP":
			javaDataType = "java.sql.Timestamp";
			break;
		default:
			break;
		}

		return javaDataType;

	}

}
