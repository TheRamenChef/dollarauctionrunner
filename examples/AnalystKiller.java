import net.ramenchef.dollarauction.DollarBidder;

public class AnalystKiller extends DollarBidder {
	private static int instances = 0;
	private final boolean tainted;
	
	public AnalystKiller() {
		this.tainted = instances++ > 0;
	}
	
	@Override
	public int nextBid(int opponentsBid) {
		if (tainted)
			throw new RuntimeException("A mysterious error occurred! >:)");
		
		return 5;
	}
}