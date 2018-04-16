import net.ramenchef.dollarauction.DollarBidder;

public class GreedyBidder extends DollarBidder {
	@Override
	public int nextBid(int opponentsBid) {
		return opponentsBid + 5;
	}
}