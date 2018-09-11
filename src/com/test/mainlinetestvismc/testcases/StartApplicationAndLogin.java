/**
 * 
 */
package com.test.mainlinetestvismc.testcases;

import com.test.mainlinetestvismc.pages.AddPatientPage;
import com.test.mainlinetestvismc.pages.LoginPage;
import com.test.mainlinetestvismc.utilities.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
@Listeners(GenerateReport.class)

/**
 * @author ABC
 *
 */
public class StartApplicationAndLogin {

	WebDriver driver = null;
	public static Logger log = null;

	@BeforeTest
	public void driversetUp() throws IOException {
		System.setProperty("webdriver.gecko.driver", "C:/Software/Selenium/Selenium_3/geckodriver.exe");

		log = Logger.getLogger("devpinoyLogger");
		driver = new FirefoxDriver();
		driver.get("https://mainlinetest.vismc.com/gladstone/portal/bloom/login/pages/login_userName.html");
		driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
		GlobalMethods.updateName();
	}

	@Test
	public void launchpad() throws IOException {
		

		log.debug("Launching Application: Gladstone : Portal");
		PropertyConfigurator.configure(
				"C:\\Software\\Selenium_workspace\\Vismc\\src\\com\\test\\mainlinetestvismc\\repository\\Log4j.properties");

		log.debug("Reading data from Excel");

		ExcelReader excelReader = new ExcelReader(
				"C:/Software/Selenium_workspace/Vismc/src/com/test/mainlinetestvismc/repository/Test.xlsx");

		int col = excelReader.getColumnCount("OrderDetail");
		int Patientcol = excelReader.getColumnCount("PatientDetail");
		// This is the way to access excel data.
		// String str = excelReader.getCellData("OrderDetail", row - 1, col - 2);
		LoginPage loginPage = PageFactory.initElements(driver, LoginPage.class);
		AddPatientPage addPatientPage = PageFactory.initElements(driver, AddPatientPage.class);
		HashMap<String, String> logindata = new HashMap<>();
		HashMap<String, String> patientdata = new HashMap<>();

		log.debug("Fetching the data from excel");
		for (int i = 0; i < col; i++) {
			logindata.put(excelReader.getCellData("OrderDetail", 0, i), excelReader.getCellData("OrderDetail", 1, i));
		}
		for (int i = 0; i < Patientcol; i++) {
			patientdata.put(excelReader.getCellData("PatientDetail", 0, i),
					excelReader.getCellData("PatientDetail", 1, i));

		}

		log.debug("Entering login Entering details");
		Assert.assertTrue(loginPage.enterLoginDetail(logindata));

		log.debug("Navigating to Patient page to enter details");
		try {
			Assert.assertTrue(addPatientPage.userNavigateToAddPage());
			log.debug("Entering the patient detail, and Saving the patient detail.");
			addPatientPage.savePatient(patientdata);
			log.debug("Verifying the patient detail.");
			addPatientPage.verifyPatient(patientdata);
		} catch (Exception e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}

	}

	@AfterTest
	public void closeScript() {
		driver.close();
	}

}
