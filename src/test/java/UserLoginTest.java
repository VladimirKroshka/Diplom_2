import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import resources.POJO.User;
import utils.UserMethod;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static resources.Endpoints.*;
import static utils.RandomGenerator.generateRandomNumber;

public class UserLoginTest {
    private User user;
    private String refreshToken;
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        //генерируем уникальные данные перед тестом
        user = new User();
        user.setEmail("test-data" + generateRandomNumber(5) + "@yandex.ru");
        user.setPassword(generateRandomNumber(10));
        user.setName("Username" + generateRandomNumber(5));
    }

    @After
    public void tearDown() {
        //делаем разлогин
        if (refreshToken != null) {
            UserMethod.logoutUser(refreshToken);
        }
        // Удаляем пользователя, если мы его создавали
        if (accessToken != null) {
            UserMethod.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("Логин под существующим пользователем")
    public void testLoginExistingUser() {
        loginExistingUser();
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("логин с неверным логином и паролем")
    public void testLoginWithIncorrectCreditional() {
        loginWithIncorrectCreditional();
    }

    @Step("Логин под существующим пользователем")
    public void loginExistingUser() {
        //создаем пользователя для теста и получаем токен
        accessToken = UserMethod.createUniqueUser(user);
        //авторизуемся под пользователем
        Response response = given()
                .contentType("application/json")
                .body(this.user)
                .post(BASE_URL + API_LOGIN_USER);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
        //сохраняем refreshToken, он нужен, чтобы делать logout
        refreshToken = response.jsonPath().getString("refreshToken");
    }

    public void loginWithIncorrectCreditional() {
        //создаем пользователя для теста и получаем токен
        accessToken = UserMethod.createUniqueUser(user);
        //добавляем к паролю лишний знак
        Response response = given()
                .contentType("application/json")
                .body("{\"email\": \"" + user.getEmail() + "\", \"password\": \"" + user.getPassword() + "1\"}")
                .post(BASE_URL + API_LOGIN_USER);
        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
        ;
    }
}

