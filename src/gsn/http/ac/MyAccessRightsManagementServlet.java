package gsn.http.ac;

import gsn.Main;
import gsn.beans.ContainerConfig;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: May 15, 2010
 * Time: 5:57:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyAccessRightsManagementServlet extends HttpServlet
{

     private static transient Logger logger= Logger.getLogger( MyAccessRightsManagementServlet.class );

    /****************************************** Servlet Methods*******************************************/
    /****************************************************************************************************/

    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Get the session
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        this.checkSessionScheme(req,res);
        this.printHeader(out);
        this.printLayoutMastHead(out,user);
        this.printLayoutContent(out);
        this.printUserAccountLinks(out);
        this.printLayoutFooter(out);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
       this.doGet(req,res);

    }


    /****************************************** HTML Printing Methods*******************************************/
    /***********************************************************************************************************/
    private void printHeader(PrintWriter out)
	{
        out.println("<HTML>");
        out.println("<HEAD>");
		out.println("<TITLE>Access Rights Management</TITLE>");
        out.println(" <link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"/style/foundation.css\"/>");
        out.println(" <link rel=\"stylesheet\" media=\"screen\" type=\"text/css\" href=\"/style/app.css\"/>");
        out.println("</HEAD>");
        out.println("<body>");
        out.println("<div class=\"columns\"><br /><br /><br /></div><div class=\"contain-to-grid fixed\"><nav class=\"top-bar sticky\"><div class=\"row\"><ul class=\"title-area\"><li class=\"name\"><h1><a href='#'><strong>GSN Server</strong></a></h1></li></ul><strong><section id=\"navigation\" class=\"top-bar-section\"></section></strong></div></nav></div>");
        out.println("<div id=\"container\" class=row>");
        out.println("<div class='box columns'>");
        out.println("<h1>Access Rights Management</h1>");

	}
   private void printLayoutMastHead(PrintWriter out, User user)
    {
    }
    private void printLayoutContent(PrintWriter out)
    {
        out.println("<div id=\"content\" class='medium-12 columns'>");
    }
    private void printLayoutFooter(PrintWriter out)
    {
        out.println("</div>");
        out.println("<div id=\"footer\" class='medium-12 columns'> ");
        out.println(" <p align=\"center\"><FONT COLOR=\"#000000\"/>Powered by <a class=\"nonedecolink\" href=\"http://globalsn.sourceforge.net/\">GSN</a>,  Distributed Information Systems Lab, EPFL 2010</p>");
        out.println("</div>");//footer
        out.println("</div>");//box
        out.println("</div>");//container
        out.println("<script src=\"//ajax.googleapis.com/ajax/libs/jquery/1.8/jquery.min.js\"></script>");
        out.println("<script type='text/javascript' src='/js/manager.js'></script>");
        out.println("</body>");
        out.println("</html>");
    }
    private void printFooter(PrintWriter out)
    {
        out.println("</div>");
        out.println("<div id=\"footer\" class='medium-12 columns'> ");
        out.println(" <p align=\"center\"><FONT COLOR=\"#000000\"/>Powered by <a class=\"nonedecolink\" href=\"http://globalsn.sourceforge.net/\">GSN</a>,  Distributed Information Systems Lab, EPFL 2010</p>");
        out.println("</div>");//footer
        out.println("</div>");//box
        out.println("</div>");//container
        out.println("<script src=\"//ajax.googleapis.com/ajax/libs/jquery/1.8/jquery.min.js\"></script>");
        out.println("<script type='text/javascript' src='/js/manager.js'></script>");
        out.println("</body>");
        out.println("</html>");
    }

    private void printLinks(PrintWriter out, User user)
    {
        if(user!=null)
        {
            out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");
        }

    }
    private void printUserName(PrintWriter out, User user)
    {
        //String username=user.getUserName();
        if(user !=null)
        {
            out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");
        }
    }
    public void printUserAccountLinks(PrintWriter out)
    {
        out.println("<p>Welcome to your access rights management ! you have the following options:</p>");
        out.println("<ul class=button-group >");
        out.println("<LI class=linklistli><a class='button small' href=/gsn/MyUserCandidateRegistrationServlet>New User?  Sign Up</a></LI>");
        out.println("<LI class=linklistli><a class='button small' href=\"/gsn/MyUserAccountManagementServlet\">User Account Management</a></LI>");
        out.println("<LI class=linklistli><a class='button small' href=\"/gsn/MyAdminManagementServlet\">Admin Only</a></LI>");
        out.println("</ul>");
    }

    /****************************************** Client Session related Methods*******************************************/
    /********************************************************************************************************************/

    private void checkSessionScheme(HttpServletRequest req, HttpServletResponse res)throws IOException
    {

         if(req.getScheme().equals("https")== true)
        {
            if((req.getSession().getAttribute("scheme")==null))
            {
                req.getSession().setAttribute("scheme","https");
            }
        }
         else if(req.getScheme().equals("http")== true )
        {
             if((req.getSession().getAttribute("scheme")==null))
            {
                req.getSession().setAttribute("scheme","http");
            }
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyAccessRightsManagementServlet");

        }
    }






}
