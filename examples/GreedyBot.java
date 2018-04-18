import net.ramenchef.dollarauction.DollarBidder;

public class GreedyBot extends DollarBidder {
	@Override
	public int nextBid(int opponentsBid) {
		return opponentsBid + 5;
	}
}
