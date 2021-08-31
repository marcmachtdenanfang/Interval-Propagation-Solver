[![Build Status](https://www.travis-ci.com/marcmachtdenanfang/Interval-Propagation-Solver.svg?branch=main)](https://www.travis-ci.com/marcmachtdenanfang/Interval-Propagation-Solver)

# Interval-Propagation-Solver

For documentation: Please go to https://marcmachtdenanfang.github.io/Interval-Propagation-Solver/

*To build the solver:*

mvn package

*To run the solver:* 

Right now `mvn package` creates a fat jar, so you can use the next command to run the solver.

    java -cp target/Interval-Propagation-Solver-0.1-ALPHA-jar-with-dependencies.jar org.mcnip.solver.App -i hys-formulas/ex0.hys


