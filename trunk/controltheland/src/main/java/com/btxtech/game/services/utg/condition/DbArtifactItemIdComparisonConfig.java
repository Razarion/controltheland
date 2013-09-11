package com.btxtech.game.services.utg.condition;

import com.btxtech.game.jsre.common.utg.config.AbstractComparisonConfig;
import com.btxtech.game.jsre.common.utg.config.ArtifactItemIdComparisonConfig;
import com.btxtech.game.services.common.CrudChildServiceHelper;
import com.btxtech.game.services.common.CrudParent;
import com.btxtech.game.services.item.ServerItemTypeService;
import org.hibernate.annotations.Cascade;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * User: beat
 * Date: 10.09.13
 * Time: 14:11
 */
@Entity
@DiscriminatorValue("ARTIFACT_ITEM")
public class DbArtifactItemIdComparisonConfig extends DbAbstractComparisonConfig implements CrudParent {
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "dbArtifactItemIdComparisonConfig", orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
    private Collection<DbComparisonArtifactItemCount> dbComparisonArtifactItemCounts;
    @Transient
    private CrudChildServiceHelper<DbComparisonArtifactItemCount> artifactItemCountCrud;

    public CrudChildServiceHelper<DbComparisonArtifactItemCount> getArtifactItemCountCrud() {
        if (artifactItemCountCrud == null) {
            if (dbComparisonArtifactItemCounts == null) {
                dbComparisonArtifactItemCounts = new ArrayList<>();
            }
            artifactItemCountCrud = new CrudChildServiceHelper<>(dbComparisonArtifactItemCounts, DbComparisonArtifactItemCount.class, this);
        }
        return artifactItemCountCrud;
    }

    @Override
    public AbstractComparisonConfig createComparisonConfig(ServerItemTypeService serverItemTypeService) {
        Map<Integer, Integer> artifactIdTypeCount = new HashMap<>();
        for (DbComparisonArtifactItemCount artifactItemCount : dbComparisonArtifactItemCounts) {
            artifactIdTypeCount.put(artifactItemCount.getDbInventoryArtifact().getId(), artifactItemCount.getCount());
        }
        return new ArtifactItemIdComparisonConfig(artifactIdTypeCount);
    }

    @Override
    protected DbAbstractComparisonConfig createCopy() {
        DbArtifactItemIdComparisonConfig copy = new DbArtifactItemIdComparisonConfig();
        copy.dbComparisonArtifactItemCounts = new HashSet<>();
        getArtifactItemCountCrud().copyTo(copy.getArtifactItemCountCrud());
        return copy;
    }
}
