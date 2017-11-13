package web;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.AdminDao;
import dao.AdminDaoImpl;
import dao.CostDao;
import dao.CostDaoImpl;
import entity.Admin;
import entity.Cost;
import util.ImageUtil;

public class MainServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// 判断请求路径
		String path = req.getServletPath();
		if (path.equals("/findCost.do")) {
			// 查询资费
			findCost(req, res);
		} else if (path.equals("/toAddCost.do")) {
			// 打开增加资费页面
			toAddCost(req, res);
		} else if (path.equals("/addCost.do")) {
			// 增加保存资费
			addCost(req, res);
		} else if (path.equals("/toUpdateCost.do")) {
			// 打开修改资费页面
			toUpdateCost(req, res);
		} else if (path.equals("/toLogin.do")) {
			// 打开登录页
			toLogin(req, res);
		} else if (path.equals("/toIndex.do")) {
			// 打开首页
			toIndex(req, res);
		} else if (path.equals("/login.do")) {
			// 登录校验
			login(req, res);
		} else if (path.equals("/createImage.do")) {
			// 生成验证码
			createImage(req, res);
		} else {
			throw new RuntimeException("查无此页");
		}
	}

	private void createImage(HttpServletRequest req, HttpServletResponse res) throws IOException {
		// 生成验证码图片
		Object[] objs = ImageUtil.createImage();
		// 将验证码存入session，便于
		// 后续对验证码的校验。
		HttpSession session = req.getSession();
		session.setAttribute("imageCode", objs[0]);
		// 将图片输出给浏览器
		BufferedImage image = (BufferedImage) objs[1];
		res.setContentType("image/png");
		OutputStream os = res.getOutputStream();
		ImageIO.write(image, "png", os);
		os.close();
	}

	private void login(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		// 1.获取表单数据
		req.setCharacterEncoding("utf-8");
		String code = req.getParameter("adminCode");
		String pwd = req.getParameter("password");
		String icode = req.getParameter("code");
		// 2.对数据校验
		HttpSession session = req.getSession();
		String imageCode = (String) session.getAttribute("imageCode");
		if (icode == null || icode.equals("") || !icode.equalsIgnoreCase(imageCode)) {
			// 验证码错误
			req.setAttribute("error", "验证码错误");
			req.getRequestDispatcher("WEB-INF/main/login.jsp").forward(req, res);
			return;
		}

		AdminDao dao = new AdminDaoImpl();
		Admin admin = dao.findByCode(code);
		if (admin == null) {
			// 账号不存在
			req.setAttribute("error", "账号不存在");
			req.getRequestDispatcher("WEB-INF/main/login.jsp").forward(req, res);
		} else if (!admin.getPassword().equals(pwd)) {
			// 密码错误
			req.setAttribute("error", "密码错误");
			req.getRequestDispatcher("WEB-INF/main/login.jsp").forward(req, res);
		} else {
			// 校验通过
			// 将账号存入cookie
			Cookie cookie = new Cookie("adminCode", code);
			res.addCookie(cookie);
			// 将账号存入session
			session.setAttribute("adminCode", code);
			res.sendRedirect("toIndex.do");
		}
	}

	private void toIndex(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("WEB-INF/main/index.jsp").forward(req, res);
	}

	private void toLogin(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		req.getRequestDispatcher("WEB-INF/main/login.jsp").forward(req, res);
	}

	private void toUpdateCost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// 1.接收请求参数
		String id = req.getParameter("costId");
		// 2.查询要修改的资费
		CostDao dao = new CostDaoImpl();
		Cost cost = dao.findById(Integer.parseInt(id));
		// 3.转发到修改资费页面
		// 当前：/netctoss/toUpdateCost.do
		// 目标：/netctoss/WEB-INF/cost/update_cost.jsp
		req.setAttribute("cost", cost);
		req.getRequestDispatcher("WEB-INF/cost/update_cost.jsp").forward(req, res);
	}

	private void addCost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		// 1.获取表单中的数据
		req.setCharacterEncoding("utf-8");
		String name = req.getParameter("name");
		String costType = req.getParameter("costType");
		String baseDuration = req.getParameter("baseDuration");
		String baseCost = req.getParameter("baseCost");
		String unitCost = req.getParameter("unitCost");
		String descr = req.getParameter("descr");
		// 2.处理数据(新增)
		Cost c = new Cost();
		c.setName(name);
		c.setCostType(costType);
		c.setDescr(descr);
		if (baseDuration != null && !baseDuration.equals("")) {
			c.setBaseDuration(Integer.parseInt(baseDuration));
		}
		if (baseCost != null && !baseCost.equals("")) {
			c.setBaseCost(Double.parseDouble(baseCost));
		}
		if (unitCost != null && !unitCost.equals("")) {
			c.setUnitCost(Double.parseDouble(unitCost));
		}
		CostDao dao = new CostDaoImpl();
		dao.save(c);
		// 3.重定向到资费查询
		// 当前：/netctoss/addCost.do
		// 目标：/netctoss/findCost.do
		res.sendRedirect("findCost.do");
	}

	private void toAddCost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// 当前:/netctoss/toAddCost.do
		// 目标:/netctoss/WEB-INF/cost/add_cost.jsp
		req.getRequestDispatcher("WEB-INF/cost/add_cost.jsp").forward(req, res);
	}

	private void findCost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// 查询所有的资费
		CostDao dao = new CostDaoImpl();
		List<Cost> list = dao.findAll();
		// 转发到查询页面
		// 当前:/netctoss/findCost.do
		// 目标:/netctoss/WEB-INF/cost/find_cost.jsp
		req.setAttribute("costs", list);
		req.getRequestDispatcher("WEB-INF/cost/find_cost.jsp").forward(req, res);
	}

}
