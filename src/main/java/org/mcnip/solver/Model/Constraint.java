package org.mcnip.solver.Model;


import org.mcnip.solver.Contractors.Contractor;


public abstract class Constraint implements Atom {
    
    private final Contractor contractor;

    public Constraint(Contractor contractor)
    {
        this.contractor = contractor;
    }

    /**
     * Order of arguments is important!
     * @return variables
     */
    public abstract String[] getVariables();

    public Contractor getContractor()
    {
        return this.contractor;
    }
    
}
