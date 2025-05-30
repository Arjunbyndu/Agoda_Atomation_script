package AgodaRegistrationTest;

import static org.testng.Assert.assertNotNull;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import com.mailosaur.MailosaurClient;
import com.mailosaur.models.Code;
import com.mailosaur.models.Message;
import com.mailosaur.models.MessageSearchParams;
import com.mailosaur.models.SearchCriteria;



public class SignInWIthOtp 
{

	 //Configuring the Mailossur apiskey , serverid , server domain 
	   String apiKey = "T2ddcZSQenQd0pIeaAp63Dklee9ZWayM"; 
	   String serverId = "1h6vgarb"; 
	   String serverDomain = "1h6vgarb.mailosaur.net";
			
	   
		public String getRandomEmailId() {
			return "user" + System.currentTimeMillis() + "@" + serverDomain;
		}
	
	@Test
	public void utility() throws Exception
	{	
		String emailid = getRandomEmailId();
		String userFirstName = "Arjun";
		String userLasttName = "Byndoorkar";
		String OTP;
		
		//Initalizing the driver 
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--disable-notifications");
			WebDriver driver = new ChromeDriver(options);
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			Actions action = new Actions(driver);
				
			//launch the Agoda website	
				driver.get("https://www.agoda.com/en-gb/?ds=hX%2Fa232rK6Arg7%2Bw"); 
				
			//Wait for the sign in webelement to load and click on sign-in bitton 	
				WebElement signInButton = driver.findElement(By.xpath("//button[@data-element-name='sign-in-button']"));
				action.moveToElement(signInButton).click().build().perform();
				
			//Locating the Email text field in iframe , enter random emailid and click on continue button
				driver.switchTo().frame(driver.findElement(By.xpath("//iframe[@title='Universal login']"))); // Switch to iframe
				WebElement emailInput =(WebElement) wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//input[@type='email']"))));
				emailInput.sendKeys(emailid);
			
				WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='button']/div/span[contains(@class,'sc-cPiKLX')]")));	
				action.moveToElement(continueButton).click().build().perform(); //click on continue button
				driver.switchTo().defaultContent(); 
				
			// Add user details 
				driver.switchTo().frame(driver.findElement(By.xpath("//iframe[@seamless='seamless']")));
				driver.findElement(By.xpath("//input[@data-cy='profile-firstname']")).sendKeys(userFirstName);
				driver.findElement(By.xpath("//input[@data-cy='profile-lastname']")).sendKeys(userLasttName);
				driver.findElement(By.xpath("//input[@data-cy='profile-email-subscription-checkbox']")).click();
				driver.findElement(By.xpath("//button[@type='submit']")).click();
				driver.switchTo().defaultContent();
				
			// Initialize Mailosaur client with your API key
				MailosaurClient mailosaur = new MailosaurClient(apiKey);
				// Create parameters for searching emails
				    MessageSearchParams params = new MessageSearchParams();
				    params.withServer(serverId); // Specify the Mailosaur server ID to search within
				    // Define criteria for the email search 
				    SearchCriteria criteria = new SearchCriteria();
				    criteria.withSentTo(emailid);  // Look for an email sent to the dynamically generated email ID
	
				    Thread.sleep(5000); // wait for 5 seconds to load new mail in inbox
				    // Search for and retrieve the email message that matches the criteria
				    Message message = mailosaur.messages().get(params, criteria);
			    
			    
			    // This assumes the OTP is located within a <code> tag in the email's HTML body
			    Code firstCode= message.html().codes().get(0);
			    OTP = firstCode.value();
			    System.out.println("OTP Value is = "+ OTP);
			    
			    // Assert that a message was successfully retrieved (it's not null)
			    assertNotNull(message);
			    
			    
			    // Perform a click action on the OTP field and then send the extracted OTP value into it
			    driver.switchTo().frame(driver.findElement(By.xpath("//iframe[@data-cy='ul-app-frame']")));
			    WebElement otpfield =  driver.findElement(By.xpath("//input[@data-cy='otp-box-0']"));
			    action.click(otpfield).sendKeys(OTP).build().perform();
			    
			    // Click the "Continue" button to submit the OTP
			    WebElement continueToSignButton  = wait.until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//span[text()='Continue']"))));
			    action.click(continueToSignButton).build().perform();
			    driver.switchTo().defaultContent();
			  
			    //Closing the browser
			    Thread.sleep(5000); //adding sleep to load the home page before closing the websote
			    driver.quit(); // close the driver
			    
			  
	    
	}
	
	
}
