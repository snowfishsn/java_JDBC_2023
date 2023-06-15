package session7.exercise;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface FrontDataBaselmpl {
	
	
	
	public Member queryMemberByIdentificationNo(String identificationNo);

	public Set<Goods> searchGoods(String searchKeyword, int startRowNo, int endRowNo);
	
	public Map<BigDecimal, Goods> queryBuyGoods(Set<BigDecimal> goodsIDs);
	
	public boolean batchUpdateGoodsQuantity(Set<Goods> goods);
	
	public boolean batchCreateGoodsOrder(String customerID, Map<Goods,Integer> goodsOrders);
}
