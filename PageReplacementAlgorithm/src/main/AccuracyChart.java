package main;

import calculation.Calculation;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class AccuracyChart extends Application {
    private double lastMouseX, lastMouseY;
    private double lastTranslateX, lastTranslateY;
    private double lastScaleX = 1.0, lastScaleY = 1.0;

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Accuracy Chart");

        // 创建X轴和Y轴
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("页框数");
        yAxis.setLabel("命中率");

        // 创建折线图
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("页面置换算法命中率对比");

        // 创建OPT数据系列
        XYChart.Series<Number, Number> optSeries = new XYChart.Series<>();
        optSeries.setName("OPT");
        for (int pageFrame = 4; pageFrame <= 40; pageFrame++) {
            optSeries.getData().add(new XYChart.Data<>(pageFrame, Calculation.accuracy[0][pageFrame - 4]));
        }

        // 创建FIFO数据系列
        XYChart.Series<Number, Number> fifoSeries = new XYChart.Series<>();
        fifoSeries.setName("FIFO");
        for (int pageFrame = 4; pageFrame <= 40; pageFrame++) {
            fifoSeries.getData().add(new XYChart.Data<>(pageFrame, Calculation.accuracy[1][pageFrame - 4]));
        }

        // 创建LRU数据系列
        XYChart.Series<Number, Number> lruSeries = new XYChart.Series<>();
        lruSeries.setName("LRU");
        for (int pageFrame = 4; pageFrame <= 40; pageFrame++) {
            lruSeries.getData().add(new XYChart.Data<>(pageFrame, Calculation.accuracy[2][pageFrame - 4]));
        }

        // 将数据系列添加到折线图中
        lineChart.getData().add(optSeries);
        lineChart.getData().add(fifoSeries);
        lineChart.getData().add(lruSeries);

        //创建ListView组件
        ListView<String> listView0 = new ListView<>();
        //ListView<String> listView1 =new ListView<>();
        listView0.setPrefWidth(400);
        // listView1.setPrefWidth(400);
        //listView.setPrefHeight(450);

        ObservableList<String> items = FXCollections.observableArrayList();
        for (int i = 0; i < 400; i++) {
            int pageNumber = Main.pageAddresses[i];
            int randomNumber = Main.addresses[i];
            items.add("Page " + pageNumber + ": " + randomNumber);
        }
        listView0.setItems(items);

        //设置ListView的单元格工厂
        listView0.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // 将item拆分为address和pageAddress
                    String[] parts = item.split(":");
                    String address = parts[1].trim();
                    String pageAddress = parts[0].trim();

                    // 创建两个Label显示address和pageAddress
                    Label addressLabel = new Label(address);
                    Label pageAddressLabel = new Label(pageAddress);

                    // 创建HBox容器并添加Label
                    GridPane gridPane = new GridPane();
                    ColumnConstraints column1 = new ColumnConstraints();
                    column1.setPercentWidth(50);
                    ColumnConstraints column2 = new ColumnConstraints();
                    column2.setPercentWidth(50);
                    gridPane.getColumnConstraints().addAll(column1, column2);
                    // 在第一行添加分类标签
                    Label pageLabel = new Label("随机地址值序列");
                    Label addLabel = new Label("页号");
                    gridPane.add(pageLabel, 0, 0);
                    gridPane.add(addLabel, 1, 0);

                    gridPane.add(addressLabel, 0, 1);
                    gridPane.add(pageAddressLabel, 1, 1);
                    //gridPane.setHgap(10);
                    //gridPane.addColumn(0, addressLabel);
                    //gridPane.addColumn(1, pageAddressLabel);

                    setGraphic(gridPane);
                }
            }
        });


        HBox root = new HBox(10);
        VBox chartContainer = new VBox(10);
        VBox listViewContainer = new VBox(10);
        // 创建场景并显示图表
        Scene scene = new Scene(root, 800, 450);
        //root.setPadding(new Insets(30));
        //创建按钮
        Button hideOPTButton = new Button("Hide OPT");
        Button hideFIFOButton = new Button("Hide FIFO");
        Button hideLRUButton = new Button("Hide LRU");
        //创建滚轮面板
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(lineChart);
        scrollPane.setPrefWidth(800);
        scrollPane.setPrefHeight(600);
        //设置滚动面板的滚动条可见
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        chartContainer.getChildren().addAll(scrollPane, hideLRUButton, hideFIFOButton, hideOPTButton);
        listViewContainer.getChildren().addAll(listView0);
        root.getChildren().addAll(chartContainer, listViewContainer);
        //root.getChildren().addAll(lineChart,hideOPTButton,hideFIFOButton,hideLRUButton,scrollPane,listView);
        primaryStage.setScene(scene);
        primaryStage.show();

        /*// 添加鼠标事件监听器
        lineChart.setOnMousePressed(this::handleMousePressed);
        lineChart.setOnMouseDragged(this::handleMouseDragged);*/

        /*// 添加鼠标事件监听器
        lineChart.setOnMouseMoved(event -> {
            // 添加鼠标事件监听器
                double mouseX = event.getX();
                double mouseY = event.getY();
                double xAxisValue = xAxis.getValueForDisplay(mouseX).doubleValue();
                double yAxisValue = yAxis.getValueForDisplay(mouseY).doubleValue();

                Tooltip tooltip = new Tooltip(String.format("(%.2f, %.2f)", xAxisValue, yAxisValue));
                lineChart.setTooltip(tooltip);
        });*/
        // 添加鼠标事件监听器
        lineChart.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                lastMouseX = event.getX();
                lastMouseY = event.getY();
                lastTranslateX = scrollPane.getHvalue();
                lastTranslateY = scrollPane.getVvalue();
            }
        });

        lineChart.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double deltaX = event.getX() - lastMouseX;
                double deltaY = event.getY() - lastMouseY;
                scrollPane.setHvalue(lastTranslateX - deltaX / lineChart.getWidth());
                scrollPane.setVvalue(lastTranslateY - deltaY / lineChart.getHeight());
            }
        });

        lineChart.setOnScroll(event -> {
            double zoomFactor = 1.05;
            if (event.getDeltaY() < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }

            lastScaleX = lastScaleX * zoomFactor;
            lastScaleY = lastScaleY * zoomFactor;

            lineChart.setScaleX(lastScaleX);
            lineChart.setScaleY(lastScaleY);

            // 获取放大后折线图的宽度和高度
            double scaledChartWidth = lineChart.getBoundsInParent().getWidth() * lastScaleX;
            double scaledChartHeight = lineChart.getBoundsInParent().getHeight() * lastScaleY;

            // 设置滚动面板的显示范围
            scrollPane.setFitToWidth(scaledChartWidth <= scrollPane.getWidth());
            scrollPane.setFitToHeight(scaledChartHeight <= scrollPane.getHeight());

            // 调整滚动面板的滚动位置以便在放大后能够滚动查看所需的坐标
            double deltaX = (event.getX() - scrollPane.getWidth() / 2) / lineChart.getWidth();
            double deltaY = (event.getY() - scrollPane.getHeight() / 2) / lineChart.getHeight();
            scrollPane.setHvalue(scrollPane.getHvalue() + deltaX * (1 - 1 / zoomFactor));
            scrollPane.setVvalue(scrollPane.getVvalue() + deltaY * (1 - 1 / zoomFactor));
        });


        /*lineChart.setOnScroll(event -> {
            double zoomFactor = 1.05;
            if (event.getDeltaY() < 0) {
                zoomFactor = 2.0 - zoomFactor;
            }

            lastScaleX = lastScaleX * zoomFactor;
            lastScaleY = lastScaleY * zoomFactor;

            lineChart.setScaleX(lastScaleX);
            lineChart.setScaleY(lastScaleY);

            // 调整滚动面板的滚动位置以便在放大后能够滚动查看所需的坐标
            double scrollFactor =10;
            double deltaX = (event.getX() - scrollPane.getWidth() / 2) / lineChart.getWidth();
            double deltaY = (event.getY() - scrollPane.getHeight() / 2) / lineChart.getHeight();
            scrollPane.setHvalue(scrollPane.getHvalue() + deltaX * (1 - 1 / zoomFactor)*scrollFactor);
            scrollPane.setVvalue(scrollPane.getVvalue() + deltaY * (1 - 1 / zoomFactor)*scrollFactor);
        });*/


        hideOPTButton.setOnAction(event -> {
            boolean visible = !optSeries.getNode().isVisible();
            optSeries.getNode().setVisible(visible);
            for (XYChart.Data<Number, Number> data : optSeries.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setVisible(visible);
                    node.setManaged(visible);
                }
            }
        });

        hideFIFOButton.setOnAction(event -> {
            boolean visible = !fifoSeries.getNode().isVisible();
            fifoSeries.getNode().setVisible(visible);
            for (XYChart.Data<Number, Number> data : fifoSeries.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setVisible(visible);
                    node.setManaged(visible);
                }
            }
        });

        hideLRUButton.setOnAction(event -> {
            boolean visible = !lruSeries.getNode().isVisible();
            lruSeries.getNode().setVisible(visible);
            for (XYChart.Data<Number, Number> data : lruSeries.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    node.setVisible(visible);
                    node.setManaged(visible);
                }
            }
        });
    }
}
        /*Zooming zooming = new Zooming(lineChart);
        scrollPane.addEventFilter(ScrollEvent.ANY,zooming::handle);*/

