package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;

import lib.NegativeBGAreaChart;

public class MainUIController implements Initializable {

    // FXML
    @FXML
    private AnchorPane areaChartPane;
    @FXML
    private Button setWave;
    @FXML
    private Label leftStatus;
    @FXML
    private TextField distVal, depthVal, upperHeightVal, lowerHeightVal, upperWidthVal, lowerWidthVal;

    // 描画用
    private NegativeBGAreaChart seabedChart;
    private double upperHeight, lowerHeight, upperWidth, lowerWidth;

    // 生成データ用
    private ArrayList<SeabedData> seabedData;

    /**
     * fxml.Initializable
     * コンストラクタと同時に一回呼ばれる
     *
     * @param location Location情報
     * @param resource リソース情報
     */
    @Override
    public void initialize(URL location, ResourceBundle resource) {
        // 初期化
        upperWidth = 350.0;
        lowerWidth = 0.0;
        upperHeight = 20.0;
        lowerHeight = -20.0;
        seabedData = new ArrayList<SeabedData>();
        initAreaChart();

        // AreaChartにマウスイベントを実装
        seabedChart.lookup(".chart-plot-background")
                   .setOnMouseDragged(event -> {
                        Number dist = (Number)seabedChart.getXAxis().getValueForDisplay(event.getX());
                        Number depth = (Number)seabedChart.getYAxis().getValueForDisplay(event.getY());
                        leftStatus.setText("Set: "+ dist + "km, " + depth + "m");
                        setSeabedData(dist.doubleValue(), -depth.doubleValue());
                        draw();
                   });

        // UI部品の動作を実装
        setWave.setOnAction(event -> {
            double dist = loadInputValue(distVal);
            double depth = loadInputValue(depthVal);
            setSeabedData(dist, depth);
            draw();
        });
        upperWidthVal.textProperty().addListener((obs, oldText, newText) -> {
            upperWidth = loadInputValue(upperWidthVal);
            initAreaChart();
            draw();
        });
        lowerWidthVal.textProperty().addListener((obs, oldText, newText) -> {
            lowerWidth = loadInputValue(lowerWidthVal);
            initAreaChart();
            draw();
        });
        upperHeightVal.textProperty().addListener((obs, oldText, newText) -> {
            upperHeight = loadInputValue(upperHeightVal);
            initAreaChart();
            draw();
        });
        lowerHeightVal.textProperty().addListener((obs, oldText, newText) -> {
            lowerHeight = loadInputValue(lowerHeightVal);
            initAreaChart();
            draw();
        });
    }

    /**
     * AreaChart設定
     */
    private void initAreaChart() {
        // x, y軸
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Distance(km)");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(lowerWidth);
        xAxis.setUpperBound(upperWidth);
        yAxis.setLabel("Height(m)");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(lowerHeight);
        yAxis.setUpperBound(upperHeight);

        // AreaChart
        seabedChart = new NegativeBGAreaChart<>(xAxis, yAxis);
        seabedChart.setCreateSymbols(true);
        seabedChart.setAnimated(false);

        // 配置
        AnchorPane.setTopAnchor(seabedChart, 10.0);
        AnchorPane.setLeftAnchor(seabedChart, 10.0);
        AnchorPane.setRightAnchor(seabedChart, 10.0);
        AnchorPane.setBottomAnchor(seabedChart, 10.0);
        areaChartPane.getChildren().clear();
        areaChartPane.getChildren().add(seabedChart);
    }

    /**
     * 描画
     */
    private void draw() {
        // 描画データ準備
        XYChart.Series<Number, Number> seriesSeabed = new XYChart.Series<Number, Number>();
        for(SeabedData data : seabedData) {
            seriesSeabed.getData()
                        .add(
                            new XYChart.Data<Number, Number>(data.dist, -data.depth)
                        );
        }

        // データセット
        seabedChart.getData().clear();
        seabedChart.getData().add(seriesSeabed);
        seabedChart.setLegendVisible(false);
    }

    /**
     * データ追加
     */
    private void setSeabedData(double dist, double depth) {
        try {
            SeabedData data = seabedData.stream()
                                        .filter(elem -> elem.dist == dist)
                                        .findFirst()
                                        .get();
            int idx = seabedData.indexOf(data);
            seabedData.set(idx, new SeabedData(dist, depth));
        } catch (Exception e) {
            seabedData.add(new SeabedData(dist, depth));
        }
    }

    /**
     * TextFieldから値を読み取って返す
     */
    private double loadInputValue(TextField tf) {
        try {
            return Double.parseDouble(tf.getText());
        } catch (Exception e) {
            return 0.0;
        }
    }

}
