package resources;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class BaseTest {
    public static RequestSpecification requestSpecification;

    static {
        // Настройка baseURI для всех тестов
        RestAssured.baseURI = Endpoints.BASE_URL; // Укажите ваш базовый URL
        requestSpecification = RestAssured.given()
                .baseUri(RestAssured.baseURI)
                .filter(new AllureRestAssured()); // Добавьте Allure фильтр для интеграции
    }
}
