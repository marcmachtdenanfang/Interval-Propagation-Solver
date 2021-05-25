package org.mcnip.solver.Model;

import java.util.List;

import org.mcnip.solver.Contractors.Contractor;

import lombok.Getter;

public class Formula {
    
    @Getter private List<Clause> clauses;

    public Formula() {
        
    }


}
