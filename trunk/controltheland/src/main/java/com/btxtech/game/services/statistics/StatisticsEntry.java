package com.btxtech.game.services.statistics;

import javax.persistence.Embeddable;

/**
 * User: beat
 * Date: 16.09.2011
 * Time: 20:58:42
 */
@Embeddable
public class StatisticsEntry {
    private int killedStructureBot;
    private int killedUnitsBot;
    private int killedStructurePlayer;
    private int killedUnitsPlayer;
    private int lostStructureBot;
    private int lostUnitsBot;
    private int lostStructurePlayer;
    private int lostUnitsPlayer;
    private int builtStructures;
    private int builtUnits;
    private int basesDestroyedBot;
    private int basesDestroyedPlayer;
    private int basesLostBot;
    private int basesLostPlayer;

    public int getKilledStructureBot() {
        return killedStructureBot;
    }

    public void increaseKilledStructureBot() {
        killedStructureBot++;
    }

    public int getKilledUnitsBot() {
        return killedUnitsBot;
    }

    public void increaseKilledUnitsBot() {
        killedUnitsBot++;
    }

    public int getKilledStructurePlayer() {
        return killedStructurePlayer;
    }

    public void increaseKilledStructurePlayer() {
        killedStructurePlayer++;
    }

    public int getKilledUnitsPlayer() {
        return killedUnitsPlayer;
    }

    public void increaseKilledUnitsPlayer() {
        killedUnitsPlayer++;
    }

    public int getLostStructureBot() {
        return lostStructureBot;
    }

    public void increaseLostStructureBot() {
        lostStructureBot++;
    }

    public int getLostUnitsBot() {
        return lostUnitsBot;
    }

    public void increaseLostUnitsBot() {
        lostUnitsBot++;
    }

    public int getLostStructurePlayer() {
        return lostStructurePlayer;
    }

    public void increaseLostStructurePlayer() {
        lostStructurePlayer++;
    }

    public int getLostUnitsPlayer() {
        return lostUnitsPlayer;
    }

    public void increaseLostUnitsPlayer() {
        lostUnitsPlayer++;
    }

    public int getBuiltStructures() {
        return builtStructures;
    }

    public void increaseBuiltStructures() {
        builtStructures++;
    }

    public int getBuiltUnits() {
        return builtUnits;
    }

    public void increaseBuiltUnits() {
        builtUnits++;
    }

    public int getBasesDestroyedBot() {
        return basesDestroyedBot;
    }

    public void increaseBasesDestroyedBot() {
        basesDestroyedBot++;
    }

    public int getBasesDestroyedPlayer() {
        return basesDestroyedPlayer;
    }

    public void increaseBasesDestroyedPlayer() {
        basesDestroyedPlayer++;
    }

    public int getBasesLostBot() {
        return basesLostBot;
    }

    public void increaseBasesLostBot() {
        basesLostBot++;
    }

    public int getBasesLostPlayer() {
        return basesLostPlayer;
    }

    public void increaseBasesLostPlayer() {
        basesLostPlayer++;
    }

    public void setKilledStructureBot(int killedStructureBot) {
        this.killedStructureBot = killedStructureBot;
    }

    public void setKilledUnitsBot(int killedUnitsBot) {
        this.killedUnitsBot = killedUnitsBot;
    }

    public void setKilledStructurePlayer(int killedStructurePlayer) {
        this.killedStructurePlayer = killedStructurePlayer;
    }

    public void setKilledUnitsPlayer(int killedUnitsPlayer) {
        this.killedUnitsPlayer = killedUnitsPlayer;
    }

    public void setLostStructureBot(int lostStructureBot) {
        this.lostStructureBot = lostStructureBot;
    }

    public void setLostUnitsBot(int lostUnitsBot) {
        this.lostUnitsBot = lostUnitsBot;
    }

    public void setLostStructurePlayer(int lostStructurePlayer) {
        this.lostStructurePlayer = lostStructurePlayer;
    }

    public void setLostUnitsPlayer(int lostUnitsPlayer) {
        this.lostUnitsPlayer = lostUnitsPlayer;
    }

    public void setBuiltStructures(int builtStructures) {
        this.builtStructures = builtStructures;
    }

    public void setBuiltUnits(int builtUnits) {
        this.builtUnits = builtUnits;
    }

    public void setBasesDestroyedBot(int basesDestroyedBot) {
        this.basesDestroyedBot = basesDestroyedBot;
    }

    public void setBasesDestroyedPlayer(int basesDestroyedPlayer) {
        this.basesDestroyedPlayer = basesDestroyedPlayer;
    }

    public void setBasesLostBot(int basesLostBot) {
        this.basesLostBot = basesLostBot;
    }

    public void setBasesLostPlayer(int basesLostPlayer) {
        this.basesLostPlayer = basesLostPlayer;
    }
}
