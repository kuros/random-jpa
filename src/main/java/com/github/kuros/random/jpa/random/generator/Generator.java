package com.github.kuros.random.jpa.random.generator;

import java.util.ArrayList;
import java.util.List;

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
public final class Generator {

    private List<RandomAttributeGenerator> randomAttributeGenerators;
    private List<RandomClassGenerator> randomClassGenerators;

    private Generator() {
        randomAttributeGenerators = new ArrayList<RandomAttributeGenerator>();
        randomClassGenerators = new ArrayList<RandomClassGenerator>();
    }

    public static Generator newInstance() {
        return new Generator();
    }

    public Generator addAttributeGenerator(final RandomAttributeGenerator randomAttributeGenerator) {
        randomAttributeGenerators.add(randomAttributeGenerator);
        return this;
    }

    public Generator addClassGenerator(final RandomClassGenerator randomClassGenerator) {
        randomClassGenerators.add(randomClassGenerator);
        return this;
    }

    public List<RandomAttributeGenerator> getRandomAttributeGenerators() {
        return randomAttributeGenerators;
    }

    public List<RandomClassGenerator> getRandomClassGenerators() {
        return randomClassGenerators;
    }
}
