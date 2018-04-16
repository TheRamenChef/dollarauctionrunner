import net.ramenchef.dollarauction.DollarBidder;

public class Analyst extends DollarBidder {
	private DollarBidder enemy;
	
	@Override
	public void newAuction(Class<? extends DollarBidder> opponent) {
		try {
			this.enemy = opponent.newInstance();
			enemy.newAuction(this.getClass());
		} catch (ReflectiveOperationException e) {
			this.enemy = null;
		}
	}
	
	@Override
	public int nextBid(int opponentsBid) {
		return enemy != null && enemy.nextBid(95) < 100 ? 95 : 0;
	}
}