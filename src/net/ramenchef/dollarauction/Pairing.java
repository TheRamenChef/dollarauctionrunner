package net.ramenchef.dollarauction;

class Pairing {
	public final DollarBidder bidder1;
	public final DollarBidder bidder2;
	
	public Pairing(DollarBidder b1, DollarBidder b2) {
		this.bidder1 = b1;
		this.bidder2 = b2;
	}
}
