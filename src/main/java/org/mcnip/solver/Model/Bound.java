package org.mcnip.solver.Model;

import java.util.ArrayList;
import java.util.List;

import org.mcnip.solver.Contractors.Contractor;

import lombok.Getter;

public class Bound extends Constraint {
    
    @Getter private String varName;
    @Getter private Number bound;

    public Bound(String varName, Number bound, Contractor contractor)
    {
        super(contractor);
        this.varName = varName;
        this.bound = bound;
    }

    public String[] getVariables()
    {
        return new String[1];
    }

}
