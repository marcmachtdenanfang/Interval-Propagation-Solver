package org.mcnip.solver.Model;

import java.util.List;
import lombok.Getter;

public class Formula {
    
    @Getter private List<Clause> clauses;

    /**
     * Only accepts formulas already in cnf as per our parser.
     * (The parser generates cnf).
     * @param clauses List of Clauses.
     */
    public Formula(List<Clause> clauses) {
        this.clauses = clauses;
    }

}
