package org.mcnip.solver.Parser;

import lombok.Getter;

public class Constant implements AST {
    
    @Getter private int value;

    public Constant(int value)
    {
        this.value = value;
    }
}
