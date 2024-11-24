package utils;

import io.restassured.response.Response;
import resources.POJO.User;
import resources.POJO.UserUpdateRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static resources.Endpoints.*;

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
}
