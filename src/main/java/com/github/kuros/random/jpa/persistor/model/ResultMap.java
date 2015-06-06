package com.github.kuros.random.jpa.persistor.model;

import com.github.kuros.random.jpa.types.Printer;

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
public interface ResultMap {
    @SuppressWarnings("unchecked")
    <T> T get(Class<T> type);

    @SuppressWarnings("unchecked")
    <T> T get(Class<T> type, int index);

    @SuppressWarnings("unchecked")
    <T> List<T> getAll(Class<T> type);

    void print(Printer printer);
}
