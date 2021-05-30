package org.mcnip.solver.Model;

import java.util.List;

import lombok.Getter;

public class Clause {

    @Getter private List<String> variables;
    @Getter private List<Constraint> constraints;

    public Clause(List<String> variables, List<Constraint> constraints)
    {
        this.variables = variables;
        this.constraints = constraints;
    }
    
}
