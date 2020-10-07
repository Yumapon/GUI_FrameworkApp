package entityCreater.entity;

import annotation.Entity;
import annotation.ManyToOne;
import annotation.Table;
import annotation.column;
import annotation.id;

@Entity
@Table("test2")
public class Test2 {

	@id
	@column
	private Integer id;

	@column
	private String num;

	@column
	private String namename;

	@ManyToOne
	private Task_list task_list;

	@ManyToOne
	private entityCreater.entity.Test test;

	public entityCreater.entity.Test getTest() {
		return test;
	}

	public void setTest(entityCreater.entity.Test test) {
		this.test = test;
	}

	public Task_list getTask_list() {
		return task_list;
	}

	public void setTask_list(Task_list task_list) {
		this.task_list = task_list;
	}

	public void setId(Integer id) {
		this.id = id;
 	}

	public Integer getId() {
		return this.id;
 	}

	public void setNum(String num) {
		this.num = num;
 	}

	public String getNum() {
		return this.num;
 	}

	public void setNamename(String namename) {
		this.namename = namename;
 	}

	public String getNamename() {
		return this.namename;
 	}

}
