package session7.exercise;

import java.math.BigDecimal;
import java.util.Set;

import Modle.Storegoods2;

public interface DataBaseImpl {
	
	public Set<Goods> queryGoods();
	
	public int createGoods(Goods goods);
	
	public boolean updateGoods(Goods goods);
	
	public boolean deleteGoods(BigDecimal goodsID);
	
	public Set<Order> queryOrderBetweenDate(String queryStartDate, String queryEndDate);

}
