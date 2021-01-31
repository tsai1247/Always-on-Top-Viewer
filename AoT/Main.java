package AoT;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javafx.application.Application;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.logging.*;

public class Main extends Application
{
	Pane pane = new Pane();
	Scene scene = new Scene(pane);
	ImageView bg = new ImageView(new Image(getClass().getResourceAsStream("bg.png")));
	ImageView move = new ImageView(new Image(getClass().getResourceAsStream("moveImg.png")));
	ImageView img;
	
	Text text = new Text();
	ScrollPane pane_text = new ScrollPane(text);
	
	Button browse = new Button("瀏覽");
	double ScreenX, ScreenY;
	double InitWidth=200, InitHeight=100;
	double imgWidth=InitWidth, imgHeight=InitHeight;
	double mag = 1;
	boolean rightclick = false;
	
	FileChooser FC = new FileChooser();
	
	ScrollPane newPane = new ScrollPane();
	Scene newScene = new Scene(newPane);
	Stage newStage = new Stage();
	TextArea ta = new TextArea();
	
	
	
	Stage stage;
	@Override
	public void start(Stage stage)
	{   
		Initial_bg();
		Initial_img();
		Initial_text();
		Initial_Move();
		Initial_Browse();
		Set_Visible(browse);
		Set_Visible(move);
	
	    FC.setTitle("開啟");
	    FC.getExtensionFilters().addAll
	    (
		    	new FileChooser.ExtensionFilter("文字", "*.txt"), 
	    	new FileChooser.ExtensionFilter("圖片", "*.jpg", "*.png")
	    ); 
	    
		this.stage = stage;
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setWidth(InitWidth);
		stage.setHeight(InitHeight);
		stage.setAlwaysOnTop(true);
		stage.setX(100);
		stage.setY(100);
		stage.setOnCloseRequest(e->{
			if(newStage.isShowing())
				newStage.close();
		});
		
		ta.setFocusTraversable(false);
		newPane.setContent(ta);
		newStage.setScene(newScene);
		newStage.setWidth(stage.getWidth());
		newStage.setHeight(stage.getWidth()/2.5);
		newStage.setX(stage.getX());
		newStage.setY(stage.getY()+stage.getHeight());
		
		newStage.initStyle(StageStyle.UNDECORATED);
		newStage.setAlwaysOnTop(true);
		newStage.setOnCloseRequest(e->{
			rightclick = false;
		});

		scene.setOnKeyPressed(e->{
			if(img.isVisible() || pane_text.isVisible())
			{
				if(e.getCode().equals(KeyCode.PAGE_UP))
				{
					ADD();
				}
				else if(e.getCode().equals(KeyCode.PAGE_DOWN))
				{
					MINUS();
				}
				else if(e.getCode().equals(KeyCode.SPACE))
				{
					SPACE();
				}
			}
		});
		pane_text.setOnKeyPressed(e->{
			if(img.isVisible() || pane_text.isVisible())
			{
				if(e.getCode().equals(KeyCode.PAGE_UP))
				{
					ADD();
				}
				else if(e.getCode().equals(KeyCode.PAGE_DOWN))
				{
					MINUS();
				}
				else if(e.getCode().equals(KeyCode.SPACE))
				{
					SPACE();
				}
			}
		});
		
		stage.show();
	}
	
	private void SPACE() {
		mag = 1;
		ReSize();
	}

	private void MINUS() {
		mag -= 0.05;
		mag = Math.max(mag, 0.1);
		ReSize();
	}

	private void ADD() {
		mag += 0.04;
		mag = Math.min(mag, 30);
		ReSize();
	}

	private void ReSize() {

		stage.setWidth(imgWidth*mag);
		stage.setHeight(imgHeight*mag);
		img.setFitWidth(imgWidth*mag);
		img.setFitHeight(imgHeight*mag);
		text.setFont(Font.font(12*mag));
		pane_text.setPrefSize(imgWidth*mag, imgHeight*mag);

		browse.setLayoutX(stage.getWidth()-41);
		newStage.setX(stage.getX());
		newStage.setY(stage.getY()+stage.getHeight());
		
	}

	private void Initial_text() {
		pane.getChildren().add(pane_text);
		pane_text.setVisible(false);
		pane_text.setFocusTraversable(false);
		text.setFocusTraversable(false);
		
	}

	private void Initial_img() {
		img = new ImageView(new Image(getClass().getResourceAsStream("bg.png")));
		pane.getChildren().add(img);
		img.setVisible(false);
		img.setFocusTraversable(false);
	}

