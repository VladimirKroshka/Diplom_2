package utils;

import io.restassured.response.Response;
import resources.POJO.OrderRequest;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static resources.Endpoints.*;

public class OrderMethod {

    public static Response createOrder(String accessToken, OrderRequest orderRequest) {
        return given()
                .auth().oauth2(accessToken)
                .contentType("application/json")
                .log().all()
                .body(orderRequest)
                .when()
                .post(BASE_URL + API_CREATE_ORDER)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public static String  createOrderReturnOrder(String accessToken, OrderRequest orderRequest) {
        Response response = given()
                .auth().oauth2(accessToken)
                .contentType("application/json")
                .log().all()
                .body(orderRequest)
                .when()
                .post(BASE_URL + API_CREATE_ORDER)
                .then()
                .log().all()
                .extract()
                .response();
        return response.jsonPath().getString("order.number");
    }

    public static Response createOrderWithoutAuthorization(OrderRequest orderRequest) {
        return given()
                .contentType("application/json")
                .body(orderRequest)
                .log().all()
                .when()
                .post(BASE_URL + API_CREATE_ORDER)
                .then()
                .log().all()
                .extract()
                .response();
    }

    public static List<String> getIngredients() {
        Response response = given()
                .when()
                .log().all()
                .get(BASE_URL + API_GET_INGREDIENTS)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .response();

        return response.jsonPath().getList("data._id", String.class);
    }

    public static Response getUserOrders(String accessToken) {
        return given()
                .auth().oauth2(accessToken)
                .when()
                .get(BASE_URL + API_GET_ORDER)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .extract()
                .response();
    }
}
