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
public class Logger {
    private final com.openpojo.log.Logger logger;
    public Logger(com.openpojo.log.Logger logger) {
        this.logger = logger;
    }

    public void info(final String message, final Object... args) {
        logger.info(message, args);
    }

    public void debug(final String message, final Object... args) {
        logger.debug(message, args);
    }

    public void error(final String message, final Object... args) {
        logger.error(message, args);
    }
}
