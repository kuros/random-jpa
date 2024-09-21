package com.github.kuros.random.jpa.resolver;

import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.link.Link;
import com.github.kuros.random.jpa.mapper.Relation;
import com.github.kuros.random.jpa.testUtil.EntityManagerProvider;
import com.github.kuros.random.jpa.testUtil.entity.X;
import com.github.kuros.random.jpa.testUtil.entity.X_;
import com.github.kuros.random.jpa.testUtil.entity.Z;
import com.github.kuros.random.jpa.testUtil.entity.Z_;
import com.github.kuros.random.jpa.util.Util;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DependencyResolverTest {

    @Test
    public void shouldResolveDependenciesToRelations() {
        EntityManagerProvider.init();
        final Dependencies dependencies = Dependencies.newInstance();
        dependencies.withLink(Link.newLink(Z_.xId, X_.id));

        final Set<Relation> relationSet = DependencyResolver.resolveDependency(dependencies);

        assertEquals(1, relationSet.size());

        final List<Relation> relations = new ArrayList<>(relationSet);

        assertEquals(Util.getField(Z.class, "xId"), relations.get(0).getFrom().getField());
        assertEquals(Util.getField(X.class, "id"), relations.get(0).getTo().getField());
    }

    @Test
    public void shouldReturnEmptyListIfDependenciesAreNull() {
        final Set<Relation> relations = DependencyResolver.resolveDependency(null);
        assertEquals(0, relations.size());
    }
}
