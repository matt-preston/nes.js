package org.perturbed.nesjs.benchmark.client;

import org.perturbed.nesjs.benchmark.*;

import com.google.gwt.resources.client.*;
import com.google.gwt.resources.ext.*;

@ResourceGeneratorType(BinaryResourceGenerator.class)
public interface BinaryResource extends ResourcePrototype
{
    public int[] getBytes();
}
