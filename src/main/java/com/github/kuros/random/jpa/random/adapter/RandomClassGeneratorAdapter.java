package com.github.kuros.random.jpa.random.adapter;

import com.github.kuros.random.jpa.random.generator.RandomClassGenerator;
import com.openpojo.random.RandomGenerator;

import java.util.Collection;

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
public class RandomClassGeneratorAdapter {

    public static RandomGenerator adapt(final RandomClassGenerator randomClassGenerator) {
        return new RandomGenerator() {
            public Collection<Class<?>> getTypes() {
                return randomClassGenerator.getTypes();
            }

            public Object doGenerate(final Class<?> aClass) {
                return randomClassGenerator.doGenerate(aClass);
            }
        };
    }
}
