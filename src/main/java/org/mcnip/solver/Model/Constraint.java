package org.mcnip.solver.Model;


import org.mcnip.solver.Contractors.Contractor;
import lombok.Getter;

public abstract class Constraint implements Atom {
    
    @Getter private Contractor contractor;

    public Constraint(Contractor contractor)
    {
        this.contractor = contractor;
    }

    /**
     * Order of arguments is important!
     * @return
     */
    public abstract String[] getVariables();
    
}
