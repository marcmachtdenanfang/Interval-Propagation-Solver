package org.mcnip.solver.Model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Clause {

    private Set<String> variables;
    private List<Constraint> constraints;

    public Clause(Set<String> variables, List<Constraint> constraints)
    {
        this.variables = variables;
        this.constraints = constraints;
    }

    @Override
    public String toString()
    {
        return ""+this.constraints.stream().collect(Collectors.toList());
        //return "variables: " + this.variables.stream().collect(Collectors.joining(","));
    }

    public Set<String> getVariables() {
        return variables;
    }

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public Constraint getConstraint(int n)
    {
        return this.constraints.get(n);
    }
}
