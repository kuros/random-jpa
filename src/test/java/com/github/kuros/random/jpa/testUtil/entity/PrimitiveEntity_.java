package com.github.kuros.random.jpa.testUtil.entity;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

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
@StaticMetamodel(PrimitiveEntity.class)
public class PrimitiveEntity_ {

    public static volatile SingularAttribute<PrimitiveEntity, Long> id;
    public static volatile SingularAttribute<PrimitiveEntity, Boolean> defaultBoolean;
    public static volatile SingularAttribute<PrimitiveEntity, Byte> defaultByte;
    public static volatile SingularAttribute<PrimitiveEntity, Short> defaultShort;
    public static volatile SingularAttribute<PrimitiveEntity, Integer> defaultInt;
    public static volatile SingularAttribute<PrimitiveEntity, Long> defaultLong;
    public static volatile SingularAttribute<PrimitiveEntity, Float> defaultFloat;
    public static volatile SingularAttribute<PrimitiveEntity, Double> defaultDouble;
    public static volatile SingularAttribute<PrimitiveEntity, Character> defaultChar;

}
