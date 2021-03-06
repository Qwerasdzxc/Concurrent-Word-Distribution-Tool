package view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import manager.PipelineManager;
import model.Cruncher;

public class CruncherView {

	private MainView mainView;
	private Cruncher cruncher;
	private Text status;

	private Pane main;

	public CruncherView(MainView mainView, Cruncher cruncher) {
		this.mainView = mainView;
		this.cruncher = cruncher;
		
		main = new VBox();

		Text text = new Text("Name: " + cruncher.toString());
		main.getChildren().add(text);
		VBox.setMargin(text, new Insets(0, 0, 2, 0));

		text = new Text("Arity: " + cruncher.getArity());
		main.getChildren().add(text);
		VBox.setMargin(text, new Insets(0, 0, 5, 0));

		Button remove = new Button("Remove cruncher");
		remove.setOnAction(e -> removeCruncher());
		main.getChildren().add(remove);
		VBox.setMargin(remove, new Insets(0, 0, 5, 0));

		status = new Text("");
		main.getChildren().add(status);

		VBox.setMargin(main, new Insets(0, 0, 15, 0));

		createCruncherComponent();
	}

	private void createCruncherComponent() {
		PipelineManager.getInstance().addNewCruncherComponent(cruncher.getArity(), status);
		PipelineManager.getInstance().attachCruncherComponentToOutputComponent(cruncher.getArity());
	}

	public Pane getCruncherView() {
		return main;
	}

	private void removeCruncher() {
		mainView.removeCruncher(this);

		PipelineManager.getInstance().detachCruncherComponentFromOutputComponent(cruncher.getArity());
		PipelineManager.getInstance().removeCruncherComponent(cruncher.getArity());
	}

	public Cruncher getCruncher() {
		return cruncher;
	}

	public MainView getMainView() {
		return mainView;
	}
}
