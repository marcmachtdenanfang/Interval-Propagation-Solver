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
        
        Context ctx = new Context();

        System.out.println( "Hello World!" );
        Pair<Integer, Double> p = new Pair(3,4.0, new AddContractor());
        System.out.println(p.getResult().getClass());
        System.out.println(3+p.getResult());
        int i = p.getResult();

    }
}
