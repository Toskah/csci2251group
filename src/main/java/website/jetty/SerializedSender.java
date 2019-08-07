package website.jetty;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SerializedSender extends HttpServlet {

    private String username;
    private String password;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        this.username = request.getParameter("username");
        this.password = request.getParameter("password");

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
