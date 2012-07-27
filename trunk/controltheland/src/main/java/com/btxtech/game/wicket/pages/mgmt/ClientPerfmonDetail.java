package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.jsre.common.perfmon.PerfmonEnum;
import com.btxtech.game.services.mgmt.ClientPerfmonDto;
import com.btxtech.game.services.mgmt.ClientPerfmonEntry;
import com.btxtech.game.services.mgmt.MgmtService;
import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * User: beat
 * Date: 26.07.12
 * Time: 17:16
 */
public class ClientPerfmonDetail extends MgmtWebPage {
    public static final String SESSION_KEY = "sessionId";
    @SpringBean
    private MgmtService mgmtService;

    public ClientPerfmonDetail(PageParameters parameters) {
        super(parameters);
        add(new FeedbackPanel("msgs"));
        String sessionId = parameters.getString(SESSION_KEY);
        setupChart(mgmtService.getClientPerfmonData(sessionId));
    }

    private void setupChart(ClientPerfmonDto clientPerfmonDto) {
        List<ClientPerfmonEntry> clientPerfmonEntries = new ArrayList<>(clientPerfmonDto.getWorkTimes());
        Collections.sort(clientPerfmonEntries, new Comparator<ClientPerfmonEntry>() {
            @Override
            public int compare(ClientPerfmonEntry o1, ClientPerfmonEntry o2) {
                return Integer.compare(o2.getTime(),o1.getTime());
            }
        });
        double longestTime = clientPerfmonEntries.get(0).getTime();
        List<Color> colors = createColors(clientPerfmonDto.getWorkTimes().size());
        List<BarChartPlot> plots = new ArrayList<>();
        for (ClientPerfmonEntry perfmonEntry : clientPerfmonEntries) {
            plots.add(Plots.newBarChartPlot(Data.newData((double) perfmonEntry.getTime() / longestTime * 100.0), colors.remove(0), perfmonEntry.getPerfmonEnum().getDisplayName()));
        }

        // Instantiating chart.
        BarChart chart = GCharts.newBarChart(plots);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.BLACK, 13, AxisTextAlignment.CENTER);

        // Time axis
        AxisLabels timeAxis = AxisLabelsFactory.newAxisLabels("Load [%]", 50.0);
        timeAxis.setAxisStyle(axisStyle);
        chart.addYAxisLabels(timeAxis);
        chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, longestTime / (double)clientPerfmonDto.getTotalTime() * 100.0));

        // Worker axis
        AxisLabels workerAxis = AxisLabelsFactory.newAxisLabels("Worker", 50.0);
        workerAxis.setAxisStyle(axisStyle);
        chart.addXAxisLabels(workerAxis);

        chart.setSize(600, 450);
        chart.setBarWidth(5);
        chart.setSpaceWithinGroupsOfBars(5);
        //chart.setDataStacked(true);
        chart.setTitle("Work time", Color.BLACK, 16);
        chart.setGrid(100, 10, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(Color.ALICEBLUE));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.LAVENDER, 100);
        fill.addColorAndOffset(Color.WHITE, 0);
        chart.setAreaFill(fill);
        add(new Charts4jImage(chart));
    }

    private List<Color> createColors(int number) {
        List<Color> colors = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            colors.add(Color.newColor(String.format("%1$02X%2$02X%3$02X", (int)(Math.random() * 256.0), (int)(Math.random() * 256.0), (int)(Math.random() * 256.0))));
        }
        return colors;
    }
}
