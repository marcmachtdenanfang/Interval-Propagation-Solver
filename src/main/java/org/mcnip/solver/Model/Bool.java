package org.mcnip.solver.Model;

import lombok.Getter;

public class Bool implements Atom {
    
    @Getter private String name;
    @Getter private boolean polarity;

    public Bool(String name, boolean polarity)
    {
        this.name = name;
        this.polarity = polarity;
    }

}
