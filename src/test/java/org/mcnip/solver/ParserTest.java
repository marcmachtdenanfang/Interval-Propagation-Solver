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
        ParserKt.test();
    }

    @Test(expected = FileNotFoundException.class)
    public void KotlinFileNotFoundTest()
    {
        Parser p = new Parser("abcd");
    }

    @Test
    public void KotlinFileFoundTest()
    {
        Parser p = new Parser("./hys-formulas/bouncing_ball_euler.hys");
    }
    
}
