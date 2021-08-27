package org.mcnip.solver;

import org.apache.commons.cli.*;
import org.mcnip.solver.Model.Formula;
import org.mcnip.solver.Model.Interval;

import java.util.Map;

public class App {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    private static CommandLine cmd = null;
    private static String inputFilePath;

    public static boolean verbosePrinting;


    public static void main( String[] args )
    {
        // use -h or --help to show options.
        handleOptions(args);

        // Parsing
        Parser parser = new Parser(inputFilePath);
        Formula formula = parser.getFormula();
        Map<String, Interval> intervals = parser.getIntervals();
        
        if(cmd.hasOption("p")) {
            System.out.println(parser.asCNF());
            // This code snippet exists mostly to demonstrate how to use the parser!
            System.out.println(ANSI_GREEN + "-- Variable declarations:" + ANSI_RESET);
            intervals.forEach((k,v) -> System.out.println(v+""+v.getLowerBound().getType()));

            System.out.println(ANSI_GREEN + "-- Clauses: " + ANSI_RESET);
            formula.getClauses().forEach(System.out::println);
        }

        // Main functionality
        Context ctx = new Context(formula, intervals);
        
        if (cmd.hasOption("v")) {
            verbosePrinting = true;
            ctx.verbosePrinting = true;
        }

        if (cmd.hasOption("l")) {
            int t = Integer.parseInt(cmd.getOptionValue("l"));
            if (t < 1) {
                System.err.println("Likelihood option must at least be 1.");
                System.err.println("Defaulting likelihood to 5.");
            }
            else
                ctx.probability = t;
        }

        if (cmd.hasOption("b")) {
            int t = Integer.parseInt(cmd.getOptionValue("b"));
            if (t < 0) {
                System.err.println("Bit precision for integers must be positive.");
                System.err.println("Defaulting to 128.");
            }
            else
                ctx.intPrecision = t;
        }

        if (cmd.hasOption("m")) ctx.minimize = cmd.getOptionValue("m");

        if (ctx.solve()) {
            System.out.println("SAT");
            ctx.intervalAssignmentStack.peek().values().forEach(interval -> {
                if (verbosePrinting)
                    System.out.println(interval.toString(true));
                else if (interval.getVarName().charAt(0) != '_')
                    System.out.println(interval);
            } );
        }
        else System.out.println("UNSAT");

        if (cmd.hasOption("p")) System.out.println("Number of backtracks: " + ctx.backtracks);
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
        Option print = new Option("p", "print", false, "Optional: Verbose printing of parsed formula.");
        Option verbose = new Option("v", "verbose", false, "Optional: Verbose printing of Output during solving.");
        Option likelihood = new Option("l", "likelihood", true, "Optional: Change likelihood of adding aux_variables into pool of variables eligible for splitting. Must at least be 1. Default is 5.");
        Option intPrecision = new Option("b", "intPrecision", true, "Optional: Desired number of bits representing integers during variable splitting. Default is 128.");
        Option minimize = new Option("m", "minimize", true, "Optional: Name of variable to minimize.");
        Option input = 
              Option.builder("i")
                    .longOpt("input")
                    .required(true)
                    .hasArg(true)
                    .desc("HySAT input file path")
                    .build();
        
        options.addOption(help);

        CommandLineParser cmdParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        
        boolean hasHelp = false;
        try {
            cmd = cmdParser.parse(options, args);
            if(cmd.hasOption(help.getOpt())) hasHelp = true;
        } catch (ParseException ignored) { }
        
        // proceed to actually add options.
        options = new Options();
        options.addOption(input);
        options.addOption(help);
        options.addOption(print);
        options.addOption(verbose);
        options.addOption(likelihood);
        options.addOption(intPrecision);
        options.addOption(minimize);
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
