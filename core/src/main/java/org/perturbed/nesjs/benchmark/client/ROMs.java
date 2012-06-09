package org.perturbed.nesjs.benchmark.client;

import com.google.gwt.core.client.*;
import com.google.gwt.resources.client.*;

public interface ROMs extends ClientBundle
{
    ROMs INSTANCE = GWT.create(ROMs.class);

    @Source("01-implied.nes")
    BinaryResource impliedTestROM();
    
    @Source("nestest.nes")
    BinaryResource nestestROM();
}
