package org.mcnip.solver;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;

import org.junit.Test;

public class ParserTest {

    @Test
    public void ParserTest1()
    {
        //Parser parser = new Parser("abc");
        assertTrue(true);
    }

    @Test
    public void KotlinStaticTest()
    {
        ExampleKt.example();
    }

    @Test(expected = FileNotFoundException.class)
    public void KotlinFileNotFoundTest()
    {
        Parser p = new Parser("abcd");
    }

    @Test
    public void ex00Test() {
        Parser p = new Parser("./hys-formulas/ex0.hys");
        System.out.println(p.asCNF());
    }

    @Test
    public void ex04Test() {
        Parser p = new Parser("./hys-formulas/ex4.hys");
        System.out.println(p.asCNF());
    }
    
}
