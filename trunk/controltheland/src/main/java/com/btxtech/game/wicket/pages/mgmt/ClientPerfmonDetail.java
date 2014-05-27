package com.btxtech.game.wicket.pages.mgmt;

import com.btxtech.game.services.common.DateUtil;
import com.btxtech.game.services.mgmt.ClientPerfmonDto;
import com.btxtech.game.services.mgmt.ClientPerfmonEntry;
import com.btxtech.game.services.mgmt.MgmtService;
import com.googlecode.charts4j.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        String sessionId = parameters.get(SESSION_KEY).toString();
        ClientPerfmonDto clientPerfmonDto = mgmtService.getClientPerfmonData(sessionId);
        setupChart(clientPerfmonDto);
        setupChildCharts(clientPerfmonDto);
    }

    private void setupChart(ClientPerfmonDto clientPerfmonDto) {
        List<ClientPerfmonEntry> clientPerfmonEntries = new ArrayList<>(clientPerfmonDto.getWorkTimes());
        Collections.sort(clientPerfmonEntries, new Comparator<ClientPerfmonEntry>() {
            @Override
            public int compare(ClientPerfmonEntry o1, ClientPerfmonEntry o2) {
                return Integer.compare(o2.getTime(), o1.getTime());
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
        chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, longestTime / (double) clientPerfmonDto.getTotalTime() * 100.0));

        // Worker axis
        AxisLabels workerAxis = AxisLabelsFactory.newAxisLabels("Worker", 50.0);
        workerAxis.setAxisStyle(axisStyle);
        chart.addXAxisLabels(workerAxis);

        chart.setSize(600, 450);
        chart.setBarWidth(5);
        chart.setSpaceWithinGroupsOfBars(5);
        chart.setTitle("Work time: " + DateUtil.formatDuration(clientPerfmonDto.getTotalTime()), Color.BLACK, 16);
        chart.setGrid(100, 10, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(Color.ALICEBLUE));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.LAVENDER, 100);
        fill.addColorAndOffset(Color.WHITE, 0);
        chart.setAreaFill(fill);
        add(new Charts4jImage(chart));
    }

    private void setupChildCharts(ClientPerfmonDto clientPerfmonDto) {
        List<ClientPerfmonEntry> children = new ArrayList<>();
        for (ClientPerfmonEntry clientPerfmonEntry : clientPerfmonDto.getWorkTimes()) {
            if (clientPerfmonEntry.hasChildren()) {
                children.add(clientPerfmonEntry);
            }
        }


        ListView<ClientPerfmonEntry> listView = new ListView<ClientPerfmonEntry>("childrenPerfmonData", children) {
            @Override
            protected void populateItem(ListItem<ClientPerfmonEntry> listItem) {
                listItem.add(new Label("title", listItem.getModelObject().getPerfmonEnum().getDisplayName()));
                listItem.add(setupChildChart(listItem.getModelObject()));
            }
        };
        add(listView);
    }

    private Charts4jImage setupChildChart(ClientPerfmonEntry clientPerfmonEntry) {
        List<ClientPerfmonEntry.ChildClientPerfmonEntry> childrenPerfmonEntries = clientPerfmonEntry.getSortedChildrenAndRest();
        double longestTime = childrenPerfmonEntries.get(0).getTime();
        List<Color> colors = createColors(childrenPerfmonEntries.size());
        List<BarChartPlot> plots = new ArrayList<>();
        for (ClientPerfmonEntry.ChildClientPerfmonEntry childPerfmonEntry : childrenPerfmonEntries) {
            plots.add(Plots.newBarChartPlot(Data.newData((double) childPerfmonEntry.getTime() / longestTime * 100.0), colors.remove(0), childPerfmonEntry.getChildName()));
        }

        // Instantiating chart.
        BarChart chart = GCharts.newBarChart(plots);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.BLACK, 13, AxisTextAlignment.CENTER);

        // Time axis
        AxisLabels timeAxis = AxisLabelsFactory.newAxisLabels("Load [%]", 50.0);
        timeAxis.setAxisStyle(axisStyle);
        chart.addYAxisLabels(timeAxis);
        chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, longestTime / (double) clientPerfmonEntry.getTime() * 100.0));

        // Worker axis
        AxisLabels workerAxis = AxisLabelsFactory.newAxisLabels("Worker", 50.0);
        workerAxis.setAxisStyle(axisStyle);
        chart.addXAxisLabels(workerAxis);

        chart.setSize(600, 450);
        chart.setBarWidth(5);
        chart.setSpaceWithinGroupsOfBars(5);
        chart.setTitle("Work time: " + DateUtil.formatDuration(clientPerfmonEntry.getTime()), Color.BLACK, 16);
        chart.setGrid(100, 10, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(Color.ALICEBLUE));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.LAVENDER, 100);
        fill.addColorAndOffset(Color.WHITE, 0);
        chart.setAreaFill(fill);
        return new Charts4jImage(chart);
    }

    private List<Color> createColors(int number) {
        List<Color> colors = new ArrayList<>();
        int step = 256 / number;
        for (int i = 0; i < number; i++) {
            colors.add(Color.newColor(String.format("%1$02X%2$02X%3$02X", 255 - (i * step), i * step, i % 2 == 0 ? 0 : 255)));

        }
        return colors;
    }
}
