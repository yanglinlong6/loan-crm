package com.loan.cps;

import com.loan.cps.common.JSONUtil;
import com.loan.cps.common.MobileLocationUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */

public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        System.out.println(JSONUtil.toString(MobileLocationUtil.getAndCheck("13632965527")));
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        System.out.println(JSONUtil.toString(MobileLocationUtil.getAndCheck("13632965527")));
    }

    public static void main(String[] args){

    }
}
