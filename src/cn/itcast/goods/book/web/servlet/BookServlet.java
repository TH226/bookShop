package cn.itcast.goods.book.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.book.service.BookService;
import cn.itcast.goods.pager.PageBean;
import cn.itcast.servlet.BaseServlet;



public class BookServlet extends BaseServlet {
	BookService bookService = new BookService();
	
	
	
	
/**
 * 获取pc
 * @param req
 * @return
 */
	private int getPc(HttpServletRequest req){
		int pc = 1;
		String param = req.getParameter("pc");
		if(param!=null&& !param.trim().isEmpty()){
			try{
				pc=Integer.parseInt(param);
			}catch(RuntimeException e){}
		}
		return pc;
	}
	/**
	 * 截取url，页面中的分页导航中需要使用它作为产连接的目标
	 *  请求：http://localhost:8080/goods/BookServlet?method=findByCategory&cid=xxx&pc=3
	 *  /goods/BookServlet + method=findByCategory&cid=xxx
	 * @param req
	 * @return
	 */
	private String getUrl(HttpServletRequest req){
		String url = req.getRequestURI() + "?" + req.getQueryString();
		/**
		 * 如果url中存在pc参数则截取掉，不存在就不截取
		 */
		int index = url.lastIndexOf("&pc=");
		if(index != -1){
			url = url.substring(0,index);
		}
		return url;
	}
	
	
	/**
	 * 按照bid查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);
		return "f:/jsps/book/desc.jsp";
	}
	
	
	/**
	 * 按照分类查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	
	public String findByCategory(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//super.service(req, resp);
		/**
		 * 1.得到pc：如果页面传递，使用页面的，没传 pc=1
		 */
		int pc = getPc(req);
		//System.out.println("我是pc："+pc);
		/**
		 * 2.得到url
		 */
		String url = getUrl(req);
		
		/**
		 * 3.获取查询条件，本方法就是cid，即分类的id
		 */
		String cid = req.getParameter("cid");
		System.out.println("我是cid："+cid);
		//System.out.println("我是cid："+cid);
		/**
		 * 4.使用pc和cid调用servlet#findBooktegory得到pageBean
		 */
		PageBean<Book> pb = bookService.findByCategory(cid, pc);
		/**
		 * 5.给PageBean设置url，保存PageBean 转发到/jsps/book/list.jsp
		 */
		//System.out.println("我是URL: "+url);
		pb.setUrl(url);
		//System.out.println("我是pb："+pb);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}

	/**
	 * 按照作者查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	
	public String findByAuthor(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//req.setCharacterEncoding("UTF-8");
		//resp.setCharacterEncoding("UTF-8");
		/*
		 * 1. 得到pc：如果页面传递，使用页面的，如果没传，pc=1
		 */
		int pc = getPc(req);
		/*
		 * 2. 得到url：...
		 */
		String url = getUrl(req);
		/*
		 * 3. 获取查询条件，本方法就是cid，即分类的id
		 */
		//String author = new String(req.getParameter("author").getBytes("iso-8859-1"), "utf-8");
		String author = req.getParameter("author");
		/*
		 * 4. 使用pc和cid调用service#findByCategory得到PageBean
		 */
		PageBean<Book> pb = bookService.findByAuthor(author, pc);
		/*
		 * 5. 给PageBean设置url，保存PageBean，转发到/jsps/book/list.jsp
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}

	/**
	 * 按照出版社查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByPress(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//super.service(req, resp);
		/**
		 * 1.得到pc：如果页面传递，使用页面的，没传 pc=1
		 */
		int pc = getPc(req);
		//System.out.println("我是pc："+pc);
		/**
		 * 2.得到url
		 */
		String url = getUrl(req);
		
		/**
		 * 3.获取查询条件，本方法就是cid，即分类的id
		 */
		String press = req.getParameter("press");
		//System.out.println("我是cid："+cid);
		/**
		 * 4.使用pc和cid调用servlet#findBooktegory得到pageBean
		 */
		PageBean<Book> pb = bookService.findByPress(press, pc);
		/**
		 * 5.给PageBean设置url，保存PageBean 转发到/jsps/book/list.jsp
		 */
		//System.out.println("我是URL: "+url);
		pb.setUrl(url);
		//System.out.println("我是pb："+pb);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	
	public String findByBname(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//super.service(req, resp);
		/**
		 * 1.得到pc：如果页面传递，使用页面的，没传 pc=1
		 */
		int pc = getPc(req);
		//System.out.println("我是pc："+pc);
		/**
		 * 2.得到url
		 */
		String url = getUrl(req);
		
		/**
		 * 3.获取查询条件，本方法就是cid，即分类的id
		 */
		String bname = req.getParameter("bname");
		//System.out.println("我是cid："+cid);
		/**
		 * 4.使用pc和cid调用servlet#findBooktegory得到pageBean
		 */
		PageBean<Book> pb = bookService.findByBname(bname, pc);
		/**;
		 * 5.给PageBean设置url，保存PageBean 转发到/jsps/book/list.jsp
		 */
		//System.out.println("我是URL: "+url);
		pb.setUrl(url);
		//System.out.println("我是pb："+pb);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/**
	 * 按照组合条件查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByCombination(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//super.service(req, resp);
		/**
		 * 1.得到pc：如果页面传递，使用页面的，没传 pc=1
		 */
		int pc = getPc(req);
		//System.out.println("我是pc："+pc);
		/**
		 * 2.得到url
		 */
		String url = getUrl(req);
		
		/**
		 * 3.获取查询条件，本方法就是cid，即分类的id
		 */
		Book criteria = CommonUtils.toBean(req.getParameterMap(), Book.class);
		//System.out.println("我是cid："+cid);
		/**
		 * 4.使用pc和cid调用servlet#findBooktegory得到pageBean
		 */
		PageBean<Book> pb = bookService.findByCombination(criteria, pc);
		/**
		 * 5.给PageBean设置url，保存PageBean 转发到/jsps/book/list.jsp
		 */
		//System.out.println("我是URL: "+url);
		pb.setUrl(url);
		//System.out.println("我是pb："+pb);
		req.setAttribute("pb", pb);
		return "f:/jsps/book/list.jsp";
	}
}
