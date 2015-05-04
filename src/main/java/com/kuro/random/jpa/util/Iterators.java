package com.kuro.random.jpa.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/3/15.
 */
public class Iterators {

    public static <T> List<T> safe(final List<T> list) {
        return list == null ? new ArrayList<T>() : list;
    }
}
