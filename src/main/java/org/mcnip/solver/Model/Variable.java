package org.mcnip.solver.Model;

import lombok.Getter;

public class Variable {
    
    @Getter private String name;
    @Getter private Type type;

    /**
     * Used to manage variables and their types only.
     * Intervals store the name of their variable as well.
     * Booleans are only stored as empty variables and bounds associated via their names.
     * @param name Name of the variable.
     * @param type BOOL, INT or REAL.
     */
    public Variable(String name, Type type)
    {
        this.name = name;
        this.type = type;
    }

}
