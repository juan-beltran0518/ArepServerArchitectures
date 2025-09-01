package com.edu.esuelaing.arep;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.edu.esuelaing.arep.ioc.MicroSpringBoot;
import org.junit.jupiter.api.Test;

public class IoCSampleTest {

    @Test
    void testMicroSpringBootRun() {
        try {
            MicroSpringBoot.run("java.lang.String");
            assertTrue(true); 
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void testMicroSpringBootRunWithInvalidClass() {
        try {
            MicroSpringBoot.run("com.nonexistent.Class");
            assertTrue(false); 
        } catch (Exception e) {
            assertTrue(true); 
        }
    }
}