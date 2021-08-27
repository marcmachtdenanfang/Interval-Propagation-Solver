package org.mcnip.solver.Model;

import lombok.Getter;

import java.util.List;

public class Formula {
    
    @Getter private final List<Clause> clauses;

    /**
     * Only accepts formulas already in CNF as per our parser.
     * (The parser generates CNF).
     * @param clauses List of Clauses.
     */
    public Formula(List<Clause> clauses) {
        this.clauses = clauses;
    }

}