	private void Set_Visible(Node object) {
		object.setOpacity(0);
		object.setOnMouseEntered(e->{
			object.setOpacity(1);
		});
		object.setOnMouseExited(e->{
			object.setOpacity(0);
		});
	}

	
	private void Initial_Browse() {
		pane.getChildren().add(browse);
		browse.setLayoutX(InitWidth-41);
		browse.setLayoutY(0);
		browse.setFocusTraversable(false);
		SetBrowse();
	}

	private void SetBrowse() {
		browse.setOnMouseClicked(e->{
			MouseButton mb = e.getButton();
			if(mb.equals(MouseButton.PRIMARY))
			{
				try {
					String s = FC.showOpenDialog(stage).getAbsolutePath();
					
					System.out.println(s);
					String subs = s.substring(s.length()-3, s.length());
					if(subs.equals("txt"))
					{
						mag = 1;
						text.setFont(Font.font(12));
						if(newStage.isShowing())
						{
							newStage.close();
							rightclick = false;
						}
						ShowTxt(s);
					}
					else if( subs.equals("jpg") || subs.equals("png") )
					{
						mag = 1;
						ShowJPG(s);
						if(newStage.isShowing())
						{
							newStage.close();
							rightclick = false;
						}
					}
					
				}	
				catch(Exception ex1)
				{
					
				}
			}
			else// if(mb.equals(MouseButton.SECONDARY))
			{
				rightclick = !rightclick;
				if(rightclick)
				{
					ta.setText("");
					ta.setPromptText("text...");
					newStage.setWidth(stage.getWidth());
					newStage.setHeight(stage.getWidth()/2.5);
					ta.setPrefSize(newStage.getWidth(), newStage.getHeight());
					newStage.setX(stage.getX());
					newStage.setY(stage.getY()+stage.getHeight());
					
					newStage.show();
					
				}
				else
				{
					newStage.close();
					mag = 1;
					text.setFont(Font.font(12));
					ShowTxt(ta.getText());
				}
			}
//				FC.setInitialDirectory(new File(s));
		});
	}


	private void ShowJPG(String s) {
		try
		{
			Image tmpimg = new Image(new FileInputStream(s));
			img.setImage(tmpimg);
			imgWidth = tmpimg.getWidth();
			imgHeight = tmpimg.getHeight();
			stage.setWidth(imgWidth);
			stage.setHeight(imgHeight);
			pane_text.setPrefSize(10, 10);
			browse.setLayoutX(imgWidth-41);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		System.out.println(imgWidth);
		System.out.println(imgHeight);
		
		pane_text.setVisible(false);
		img.setVisible(true);
	}
	
	private void ShowTxt(String s) {
		BufferedReader br;
		int width=0, height=0;
		try {
			br= new BufferedReader(new InputStreamReader(new FileInputStream(s),"UTF-8"));
			String content = "";
			String tmp;
			while((tmp=br.readLine())!=null)
			{
				content += tmp + "\r\n";
				width = Math.max(width, tmp.getBytes().length);
				height++;
			}
			text.setText(content);
			System.out.println(content);
		}
		catch (IOException e) {
			text.setText(s);
			System.out.println(s);
			String[] splits = s.split("\n");
			for(int i=0; i<splits.length; i++)
			{
				width = Math.max(splits[i].getBytes().length, width);
				
//				System.out.println("---------------------");
//				System.out.println(splits[i].getBytes().length);
//				System.out.println(splits[i]);
				
			}
			height = splits.length;
		}
		
		width *= 7;
		width += 50;

		height++;
		height *= 16;

		width = Math.max(100, width);
		height = Math.max(30, height);
		
		width = Math.min(1250, width);
		height = Math.min(600, height);

		imgWidth = width;
		imgHeight = height;
		System.out.println(width);
		System.out.println(height);
		
		stage.setWidth(width);
		stage.setHeight(height);
		browse.setLayoutX(width-41);
		pane_text.setPrefSize(width, height);
		
		
		pane_text.setVisible(true);
		img.setVisible(false);
		bg.setVisible(false);

	}

	private void Initial_Move() {
		pane.getChildren().add(move);
		move.setFocusTraversable(false);
		SetMove();
	}

	private void SetMove() {
		move.setOnMousePressed(e->{
			ScreenX = e.getScreenX();
			ScreenY = e.getScreenY();
		});
		move.setOnMouseDragged(e->{
			stage.setX(stage.getX()+e.getScreenX()-ScreenX);
			stage.setY(stage.getY()+e.getScreenY()-ScreenY);
			ScreenX = e.getScreenX();
			ScreenY = e.getScreenY();
			newStage.setX(stage.getX());
			newStage.setY(stage.getY()+stage.getHeight());
		});		
	}

	private void Initial_bg() {
		pane.getChildren().add(bg);

		bg.setFocusTraversable(false);
		bg.setFitWidth(InitWidth);
		bg.setFitHeight(InitHeight);
	}

	public static void main(String[] args)
	{
		launch();
	}
}
