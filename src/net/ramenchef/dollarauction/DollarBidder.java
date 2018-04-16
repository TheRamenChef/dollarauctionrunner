package net.ramenchef.dollarauction;

public abstract class DollarBidder {
	int score;
	
	/**
	 * (Optional) Perform whatever resets you need to prepare for the next auction.
	 * 
	 * @param opponent The class of the other bot in this round
	 */
	public void newAuction(Class<? extends DollarBidder> opponent) {}
	
	/**
	 * Return how much to bid in this round. Bidding ends if this value
	 * is not enough to top the previous bid or if both bids exceed $100.
	 * 
	 * @param opponentsBid how much the other bot bid in the previous round
	 * 
	 * @return how much to bid in this round
	 */
	public abstract int nextBid(int opponentsBid);
}