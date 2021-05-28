package org.mcnip.solver;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Contractors.BinContractor.AddContractor;
import org.mcnip.solver.Model.Pair;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        // Parsing
        if (args.length > 0) {
            Parser parser = new Parser(args[0]);
            System.out.println(parser.asCNF());
        }


        // Main functionality
        //Context ctx = new Context();


        // while satisfiable do things,
        // ctx.doStuff();

        // if UNSAT terminate with error.
        // 

        // if correct result, terminate with solution.

        //Pair<Integer, Double> p = new Pair(3,4.0, new AddContractor());
        //System.out.println(p.getResult().getClass());
        //System.out.println(3 + p.getResult());
        //int i = p.getResult();
        

    }
}
