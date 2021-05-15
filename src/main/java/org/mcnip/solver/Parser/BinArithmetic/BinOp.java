package org.mcnip.solver.Parser.BinArithmetic;

import org.mcnip.solver.Parser.AST;

import lombok.Getter;

public abstract class BinOp implements AST {
    
    @Getter private AST left;
    @Getter private AST right;

    public BinOp(AST left, AST right)
    {
        this.left = left;
        this.right = right;
    }
}