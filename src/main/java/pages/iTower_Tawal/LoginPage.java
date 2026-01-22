package pages.iTower_Tawal;

public class LoginPage extends pages.AllClientsBasePages.BasePage {

	public void doLogin(String username, String password) throws InterruptedException {
		enterTextIntoInputBoxForLogin("username_ID", username);
		enterTextIntoInputBoxForLogin("password_ID", password);
		click("signInButton_XPATH");

	}
}