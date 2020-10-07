package usercreatesample.application;

import java.io.IOException;
import java.time.ZoneId;

import container.BusinessLogicFactory;
import entityCreater.entity.Task_list;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import usercreatesample.businessLogic.BusinessLogic;

public class TaskCreateController {

	//FactoryからBusinessLogicを取得
	BusinessLogicFactory blFactory = new BusinessLogicFactory();
	BusinessLogic bl = (BusinessLogic) blFactory.getBusinessLogic("bl1");

	//タスク新規作成画面
	@FXML
	private TextField taskName;
	@FXML
	private TextField client;
	@FXML
	private DatePicker deadline;
	@FXML
	private TextArea taskContent;

	//コンテナからBusinessLogicを取得

	@FXML
	public void create(ActionEvent eve) {
		//タスクを格納
		Task_list task = new Task_list();
		task.setNum(bl.taskNum());
		task.setName(taskName.getText());
		task.setContent(taskContent.getText());
		java.util.Date date = java.util.Date.from(deadline.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		task.setDeadline(sqlDate);
		task.setClient(client.getText());

		bl.taskstorage(task);

		//現在開いている画面を閉じる
		Scene s = ((Node) eve.getSource()).getScene();
		Window window = s.getWindow();
		window.hide();

		//次画面を生成
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("TaskList.fxml"));
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("タスク一覧");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@FXML
	public void close(ActionEvent eve) {
		//現在開いている画面を閉じる
		Scene s = ((Node) eve.getSource()).getScene();
		Window window = s.getWindow();
		window.hide();

		//次画面を生成
		try {
			Parent parent = FXMLLoader.load(getClass().getResource("TaskList.fxml"));
			Scene scene = new Scene(parent);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("タスク一覧");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
