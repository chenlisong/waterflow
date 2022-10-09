package com.example.demo;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UtilTest {

	public void fibTest() {

//		printHi();
//
//		int n = 40;
//		long startLine = System.currentTimeMillis();
//		logger.info("execute fib {}, result is {}, cost time is: {}", n, fib(n), (startLine=System.currentTimeMillis()) - startLine);
//
//		logger.info("execute lookupfib {}, result is {}, cost time is: {}", n, lookupFib(n), (startLine=System.currentTimeMillis()) - startLine);
//
//		logger.info("execute fib with loop {}, result is {}, cost time is: {}", n, fibWithloop(n), (startLine=System.currentTimeMillis()) - startLine);

		streamOps();

	}

	private static Map<Integer, BigInteger> loookupTable = new HashMap<Integer, BigInteger>();

	static {
		loookupTable.put(0, BigInteger.ZERO);
		loookupTable.put(1, BigInteger.ONE);
	}

	//朴素方式 - 递归
	private static BigInteger fib(int n) {
		if(n == 0) {
			return BigInteger.ZERO;
		}else if(n == 1) {
			return BigInteger.ONE;
		}
		return fib(n - 1).add(fib(n - 2));
	}

	//记忆法 - 递归
	private static BigInteger lookupFib(int n) {
		if (loookupTable.containsKey(n)) {
			return loookupTable.get(n);
		}
		BigInteger result = lookupFib(n - 1).add(lookupFib(n - 2));
		loookupTable.put(n, result);
		return result;
	}

	//非递归
	private static int fibWithloop(int n) {
		if(n <= 1) {
			return n;
		}

		int result = 0, left = 0;
		int index = 1, right = 1;
		while(n >= index++) {
			result += left;
			left = right; right = result;

			if(n == index) {
				result += right;
			}
		}
		return result;
	}

	private static void printHi() {
		new Thread(() -> System.out.println("hello world.")).start();
	}

	private static void streamOps() {
		Stream.of(1,2,3).map(v -> v + 1).flatMap(v -> Stream.of(v*5, v*10)).map(v -> v+",").forEach(System.out::print);
		System.out.println();

		Stream.of(1,2,3).reduce((v1, v2) -> v1 + v2).map(v -> v + ",").ifPresent(System.out::println);

		Object result = Stream.of(1,2,3,4,5).reduce(1, (v1, v2) -> v1 * v2);
		System.out.println(result);

		result = Stream.of(1,2,3,4,5).reduce(0, (v1, v2) -> v1 + v2, (v1,v2) -> v1 + v2);
		System.out.println(result);

		System.out.println(Stream.of("Alex", "Bob", "David", "Amy").collect(Collectors.groupingBy(v -> v.charAt(0))));

        System.out.println(Stream.of("a", "b", "c").collect(Collectors.joining(", ")));

        System.out.println(Stream.of("hello", "world", "a").collect(Collectors.averagingInt(String::length)));

        IntSummaryStatistics statistics = Stream.of("hello", "world", "a").collect(Collectors.summarizingInt(String::length));
        System.out.println(statistics);

        Function f = (v) -> v + "";
	}
}

