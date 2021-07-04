package org.mcnip.solver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Contractors.BinContractor.AddContractor;
import org.mcnip.solver.Contractors.BinContractor.MulContractor;
import org.mcnip.solver.Contractors.BoundContractor.GreaterEqualsContractor;
import org.mcnip.solver.Contractors.BoundContractor.LessEqualsContractor;
import org.mcnip.solver.Model.Bound;
import org.mcnip.solver.Model.Constraint;
import org.mcnip.solver.Model.DotInterval;
import org.mcnip.solver.Model.Formula;
import org.mcnip.solver.Model.IPSNumber;
import org.mcnip.solver.Model.Interval;
import org.mcnip.solver.Model.Type;
import org.mcnip.solver.Model.Triplet;
import org.mcnip.solver.SatSolver.Solver;

import org.mockito.Mockito;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public class WeirdTest {
    
   

    @Test
    @Ignore
    public void mulContractionTest1UpdateMethod()
    {
        // Setup.
        Interval x = new Interval("x", 0, 1, true, true);
        Interval y = new Interval("y", -1000, 1000, true, true);
        Interval z = new DotInterval("z", 100);

        HashMap<String, Interval> in = new HashMap<>();
        in.put(x.getVarName(), x);
        in.put(y.getVarName(), y);
        in.put(z.getVarName(), z);

        Contractor c = new MulContractor();
        Constraint triple = new Triplet(z,x,y,c);

        // Mocking the "solver.solve(formula)" method call.
        List<Constraint> list = List.of(triple);
        Solver mockedSolver = Mockito.mock(Solver.class);
        when(mockedSolver.solve(any())).thenReturn(list);

        Context ctx = new Context(Mockito.mock(IParser.class), in, mockedSolver);

        // Method that actually gets tested.
        var res = ctx.updateIntervals(in, triple);
        var x1 = res.get("x");
        var y1 = res.get("y");
        var z1 = res.get("z");
        System.out.println("mulContractionTest1UpdateMethod");

        System.out.println(x1);
        System.out.println(y1);
        System.out.println(y1.getVarName());
        System.out.println(z1);

        assertFalse(ctx.checkForEmptyInterval(Map.of("x",x1, "y",y1, "z",z1)));
        assertTrue(x1.getVarName().equals("x"));
        assertTrue(y1.getVarName().equals("y"));
        assertTrue(z1.getVarName().equals("z"));
    }



}