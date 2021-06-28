package org.mcnip.solver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Contractors.BinContractor.AddContractor;
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

        Context ctx = new Context(Mockito.mock(IParser.class), in, mockedSolver);
        
        // simulate behaviour of ctx.update() method.
        HashMap<String, Interval> intervals = new HashMap<>();
        for(String id : triple.getVariables())
        {
            intervals.put(id, in.get(id));
        }

        Map<String, Interval> tempMap = ctx.updateIntervals(intervals, triple);
        
        assertTrue(tempMap.get("x").getLowerBound().equals(IPSNumber.ZERO_int));
        assertTrue(tempMap.get("y").getLowerBound().equals(new IPSNumber(9, Type.INT)));
        assertTrue(tempMap.get("z").getLowerBound().equals(IPSNumber.TEN_int));

        assertTrue(tempMap.get("x").getUpperBound().equals(IPSNumber.ONE_int));
        assertTrue(tempMap.get("y").getUpperBound().equals(IPSNumber.TEN_int));
        assertTrue(tempMap.get("z").getUpperBound().equals(IPSNumber.TEN_int));
        
        // original y should be the same as before.
        assertFalse(y.getLowerBound().equals(new IPSNumber(9, Type.INT)));
        assertFalse(intervals.get("y").getLowerBound().equals(new IPSNumber(9, Type.INT)));
        assertTrue(y.getLowerBound().equals(IPSNumber.ZERO_int));
        assertTrue(intervals.get("y").getLowerBound().equals(IPSNumber.ZERO_int));
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

        Context ctx = new Context(Mockito.mock(IParser.class), in, mockedSolver);

        // Method that actually gets tested.
        ctx.update();

        // Check results for Correctness.
        assertTrue(ctx.varIntervals.get("x").getLowerBound().equals(IPSNumber.ZERO_int));
        assertTrue(ctx.varIntervals.get("y").getLowerBound().equals(new IPSNumber(9, Type.INT)));
        assertTrue(ctx.varIntervals.get("z").getLowerBound().equals(IPSNumber.TEN_int));

        assertTrue(ctx.varIntervals.get("x").getUpperBound().equals(IPSNumber.ONE_int));
        assertTrue(ctx.varIntervals.get("y").getUpperBound().equals(IPSNumber.TEN_int));
        assertTrue(ctx.varIntervals.get("z").getUpperBound().equals(IPSNumber.TEN_int));
        System.out.println(ctx.varIntervals.get("x"));
        System.out.println(ctx.varIntervals.get("y"));
        System.out.println(ctx.varIntervals.get("z"));
    }

    @Test
    public void addContractionTest2UpdateMethod()
    {
        // Setup.
        Interval x = new Interval("x", -20, 1, true, true);
        Interval y = new Interval("y", 0, 10, true, true);
        Interval z = new DotInterval("z", 100);
        
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

        Context ctx = new Context(Mockito.mock(IParser.class), in, mockedSolver);

        // Method that actually gets tested.
        ctx.update();
        System.out.println(ctx.varIntervals.get("x"));
        System.out.println(ctx.varIntervals.get("y"));
        System.out.println(ctx.varIntervals.get("z"));

        // Check results for Correctness.
        assertTrue(ctx.varIntervals.get("x").getLowerBound().equals(IPSNumber.ZERO_int));
        assertTrue(ctx.varIntervals.get("y").getLowerBound().equals(new IPSNumber(9, Type.INT)));
        assertTrue(ctx.varIntervals.get("z").getLowerBound().equals(IPSNumber.TEN_int));

        assertTrue(ctx.varIntervals.get("x").getUpperBound().equals(IPSNumber.ONE_int));
        assertTrue(ctx.varIntervals.get("y").getUpperBound().equals(IPSNumber.TEN_int));
        assertTrue(ctx.varIntervals.get("z").getUpperBound().equals(IPSNumber.TEN_int));
    }

    @Test
    public void addContractionTest3UpdateMethod()
    {
        // Setup.
        Interval x = new Interval("x", 10, 10, true, true);
        Interval y = new Interval("y", 10, 10, true, true);
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

        Context ctx = new Context(Mockito.mock(IParser.class), in, mockedSolver);

        // Method that actually gets tested.
        ctx.update();
        System.out.println(ctx.varIntervals.get("x"));
        System.out.println(ctx.varIntervals.get("y"));
        System.out.println(ctx.varIntervals.get("z"));

        // Check results for Correctness.
        assertTrue(ctx.varIntervals.get("x").getLowerBound().equals(IPSNumber.ZERO_int));
        assertTrue(ctx.varIntervals.get("y").getLowerBound().equals(new IPSNumber(9, Type.INT)));
        assertTrue(ctx.varIntervals.get("z").getLowerBound().equals(IPSNumber.TEN_int));

        assertTrue(ctx.varIntervals.get("x").getUpperBound().equals(IPSNumber.ONE_int));
        assertTrue(ctx.varIntervals.get("y").getUpperBound().equals(IPSNumber.TEN_int));
        assertTrue(ctx.varIntervals.get("z").getUpperBound().equals(IPSNumber.TEN_int));
    }

    @Test
    public void addContractionTest4UpdateMethod()
    {
        // Setup.
        Interval x = new Interval("x", Type.INT);
        Interval y = new Interval("y", Type.INT);
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

        Context ctx = new Context(Mockito.mock(IParser.class), in, mockedSolver);

        // Method that actually gets tested.
        ctx.update();
        System.out.println(ctx.varIntervals.get("x"));
        System.out.println(ctx.varIntervals.get("y"));
        System.out.println(ctx.varIntervals.get("z"));

        // Check results for Correctness.
        assertTrue(ctx.varIntervals.get("x").getLowerBound().equals(IPSNumber.ZERO_int));
        assertTrue(ctx.varIntervals.get("y").getLowerBound().equals(new IPSNumber(9, Type.INT)));
        assertTrue(ctx.varIntervals.get("z").getLowerBound().equals(IPSNumber.TEN_int));

        assertTrue(ctx.varIntervals.get("x").getUpperBound().equals(IPSNumber.ONE_int));
        assertTrue(ctx.varIntervals.get("y").getUpperBound().equals(IPSNumber.TEN_int));
        assertTrue(ctx.varIntervals.get("z").getUpperBound().equals(IPSNumber.TEN_int));
    }

    @Test
    public void addContractionTest5UpdateMethod()
    {
        // Setup.
        Interval x = new Interval("x", Type.INT);
        Interval z = new DotInterval("z", 10);
        
        HashMap<String, Interval> in = new HashMap<>();
        in.put(x.getVarName(), x);
        in.put(x.getVarName(), x);
        in.put(z.getVarName(), z);
        
        Contractor addContractor = new AddContractor(); 
        Constraint triple = new Triplet(z,x,x,addContractor);

        // Mocking the "solver.solve(formula)" method call.
        List<Constraint> list = List.of(triple);
        Solver mockedSolver = Mockito.mock(Solver.class);
        when(mockedSolver.solve(any())).thenReturn(list);

        Context ctx = new Context(Mockito.mock(IParser.class), in, mockedSolver);

        // Method that actually gets tested.
        ctx.update();
        System.out.println(ctx.varIntervals.get("x"));
        System.out.println(ctx.varIntervals.get("z"));

        // Check results for Correctness.
        assertTrue(ctx.varIntervals.get("x").getLowerBound().equals(IPSNumber.ZERO_int));
        assertTrue(ctx.varIntervals.get("z").getLowerBound().equals(IPSNumber.TEN_int));

        assertTrue(ctx.varIntervals.get("x").getUpperBound().equals(IPSNumber.ONE_int));
        assertTrue(ctx.varIntervals.get("z").getUpperBound().equals(IPSNumber.TEN_int));
    }

    @Test
    public void extractBoundsTest()
    {
        Interval x = new Interval("x", new IPSNumber(-20, Type.INT), IPSNumber.ONE_int);
        Context ctx = new Context(Mockito.mock(IParser.class), new HashMap<>(), Mockito.mock(Solver.class));

        Map<String, Interval> map = new HashMap<>();
        map.put(x.getVarName(), x);
        List<Bound> newBounds = ctx.extractBounds(map);

        for(Bound b : newBounds)
        {
            if(b.getContractor() instanceof GreaterEqualsContractor)
            {
                assert(b.getVarName().equals(x.getVarName()));
                assert(b.getBound().getLowerBound().equals(b.getBound().getUpperBound()));
                assert(b.getBound().getLowerBound().equals(new IPSNumber(-20, Type.INT)));
            } else if(b.getContractor() instanceof LessEqualsContractor)
            {
                assert(b.getVarName().equals(x.getVarName()));
                assert(b.getBound().getLowerBound().equals(b.getBound().getUpperBound()));
                assert(b.getBound().getLowerBound().equals(new IPSNumber(1, Type.INT)));
            } else
            {
                // expected behaviour is one of the two above.
                assert(false);
            }
        }

        newBounds.forEach(System.out::println);
    }

    @Test
    public void mulContractionTest1UpdateMethod()
    {
        // Setup.
        Interval x = new Interval("x", 1, 1, true, true);
        Interval y = new Interval("y", 2, 2, true, true);
        Interval z = new Interval("z",-100, 100,true,true);

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
        System.out.println(x1);
        System.out.println(y1);
        System.out.println(z1);

        assertTrue(x1.getLowerBound().equals(IPSNumber.ONE_int));
        assertTrue(y1.getLowerBound().equals(new IPSNumber(2, Type.INT)));
        assertTrue(z1.getLowerBound().equals(new IPSNumber(2, Type.INT)));

        assertTrue(x1.getUpperBound().equals(IPSNumber.ONE_int));
        assertTrue(y1.getUpperBound().equals(new IPSNumber(2, Type.INT)));
        assertTrue(z1.getUpperBound().equals(new IPSNumber(2, Type.INT)));
    }

    @Test
    public void addContractionTestWeird01()
    {
        // Setup.
        Interval x = new Interval("x", -20, 1, true, true);
        Interval y = new Interval("y", 0, 10, true, true);
        Interval z = new DotInterval("z", 100);

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

        Context ctx = new Context(Mockito.mock(IParser.class), in, mockedSolver);

        // Method that actually gets tested.
        var res = ctx.updateIntervals(in, triple);
        var x1 = res.get("x");
        var y1 = res.get("y");
        var z1 = res.get("z");
        System.out.println("huhu");
        System.out.println(x1);
        System.out.println(y1);
        System.out.println(z1);

        assertTrue("at least one of them should be empty.", x1.isEmpty() || y1.isEmpty() || z1.isEmpty());
    }
}
