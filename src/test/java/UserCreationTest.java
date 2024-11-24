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

public class UserCreationTest {

    private User user;
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
        // Удаляем пользователя, если получилось создать его
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
        Response response = given()
                .contentType("application/json")
                .body(this.user)
                .post(BASE_URL + API_REGISTER_USER);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
        //сохраняем токен для использования и удаления пользователя
        accessToken = response.jsonPath().getString("accessToken").replace("Bearer ", "");
    }

    @Step("Создание пользователя, который уже зарегистрирован")
    public void createExistingUser() {
        //Создаем пользователя
        testCreateUniqueUser();
        //Пытаемся повторно создать пользователя
        given()
                .contentType("application/json")
                .body(this.user)
                .when()
                .post(BASE_URL + API_REGISTER_USER)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Step("Создание пользователя без одного из обязательных полей")
    public void createUserWithoutRequiredField() {
        given()
                .contentType("application/json")
                .body("{\"email\": \"" + user.getEmail() + "\", \"password\": \"" + user.getPassword() + "\"}")
                .when()
                .post(BASE_URL + API_REGISTER_USER)
                .then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
