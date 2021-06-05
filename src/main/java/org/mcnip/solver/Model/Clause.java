package org.mcnip.solver.Model;

import java.util.List;
import java.util.Set;

import lombok.Getter;

public class Clause {

    @Getter private Set<String> variables;
    @Getter private List<Constraint> constraints;

    public Clause(Set<String> variables, List<Constraint> constraints)
    {
        this.variables = variables;
        this.constraints = constraints;
    }
    
}
