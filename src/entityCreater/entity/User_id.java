package entityCreater.entity;

import annotation.Entity;
import annotation.Table;
import annotation.column;
import annotation.id;

@Entity
@Table("USER_ID")
public class User_id {

	@id
	@column
	private Integer id;

	@column
	private String password;

	public void setId(Integer id) {
		this.id = id;
 	}

	public Integer getId() {
		return this.id;
 	}

	public void setPassword(String password) {
		this.password = password;
 	}

	public String getPassword() {
		return this.password;
 	}

}
