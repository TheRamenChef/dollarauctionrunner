package net.ramenchef.dollarauction;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class DollarAuction {
	static {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
	}
	
	public static void main(String[] args) {
		List<DollarBidder> bidderList = new ArrayList<>(args.length);
		for (int i = 0; i < args.length; i++) {
			String name = args[i];
			
			Class<? extends DollarBidder> bidderClass;
			try {
				bidderClass = Class.forName(name).asSubclass(DollarBidder.class);
			} catch (ClassNotFoundException e) {
				System.err.printf("Could not find a class file for bot %1$s. Skipping.%n", name);
				continue;
			} catch (ExceptionInInitializerError e) {
				System.err.printf("Could not load class %1$s. Skipping.%n", name);
				e.getCause().printStackTrace();
				continue;
			} catch (ClassCastException e) {
				System.err.printf("Bot %1$s does not implement DollarBidder. Skipping.%n", name);
				continue;
			}
			
			Constructor<? extends DollarBidder> constructor;
			try {
				constructor = bidderClass.getConstructor();
			} catch (NoSuchMethodException e) {
				System.err.printf("Bot %1$s does not have a valid constructor. Skipping.%n", name);
				continue;
			}
			
			try {
				bidderList.add(constructor.newInstance());
			} catch (InstantiationException e) {
				System.err.printf("Bot %1$s could not be instantiated. Skipping.%n", name);
			} catch (IllegalAccessException e) {
				System.err.printf("Bot %1$s does not have a public constructor. Skipping.%n", name);
			} catch (InvocationTargetException e) {
				System.err.printf("Bot %1$s failed to construct. Skipping.%n", name);
				e.getCause().printStackTrace();
			}
		}
		
		DollarBidder[] bidders = bidderList.toArray(new DollarBidder[bidderList.size()]);
		Pairing[] pairings = new Pairing[bidders.length * (bidders.length - 1)];
		int index = 0;
		for (int i = 0; i < bidders.length; i++)
			for (int j = i + 1; j < bidders.length; j++) {
				pairings[index++] = new Pairing(bidders[i], bidders[j]);
				pairings[index++] = new Pairing(bidders[j], bidders[i]);
			}
		Random rand = new SecureRandom();
		for (int i = pairings.length - 1; i > 0; i--) {
			int ind = rand.nextInt(i + 1);
			Pairing temp = pairings[i];
			pairings[i] = pairings[ind];
			pairings[ind] = temp;
		}
		
		for (int i = 1; i <= pairings.length; i++) {
			Pairing pairing = pairings[i - 1];
			String name1 = pairing.bidder1.getClass().getName();
			String name2 = pairing.bidder2.getClass().getName();
			
			try {
				pairing.bidder1.newAuction(pairing.bidder2.getClass());
			} catch (Throwable t) {
				System.err.printf("Bot %1$s could not prepare for the auction.%n", pairing.bidder1.getClass().getName());
				t.printStackTrace();
			}
			try {
				pairing.bidder2.newAuction(pairing.bidder1.getClass());
			} catch (Throwable t) {
				System.err.printf("Bot %1$s could not prepare for the auction.%n", pairing.bidder2.getClass().getName());
				t.printStackTrace();
			}
			
			long bid1 = 0, bid2 = 0;
			while (true) {
				int next;
				try {
					next = pairing.bidder1.nextBid(bid2);
				} catch (Throwable t) {
					System.err.printf("Bot %1$s threw an exception while determining its bid.%n", name1);
					t.printStackTrace();
					System.out.printf("Round %1$d: %2$s vs. %3$s: %3$s wins by exception.%n", i, name1, name2);
					pairing.bidder1.score -= bid1;
					pairing.bidder2.score += 100;
					break;
				}
				if (next < bid2 + 5) {
					System.out.printf("Round %1$d: %2$s vs. %3$s: %2$s stopped bidding. %3$s wins.%n", i, name1, name2);
					pairing.bidder1.score -= bid1;
					if (bid2 > 0) {
						pairing.bidder2.score -= bid2;
						pairing.bidder2.score += 100;
					}
					break;
				}
				if (next > 10000 && bid2 > 10000) {
					System.out.printf("Round %1$d: %2$s vs. %3$s: Bidding spiraled out of control. Both bots lose.%n", i, name1, name2);
					pairing.bidder1.score -= 10000;
					pairing.bidder2.score -= 10000;
					break;
				}
				bid1 = next;
				
				try {
					next = pairing.bidder2.nextBid(bid1);
				} catch (Throwable t) {
					System.err.printf("Bot %1$s threw an exception while determining its bid.%n", name2);
					t.printStackTrace();
					System.out.printf("Round %1$d: %2$s vs. %3$s: %2$s wins by exception.%n", i, name1, name2);
					pairing.bidder2.score -= bid2;
					pairing.bidder1.score += 100;
					break;
				}
				if (next < bid1 + 5) {
					System.out.printf("Round %1$d: %2$s vs. %3$s: %3$s stopped bidding. %2$s wins.%n", i, name1, name2);
					pairing.bidder2.score -= bid2;
					pairing.bidder1.score -= bid1;
					pairing.bidder1.score += 100;
					break;
				}
				if (next > 10000 && bid1 > 10000) {
					System.out.printf("Round %1$d: %2$s vs. %3$s: Bidding spiraled out of control. Both bots lose.%n", i, name1, name2);
					pairing.bidder1.score -= 10000;
					pairing.bidder2.score -= 10000;
					break;
				}
				bid2 = next;
			}
		}
		
		System.out.println();
		
		Arrays.sort(bidders, Comparator.comparingDouble(bidder -> -bidder.score));
		System.out.println("Scoreboard:");
		for (int i = 0; i < bidders.length; i++)
			System.out.printf("%1$s: $%2$.2f%n", bidders[i].getClass().getName(), bidders[i].score * 0.01);
	}
}
