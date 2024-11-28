package utils;

import io.restassured.response.Response;
import resources.pojo.User;
import resources.pojo.UserUpdateRequest;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static resources.Endpoints.*;
import static org.hamcrest.Matchers.equalTo;

public class UserMethod {
    public static String createUniqueUser(User user) {
        Response response = given()
                .contentType("application/json")
                .body(user)
                .log().all()
                .post(BASE_URL + API_REGISTER_USER);

        response.then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true));

        // Возвращаем токен пользователя
        return response.jsonPath().getString("accessToken").replace("Bearer ", "");
    }

    public static String loginExistingUserReturnAccessToken(User user) {
        Response response = given()
                .contentType("application/json")
                .body(user)
                .post(BASE_URL + API_LOGIN_USER);

        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
        // Возвращаем токен пользователя
        return response.jsonPath().getString("accessToken").replace("Bearer ", "");
    }

    public static void deleteUser(String accessToken) {
        given()
                .auth().oauth2(accessToken)
                .log().all()
                .when()
                .delete(BASE_URL + API_DELETED_USER)
                .then()
                .log().all()
                .statusCode(202);
    }

    public static Response loginUser(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(API_LOGIN_USER);
    }

    public static void logoutUser(String refreshToken) {
        given()
                .contentType("application/json")
                .body("{\"token\": \"" + refreshToken + "\"}")
                .log().all()
                .when()
                .post(BASE_URL + API_LOGOUT_USER)
                .then()
                .statusCode(200);
    }

    public static void updateUser(String accessToken, UserUpdateRequest updateRequest) {
        given()
                .auth().oauth2(accessToken)
                .contentType("application/json")
                .body(updateRequest)
                .log().all()
                .when()
                .patch(BASE_URL + API_UPDATE_USER)
                .then()
                .statusCode(200)
                .log().all()
                .body("success", equalTo(true));
    }

    public static Response getUser(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .when()
                .log().all()
                .get(BASE_URL + API_GET_USER)
                .then()
                .statusCode(200)
                .log().all()
                .body("success", equalTo(true))
                .extract()
                .response();
    }

    public static Response createUser(User user) {
        return given()
                .contentType("application/json")
                .body(user)
                .when()
                .post(API_REGISTER_USER);
    }

    public static Response createUserWithPartialData(Map<String, String> partialUserData) {
        return given()
                .contentType("application/json")
                .body(partialUserData)
                .when()
                .post(API_REGISTER_USER);
    }

    public static Response loginWithIncorrectCredentials(User user) {
        // Создаем карту с данными для логина
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", user.getEmail());
        loginData.put("password", user.getPassword() + "1"); // Добавляем лишний символ к паролю

        // Отправляем запрос с данными из карты
        return given()
                .contentType("application/json")
                .body(loginData) // Передаем карту
                .when()
                .post(API_LOGIN_USER);
    }

    public static void updateUserWithoutAuthorization(UserUpdateRequest updateRequest) {
        given()
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .patch(BASE_URL + API_UPDATE_USER)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    public static void updateUserWithExistingEmail(String accessToken, UserUpdateRequest updateRequest) {
        given()
                .auth().oauth2(accessToken)
                .contentType("application/json")
                .body(updateRequest)
                .when()
                .patch(BASE_URL + API_UPDATE_USER)
                .then()
                .statusCode(403)
                .log().all()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));
    }
}
