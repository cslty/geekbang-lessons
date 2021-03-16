# 作业

## 项目运行
 - mvn clean package -U
 - java -jar .\user-web\target\user-web-v1-SNAPSHOT-war-exec.jar
 

## 第一周作业

### 要求

- 通过自研 Web MVC 框架实现（可以自己实现）一个用户注册，forward 到一个成功的页面（JSP 用法）/register
- 通过 Controller -> Service -> Repository 实现（数据库实现）
- （非必须）JNDI 的方式获取数据库源（DataSource），在获取 Connection

### 实现

- tomcat插件添加配置`<enableNaming>true</enableNaming>`
- 注册页面：`http://localhost:8080/register`
- 代码实现类：`org.geektimes.projects.user.web.controller.RegisterController`


## 第二周作业

### 要求

- 通过课堂上的简易版依赖注入和依赖查找，实现用户注册功能
- 通过 UserService 实现用户注册，注册用户需要校验
  - Id：必须大于 0 的整数
  - 密码：6-32 位 电话号码: 采用中国大陆方式（11 位校验）

### 实现

1. tomcat7不兼容hibernate-validator:6+，将版本降到5.4.3.Final
    ```xml
       <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator</artifactId>
            <version>5.4.3.Final</version>
       </dependency>
    ```
2. 依赖注入实现
    ```java
       @Resource(name = "bean/EntityManager")
       private EntityManager entityManager;
   
       @Resource(name = "bean/UserRepository")
       private UserRepository userRepository;
   
       @Resource(name = "bean/Validator")
       private Validator validator;
   
       ComponentContext.getInstance().getComponent("bean/UserService");
    ```
3. 用户注册校验`org.geektimes.projects.user.service.UserServiceImpl.validateRegister`
    ```java
        @Id
        // 不做处理
        @Min(value = 1, message = "id需大于0")
        @GeneratedValue(strategy = AUTO)
        private Long id;
    
        @Column
        @NotBlank(message = "用户名不能为空", groups = {Register.class, Login.class})
        @Length(max = 16, message = "用户名不能超过16个字符", groups = {Register.class, Login.class})
        private String name;
    
        @Column
        @NotBlank(message = "密码不能为空", groups = {Register.class, Login.class})
        @Length(min = 6, max = 32, message = "密码只支持6~32个字符", groups = {Register.class, Login.class})
        private String password;
    
        @Column
        @NotBlank(message = "邮箱不能为空", groups = {Register.class})
        @Email(message = "邮箱格式错误", groups = {Register.class})
        private String email;
    
        @Column
        @NotBlank(message = "手机号不能为空", groups = {Register.class})
        @Phone(message = "手机号格式错误", groups = {Register.class})
        private String phoneNumber;
    ```
4. 手机号规则校验
    - 注解：`org/geektimes/projects/user/validator/bean/validation/Phone.java`
    - 实现：`org.geektimes.projects.user.validator.bean.validation.PhoneValidator`
    
5. 销毁阶段实现
    - 实现：`org.geektimes.context.ComponentContext.processPreDestroy`
    - 测试：`org.geektimes.projects.user.service.UserServiceImpl.destroy`
    
    
## 第三周作业

### 要求

- 整合`https://jolokia.org/`
  - 实现一个自定义JMX MBean, 通过jolokia做Servlet代理
- 继续完成 Microprofile Config Api 中的实现
  - 扩展 org.eclipse.microprofile.config.spi.ConfigSource 实现，包括 OS 环境变量，以及本地配置文件
  - 扩展 org.eclipse.microprofile.config.spi.Converter 实现，提供 String 类型到简单类型
- 通过 org.eclipse.microprofile.config.Config 读取到当前应用名称
  - 应用名称 property name = "application.name"
  
### 实现(jolokia)
1. maven依赖
    ```xml
        <dependency>
            <groupId>org.jolokia</groupId>
            <artifactId>jolokia-core</artifactId>
            <version>1.6.2</version>
        </dependency>
        <dependency>
            <groupId>org.jolokia</groupId>
            <artifactId>jolokia-client-java</artifactId>
            <version>1.6.2</version>
        </dependency>
    ```
2. servlet配置
    ```xml
        <servlet>
            <servlet-name>jolokia-agent</servlet-name>
            <servlet-class>org.jolokia.http.AgentServlet</servlet-class>
            <load-on-startup>2</load-on-startup>
        </servlet>
    
        <servlet-mapping>
            <servlet-name>jolokia-agent</servlet-name>
            <url-pattern>/jolokia/*</url-pattern>
        </servlet-mapping>
    ```
3. 初始化注册MBean
  org.geektimes.projects.user.web.listener.ManagementBeanInitializerListener
    ```xml
    <listener>
        <listener-class>org.geektimes.projects.user.web.listener.ManagementBeanInitializerListener</listener-class>
    </listener>
    ```
4. 测试类：`org.geektimes.management.demo.JolokiaDemo`
    - MBean列表：`JolokiaDemo.listMBean` http://localhost:8080/jolokia/list
    - MBean属性写入：`JolokiaDemo.writeMBean` http://localhost:8080/jolokia/write/org.geektimes.projects.user.management:type=User/Name/lisi
    - MBean属性读取：`JolokiaDemo.readMBean` http://localhost:8080/jolokia/read/org.geektimes.projects.user.management:type=User/User
    - MBean方法执行：`JolokiaDemo.execMBean` http://localhost:8080/jolokia/exec/org.geektimes.projects.user.management:type=User/toString
    
### 实现(Microprofile Config Api)
1. ConfigSource 扩展
    - META-INF/application.properties配置文件：`org.geektimes.configuration.microprofile.config.source.ApplicationPropertiesConfigSource`
    - 系统环境变量：`org.geektimes.configuration.microprofile.config.source.SystemEnvironmentConfigSource`
2. Converter 定义
    ```text
       org.geektimes.configuration.microprofile.config.converter.StringConvert
       org.geektimes.configuration.microprofile.config.converter.StringToBooleanConvert
       org.geektimes.configuration.microprofile.config.converter.StringToDoubleConvert
       org.geektimes.configuration.microprofile.config.converter.StringToIntegerConvert
       org.geektimes.configuration.microprofile.config.converter.StringToLongConvert
    ```
3. Config 配置读取
    - 实现：`org.geektimes.configuration.microprofile.config.JavaConfig`
    - 获取：`DefaultConfigProviderResolver.instance().getConfig()`
    - 调用：`Config.getValue(String propertyName, Class<T> propertyType)`
4. 测试
    - META-INF/application.properties：`org.geektimes.projects.user.web.listener.TestingListener.testPropertyFromApplicationProperties`
    - 系统环境变量：`org.geektimes.projects.user.web.listener.TestingListener.testPropertyFromSystemEnvironment`