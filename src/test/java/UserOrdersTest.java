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

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static resources.Endpoints.*;
import static utils.RandomGenerator.generateRandomNumber;

public class UserOrdersTest {
    private User user;
    private String accessToken;
    private List<String> validIngredients;

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
    }

    @After
    public void tearDown() {
        // Удаляем пользователя, если получилось создать его
        if (accessToken != null) {
            UserMethod.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя")
    @Description("Получение заказов авторизованного пользователя")
    public void testGetOrdersForAuthorizedUser(){
        getOrdersForAuthorizedUser();
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя")
    @Description("Получение заказов неавторизованного пользователя")
    public void testGetOrdersForUnauthorizedUser(){
        getOrdersForUnauthorizedUser();
    }

    @Step("Получение заказов авторизованного пользователя")
    public void getOrdersForAuthorizedUser() {
        // Создаем пользователя и получаем токен
        accessToken = UserMethod.createUniqueUser(user);

        // Создаем заказ для пользователя
        List<String> selectedIngredients = Arrays.asList(validIngredients.get(0), validIngredients.get(2), validIngredients.get(4));
        OrderRequest orderRequest = new OrderRequest(selectedIngredients);
        //Запоминаем этот заказ
        String specificNumberOrder = OrderMethod.createOrderReturnOrder(accessToken, orderRequest);

        //Создадим еще несколько заказов
        for(int i = 0; i < 2; i++) {
            OrderMethod.createOrder(accessToken, orderRequest);
        }

       // Получаем заказы пользователя
        Response response = OrderMethod.getUserOrders(accessToken);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .log().all();
        //Находим определенный заказ

        String orderNumber = null;
        List<String> orderNumbers = response.jsonPath().getList("orders.number", String.class);
        for (String number : orderNumbers) {
            if (number.equals(specificNumberOrder)) {
                orderNumber = number;
                break;
            }
        }
    }

    @Step (("Получение заказов неавторизованного пользователя"))
    public void getOrdersForUnauthorizedUser() {
        // Получаем заказы неавторизованного пользователя
        Response response = given()
                .when()
                .log().all()
                .get(BASE_URL + API_GET_ORDER)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"))
                .log().all()
                .extract()
                .response();
    }
}
