package com.github.kuros.random.jpa.resolver;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/30/15.
 */
public interface EntityResolver {
    Map<Field, Object> getFieldValueMap();
}
