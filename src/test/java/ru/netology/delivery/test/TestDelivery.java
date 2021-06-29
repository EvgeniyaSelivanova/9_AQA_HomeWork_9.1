package ru.netology.delivery.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.val;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class TestDelivery {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        val validUser = DataGenerator.Registration.generateUser("ru");
        val daysToAddForFirstMeeting = 4;
        val firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        val daysToAddForSecondMeeting = 7;
        val secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);
        // TODO: добавить логику теста в рамках которого будет выполнено планирование и перепланирование встречи.
        // Для заполнения полей формы можно использовать пользователя validUser и строки с датами в переменных
        // firstMeetingDate и secondMeetingDate. Можно также вызывать методы generateCity(locale),
        // generateName(locale), generatePhone(locale) для генерации и получения в тесте соответственно города,
        // имени и номера телефона без создания пользователя в методе generateUser(String locale) в датагенераторе
        SelenideElement form = $("[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id='city'] input").setValue(validUser.getCity());
        form.$("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        form.$("[data-test-id='date'] input").setValue(firstMeetingDate);
        form.$("[data-test-id='name'] input").setValue(validUser.getName());
        form.$("[data-test-id='phone'] input").setValue(validUser.getPhone());
        form.$("[data-test-id='agreement']").click();
        form.$$("button").find(exactText("Запланировать")).click();
        String firstDate = $("[data-test-id='date'] input").getValue();
        $("[data-test-id='success-notification'] .notification__content")
                .should(exactText("Встреча успешно запланирована на " + firstDate)).shouldBe(visible);
        form.$("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        form.$("[data-test-id='date'] input").setValue(secondMeetingDate);
        form.$$("button").find(exactText("Запланировать")).click();
        $(withText("Необходимо подтверждение")).shouldBe(Condition.visible);
        $("[data-test-id='replan-notification'] button").click();
        String secondDate = $("[data-test-id='date'] input").getValue();
        $("[data-test-id='success-notification'] .notification__content")
                .shouldHave(exactText("Встреча успешно запланирована на " + secondDate));

    }

    @Test
    void shouldConfirmIncorrectInputCity() {
        val daysToAddForFirstMeeting = 4;
        val firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        SelenideElement form = $("[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id='city'] input").setValue(DataGenerator.generateCity("en"));
        form.$("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        form.$("[data-test-id='date'] input").setValue(firstMeetingDate);
        form.$("[data-test-id='name'] input").setValue(DataGenerator.generateName("ru"));
        form.$("[data-test-id='phone'] input").setValue(DataGenerator.generatePhone("ru"));
        form.$("[data-test-id='agreement']").click();
        form.$$("button").find(exactText("Запланировать")).click();
        $("[data-test-id='city'].input_invalid .input__sub").should(exactText("Доставка в выбранный " +
                "город недоступна")).shouldBe(Condition.visible);
    }

    @Test
    void shouldConfirmIncorrectInputName() {
        val daysToAddForFirstMeeting = 4;
        val firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        SelenideElement form = $("[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id='city'] input").setValue(DataGenerator.generateCity("ru"));
        form.$("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        form.$("[data-test-id='date'] input").setValue(firstMeetingDate);
        form.$("[data-test-id='name'] input").setValue(DataGenerator.generateName("en"));
        form.$("[data-test-id='phone'] input").setValue(DataGenerator.generatePhone("ru"));
        form.$("[data-test-id='agreement']").click();
        form.$$("button").find(exactText("Запланировать")).click();
        $("[data-test-id='name'].input_invalid .input__sub").should(exactText("Имя и Фамилия указаные неверно. " +
                "Допустимы только русские буквы, пробелы и дефисы.")).shouldBe(Condition.visible);
    }

    @Test
    void shouldConfirmIncorrectInputPhone() {
        val daysToAddForFirstMeeting = 4;
        val firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        SelenideElement form = $("[class='form form_size_m form_theme_alfa-on-white']");
        form.$("[data-test-id='city'] input").setValue(DataGenerator.generateCity("ru"));
        form.$("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        form.$("[data-test-id='date'] input").setValue(firstMeetingDate);
        form.$("[data-test-id='name'] input").setValue(DataGenerator.generateName("ru"));
        form.$("[data-test-id='phone'] input").setValue(DataGenerator.generatePhone("en"));
        form.$("[data-test-id='agreement']").click();
        form.$$("button").find(exactText("Запланировать")).click();
        $("[data-test-id='phone'].input_invalid .input__sub").should(exactText("Телефон указан неверно. Должно быть 11 цифр, например, " +
                "+79012345678.")).shouldBe(Condition.visible);
    }

}