/**
 * User: beat
 * Date: 21.03.13
 * Time: 23:34
 */

import com.btxtech.game.jsre.common.gameengine.services.collision.CollisionService;
import com.btxtech.game.jsre.common.gameengine.services.terrain.Terrain;
import gui.MovingGui;
import model.MovingModel;
import scenario.Scenario;
import scenario.moving.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MovingMain {

    public static void main(String[] args) {
        System.out.println("***** START MovingMain *****");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        UIManager.put("swing.boldMetal", Boolean.FALSE);


        Terrain terrain = new Terrain();
        MovingGui movingGui = new MovingGui();
        MovingModel movingModel = new MovingModel();
        movingGui.setMovingModel(movingModel);
        CollisionService collisionService = new CollisionService(movingModel);
        movingModel.setCollisionService(collisionService);
        movingModel.setTerrain(terrain);

        List<Scenario> scenarios = new ArrayList<>();

        scenarios.add(new Frontal());
        scenarios.add(new Frontal3());
        scenarios.add(new MoveToPosition());
        scenarios.add(new Factory());
        scenarios.add(new Frontal2());
        scenarios.add(new Blocking());
        scenarios.add(new FollowPathMulti());
        scenarios.add(new FollowPath());
        scenarios.add(new MoveToOccupiedPosition());
        scenarios.add(new Random());
        // TODO rework
        scenarios.add(new TerrainAndUnits());
        scenarios.add(new TerrainOnly());
        movingGui.setScenarios(scenarios);
        // TODO Attack (Build, Attack, Harvest)
        // TODO Load unload container

        movingModel.init();
        movingGui.start();
    }
}
