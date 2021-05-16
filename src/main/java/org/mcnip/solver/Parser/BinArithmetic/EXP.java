package org.mcnip.solver.Parser.BinArithmetic;

import org.mcnip.solver.Parser.AST;
import org.mcnip.solver.Parser.IntConstant;

public class EXP extends BinOp {
    
    /**
     * 
     * @param node Can be another arithmetic expression or a variable.
     * @param exponent Exponent is a constant value.
     */
    public EXP(AST node, IntConstant exponent)
    {
        super(node, exponent);
    }

}
