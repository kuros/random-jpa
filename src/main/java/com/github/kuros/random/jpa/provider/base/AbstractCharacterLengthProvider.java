package com.github.kuros.random.jpa.provider.base;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.provider.SQLCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.model.ColumnCharacterLength;
import com.github.kuros.random.jpa.provider.model.ColumnDetail;
import com.github.kuros.random.jpa.util.NumberUtil;

import javax.persistence.EntityManager;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

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
public abstract class AbstractCharacterLengthProvider implements SQLCharacterLengthProvider {
    protected static final Map<String, Class<?>> DATA_TYPE_MAP;
    private Map<String, ColumnCharacterLength> columnLengthsByTable;
    private EntityManager entityManager;
    private AttributeProvider attributeProvider;

    public AbstractCharacterLengthProvider(final AttributeProvider attributeProvider, final EntityManager entityManager) {
        this.attributeProvider = attributeProvider;
        this.entityManager = entityManager;
        this.columnLengthsByTable = init();
    }

    public Object applyLengthConstraint(final String entityName, final String attributeName, final Object value) {
        final ColumnCharacterLength columnCharacterLength = columnLengthsByTable.get(entityName);
        if (columnCharacterLength == null
                || value == null
                || columnCharacterLength.getColumnDetail(attributeName) == null) {
            return value;
        }

        final ColumnDetail columnDetail = columnCharacterLength.getColumnDetail(attributeName);
        Object returnValue = value;

        if (columnDetail.getStringLength() != null && value instanceof String) {
            final String s = value.toString();
            final int length = s.length() < columnDetail.getStringLength() ? s.length() : columnDetail.getStringLength();
            returnValue = s.substring(0, length);
        }

        if (value instanceof Number) {
            final NumberFormat numberFormat = NumberFormat.getNumberInstance();
            if (columnDetail.getPrecision() != null) {
                numberFormat.setMaximumIntegerDigits(columnDetail.getPrecision());
            }

            if (columnDetail.getScale() != null) {
                numberFormat.setMaximumFractionDigits(columnDetail.getScale());
            }

            returnValue = NumberUtil.castNumber(value.getClass(),
                    NumberUtil.parseNumber(columnDetail.getMappedDataType(), numberFormat.format(value)));
        }
        return returnValue;
    }

    protected abstract Map<String, ColumnCharacterLength> init();
    protected abstract String getQuery();

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    protected AttributeProvider getAttributeProvider() {
        return attributeProvider;
    }

    static {
        DATA_TYPE_MAP = new HashMap<String, Class<?>>();
        DATA_TYPE_MAP.put("int", Integer.class);
        DATA_TYPE_MAP.put("integer", Integer.class);
        DATA_TYPE_MAP.put("bigint", Long.class);
        DATA_TYPE_MAP.put("decimal", Double.class);
        DATA_TYPE_MAP.put("float", Double.class);
        DATA_TYPE_MAP.put("money", Double.class);
        DATA_TYPE_MAP.put("real", Double.class);
    }
}
