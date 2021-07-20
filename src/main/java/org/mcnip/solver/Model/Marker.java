package org.mcnip.solver.Model;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Marker implements Atom {
    @Override
    public String toString() {
        return "|";
    }
}