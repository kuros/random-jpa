package com.github.kuros.random.jpa.mapper;

import com.github.kuros.random.jpa.link.Dependencies;
import com.github.kuros.random.jpa.metamodel.MetaModelProvider;
import com.github.kuros.random.jpa.metamodel.model.FieldWrapper;
import com.github.kuros.random.jpa.provider.RelationshipProvider;
import com.github.kuros.random.jpa.provider.model.ForeignKeyRelation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.metamodel.Attribute;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
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
    @Mock
    private Attribute<TestClass, Integer> attribute;
    @Mock
    private Member member;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(attribute.getJavaMember()).thenReturn(member);
        when(member.getDeclaringClass()).thenAnswer(invocationOnMock -> TestClass.class);
    }

    @Test
    public void testRelationCreatorWithEmptyMetaModelProvider() {
        when(metaModelProvider.getFieldsByTableName()).thenReturn(new HashMap<>());
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
        assertEquals(getDeclaredField(TestClass.class, "attr1"), generate.get(0).getFrom().getField());
        assertEquals(getDeclaredField(TestClass2.class, "attr1"), generate.get(0).getTo().getField());

        assertEquals(getDeclaredField(TestClass.class, "attr2"), generate.get(1).getFrom().getField());
        assertEquals(getDeclaredField(TestClass2.class, "attr2"), generate.get(1).getTo().getField());
    }

    @Test
    public void shouldNotGenerateRelationWithForeignKeyRelationshipWhenIgnoredAttributeIsFound() throws Exception {
        when(metaModelProvider.getFieldsByTableName()).thenReturn(getFieldsByTableName());
        when(attribute.getName()).thenReturn("attr2");

        mockRelationshipProvider();

        final Dependencies dependency = Dependencies.newInstance();
        dependency.ignoreAttributes(attribute);
        final List<Relation> generate = RelationCreator
                .from(metaModelProvider)
                .with(relationshipProvider)
                .with(dependency)
                .generate();
        assertEquals(1, generate.size());
        assertEquals(getDeclaredField(TestClass.class, "attr1"), generate.get(0).getFrom().getField());
        assertEquals(getDeclaredField(TestClass2.class, "attr1"), generate.get(0).getTo().getField());
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

    @Test
    public void testNoRelationIsCreatedWhenTableMappingIsNotFound() throws Exception {
        when(metaModelProvider.getFieldsByTableName()).thenReturn(getFieldsByTableName());

        mockRelationshipProviderForReferencedTableNotFound();

        final List<Relation> generate = RelationCreator
                .from(metaModelProvider)
                .with(relationshipProvider)
                .generate();

        assertEquals(0, generate.size());
    }

    @Test
    public void testNoRelationIsCreatedWhenColumnMappingIsNotFound() throws Exception {
        when(metaModelProvider.getFieldsByTableName()).thenReturn(getFieldsByTableName());

        mockRelationshipProviderForReferencedColumnNotFound();

        final List<Relation> generate = RelationCreator
                .from(metaModelProvider)
                .with(relationshipProvider)
                .generate();

        assertEquals(0, generate.size());
    }
    private void mockRelationshipProvider() {
        final List<ForeignKeyRelation> foreignKeyRelations = new ArrayList<>();
        foreignKeyRelations.add(ForeignKeyRelation.newInstance("test_class_table_name", "attr1", "test_class_2_table_name", "attr1"));
        foreignKeyRelations.add(ForeignKeyRelation.newInstance("test_class_table_name", "attr_2", "test_class_2_table_name", "attr2"));
        when(relationshipProvider.getForeignKeyRelations()).thenReturn(foreignKeyRelations);
    }

    private void mockRelationshipProviderForReferencedTableNotFound() {
        final List<ForeignKeyRelation> foreignKeyRelations = new ArrayList<>();
        foreignKeyRelations.add(ForeignKeyRelation.newInstance("test_class_table_name", "attr1", "table_not_found", "attr1"));
        when(relationshipProvider.getForeignKeyRelations()).thenReturn(foreignKeyRelations);
    }

    private void mockRelationshipProviderForReferencedColumnNotFound() {
        final List<ForeignKeyRelation> foreignKeyRelations = new ArrayList<>();
        foreignKeyRelations.add(ForeignKeyRelation.newInstance("test_class_table_name", "attr1", "test_class_2_table_name", "someAttr"));
        when(relationshipProvider.getForeignKeyRelations()).thenReturn(foreignKeyRelations);
    }

    private void mockRelationshipProviderWithNoMapping() {
        final List<ForeignKeyRelation> foreignKeyRelations = new ArrayList<>();
        foreignKeyRelations.add(ForeignKeyRelation.newInstance("test_class_table_name", "attr3", "test_class_2_table_name", "attr3"));
        foreignKeyRelations.add(ForeignKeyRelation.newInstance("test_class_table_name", "attr_4", "test_class_2_table_name", "attr4"));
        when(relationshipProvider.getForeignKeyRelations()).thenReturn(foreignKeyRelations);
    }

    private Map<String, List<FieldWrapper>> getFieldsByTableName() throws NoSuchFieldException {
        final Map<String, List<FieldWrapper>> fieldsByTableName = new HashMap<>();

        final FieldWrapper attr1 = new FieldWrapper(TestClass.class, getDeclaredField(TestClass.class, "attr1"), null);
        final FieldWrapper attr2 = new FieldWrapper(TestClass.class, getDeclaredField(TestClass.class, "attr2"), "attr_2");
        final List<FieldWrapper> testClassFieldWrappers = new ArrayList<>();
        testClassFieldWrappers.add(attr1);
        testClassFieldWrappers.add(attr2);
        fieldsByTableName.put("test_class_table_name", testClassFieldWrappers);


        final FieldWrapper attr3 = new FieldWrapper(TestClass2.class, getDeclaredField(TestClass2.class, "attr1"), null);
        final FieldWrapper attr4 = new FieldWrapper(TestClass2.class, getDeclaredField(TestClass2.class, "attr2"), null);
        final List<FieldWrapper> testClass2FieldWrappers = new ArrayList<>();
        testClass2FieldWrappers.add(attr3);
        testClass2FieldWrappers.add(attr4);
        fieldsByTableName.put("test_class_2_table_name", testClass2FieldWrappers);

        return fieldsByTableName;
    }

    private Field getDeclaredField(final Class<?> type, final String attr1) throws NoSuchFieldException {
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
