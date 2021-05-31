package org.mcnip.solver.Model;


import org.mcnip.solver.Contractors.Contractor;
import lombok.Getter;
import lombok.Setter;

public abstract class Constraint implements Atom {
    
    @Getter @Setter private Contractor contractor;

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
