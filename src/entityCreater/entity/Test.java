package entityCreater.entity;

import java.util.List;

import annotation.Entity;
import annotation.OneToMany;
import annotation.OneToOne;
import annotation.Table;
import annotation.column;
import annotation.id;

@Entity
@Table("test")
public class Test {

	@id
	@column
	private String num;

	@column
	private String namename;

	@OneToOne
	private Task_list task_list;

	@OneToMany(mappingBy = {"num"})
	private List<Test2> test2;

	public List<Test2> getTest2() {
		return test2;
	}

	public void setTest2(List<Test2> test2) {
		this.test2 = test2;
	}

	public Task_list getTask_list() {
		return task_list;
	}

	public void setTask_list(Task_list task_list) {
		this.task_list = task_list;
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
