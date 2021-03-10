package org.geektimes.projects.user.web.controller;

import org.geektimes.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.exception.UserRegisterException;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 * 注册实现
 * <p>
 *
 * @author liuwei
 * @since 2021/3/2
 */
@Path("/register")
public class RegisterController implements PageController {

    private static Logger logger = Logger.getLogger(RegisterController.class.getName());

    final UserService userService = ComponentContext.getInstance().getComponent("bean/UserService");

    @GET
    @POST
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        // GET请求路由跳转
        if (Objects.equals(request.getMethod(), HttpMethod.GET)) {
            return "register.jsp";
        }
        return doRegister(request, response);
    }

    /**
     * <p>
     * 注册调用
     * </p>
     *
     * @param request  请求对象
     * @param response 返回对象
     * @return jsp页面
     */
    public String doRegister(HttpServletRequest request, HttpServletResponse response) {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");

        User user = new User(username, password, email, phoneNumber);
        if (logger.isLoggable(Level.INFO)) {
            logger.info("[用户注册] 注册参数：" + user);
        }

        try {
            if (!userService.register(user)) {
                request.setAttribute("errorMessage", "注册失败，服务不可用");
                return "register.jsp";
            }
        } catch (Exception e) {
            if (e instanceof UserRegisterException) {
                request.setAttribute("errorMessage", e.getMessage());
            } else {
                logger.log(Level.SEVERE, e.getMessage(), e);
                request.setAttribute("errorMessage", "注册异常，服务不可用");
            }
            return "register.jsp";
        }
        return "index.jsp";
    }

}