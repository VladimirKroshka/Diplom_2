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
import resources.pojo.UserUpdateRequest;
import utils.UserMethod;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static resources.Endpoints.*;
import static utils.RandomGenerator.generateRandomNumber;

public class UserUpdateTest extends BaseTest {
    private User user;
    private String accessToken;
    private String accessTokenFirstUsers;

    @Before
    public void setUp() {
        // Генерируем уникальные данные перед тестом
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
        // Удаляем еще одного пользователя
        if (accessTokenFirstUsers != null) {
            UserMethod.deleteUser(accessTokenFirstUsers);
        }
    }

    @Test
    @DisplayName("Изменение данных пользователя")
    @Description("Изменение данных пользователя с авторизацией")
    public void testUpdateUserWithAuthorization() {
        updateUserWithAuthorization();
    }

    @Test
    @DisplayName("Изменение данных пользователя")
    @Description("Изменение данных пользователя без авторизации")
    public void testUpdateUserWithoutAuthorization(){
        updateUserWithoutAuthorization();
    }

    @Test
    @DisplayName("Изменение данных пользователя")
    @Description("Изменение данных пользователя с занятым email")
    public void testUpdateUserWithExistingEmail(){
        updateUserWithExistingEmail();
    }

    @Step("Изменение данных пользователя с авторизацией")
    public void updateUserWithAuthorization() {
        // Создаем пользователя и получаем токен
        accessToken = UserMethod.createUniqueUser(user);

        // Обновляем данные пользователя
        user.setEmail("new"+user.getEmail());
        user.setName("new"+user.getName());
        user.setPassword("new"+user.getPassword());
        UserUpdateRequest updateRequest = new UserUpdateRequest(user.getEmail(), user.getPassword(),
                user.getName());

        UserMethod.updateUser(accessToken, updateRequest);
        // заходим под новыми данными
        accessToken = UserMethod.loginExistingUserReturnAccessToken(user);
        // Проверяем, что данные пользователя обновлены
        Response response = UserMethod.getUser(accessToken);
        //сравниваем полученные значения с теми, что отправляли
        response.then()
                .log().all()
                .body("user.name", equalTo(user.getName()))
                .body("user.email", equalTo(user.getEmail()));
    }

    @Step("Изменение данных пользователя без авторизации")
    public void updateUserWithoutAuthorization() {
        // Создаем пользователя и получаем токен
        accessToken = UserMethod.createUniqueUser(user);

        // Обновляем данные пользователя без авторизации
        UserUpdateRequest updateRequest = new UserUpdateRequest(user.getEmail(), user.getPassword(), "NewName");
        UserMethod.updateUserWithoutAuthorization(updateRequest);
    }


    public void updateUserWithExistingEmail() {
        // Создаем первого пользователя и получаем токен
        accessTokenFirstUsers = UserMethod.createUniqueUser(user);
        String emailFirstUser = user.getEmail();

        // Создаем второго пользователя
        user.setEmail("1"+user.getEmail());
        user.setPassword("1"+user.getPassword());
        user.setName("1"+user.getName());
        accessToken = UserMethod.createUniqueUser(user);

        // Обновляем данные второго пользователя, использую почту первого
        UserUpdateRequest updateRequest = new UserUpdateRequest(emailFirstUser, user.getPassword(), user.getName());
        UserMethod.updateUserWithExistingEmail(accessToken, updateRequest);
    }
}
