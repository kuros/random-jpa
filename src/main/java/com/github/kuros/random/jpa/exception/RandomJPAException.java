package com.github.kuros.random.jpa.exception;

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
public class RandomJPAException extends RuntimeException {

    public RandomJPAException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RandomJPAException(final String message) {
        super(message);
    }

    public RandomJPAException(final Throwable cause) {
        super(cause);
    }
}
