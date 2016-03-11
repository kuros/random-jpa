package com.github.kuros.random.jpa.log;

import com.github.kuros.random.jpa.exception.RandomJPAException;

import java.lang.reflect.Constructor;

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
public class LogFactory {

    private static final String[] PROVIDED_LOGGERS = {
            "com.github.kuros.random.jpa.log.providers.Slf4JProvider",
            "com.github.kuros.random.jpa.log.providers.Log4JProvider",
            "com.github.kuros.random.jpa.log.providers.DefaultProvider"};
    private static Class<?> activeLogger;

    public static Logger getLogger(final Class<?> clazz) {

        try {
            return instantiateLogger(activeLogger, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new RandomJPAException("Unable to instantiate Logger");
    }

    static {
        activeLogger = getLoggerClass();
    }

    private static Class<?> getLoggerClass() {
        for (String providedLogger : PROVIDED_LOGGERS) {
            try {
                final Class<?> aClass = Class.forName(providedLogger);
                instantiateLogger(aClass, LogFactory.class);
                return aClass;
            } catch (final Exception e) {
                //Do nothing
            }
        }

        throw new RandomJPAException("Unable to find suitable Logger");
    }

    private static Logger instantiateLogger(final Class<?> loggerClass, final Class<?> clazz) throws NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        final Constructor<?> declaredConstructor = loggerClass.getDeclaredConstructor(Class.class);
        declaredConstructor.setAccessible(true);
        return (Logger) declaredConstructor.newInstance(clazz);
    }
}
