package gsn.http.ac;

import gsn.Main;
import gsn.beans.ContainerConfig;
import gsn.http.WebConstants;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Behnaz Bostanipour
 * Date: Apr 12, 2010
 * Time: 5:02:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MyCreateGroupServlet  extends HttpServlet
{
     private static transient Logger logger                             = Logger.getLogger( MyCreateGroupServlet.class );

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
                this.setSessionPrintWriter(req,out);
		        printHeader(out);
                printLayoutMastHead(out, user);
                printLayoutContent(out);
		        printForm(out);
		        printLayoutFooter(out);

           }
       }

    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
	{
        doGet(req,res);
		handleForm(req, res);

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
        out.println("<h1>Create New Group Form</h1>");
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

        out.println("<a class=linkclass href=\"/gsn/MyAdminManagementServlet\">admin only</a>");
        out.println("<a class=linkclass href=\"/gsn/MyLogoutHandlerServlet\">logout</a>");


    }
    private void printUserName(PrintWriter out, User user)
    {

        out.println("<p id=\"login\">logged in as : "+user.getUserName()+"</p>");
    }



     private void printForm(PrintWriter out) throws ServletException
	{
        Vector dsNames = this.getDSNames();
        if(dsNames==null)
        {
            out.println("<p><b>Can not print the form !</b></p>");
            return;
        }
        else
        {
            out.println("<FORM METHOD=POST>");  // posts to itself
            //out.println("<div class=\"image_float\">");
            out.println("<h2>Group Name</h2>");
            out.println("<BR>");
            this.printFormInputs(out);
            //out.println("</div>");
            out.println("<BR>");
            out.println("<h2>Group Structure</h2>");
            out.println("<BR>");
            this.printDSList(out,dsNames);
            //out.println("<div class=\"spacer\"></div>");
            out.println("<BR>");
            out.println("<BR>");
            this.printFormButtons(out);
            out.println("</FORM>");

        }
    }



    private void printFormInputs(PrintWriter out)
    {
        out.println("<table>");
        out.println("<tr><th>groupname</th><td><INPUT TYPE=TEXT NAME=groupname size=30></td></tr>");
        out.println("</table>");
    }
     private void printFormButtons(PrintWriter out)
    {
        //out.println("<table class=transparenttable>");
        out.println("<INPUT TYPE=SUBMIT class=sumitbuttonstyle VALUE=\"Create Now\">");
        out.println("&nbsp&nbsp<INPUT TYPE=RESET class=sumitbuttonstyle VALUE=\"Reset\">");
        //out.println("</table>");
    }


    private void printDSList(PrintWriter out,Vector dsNames)
    {
        String vs=null;

        if(dsNames.size()==0)
        {
            out.println("<table class=transparenttable>");
            out.println("<tr><td><LI>No virtaul sensor is available.</LI></td></tr>");
            out.println("</table>");
        }
        else
        {
            out.println("<table>");
            out.println("<tr><th> virtual sensor name </th>");
            out.println("<th> access right</th></tr>");
            for(int i=0; i<dsNames.size();i++)
            {
                vs=(String)(dsNames.get(i));
                out.println("<tr><td>" + vs + "</td>");
                out.println("<td><INPUT TYPE=RADIO  NAME="+vs+" VALUE= 1> read ");
                out.println("<INPUT TYPE=RADIO  NAME="+vs+" VALUE= 2> write ");
                out.println("<INPUT TYPE=RADIO  NAME="+vs+" VALUE=3> read/write </td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }
    }


    /****************************************** AC related Methods*******************************************************/
    /********************************************************************************************************************/

     private void handleForm(HttpServletRequest req,HttpServletResponse res) throws IOException
	{
		HttpSession session = req.getSession();
		PrintWriter out = (PrintWriter) session.getAttribute("out");
		ParameterSet pm = new ParameterSet(req);
        ConnectToDB ctdb =null;
		try
		{
            if(pm.hasEmptyParameter())
			{
				//out.println("At least one of the input parameters is empty "+"<br>");
                this.managaeUserAlert(out, "At least one of the input parameters is empty ! ",true );

			}
			else
			{
                ctdb =new ConnectToDB();
                String originalgroupname=pm.valueForName("groupname");
                String groupname= originalgroupname.replace(" ","");


                if(ctdb.valueExistsForThisColumn(new Column("GROUPNAME",groupname),"ACGROUP")==false )
                {
                    Vector vector = ctdb.getDataSourceListForParameterSet(pm);
                     if(vector.size()!= 0)
                     {

                        Group group= new Group(groupname,vector);
                        if(ctdb.registerGroup(group)== true)
                        {

                            this.managaeUserAlert(out, "Group creation was successful ! ",false );
                        }
                         else
                        {
                            this.managaeUserAlert(out, "Registration in DB failed ! ",true );
                        }
                     }
                     else
                     {
                         //out.println("You did not choose any virtual sensor !<br>");
                         this.managaeUserAlert(out, "You did not choose any virtual sensor ! ",true );
                     }

                }
                else
                {
                    //out.println("This groupname exists already in DB, choose another one!<br>");
                    this.managaeUserAlert(out, "This groupname exists already in DB, choose another one ! ",true );
                }
            }

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
    private void managaeUserAlert(PrintWriter out, String alertMessage, boolean hasFailed)
    {
        this.createAlertBox(out, alertMessage, hasFailed);
        this.callAlertBox(out);
    }


    private void createAlertBox(PrintWriter out, String alertMessage, boolean hasFailed)
    {
        out.println("<div id=\"AlertBox\" class=\"alert\">");
        out.println("<p>");
        out.println(alertMessage );
        out.println("</p>");
        if(hasFailed== true)
        {
            out.println("<p>");
            out.println("Failed to create the group, ");
            out.println("you may want to try again !");
            out.println("</p>");
        }
        else
        {
            out.println("<p>");
            out.println(" Ready to create a new group." );
            out.println("</p>");
        }

        out.println("<form style=\"text-align:right\">");
        out.println("<input");
        out.println("type=\"button\"");
        out.println("class= alertbuttonstyle");
        out.println("value=\"OK\"");
        out.println("style=\"width:75px;\"");
        out.println("onclick=\"document.getElementById('AlertBox').style.display='none'\">");
        out.println("</form>");
        out.println("</div>");

    }
    private void callAlertBox(PrintWriter out)
    {
        out.println("<SCRIPT LANGUAGE=\"JavaScript\" TYPE=\"TEXT/JAVASCRIPT\">");
        out.println("<!--");
        out.println("DisplayAlert('AlertBox',500,200);");
        out.println("//-->");
        out.println("</SCRIPT>");
    }

    /****************************************** Client Session related Methods*******************************************/
    /********************************************************************************************************************/

    private void setSessionPrintWriter(HttpServletRequest req,PrintWriter out)
    {
        req.getSession().setAttribute("out",out);
    }
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
            res.sendRedirect("https://"+req.getServerName()+":"+ Main.getContainerConfig().getSSLPort()+"/gsn/MyCreateGroupServlet");

        }
    }


    /****************************************** DB related Methods*******************************************/
    /********************************************************************************************************************/

     private Vector getDSNames()
    {
        Vector dsNames =null;
		ConnectToDB ctdb = null;
		try
		{   ctdb = new ConnectToDB();
			dsNames = ctdb.getValuesVectorForOneColumnUnderOneCondition(new Column("DATASOURCENAME"),new Column("ISCANDIDATE","no"),"ACDATASOURCE");
		}
        catch(Exception e)
        {
            System.out.println("Exception caught : "+e.getMessage());
            logger.error("ERROR IN getDSNames");
			logger.error(e.getMessage(),e);
        }
        finally
        {
            if(ctdb!=null)
            {
                ctdb.closeStatement();
                ctdb.closeConnection();
            }
        }
        return dsNames;
    }



}