/*        lineChart.setOnScroll(event -> {
        double scrollDelta = event.getDeltaY();
        double scaleFactor = Math.pow(1.01, scrollDelta);

         获取图表的X轴和Y轴
        NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();*//*

        // 获取数据点的半径
        double dataPointRadius = 1.5; // 根据实际需要调整半径大小

        // 放大或缩小数据点的半径
        if (scrollDelta > 0) {
        dataPointRadius *= scaleFactor;
        } else {
        dataPointRadius /= scaleFactor;
        }

        // 设置新的数据点半径
        for (XYChart.Series<Number, Number> series : lineChart.getData()) {
        for (XYChart.Data<Number, Number> data : series.getData()) {
        Node node = data.getNode();
        if (node != null) {
        node.setStyle("-fx-background-radius: " + dataPointRadius + "px;");
        }
        }
        }

        // 根据比例因子调整X轴和Y轴的范围
        double newLowerBoundX = xAxis.getLowerBound() * scaleFactor;
        double newUpperBoundX = xAxis.getUpperBound() * scaleFactor;
        double newLowerBoundY = yAxis.getLowerBound() * scaleFactor;
        double newUpperBoundY = yAxis.getUpperBound() * scaleFactor;

        // 设置新的X轴和Y轴范围
        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);
        xAxis.setLowerBound(newLowerBoundX);
        xAxis.setUpperBound(newUpperBoundX);
        yAxis.setLowerBound(newLowerBoundY);
        yAxis.setUpperBound(newUpperBoundY);

        // 刷新图表
        lineChart.layout();
        });
        }
     */
    /*private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();
            lastTranslateX = ((LineChart) event.getSource()).getTranslateX();
            lastTranslateY = ((LineChart) event.getSource()).getTranslateY();
        }
    }
    private void handleMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            double deltaX = event.getSceneX() - lastMouseX;
            double deltaY = event.getSceneY() - lastMouseY;
            double newTranslateX = lastTranslateX + deltaX;
            double newTranslateY = lastTranslateY + deltaY;

            ((LineChart) event.getSource()).setTranslateX(newTranslateX);
            ((LineChart) event.getSource()).setTranslateY(newTranslateY);
        }
    }
}
ObservableList<String> resultList = FXCollections.observableArrayList();
        //resultList.add("页框数: OPT命中率:FIFO命中率:LRU命中率");
        for(int i=4;i<400;i++){
            int number =i;
            double optAccuracy = Culsulation.accuracy[0][i-4];
            double fifoAccuracy =Culsulation.accuracy[1][i-4];
            double lruAccuracy = Culsulation.accuracy[2][i-4];
            resultList.add(number+":"+optAccuracy +":"+fifoAccuracy+":"+lruAccuracy);
        }
        listView1.setItems(resultList);
        //设置ListView1的单元格工厂
        listView1.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // 将item拆分
                    String[] parts = item.split(":");
                    String a = parts[0].trim();
                    String b = parts[1].trim();
                    String c = parts[2].trim();
                    String d = parts[3].trim();

                    // 创建4个Label显示address和pageAddress
                    Label label1 = new Label(a);
                    Label label2 = new Label(b);
                    Label label3 = new Label(c);
                    Label label4 = new Label(d);
                    // 创建HBox容器并添加Label
                    GridPane gridPane1 = new GridPane();
                    ColumnConstraints column1 = new ColumnConstraints();
                    column1.setPercentWidth(50);
                    ColumnConstraints column2 = new ColumnConstraints();
                    column2.setPercentWidth(50);
                    ColumnConstraints column3 = new ColumnConstraints();
                    column2.setPercentWidth(50);
                    ColumnConstraints column4 = new ColumnConstraints();
                    column2.setPercentWidth(50);
                    gridPane1.getColumnConstraints().addAll(column1, column2,column3,column4);
                    // 在第一行添加分类标签
                    Label yekuang = new Label("页框");
                    Label rate1 =new Label("opt命中率");
                    Label rate2 =new Label("fifo命中率");
                    Label rate3 =new Label("lru命中率");
                    gridPane1.add(yekuang, 0, 0);
                    gridPane1.add(rate1, 1, 0);
                    gridPane1.add(rate2, 2, 0);
                    gridPane1.add(rate3, 3, 0);

                    gridPane1.add(label1, 0, 1);
                    gridPane1.add(label2, 1, 1);
                    gridPane1.add(label3, 2, 1);
                    gridPane1.add(label4, 3, 1);
                    //gridPane.setHgap(10);
                    //gridPane.addColumn(0, addressLabel);
                    //gridPane.addColumn(1, pageAddressLabel);

                    setGraphic(gridPane1);
                }
            }
        });

     */