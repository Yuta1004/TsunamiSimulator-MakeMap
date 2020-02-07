package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

import lib.NegativeBGAreaChart;

public class MainUIController implements Initializable {

    // FXML
    @FXML
    AnchorPane areaChartPane;

    // 描画用
    NegativeBGAreaChart seabedChart;

    @Override
    public void initialize(URL location, ResourceBundle resource) {
        // 初期化
        initAreaChart();
    }

    /**
     * AreaChart設定
     */
    private void initAreaChart() {
        // x, y軸
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Distance(km)");
        yAxis.setLabel("Height(m)");

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

}
