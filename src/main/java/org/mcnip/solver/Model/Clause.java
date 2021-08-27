package org.mcnip.solver.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Clause {

    private final Set<String> variables;
    private final List<Constraint> constraints;

    public Clause(Set<String> variables, List<Constraint> constraints)
    {
        this.variables = variables;
        this.constraints = constraints;
    }

    @Override
    public String toString()
    {
        return "" + new ArrayList<>(this.constraints);
    }

    public Set<String> getVariables() {
        return variables;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

}
