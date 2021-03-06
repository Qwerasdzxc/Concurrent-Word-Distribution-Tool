package view;

import app.Config;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import manager.PipelineManager;
import model.Cruncher;
import model.Disk;
import model.FileInput;
import output.OutputComponent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainView {
	private Stage stage;
	private ComboBox<Disk> disks;
	private HBox left;
	private VBox fileInput, cruncher;
	private Pane center, right;
	private ListView<String> results;
	private Button addFileInput, singleResult, sumResult;
	private ArrayList<FileInputView> fileInputViews;
	private LineChart<Number, Number> lineChart;
	private ArrayList<Cruncher> availableCrunchers;
	private ObservableList<String> outputResults;
	private Text sortText;
	private ProgressBar sortProgressBar;

	private Button addCruncher;

	public void initMainView(BorderPane borderPane, Stage stage) {

		this.stage = stage;

		PipelineManager.getInstance().setView(this);

		fileInputViews = new ArrayList<>();
		availableCrunchers = new ArrayList<>();

		left = new HBox();

		borderPane.setLeft(left);

		initOutput();

		initFileInput();

		initCruncher();

		initCenter(borderPane);

		initRight(borderPane);

		stage.setOnCloseRequest(windowEvent -> {
			Stage shutdownStage = new Stage();
			shutdownStage.initStyle(StageStyle.UNDECORATED);
			shutdownStage.setTitle("Shut down in progress.");

			VBox vBox = new VBox();
			vBox.setAlignment(Pos.CENTER);
			vBox.getChildren().add(new Text("Application shut down in progress. Please wait..."));
			shutdownStage.setScene(new Scene(vBox, 300, 300));
			shutdownStage.initModality(Modality.APPLICATION_MODAL);

			PipelineManager.getInstance().shutDownApplication(shutdownStage);

			shutdownStage.showAndWait();

			Platform.exit();
			System.exit(0);
		});
	}

	private void initOutput() {
		outputResults = FXCollections.observableArrayList();

		String sortProgressLimit = Config.getProperty("sort_progress_limit");
		PipelineManager.getInstance().addNewOutputComponent(outputResults, Integer.parseInt(sortProgressLimit));
	}

	private void initFileInput() {
		fileInput = new VBox();

		fileInput.getChildren().add(new Text("File inputs:"));
		VBox.setMargin(fileInput.getChildren().get(0), new Insets(0, 0, 10, 0));

		disks = new ComboBox<Disk>();
		disks.getSelectionModel().selectedItemProperty().addListener(e -> updateEnableAddFileInput());
		disks.setMinWidth(120);
		disks.setMaxWidth(120);
		fileInput.getChildren().add(disks);

		addFileInput = new Button("Add FileInput");
		addFileInput.setOnAction(e -> addFileInput(new FileInput(disks.getSelectionModel().getSelectedItem())));
		VBox.setMargin(addFileInput, new Insets(5, 0, 10, 0));
		addFileInput.setMinWidth(120);
		addFileInput.setMaxWidth(120);
		fileInput.getChildren().add(addFileInput);

		int width = 210;

		VBox divider = new VBox();
		divider.getStyleClass().add("divider");
		divider.setMinWidth(width);
		divider.setMaxWidth(width);
		fileInput.getChildren().add(divider);
		VBox.setMargin(divider, new Insets(0, 0, 15, 0));

		Insets insets = new Insets(10);
		ScrollPane scrollPane = new ScrollPane(fileInput);
		scrollPane.setMinWidth(width + 35);
		fileInput.setPadding(insets);
		fileInput.getChildren().add(scrollPane);

		left.getChildren().add(scrollPane);

		
		try {
			String[] disksArray = Config.getProperty("disks").split(";");
			for (String disk : disksArray) {
				File file = new File(disk);
				if(!file.exists() || !file.isDirectory()) {
					throw new Exception("Bad directory path");
				}
				disks.getItems().add(new Disk(file));
			}
			if (disksArray.length > 0) {
				disks.getSelectionModel().select(0);
			}
		} catch (Exception e) {
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Closing");
					alert.setHeaderText("Bad config disks");
					alert.setContentText(null);

					alert.showAndWait();
					System.exit(0);
				}
			});
		}

		updateEnableAddFileInput();
	}

	private void initCruncher() {
		cruncher = new VBox();

		Text text = new Text("Crunchers");
		cruncher.getChildren().add(text);
		VBox.setMargin(text, new Insets(0, 0, 5, 0));

		addCruncher = new Button("Add cruncher");
		addCruncher.setOnAction(e -> addCruncher());
		cruncher.getChildren().add(addCruncher);
		VBox.setMargin(addCruncher, new Insets(0, 0, 15, 0));

		int width = 110;

		Insets insets = new Insets(10);
		ScrollPane scrollPane = new ScrollPane(cruncher);
		scrollPane.setMinWidth(width + 35);
		cruncher.setPadding(insets);
		left.getChildren().add(scrollPane);
	}

	private void initCenter(BorderPane borderPane) {
		center = new HBox();

		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel("Bag of words");
		yAxis.setLabel("Frequency");
		lineChart = new LineChart<Number, Number>(xAxis, yAxis);
		lineChart.setMinWidth(700);
		lineChart.setMinHeight(600);
		center.getChildren().add(lineChart);

		borderPane.setCenter(center);
	}

	private void initRight(BorderPane borderPane) {
		right = new VBox();
		right.setPadding(new Insets(10));
		right.setMaxWidth(200);

		results = new ListView<String>(outputResults);
		right.getChildren().add(results);
		VBox.setMargin(results, new Insets(0, 0, 10, 0));
		results.getSelectionModel().selectedItemProperty().addListener(e -> updateResultButtons());
		results.getSelectionModel().selectedIndexProperty().addListener(e -> updateResultButtons());
		results.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		singleResult = new Button("Single result");
		singleResult.setOnAction(e -> getSingleResult());
		singleResult.setDisable(true);
		right.getChildren().add(singleResult);
		VBox.setMargin(singleResult, new Insets(0, 0, 5, 0));

		sumResult = new Button("Sum results");
		sumResult.setDisable(true);
		sumResult.setOnAction(e -> sumResults());
		right.getChildren().add(sumResult);
		VBox.setMargin(sumResult, new Insets(0, 0, 10, 0));

		sortText = new Text();
		sortProgressBar = new ProgressBar(0);
		sortProgressBar.setVisible(false);
		sortProgressBar.progressProperty().addListener((observableValue, number, value) -> {
			if (value.intValue() > 0)
				sortText.setText("Sorting...");
			else
				sortText.setText("");
		});
		right.getChildren().add(sortText);
		right.getChildren().add(sortProgressBar);

		borderPane.setRight(right);
	}

	public void updateEnableAddFileInput() {
		Disk disk = disks.getSelectionModel().getSelectedItem();
		if (disk != null) {
			for (FileInputView fileInputView : fileInputViews) {
				if (fileInputView.getFileInput().getDisk() == disk) {
					addFileInput.setDisable(true);
					return;
				}
			}
			addFileInput.setDisable(false);
		} else {
			addFileInput.setDisable(true);
		}
	}

	public void updateResultButtons() {
		if (results.getSelectionModel().getSelectedItems() == null
				|| results.getSelectionModel().getSelectedItems().size() == 0) {
			singleResult.setDisable(true);
			sumResult.setDisable(true);
		} else if (results.getSelectionModel().getSelectedItems().size() == 1) {
			singleResult.setDisable(false);
			sumResult.setDisable(true);
		} else {
			singleResult.setDisable(true);
			sumResult.setDisable(false);
		}
	}

	private void getSingleResult() {
		String selectedFile = results.getSelectionModel().getSelectedItem();
		if (selectedFile.endsWith("*")) {
			Alert newAlert = new Alert(Alert.AlertType.ERROR, "Result not ready yet!", ButtonType.OK);
			newAlert.showAndWait();
			return;
		}

		try {
			OutputComponent outputComponent = PipelineManager.getInstance().getOutputComponent();
			Map<String, Long> resultData = outputComponent.poll(selectedFile);
			outputComponent.showSortedData(resultData, lineChart, sortProgressBar);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sumResults() {
		TextInputDialog textInputDialog = new TextInputDialog("sum");
		textInputDialog.setHeaderText("Enter result name");
		textInputDialog.initModality(Modality.APPLICATION_MODAL);
		textInputDialog.showAndWait().ifPresent(resultName -> {
			if (results.getItems().contains(resultName) || results.getItems().contains(resultName + "*")) {
				Alert newAlert = new Alert(Alert.AlertType.ERROR, "Result name already exists!", ButtonType.OK);
				newAlert.showAndWait();
				return;
			}

			List<String> selected = new ArrayList<>(results.getItems());
			PipelineManager.getInstance().getOutputComponent().calculateSumData(selected, resultName, right);
		});
	}

	public void addFileInput(FileInput fileInput) {
		FileInputView fileInputView = new FileInputView(fileInput, this);
		this.fileInput.getChildren().add(fileInputView.getFileInputView());
		VBox.setMargin(fileInputView.getFileInputView(), new Insets(0, 0, 30, 0));
		fileInputView.getFileInputView().getStyleClass().add("file-input");
		fileInputViews.add(fileInputView);
		if (availableCrunchers != null) {
			fileInputView.updateAvailableCrunchers(availableCrunchers);
		}
		updateEnableAddFileInput();
	}

	public void removeFileInputView(FileInputView fileInputView) {
		fileInput.getChildren().remove(fileInputView.getFileInputView());
		fileInputViews.remove(fileInputView);
		updateEnableAddFileInput();
	}

	public void updateCrunchers(ArrayList<Cruncher> crunchers) {
		for (FileInputView fileInputView : fileInputViews) {
			fileInputView.updateAvailableCrunchers(crunchers);
		}
		this.availableCrunchers = crunchers;
	}

	public Stage getStage() {
		return stage;
	}

	private void addCruncher() {
		TextInputDialog dialog = new TextInputDialog("1");
		dialog.setTitle("Add cruncher");
		dialog.setHeaderText("Enter cruncher arity");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(res -> {
			try {
				int arity = Integer.parseInt(res);
				for (Cruncher cruncher : availableCrunchers) {
					if (cruncher.getArity() == arity) {
						Alert alert = new Alert(AlertType.WARNING);
						alert.setTitle("Error");
						alert.setHeaderText("Cruncher with this arity already exists.");
						alert.setContentText(null);
						alert.showAndWait();
						return;
					}
				}
				Cruncher cruncher = new Cruncher(arity);
				CruncherView cruncherView = new CruncherView(this, cruncher);
				this.cruncher.getChildren().add(cruncherView.getCruncherView());
				availableCrunchers.add(cruncher);
				updateCrunchers(availableCrunchers);
			} catch (NumberFormatException e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Wrong input");
				alert.setHeaderText("Arity must be a number");
				alert.showAndWait();
			}
		});
	}

	public void stopCrunchers() {
		
	}

	public void stopFileInputs() {
		
	}

	public void removeCruncher(CruncherView cruncherView) {
		for (FileInputView fileInputView : fileInputViews) {
			fileInputView.removeLinkedCruncher(cruncherView.getCruncher());
		}
		availableCrunchers.remove(cruncherView.getCruncher());
		updateCrunchers(availableCrunchers);
		cruncher.getChildren().remove(cruncherView.getCruncherView());
	}

	public void showOutOfMemoryError() {
		Alert alert = new Alert(AlertType.ERROR, "Application out of memory.", ButtonType.CLOSE);
		alert.showAndWait();

		Platform.exit();
		System.exit(0);
	}

	public Pane getRight() {
		return right;
	}
}
