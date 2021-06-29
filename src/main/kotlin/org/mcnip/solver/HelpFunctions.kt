package org.mcnip.solver

import org.mcnip.solver.Context.*
import org.mcnip.solver.Model.*

private fun <K> Map<K, Interval>.containsEmptyInterval() = values.fold(false) { acc, interval -> acc || interval.isEmpty }

private infix fun List<String>.from(assignment: Map<String, Interval>) = map { it to assignment.getValue(it) }.toMap()

fun findUnits(clauses: List<Clause>, map: Map<String, Interval>, assertedAtoms: List<Atom>): List<Constraint> {
  val units = mutableListOf<Constraint>()
  var maybeUnit = mapOf<String, Interval>() to clauses[0].constraints[0]
  clauses.forEach { clause ->
    when (clause.constraints.map { constraint ->
      if(constraint is Bool)
        (assertedAtoms.find { (it is Bool) && it.name == constraint.name } as Bool?)?.run { isPolarity xor constraint.isPolarity }?:false
        /*var temp: bool = false
        var contractor = constraint.getContractor()
        for(a in assertedAtoms) {
          if(a is Bool && a.getVariables()[0].equals(constraint.getVariables()[0])) temp = contractor.boolContract(constraint, a)
          if(temp)
        }*/
      else {
        maybeUnit = updateIntervals(map.filter { it.key in constraint.variables }, constraint) to constraint
        maybeUnit.first.map { it.value.isEmpty }.reduce { acc, bool -> acc || bool }
      }
    }.filter { !it }.size) {
      //0 -> signaling UNSAT should already be possible here
      1 -> units += maybeUnit.second
    }
  }
  return units
}

fun narrowContractors(atoms: List<Atom>, currentAssignment: MutableMap<String, Interval>): kotlin.Pair<MutableMap<String, Interval>, MutableList<Bound>> {
  val bounds = mutableListOf<Bound>()
  atoms.forEach { atom ->
    if (atom !is Bool)
      updateIntervals(when (atom) {
        is Bound -> listOf(atom.varName, atom.bound.varName) from currentAssignment
        is Pair -> listOf(atom.result.varName, atom.origin.varName) from currentAssignment
        else -> listOf((atom as Triplet).result.varName, atom.leftArg.varName, atom.rightArg.varName) from currentAssignment
      }, atom as Constraint).let { newIntervals ->
        if (newIntervals.containsEmptyInterval())
          {} //Step 5
        else
          currentAssignment += newIntervals
        if (atom is Pair || atom is Triplet) {
          bounds += extractBounds(newIntervals)
        }
      }
  }
  return currentAssignment to bounds
}