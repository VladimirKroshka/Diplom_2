import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import resources.POJO.OrderRequest;
import resources.POJO.User;
import utils.OrderMethod;
import utils.UserMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static resources.Endpoints.*;
import static utils.RandomGenerator.generateRandomNumber;


public class OrderCreationTest {
    private User user;
    private String accessToken;
    private List<String> validIngredients;
    private List<String> invalidIngredients;

    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        // Генерируем уникальные данные перед тестом
        user = new User();
        user.setEmail("test-data" + generateRandomNumber(5) + "@yandex.ru");
        user.setPassword(generateRandomNumber(10));
        user.setName("Username" + generateRandomNumber(5));

        // Получаем список ингредиентов
        validIngredients = OrderMethod.getIngredients();
        // Создаем невалидный список
        invalidIngredients = Arrays.asList("Русские буквы бу", "invalid_id_2");
    }

    @After
    public void tearDown() {
        // Удаляем пользователя, если получилось создать его
        if (accessToken != null) {
            UserMethod.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа с авторизацией")
    public void testCreateOrderWithAuthorization() {
        createOrderWithAuthorization();
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuthorization() {
        createOrderWithoutAuthorization();
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа с ингредиентами")
    public void testCreateOrderWithIngredients() {
        createOrderWithIngredients();
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        createOrderWithoutIngredients();
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredients() {
        createOrderWithInvalidIngredients();
    }

    @Step("Создание заказа с авторизацией")
    public void createOrderWithAuthorization() {
        // Создаем пользователя и получаем токен
        accessToken = UserMethod.createUniqueUser(user);

        // Создаем заказ с авторизацией
        List<String> selectedIngredients = Arrays.asList(validIngredients.get(0), validIngredients.get(2), validIngredients.get(4));
        OrderRequest orderRequest = new OrderRequest(selectedIngredients);
        Response response = OrderMethod.createOrder(accessToken, orderRequest);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Создание заказа без авторизации")
    public void createOrderWithoutAuthorization() {
        // Создаем заказ без авторизации
        List<String> selectedIngredients = Arrays.asList(validIngredients.get(0), validIngredients.get(2), validIngredients.get(4));
        OrderRequest orderRequest = new OrderRequest(selectedIngredients);
        Response response = OrderMethod.createOrderWithoutAuthorization(orderRequest);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Создание заказа с ингредиентами")
    public void createOrderWithIngredients() {
        // Создаем пользователя и получаем токен
        accessToken = UserMethod.createUniqueUser(user);

        // Создаем заказ с ингредиентами
        List<String> selectedIngredients = Arrays.asList(validIngredients.get(0), validIngredients.get(2), validIngredients.get(4));
        OrderRequest orderRequest = new OrderRequest(selectedIngredients);
        Response response = OrderMethod.createOrder(accessToken, orderRequest);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Step("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        // Создаем пользователя и получаем токен
        accessToken = UserMethod.createUniqueUser(user);

        // Создаем заказ без ингредиентов
        OrderRequest orderRequest = new OrderRequest(new ArrayList<>());
        Response response = OrderMethod.createOrder(accessToken, orderRequest);
        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredients() {
        // Создаем пользователя и получаем токен
        accessToken = UserMethod.createUniqueUser(user);

        // Создаем заказ с неверным хешем ингредиентов
        OrderRequest orderRequest = new OrderRequest(invalidIngredients);
        Response response = OrderMethod.createOrder(accessToken, orderRequest);
        response.then()
                .statusCode(500);
    }
}
