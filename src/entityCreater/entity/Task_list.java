package entityCreater.entity;

import java.util.List;

import annotation.Entity;
import annotation.OneToMany;
import annotation.OneToOne;
import annotation.Table;
import annotation.column;
import annotation.id;

@Entity
@Table("TASK_LIST")
public class Task_list {

	@id
	@column
	private String num;

	@column
	private java.sql.Date deadline;

	@column
	private String name;

	@column
	private String content;

	@column
	private String client;

	@OneToOne
	private entityCreater.entity.Test test;

	@OneToMany(mappingBy = {"num"})
	private List<Test2> test2;

	public List<Test2> getTest2() {
		return test2;
	}

	public void setTest2(List<Test2> test2) {
		this.test2 = test2;
	}

	public entityCreater.entity.Test getTest() {
		return test;
	}

	public void setTest(entityCreater.entity.Test test) {
		this.test = test;
	}

	public void setNum(String num) {
		this.num = num;
 	}

	public String getNum() {
		return this.num;
 	}

	public void setDeadline(java.sql.Date deadline) {
		this.deadline = deadline;
 	}

	public java.sql.Date getDeadline() {
		return this.deadline;
 	}

	public void setName(String name) {
		this.name = name;
 	}

	public String getName() {
		return this.name;
 	}

	public void setContent(String content) {
		this.content = content;
 	}

	public String getContent() {
		return this.content;
 	}

	public void setClient(String client) {
		this.client = client;
 	}

	public String getClient() {
		return this.client;
 	}

}
