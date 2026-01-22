package allureListeners;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import io.qameta.allure.Allure;

public class AllureListener implements ITestListener {
	@Override
	public void onStart(ITestContext context) {
		Allure.step("Test Suite '" + context.getName() + "' has started");
	}

	@Override
	public void onFinish(ITestContext context) {
		Allure.step("Test Suite '" + context.getName() + "' has finished");
	}

	@Override
	public void onTestStart(ITestResult result) {
		Allure.step("Test case '" + result.getName() + "' has started");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		WebDriver driver = pages.AllClientsBasePages.BasePage.driver;
		if (driver != null) {
			byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
			Allure.getLifecycle().addAttachment("Screenshot on Failure", "image/png", "png", screenshot);
		}

		Allure.step("Test case has failed ❌: " + result.getThrowable().getMessage(),
				io.qameta.allure.model.Status.FAILED);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		Allure.step("Test case has passed ✅", io.qameta.allure.model.Status.PASSED);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		Allure.step("Test case has skipped ⏸️", io.qameta.allure.model.Status.SKIPPED);
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

		Allure.step("Test case has failed within success percentage ⚠️", io.qameta.allure.model.Status.BROKEN);
	}

}