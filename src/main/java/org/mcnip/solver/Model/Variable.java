package org.mcnip.solver.Model;

import lombok.Getter;
import lombok.Setter;

public class Variable {
    
    @Getter         private String name;
    @Getter @Setter private Interval interval;

    public Variable(String name)
    {
        this.name = name;
    }

    public Variable(String name, Interval interval)
    {
        this.name = name;
        this.interval = interval;
    }

}
