package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import util.DBUtil;
import entity.Cost;

public class CostDaoImpl implements CostDao {

	@Override
	public List<Cost> findAll() {
		Connection con = null;
		try {
			//��������
			con = DBUtil.getConnection();
			//SQL
			String sql = 
				"select * from cost_lhh " +
				"order by cost_id";
			//����SQL
			PreparedStatement ps = 
				con.prepareStatement(sql);
			//ִ��SQL
			ResultSet rs = ps.executeQuery();
			List<Cost> list = new ArrayList();
			while(rs.next()) {
				Cost c = createCost(rs);
				list.add(c);
			}
			return list;
		} catch (SQLException e) {
			//��¼������־
			e.printStackTrace();
			throw new RuntimeException(
				"��ѯ�ʷ�ʧ��.", e);
		} finally {
			//�黹����
			DBUtil.close(con);
		}
	}

	/**
	 * Alt+Shift+M
	 */
	private Cost createCost(ResultSet rs) 
		throws SQLException {
		Cost c = new Cost();
		c.setCostId(rs.getInt("cost_id"));
		c.setName(rs.getString("name"));
		c.setBaseDuration(rs.getInt("base_duration"));
		c.setBaseCost(rs.getDouble("base_cost"));
		c.setUnitCost(rs.getDouble("unit_cost"));
		c.setStatus(rs.getString("status"));
		c.setDescr(rs.getString("descr"));
		c.setCreatime(rs.getTimestamp("creatime"));
		c.setStartime(rs.getTimestamp("startime"));
		c.setCostType(rs.getString("cost_type"));
		return c;
	}
	
	public static void main(String[] args) {
		CostDao dao = new CostDaoImpl();
		Cost c = dao.findById(1);
		System.out.println(
			c.getName() + "," +
			c.getDescr());
	}

	@Override
	public void save(Cost cost) {
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			String sql = 
				"insert into cost_lhh values" +
				"(cost_seq_lhh.nextval," +
				"?,?,?,?,'0',?,sysdate,sysdate,?)";
			PreparedStatement ps = 
				con.prepareStatement(sql);
			ps.setString(1, cost.getName());
			//setInt/setDouble��Ҫ����int/double��
			//��֧��null�������ֶ��ڿ���������Ϊ�գ�
			//Ϊ�˿��Դ���null�����ǽ��䵱��������
//			ps.setInt(2, cost.getBaseDuration());
//			ps.setDouble(3, cost.getBaseCost());
//			ps.setDouble(4, cost.getUnitCost());
			ps.setObject(2, cost.getBaseDuration());
			ps.setObject(3, cost.getBaseCost());
			ps.setObject(4, cost.getUnitCost());
			ps.setString(5, cost.getDescr());
			ps.setString(6, cost.getCostType());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"�����ʷ�ʧ��", e);
		} finally {
			DBUtil.close(con);
		}
	}

	@Override
	public Cost findById(int id) {
		Connection con = null;
		try {
			con = DBUtil.getConnection();
			String sql = 
				"select * from cost_lhh " +
				"where cost_id=?";
			PreparedStatement ps = 
				con.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				return createCost(rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(
				"����ID��ѯ�ʷ�ʧ��", e);
		} finally {
			DBUtil.close(con);
		}
		return null;
	}

}







