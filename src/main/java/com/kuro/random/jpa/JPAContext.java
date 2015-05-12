package com.kuro.random.jpa;

import com.kuro.random.jpa.definition.HierarchyGenerator;
import com.kuro.random.jpa.definition.HierarchyGeneratorImpl;
import com.kuro.random.jpa.definition.RelationCreator;
import com.kuro.random.jpa.link.Dependencies;
import com.kuro.random.jpa.mapper.HierarchyGraph;
import com.kuro.random.jpa.mapper.Relation;
import com.kuro.random.jpa.mapper.TableNode;
import com.kuro.random.jpa.provider.ForeignKeyRelation;
import com.kuro.random.jpa.provider.MetaModelProvider;
import com.kuro.random.jpa.provider.RelationshipProvider;
import com.kuro.random.jpa.types.CreationPlan;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kumar Rohit on 4/22/15.
 */
public final class JPAContext {

    private final EntityManager entityManager;
    private final Map<String, EntityType<?>> metaModelRelations;
    private HierarchyGraph hierarchyGraph;
    private Dependencies dependencies;

    public static JPAContext newInstance(final EntityManager entityManager) {
        return new JPAContext(entityManager, null);
    }

    public static JPAContext newInstance(final EntityManager entityManager, final Dependencies customDependencies) {
        return new JPAContext(entityManager, customDependencies);
    }

    private JPAContext(final EntityManager entityManager, final Dependencies dependencies) {
        this.entityManager = entityManager;
        this.dependencies = dependencies;

        final MetaModelProvider metaModelProvider = MetaModelProvider.newInstance(entityManager);
        this.metaModelRelations = metaModelProvider.getMetaModelRelations();

        initialize();
    }

    private void initialize() {
        final RelationshipProvider relationshipProvider = RelationshipProvider.newInstance(entityManager);
        final List<ForeignKeyRelation> foreignKeyRelations = relationshipProvider.getForeignKeyRelations();

        final List<Relation> relations = RelationCreator.from(metaModelRelations)
                .with(foreignKeyRelations)
                .with(dependencies)
                .generate();

        final HierarchyGenerator hierarchyGenerator = getHierarchyGenerator();
        hierarchyGraph = hierarchyGenerator.generate(relations);
    }

    public void create() {

        final CreationPlan creationPlan = CreationPlan.newIntance();

        final Map<TableNode, List<TableNode>> parentRelations = hierarchyGraph.getParentRelations();
        final Set<TableNode> tableNodes = parentRelations.keySet();

        for (TableNode tableNode : tableNodes) {
            System.out.println(tableNode.getTableClass());
            generateCreationOrder(creationPlan, parentRelations, tableNode);
            creationPlan.add(tableNode);
        }

        for (TableNode tableNode : creationPlan.getCreationPlan()) {
            System.out.println(tableNode.getTableClass());
        }
    }

    private void generateCreationOrder(final CreationPlan creationPlan, final Map<TableNode, List<TableNode>> parentRelations, final TableNode tableNode) {
        final List<TableNode> parents = parentRelations.get(tableNode);
        for (TableNode parent : parents) {
            if (!creationPlan.contains(parent)) {
                if (parentRelations.containsKey(parent)) {
                    generateCreationOrder(creationPlan, parentRelations, parent);
                } else {
                    creationPlan.add(parent);
                }
            }
        }
    }

    private HierarchyGenerator getHierarchyGenerator() {
        return new HierarchyGeneratorImpl();
    }


}
