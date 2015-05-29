package com.github.kuros.random.jpa.metamodel;

import java.util.List;
import java.util.Map;

/**
 * Created by Kumar Rohit on 5/30/15.
 */
public interface MetaModelProvider {

    Map<String, List<FieldName>> getFieldsByTableName();

}
