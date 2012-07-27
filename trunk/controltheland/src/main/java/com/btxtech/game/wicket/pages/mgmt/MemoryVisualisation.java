package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.mgmt.MemoryUsageHistory;
import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import org.apache.wicket.markup.html.panel.Panel;

import java.text.SimpleDateFormat;

/**
 * User: beat
 * Date: 05.03.2012
 * Time: 19:39:30
 */
public class MemoryVisualisation extends Panel {
    public MemoryVisualisation(String id, MemoryUsageHistory memoryUsageHistory, String title) {
        super(id);

        Line initLine = Plots.newLine(Data.newData(memoryUsageHistory.getInits()), Color.newColor("00FF00"), "Init");
        initLine.setLineStyle(LineStyle.newLineStyle(1, 1, 0));

        Line usedLine = Plots.newLine(Data.newData(memoryUsageHistory.getUseds()), Color.newColor("FFFF00"), "Used");
        usedLine.setLineStyle(LineStyle.newLineStyle(1, 1, 0));

        Line comittedLine = Plots.newLine(Data.newData(memoryUsageHistory.getCommitteds()), Color.newColor("0000FF"), "Comitted");
        comittedLine.setLineStyle(LineStyle.newLineStyle(1, 1, 0));

        Line maxLine = Plots.newLine(Data.newData(memoryUsageHistory.getMaxs()), Color.newColor("FF0000"), "Max");
        maxLine.setLineStyle(LineStyle.newLineStyle(1, 1, 0));


        // Defining chart.
        LineChart chart = GCharts.newLineChart(maxLine, comittedLine, usedLine, initLine);
        chart.setSize(700, 420);
        chart.setTitle(title, Color.WHITE, 14);
        chart.setGrid(25, 25, 3, 2);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.WHITE, 12, AxisTextAlignment.CENTER);
        AxisLabels xAxis = AxisLabelsFactory.newAxisLabels(memoryUsageHistory.getSignificantDates(5, new SimpleDateFormat(DateUtil.TIME_FORMAT_STRING)));
        xAxis.setAxisStyle(axisStyle);
        AxisLabels yAxis = AxisLabelsFactory.newAxisLabels(memoryUsageHistory.getSignificantValues(5));
        yAxis.setAxisStyle(axisStyle);

        // Adding axis info to chart.
        chart.addXAxisLabels(xAxis);
        chart.addYAxisLabels(yAxis);

        // Defining background and chart fills.
        chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 100);
        fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
        chart.setAreaFill(fill);

        add(new Charts4jImage(chart));
    }

}
