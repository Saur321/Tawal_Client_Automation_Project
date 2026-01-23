package pages.AllClientsBasePages;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.asserts.SoftAssert;

import com.opencsv.CSVReader;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Status;

public class BasePage {

	public static WebDriver driver;
	public static Properties OR = new Properties();
	public static Properties config = new Properties();
	public static Properties allure = new Properties();
	private static FileInputStream fis;
	protected static Logger log = Logger.getLogger("BaseTest.class");
	public static WebDriverWait wait;
	public static int totalTickedIdCountInDownloadedFile = 0;
	public static int totalSiteIdCountInDownloadedFile = 0;
	public static Path lastGeneratedReport = null;
	public static int totalChangeIdCountInDownloadedFile = 0;
	public static int totalSiteIdCountInDownloadedFileApollo = 0;
	SoftAssert sa = new SoftAssert();
	
	//@BeforeClass
	public void setUp(String clientName) {
	    PropertyConfigurator.configure("./src/main/resources/iTower_Clients_Properties/properties_"+clientName+"/log4j.properties");
	    log.info("Test Execution Starts !!");
	    Allure.step("Test Execution Starts !!");

	    // Load config.properties
	    try {
	        fis = new FileInputStream("./src/main/resources/iTower_Clients_Properties/properties_"+clientName+"/config.properties");
	        config.load(fis);
	        log.info("Config properties file loaded");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // Load or.properties
	    try {
	        fis = new FileInputStream("./src/main/resources/iTower_Clients_Properties/properties_"+clientName+"/or.properties");
	        OR.load(fis);
	        log.info("OR properties file loaded");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    // Read browser name from config
	    String browserName = config.getProperty("browser").toLowerCase();

	    switch (browserName) {
	        case "chrome":
	            ChromeOptions chromeOptions = new ChromeOptions();
	            chromeOptions.addArguments("start-maximized");
	            chromeOptions.setAcceptInsecureCerts(true);
	            chromeOptions.addArguments("force-device-scale-factor=0.90");
	            chromeOptions.addArguments("high-dpi-support=0.90");

	            // Headless mode
	            if (Boolean.parseBoolean(config.getProperty("headless", "false"))) {
	                chromeOptions.addArguments("--headless=new");
	                log.info("Running in headless mode");
	                Allure.step("Running in headless mode");
	                System.out.println("Test Scripts are Running in headless mode");
	            }

	            // Download preferences
	            String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
	            String completeDownloadExcelPath = userDir + "\\src\\main\\resources\\downloadExcel";
	            Map<String, Object> chromePrefs = new HashMap<>();
	            chromePrefs.put("download.default_directory", completeDownloadExcelPath);
	            chromePrefs.put("profile.default_content_setting_values.automatic_downloads", 1);
	            chromePrefs.put("download.prompt_for_download", false);
	            chromeOptions.setExperimentalOption("prefs", chromePrefs);
	            chromeOptions.setExperimentalOption("useAutomationExtension", false);

	            driver = new ChromeDriver(chromeOptions);
	            log.info("Chrome browser launched");
	            Allure.step("Chrome browser launched");
	            break;

	        case "firefox":
	            driver = new FirefoxDriver();
	            log.info("Firefox browser launched");
	            Allure.step("Firefox browser launched");
	            break;

	        case "edge":
	            EdgeOptions edgeOptions = new EdgeOptions();
	            edgeOptions.addArguments("start-maximized");

	            // Headless mode for Edge
	            if (Boolean.parseBoolean(config.getProperty("headless", "false"))) {
	                edgeOptions.addArguments("--headless=new");
	                log.info("Running Edge in headless mode");
	                Allure.step("Running Edge in headless mode");
	            }

	            driver = new EdgeDriver(edgeOptions);
	            log.info("Edge browser launched");
	            Allure.step("Edge browser launched");
	            break;

	        default:
	            throw new IllegalArgumentException("Unsupported browser specified in config.properties: " + browserName);
	    }

	    // Common driver setup
	    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(120));
	    driver.get(config.getProperty("testsiteurl"));
	    log.info("Navigate to : " + config.getProperty("testsiteurl"));
	    Allure.step("Navigate to : " + config.getProperty("testsiteurl"));
	    // driver.manage().window().maximize();
	    driver.manage().timeouts()
	            .implicitlyWait(Duration.ofSeconds(Integer.parseInt(config.getProperty("implicit.wait"))));
	    wait = new WebDriverWait(driver, Duration.ofSeconds(Integer.parseInt(config.getProperty("explicit.wait"))));
	}
	
	// Generic method to enter text into an input box
		public void enterTextIntoInputBox(String locatorKey, String value) {

			try {
				if (locatorKey.endsWith("_ID")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.id(OR.getProperty(locatorKey))))
							.sendKeys(value);
				} else if (locatorKey.endsWith("_XPATH")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey))))
							.sendKeys(value);
				} else if (locatorKey.endsWith("_CSS")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))))
							.sendKeys(value);
				}
				log.info("Typing in " + locatorKey + " element and entered the value as " + value);
				Allure.step("Typing in " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "") + " element and entered the value",
						io.qameta.allure.model.Status.PASSED);
			} catch (NoSuchElementException e) {
				log.error("Element not found: " + locatorKey);
				Allure.step("Element not found: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						io.qameta.allure.model.Status.FAILED);
				e.printStackTrace();

			}
		}
		
		// Generic method to enter text into an input box
		public void enterTextIntoInputBoxForLogin(String locatorKey, String value) {
			try {
				if (locatorKey.endsWith("_ID")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.id(OR.getProperty(locatorKey))))
							.sendKeys(value);
				} else if (locatorKey.endsWith("_XPATH")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey))))
							.sendKeys(value);
				} else if (locatorKey.endsWith("_CSS")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))))
							.sendKeys(value);
				}
				log.info("Typing in " + locatorKey + " element and entered the value as " + value);
				Allure.step("Typing in " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "") + " element and entered the value",
						io.qameta.allure.model.Status.PASSED);
			} catch (NoSuchElementException e) {
				log.error("Element not found: " + locatorKey);
				Allure.step("Element not found: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						io.qameta.allure.model.Status.FAILED);
				e.printStackTrace();
			}
		}
		
		// Generic method to click on any webElement
		public static void click(String locatorKey) {
			try {
				if (locatorKey.endsWith("_ID")) {
					wait.until(ExpectedConditions.elementToBeClickable(By.id(OR.getProperty(locatorKey)))).click();
				} else if (locatorKey.endsWith("_XPATH")) {
					wait.until(ExpectedConditions.elementToBeClickable(By.xpath(OR.getProperty(locatorKey)))).click();
				} else if (locatorKey.endsWith("_CSS")) {
					wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(OR.getProperty(locatorKey)))).click();
				} else if (locatorKey.endsWith("_CLASS")) {
					wait.until(ExpectedConditions.elementToBeClickable(By.className(OR.getProperty(locatorKey)))).click();
				}
				log.info("Clicking on " + locatorKey);
				Allure.step("Clicking on " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						io.qameta.allure.model.Status.PASSED);
			} catch (NoSuchElementException e) {
				log.error("The element " + locatorKey + " is not available to click");
				Allure.step("The element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "") + " is not available to click",
						io.qameta.allure.model.Status.FAILED);
			}
		}
		
		public void UploadFile(String inputDataFileName) {
			System.out.println(config.getProperty("uploadFileLocation"));
		}
		
		// Method to check if the element is present
		public boolean isElementPresent(String locatorKey) {
			try {
				if (locatorKey.endsWith("_ID")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.id(OR.getProperty(locatorKey))));
				} else if (locatorKey.endsWith("_XPATH")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey))));
				} else if (locatorKey.endsWith("_CSS")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))));
				}
				log.info("Element " + locatorKey + " is present on webpage");
				Allure.step("Element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "" + " is present on webpage"));
				return true;
			} catch (Throwable t) {
				log.info("Error while finding presence of element " + locatorKey);
				Allure.step("Error while finding presence of element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""));
				return false;
			}
		}
		
		

		

		// Method to perform page scroll down
		public void pageScrollDown() {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("window.scrollTo(0, 500);");
		}

		// Method to perform page scroll up
		public void pageScrollUp() {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("window.scrollTo(0, -500);");
		}

		// Method to perform page scroll left
		public void pageScrollLeft() throws InterruptedException {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("document.querySelector('.ag-center-cols-viewport').scrollLeft=2500");
		}

		// Method to perform page scroll right
		public void pageScrollRight() throws InterruptedException {
			JavascriptExecutor jse = (JavascriptExecutor) driver;
			jse.executeScript("document.querySelector('.ag-center-cols-viewport').scrollLeft=-4000");
		}

		// Method to get upload file location
		public String getUploadFileLocation() {
			return config.getProperty("uploadFileLocation");
		}

		// Method to get download file location
		public String getDownloadedFileLocation() {
			return config.getProperty("downloadFileLocation");
		}

		// Method to get text
		public String getText(String locatorKey) {
			String text = null;
			try {
				if (locatorKey.endsWith("_ID")) {
					text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(OR.getProperty(locatorKey))))
							.getText();
				} else if (locatorKey.endsWith("_XPATH")) {
					text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OR.getProperty(locatorKey))))
							.getText();
				} else if (locatorKey.endsWith("_CSS")) {
					text = wait.until(
							ExpectedConditions.visibilityOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))))
							.getText();
				}
				log.info("Element available as text : " + text);
				Allure.step("Element available as text : " + text, io.qameta.allure.model.Status.PASSED);

				return text;
			} catch (Throwable t) {
				log.info("The element " + locatorKey + " is not available on page");
				Allure.step("The element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "") + " is not available on page",
						io.qameta.allure.model.Status.FAILED);
				return text;
			}
		}

		// get Text For Wrong Password
		public String getTextForWrongPassword(String locatorKey) {
			String text = null;
			try {
				if (locatorKey.endsWith("_XPATH")) {
					text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OR.getProperty(locatorKey))))
							.getText();
				}
				log.info("Element available as text : " + text);
				Allure.step("Element available as text : " + text, io.qameta.allure.model.Status.PASSED);

				return text;
			} catch (Throwable t) {
				log.info("The element " + locatorKey + " is not available on page");
				return text;
			}
		}
		
		// Delete files form location
		public void deleteOldFilesFromLocation(String deleteFileName) {
			String userDir = System.getProperty("user.dir");
			String downloadPath = userDir + config.getProperty("uploadFileLocation"); // Use the same path
			File downloadDirectory = new File(downloadPath);

			File[] files = downloadDirectory.listFiles((dir, name) -> name.startsWith(deleteFileName));

			if (files == null || files.length == 0) {
				System.out.println("No old files found to delete for: " + deleteFileName);
				return;
			}

			for (File file : files) {
				if (file.delete()) {
					System.out.println("Deleted old file: " + file.getName());
				} else {
					System.err.println("Failed to delete file: " + file.getName());
				}
			}
		}
		
		// method for explicit Wait With Clickable
		public void explicitWaitWithClickable(String locatorKey) {
			try {
				if (locatorKey.endsWith("_ID")) {
					WebElement openWndow = wait
							.until(ExpectedConditions.elementToBeClickable(By.id(OR.getProperty(locatorKey))));
					openWndow.click();
				} else if (locatorKey.endsWith("_XPATH")) {
					WebElement openWndow = wait
							.until(ExpectedConditions.elementToBeClickable(By.xpath(OR.getProperty(locatorKey))));
					openWndow.click();
				} else if (locatorKey.endsWith("_CSS")) {
					WebElement openWndow = wait
							.until(ExpectedConditions.elementToBeClickable(By.cssSelector(OR.getProperty(locatorKey))));
					openWndow.click();
				}
				log.info("Element " + locatorKey + " is clickable on webpage");
				Allure.step("Element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "" + " is clickable on webpage"),
						Status.PASSED);

			} catch (Throwable t) {
				log.info("Error while clicking of element " + locatorKey);
				Allure.step("Error while clicking clickable of element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.FAILED);

			}
		}

		// Method to get today's date
		public String getTodaysDate() {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d-MMM-yyyy");
			LocalDate localDate = LocalDate.now();
			return dtf.format(localDate);
		}

		// Method to get alert text
		public String getAlertText() {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			log.info("Handling the alert and getting the alert text as " + alertText);
			Allure.step("Handling the alert and getting the alert text as " + alertText);
			return alertText;
		}

		// Method to dismiss the alert
		public void dismissTheAlert() {
			driver.switchTo().alert().dismiss();
			log.info("Dismissing the alert");
			Allure.step("Dismissing the alert");

		}

		// Method to accept the alert
		public void acceptTheAlert() {
			driver.switchTo().alert().accept();
			log.info("Accepting the alert");
			Allure.step("Accepting the alert");
		}

		// Click on blank area
		public void clickOnBlankArea() throws InterruptedException {
			Thread.sleep(5000);
			driver.findElement(By.xpath("//body")).click();
		}

		public void clickOnBlankAreaOfPage() {

			Actions act = new Actions(driver);
			act.sendKeys(Keys.ENTER).perform();
		}

		// method for is File Exists At Location
		public boolean isFileExistsAtLocation(String fileName) {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String downloadedFileDirectory = userDir + "\\src\\main\\resources\\downloadExcel\\";
			File tmpDir = new File(downloadedFileDirectory + fileName + ".xlsx");
			FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(Duration.ofMinutes(3))
					.pollingEvery(Duration.ofSeconds(10));
			wait.until(x -> tmpDir.exists());
			boolean fileExists = tmpDir.exists();
			if (fileExists) {
				log.info(fileName + "File exists at a location : " + fileExists);
				Allure.step(fileName + "File exists at a location : " + fileExists);
			} else {
				log.info(fileName + "File exists at a location : " + fileExists);
				Allure.step(fileName + "File exists at a location : " + fileExists);
			}
			return fileExists;
		}

		// To close all the session of browser
	//	@AfterClass
		public void quit() {
			 driver.quit();
			log.info("Browser is closed and test execution complete ");
			Allure.step("Browser is closed and test execution complete ");
		}

		// method for refresh Page
		public void refreshPage() throws InterruptedException {
			driver.navigate().refresh();
		}
		
		// method for handle frame using presence Of Element Located
		public void handleIframe(String locatorKey) {
			try {
				if (locatorKey.endsWith("_ID")) {
					WebElement iframe = wait
							.until(ExpectedConditions.presenceOfElementLocated(By.id(OR.getProperty(locatorKey))));
					driver.switchTo().frame(iframe); // Switch to the iframe context

				} else if (locatorKey.endsWith("_XPATH")) {
					WebElement iframe = wait
							.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey))));
					driver.switchTo().frame(iframe); // Switch to the iframe context
				} else if (locatorKey.endsWith("_CSS")) {
					WebElement iframe = wait
							.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))));
					driver.switchTo().frame(iframe); // Switch to the iframe context
				}
				log.info("Frame " + locatorKey + " is handled on webpage");
				Allure.step("Frame " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "" + " is handled on webpage"),
						Status.PASSED);

			} catch (Throwable t) {
				log.info("Error while handling the frame " + locatorKey);
				Allure.step("Error while handling the frame " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.FAILED);

			}
		}
		
		// method for click on escape key
		public void clickEscape() {
			Actions action = new Actions(driver);
			action.keyDown(Keys.CONTROL).sendKeys(Keys.ESCAPE).build().perform();
		}

		// method for click On List Of Web element and return value
		public List<WebElement> clickOnListOfWebelement(String locatorKey) {
			List<WebElement> setOfTickedType = null;
			try {
				if (locatorKey.endsWith("_ID")) {
					setOfTickedType = driver.findElements((By.id(OR.getProperty(locatorKey))));

				} else if (locatorKey.endsWith("_XPATH")) {
					setOfTickedType = driver.findElements((By.xpath(OR.getProperty(locatorKey))));
				} else if (locatorKey.endsWith("_CSS")) {
					setOfTickedType = driver.findElements((By.cssSelector(OR.getProperty(locatorKey))));
				}
				log.info(" List of Element " + locatorKey + " is presence on webpage");
				Allure.step("List of Element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "" + " is presence on webpage"),
						Status.PASSED);
			} catch (Throwable t) {
				log.info("Error while handling List of Element " + locatorKey);
				Allure.step("Error while handling List of Element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.FAILED);

			}
			return setOfTickedType;

		}

		// method for upload The CSV File
		public void uploadTheCSVFile(String dataUpload) throws InterruptedException {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String fileLocation = userDir + getUploadFileLocation() + dataUpload + ".csv";
			uploadExcel("fileupload_ID", fileLocation);
			Allure.step(dataUpload + " csv file uploaded.");
			Thread.sleep(3000);
		}

		// Generic method to upload csv file
		public void uploadExcel(String locatorKey, String value) {
			try {
				if (locatorKey.endsWith("_ID")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.id(OR.getProperty(locatorKey))))
							.sendKeys(value);
				} else if (locatorKey.endsWith("_XPATH")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey))))
							.sendKeys(value);
				} else if (locatorKey.endsWith("_CSS")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))))
							.sendKeys(value);
				}
				log.info("File is uploaded from the location as " + value);
				Allure.step("File is uploaded from the location as " + value, io.qameta.allure.model.Status.PASSED);
			} catch (NoSuchElementException e) {
				log.error("File location not found: " + locatorKey);
				Allure.step("File location not found: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						io.qameta.allure.model.Status.FAILED);
				e.printStackTrace();
			}
		}

		// method for handle soft assert
		public void handleSoftAssert(String locatorKey, String expectedvalue) {
			SoftAssert sa = new SoftAssert();
			sa.assertEquals(getText(locatorKey), expectedvalue);

		}

		// method for click On Webelement and return value
		public WebElement clickOnWebelement(String locatorKey) {
			WebElement filterWebElement = null;
			try {
				if (locatorKey.endsWith("_ID")) {
					filterWebElement = driver.findElement((By.id(OR.getProperty(locatorKey))));

				} else if (locatorKey.endsWith("_XPATH")) {
					filterWebElement = driver.findElement((By.xpath(OR.getProperty(locatorKey))));
				} else if (locatorKey.endsWith("_CSS")) {
					filterWebElement = driver.findElement((By.cssSelector(OR.getProperty(locatorKey))));
				}
				log.info("Element " + locatorKey + " is presence on webpage");
				Allure.step("Element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "" + " is presence on webpage"),
						Status.PASSED);
			} catch (Throwable t) {
				log.info("Error while handling Element " + locatorKey);
				Allure.step("Error while handling Element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""), Status.FAILED);

			}
			return filterWebElement;

		}

		// method for scroll Left
		public void scrollLeft() throws InterruptedException {
			WebElement lefttoright = wait
					.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='nw_FilterScroll']")));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollLeft = arguments[0].scrollWidth;", lefttoright);
			Thread.sleep(5000);
		}

	// method for press End Keys Using Actions
		public void pressEndKeysUsingActions() {
			Actions actions = new Actions(driver);
			actions.sendKeys(Keys.END).perform();
		}

		public void explicitWaitWithinvisibilityOfElementLocated(String locatorKey) {
			try {
				if (locatorKey.endsWith("_ID")) {
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(OR.getProperty(locatorKey))));

				} else if (locatorKey.endsWith("_XPATH")) {
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(OR.getProperty(locatorKey))));
				} else if (locatorKey.endsWith("_CSS")) {
					wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))));
				}
				log.info("Element " + locatorKey + " is invisible on webpage");
				Allure.step("Element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "" + " is invisible on webpage"),
						Status.PASSED);

			} catch (Throwable t) {
				log.info("Error while invisible of element " + locatorKey);
				Allure.step("Error while invisible of element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.FAILED);

			}
		}
		
		public void fluentWaitForInvisibilityOfElementLocated(String locatorKey) {
		    try {
		        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
		            .withTimeout(Duration.ofMinutes(10))  // maximum time to wait
		            .pollingEvery(Duration.ofSeconds(2)) // polling interval
		            .ignoring(NoSuchElementException.class); // exceptions to ignore

		        if (locatorKey.endsWith("_ID")) {
		            fluentWait.until(ExpectedConditions.invisibilityOfElementLocated(By.id(OR.getProperty(locatorKey))));
		        } else if (locatorKey.endsWith("_XPATH")) {
		            fluentWait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(OR.getProperty(locatorKey))));
		        } else if (locatorKey.endsWith("_CSS")) {
		            fluentWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))));
		        }
		        
		        log.info("Element " + locatorKey + " is invisible on webpage");
		        Allure.step("Element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "" + " is invisible on webpage"),
		                Status.PASSED);

		    } catch (Throwable t) {
		        log.info("Error while waiting for invisibility of element " + locatorKey);
		        Allure.step("Error while waiting for invisibility of element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
		                Status.FAILED);
		    }
		}

		// calender handle based on previous month
		public void handleCalenderOnTroubleTicket(int numberOFMonth) throws InterruptedException, TimeoutException {
			Thread.sleep(2000);
			click("dateOnCalender_ID");
			for (int i = 1; i <= numberOFMonth; i++) {
				explicitWaitWithClickable("backMonthOnCalender_XPATH");
				Thread.sleep(1000);
			}
			handleClickByJS("selectHourOnCalender_XPATH");
			explicitWaitWithClickable("filterReport_XPATH");
			explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
			pressEndKeysUsingActions();
			Thread.sleep(2000);

		}

		//Switch to default content iFrame
		public void switchToDefaultContentFromIframe() {
			driver.switchTo().defaultContent();
		}

		// rename Downloaded File with new Name
		public void renameDownloadedFile(String originalPrefix, String newNameBase) throws TimeoutException {
			// Construct the full path to downloadExcel directory
			String basePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
			File downloadDirectory = new File(basePath + "downloadExcel\\");

			// Ensure the downloadExcel directory exists
			if (!downloadDirectory.exists()) {
				downloadDirectory.mkdirs();
			}

			// Setup FluentWait
			Wait<File> wait = new FluentWait<>(downloadDirectory).withTimeout(Duration.ofSeconds(1))
					.pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchFileException.class);

			try {
				// Wait for file to appear with the given prefix
				File downloadedFile = wait.until(dir -> {
					File[] files = dir.listFiles((d, name) -> name.startsWith(originalPrefix));
					if (files != null && files.length > 0) {
						return files[0];
					}
					return null;
				});

				// Get the file's last modified timestamp
				long lastModified = downloadedFile.lastModified();
				Date fileDate = new Date(lastModified);

				// Create date format for the filename (dd-MMM-yyyy_HH-mm-ss)
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss", Locale.ENGLISH);
				String dateTimeString = dateFormat.format(fileDate);

				// Construct new filename with timestamp and .csv extension
				String newFileName = newNameBase + "_" + dateTimeString + ".csv";
				File newFile = new File(downloadDirectory, newFileName);

				// Delete if file with new name already exists
				if (newFile.exists()) {
					boolean deleted = wait.until(f -> f.delete());
					if (!deleted) {
						Allure.step("Failed to delete existing file: " + newFileName);
						return;
					}
				}

				// Rename the file with retry
				boolean renamed = wait.until(f -> {
					if (downloadedFile.renameTo(newFile)) {
						return true;
					}
					// Additional checks if needed
					return false;
				});

				if (renamed) {
					Allure.step("File renamed successfully from " + downloadedFile.getName() + " to " + newFileName);
				} else {
					Allure.step("Failed to rename file after multiple attempts");
				}

			} catch (Exception e) {
				Allure.step("Error occurred: " + e.getMessage());
			}
		}

		public void handleScroll(String locatorKey) throws InterruptedException {
			WebElement container = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locatorKey)));
			JavascriptExecutor js = (JavascriptExecutor) driver;

			js.executeScript("arguments[0].scrollTop += 500", container);

			Thread.sleep(2000);

		}

		// Method to get dynamic locator
		public static By getDynamicLocator(String locatorKey, int index) {
			try {
				String locatorTemplate = OR.getProperty(locatorKey);
				String dynamicXpath = locatorTemplate.replace("{index}", String.valueOf(index));
				Allure.step("Getting dynamic locator for key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.PASSED);
				return By.xpath(dynamicXpath);
			} catch (Exception e) {
				Allure.step("Failed to get dynamic locator for key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.FAILED);
				throw e;
			}
		}

		// Method to click on dynamic locator using indexing
		public void dynamicLocatorClick(String locatorKey, int index) {
			try {
				String locatorTemplate = OR.getProperty(locatorKey);
				String dynamicXpath = locatorTemplate.replace("{index}", String.valueOf(index));
				WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dynamicXpath)));
				element.click();
				Allure.step("Clicked on element with locator key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.PASSED);
			} catch (Exception e) {
				Allure.step("Failed to click on element with locator key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.FAILED);
				throw e;
			}
		}

		// Method to click on dynamic locator
		public void dynamicLocatorClick(String locatorKey, String RCACategory) {
			try {
				String locatorTemplate = OR.getProperty(locatorKey);
				String dynamicXpath = locatorTemplate.replace("{RCACategory}", String.valueOf(RCACategory));
				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dynamicXpath))).click();
				Allure.step("Clicked on element with locator key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "")
						+ " and RCACategory: " + RCACategory, Status.PASSED);
			} catch (Exception e) {
				Allure.step("Failed to click on element with locator key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "")
						+ " and RCACategory: " + RCACategory, Status.FAILED);
				throw e;
			}
		}

		// Method for getText value using dynamic locator
		public String dynamicLocatorGetText(String locatorKey, int index) {
			try {
				String locatorTemplate = OR.getProperty(locatorKey);
				String dynamicXpath = locatorTemplate.replace("{index}", String.valueOf(index));
				WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(dynamicXpath)));
				String text = element.getText();
				Allure.step("Got text: '" + text + "' from element with locator key: "
						+ locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""), Status.PASSED);
				return text;
			} catch (Exception e) {
				Allure.step("Failed to get text from element with locator key: "
						+ locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""), Status.FAILED);
				throw e;
			}
		}

		// Method for sendkeys value using dynamic locator
		public void dynamicLocatorSendKeys(String locatorKey, int index, String value) {
			try {
				String locatorTemplate = OR.getProperty(locatorKey);
				String dynamicXpath = locatorTemplate.replace("{index}", String.valueOf(index));
				WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dynamicXpath)));
				element.sendKeys(value);
				Allure.step("Sent keys: '" + value + "' to element with locator key: "
						+ locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""), Status.PASSED);
			} catch (Exception e) {
				Allure.step(
						"Failed to send keys to element with locator key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.FAILED);
				throw e;
			}
		}

		// verify show Hide Filter Scroll
		public void showHideFilterScroll(String locatorKey) throws InterruptedException {
			try {
				WebElement filterScroll = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(locatorKey)));
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("arguments[0].scrollLeft = arguments[0].scrollWidth;", filterScroll);
				Thread.sleep(5000);
				Allure.step("Performed scroll operation on element with locator: "
						+ locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""), Status.PASSED);
			} catch (Exception e) {
				Allure.step("Failed to perform scroll operation on element with locator: "
						+ locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""), Status.FAILED);
				throw e;
			}
		}

		// method for clear Text From InputBox
		public void clearTextFromInputBox(String locatorKey, int index) {
			try {
				String locatorTemplate = OR.getProperty(locatorKey);
				String dynamicXpath = locatorTemplate.replace("{index}", String.valueOf(index));
				driver.findElement(By.xpath(dynamicXpath)).clear();
				Allure.step(
						"Cleared text from input box with locator key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.PASSED);
			} catch (Exception e) {
				Allure.step("Failed to clear text from input box with locator key: "
						+ locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""), Status.FAILED);
				throw e;
			}
		}

		// method for click An Element In List
		public void clickAnElementInList(List<WebElement> list, int index) {
			try {
				list.get(index).click();
				Allure.step("Clicked on element at index: " + index + " in the list", Status.PASSED);
			} catch (Exception e) {
				Allure.step("Failed to click on element at index: " + index + " in the list", Status.FAILED);
				throw e;
			}
		}

		// Generic method for get Text From Input Box
		public String getTextFromInputBox(String locatorKey, String value) {

			try {
				if (locatorKey.endsWith("_ID")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.id(OR.getProperty(locatorKey))))
							.sendKeys(value);
				} else if (locatorKey.endsWith("_XPATH")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey))))
							.sendKeys(value);
				} else if (locatorKey.endsWith("_CSS")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))))
							.sendKeys(value);
				}
				log.info("Typing in " + locatorKey + " element and entered the value as " + value);
				Allure.step("Typing in " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "") + " element and entered the value",
						io.qameta.allure.model.Status.PASSED);
			} catch (NoSuchElementException e) {
				log.error("Element not found: " + locatorKey);
				Allure.step("Element not found: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						io.qameta.allure.model.Status.FAILED);
				e.printStackTrace();
			}
			return value;
		}

		// handle InputField By JS
		public void handleInputFieldByJS(String locatorKey, String Id) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			WebElement ticketid = wait
					.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey))));
			js.executeScript("arguments[0].value='" + Id + "'", ticketid);
		}

		// Send RIGHT arrow key multiple times
		public void scrollForAGGrid() {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			for (int i = 0; i < 40; i++) {
				js.executeScript("document.querySelector('.ag-body-viewport').scrollLeft += 20;");
				try {
					Thread.sleep(100); // slight pause to allow smooth scrolling (adjust if needed)
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt(); // handle the exception properly
				}
			}
		}

		// method for handle Calender On Trouble Ticket On Basis Of Month
		public void handleCalenderOnTroubleTicketOnBasisOfMonth(int numberOFMonth) throws InterruptedException {
			Thread.sleep(1000);
			explicitWaitWithClickable("dateOnCalender_ID");
			for (int i = 1; i <= numberOFMonth; i++) {
				explicitWaitWithClickable("backMonthOnCalender_XPATH");
				Thread.sleep(1000);
			}
			Thread.sleep(1000);
			handleClickByJS("selectHourOnCalender_XPATH");
		}

		// Read data based on column name from csv file
		public void readColumnData(String columnName, String expectedColumnValue, String renameFileName)
				throws FileNotFoundException, IOException {

			List<Object> columnData = new ArrayList<>();
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			File downloaddirectory = new File(userDir);
			String downloadDirectory = downloaddirectory + config.getProperty("downloadFileLocation");
			File directory = new File(downloadDirectory);
			File[] files = directory.listFiles((dir, name) -> name.startsWith(renameFileName));

			if (files == null || files.length == 0) {
				Allure.step("File with name starting with '" + renameFileName + "' not found", Status.FAILED);
				sa.fail("File not found");
				return;
			}

			String filePath = files[0].getAbsolutePath();
			System.out.println("Reading file: " + filePath);
			boolean allValuesMatch = true;

			try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
				// Skip any metadata line that does not contain real header
			}
		}

		// method for File Exists At Location Directory
		public boolean isFileExistsAtLocationDir(String fileNamePattern) throws TimeoutException {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String downloadedFileDirectory = Paths.get(userDir, "src", "main", "resources", "downloadExcel").toString();

			FluentWait<WebDriver> wait = new FluentWait<>(driver).withTimeout(Duration.ofMinutes(1))
					.pollingEvery(Duration.ofSeconds(10))
					.withMessage("File matching pattern '" + fileNamePattern + "' not found within timeout period");

			// Wait until a matching file is found
			wait.until(d -> {
				File dir = new File(downloadedFileDirectory);
				File[] matchingFiles = dir.listFiles((dir1, name) -> name.startsWith(fileNamePattern)
						|| name.contains(fileNamePattern) && name.endsWith(".csv"));

				return matchingFiles != null && matchingFiles.length > 0;
			});

			// If we get here, at least one matching file exists
			File dir = new File(downloadedFileDirectory);
			File[] matchingFiles = dir.listFiles((dir1, name) -> name.startsWith(fileNamePattern)
					|| name.contains(fileNamePattern) && name.endsWith(".csv"));

			if (matchingFiles != null && matchingFiles.length > 0) {
				String foundFiles = Arrays.stream(matchingFiles).map(File::getName).collect(Collectors.joining(", "));

				Allure.step(String.format("Found %d file(s) matching '%s'", matchingFiles.length, fileNamePattern));
				return true;
			}

			return false;
		}

		// verify is File Exists At Location Directory using soft assert
		public void verifyUsingAssertFileIsExistInLocation(String fileName) throws TimeoutException {
			SoftAssert sa = new SoftAssert();
			boolean FileDownloadedLocation = isFileExistsAtLocationDir(fileName);
			sa.assertTrue(FileDownloadedLocation);
		}

		// rename Downloaded File with new Name And Return Value
		public String renameDownloadedFileAndReturnValue(String originalPrefix, String newNameBase)
				throws TimeoutException {
			// Construct the full path to downloadExcel directory
			String basePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
			File downloadDirectory = new File(basePath + "downloadExcel\\");
			// Ensure the downloadExcel directory exists
			if (!downloadDirectory.exists()) {
				downloadDirectory.mkdirs();
			}

			// Setup FluentWait
			Wait<File> wait = new FluentWait<>(downloadDirectory).withTimeout(Duration.ofSeconds(30))
					.pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchFileException.class);

			try {
				// Wait for file to appear with the given prefix
				File downloadedFile = wait.until(dir -> {
					File[] files = dir.listFiles((d, name) -> name.startsWith(originalPrefix));
					if (files != null && files.length > 0) {
						return files[0];
					}
					return null;
				});

				// Get the file's last modified timestamp
				long lastModified = downloadedFile.lastModified();
				Date fileDate = new Date(lastModified);

				// Create date format for the filename (dd-MMM-yyyy_HH-mm-ss)
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss", Locale.ENGLISH);
				String dateTimeString = dateFormat.format(fileDate);

				// Construct new filename with timestamp and .csv extension
				String newFileName = newNameBase + "_" + dateTimeString + ".csv";
				File newFile = new File(downloadDirectory, newFileName);

				// Delete if file with new name already exists
				if (newFile.exists()) {
					boolean deleted = wait.until(f -> f.delete());
					if (!deleted) {
						Allure.step("Failed to delete existing file: " + newFileName);
						return newFile.getAbsolutePath(); // Return the path even if deletion failed
					}
				}

				// Rename the file with retry
				boolean renamed = wait.until(f -> {
					if (downloadedFile.renameTo(newFile)) {
						return true;
					}
					// Additional checks if needed
					return false;
				});

				if (renamed) {
					Allure.step("File renamed successfully from " + downloadedFile.getName() + " to " + newFileName);
					return newFile.getAbsolutePath(); // Return the path of the renamed file
				} else {
					Allure.step("Failed to rename file after multiple attempts");
					return downloadedFile.getAbsolutePath(); // Return original path if rename failed
				}

			} catch (Exception e) {
				Allure.step("Error occurred: " + e.getMessage());
				throw new TimeoutException("Failed to rename file: " + e.getMessage());
			}
		}

		// clean Allure Results
		@BeforeSuite
		public void cleanAllureResults() throws Exception {
			Path allureResults = Paths.get("allure-results");
			if (Files.exists(allureResults)) {
				Files.walk(allureResults).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
			}
		}

		// clean Reports
		@BeforeSuite
		public void cleanReports() throws IOException {
			Path reportsDir = Paths.get("Reports");

			if (Files.exists(reportsDir)) {
				// Delete all files and subdirectories inside "Reports"
				try (Stream<Path> paths = Files.walk(reportsDir, FileVisitOption.FOLLOW_LINKS)) {
					paths.filter(path -> !path.equals(reportsDir)) // Skip the parent directory
							.sorted(Comparator.reverseOrder()) // Delete deepest files first
							.forEach(path -> {
								try {
									Files.delete(path); // Delete file or empty directory
								} catch (IOException e) {
									System.err.println("Failed to delete: " + path);
									e.printStackTrace();
								}
							});
				}
			}
		}

		// clean Download Excel File
		@BeforeSuite
		public void cleanDownloadExcelFile() throws IOException {
			Path reportsDir = Paths.get("src\\main\\resources\\downloadExcel");

			if (Files.exists(reportsDir)) {
				// Delete all files and subdirectories inside "Reports"
				try (Stream<Path> paths = Files.walk(reportsDir, FileVisitOption.FOLLOW_LINKS)) {
					paths.filter(path -> !path.equals(reportsDir)) // Skip the parent directory
							.sorted(Comparator.reverseOrder()) // Delete deepest files first
							.forEach(path -> {
								try {
									Files.delete(path); // Delete file or empty directory
								} catch (IOException e) {
									System.err.println("Failed to delete: " + path);
									e.printStackTrace();
								}
							});
				}
			}
		}

		// handle click by JS
		public void handleClickByJS(String locatorKey) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			try {
				if (locatorKey.endsWith("_XPATH")) {
					WebElement element = wait
							.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey))));
					js.executeScript("arguments[0].click();", element);

				} else if (locatorKey.endsWith("_ID")) {
					WebElement element = wait
							.until(ExpectedConditions.presenceOfElementLocated(By.id(OR.getProperty(locatorKey))));
					js.executeScript("arguments[0].click();", element);

				} else if (locatorKey.endsWith("_CSS")) {
					WebElement element = wait
							.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))));
					js.executeScript("arguments[0].click();", element);

				}
				log.info("Clicking on " + locatorKey);
				Allure.step("Clicking on " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						io.qameta.allure.model.Status.PASSED);
			} catch (NoSuchElementException e) {
				log.error("The element " + locatorKey + " is not available to click");
				Allure.step("The element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "") + " is not available to click",
						io.qameta.allure.model.Status.FAILED);
			}
		}

		// press Enter Key Using Actions
		public void pressEnterKeyUsingActions() {
			Actions actions = new Actions(driver);
			actions.sendKeys(Keys.ENTER).perform();
			;
		}

		// Scrolling till element is visible over the screen
		public void performScrolling() {
			JavascriptExecutor js = (JavascriptExecutor) driver;

			WebElement scrollelement = driver.findElement(By.xpath("//div[@id='divExport']"));

			js.executeScript("arguments[0].scrollIntoView();", scrollelement);

		}

		// Method to getDomProperty text
		public String getDomProperty(String locatorKey, String value) {
			String text = null;
			try {
				if (locatorKey.endsWith("_ID")) {
					text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(OR.getProperty(locatorKey))))
							.getDomProperty(value);
				} else if (locatorKey.endsWith("_XPATH")) {
					text = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OR.getProperty(locatorKey))))
							.getDomProperty(value);
				} else if (locatorKey.endsWith("_CSS")) {
					text = wait
							.until(ExpectedConditions
									.visibilityOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))))
							.getDomProperty(value);
				}
				log.info("Value available as text : " + text);
				Allure.step("Value available as text : " + text, io.qameta.allure.model.Status.PASSED);

				return text;
			} catch (Throwable t) {
				log.info("Value " + locatorKey + " is not available on page");
				Allure.step("Value " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "") + " is not available on page",
						io.qameta.allure.model.Status.FAILED);
				return text;
			}
		}

		// handle Calender On Access Managment
		public void handleCalenderOnAccessManagment(int numberOFMonth, int day)
				throws InterruptedException, TimeoutException {
			Thread.sleep(3000);
			click("startDateAccess_XPATH");
			for (int i = 1; i <= numberOFMonth; i++) {
				explicitWaitWithClickable("previousButtonCalendar_CSS");
				Thread.sleep(1000);
				// click("dateday1_XPATH");
				driver.findElement(By.xpath("//*[@id='ui-datepicker-div']/table/tbody/tr/td/a[text()='" + day + "']"))
						.click();

			}

		}

		// verify click On Day functionalities for calender
		public void clickOnDayWithOutTimeslot(int day) throws InterruptedException, TimeoutException {

			click("endDate_XPATH");
			click("previousButtonCalendar_CSS");
			Thread.sleep(3000);
			driver.findElement(By.xpath("//*[@id='ui-datepicker-div']/table/tbody/tr/td/a[text()='" + day + "']")).click();
		}

		public void clearTextFromInputBox(String locatorKey) {
			try {
				String locatorTemplate = OR.getProperty(locatorKey);

				driver.findElement(By.xpath(locatorTemplate)).clear();
				Allure.step(
						"Cleared text from input box with locator key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.PASSED);
			} catch (Exception e) {
				Allure.step("Failed to clear text from input box with locator key: "
						+ locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""), Status.FAILED);
				throw e;
			}
		}

		// Scrolling till element is visible over the screen
		public void performScrolling(String locatorKey) {
			JavascriptExecutor js = (JavascriptExecutor) driver;
			if (locatorKey.endsWith("_XPATH")) {
				WebElement scrollelement = wait
						.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey))));
				js.executeScript("arguments[0].scrollIntoView();", scrollelement);

			} else if (locatorKey.endsWith("_ID")) {
				WebElement scrollelement = wait
						.until(ExpectedConditions.presenceOfElementLocated(By.id(OR.getProperty(locatorKey))));
				js.executeScript("arguments[0].scrollIntoView();", scrollelement);

			} else if (locatorKey.endsWith("_CSS")) {
				WebElement scrollelement = wait
						.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))));
				js.executeScript("arguments[0].scrollIntoView();", scrollelement);

			}
		}

		@AfterSuite
		public void generateStaticAllureReport() {
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			Path reportDir = Paths.get("allure-reports", "Automation_Report__" + timestamp).toAbsolutePath();
			Path resultsDir = Paths.get("allure-results").toAbsolutePath();

			try {
				// Verify results exist
				if (!Files.exists(resultsDir) || Files.list(resultsDir).count() == 0) {
					System.err.println("ERROR: No Allure results found");
					return;
				}

				// Generate report
				String allureCmd = System.getProperty("os.name").toLowerCase().contains("win") ? "allure.bat" : "allure";

				System.out.println("Generating Allure report...");

				// Generate report with single-file option
				ProcessBuilder generateProcess = new ProcessBuilder(allureCmd, "generate", resultsDir.toString(),
						"--output", reportDir.toString(), "--clean", "--single-file" // This creates a more portable report
				);
				generateProcess.redirectErrorStream(true);

				Process generate = generateProcess.start();
				printProcessOutput(generate, "[ALLURE GENERATE]");

				if (!generate.waitFor(2, TimeUnit.MINUTES)) {
					generate.destroy();
					System.err.println("Timeout during report generation");
					return;
				}

				if (generate.exitValue() == 0) {
					// Zip the report for easy emailing
					Path zipFile = zipReportDirectory(reportDir);
					System.out.println("SUCCESS: Static report generated and zipped at " + zipFile);

					// Open the report in default browser
					openReportInBrowser(reportDir);
				} else {
					System.err.println("ERROR: Report generation failed");
				}
			} catch (Exception e) {
				System.err.println("ERROR during report generation: " + e.getMessage());
				e.printStackTrace();
			}
		}

		private void openReportInBrowser(Path reportDir) {
			try {
				Path indexHtml = reportDir.resolve("index.html");

				if (Files.exists(indexHtml)) {
					System.out.println("Opening report in default browser...");

					if (Desktop.isDesktopSupported()) {
						Desktop desktop = Desktop.getDesktop();
						if (desktop.isSupported(Desktop.Action.BROWSE)) {
							desktop.browse(indexHtml.toUri());
						} else {
							System.err.println("Browse action not supported on this platform");
							openFallbackWay(indexHtml);
						}
					} else {
						System.err.println("Desktop not supported, trying fallback method");
						openFallbackWay(indexHtml);
					}
				} else {
					System.err.println("Report index.html not found at: " + indexHtml);
				}
			} catch (Exception e) {
				System.err.println("Failed to open report in browser: " + e.getMessage());
			}
		}

		private void openFallbackWay(Path indexHtml) {
			// Try alternative methods if Desktop.browse() doesn't work
			try {
				String os = System.getProperty("os.name").toLowerCase();

				if (os.contains("win")) {
					// Windows
					Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", indexHtml.toString() });
				} else if (os.contains("mac")) {
					// MacOS
					Runtime.getRuntime().exec(new String[] { "open", indexHtml.toString() });
				} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
					// Linux/Unix
					Runtime.getRuntime().exec(new String[] { "xdg-open", indexHtml.toString() });
				} else {
					System.err.println("Unsupported OS for automatic report opening");
				}
			} catch (IOException e) {
				System.err.println("Fallback method failed: " + e.getMessage());
			}
		}

		// Your existing zipReportDirectory and printProcessOutput methods remain the
		// same

		private Path zipReportDirectory(Path reportDir) throws IOException {
			Path zipFile = Paths.get(reportDir.getParent().toString(), reportDir.getFileName() + ".zip");

			try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile));
					Stream<Path> paths = Files.walk(reportDir)) {

				paths.filter(path -> !Files.isDirectory(path)).forEach(path -> {
					ZipEntry zipEntry = new ZipEntry(reportDir.relativize(path).toString());
					try {
						zos.putNextEntry(zipEntry);
						Files.copy(path, zos);
						zos.closeEntry();
					} catch (IOException e) {
						System.err.println("Error zipping file: " + e.getMessage());
					}
				});
			}
			return zipFile;
		}

		public void printProcessOutput(Process process, String prefix) {
			// Create threads to read both input and error streams
			Thread inputThread = new Thread(() -> {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						System.out.println(prefix + " [OUT] " + line);
					}
				} catch (IOException e) {
					System.err.println("Error reading process output: " + e.getMessage());
				}
			});

			Thread errorThread = new Thread(() -> {
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						System.err.println(prefix + " [ERR] " + line);
					}
				} catch (IOException e) {
					System.err.println("Error reading process error: " + e.getMessage());
				}
			});

			// Start both threads
			inputThread.start();
			errorThread.start();

			// Wait for both threads to complete
			try {
				inputThread.join();
				errorThread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.err.println("Thread interrupted while reading process output");
			}
		}

		@AfterSuite
		// backups of all downloaded files
		public void backupAllFiles() throws IOException {
			// Set base paths
			String basePath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
					+ File.separator + "resources" + File.separator;
			File downloadDirectory = new File(basePath + "downloadExcel");

			// Verify source directory exists
			if (!downloadDirectory.exists() || !downloadDirectory.isDirectory()) {
				throw new IOException("Source directory not found: " + downloadDirectory.getAbsolutePath());
			}

			// Create main backup folder if it doesn't exist
			File mainBackupDir = new File(downloadDirectory.getParent(), "downloadExcel_backups");
			if (!mainBackupDir.exists() && !mainBackupDir.mkdirs()) {
				throw new IOException("Failed to create main backup directory: " + mainBackupDir.getAbsolutePath());
			}

			// Create timestamped subfolder for this run
			String timeStamp = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss").format(new java.util.Date());
			File backupDir = new File(mainBackupDir, timeStamp);

			if (!backupDir.mkdirs()) {
				throw new IOException("Failed to create timestamped backup directory: " + backupDir.getAbsolutePath());
			}

			// Get all files in source directory
			File[] filesToBackup = downloadDirectory.listFiles(File::isFile);

			if (filesToBackup == null || filesToBackup.length == 0) {
				return;
			}

			// Copy each file
			for (File sourceFile : filesToBackup) {
				String fileName = sourceFile.getName();
				Path sourcePath = sourceFile.toPath();
				Path targetPath = Paths.get(backupDir.getAbsolutePath(), fileName);

				try {
					Files.copy(sourcePath, targetPath);
				} catch (IOException e) {
					throw e;
				}
			}

		}

		// Generic method to enter text into an input box
		public void enterTextIntoInputBoxUsingActionsClass(String locatorKey, String value) {
			Actions act = new Actions(driver);
			try {
				if (locatorKey.endsWith("_ID")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.id(OR.getProperty(locatorKey)))).click();
					act.sendKeys(value).perform();
				} else if (locatorKey.endsWith("_XPATH")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey)))).click();
					act.sendKeys(value).perform();
				} else if (locatorKey.endsWith("_CSS")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))))
							.click();
					act.sendKeys(value).perform();
				}
				log.info("Typing in " + locatorKey + " element and entered the value as " + value);
				Allure.step("Typing in " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "") + " element and entered the value",
						io.qameta.allure.model.Status.PASSED);
			} catch (NoSuchElementException e) {
				log.error("Element not found: " + locatorKey);
				Allure.step("Element not found: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						io.qameta.allure.model.Status.FAILED);
				e.printStackTrace();
			}

		}

		// method for upload The xlsx File
		public void uploadThexlsxFile(String dataUpload) throws InterruptedException {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String fileLocation = userDir + getUploadFileLocation() + dataUpload + ".xlsx";
			uploadExcel("document_Upload_XPATH", fileLocation);
			Allure.step(dataUpload + " xlsx file uploaded.");
		}

		// method for upload The png File
		public void uploadThepngFile(String dataUpload) throws InterruptedException {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String fileLocation = userDir + getUploadFileLocation() + dataUpload + ".png";
			uploadExcel("document_Upload_XPATH", fileLocation);
			Allure.step(dataUpload + " png file uploaded.");
		}

		// method for upload The jpg File
		public void uploadThejpgFile(String dataUpload) throws InterruptedException {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String fileLocation = userDir + getUploadFileLocation() + dataUpload + ".jpg";
			uploadExcel("document_Upload_XPATH", fileLocation);
			Allure.step(dataUpload + " jpg file uploaded.");
		}

		// method for upload The pdf File
		public void uploadThepdfFile(String dataUpload) throws InterruptedException {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String fileLocation = userDir + getUploadFileLocation() + dataUpload + ".pdf";
			uploadExcel("document_Upload_XPATH", fileLocation);
			Allure.step(dataUpload + " pdf file uploaded.");
		}

		// method for upload The docx File
		public void uploadThedocxFile(String dataUpload) throws InterruptedException {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String fileLocation = userDir + getUploadFileLocation() + dataUpload + ".docx";
			uploadExcel("document_Upload_XPATH", fileLocation);
			Allure.step(dataUpload + " docx file uploaded.");
		}

		// Method to click using dynamic locator
		public String clickUsingDynamicLocator(String locatorKey, String dynamicValue) {
			try {
				String locatorTemplate = OR.getProperty(locatorKey);
				// Find the placeholder between curly braces
				String placeholder = locatorTemplate.replaceAll(".*\\{(.*?)\\}.*", "$1");
				String dynamicXpath = locatorTemplate.replace("{" + placeholder + "}", dynamicValue);

				wait.until(ExpectedConditions.elementToBeClickable(By.xpath(dynamicXpath))).click();
				Allure.step("Clicked on element with locator key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "")
						+ " and dynamicValue: " + dynamicValue, Status.PASSED);
			} catch (Exception e) {
				Allure.step("Failed to click on element with locator key: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "")
						+ " and dynamicValue: " + dynamicValue, Status.FAILED);
				throw e;
			}
			return dynamicValue;
		}

		// click On Suggested Value
		public void clickOnSuggestedValue() {
			try {
				click("click_On_Suggestion_XPATH");
			} catch (Exception e) {
			}
		}

		// handle Operator
		public void handleOperator() {
			Actions act = new Actions(driver);
			act.sendKeys(Keys.TAB).perform();
			act.sendKeys(Keys.TAB).perform();
			act.sendKeys(Keys.ENTER).perform();
			act.sendKeys(Keys.TAB).perform();
		}

	//logout from system
		public void performLogout() throws InterruptedException, TimeoutException {
			Thread.sleep(3000);
			handleClickByJS("login_User_XPATH");
			click("signOut_XPATH");
			click("logOut_XPATH");

		}

		// switch to user
		public void switchToUser() throws TimeoutException {
			try {
				handleClickByJS("remove_Popup_Error_XPATH");
			} catch (Exception e) {
				//
			}
			handleClickByJS("iTower_Modules_XPATH");
			click("click_On_ONM_Module_XPATH");
		}

		// out from login
		public void outFromLogin() {
			
			handleClickByJS("click_On_Assigned_User_CSS");
			handleClickByJS("click_On_Logout_XPATH");
		}

		public void closeTheOpenTab() {
			Actions act = new Actions(driver);
			act.sendKeys(Keys.TAB).perform();
		}

		// Method for handle List Of Webelement And Enter Value
		public void handleListOfWebelementAndEnterValue(String locatorKey, String value) {
			try {
				List<WebElement> element = clickOnListOfWebelement(locatorKey);
				if (locatorKey.endsWith("_ID")) {
					element = wait
							.until(ExpectedConditions.presenceOfAllElementsLocatedBy((By.id(OR.getProperty(locatorKey)))));
				} else if (locatorKey.endsWith("_XPATH")) {
					element = wait.until(
							ExpectedConditions.presenceOfAllElementsLocatedBy((By.xpath(OR.getProperty(locatorKey)))));
				} else if (locatorKey.endsWith("_CSS")) {
					element = wait.until(ExpectedConditions
							.presenceOfAllElementsLocatedBy((By.cssSelector(OR.getProperty(locatorKey)))));
				}
				for (WebElement elementType : element) {
					String getValue = elementType.getText();
					if (getValue.equalsIgnoreCase(value)) {
						elementType.click();
						break;
					}
				}
				log.info("Typing in " + locatorKey + " element and entered the value as " + value);
				Allure.step("Typing in " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "") + " element and entered the value",
						io.qameta.allure.model.Status.PASSED);
			} catch (NoSuchElementException e) {
				log.error("Element not found: " + locatorKey);
				Allure.step("Element not found: " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						io.qameta.allure.model.Status.FAILED);
				e.printStackTrace();
			}

		}

		// select Today Date
		public void selectTodayDate(int numberOFMonth) throws InterruptedException {
			for (int i = 1; i <= numberOFMonth; i++) {
				explicitWaitWithClickable("next_Year_CSS");
				Thread.sleep(1000);
			}
			int todayDayOfMonth = LocalDate.now().getDayOfMonth();
			wait.until(ExpectedConditions
					.presenceOfElementLocated(By.xpath("//a[normalize-space()='" + todayDayOfMonth + "']"))).click();

		}

		// type text into alert
		public void typesIntoAnAlert(String messsage) {
			driver.switchTo().alert().sendKeys(messsage);
			log.info("Types into an alert: " + messsage);
			Allure.step("Types into an alert: " + messsage);
		}

		// rename Downloaded File
		public String renameDownloadedFileBY(String originalPrefix, String newNameBase) throws TimeoutException {
			// Construct the full path to downloadExcel directory
			String basePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
			File downloadDirectory = new File(basePath + "downloadExcel\\");

			// Ensure the downloadExcel directory exists
			if (!downloadDirectory.exists()) {
				downloadDirectory.mkdirs();
			}

			// Setup FluentWait
			Wait<File> wait = new FluentWait<>(downloadDirectory).withTimeout(Duration.ofSeconds(30))
					.pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchFileException.class);

			try {
				// Wait for file to appear with the given prefix
				File downloadedFile = wait.until(dir -> {
					File[] files = dir.listFiles((d, name) -> name.startsWith(originalPrefix));
					if (files != null && files.length > 0) {
						return files[0];
					}
					return null;
				});

				// Get the file's last modified timestamp
				long lastModified = downloadedFile.lastModified();
				Date fileDate = new Date(lastModified);

				// Create date format for the filename (dd-MMM-yyyy_HH-mm-ss)
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss", Locale.ENGLISH);
				String dateTimeString = dateFormat.format(fileDate);

				// Construct new filename with timestamp and .csv extension
				String newFileName = newNameBase + "_" + dateTimeString + ".csv";
				File newFile = new File(downloadDirectory, newFileName);

				// Delete if file with new name already exists
				if (newFile.exists()) {
					boolean deleted = wait.until(f -> f.delete());
					if (!deleted) {
						Allure.step("Failed to delete existing file: " + newFileName);
						return newFile.getAbsolutePath(); // Return the path even if deletion failed
					}
				}

				// Rename the file with retry
				boolean renamed = wait.until(f -> {
					if (downloadedFile.renameTo(newFile)) {
						return true;
					}
					// Additional checks if needed
					return false;
				});

				if (renamed) {
					Allure.step("File renamed successfully from " + downloadedFile.getName() + " to " + newFileName);
					return newFile.getAbsolutePath(); // Return the path of the renamed file
				} else {
					Allure.step("Failed to rename file after multiple attempts");
					return downloadedFile.getAbsolutePath(); // Return original path if rename failed
				}

			} catch (Exception e) {
				Allure.step("Error occurred: " + e.getMessage());
				throw new TimeoutException("Failed to rename file: " + e.getMessage());
			}
		}

		// handle calender
		public void handleCalenderOnScheduledActivityPage(int numberOFMonth, String monthDay) throws InterruptedException {
			handleClickByJS("calender_On_Scheduled_Activity_XPATH");
			for (int i = 1; i <= numberOFMonth; i++) {
				handleClickByJS("back_Month_On_Scheduled_Activity_XPATH");
				Thread.sleep(1000);
			}
			Thread.sleep(2000);
			wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
					"//tbody[@id='ctl00_CPH1_TC1_TabPanel4_CalendarExtender1_daysBody']/tr//td/div[contains(@title, '"
							+ monthDay + "')]")))
					.click();
		}

		// rename Downloaded Excel File
		public void renameDownloadedExcelFile(String originalPrefix, String newNameBase) throws TimeoutException {
			// Construct the full path to downloadExcel directory
			String basePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
			File downloadDirectory = new File(basePath + "downloadExcel\\");

			// Ensure the downloadExcel directory exists
			if (!downloadDirectory.exists()) {
				downloadDirectory.mkdirs();
			}

			// Setup FluentWait
			Wait<File> wait = new FluentWait<>(downloadDirectory).withTimeout(Duration.ofSeconds(30))
					.pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchFileException.class);

			try {
				// Wait for file to appear with the given prefix
				File downloadedFile = wait.until(dir -> {
					File[] files = dir.listFiles((d, name) -> name.startsWith(originalPrefix));
					if (files != null && files.length > 0) {
						return files[0];
					}
					return null;
				});

				// Construct new filename with .csv extension (without date)
				String newFileName = newNameBase + ".xlsx";
				File newFile = new File(downloadDirectory, newFileName);

				// Delete if file with new name already exists
				if (newFile.exists()) {
					boolean deleted = wait.until(f -> f.delete());
					if (!deleted) {
						Allure.step("Failed to delete existing file: " + newFileName);
						return;
					}
				}

				// Rename the file with retry
				boolean renamed = wait.until(f -> {
					if (downloadedFile.renameTo(newFile)) {
						return true;
					}
					// Additional checks if needed
					return false;
				});

				if (renamed) {
					Allure.step("File renamed successfully from " + downloadedFile.getName() + " to " + newFileName);
				} else {
					Allure.step("Failed to rename file after multiple attempts");
				}

			} catch (Exception e) {
				Allure.step("Error occurred: " + e.getMessage());
			}
		}

		// rename Downloaded Excel File for all . extension file
		public void renameDownloadedFile(String originalPrefix, String newNameBase, String fileExtension)
				throws TimeoutException {
			// Construct the full path to download directory
			String basePath = System.getProperty("user.dir") + "\\src\\main\\resources\\";
			File downloadDirectory = new File(basePath + "downloadExcel\\"); // Changed from downloadExcel to more generic
																				// "downloads"

			// Ensure the download directory exists
			if (!downloadDirectory.exists()) {
				downloadDirectory.mkdirs();
			}

			// Setup FluentWait
			Wait<File> wait = new FluentWait<>(downloadDirectory).withTimeout(Duration.ofSeconds(30))
					.pollingEvery(Duration.ofSeconds(1)).ignoring(NoSuchFileException.class);

			try {
				// Wait for file to appear with the given prefix
				File downloadedFile = wait.until(dir -> {
					File[] files = dir.listFiles((d, name) -> name.startsWith(originalPrefix));
					if (files != null && files.length > 0) {
						return files[0];
					}
					return null;
				});

				// Get the original file extension if not provided
				String extensionToUse = fileExtension;
				if (extensionToUse == null || extensionToUse.isEmpty()) {
					String originalName = downloadedFile.getName();
					int lastDotIndex = originalName.lastIndexOf('.');
					if (lastDotIndex > 0) {
						extensionToUse = originalName.substring(lastDotIndex);
					} else {
						extensionToUse = ""; // no extension
					}
				} else if (!extensionToUse.startsWith(".")) {
					extensionToUse = "." + extensionToUse;
				}

				// Construct new filename with proper extension
				String newFileName = newNameBase + extensionToUse;
				File newFile = new File(downloadDirectory, newFileName);

				// Delete if file with new name already exists
				if (newFile.exists()) {
					boolean deleted = wait.until(f -> f.delete());
					if (!deleted) {
						Allure.step("Failed to delete existing file: " + newFileName);
						return;
					}
				}

				// Rename the file with retry
				boolean renamed = wait.until(f -> {
					if (downloadedFile.renameTo(newFile)) {
						return true;
					}
					// Additional checks if needed
					return false;
				});

				if (renamed) {
					Allure.step("File renamed successfully from " + downloadedFile.getName() + " to " + newFileName);
				} else {
					Allure.step("Failed to rename file after multiple attempts");
				}

			} catch (Exception e) {
				Allure.step("Error occurred: " + e.getMessage());
			}
		}

		// upload xlsx file
		public void uploadTheExcelFileFromUploadLocation(String locatorKey, String dataUpload) throws InterruptedException {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String fileLocation = userDir + getUploadFileLocation() + dataUpload + ".xlsx";
			uploadExcel(locatorKey, fileLocation);
			Allure.step(dataUpload + " xlsx file uploaded.");
			Thread.sleep(3000);
		}

		// upload All PNG Files From Folder
		public void uploadAllPNGFilesFromFolder(String folderName, String locatorKey) throws InterruptedException {
			    // Get absolute path to the folder
			    String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			    Path folderPath = Paths.get(userDir + getUploadFileLocation() + folderName);

			    // Verify folder exists
			    if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
			        throw new RuntimeException("Folder not found: " + folderPath);
			    }

			    // Get all files from the folder (regardless of extension)
			    List<String> filePaths;
			    try {
			        filePaths = Files.walk(folderPath)
			                .filter(Files::isRegularFile)
			                .map(Path::toString)
			                .collect(Collectors.toList());
			    } catch (Exception e) {
			        throw new RuntimeException("Error reading files from folder", e);
			    }

			    if (filePaths.isEmpty()) {
			        Allure.step("No files found in folder: " + folderName);
			        return;
			    }

			    // Join all file paths with newline character
			    String allFiles = String.join("\n", filePaths);

			    // Upload all files
			    uploadExcel(locatorKey, allFiles);

			    Allure.step("Uploaded " + filePaths.size() + " files from folder: " + folderName);
			    Thread.sleep(1000); // Consider replacing with explicit wait
			}

		// handle Calender For Site Activity
		public void handleCalenderForSiteActivity() throws TimeoutException {
			click("click_Calender_XPATH");
			while (true) {
				String monthYear = driver.findElement(By.className("ui-datepicker-title")).getText();

				if (monthYear.equals("November 2025")) {
					break;
				} else {
					driver.findElement(By.xpath("//span[text()='Prev']")).click();
				}
			}
			driver.findElement(By.xpath("//a[text()='14']")).click();
		}

		// handle scroll functionalities On Site Activity page
		public void handleScrollSiteActivity() throws InterruptedException {
			WebElement container = driver.findElement(By.className("ag-body-viewport"));
			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", container);
			for (int i = 0; i < 5; i++) {
				js.executeScript("arguments[0].scrollTop += 100", container);
				Thread.sleep(500);
			}
		}

		// handle Calender
		public void handleCalender(int numberOFMonth, int dateOnMonth) throws InterruptedException {
			for (int i = 1; i <= numberOFMonth; i++) {
				explicitWaitWithClickable("next_Year_CSS");
				Thread.sleep(1000);
			}
			wait.until(
					ExpectedConditions.presenceOfElementLocated(By.xpath("//a[normalize-space()='" + dateOnMonth + "']")))
					.click();

		}

		// verify is xlsx File Exists At Location Directory using soft assert
		public void verifyUsingAssertXLSXFileIsExistInLocation(String fileName) throws TimeoutException {
			SoftAssert sa = new SoftAssert();
			boolean FileDownloadedLocation = isXLSXExistsAtLocation(fileName);
			sa.assertTrue(FileDownloadedLocation);
		}

		// verify is pdf File Exists At Location Directory using soft assert
		public void verifyUsingAssertPDFFileIsExistInLocation(String fileName) throws TimeoutException {
			SoftAssert sa = new SoftAssert();
			boolean FileDownloadedLocation = isPDFExistsAtLocation(fileName);
			sa.assertTrue(FileDownloadedLocation);
		}

		// method for is File Exists At Location
		public boolean isPDFExistsAtLocation(String fileNamePattern) throws TimeoutException {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String downloadedFileDirectory = Paths.get(userDir, "src", "main", "resources", "downloadExcel").toString();

			FluentWait<WebDriver> wait = new FluentWait<>(driver).withTimeout(Duration.ofMinutes(1))
					.pollingEvery(Duration.ofSeconds(2))
					.withMessage("File matching pattern '" + fileNamePattern + "' not found within timeout period");

			// Wait until a matching file is found
			wait.until(d -> {
				File dir = new File(downloadedFileDirectory);
				File[] matchingFiles = dir.listFiles((dir1, name) -> name.startsWith(fileNamePattern)
						|| name.contains(fileNamePattern) && name.endsWith(".pdf"));

				return matchingFiles != null && matchingFiles.length > 0;
			});

			// If we get here, at least one matching file exists
			File dir = new File(downloadedFileDirectory);
			File[] matchingFiles = dir.listFiles((dir1, name) -> name.startsWith(fileNamePattern)
					|| name.contains(fileNamePattern) && name.endsWith(".pdf"));

			if (matchingFiles != null && matchingFiles.length > 0) {
				String foundFiles = Arrays.stream(matchingFiles).map(File::getName).collect(Collectors.joining(", "));

				Allure.step(String.format("Found %d file(s) matching '%s'", matchingFiles.length, fileNamePattern));
				return true;
			}

			return false;
		}

		// method for is File Exists At Location
		public boolean isXLSXExistsAtLocation(String fileNamePattern) throws TimeoutException {
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			String downloadedFileDirectory = Paths.get(userDir, "src", "main", "resources", "downloadExcel").toString();

			FluentWait<WebDriver> wait = new FluentWait<>(driver).withTimeout(Duration.ofMinutes(1))
					.pollingEvery(Duration.ofSeconds(2))
					.withMessage("File matching pattern '" + fileNamePattern + "' not found within timeout period");

			// Wait until a matching file is found
			wait.until(d -> {
				File dir = new File(downloadedFileDirectory);
				File[] matchingFiles = dir.listFiles((dir1, name) -> name.startsWith(fileNamePattern)
						|| name.contains(fileNamePattern) && name.endsWith(".xlsx"));

				return matchingFiles != null && matchingFiles.length > 0;
			});

			// If we get here, at least one matching file exists
			File dir = new File(downloadedFileDirectory);
			File[] matchingFiles = dir.listFiles((dir1, name) -> name.startsWith(fileNamePattern)
					|| name.contains(fileNamePattern) && name.endsWith(".xlsx"));

			if (matchingFiles != null && matchingFiles.length > 0) {
				String foundFiles = Arrays.stream(matchingFiles).map(File::getName).collect(Collectors.joining(", "));

				Allure.step(String.format("Found %d file(s) matching '%s'", matchingFiles.length, fileNamePattern));
				return true;
			}

			return false;
		}

		// Read data based coloumn name from csv file
		public void readColumnData(String columnName, String RenameFileName) throws FileNotFoundException, IOException {
			List<Object> columnData = new ArrayList<>();
			String userDir = System.getProperty("user.dir");
			String downloadDirectory = userDir + config.getProperty("downloadFileLocation");
			File directory = new File(downloadDirectory);
			File[] files = directory.listFiles((dir, name) -> name.startsWith(RenameFileName));

			if (files == null || files.length == 0) {
				Allure.step("No CSV file found starting with: " + RenameFileName, Status.FAILED);
				sa.fail("CSV file not found");
				return;
			}

			String filePath = files[0].getAbsolutePath();
			int columnIndex = -1;

			try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
				String[] headers = null;

				// Skip initial rows until a valid header is found
				while ((headers = reader.readNext()) != null) {
					if (headers.length > 1 && Arrays.stream(headers)
							.anyMatch(h -> h != null && h.trim().replaceAll("\\u00A0", " ").equalsIgnoreCase(columnName))) {
						break; // Found valid header row
					}
				}

				if (headers == null) {
					Allure.step("CSV file doesn't contain a valid header row", Status.FAILED);
					sa.fail("No valid headers found");
					return;
				}

				// Find the column index of the required column
				for (int i = 0; i < headers.length; i++) {
					String normalizedHeader = headers[i].trim().replaceAll("\\u00A0", " ");
					if (columnName.trim().equalsIgnoreCase(normalizedHeader)) {
						columnIndex = i;
						break;
					}
				}

				if (columnIndex == -1) {
					Allure.step("Column '" + columnName + "' not found in headers: " + Arrays.toString(headers),
							Status.FAILED);
					sa.fail("Column not found");
					return;
				}

				// Read column data from remaining rows
				String[] nextLine;
				while ((nextLine = reader.readNext()) != null) {
					if (columnIndex < nextLine.length && nextLine[columnIndex] != null
							&& !nextLine[columnIndex].isEmpty()) {
						columnData.add(nextLine[columnIndex]);
					}
				}

			} catch (Exception e) {
				Allure.step("Error reading file: " + e.getMessage(), Status.FAILED);
				sa.fail("Exception in reading CSV");
				return;
			}

			totalSiteIdCountInDownloadedFile = columnData.size();
			Allure.step("Total data count in Downloaded File: " + totalSiteIdCountInDownloadedFile, Status.PASSED);
		}

		// click On Suggested Value 2
		public void clickOnSuggestedValue2() {
			try {
				click("click_On_Suggestion2_XPATH");
			} catch (Exception e) {
			}
		}

		public void handleCalendarRefScheduledActivities(String locatorKey, int date) {
			List<WebElement> dates = driver.findElements(By.xpath(OR.getProperty(locatorKey)));
			String dateInString = Integer.toString(date);
			for (int i = 0; i <= dates.size() - 1; i++) {
				if (dates.size() > 1 && dates.get(i).toString().equals(dateInString)) {
					dates.get(i).click();
					break;
				}
			}
		}

		@BeforeSuite
		public void checkFolderAndCreate() {
			// Define the base path
			String userDir = new File(System.getProperty("user.dir")).getAbsolutePath();
			Path basePath = Paths.get(userDir, "src", "main", "resources");

			// Specify the folder name
			String folderName = "downloadExcel";

			// Create the full path
			String fullPath = basePath + File.separator + folderName;

			// Create a File object for the directory
			File directory = new File(fullPath);

			// Check if the directory exists
			if (!directory.exists()) {
				// Attempt to create the directory
				boolean created = directory.mkdir();

				if (created) {
					System.out.println("");
				} else {
					System.out.println("");
				}
			} else {
				System.out.println("");
			}
		}
		
		public void handleCalendarRefScheduledActivities(String locatorKey) {
			List<WebElement> dates = driver.findElements(By.xpath(OR.getProperty(locatorKey)));
			
			int todayDayOfMonth = LocalDate.now().getDayOfMonth();
			
			String dateInString = Integer.toString(todayDayOfMonth);
			for (int i = 0; i <= dates.size() - 1; i++) {
				if (dates.size() > 1 && dates.get(i).toString().equals(dateInString)) {
					dates.get(i).click();
					break;
				}
			}
		}
		
		// method for explicit Wait With presence Of Element Located
		public void explicitWaitWithpresenceOfElementLocated(String locatorKey) {
			try {
				if (locatorKey.endsWith("_ID")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.id(OR.getProperty(locatorKey))));

				} else if (locatorKey.endsWith("_XPATH")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(OR.getProperty(locatorKey))));
				} else if (locatorKey.endsWith("_CSS")) {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(OR.getProperty(locatorKey))));
				}
				log.info("Element " + locatorKey + " is presence on webpage");
				Allure.step("Element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "" + " is presence on webpage"),
						Status.PASSED);

			} catch (Throwable t) {
				log.info("Error while presence of element " + locatorKey);
				Allure.step("Error while presence of element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
						Status.FAILED);

			}
		}
		public void handleCalenderOnTroubleTickett(String numberOFMonth) throws InterruptedException, TimeoutException {
			Thread.sleep(5000);
			click("dateOnCalender_ID");
			for (int i = 1; i <= Integer.parseInt(numberOFMonth); i++) {
				explicitWaitWithClickable("backMonthOnCalender_XPATH");
				Thread.sleep(1000);
			}
			handleClickByJS("selectHourOnCalender_XPATH");
			explicitWaitWithClickable("filterReport_XPATH");
			explicitWaitWithinvisibilityOfElementLocated("waitForInvisible_XPATH");
			pressEndKeysUsingActions();
			Thread.sleep(2000);

		}
		
		public String fluentWaitForVisibilityOfElementLocated(String locatorKey, int maxTimeOut, int pollingTime) {
			String text = null;
		    try {
		    	
		        Wait<WebDriver> fluentWait = new FluentWait<>(driver)
		            .withTimeout(Duration.ofMinutes(maxTimeOut))  // maximum time to wait
		            .pollingEvery(Duration.ofSeconds(pollingTime)) // polling interval
		            .ignoring(NoSuchElementException.class); // exceptions to ignore

		        if (locatorKey.endsWith("_ID")) {
		        	text=fluentWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(OR.getProperty(locatorKey)))).getText();
		        } else if (locatorKey.endsWith("_XPATH")) {
		        	text= fluentWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(OR.getProperty(locatorKey)))).getText();
		        } else if (locatorKey.endsWith("_CSS")) {
		        	text=fluentWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(OR.getProperty(locatorKey)))).getText();
		        }
		        
		        log.info("Element " + locatorKey + " is invisible on webpage");
		        Allure.step("Element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", "" + " is invisible on webpage"),
		                Status.PASSED);

		        return text;
		        
		    } catch (Throwable t) {
		        log.info("Error while waiting for invisibility of element " + locatorKey);
		        Allure.step("Error while waiting for invisibility of element " + locatorKey.replaceAll("_(ID|XPATH|CSS)$", ""),
		                Status.FAILED);
		        return text;
		    }
			
		}
		
}
