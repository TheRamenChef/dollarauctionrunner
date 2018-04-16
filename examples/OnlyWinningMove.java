import net.ramenchef.dollarauction.DollarBidder;

public class OnlyWinningMove extends DollarBidder {
	@Override
	public int nextBid(int opponentsBid) {
		return 0;
	}
}
