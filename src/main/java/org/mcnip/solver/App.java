package org.mcnip.solver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.mcnip.solver.Contractors.Contractor;
import org.mcnip.solver.Contractors.BinContractor.AddContractor;
import org.mcnip.solver.Model.Formula;
import org.mcnip.solver.Model.Bound;
import org.mcnip.solver.Model.Atom;
import org.mcnip.solver.Model.Bool;
import org.mcnip.solver.Model.Constraint;
import org.mcnip.solver.Model.Clause;
import org.mcnip.solver.Model.Interval;
import org.mcnip.solver.Model.Pair;
import org.mcnip.solver.SatSolver.Solver;



/**
 * Hello world!
 *
 */
public class App 
{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static CommandLine cmd = null;
    private static String inputFilePath;



    public static void main( String[] args )
    {
        if (args.length == 0)
        {
            System.err.println("Missing path/to/input/file.hys");
            return;
        } 

        // use -h or --help to show options.
        handleOptions(args);

        // Check whether help option was called.
        
        // Parsing
        Parser parser = new Parser(inputFilePath);
        
        
        if(cmd.hasOption("p")) {
            Formula f = parser.getFormula();
            System.out.println(parser.asCNF());
            // This code snippet exists mostly to demonstrate how to use the parser!
            System.out.println(ANSI_GREEN + "-- Variable declarations:" + ANSI_RESET);
            Map<String, Interval> intervals = parser.getIntervals();
            intervals.forEach((k,v) -> System.out.println(v+""+v.getLowerBound().getType()));

            System.out.println(ANSI_GREEN + "-- Clauses: " + ANSI_RESET);
            List<Clause> clauses = f.getClauses();
            clauses.forEach(clause -> System.out.println(clause));

        }

        
        // Main functionality
        Context ctx = new Context(parser, new Solver(){
            public List<Constraint> solve(Formula clauses){ return new ArrayList<Constraint>(); }
        });
        ctx.assertUnitClauses();

        System.out.println("asserted unit clauses");
        for(Atom a : ctx.assertedAtoms){
            System.out.println(a);
        }

        ctx.narrowContractions();
        System.out.println("narrowed assignments");
        ctx.intervalAssignmentStack.peek().forEach((k,v) -> System.out.println(v));
        System.out.println("asserted atoms");
        ctx.assertedAtoms.forEach(a -> System.out.println(a));


        // while satisfiable do things,
        // ctx.doStuff();

        // if UNSAT terminate with error.
        // 

        // if correct result, terminate with solution.

    }


    /**
     * Handles commandline arguments. If -h or --help is passed then only 
     * help page will be printed.
     * <p>
     * -i or --input is necessary flag to pass /path/to/input/file
     * <p>
     * -p or --print to show verbose parser output.
     * @param args Commandline arguments passed to applications.
     */
    private static void handleOptions(String[] args) {
        Options options = new Options();
        Option help = new Option("h", "help", false, "print this message");
        Option print = new Option("p", "print", false, "Optional: verbose printing of parsed formula");
        Option input = 
              Option.builder("i")
                    .longOpt("input")
                    .required(true)
                    .hasArg(true)
                    .desc("hysat input file path")
                    .build();
        
        options.addOption(help);

        CommandLineParser cmdParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        
        boolean hasHelp = false;
        try {
            cmd = cmdParser.parse(options, args);
            if(cmd.hasOption(help.getOpt())) hasHelp = true;
        } catch (ParseException e) { }
        
        // proceed to actually add options.
        options = new Options();        
        options.addOption(input);
        options.addOption(help);
        options.addOption(print);
        cmd = null;

        if(hasHelp || args.length == 0) {
            formatter.printHelp("Help", options);
            System.exit(0);
        }

        try {
            cmd = cmdParser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Interval-Propagation-Solver", options);

            System.exit(1);
        }
        
        // check again if help flag is present.
        if(cmd.hasOption(help.getOpt()))
        {
            formatter.printHelp("Help", options);
            System.exit(0);
        }

        // Finished handling options.
        inputFilePath = cmd.getOptionValue("input");
    }

}
