package usercreatesample.application;

import java.io.IOException;

import container.BusinessLogicFactory;
import entityCreater.entity.User_id;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import usercreatesample.businessLogic.BusinessLogic;

public class LoginController extends Application{

	//ログイン画面
	@FXML
	private TextField userid;
	@FXML
	private TextField pass;
	@FXML
	private Label error;

	//FactoryからBusinessLogicを取得
	BusinessLogicFactory blFactory = new BusinessLogicFactory();
	BusinessLogic bl = (BusinessLogic) blFactory.getBusinessLogic("bl1");

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {
		// Login.fxml の読み込み
		Pane root = FXMLLoader.load(getClass().getResource("Login.fxml"));

		// Scene の作成・登録
		Scene scene = new Scene(root);
		stage.setScene(scene);
		// 表示
		stage.show();
	}

	@FXML
	public void loginButton(ActionEvent eve) {

		//Login処理
		User_id user_id = new User_id();
		user_id.setId(Integer.parseInt(userid.getText()));
		user_id.setPassword(pass.getText());

		System.out.println(userid.getText());
		System.out.println(pass.getText());

		if(!bl.login(user_id)) {
			//ログイン失敗
			error.setText("ユーザIDかパスワードが間違っています");
			return;
		}

		//ログイン成功

		//現在開いている画面を閉じる
		Scene s = ((Node) eve.getSource()).getScene();
		Window window = s.getWindow();
		window.hide();

		//次画面を生成
		try {
			AnchorPane parent = FXMLLoader.load(getClass().getResource("TaskList.fxml"));
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
