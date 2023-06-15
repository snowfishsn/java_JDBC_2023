package session7.exercise;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashSet;
import java.util.Set;

import DBConnection.DBConnectionFactory;

public class BackEndDao implements DataBaseImpl {

	private static BackEndDao backendDao = new BackEndDao();

	private BackEndDao() {
	}

	public static BackEndDao getInstance() {
		return backendDao;
	}

	/**
	 * 後臺管理商品列表
	 * 
	 * @return Set(Goods)
	 */
	public Set<Goods> queryGoods() {
		Set<Goods> goods = new LinkedHashSet<>();
		String querySQL = "SELECT * FROM BEVERAGE_GOODS ";
		try (Connection conn = DBConnectionFactory.getLocalDBConnection();
				PreparedStatement st = conn.prepareStatement(querySQL)) {
			ResultSet rs = st.executeQuery(querySQL);

			while (rs.next()) {
				Goods good = new Goods();
				good.setGoodsID(rs.getBigDecimal("GOODS_ID"));
				good.setGoodsName(rs.getString("GOODS_NAME"));
				good.setGoodsPrice(rs.getInt("PRICE"));
				good.setGoodsQuantity(rs.getInt("QUANTITY"));
				good.setGoodsImageName(rs.getString("IMAGE_NAME"));
				good.setStatus(rs.getString("STATUS"));

				goods.add(good);
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return goods;
	}

	/**
	 * 後臺管理新增商品
	 * 
	 * @param goods
	 * @return int(商品編號)
	 */
	public int createGoods(Goods goods) {
		int goodsID = 0;
		String[] cols = { "GOODS_ID" };
		try (Connection conn = DBConnectionFactory.getLocalDBConnection()) {
			conn.setAutoCommit(false);
			String insertSQL = "INSERT INTO BEVERAGE_GOODS VALUES (BEVERAGE_GOODS_SEQ.NEXTVAL, ?, ?, ?, ?, ?)";

			try (PreparedStatement ps = conn.prepareStatement(insertSQL, cols)) {
				ps.setString(1, goods.getGoodsName());
				ps.setInt(2, goods.getGoodsPrice());
				ps.setInt(3, goods.getGoodsQuantity());
				ps.setString(4, goods.getGoodsImageName());
				ps.setString(5, goods.getStatus());

				ps.executeUpdate();

				ResultSet rsKeys = ps.getGeneratedKeys();
				rsKeys.next();
				goodsID = rsKeys.getInt(1);

				conn.commit();

			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return goodsID;
	}

	/**
	 * 後臺管理更新商品
	 * 
	 * @param goods
	 * @return boolean
	 */
	public boolean updateGoods(Goods goods) {
		boolean updateSuccess = false;

		try (Connection conn = DBConnectionFactory.getLocalDBConnection()) {
			conn.setAutoCommit(false);

			String updateSql = "UPDATE BEVERAGE_GOODS SET GOODS_NAME=?, PRICE=?, QUANTITY =?, ";
			updateSql += "IMAGE_NAME=?,  STATUS=? WHERE GOODS_ID=?";

			try (PreparedStatement st = conn.prepareStatement(updateSql)) {

				st.setString(1, goods.getGoodsName());
				st.setInt(2, goods.getGoodsPrice());
				st.setInt(3, goods.getGoodsQuantity());
				st.setString(4, goods.getGoodsImageName());
				st.setString(5, goods.getStatus());
				st.setBigDecimal(6, goods.getGoodsID());

				int valueupdate = st.executeUpdate();
				if (valueupdate != 0) {
					updateSuccess = true;
				}

				conn.commit();
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return updateSuccess;
	}

	/**
	 * 後臺管理刪除商品
	 * 
	 * @param goodsID
	 * @return boolean
	 */
	public boolean deleteGoods(BigDecimal goodsID) {
		boolean deleteSuccess = false;

		try (Connection conn = DBConnectionFactory.getLocalDBConnection()) {
			conn.setAutoCommit(false);

			String deleteSql = "DELETE FROM BEVERAGE_GOODS WHERE GOODS_ID = ?";
			try (PreparedStatement st = conn.prepareStatement(deleteSql)) {
				st.setBigDecimal(1, goodsID);
				int valuedeletedate = st.executeUpdate();
				if (valuedeletedate != 0) {
					deleteSuccess = true;
				}
				conn.commit();
			} catch (SQLException e) {
				// 若發生錯誤則資料 rollback(回滾)
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return deleteSuccess;
	}

	/**
	 * 後臺管理顧客訂單查詢
	 * 
	 * @param queryStartDate
	 * @param queryEndDate
	 * @return Set(SalesReport)
	 */
	public Set<Order> queryOrderBetweenDate(String queryStartDate, String queryEndDate) {
		Set<Order> reports = new LinkedHashSet<>();

		try (Connection conn = DBConnectionFactory.getLocalDBConnection()) {
			conn.setAutoCommit(false);

			String queryBetweenSql = "SELECT Q.ORDER_ID,Q.ORDER_DATE,Q.GOODS_BUY_PRICE,Q.BUY_QUANTITY ,W.CUSTOMER_NAME,E.GOODS_NAME, "
					+ "GOODS_BUY_PRICE * BUY_QUANTITY AS buyAmount " + "FROM BEVERAGE_ORDER Q "
					+ "JOIN  BEVERAGE_MEMBER W ON Q.CUSTOMER_ID=W.IDENTIFICATION_NO "
					+ "JOIN BEVERAGE_GOODS E ON Q.ORDER_ID=E.GOODS_ID "
					+ "where Q.ORDER_DATE between to_date(?, 'YYYY-MM-DD HH24:MI:SS') and to_date(?, 'YYYY-MM-DD HH24:MI:SS') ";
			try (PreparedStatement st = conn.prepareStatement(queryBetweenSql)) {
				st.setString(1, queryStartDate);
				st.setString(2, queryEndDate);
				ResultSet rs = st.executeQuery();
				while (rs.next()) {
					Order or = new Order();
					or.setOrderDate(rs.getDate("ORDER_DATE"));
					or.setCustomerName(rs.getString("CUSTOMER_ID"));
					or.setGoodsName(rs.getInt("GOODS_NAME"));
					or.setGoodsPrice(rs.getInt("GOODS_BUY_PRICE"));
					or.setBuyQuantity(rs.getInt("BUY_QUANTITY"));

					reports.add(or);
				}

				conn.commit();
			} catch (SQLException e) {
				// 若發生錯誤則資料 rollback(回滾)
				conn.rollback();
				throw e;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return reports;
	}

}
