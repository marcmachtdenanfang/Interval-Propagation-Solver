package org.mcnip.solver.Model;

import org.mcnip.solver.Contractors.BoolContractor;

import lombok.Getter;

/*
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
*/

public class Bool extends Constraint {
    
    @Getter private final String name;
    @Getter private boolean polarity = true;

    public Bool(String name, boolean polarity)
    {
        super(new BoolContractor());
        this.name = name;
        this.polarity = polarity;
    }

    public Bool(String name)
    {
        super(new BoolContractor());
        this.name = name;
    }

    public String[] getVariables()
    {
        String[] t = {this.name};
        return t;
    }

    @Override
    public String toString()
    {
        return (polarity ? name : "!" + name);
    }
    
}