package org.perturbed.nesjs.resource.binary.client;

import org.perturbed.nesjs.resource.binary.*;

import com.google.gwt.resources.client.*;
import com.google.gwt.resources.ext.*;

@ResourceGeneratorType(BinaryResourceGenerator.class)
public interface BinaryResource extends ResourcePrototype
{
    public int[] getBytes();
}
