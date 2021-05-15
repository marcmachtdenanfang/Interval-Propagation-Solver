package org.mcnip.solver.Parser;

import lombok.Getter;

public class LitVariable implements AST {

    @Getter private String name;
    @Getter private LitType type;

    public LitVariable(String name, LitType type)
    {
        this.name = name;
        this.type = type;
    }
}
