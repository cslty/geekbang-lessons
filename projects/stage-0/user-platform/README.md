# 作业

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