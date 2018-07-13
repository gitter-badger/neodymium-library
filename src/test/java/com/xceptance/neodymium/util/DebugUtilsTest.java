package com.xceptance.neodymium.util;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.xceptance.neodymium.NeodymiumRunner;
import com.xceptance.neodymium.module.statement.browser.multibrowser.Browser;

@RunWith(NeodymiumRunner.class)
@Browser("Chrome_headless")
public class DebugUtilsTest
{
    @Test
    public void testHighlighting()
    {
        Selenide.open("https://blog.xceptance.com/");
        DebugUtils.injectJavaScript();
        assertJsSucessfullyInjected();

        final List<WebElement> list = $("body").findElements(By.cssSelector("#masthead"));
        DebugUtils.highlightElements(list, Neodymium.getDriver());
        $(".neodymium-highlight-box").shouldBe(visible);

        DebugUtils.resetAllHighlight();
        $(".neodymium-highlight-box").shouldNot(exist);

        final List<WebElement> list2 = $("body").findElements(By.cssSelector("#content article"));
        DebugUtils.highlightElements(list2, Neodymium.getDriver());
        $$(".neodymium-highlight-box").shouldHaveSize(10);

        DebugUtils.resetAllHighlight();
        $(".neodymium-highlight-box").shouldNot(exist);
    }

    @Test
    public void testHighlightingWithoutImplicitWaitTime()
    {
        Selenide.open("https://blog.xceptance.com/");
        DebugUtils.injectJavaScript();
        assertJsSucessfullyInjected();

        final List<WebElement> list = $("body").findElements(By.cssSelector("#masthead"));
        DebugUtils.highlightElements(list, Neodymium.getDriver());
        $(".neodymium-highlight-box").shouldBe(visible);

        DebugUtils.resetAllHighlight();
        $(".neodymium-highlight-box").shouldNot(exist);
    }

    @Test
    public void testWaiting()
    {
        NeodymiumWebDriverTestListener eventListener = new NeodymiumWebDriverTestListener();
        ((EventFiringWebDriver) Neodymium.getDriver()).register(eventListener);

        Neodymium.configuration().setProperty("neodymium.debugUtils.highlight", "true");

        // one wait due to navigation
        Selenide.open("https://blog.xceptance.com/");
        Assert.assertEquals(0, eventListener.impliciteWaitCount);

        // one wait due to find
        $("body #masthead").should(exist);
        Assert.assertEquals(1, eventListener.impliciteWaitCount);
        assertJsSucessfullyInjected();

        // two waits due to chain finding
        $("body").findElements(By.cssSelector("#content article"));
        Assert.assertEquals(3, eventListener.impliciteWaitCount);

        // two waits due to find and click
        $("#text-3 h1").click();
        Assert.assertEquals(4, eventListener.impliciteWaitCount);

        // additional two waits due to find and click
        $("#masthead .search-toggle").click();
        Assert.assertEquals(5, eventListener.impliciteWaitCount);

        // three waits due to find and change value (consumes 2 waits)
        $("#search-container .search-form input.search-field").val("abc");
        Assert.assertEquals(6, eventListener.impliciteWaitCount);

        // two waits due to find and press enter
        $("#search-container .search-form input.search-field").pressEnter();
        Assert.assertEquals(7, eventListener.impliciteWaitCount);
    }

    @Test
    public void testIFrames() throws Exception
    {
        Neodymium.configuration().setProperty("neodymium.debugUtils.highlight", "true");

        Selenide.open("https://www.w3schools.com/tags/tryit.asp?filename=tryhtml_select");
        Neodymium.getDriver().switchTo().frame("iframeResult");

        SelenideElement body = $("body");
        body.click();
        assertJsSucessfullyInjected();

        final List<WebElement> list = $("body").findElements(By.cssSelector("select"));

        Neodymium.configuration().setProperty("neodymium.debugUtils.highlight", "false");
        DebugUtils.highlightElements(list, Neodymium.getDriver());
        $(".neodymium-highlight-box").shouldBe(visible);

        DebugUtils.resetAllHighlight();
        $(".neodymium-highlight-box").shouldNot(exist);
    }

    private void assertJsSucessfullyInjected()
    {
        Assert.assertTrue(Selenide.executeJavaScript("return !!window.NEODYMIUM"));
    }
}
