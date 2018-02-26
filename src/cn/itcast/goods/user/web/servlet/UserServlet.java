package cn.itcast.goods.user.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.exception.UserException;
import cn.itcast.goods.user.service.UserService;
import cn.itcast.servlet.BaseServlet;

/**
 * 用户模块WEB层
 * @author qdmmy6
 *
 */
public class UserServlet extends BaseServlet {
	/**
	 * ajax用户名是否注册校验
	 */
	private UserService userService = new UserService();
	
	public String ajaxValidateLoginname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 * 1获取用户名
		 */
		String loginname = req.getParameter("loginname");
		/**
		 * 2通过sevice得到校验结果
		 */
		boolean b = userService.ajaxValidateLoginname(loginname);
		/**
		 * 3.发给客户端
		 */
		resp.getWriter().print(b);
		return null;
	}
	/**
	 * ajax邮箱是否注册校验
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateEmail(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 * 1获取email
		 */
		String email = req.getParameter("email");
		/**
		 * 2通过sevice得到校验结果
		 */
		boolean b = userService.ajaxValidateEmail(email);
		/**
		 * 3.发给客户端
		 */
		resp.getWriter().print(b);
		return null;
	}
	/**
	 * ajax验证码是否正确校验
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateVerifyCode(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 * 1、获取输入框中的校验码
		 */
		String verifyCode = req.getParameter("verifyCode");
		/**
		 * 2.获取图片上真实的验证码
		 */
		String vcode = (String) req.getSession().getAttribute("vCode");
		/**
		 * 3.进行忽略大小写比较，得到结果
		 */
		boolean b = verifyCode.equalsIgnoreCase(vcode);
		/**
		 * 4.发送给客户端
		 */
		resp.getWriter().print(b);
		return null;
	}
	/**
	 * 注册功能
	 */

	public String regist(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("regist().......");
		/**
		 * 1.封装表单数据到user对象
		 */
		User formuser = CommonUtils.toBean(req.getParameterMap(), User.class);
		/**
		 * 2.校验之，如果校验失败，保存错误信息，返回regist.jsp显示
		 */
		Map<String,String> errors = validateRegist(formuser, req.getSession());
		if(errors.size()>0){
			req.setAttribute("form", formuser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/regist.jsp";
		}
		/**
		 * 3.使用service完成业务
		 */
		System.out.println("调用service中regist前");
		userService.regist(formuser);
		System.out.println("调用service中regist成功");
		/**
		 * 4.保存成功信息转发到msg.jsp显示
		 */
		req.setAttribute("code", "success");
		req.setAttribute("msg", "注册成功，请马上到邮箱激活!");
		
		return "f:/jsps/msg.jsp";
	}
	
	/**
	 * 注册校验
	 */
	private Map<String,String> validateRegist(User formuser,HttpSession session){
		Map<String,String> errors = new HashMap<String,String>();
		/**
		 * 1.校验登录名
		 */
		String loginname = formuser.getLoginname();
		if(loginname==null||loginname.trim().isEmpty()){
			errors.put("loginname", "用户名不能为空");
		}else if(loginname.length()<3||loginname.length()>20){
			errors.put("loginname", "用户名长度应在3~20之间");
		}else if(!userService.ajaxValidateLoginname(loginname)){
			errors.put("loginname", "用户名已被注册！");
		}
		
		/**
		 * 2.校验密码
		 */
		String loginpass = formuser.getLoginpass();
		if(loginpass==null||loginpass.trim().isEmpty()){
			errors.put("loginpass", "密码不能为空");
		}else if(loginpass.length()<3||loginpass.length()>20){
			errors.put("loginpass", "密码长度应在3~20之间");
		}
		/**
		 * 2.校验确认密码
		 */
		String reloginpass = formuser.getReloginpass();
		if(reloginpass==null||reloginpass.trim().isEmpty()){
			errors.put("reloginname", "密码不能为空");
		}else if(!reloginpass.equals(loginpass)){
			errors.put("reloginname", "两次密码不一致");
		}
		
		/**
		 * email校验
		 */
		String email = formuser.getEmail();
		if(email==null||email.trim().isEmpty()){
			errors.put("email", "Email不能为空");
		}else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")){
			errors.put("email", "Email格式错误");
		}else if(!userService.ajaxValidateEmail(email)){
			errors.put("email", "Email已被注册！");
		}
		/**
		 * 5.验证码校验
		 */
		String verifyCode = formuser.getVerifyCode();
		String vcode = (String) session.getAttribute("vCode");
		if(verifyCode==null||verifyCode.trim().isEmpty()){
			errors.put("verifyCode", "验证码不能为空");
		}else if(!verifyCode.equalsIgnoreCase(vcode)){
			errors.put("verifyCode", "验证码错误");
		}
		return errors;
	}
	/**
	 * 激活功能
	 */

	public String activation(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 *1. 获取参数激活码
		 *2.用激活码调用service方法完成激活
		 *  service方法可能抛出异常，将异常信息保存到request中，转发到mag.jsp
		 *  3.保存成功信息到request，转发到mag.jsp
		 */
		String code = req.getParameter("activationCode");
		try {
			userService.activation(code);
			req.setAttribute("code", "success");//通知msg显示对号
			req.setAttribute("msg", "恭喜激活成功，请马上登录");
		} catch (UserException e) {
			//service抛出了异常
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("code", "error");//通知mag.jsp显示x
		}
		return "f:/jsps/msg.jsp";
	}
	/**
	 * 修改密码
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 * 1.封装表单数据到User中
		 * 2.从session中获取uid
		 * 3.使用uid和表单中的oldPass和newPass来调用service方法
		 *   *如果出现了异常，保存异常信息到request中，转发到pwd.jsp中
		 * 4.保存成功信息保存到request
		 * 5.转发到msg.jsp
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		User user = (User) req.getSession().getAttribute("sessionUser");
		//如果用户没有登陆返回到登录页面,显示错误信息
		if(user == null){
			req.setAttribute("msg", "您还没有登录！");
			return "f:/jsps/user/login.jsp";
		}
		
		try {
			userService.updatePassword(user.getUid(), formUser.getNewpass(), 
					formUser.getLoginpass());
			req.setAttribute("msg", "修改密码成功");
			req.setAttribute("code", "success");
			return "f:/jsps/msg.jsp";
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());//保存异常信息
			req.setAttribute("user", formUser);//为了回显
			return "f:/jsps.user/pwd.jsp";
		}
		
	}
	
	
	/**
	 * 退出功能
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String quit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.getSession().invalidate();
		return "r:/jsps/user/login.jsp";
	}
	
	
	/**
	 * 登陆功能
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String login(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/**
		 * 1.封装表单数据到User
		 * 2.校验表单数据
		 * 3.使用service查询，得到User
		 * 4.查看用户是否存在，
		 *  *若不存在 保存错误信息：用户名或密码错误
		 *  *保存用户数据，用于回显
		 *  *转发到login.jsp
		 * 5.如果存在，查看状态，如果状态为false
		 *  *保存错误信息：你没有激活
		 *  *保存表单数据，用于回显
		 *  *转发到login.jsp
		 * 6.登陆成功
		 *  *保存当前查询出的user到session中
		 *  *保存当前用户的名称到cookie中，注意中文需要编码处理
		 */
		/**
		 * 1.封装表单数据到User
		 */
		User formuser = CommonUtils.toBean(req.getParameterMap(), User.class);
		/**
		 * 校验
		 */
		Map<String,String> errors = validateLogin(formuser, req.getSession());
		if(errors.size()>0){
			req.setAttribute("form", formuser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/login.jsp";
		}
		/**
		 * 3.调用userService的login方法
		 */
		User user = userService.login(formuser);
		/**
		 * 4.开始判断
		 */
		if(user==null){
			req.setAttribute("msg", "用户名或密码错误");
			req.setAttribute("user", formuser);
			return "f:/jsps/user/login.jsp";
		}else{
			if(!user.isStatus()){
				req.setAttribute("msg", "您还没有激活");
				req.setAttribute("user", formuser);
				return "f:/jsps/user/login.jsp";
			}else{
				//保存用户到session
				req.getSession().setAttribute("sessionUser", user);
				//获取用户名保存到cookie中
				String loginname = user.getLoginname();
				loginname = URLEncoder.encode(loginname,"utf-8");
				Cookie cookie = new Cookie("loginname",loginname);
				cookie.setMaxAge(60 * 60 * 24 * 10); //保存10天
				resp.addCookie(cookie);
				return "r:/index.jsp";//重定向到首页
			}
		}
	}
	
	/**
	 * 登录校验
	 */
	private Map<String,String> validateLogin(User formuser,HttpSession session){
		Map<String,String> errors = new HashMap<String,String>();
		 
		return errors;
	}
}
