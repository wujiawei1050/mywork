package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * 数据库工具类
 * 封装数据的链接和过程 
 *
 */
public class DBUtil {
	// DataSource 是BasicDataSource的接口
	// BasicDataSource 实现DataSource接口
	/*
	 * 连接池一个即可，被全部的子线程共享的一个
	 * 连接池对象！所以定义为静态（只有一份）的变量
	 */
	private static DataSource ds; 
	
	private static String driver;
	private static String url;
	private static String user;
	private static String pwd;
	/**
	 * 加载一次性的静态资源：配置文件
	 */
	static{
		String file = "util/db.properties";
		Properties cfg = new Properties();
		try{
			InputStream in = DBUtil.class
				.getClassLoader()
				.getResourceAsStream(file);
			cfg.load(in);
			//读取配置文件中的jdbc参数信息
			driver = cfg.getProperty
					("jdbc.driver");
			url=cfg.getProperty("jdbc.url");
			user=cfg.getProperty("jdbc.user");
			pwd=cfg.getProperty("jdbc.pwd");
			System.out.println(driver);
			/*
			 * 加载配置文件以后，得到了数据库的
			 * 连接参数。利用连接参数初始化连接池
			 * 对象。
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
	 * 连接到数据库 
	 * @return 连接对象
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
			 * 修改DBUtil类的 连接数据库方法
			 * 更新为利用连接池连接到数据库的方法
			 * DBUtil 就可以用在 多线程情况下了
			 */
			Connection conn = 
					ds.getConnection();
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			/*
			 *将捕获的任何异常都转换为SQL异常再抛出。
			 *是常见的异常类型转换的技巧!
			 */
			throw new SQLException(e);
			//return null;
		}
	}
	
	/**
	 * 关闭数据库的方法
	 * 对于从连接池获得的连接对象而言，关闭
	 * 就是归还到连接池
	 * 原理：连接中连接的close()方法是被重写
	 * 的方法，修改了原有的关闭行为。
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






