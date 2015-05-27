package com.github.kuros.random.jpa.persistor.random.generator;

import javax.persistence.metamodel.Attribute;
import java.util.List;

/**
 * Created by Kumar Rohit on 5/16/15.
 */
public interface RandomAttributeGenerator {

    List<? extends Attribute> getAttributes();

    Object doGenerate();
}
