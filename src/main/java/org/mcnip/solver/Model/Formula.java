package org.mcnip.solver.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import lombok.Getter;

public class Formula {
    
    @Getter private List<Clause> clauses;
    @Getter private List<Bool> bools;

    /**
     * Only accepts formulas already in cnf as per our parser.
     * (The parser generates cnf).
     * @param in List of Clauses.
     */
    public Formula(List<Clause> in) {
        this.clauses = preprocessFormula(in);
        this.bools = extractBools(clauses);
    }

    /**
     * Make sure that Bools are at the beginning of Clauses,
     * as they are more easily propagated.
     * @param clauses List of {@link org.mcnip.solver.Model.Clause}.
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

    private List<Bool> extractBools(List<Clause> clauses)
    {
        List<Bool> retBools = new ArrayList<>();
        Iterator<Clause> iterator = clauses.iterator();

        while(iterator.hasNext())
        {
            Clause clause = iterator.next();
            List<Bool> temp = clause.getConstraints()
                                .stream()
                                .filter(constr -> constr instanceof Bool)
                                .map(constr -> (Bool) constr) // unfortunately necessary
                                .collect(Collectors.toList());
            retBools.addAll(temp);
        }
        return retBools;
    }

}
