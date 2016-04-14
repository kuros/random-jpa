package com.github.kuros.random.jpa.testUtil.hierarchyGraph;

import com.github.kuros.random.jpa.link.Link;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.testUtil.entity.A;
import com.github.kuros.random.jpa.testUtil.entity.A_;
import com.github.kuros.random.jpa.testUtil.entity.B;
import com.github.kuros.random.jpa.testUtil.entity.B_;
import com.github.kuros.random.jpa.testUtil.entity.C;
import com.github.kuros.random.jpa.testUtil.entity.C_;
import com.github.kuros.random.jpa.testUtil.entity.D;
import com.github.kuros.random.jpa.testUtil.entity.D_;
import com.github.kuros.random.jpa.testUtil.entity.E;
import com.github.kuros.random.jpa.testUtil.entity.E_;
import com.github.kuros.random.jpa.testUtil.entity.F;
import com.github.kuros.random.jpa.testUtil.entity.F_;
import com.github.kuros.random.jpa.testUtil.entity.P;
import com.github.kuros.random.jpa.testUtil.entity.P_;
import com.github.kuros.random.jpa.testUtil.entity.Q;
import com.github.kuros.random.jpa.testUtil.entity.Q_;
import com.github.kuros.random.jpa.testUtil.entity.R;
import com.github.kuros.random.jpa.testUtil.entity.R_;
import com.github.kuros.random.jpa.testUtil.entity.RelationEntity;
import com.github.kuros.random.jpa.testUtil.entity.RelationEntity_;
import com.github.kuros.random.jpa.testUtil.entity.RelationManyToOne;
import com.github.kuros.random.jpa.testUtil.entity.RelationManyToOne_;
import com.github.kuros.random.jpa.testUtil.entity.RelationOneToOne;
import com.github.kuros.random.jpa.testUtil.entity.RelationOneToOne_;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.X_;
import com.github.kuros.random.jpa.testUtil.entity.Y;
import com.github.kuros.random.jpa.testUtil.entity.Y_;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.entity.Z_;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DependencyHelper {

    public static List<Relation> getDependency() {

        final List<Relation> relations = new ArrayList<Relation>();

        relations.add(Relation.newInstance(getFieldWrapper(C.class, "aId"), getFieldWrapper(A.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(C.class, "bId"), getFieldWrapper(B.class, "id")));

        relations.add(Relation.newInstance(getFieldWrapper(D.class, "aId"), getFieldWrapper(A.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(D.class, "cId"), getFieldWrapper(C.class, "id")));

        relations.add(Relation.newInstance(getFieldWrapper(E.class, "dId"), getFieldWrapper(D.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(E.class, "bId"), getFieldWrapper(B.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(E.class, "fId"), getFieldWrapper(F.class, "id")));

        relations.add(Relation.newInstance(getFieldWrapper(Z.class, "xId"), getFieldWrapper(X.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(Z.class, "yId"), getFieldWrapper(Y.class, "id")));

        relations.add(Relation.newInstance(getFieldWrapper(Q.class, "pId"), getFieldWrapper(P.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(R.class, "pId"), getFieldWrapper(P.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(RelationEntity.class, "relationOneToOne"), getFieldWrapper(RelationOneToOne.class, "id")));
        relations.add(Relation.newInstance(getFieldWrapper(RelationEntity.class, "relationManyToOne"), getFieldWrapper(RelationManyToOne.class, "id")));

        return relations;
    }

    public static List<Link> getLinks() {
        final List<Link> links = new ArrayList<Link>();

        links.add(Link.newLink(C_.aId, A_.id));
        links.add(Link.newLink(C_.bId, B_.id));

        links.add(Link.newLink(D_.aId, A_.id));
        links.add(Link.newLink(D_.cId, C_.id));

        links.add(Link.newLink(E_.dId, D_.id));
        links.add(Link.newLink(E_.bId, B_.id));
        links.add(Link.newLink(E_.fId, F_.id));

        links.add(Link.newLink(Z_.xId, X_.id));
        links.add(Link.newLink(Z_.yId, Y_.id));

        links.add(Link.newLink(Q_.pId, P_.id));
        links.add(Link.newLink(R_.pId, P_.id));

        links.add(Link.newLink(RelationEntity_.relationOneToOne, RelationOneToOne_.id));
        links.add(Link.newLink(RelationEntity_.relationManyToOne, RelationManyToOne_.id));
        return links;
    }

    private static FieldWrapper getFieldWrapper(final Class<?> aClass, final String aId) {
        try {
            final Field declaredField = aClass.getDeclaredField(aId);
            return new FieldWrapper(declaredField);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

}
