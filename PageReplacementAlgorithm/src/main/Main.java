package main;

import calculation.Calculation;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import produceData.ConvertToPageNumber;
import produceData.RandomNumber;

public class Main extends Application {
	public static int[] addresses;
	public static int[] pageAddresses;
	//private static double[][] accuracy;
	//static double[][] accuracy = new double[4][37];

	private TextArea outputTextArea;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		VBox root = new VBox(10);
		root.setPadding(new Insets(30));


		Button simulateButton = new Button("开始模拟");
		Button nextButton = new Button("产生页号");
		Button calculateButton = new Button("计算命中率");
		Button countButton = new Button("统计");
		HBox buttonBox = new HBox(simulateButton, nextButton, calculateButton, countButton);


		outputTextArea = new TextArea();
		outputTextArea.setEditable(false);
		outputTextArea.setWrapText(true);

		simulateButton.setOnAction(event -> {
			// Generate random address sequence
			addresses = RandomNumber.produce();
            /*int columns = 10; // 每行的列数
			int maxAddress = Arrays.stream(addresses).max().orElse(0);
			int columnWidth = String.valueOf(maxAddress).length() + 1; // 计算每列的宽度*/

			StringBuilder result = new StringBuilder();

			result.append("下面是生成的400个随机地址:\n");
			for (int i = 0; i < addresses.length; i++) {
                /*String formattedAddress = String.format("%-" + columnWidth + "d", addresses[i]);
				result.append(formattedAddress);*/
				result.append(String.format("%d", addresses[i])).append("\t");
				if ((i + 1) % 10 == 0) {
					result.append("\n");
				}
			}
			outputTextArea.setText(result.toString());

			//refreshPageNumbers();
			//accuracy = new double[3][MAX_PAGE_FRAMES - MIN_PAGE_FRAMES + 1];


			outputTextArea.setText(result.toString());
		});

		nextButton.setOnAction((event -> {
			pageAddresses = ConvertToPageNumber.convert(addresses);
			StringBuilder result = new StringBuilder();
			result.append("下面是根据400个随机地址所产生的相应的页号\n");
			for (int i = 0; i < 400; i++) {
				if (i != 0 && i % 10 == 0) {// 以每行10个的形式输出
					result.append('\n');
				}
				//System.out.printf("%-3d ", ary[i]);// 格式化输出
				result.append(String.format("%d", pageAddresses[i])).append("\t\t");
			}
			result.append(" ");
			outputTextArea.setText(result.toString());
		}));


		calculateButton.setOnAction(event2 -> {
			//int[] pageAddresses = ConvertToPageNumber.convert(addresses);

			for (int pageFrame = 4; pageFrame <= 40; pageFrame++) {
				Calculation.calculateOPT(pageAddresses, pageFrame);
				Calculation.calculateFIFO(pageAddresses, pageFrame);
				Calculation.calculateLRU(pageAddresses, pageFrame);
			}

			StringBuilder result = new StringBuilder();
			result.append("页框数\t\tOPT命中率\t\t\tFIFO命中率\t\t\tLRU命中率\n");

			for (int pageFrame = 4; pageFrame <= 40; pageFrame++) {
				result.append(pageFrame).append("\t\t\t");
				result.append(String.format("%.4f", Calculation.accuracy[0][pageFrame - 4])).append("\t\t\t\t");
				result.append(String.format("%.4f", Calculation.accuracy[1][pageFrame - 4])).append("\t\t\t\t");
				result.append(String.format("%.4f", Calculation.accuracy[2][pageFrame - 4])).append("\n");
			}
			outputTextArea.setText(result.toString());
		});

		countButton.setOnAction((event -> {
			AccuracyChart chart = new AccuracyChart();
			Stage chartStage = new Stage();
			chart.start(chartStage);
		}));

		ScrollPane scrollPane = new ScrollPane(outputTextArea);
		scrollPane.setPrefSize(700, 500);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		root.getChildren().addAll(buttonBox, scrollPane);

		Scene scene = new Scene(root, 800, 600);
		primaryStage.setTitle("Page Replacement Simulation");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}