[![Build Status](https://www.travis-ci.com/marcmachtdenanfang/Interval-Propagation-Solver.svg?branch=main)](https://www.travis-ci.com/marcmachtdenanfang/Interval-Propagation-Solver)

# Interval-Propagation-Solver

*To build the solver:*

mvn package


*To run the solver:* (no functionality yet)

java -cp target/Interval-Propagation-Solver-0.1-ALPHA.jar org.mcnip.solver.App


We need a mechanism to call an error when a contraction fails.
Then we know, that some of these clauses lead to a conflict.
So first of all we then want to *immediately* halt this iteration.
How do we find the clauses that were the origin of the conflict?
Do we need identifiers for the clauses?


Regarding the Classes:

SatSolver and below: 
only dummy implementation yet.

Parser and below: 
only AST model Classes to get a feeling on how to work with a Parser.
Basis for writing our own Parser.
Tools like Antlr4 probably generate their own way of dealing with this.

Model:
General idea on what we generate based on our parser output.
Needs to be extended so that we can have both variables as well as numbers everywhere.
Pair, Triplet and Bound all share the trait, that they are Constraints!
Bound needs to be implemented.

Contractor:
Idea: 
we have a Contractor interface, and pass all necessary data to the contract method.
That way using polymorphism we save a lot of implementation effort, as we just have to call contract.
AddContractor does not yet deal with open/closed intervals.

Problem:
How to nicely handle issues like: which value is supposed to be subtracted from which?
Rudimentary solution:
String[] names as an argument with fixed order,
i.e. for binary operations: String[0] is the result, String[1] is the argument left of the operator, and String[2] is on the right of the operator.

x = y-z
x = String[0]
y = String[1]
z = String[2]



@Getter/@Setter:
I added the Lombok Plugin to easily add Getter/Setter methods where necessary.