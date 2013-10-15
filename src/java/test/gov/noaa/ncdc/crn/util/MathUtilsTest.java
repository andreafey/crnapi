package gov.noaa.ncdc.crn.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class MathUtilsTest {

    @Test
    public final void whatupNaN() {
        assertEquals(Double.NaN,Double.NaN,0.00000000001);

        Double nan = new Double(Double.NaN);
        assertTrue(nan.isNaN());
        assertEquals((double)nan,Double.NaN,0.00000000001);

        double zeroDivByZero = 0.0/0.0;
        Double oneDivByZero = 1.0/0.0;
        Double sqrtNeg2 = Math.sqrt(-2.0);

        // is the number defined as NaN?
        assertTrue(((Double)zeroDivByZero).isNaN());
        assertFalse(oneDivByZero.isNaN());
        assertTrue(sqrtNeg2.isNaN());

        // is the number equal to the special constant NaN?
        assertEquals (zeroDivByZero,Double.NaN,0.00000000001);
        assertNotSame(oneDivByZero, Double.NaN);
        assertEquals((double)sqrtNeg2,Double.NaN,0.00000000001);

        assertTrue(nan.equals((Double)zeroDivByZero));
        assertTrue(nan.equals(zeroDivByZero));
        assertFalse(nan.equals(oneDivByZero));
        assertTrue(nan.equals(sqrtNeg2));

        // does the compareTo function think the numbers are equal?
        assertEquals(0,nan.compareTo(zeroDivByZero));
        assertNotSame(0,nan.compareTo(oneDivByZero));
        assertEquals(0,nan.compareTo(sqrtNeg2));

        // does autoboxing to a double change equality? 
        assertTrue(Double.isNaN(zeroDivByZero));
        assertFalse(Double.isNaN(oneDivByZero));
        assertTrue(Double.isNaN(sqrtNeg2));
    }
    @Test
    public final void testRound() {
        String test = "1.234";
        String round = MathUtils.round(test, 2);
        String expected = "1.23";
        assertEquals("1. not properly rounded",expected,round);
        test = "1.236";
        round = MathUtils.round(test, 2);
        expected = "1.24";
        assertEquals("2. not properly rounded",expected,round);
        test = "31.5907";
        round = MathUtils.round(test, 2);
        expected = "31.59";
        assertEquals("3.1 not properly rounded",expected,round);
        test = "1.235";
        round = MathUtils.round(test, 2);
        expected = "1.24";
        assertEquals("3.2 not properly rounded",expected,round);
        test = "-1.235";
        round = MathUtils.round(test, 2);
        expected = "-1.23";
        assertEquals("3.3 not properly rounded",expected,round);
        test = "33.57725";
        round = MathUtils.round(test, 2);
        expected = "33.58";
        assertEquals("3.4 not properly rounded",expected,round);
        test = "-101.5950";
        round = MathUtils.round(test, 2);
        expected = "-101.59";
        assertEquals("4. not properly rounded",expected,round);
        test = "-75.9270";
        round = MathUtils.round(test, 2);
        expected = "-75.93";
        assertEquals("5. not properly rounded",expected,round);
        test = "-110.2895";
        round = MathUtils.round(test, 2);
        expected = "-110.29";
        assertEquals("6. not properly rounded",expected,round);
        test = "-124.3186";
        round = MathUtils.round(test, 2);
        expected = "-124.32";
        assertEquals("7. not properly rounded",expected,round);
        test = "-75.7861";
        round = MathUtils.round(test, 2);
        expected = "-75.79";
        assertEquals("8. not properly rounded",expected,round);
        test = "-75.7861";
        round = MathUtils.round(test, 0);
        expected = "-76";
        assertEquals("9. not properly rounded",expected,round);
        /* scientific notation is returned when scale<0 */
        test = "-75.7861";
        round = MathUtils.round(test, -1);
        expected = "-8E+1";
        assertEquals("10. not properly rounded",expected,round);
        test = "-75.7861";
        round = MathUtils.round(test, -2);
        expected = "-1E+2";
        assertEquals("111. not properly rounded",expected,round);
    }
    @Test(expected=NumberFormatException.class)
    public final void testRoundEmpty(){
        String test = "";
        MathUtils.round(test, 2);
    }
    @Test(expected=NumberFormatException.class)
    public final void testRoundNaN(){
        String test = "foo";
        MathUtils.round(test, 2);
    }
    @Test(expected=NullPointerException.class)
    public final void testRoundNull(){
        String test = null;
        MathUtils.round(test, 2);
    }
    @Test
    public final void testRoundBigDecimal() {
        BigDecimal test = new BigDecimal("1.234");
        BigDecimal round = MathUtils.round(test, 2);
        BigDecimal expected = new BigDecimal("1.23");
        assertEquals("1. not properly rounded",expected,round);
        test = new BigDecimal("1.236");
        round = MathUtils.round(test, 2);
        expected = new BigDecimal("1.24");
        assertEquals("2. not properly rounded",expected,round);
        test = new BigDecimal("31.5907");
        round = MathUtils.round(test, 2);
        expected = new BigDecimal("31.59");
        assertEquals("3.1 not properly rounded",expected,round);
        test = new BigDecimal("1.235");
        round = MathUtils.round(test, 2);
        expected = new BigDecimal("1.24");
        assertEquals("3.2 not properly rounded",expected,round);
        test = new BigDecimal("-1.235");
        round = MathUtils.round(test, 2);
        expected = new BigDecimal("-1.23");
        assertEquals("3.3 not properly rounded",expected,round);
        test = new BigDecimal("33.57725");
        round = MathUtils.round(test, 2);
        expected = new BigDecimal("33.58");
        assertEquals("3.4 not properly rounded",expected,round);
        test = new BigDecimal("-101.5950");
        round = MathUtils.round(test, 2);
        expected = new BigDecimal("-101.59");
        assertEquals("4. not properly rounded",expected,round);
        test = new BigDecimal("-75.9270");
        round = MathUtils.round(test, 2);
        expected = new BigDecimal("-75.93");
        assertEquals("5. not properly rounded",expected,round);
        test = new BigDecimal("-110.2895");
        round = MathUtils.round(test, 2);
        expected = new BigDecimal("-110.29");
        assertEquals("6. not properly rounded",expected,round);
        test = new BigDecimal("-124.3186");
        round = MathUtils.round(test, 2);
        expected = new BigDecimal("-124.32");
        assertEquals("7. not properly rounded",expected,round);
        test = new BigDecimal("-75.7861");
        round = MathUtils.round(test, 2);
        expected = new BigDecimal("-75.79");
        assertEquals("8. not properly rounded",expected,round);
        test = new BigDecimal("-75.7861");
        round = MathUtils.round(test, 0);
        expected = new BigDecimal("-76");
        assertEquals("9. not properly rounded",expected,round);
        /* scientific notation is returned when scale<0 */
        test = new BigDecimal("-75.7861");
        round = MathUtils.round(test, -1);
        expected = new BigDecimal("-8E+1");
        assertEquals("10. not properly rounded",expected,round);
        test = new BigDecimal("-75.7861");
        round = MathUtils.round(test, -2);
        expected = new BigDecimal("-1E+2");
        assertEquals("111. not properly rounded",expected,round);
    }
    @Test(expected=NullPointerException.class)
    public final void testRoundNullBigDecimal(){
        BigDecimal test = null;
        MathUtils.round(test, 2);
    }
    @Test
    public final void testScaleToInt() {
        String test = "1.234";
        String expected = "123";
        double scale = 100;
        String scaled = MathUtils.scaleToInt(test, scale);
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        scale = 10;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "12";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "1.236";
        scale = 100;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "124";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "31.5907";
        scale = 100;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "3159";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        scale = 10;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "316";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        scale = 0.1;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "3";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "1.235";
        scale = 100;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "124";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "-1.235";
        scale = 100;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "-123";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "33.57725";
        scale = 100;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "3358";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "-101.5950";
        scale = 100;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "-10159";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "-75.9270";
        scale = 100;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "-7593";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "-110.2895";
        scale = 100;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "-11029";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "-124.3186";
        scale = 100;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "-12432";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "-75.7865";
        scale = 100;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "-7579";
        scale = 100;
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        scale = 1000;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "-75786";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);

        test = "-75.7861";
        scale = 1;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "-76";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "-75.7861";
        scale = 0.1;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "-8";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);

        test = "-75.7861";
        scale = 0.01;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "-1";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
        test = "-75.7861";
        scale = 0.001;
        scaled = MathUtils.scaleToInt(test, scale);
        expected = "0";
        assertEquals(String.format("scaleToInt(%1$s,%2$s) not properly scaled",
        		test,scale),expected,scaled);
    }
    @Test(expected=NumberFormatException.class)
    public final void testScaleToIntEmpty(){
        String test = "";
        MathUtils.scaleToInt(test, 100);
    }
    @Test(expected=NumberFormatException.class)
    public final void testScaleToIntNaN(){
        String test = "foo";
        MathUtils.scaleToInt(test, 100);
    }
    @Test(expected=NullPointerException.class)
    public final void testScaleToIntNull(){
        String test = null;
        MathUtils.scaleToInt(test, 100);
    }
    @Test(expected=IllegalArgumentException.class)
    public final void testScaleToIntIllegalScale() {
        String test = "-75.7861";
        MathUtils.scaleToInt(test, -10);
    }

    @Test
    public final void testScaleFromInt() {
        Integer test = 123;
        String expected = "1.23";
        String scaled = MathUtils.scaleFromInt(test, 100);
        assertEquals("1.1 not properly scaled",expected,scaled);
        scaled = MathUtils.scaleFromInt(test, 10);
        expected = "12.3";
        assertEquals("1.2 not properly scaled",expected,scaled);
        test = 3159;
        scaled = MathUtils.scaleFromInt(test, 100);
        expected = "31.59";
        assertEquals("3.1 not properly scaled",expected,scaled);
        scaled = MathUtils.scaleFromInt(test, 10);
        expected = "315.9";
        assertEquals("3.2 not properly scaled",expected,scaled);
        test = -124;
        scaled = MathUtils.scaleFromInt(test, 100);
        expected = "-1.24";
        assertEquals("3.5 not properly scaled",expected,scaled);
        test = 3358;
        scaled = MathUtils.scaleFromInt(test, 100);
        expected = "33.58";
        assertEquals("3.6 not properly scaled",expected,scaled);
        test = -10160;
        scaled = MathUtils.scaleFromInt(test, 100);
        expected = "-101.60";
        assertEquals("4. not properly scaled",expected,scaled);
        test = -7593;
        scaled = MathUtils.scaleFromInt(test, 100);
        expected = "-75.93";
        assertEquals("5. not properly scaled",expected,scaled);
        test = -11029;
        scaled = MathUtils.scaleFromInt(test, 100);
        expected = "-110.29";
        assertEquals("6. not properly scaled",expected,scaled);
    }
    @Test(expected=NullPointerException.class)
    public final void testScaleFromIntNull(){
        Integer test = null;
        MathUtils.scaleFromInt(test, 100);
    }
    @Test(expected=IllegalArgumentException.class)
    public final void testScaleFromIntIllegalScale() {
        Integer test = -75786;
        MathUtils.scaleFromInt(test, 4);
    }
    @Test
    public final void testBitSetToUnsignedInt() {
    	BitSet bitset = new BitSet();
    	bitset.set(0);
    	int val = MathUtils.bitSetToUnsignedInt(bitset);
    	int expected = 1;
    	assertEquals(expected, val);
    	bitset.set(2);
    	val = MathUtils.bitSetToUnsignedInt(bitset);
    	expected = 5;
    	assertEquals(expected, val);
    	bitset.clear();
    	val = MathUtils.bitSetToUnsignedInt(bitset);
    	expected = 0;
    	assertEquals(expected, val);    	
    }
    @Test
    public final void testUnsignedIntToBitSet(){
    	BitSet expected = new BitSet();
    	int value = 0;
    	BitSet result = MathUtils.unsignedIntToBitSet(value);
    	assertEquals(expected.get(0, expected.length()),result.get(0, expected.length()));
    	expected.set(0);
    	value = 1;
    	assertEquals(expected,MathUtils.unsignedIntToBitSet(value));
    	expected.set(2);
    	value=5;
    	assertEquals(expected,MathUtils.unsignedIntToBitSet(value));
    }
    @Test
    public final void testDivide() {
    	BigDecimal result = 
    			MathUtils.divide(new BigDecimal("4"), new BigDecimal("3"), 2);
    	assertEquals("expected 4/3=1.33",new BigDecimal("1.33"),result);
    }
    @Test(expected = IllegalArgumentException.class)
    public final void testDivideByZero() {
    	// dividing by zero
    	MathUtils.divide(new BigDecimal("4"), BigDecimal.ZERO, 2);
    }
    @Test(expected = NullPointerException.class)
    public final void testDivideByNull() {
    	// dividing by null
    	MathUtils.divide(new BigDecimal("4"), null, 2);
    }
    @Test(expected = NullPointerException.class)
    public final void testDivideNullNumerator() {
    	// dividing null numerator
    	MathUtils.divide(null, new BigDecimal("4"), 2);
    }
    @Test
    public final void testAverage() {
    	List<BigDecimal> list = new ArrayList<>();
    	list.add(BigDecimal.ZERO);
    	list.add(BigDecimal.ONE);
    	list.add(new BigDecimal("2.46"));
    	assertEquals("expected 1.15 when rounded",new BigDecimal("1.15"),
    			MathUtils.average(list, 2));
    	
    	// divisor is zero
    	list.clear();
    	assertNull("expected null from empty list",MathUtils.average(list, 2));
    	
    	// all nulls
    	list.clear();
    	list.add(null);
    	list.add(null);
    	assertNull("expected null from list of nulls",MathUtils.average(list, 2));
    	
    	// test correct rounding
    	list.clear();
    	list.add(BigDecimal.ZERO);
    	list.add(BigDecimal.ONE);
    	assertEquals("expected ONE when rounded up", BigDecimal.ONE,
    			MathUtils.average(list, 0));
    	list.clear();
    	list.add(BigDecimal.ZERO);
    	list.add(new BigDecimal("-1"));
    	assertEquals("expected ZERO when rounded toward positive",
    			BigDecimal.ZERO, MathUtils.average(list, 0));
    }
    @Test(expected=NullPointerException.class)
    public final void testAverageNullArg() {
    	// list is null
    	MathUtils.average(null, 2);
    }
    @Test
    public final void testAverage2() {
    	BigDecimal a = new BigDecimal("1.00");
    	a.setScale(2);
    	BigDecimal b = new BigDecimal("0.00");
    	b.setScale(2);
    	BigDecimal c = new BigDecimal("2.46");
    	c.setScale(2);
    	List<BigDecimal> list = Lists.newArrayList(a,b,c);
    	BigDecimal expected = new BigDecimal("1.15");
    	BigDecimal result = MathUtils.average(list,2);
    	assertEquals("expected 1.15 when rounded", expected, result);
    	
    	// rounds to minimum precision of members
    	BigDecimal d = new BigDecimal("4.70");
    	d.setScale(1);
    	list = Lists.newArrayList(a,b,c,d);
    	expected = new BigDecimal("2.0");
    	result = MathUtils.average(list,1);
    	assertEquals("expected less precision when less precision requested",
    			expected, result);
    	
    	// rounds to minimum precision of members
    	BigDecimal e = new BigDecimal("4.70");
    	e.setScale(2);
    	// replace previous d with newly-scaled version
    	list = Lists.newArrayList(a,b,c,e);
    	expected = new BigDecimal("2.04");
    	result = MathUtils.average(list,2);
    	assertEquals("expected more precision when more precision requested",
    			expected, result);
    	
    }
    @Test
    public final void testAVERAGE_100TH() {
    	BigDecimal a = new BigDecimal("1.00");
    	BigDecimal b = new BigDecimal("0.00");
    	BigDecimal c = new BigDecimal("2.46");
    	List<BigDecimal> list = Lists.newArrayList(a,b,c);
    	BigDecimal expected = new BigDecimal("1.15");
    	BigDecimal result = MathUtils.AVERAGE_100TH.apply(list);
    	assertEquals("expected 1.15 when rounded to 100ths", expected, result);
    }
    @Test
    public final void testAVERAGE_10TH() {
    	BigDecimal a = new BigDecimal("1.00");
    	a.setScale(2);
    	BigDecimal b = new BigDecimal("0.00");
    	b.setScale(2);
    	BigDecimal c = new BigDecimal("2.46");
    	c.setScale(2);
    	List<BigDecimal> list = Lists.newArrayList(a,b,c);
    	BigDecimal expected = new BigDecimal("1.2");
    	BigDecimal result = MathUtils.AVERAGE_10TH.apply(list);
    	assertEquals("expected 1.2 when rounded to 10ths", expected, result);

    	// not using BigDecimal.ONE so I can verify scale
    	a = new BigDecimal("1");
    	a.setScale(0);
    	b = new BigDecimal("0");
    	b.setScale(0);
    	list = Lists.newArrayList(BigDecimal.ONE,BigDecimal.ZERO,c);
    	expected = new BigDecimal("1.2");
    	result = MathUtils.AVERAGE_10TH.apply(list);
    	assertEquals("expected 1.2 when rounded to 10ths, even if members have lesser precision", expected, result);
    }
    @Test
    public final void testSUM() {
    	List<BigDecimal> list = Lists.newArrayList(
    			BigDecimal.ZERO,
    			BigDecimal.ONE,
    			new BigDecimal("2.45"));
    	BigDecimal expected = new BigDecimal("3.45");
    	BigDecimal result = MathUtils.SUM.apply(list);
    	assertEquals("incorrect sum",expected,result);
    }
    @Test
    public final void testSum() {
    	List<BigDecimal> list = new ArrayList<BigDecimal>();
    	list.add(BigDecimal.ZERO);
    	list.add(BigDecimal.ONE);
    	list.add(new BigDecimal("2.45"));
    	BigDecimal expected = new BigDecimal("3.45");
    	BigDecimal result = MathUtils.sum(list);
    	assertEquals("incorrect sum",expected,result);
    	
    	// neg numbers
    	list.add(new BigDecimal("-2.49"));
    	expected=new BigDecimal("0.96");
    	result = MathUtils.sum(list);

    	// nulls
    	list.add(null);
    	result = MathUtils.sum(list);
    	// expect same result
    	assertEquals("wrong result adding a null",expected,result);
    	
    	// all nulls
    	list.clear();
    	list.add(null);
    	list.add(null);
    	expected = BigDecimal.ZERO;
    	result = MathUtils.sum(list);
    	assertEquals("incorrect sum of nulls",expected,result);
    	
    	// empty list
    	list.clear();
    	expected = BigDecimal.ZERO;
    	result = MathUtils.sum(list);
    	assertEquals("incorrect sum of empty list",expected,result);
    }
    @Test(expected=NullPointerException.class)
    public final void testSumNullArg() {
    	// list is null
    	MathUtils.sum(null);
    }
    @Test
    public final void testMinimum() {
    	List<BigDecimal> list = new ArrayList<BigDecimal>();
    	list.add(BigDecimal.ZERO);
    	list.add(BigDecimal.ONE);
    	list.add(new BigDecimal("2.45"));
    	BigDecimal expected = BigDecimal.ZERO;
    	BigDecimal result = MathUtils.minimum(list);
    	
    	// neg numbers
    	list.add(new BigDecimal("-2.49"));
    	expected=new BigDecimal("-2.49");
    	result = MathUtils.minimum(list);
    	assertEquals("wrong result adding a negative",expected,result);
    	
    	// nulls
    	list.add(null);
    	result = MathUtils.minimum(list);
    	// expect same result
    	assertEquals("wrong result adding a null",expected,result);
    	
    	// all nulls
    	list.clear();
    	list.add(null);
    	list.add(null);
    	result = MathUtils.minimum(list);
    	assertNull("expected null all nulls",result);
    	
    	// empty list
    	list.clear();
    	result = MathUtils.minimum(list);
    	assertNull("expected null empty list",result);
    }
    @Test(expected=NullPointerException.class)
    public final void testMinimumNullArg() {
    	// list is null
    	MathUtils.minimum(null);
    }
    @Test
    public final void testMaximum() {
    	List<BigDecimal> list = new ArrayList<BigDecimal>();
    	list.add(BigDecimal.ZERO);
    	list.add(BigDecimal.ONE);
    	list.add(new BigDecimal("2.45"));
    	BigDecimal expected = new BigDecimal("2.45");
    	BigDecimal result = MathUtils.maximum(list);
    	assertEquals("wrong result all pos #s",expected,result);
    	
    	// neg numbers
    	list.add(new BigDecimal("-2.49"));
    	result = MathUtils.maximum(list);
    	// expect same result
    	assertEquals("wrong result adding a negative",expected,result);
    	
    	// nulls
    	list.add(null);
    	result = MathUtils.maximum(list);
    	// expect same result
    	assertEquals("wrong result adding a null",expected,result);
    	
    	// all nulls
    	list.clear();
    	list.add(null);
    	list.add(null);
    	result = MathUtils.maximum(list);
    	assertNull("expected null all nulls",result);
    	
    	// empty list
    	list.clear();
    	result = MathUtils.maximum(list);
    	assertNull("expected null empty list",result);
    }
    @Test(expected=NullPointerException.class)
    public final void testMaximumNullArg() {
    	// list is null
    	MathUtils.maximum(null);
    }
}
