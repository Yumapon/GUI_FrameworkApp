package usercreatesample.application;

import java.io.IOException;

import container.BusinessLogicFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import usercreatesample.businessLogic.BusinessLogic;

public class TaskDeleteController {

	//FactoryからBusinessLogicを取得
	BusinessLogicFactory blFactory = new BusinessLogicFactory();
	BusinessLogic bl = (BusinessLogic) blFactory.getBusinessLogic("bl1");

	@FXML
	private TextField deleteNum;

	@FXML
	public void delete(ActionEvent eve) {
		//taskを削除
		String[] taskNumList = {deleteNum.getText()};
		bl.deleteTask(taskNumList);

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
