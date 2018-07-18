/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.uos.se.prom.dsmproject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dziegenhagen
 */
public class MainAppTest {
    
    public MainAppTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
//
//    /**
//     * Test of start method, of class MainApp.
//     */
//    @Test
//    public void testStart() throws Exception {
//        System.out.println("start");
//        Stage stage = null;
//        MainApp instance = new MainApp();
//        instance.start(stage);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of main method, of class MainApp.
//     */
//    @Test
//    public void testMain() {
//        System.out.println("main");
//        String[] args = null;
//        MainApp.main(args);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    
    
    public void tempTest() throws NoSuchAlgorithmException {
        Random random = new Random(System.currentTimeMillis());
        MessageDigest m = MessageDigest.getInstance("MD5");

        for (int i = 0; i < 10; i++) {
            System.out.println(Long.toHexString(random.nextLong()));

            String randString = Long.toHexString(random.nextLong());
            System.out.println(randString);

            m.reset();
            m.update(randString.getBytes());
            byte[] digest = m.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            String hashtext = bigInt.toString(16);

//            byte[] digest1 = digest.digest(randString.getBytes());
//            String string = new String(digest1);
//            
            System.out.println(hashtext);
            System.out.println("");
        }
    }
    
}
