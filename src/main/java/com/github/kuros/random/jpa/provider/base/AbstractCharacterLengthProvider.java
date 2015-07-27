package com.github.kuros.random.jpa.provider.base;

import com.github.kuros.random.jpa.metamodel.AttributeProvider;
import com.github.kuros.random.jpa.metamodel.model.EntityTableMapping;
import com.github.kuros.random.jpa.provider.SQLCharacterLengthProvider;
import com.github.kuros.random.jpa.provider.model.ColumnCharacterLength;
import com.github.kuros.random.jpa.provider.model.ColumnDetail;
import com.github.kuros.random.jpa.util.NumberUtil;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
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
    private Map<String, ColumnCharacterLength> columnLengthsByTable;
    protected EntityManager entityManager;
    protected AttributeProvider attributeProvider;

    public AbstractCharacterLengthProvider(final AttributeProvider attributeProvider, final EntityManager entityManager) {
        this.attributeProvider = attributeProvider;
        this.entityManager = entityManager;
        this.columnLengthsByTable = init();
    }

    protected Map<String, ColumnCharacterLength> init() {
        final Map<String, ColumnCharacterLength> lengths = new HashMap<String, ColumnCharacterLength>();
        final Query query = entityManager.createNativeQuery(getQuery());
        final List resultList = query.getResultList();
        for (Object o : resultList) {
            final Object[] row = (Object[]) o;

            final EntityTableMapping entityTableMapping = attributeProvider.get((String) row[0]);

            if (entityTableMapping == null) {
                continue;
            }

            final String attributeName = entityTableMapping.getAttributeName((String) row[1]);
            final Integer length = (Integer) row[2];

            final ColumnDetail columnDetail = new ColumnDetail(length);

            final String entityName = entityTableMapping.getEntityName();
            ColumnCharacterLength columnCharacterLength = lengths.get(entityName);
            if (columnCharacterLength == null) {
                columnCharacterLength = ColumnCharacterLength.newInstance();
                lengths.put(entityName, columnCharacterLength);
            }

            columnCharacterLength.add(attributeName, columnDetail);
        }

        return lengths;
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

            numberFormat.setRoundingMode(RoundingMode.HALF_UP);

            returnValue = NumberUtil.parseNumber(value.getClass(), numberFormat.format(value));
        }
        return returnValue;
    }

    protected abstract String getQuery();
}
