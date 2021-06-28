package org.mcnip.solver.Model;


import org.mcnip.solver.Contractors.Contractor;


public abstract class Constraint implements Atom {
    
    private Contractor contractor;

    public Constraint(Contractor contractor)
    {
        this.contractor = contractor;
    }

    /**
     * Order of arguments is important!
     * @return
     */
    public abstract String[] getVariables();

    public Contractor getContractor()
    {
        return this.contractor;
    }
    
}
