package session7.exercise;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import DBConnection.DBConnectionFactory;

public class FrontEndDao {
	
	private static FrontEndDao backendDao = new FrontEndDao();
	
	private FrontEndDao(){ }

	public static FrontEndDao getInstance(){
		return backendDao;
	}
	
	/**
	 * 前臺顧客登入查詢
	 * @param identificationNo
	 * @return Member
	 */
	public Member queryMemberByIdentificationNo(String identificationNo){
		Member meb=null;
		String SQL="SELECT CUSTOMER_NAME,PASSWORD,IDENTIFICATION_NO FROM BEVERAGE_MEMBER WHERE IDENTIFICATION_NO = ? ";	
			try (Connection conn = DBConnectionFactory.getLocalDBConnection();
				PreparedStatement st = conn.prepareStatement(SQL);){
				st.setString(1, identificationNo);
				ResultSet rs = st.executeQuery();
				
				while(rs.next()) {
					meb = new Member();
					meb.setCustomerName(rs.getNString("CUSTOMER_NAME"));
					meb.setPassword(rs.getNString("PASSWORD"));
					meb.setIdentificationNo(rs.getNString("IDENTIFICATION_NO"));
					
				}
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		
		return meb;
	}
	
	/**
	 * 前臺顧客瀏灠商品
	 * @param searchKeyword
	 * @param startRowNo
	 * @param endRowNo
	 * @return Set(Goods)
	 */
	public Set<Goods> searchGoods(String searchKeyword, int startRowNo, int endRowNo) {
		Set<Goods> goods = new LinkedHashSet<>();
		String SQL = "SELECT * FROM ( " + "SELECT  rownum row_num,e.* " + "from BEVERAGE_GOODS e "
				+ "WHERE  LOWER(GOODS_NAME) LIKE ? ) f " + "where f.row_num > ? AND f.row_num < ? ";
		try (Connection conn = DBConnectionFactory.getLocalDBConnection();
				PreparedStatement st = conn.prepareStatement(SQL);) {
			st.setString(1, "%" + searchKeyword + "%");
			st.setInt(2, startRowNo);
			st.setInt(3, endRowNo);

			try (ResultSet rs = st.executeQuery()) {
				while (rs.next()) {
					Goods gd = new Goods();
					gd.setGoodsID(rs.getBigDecimal("GOODS_ID"));
					gd.setGoodsName(rs.getString("GOODS_NAME"));
					gd.setGoodsPrice(rs.getInt("PRICE"));
					gd.setGoodsQuantity(rs.getInt("QUANTITY"));
					gd.setGoodsImageName(rs.getString("IMAGE_NAME"));
					gd.setStatus(rs.getString("STATUS"));

					goods.add(gd);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return goods;
	}

	/**
	 * 查詢顧客所購買商品資料(價格、庫存)
	 * @param goodsIDs
	 * @return Map(BigDecimal, Goods)
	 */
	public Map<BigDecimal, Goods> queryBuyGoods(Set<BigDecimal> goodsIDs) {
		// key:商品編號、value:商品
		Map<BigDecimal, Goods> goods = new LinkedHashMap<>();
		String SQL = "select * from BEVERAGE_GOODS where GOODS_ID =?  ";
		try (Connection conn = DBConnectionFactory.getLocalDBConnection();
				PreparedStatement st = conn.prepareStatement(SQL);) {
			
			for(BigDecimal goodsid: goodsIDs) {
				
			st.setBigDecimal(1, goodsid);
			try (ResultSet rs = st.executeQuery()) {
			
				while (rs.next()) {
					Goods gs=new Goods();
					gs.setGoodsID(rs.getBigDecimal("GOODS_ID"));
					gs.setGoodsName(rs.getString("GOODS_NAME"));
					gs.setGoodsPrice(rs.getInt("PRICE"));
					gs.setGoodsQuantity(rs.getInt("QUANTITY"));
					gs.setGoodsImageName(rs.getString("IMAGE_NAME"));
					gs.setStatus(rs.getString("STATUS"));
					
					
					//set 才可以用add 這裡是map所以使用put
					goods.put(goodsid, gs);
					
				}
				
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return goods;
	}
	
	/**
	 * 交易完成更新扣商品庫存數量
	 * @param goods
	 * @return boolean
	 */
	public boolean batchUpdateGoodsQuantity(Set<Goods> goods){
		boolean updateSuccess = false;
		String SQL="update BEVERAGE_GOODS  "
				+ "set QUANTITY = ? "
				+ "where GOODS_ID = ? ";
		try (Connection conn = DBConnectionFactory.getLocalDBConnection();
				PreparedStatement st = conn.prepareStatement(SQL);) {
			
			for (Goods g : goods) { 

				st.setInt(1, g.getGoodsQuantity());
				st.setBigDecimal(2, g.getGoodsID());
			
				

				int valueupdate = st.executeUpdate();
				if (valueupdate != 0) {
					updateSuccess = true;
					
				}
				
			}
				conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
		return updateSuccess;
	}
	
	/**
	 * 建立訂單資料
	 * @param customerID
	 * @param goodsOrders【訂單資料(key:購買商品、value:購買數量)】
	 * @return boolean
	 *///外部給資料
	public boolean batchCreateGoodsOrder(String customerID, Map<Goods, Integer> goodsOrders) {
		boolean insertSuccess = false;

		String SQL = "INSERT INTO BEVERAGE_ORDER(ORDER_ID,ORDER_DATE,CUSTOMER_ID,GOODS_ID,GOODS_BUY_PRICE,BUY_QUANTITY) "
				+ "VALUES (BEVERAGE_ORDER_SEQ.NEXTVAL,SYSDATE,?,?,?,?) ";
		try (Connection conn = DBConnectionFactory.getLocalDBConnection()) {//連線
			conn.setAutoCommit(false);// 手動commit 關掉
			try (PreparedStatement st = conn.prepareStatement(SQL);) {//讀取SQL
				
				for (Goods g : goodsOrders.keySet()) { //keySet=set<Goods>

					st.setString(1, customerID);
					st.setBigDecimal(2, g.getGoodsID());
					st.setInt(3, g.getGoodsPrice());
					st.setInt(4, g.getGoodsQuantity());

					st.addBatch();// 多筆一起存取資料

				}
				int[] valueupdate = st.executeBatch();
				if (valueupdate.length != 0) {  //valueupdate != null
					insertSuccess = true;
					conn.commit();
				}

			} catch (SQLException e) {
				conn.rollback();
				e.printStackTrace();

			}

		} catch (SQLException e) {

			e.printStackTrace();

		}

		return insertSuccess;
	}

}
