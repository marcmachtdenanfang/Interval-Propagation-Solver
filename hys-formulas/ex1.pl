:- use_module(library(clpfd)).

main :-
	Vars = [A1, A2, A3,A4, A5, A, NN],
	N = 5,
	M = 1,
	N*A1+M #= NN,
	N*A2+M #= NN-A1-1*M,
	N*A3+M #= NN-A1-A2-2*M,
	N*A4+M #= NN-A1-A2-A3-3*M,
	N*A5+M #= NN-A1-A2-A3-A4-4*M,
	N*A+M  #= NN-A1-A2-A3-A4-5*M,
	domain(Vars,-10000,100000),
	labeling([],Vars),
	write(Vars).
