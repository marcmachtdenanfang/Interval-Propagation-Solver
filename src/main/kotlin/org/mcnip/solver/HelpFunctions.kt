package org.mcnip.solver

import org.mcnip.solver.Model.Clause
import org.mcnip.solver.Model.Constraint
import org.mcnip.solver.Model.Interval

fun Interval.isEmpty() = lowerBound > upperBound

fun findUnits(clauses: List<Clause>, map: Map<String, Interval>): List<Constraint> {
  val units = mutableListOf<Constraint>()
  var maybeUnit = mapOf<String, Interval>() to clauses[0].constraints[0]
  clauses.forEach { clause ->
    when (clause.constraints.map { constraint ->
      maybeUnit = Context.updateIntervals(map.filter { it.key in constraint.variables }, constraint) to constraint
      maybeUnit.first.map { it.value.isEmpty() }.reduce { acc, bool -> acc || bool }
    }.filter { !it }.size) {
      //0 -> signaling UNSAT should already be possible here
      1 -> units += maybeUnit.second
    }
  }
  return units
}
