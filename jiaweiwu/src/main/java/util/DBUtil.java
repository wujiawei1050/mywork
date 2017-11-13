package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * ���ݿ⹤����
 * ��װ���ݵ����Ӻ͹��� 
 *
 */
public class DBUtil {
	// DataSource ��BasicDataSource�Ľӿ�
	// BasicDataSource ʵ��DataSource�ӿ�
	/*
	 * ���ӳ�һ�����ɣ���ȫ�������̹߳����һ��
	 * ���ӳض������Զ���Ϊ��̬��ֻ��һ�ݣ��ı���
	 */
	private static DataSource ds; 
	
	private static String driver;
	private static String url;
	private static String user;
	private static String pwd;
	/**
	 * ����һ���Եľ�̬��Դ�������ļ�
	 */
	static{
		String file = "util/db.properties";
		Properties cfg = new Properties();
		try{
			InputStream in = DBUtil.class
				.getClassLoader()
				.getResourceAsStream(file);
			cfg.load(in);
			//��ȡ�����ļ��е�jdbc������Ϣ
			driver = cfg.getProperty
					("jdbc.driver");
			url=cfg.getProperty("jdbc.url");
			user=cfg.getProperty("jdbc.user");
			pwd=cfg.getProperty("jdbc.pwd");
			System.out.println(driver);
			/*
			 * ���������ļ��Ժ󣬵õ������ݿ��
			 * ���Ӳ������������Ӳ�����ʼ�����ӳ�
			 * ����
			 */
			BasicDataSource ds = 
					new BasicDataSource();
			ds.setDriverClassName(driver);
			ds.setUrl(url);
			ds.setUsername(user);
			ds.setPassword(pwd);
			ds.setInitialSize(1);
			ds.setMaxIdle(2);
			ds.setMaxActive(50);
			
			DBUtil.ds = ds;
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * ���ӵ����ݿ� 
	 * @return ���Ӷ���
	 */
	public static Connection getConnection()
		throws SQLException{
		try{
//			String driver = 
//				"oracl.jdbc.OracleDriver";
//			String url = 
//				"jdbc:oracle:thin:@192.168.201.221:1521:orcl";
//			String user = "openlab";
//			String pwd = "open123";
//			Class.forName(driver);
//			Connection conn =DriverManager
//					.getConnection(url,user,pwd);
			/*
			 * �޸�DBUtil��� �������ݿⷽ��
			 * ����Ϊ�������ӳ����ӵ����ݿ�ķ���
			 * DBUtil �Ϳ������� ���߳��������
			 */
			Connection conn = 
					ds.getConnection();
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			/*
			 *��������κ��쳣��ת��ΪSQL�쳣���׳���
			 *�ǳ������쳣����ת���ļ���!
			 */
			throw new SQLException(e);
			//return null;
		}
	}
	
	/**
	 * �ر����ݿ�ķ���
	 * ���ڴ����ӳػ�õ����Ӷ�����ԣ��ر�
	 * ���ǹ黹�����ӳ�
	 * ԭ�����������ӵ�close()�����Ǳ���д
	 * �ķ������޸���ԭ�еĹر���Ϊ��
	 */
	public static void close(Connection conn){
		try {
			if(conn!=null){
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
		throws SQLException {
		Connection con = 
			DBUtil.getConnection();
		System.out.println(con);
		con.close();
	}
	
}






