package org.mcnip.solver.Parser;

import lombok.Getter;

public class IntConstant implements AST {
    
    @Getter private int value;

    public IntConstant(int value)
    {
        this.value = value;
    }
}
