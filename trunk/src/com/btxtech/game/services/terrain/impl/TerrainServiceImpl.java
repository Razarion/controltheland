/*
 * Copyright (c) 2010.
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; version 2 of the License.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 */

package com.btxtech.game.services.terrain.impl;

import com.btxtech.game.jsre.client.common.Constants;
import com.btxtech.game.jsre.client.common.Index;
import com.btxtech.game.jsre.common.gameengine.services.terrain.TerrainType;
import com.btxtech.game.services.terrain.TerrainChangeListener;
import com.btxtech.game.services.terrain.TerrainFieldTile;
import com.btxtech.game.services.terrain.TerrainService;
import com.btxtech.game.services.terrain.Tile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * User: beat
 * Date: May 22, 2009
 * Time: 11:56:20 AM
 */
public class TerrainServiceImpl implements TerrainService {
    private HibernateTemplate hibernateTemplate;
    private int xCount;
    private int yCount;
    private Map<Index, TerrainFieldTile> terrainFieldTiles = new HashMap<Index, TerrainFieldTile>();// TODO get this from the DB do not save here
    private Map<Integer, Tile> tiles = new HashMap<Integer, Tile>(); // TODO get this from the DB do not save here
    private int[][] terrainField;// TODO get this from the DB do not save here
    private Log log = LogFactory.getLog(TerrainServiceImpl.class);
    private ArrayList<TerrainChangeListener> terrainChangeListeners = new ArrayList<TerrainChangeListener>();


    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        hibernateTemplate = new HibernateTemplate(sessionFactory);
    }

    @PostConstruct
    public void init() {
        loadTiles();
        loadTerrainFields();
    }

    @Override
    public void createNewTerrain(final int xCount, final int yCount, final Tile tile) {
        clearTerrain();
        final ArrayList<TerrainFieldTileImpl> terrain = new ArrayList<TerrainFieldTileImpl>();
        for (int i = 0; i < xCount; i++) {
            for (int j = 0; j < yCount; j++) {
                Index index = new Index(i, j);
                TerrainFieldTileImpl terrainFiledImpl = new TerrainFieldTileImpl(index, tile);
                terrainFieldTiles.put(index, terrainFiledImpl);
                terrain.add(terrainFiledImpl);
            }
        }
        hibernateTemplate.saveOrUpdateAll(terrain);
        TerrainServiceImpl.this.xCount = xCount;
        TerrainServiceImpl.this.yCount = yCount;
        onTerrainChanged();
    }

    @Override
    public void clearTerrain() {
        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Query query = session.createQuery("delete from " + TerrainFieldTileImpl.class.getName());
                query.executeUpdate();
                return null;
            }
        });
        TerrainServiceImpl.this.xCount = 0;
        TerrainServiceImpl.this.yCount = 0;
        terrainFieldTiles.clear();
        onTerrainChanged();
    }


    @Override
    public Tile createTile(byte[] imageData, TerrainType terrainType) {
        final Tile tile = new TileImpl(imageData, terrainType);
        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                session.save(tile);
                if (tile.getId() == null) {
                    throw new NullPointerException();
                }
                tiles.put(tile.getId(), tile);
                return null;
            }
        });
        return tile;
    }

    @Override
    public void createTile() {
        createTile(new byte[0], TerrainType.LAND);
    }

    @Override
    public void activateTerrainField(int[][] filed) {
        Map<Index, TerrainFieldTile> terrainFieldTiles = setupTerrainFieldTile(filed);
        activateTerrainFieldTiles(terrainFieldTiles);
    }

    @Override
    public boolean isTerrainValid() {
        return xCount > 0 && yCount > 0;
    }

    private void activateTerrainFieldTiles(final Map<Index, TerrainFieldTile> terrainFieldTiles) {
        clearTerrain();
        hibernateTemplate.saveOrUpdateAll(terrainFieldTiles.values());
        this.terrainFieldTiles = terrainFieldTiles;
        init();
    }

    private Map<Index, TerrainFieldTile> setupTerrainFieldTile(int[][] filed) {
        int yLengt = -1;
        Map<Index, TerrainFieldTile> terrainFieldTiles = new HashMap<Index, TerrainFieldTile>();
        for (int x = 0; x < filed.length; x++) {
            int[] innerField = filed[x];
            if (yLengt == -1) {
                yLengt = innerField.length;
            } else if (yLengt != innerField.length) {
                throw new IllegalArgumentException("The y length of the filed is different");
            }
            for (int y = 0; y < innerField.length; y++) {
                int tileId = innerField[y];
                Tile tile = tiles.get(tileId);
                if (tile == null) {
                    throw new IllegalArgumentException("The tile: " + x + ":" + y + " id: " + tileId + " does not exist");
                }
                Index index = new Index(x, y);
                TerrainFieldTileImpl terrainFiledImpl = new TerrainFieldTileImpl(index, tile);
                terrainFieldTiles.put(index, terrainFiledImpl);
            }
        }
        return terrainFieldTiles;
    }

    @Override
    public void saveTile(final Tile tile) {
        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                session.saveOrUpdate(tile);
                return null;
            }
        });
    }

    @Override
    public void deleteTile(final Tile tile) {
        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                session.delete(tile);
                return null;
            }
        });
        loadTiles();
    }

    @Override
    public List<Integer> getTileIds() {
        return new ArrayList<Integer>(tiles.keySet());
    }

    @Override
    public List<Tile> getTiles() {
        return new ArrayList<Tile>(tiles.values());
    }

    @Override
    public Tile getTile(int id) {
        Tile tile = tiles.get(id);
        if (tile == null) {
            throw new IllegalArgumentException("Tile does not exist: " + id);
        }
        return tile;
    }

    @Override
    public void clearTiles() {
        hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(TileImpl.class);
                for (Object o : criteria.list()) {
                    session.delete(o);
                }
                return null;
            }
        });
        tiles.clear();
    }

    private void loadTiles() {
        List<TileImpl> list = (List<TileImpl>) hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(TileImpl.class);
                return criteria.list();
            }
        });
        tiles.clear();
        for (TileImpl tile : list) {
            tiles.put(tile.getId(), tile);
        }
    }

    private void loadTerrainFields() {
        List<TerrainFieldTileImpl> list = (List<TerrainFieldTileImpl>) hibernateTemplate.execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Criteria criteria = session.createCriteria(TerrainFieldTileImpl.class);
                return criteria.list();
            }
        });
        if (list.isEmpty()) {
            log.error("Not terrain field in DB");
            return;
        }

        xCount = -1;
        yCount = -1;
        for (TerrainFieldTileImpl terrainFieldImpl : list) {
            Index index = terrainFieldImpl.getIndex();
            terrainFieldTiles.put(index, terrainFieldImpl);
            if (index.getX() > xCount) {
                xCount = index.getX();
            }
            if (index.getY() > yCount) {
                yCount = index.getY();
            }
        }
        if (xCount == -1) {
            xCount = 0;
        } else {
            xCount++;
        }
        if (yCount == -1) {
            yCount = 0;
        } else {
            yCount++;
        }
        onTerrainChanged();
    }

    @Override
    public int[][] getTerrainField() {
        return terrainField;
    }

    private void onTerrainChanged() {
        terrainField = new int[xCount][];
        for (int x = 0; x < terrainField.length; x++) {
            terrainField[x] = new int[yCount];
            for (int y = 0; y < terrainField[x].length; y++) {
                TerrainFieldTile tile = terrainFieldTiles.get(new Index(x, y));
                terrainField[x][y] = tile.getTile().getId();
            }
        }
        for (TerrainChangeListener terrainChangeListener : terrainChangeListeners) {
            terrainChangeListener.onTerrainChanged();
        }
    }

    @Override
    public TerrainFieldTile getTerrainFieldTile(int indexX, int indexY) {
        return terrainFieldTiles.get(new Index(indexX, indexY));
    }

    @Override
    public Map<Index, TerrainFieldTile> getTerrainFieldTilesCopy() {
        return new HashMap<Index, TerrainFieldTile>(terrainFieldTiles);
    }

    @Override
    public Index getTerrainFieldTileCount() {
        return new Index(xCount, yCount);
    }

    @Override
    public int getPlayFieldXSize() {
        return xCount * Constants.TILE_WIDTH;
    }

    @Override
    public int getPlayFieldYSize() {
        return yCount * Constants.TILE_HEIGHT;
    }

    @Override
    public void addTerrainChangeListener(TerrainChangeListener terrainChangeListener) {
        terrainChangeListeners.add(terrainChangeListener);
        if (xCount > 0 && yCount > 0) {
            terrainChangeListener.onTerrainChanged();
        }
    }

    @Override
    public void removeTerrainChangeListener(TerrainChangeListener terrainChangeListener) {
        terrainChangeListeners.remove(terrainChangeListener);
    }

    @Override
    public Collection<Integer> getPassableTerrainTileIds() {
        ArrayList<Integer> passableTerrainTileIds = new ArrayList<Integer>();
        for (Tile tile : tiles.values()) {
            if (tile.checkTerrainType(TerrainType.LAND)) {
                passableTerrainTileIds.add(tile.getId());
            }
        }
        return passableTerrainTileIds;
    }
}
