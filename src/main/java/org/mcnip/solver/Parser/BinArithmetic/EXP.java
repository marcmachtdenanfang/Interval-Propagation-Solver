package org.mcnip.solver.Parser.BinArithmetic;

import org.mcnip.solver.Parser.AST;
import org.mcnip.solver.Parser.Constant;

public class EXP extends BinOp {
    
    /**
     * 
     * @param node Can be another arithmetic expression or a variable.
     * @param value Value is a constant value.
     */
    public EXP(AST node, Constant value)
    {
        super(node, value);
    }

}
