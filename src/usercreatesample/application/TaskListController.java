package usercreatesample.application;

import java.io.IOException;
import java.lang.reflect.Member;
import java.util.ArrayList;

import container.BusinessLogicFactory;
import entityCreater.entity.Task_list;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import usercreatesample.businessLogic.BusinessLogic;

@SuppressWarnings("rawtypes")
public class TaskListController {

	//FactoryからBusinessLogicを取得
	BusinessLogicFactory blFactory = new BusinessLogicFactory();
	BusinessLogic bl = (BusinessLogic) blFactory.getBusinessLogic("bl1");

	//タスク一覧画面
	@FXML
	private TableView table;
	@FXML
	private TableColumn NoCoumn;
	@FXML
	private TableColumn taskNameColumn;
	@FXML
	private TableColumn taskContentColumn;
	@FXML
	private TableColumn deadlineColumn;
	@FXML
	private TableColumn ClientColumn;
	private ObservableList<Task_list> data;

	@SuppressWarnings("unchecked")
	public void initialize() {
		//task一覧を取得
		ArrayList<Task_list> taskList = bl.getList();

		//taskListをTableViewにセット
		data = FXCollections.observableArrayList();
		if(taskList.size() != 0)
			for(Task_list t : taskList)
				data.add(t);
		table.itemsProperty().setValue(data);
	    table.setItems(data);

	    NoCoumn.setCellValueFactory(new PropertyValueFactory<Member, String>("num"));
	    taskNameColumn.setCellValueFactory(new PropertyValueFactory<Member, String>("name"));
	    taskContentColumn.setCellValueFactory(new PropertyValueFactory<Member, String>("content"));
	    deadlineColumn.setCellValueFactory(new PropertyValueFactory<Member, String>("deadline"));
	    ClientColumn.setCellValueFactory(new PropertyValueFactory<Member, String>("client"));
	}

	@FXML
	public void createTaskButton(ActionEvent eve) {
		//現在開いている画面を閉じる
		Scene s = ((Node) eve.getSource()).getScene();
		Window window = s.getWindow();
		window.hide();

		//次画面を生成
		try {
			AnchorPane parent = FXMLLoader.load(getClass().getResource("TaskCreate.fxml"));
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("タスク新規作成");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void deleteTaskButton(ActionEvent eve) {
		//現在開いている画面を閉じる
		Scene s = ((Node) eve.getSource()).getScene();
		Window window = s.getWindow();
		window.hide();

		//次画面を生成
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("Delete.fxml"));
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("タスク削除");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
