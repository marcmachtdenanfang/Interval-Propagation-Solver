package org.mcnip.solver.Model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class Formula {
    
    @Getter private List<Clause> clauses;

    /**
     * Only accepts formulas already in cnf as per our parser.
     * (The parser generates cnf).
     * @param in List of Clauses.
     */
    public Formula(List<Clause> in) {
        this.clauses = preprocessFormula(in);
    }

    /**
     * Make sure that Bools are at the beginning of Clauses,
     * as they are more easily propagated.
     */
    private List<Clause> preprocessFormula(List<Clause> clauses)
    {
        List<Clause> resClauses = new ArrayList<>();
        for(Clause c : clauses)
        {
            List<Constraint> currentClause = new ArrayList<>();
            for(Constraint constr : c.getConstraints())
            {
                if(constr instanceof Bool){
                    currentClause.add(0, constr);
                } else {
                    currentClause.add(constr);
                }
            }
            resClauses.add(new Clause(c.getVariables(), currentClause));
        }
        return resClauses;
    }

}
