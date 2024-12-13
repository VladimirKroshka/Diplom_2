import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import resources.BaseTest;
import resources.pojo.OrderRequest;
import resources.pojo.User;
import utils.OrderMethod;
import utils.UserMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static utils.RandomGenerator.generateRandomNumber;

public class OrderCreationTest extends BaseTest {

    private User user;
    private String accessToken;
    private List<String> validIngredients;
    private List<String> invalidIngredients;

    @Before
    public void setUp() {
        // Устанавливаем уникальные данные перед тестом
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
        createOrderWithParameters(validIngredients, true, null, 200);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа без авторизации")
    public void testCreateOrderWithoutAuthorization() {
        createOrderWithParameters(validIngredients, false, null, 200);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа с ингредиентами")
    public void testCreateOrderWithIngredients() {
        createOrderWithParameters(validIngredients, true, null, 200);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа без ингредиентов")
    public void testCreateOrderWithoutIngredients() {
        createOrderWithParameters(new ArrayList<>(), true, "Ingredient ids must be provided", 400);
    }

    @Test
    @DisplayName("Создание заказа")
    @Description("Создание заказа с неверным хешем ингредиентов")
    public void testCreateOrderWithInvalidIngredients() {
        createOrderWithParameters(invalidIngredients, true, null, 500);  // Убираем проверку message
    }

    @Step("Генерация параметризованного заказа")
    public void createOrderWithParameters(List<String> ingredients, boolean withAuthorization, String expectedErrorMessage, int expectedStatusCode) {
        // Создаем пользователя и получаем токен, если требуется авторизация
        if (withAuthorization) {
            accessToken = UserMethod.createUniqueUser(user);
        }

        // Создаем заказ
        OrderRequest orderRequest = new OrderRequest(ingredients);
        Response response;
        if (withAuthorization) {
            response = OrderMethod.createOrder(accessToken, orderRequest);
        } else {
            response = OrderMethod.createOrderWithoutAuthorization(orderRequest);
        }

        // Проверяем на основе ожидаемого кода статуса
        if (expectedErrorMessage != null) {
            // Проверяем, если ожидается ошибка
            response.then()
                    .statusCode(expectedStatusCode)
                    .body("message", equalTo(expectedErrorMessage)); // Проверяем наличие ошибки в поле "message"
        } else {
            // Проверяем успешный результат
            response.then()
                    .statusCode(expectedStatusCode); // Ожидаем только код статуса
        }
    }
}
