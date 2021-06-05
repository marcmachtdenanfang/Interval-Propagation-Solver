package org.mcnip.solver.Model;

import lombok.Getter;

public class Bool implements Atom {
    
    @Getter private final String name;
    @Getter private boolean polarity = true;

    public Bool(String name, boolean polarity)
    {
        this.name = name;
        this.polarity = polarity;
    }

    public Bool(String name)
    {
        this.name = name;
    }

}
