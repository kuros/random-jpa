package com.github.kuros.random.jpa.mapper;

import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.link.Link;
import com.github.kuros.random.jpa.metamodel.MetaModelProvider;
import com.github.kuros.random.jpa.metamodel.model.FieldName;
import com.github.kuros.random.jpa.provider.ForeignKeyRelation;
import com.github.kuros.random.jpa.provider.RelationshipProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/*
 * Copyright (c) 2015 Kumar Rohit
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License or any
 *    later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class RelationCreatorTest {

    @Mock
    private MetaModelProvider metaModelProvider;

    @Mock
    private RelationshipProvider relationshipProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRelationCreatorWithEmptyMetaModelProvider() throws Exception {
        when(metaModelProvider.getFieldsByTableName()).thenReturn(new HashMap<String, List<FieldName>>());
        final List<Relation> generate = RelationCreator.from(metaModelProvider).generate();
        assertEquals(0, generate.size());
    }

    @Test
    public void testRelationIsCreatedFromDatabaseRelationship() throws Exception {
        when(metaModelProvider.getFieldsByTableName()).thenReturn(getFieldsByTableName());

        final List<Relation> generate = RelationCreator
                .from(metaModelProvider)
                .with(Dependencies.newInstance())
                .generate();
        assertEquals(0, generate.size());
    }

    @Test
    public void testRelationWithForeignKeyRelationship() throws Exception {
        when(metaModelProvider.getFieldsByTableName()).thenReturn(getFieldsByTableName());

        mockRelationshipProvider();

        final List<Relation> generate = RelationCreator
                .from(metaModelProvider)
                .with(relationshipProvider)
                .generate();
        assertEquals(2, generate.size());
        assertEquals(getDeclaredField(TestClass.class, "attr1"), generate.get(0).getFrom());
        assertEquals(getDeclaredField(TestClass2.class, "attr1"), generate.get(0).getTo());

        assertEquals(getDeclaredField(TestClass.class, "attr2"), generate.get(1).getFrom());
        assertEquals(getDeclaredField(TestClass2.class, "attr2"), generate.get(1).getTo());
    }

    @Test
    public void testNoRelationIsCreatedWhenFieldNotFound() throws Exception {
        when(metaModelProvider.getFieldsByTableName()).thenReturn(getFieldsByTableName());

        mockRelationshipProviderWithNoMapping();

        final List<Relation> generate = RelationCreator
                .from(metaModelProvider)
                .with(relationshipProvider)
                .generate();

        assertEquals(0, generate.size());
    }

    private void mockRelationshipProvider() {
        final List<ForeignKeyRelation> foreignKeyRelations = new ArrayList<ForeignKeyRelation>();
        foreignKeyRelations.add(ForeignKeyRelation.newInstance("test_class_table_name", "attr1", "test_class_2_table_name", "attr1"));
        foreignKeyRelations.add(ForeignKeyRelation.newInstance("test_class_table_name", "attr_2", "test_class_2_table_name", "attr2"));
        when(relationshipProvider.getForeignKeyRelations()).thenReturn(foreignKeyRelations);
    }

    private void mockRelationshipProviderWithNoMapping() {
        final List<ForeignKeyRelation> foreignKeyRelations = new ArrayList<ForeignKeyRelation>();
        foreignKeyRelations.add(ForeignKeyRelation.newInstance("test_class_table_name", "attr3", "test_class_2_table_name", "attr3"));
        foreignKeyRelations.add(ForeignKeyRelation.newInstance("test_class_table_name", "attr_4", "test_class_2_table_name", "attr4"));
        when(relationshipProvider.getForeignKeyRelations()).thenReturn(foreignKeyRelations);
    }

    private Map<String, List<FieldName>> getFieldsByTableName() throws NoSuchFieldException {
        final Map<String, List<FieldName>> fieldsByTableName = new HashMap<String, List<FieldName>>();

        FieldName attr1 = new FieldName(getDeclaredField(TestClass.class, "attr1"), null);
        FieldName attr2 = new FieldName(getDeclaredField(TestClass.class, "attr2"), "attr_2");
        List<FieldName> testClassFieldNames = new ArrayList<FieldName>();
        testClassFieldNames.add(attr1);
        testClassFieldNames.add(attr2);
        fieldsByTableName.put("test_class_table_name", testClassFieldNames);


        FieldName attr3 = new FieldName(getDeclaredField(TestClass2.class, "attr1"), null);
        FieldName attr4 = new FieldName(getDeclaredField(TestClass2.class, "attr2"), null);
        List<FieldName> testClass2FieldNames = new ArrayList<FieldName>();
        testClass2FieldNames.add(attr3);
        testClass2FieldNames.add(attr4);
        fieldsByTableName.put("test_class_2_table_name", testClass2FieldNames);

        return fieldsByTableName;
    }

    private Field getDeclaredField(Class<?> type, String attr1) throws NoSuchFieldException {
        return type.getDeclaredField(attr1);
    }

    private class TestClass {
        private Integer attr1;
        private Integer attr2;
    }

    private class TestClass2 {
        private Integer attr1;
        private Integer attr2;
    }

    private class TestClass3 {
        private Integer attr3;
    }
}
