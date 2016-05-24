package com.github.kuros.random.jpa.log;

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
 *
 *    Logging functionality provided by OpenPojo (http://openpojo.com)
 */
public interface Logger {

    void info(final String message, final Object... args);
    void info(final String message, final Throwable throwable);

    void debug(final String message, final Object... args);
    void debug(final String message, final Throwable throwable);

    void warn(final String message, final Object... args);
    void warn(final String message, final Throwable throwable);

    void error(final String message, final Object... args);
    void error(final String message, final Throwable throwable);
}
