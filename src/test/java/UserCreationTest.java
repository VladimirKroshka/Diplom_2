import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import resources.BaseTest;
import resources.pojo.User;
import utils.UserMethod;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static resources.Endpoints.*;
import static utils.RandomGenerator.generateRandomNumber;
import static utils.UserMethod.createUser;

public class UserCreationTest extends BaseTest {

    private User user;
    private String accessToken;

    @Before
    public void setUp() {
        user = new User(
                "test-data" + generateRandomNumber(5) + "@yandex.ru",
                generateRandomNumber(10),
                "Username" + generateRandomNumber(5)
        );
    }

    @After
    public void tearDown() {
        // Удаляем пользователя, если он был создан
        if (accessToken != null) {
            UserMethod.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создание уникального пользователя")
    public void testCreateUniqueUser() {
        createUniqueUser();
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создание пользователя, который уже зарегистрирован")
    public void testCreateExistingUser() {
        createExistingUser();
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создание пользователя без одного из обязательных полей")
    public void testCreateUserWithoutRequiredField() {
        createUserWithoutRequiredField();
    }

    @Step("Создание уникального пользователя")
    public void createUniqueUser() {
        Response response = createUser(user);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
        // Сохраняем токен для последующего удаления пользователя
        accessToken = response.jsonPath().getString("accessToken").replace("Bearer ", "");
    }

    @Step("Создание пользователя, который уже зарегистрирован")
    public void createExistingUser() {
        // Создаем пользователя
        createUniqueUser();
        // Пытаемся повторно создать пользователя
        Response response = createUser(user);
        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Step("Создание пользователя без одного из обязательных полей")
    public void createUserWithoutRequiredField() {
        // Создаем карту для передачи только части данных
        {
            // Передаём только email и пароль
            Map<String, String> partialUserData = new HashMap<>();
            partialUserData.put("email", user.getEmail());
            partialUserData.put("password", user.getPassword());

            Response response = UserMethod.createUserWithPartialData(partialUserData);
            response.then()
                    .statusCode(403)
                    .body("success", equalTo(false))
                    .body("message", equalTo("Email, password and name are required fields"));
        }
    }
}
