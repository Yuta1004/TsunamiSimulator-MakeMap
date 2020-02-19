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
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import java.util.Collections;

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
    private MenuItem saveData;
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

        // UI部品の動作を実装
        saveData.setOnAction(event -> outputSeabedData());
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
        seabedChart.setCreateSymbols(false);
        seabedChart.setAnimated(false);

        // マウスイベント
        Node seabedChartBK = seabedChart.lookup(".chart-plot-background");
        seabedChartBK.setOnMouseDragged(event -> {
            Number dist = (Number)seabedChart.getXAxis().getValueForDisplay(event.getX());
            Number depth = (Number)seabedChart.getYAxis().getValueForDisplay(event.getY());
            leftStatus.setText("DataLength: "+ seabedData.size() + ", Set: "+ dist + "km, " + depth + "m");
            setSeabedData(dist.doubleValue(), -depth.doubleValue());
            draw();
        });


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
        double distP = Math.ceil(dist*1000)/1000.0;
        try {
            SeabedData data = seabedData.stream()
                                        .filter(elem -> elem.dist == distP)
                                        .findFirst()
                                        .get();
            int idx = seabedData.indexOf(data);
            seabedData.set(idx, new SeabedData(distP, depth));
        } catch (Exception e) {
            seabedData.add(new SeabedData(distP, depth));
        }
    }

    /**
     * 地形データ出力
     */
    private void outputSeabedData() {
        // FileChooser準備
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save File");

        // パス取得->保存
        File outputFile = chooser.showSaveDialog((Stage)areaChartPane.getScene().getWindow());
        if(outputFile != null) {
            try {
                FileWriter fw = new FileWriter(outputFile.getAbsolutePath(), false);
                PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
                for(SeabedData data : normalization())
                    pw.println(data.dist + "\t" + (-data.depth));
                pw.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    /**
     * データ正規化
     */
    private ArrayList<SeabedData> normalization() {
        // 1. 元データソート
        Collections.sort(seabedData);

        // 2. 設定 & 準備
        double gran = 0.3;
        int size = seabedData.size();
        ArrayList<SeabedData> retData = new ArrayList<SeabedData>();

        // 3. 正規化
        int idxA = 0, idxB;
        for(; idxA < size-1; ++ idxA) {
            idxB = idxA+1;
            SeabedData dataA = seabedData.get(idxA);
            SeabedData dataB = seabedData.get(idxB);
            double dd = (dataB.depth-dataA.depth)/(dataB.dist-dataA.dist);
            for(double dist = dataA.dist; dist <= dataB.dist; dist += gran)
                retData.add(new SeabedData(dist, dataA.depth+(dist-dataA.dist)*dd));
        }

        // 4. ソート
        Collections.sort(retData);
        return retData;
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
