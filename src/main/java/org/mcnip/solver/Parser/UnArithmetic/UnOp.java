package org.mcnip.solver.Parser.UnArithmetic;

import org.mcnip.solver.Parser.AST;

import lombok.Getter;

public abstract class UnOp implements AST {
    
    @Getter AST node;

    public UnOp(AST node)
    {
        this.node = node;
    }
}