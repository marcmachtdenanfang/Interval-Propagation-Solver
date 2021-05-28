package org.mcnip.solver;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Contractors.BinContractor.AddContractor;
import org.mcnip.solver.Model.Constraint;
import org.mcnip.solver.Model.DotInterval;
import org.mcnip.solver.Model.Formula;
import org.mcnip.solver.Model.IPSNumber;
import org.mcnip.solver.Model.Interval;
import org.mcnip.solver.Model.NumberType;
import org.mcnip.solver.Model.Triplet;
import org.mcnip.solver.SatSolver.Solver;

import org.mockito.Mockito;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    /**
     * Should pass.
     */
    @Test
    public void addContractionTest1()
    {
        // z = x+y
        Interval x = new Interval("x", -20, 1, true, true);
        Interval y = new Interval("y", 0, 10, true, true);
        Interval z = new DotInterval("z", 10);
        
        HashMap<String, Interval> in = new HashMap<>();
        in.put(x.getVarName(), x);
        in.put(y.getVarName(), y);
        in.put(z.getVarName(), z);

        Contractor addContractor = new AddContractor(); 
        Constraint triple = new Triplet(z,x,y,addContractor);

        Solver mockedSolver = Mockito.mock(Solver.class);

        Context ctx = new Context(mockedSolver, in);
        

        // simulate behaviour of ctx.update() method.
        HashMap<String, Interval> intervals = new HashMap<>();
        for(String id : triple.getVariables())
        {
            intervals.put(id, in.get(id));
        }

        Map<String, Interval> tempMap = ctx.updateIntervals(intervals, triple);
        
        assertTrue(tempMap.get("x").getLowerBound().equals(IPSNumber.ZERO_int));
        assertTrue(tempMap.get("y").getLowerBound().equals(new IPSNumber(9, NumberType.INT)));
        assertTrue(tempMap.get("z").getLowerBound().equals(IPSNumber.TEN_int));

        assertTrue(tempMap.get("x").getUpperBound().equals(IPSNumber.ONE_int));
        assertTrue(tempMap.get("y").getUpperBound().equals(IPSNumber.TEN_int));
        assertTrue(tempMap.get("z").getUpperBound().equals(IPSNumber.TEN_int));

        assertTrue(true);

    }

    /**
     * Should pass.
     */
    @Test
    public void addContractionTest1UpdateMethod()
    {
        // Setup.
        Interval x = new Interval("x", -20, 1, true, true);
        Interval y = new Interval("y", 0, 10, true, true);
        Interval z = new DotInterval("z", 10);
        
        HashMap<String, Interval> in = new HashMap<>();
        in.put(x.getVarName(), x);
        in.put(y.getVarName(), y);
        in.put(z.getVarName(), z);
        
        Contractor addContractor = new AddContractor(); 
        Constraint triple = new Triplet(z,x,y,addContractor);

        // Mocking the "solver.solve(formula)" method call.
        List<Constraint> list = List.of(triple);
        Solver mockedSolver = Mockito.mock(Solver.class);
        when(mockedSolver.solve(any())).thenReturn(list);

        Context ctx = new Context(mockedSolver, in);

        // Method that actually gets tested.
        ctx.update();

        // Check results for Correctness.
        assertTrue(ctx.varIntervals.get("x").getLowerBound().equals(IPSNumber.ZERO_int));
        assertTrue(ctx.varIntervals.get("y").getLowerBound().equals(new IPSNumber(9, NumberType.INT)));
        assertTrue(ctx.varIntervals.get("z").getLowerBound().equals(IPSNumber.TEN_int));

        assertTrue(ctx.varIntervals.get("x").getUpperBound().equals(IPSNumber.ONE_int));
        assertTrue(ctx.varIntervals.get("y").getUpperBound().equals(IPSNumber.TEN_int));
        assertTrue(ctx.varIntervals.get("z").getUpperBound().equals(IPSNumber.TEN_int));
    }

}
