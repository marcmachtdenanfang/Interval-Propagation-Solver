package org.mcnip.solver.Model;

import java.util.List;

import org.mcnip.solver.Contractors.Contractor;

import lombok.Getter;

public abstract class Constraint {
    
    @Getter private Contractor contractor;

    public Constraint(Contractor contractor)
    {
        this.contractor = contractor;
    }

    public abstract List<String> getVariables();

}
