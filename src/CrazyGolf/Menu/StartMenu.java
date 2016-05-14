package CrazyGolf.Menu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import CrazyGolf.Editor.EditorPanel;
import CrazyGolf.Editor.RadioButtons;
import CrazyGolf.FileLocations;
import CrazyGolf.Game.FrameGolf;
import CrazyGolf.Game.Golf3D;
import CrazyGolf.Game.Player;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class StartMenu extends Application{
	GameMenu pauseMenu;
	GameMenu menu;
	FrameGolf game;
	SwingNode swingEditor;
	SwingNode swingGame;
	int player;

    private String level="Field1.txt";

    private Golf3D golf3D;
    private Player[] players;
    private int currentPlayer;
    private int amountOfPlayers=1;

    private JLabel labelTitle;
    private JLabel labelCurrentPlayer;
    private JLabel[] labelPlayer;
    private JLabel labelWin;


	public static void main(String[] args){
		launch(args);
	}

	public void start(Stage stage) throws Exception {

		Pane root = new Pane();
		root.setPrefSize(900,700);

		 swingEditor = new SwingNode();
         setSwingEditor(swingEditor);
         swingEditor.setVisible(false);

		menu = new GameMenu(swingEditor);
		menu.setVisible(true);



		pauseMenu = new GameMenu(swingEditor);
		pauseMenu.setVisible(false);

		InputStream is = Files.newInputStream(Paths.get(FileLocations.menuBG));
		Image background = new Image(is);
		is.close();
		ImageView imv =new ImageView(background);
		imv.setFitHeight(700);
		imv.setFitWidth(900);



		root.getChildren().addAll(imv,swingEditor,menu,pauseMenu);

		Scene scene = new Scene (root);

		scene.setOnKeyPressed(event ->{
			if(event.getCode()==KeyCode.ESCAPE){
				if(!pauseMenu.isVisible()&& !menu.isVisible()){
					FadeTransition ft1 = new FadeTransition(Duration.seconds(0.75),pauseMenu);
					ft1.setFromValue(0);
					ft1.setToValue(1);
					ft1.setOnFinished(evt-> pauseMenu.setVisible(true));
					ft1.play();
				}else{
					FadeTransition ft = new FadeTransition(Duration.seconds(0.75),pauseMenu);
					ft.setFromValue(1);
					ft.setToValue(0);
					ft.setOnFinished(evt-> pauseMenu.setVisible(false));
					ft.play();
			}
		}

		});

		stage.setScene(scene);
		stage.show();

	}
public class GameMenu extends Parent{

	public GameMenu(SwingNode swingNode){
	Pane root = new Pane();
	root.setPrefSize(900,700);

	Rectangle rex  = new Rectangle(900,700);
	rex.setFill(javafx.scene.paint.Color.WHITE);
	rex.setOpacity(0.7);

//	VBox boxMenu = new VBox(10);
	VBox sMenu = new VBox(10);
	VBox menu2 = new VBox(10);

	final int offset = 800;

	Button play = new Button("PLAY");
	play.setOnMouseClicked(event ->{
		getChildren().add(menu2);

		FadeTransition ft = new FadeTransition(Duration.seconds(0.75),menu);
		ft.setFromValue(1);
		ft.setToValue(0);

		TranslateTransition tt = new TranslateTransition(Duration.seconds(0.75), sMenu);
        tt.setToX(sMenu.getTranslateX() - offset);

        TranslateTransition tt1 = new TranslateTransition(Duration.seconds(1), menu2);
        tt1.setToX(sMenu.getTranslateX());

        tt.play();
        tt1.play();

        tt.setOnFinished(evt -> {
            getChildren().remove(sMenu);
        });

	});

	Button editor = new Button("CREATE YOUR MAP");
	editor.setOnMouseClicked(event ->{
		FadeTransition ft = new FadeTransition(Duration.seconds(0.75),menu);
		ft.setFromValue(1);
		ft.setToValue(0);
		ft.setOnFinished(evt->menu.setVisible(false));
		ft.play();
		swingNode.setVisible(true);
		});

	Button p1 = new Button("1 PLAYER");
	p1.setOnMouseClicked(event ->{
		FadeTransition ft = new FadeTransition(Duration.seconds(0.75),menu2);
		ft.setFromValue(1);
		ft.setToValue(0);
		ft.setOnFinished(evt->{
		menu2.setVisible(false);
		sMenu.setVisible(false);
		});
		ft.play();

		player =1;
		new FrameGolf(player);

//		swingGame= new SwingNode();
//        setSwingGame(swingGame);
//		swingGame.setVisible(true);
//		root.getChildren().add(swingGame);

		});

	Button p2 = new Button("2 PLAYERS");
	p1.setOnMouseClicked(event ->{
		FadeTransition ft = new FadeTransition(Duration.seconds(0.75),menu2);
		ft.setFromValue(1);
		ft.setToValue(0);
		ft.setOnFinished(evt->{
			menu2.setVisible(false);
			sMenu.setVisible(false);
			});
		ft.play();

		player =2;
		new FrameGolf(player);

//		swingGame= new SwingNode();
//        setSwingGame(swingGame);
//		swingGame.setVisible(true);
//		root.getChildren().add(swingGame);

		});
	Button p3 = new Button("3 PLAYERS");
	p1.setOnMouseClicked(event ->{
		FadeTransition ft = new FadeTransition(Duration.seconds(0.75),menu2);
		ft.setFromValue(1);
		ft.setToValue(0);
		ft.setOnFinished(evt->{
			menu2.setVisible(false);
			sMenu.setVisible(false);
			});
		ft.play();

	player =3;
	new FrameGolf(player);

//		swingGame= new SwingNode();
//        setSwingGame(swingGame);
//		swingGame.setVisible(true);
//		root.getChildren().add(swingGame);

		});

	Button p4 = new Button("4 PLAYERS");
	p1.setOnMouseClicked(event ->{
		FadeTransition ft = new FadeTransition(Duration.seconds(0.75),menu2);
		ft.setFromValue(1);
		ft.setToValue(0);
		ft.setOnFinished(evt->{
			menu2.setVisible(false);
			sMenu.setVisible(false);
			});
		ft.play();

		player =4;
		new FrameGolf(player);

//		swingGame= new SwingNode();
//        setSwingGame(swingGame);
//		swingGame.setVisible(true);
//		root.getChildren().add(swingGame);

		});



	/*Button resume = new Button("RESUME");
	resume.setOnMouseClicked(event ->{
		FadeTransition ft = new FadeTransition(Duration.seconds(0.75),pauseMenu);
		ft.setFromValue(1);
		ft.setToValue(0);
		ft.setOnFinished(evt->pauseMenu.setVisible(false));
		ft.play();
		});*/

	Button exit = new Button("EXIT");
	exit.setOnMouseClicked(event ->{
		System.exit(0);
		});


//	boxMenu.setTranslateX(325);
//	boxMenu.setTranslateY(250);

//	boxMenu.setVisible(false);

	sMenu.setTranslateX(325);
	sMenu.setTranslateY(250);



    menu2.setTranslateX(offset);

	menu2.setTranslateX(325);
    menu2.setTranslateY(250);

	sMenu.getChildren().addAll(play,editor,exit);
	menu2.getChildren().addAll(p1,p2,p3,p4);
//	boxMenu.getChildren().addAll(resume,exit);

	root.getChildren().addAll(rex,sMenu);
	getChildren().addAll(root);
	}
}

	public static class Button extends StackPane{
		Button(String name){
			Text text = new Text(name);
			text.setFont(javafx.scene.text.Font.font(20));
			text.setFill(javafx.scene.paint.Color.WHITE);

			Rectangle rex = new Rectangle(250,30);
			rex.setOpacity(0.8);
			rex.setFill(javafx.scene.paint.Color.BLACK);
			setAlignment(Pos.CENTER);
			getChildren().addAll(rex,text);

			setOnMouseEntered(event ->{
				rex.setFill(javafx.scene.paint.Color.GREY);
				text.setFill(javafx.scene.paint.Color.BLACK);
			});

			setOnMouseExited(event->{
				rex.setFill(javafx.scene.paint.Color.BLACK);
				text.setFill(javafx.scene.paint.Color.WHITE);
			});

			}
			}
	 private void setSwingEditor(SwingNode swingNode) {

         SwingUtilities.invokeLater(new Runnable() {
             public void run() {
            	  JPanel panel = new JPanel();
			        panel.setLayout(new BorderLayout());

			        panel.setPreferredSize(new Dimension(900,700));
			        RadioButtons radio = new RadioButtons();

			        EditorPanel editor = new EditorPanel(radio);
			        editor.setBackground(Color.WHITE);
			        panel.add(editor);
			        panel.add(radio, BorderLayout.EAST);
			        panel.setBorder(BorderFactory.createLineBorder(Color.black));
			        panel.setVisible(true);

			        swingNode.setContent(panel);

             }
         });
     }
//private void setSwingGame(SwingNode swingNode) {
//
//         SwingUtilities.invokeLater(new Runnable() {
//             public void run() {
//			     swingNode.setContent(panel);
//             	}
//         });
	}


