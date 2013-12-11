package gsn.http.ac;

import gsn.Main;
import gsn.beans.ContainerConfig;
import gsn.http.WebConstants;


import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 15, 2010
 * Time: 7:57:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyDisplayACTablesContentServlet extends HttpServlet
{

     /****************************************** Servlet Methods*******************************************/
    /******************************************************************************************************/
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        // Get the session
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null)
        {
        	UserUtils.redirectToLogin(req,res);
        }
        else
        {
            this.checkSessionScheme(req,res);
             if(user.isAdmin()== false)
            {
                res.sendError( WebConstants.ACCESS_DENIED , "Access denied." );
            }
            else
            {
                this.printHeader(out);
                this.printLayoutMastHead(out, user );
                this.printLayoutContent(out);
                printBody(out);
                this.printLayoutFooter(out);


            }
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
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
        out.println("<h1>Display AC Tables Content</h1>");

	}
    private void printLayoutMastHead(PrintWriter out, User user)
    {
    }
    private void printLayoutContent(PrintWriter out)
    {
        out.println("<div id=\"content\">");
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
    private void printLinks(PrintWriter out)
    {
        //out.println("<a class=linkclass href=\"/gsn/MyLoginHandlerServlet\">login</a>");
        //out.println("<a class=linkclass href=/gsn/MyAccessRightsManagementServlet>access rights management</a>");
        out.println("<a class=linkclass href=\"/gsn/MyAdminManagementServlet\">admin only</a>");
        out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");

        //out.println("<a class=linkclass href=\"/\">GSN home</a>");
    }
    private void printUserName(PrintWriter out, User user)
    {
        //String username=user.getUserName();
        out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");
    }

     private void printBody(PrintWriter out) throws ServletException
    {
        ConnectToDB ctdb = null;
        HtmlResultSet hrs=new HtmlResultSet();
        try
        {
            ctdb = new ConnectToDB();
            this.createFormSubmitButtons(out,ctdb);
        }
        catch(Exception e)
        {
            out.println("Exception caught : "+e.getMessage());
        }
        finally
        {
            if(ctdb!=null)
            {
                ctdb.closeStatement();
                ctdb.closeConnection();
            }
        }
    }
    public void createFormSubmitButtons(PrintWriter out, ConnectToDB ctdb)
    {
        out.println("<p>Please click on the appropriate table to see its content:</p>");
        out.println("<FORM ACTION=/gsn/MyHtmlResultSetServlet METHOD=POST>");
        out.println("<ul class=inline-list>");
         for(int i=ctdb.getACTables().size()-1;i>=0;i--)
        {
            String tbname=(String)ctdb.getACTables().get(i);
            out.println("<li><INPUT TYPE=SUBMIT name='tablename' class='button tiny radius' VALUE= "+tbname+"></li>");
        }
        out.println("</ul>");
        out.println("</FORM>");


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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyDisplayACTablesContentServlet");

        }
    }



}
