package com.github.kuros.random.jpa.testUtil.hierarchyGraph;

import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.entity.A;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.entity.B;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.entity.C;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.entity.D;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.entity.E;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.entity.P;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.entity.X;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.entity.Y;
import com.github.kuros.random.jpa.testUtil.hierarchyGraph.entity.Z;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DependecyHelper {

    public static List<Relation> getDependency() {

        List<Relation> relations = new ArrayList<Relation>();

        relations.add(Relation.newInstance(getFieldWrapper(C.class, "aId"), getFieldWrapper(A.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(C.class, "bId"), getFieldWrapper(B.class, "id")));

        relations.add(Relation.newInstance(getFieldWrapper(D.class, "aId"), getFieldWrapper(A.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(D.class, "cId"), getFieldWrapper(C.class, "id")));

        relations.add(Relation.newInstance(getFieldWrapper(E.class, "dId"), getFieldWrapper(D.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(E.class, "bId"), getFieldWrapper(B.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(E.class, "pId"), getFieldWrapper(P.class, "id")));

        relations.add(Relation.newInstance(getFieldWrapper(Z.class, "xId"), getFieldWrapper(X.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(Z.class, "yId"), getFieldWrapper(Y.class, "id")));

        return relations;
    }

    private static FieldWrapper getFieldWrapper(final Class<?> aClass, final String aId) {
        try {
            final Field declaredField = aClass.getDeclaredField(aId);
            return new FieldWrapper(declaredField);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
