import com.beust.ah.A;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.checkerframework.checker.units.qual.K;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.SourceType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

import static java.lang.Thread.sleep;

public class MyJunit {
    WebDriver driver;
    WebDriverWait wait;

    @Before
    public void Setup() {
        System.setProperty("webdriver.chrome.driver", "./src/test/resources/chromedriver.exe");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headed");
        driver = new ChromeDriver(chromeOptions);

        /*System.setProperty("webdriver.gecko.driver", "./src/test/resources/geckodriver.exe");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.addArguments("--headed");
        driver = new FirefoxDriver(firefoxOptions); */

        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    @Test
    public void getTitle() {
        driver.get("https://demoqa.com");
        String title = driver.getTitle();
        System.out.println(title);
        Assert.assertTrue(title.contains("ToolsQA"));
    }

    @Test
    public void checkIfElementExists() {
        driver.get("https://demoqa.com");
        //Boolean status = driver.findElement(By.className("banner-image")).isDisplayed();
        //WebElement imgElement = driver.findElement(By.className("banner-image"));
        //Boolean status = imgElement.isDisplayed();
        //Assert.assertTrue(status);

        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        WebElement imgElement = wait.until(ExpectedConditions.elementToBeClickable(By.className("banner-image")));
        Boolean status = imgElement.isDisplayed();
        Assert.assertTrue(status);
    }

    @Test
    public void fillupForm() throws InterruptedException {
        driver.get("https://demoqa.com/text-box");
        driver.findElement(By.cssSelector("[placeholder='Full Name']")).sendKeys("Mr. Abid");
        driver.findElement(By.id("userEmail")).sendKeys("abid@test.com");
        driver.findElement(By.id("currentAddress")).sendKeys("Kazipara, Dhaka-1216");
        driver.findElement(By.id("permanentAddress")).sendKeys("Dagonbhuiya, Feni");
        driver.findElement(By.id("submit")).click();
    }

    @Test
    public void clickButton() {
        driver.get("https://demoqa.com/buttons");
        WebElement doubleClickBtnElement = driver.findElement(By.id("doubleClickBtn"));
        WebElement rightClickBtnElement = driver.findElement(By.id("rightClickBtn"));
        Actions actions = new Actions(driver);
        actions.doubleClick(doubleClickBtnElement).perform();
        actions.contextClick(rightClickBtnElement).perform();

        String doubleClickMessage = driver.findElement(By.id("doubleClickMessage")).getText();
        String rightClickMessage = driver.findElement(By.id("rightClickMessage")).getText();

        Assert.assertTrue(doubleClickMessage.contains("You have done a double click"));
        Assert.assertTrue(rightClickMessage.contains("You have done a right click"));
    }

    @Test
    public void clickMultipleButton() {
        driver.get("https://demoqa.com/buttons");
        //List<WebElement> buttonElement =  driver.findElements(By.tagName("button"));
        List<WebElement> buttonElement = driver.findElements(By.cssSelector("[type=button]"));
        Actions actions = new Actions(driver);
        actions.doubleClick(buttonElement.get(1)).perform();
        actions.contextClick(buttonElement.get(2)).perform();
        actions.click(buttonElement.get(3)).perform();
    }

    @Test
    public void handleAlert() {
        driver.get("https://demoqa.com/alerts");
        driver.findElement(By.id("alertButton")).click();
        driver.switchTo().alert().accept();
        driver.findElement(By.id("timerAlertButton")).click();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();
        driver.findElement(By.id("confirmButton")).click();
        driver.switchTo().alert().dismiss();
        driver.findElement(By.id("promtButton")).click();
        driver.switchTo().alert().sendKeys("abid");
        driver.switchTo().alert().accept();
        String promtResultMessage = driver.findElement(By.id("promptResult")).getText();
        Assert.assertTrue(promtResultMessage.contains("abid"));
    }

    @Test
    public void selectDate() {
        driver.get("https://demoqa.com/date-picker");
        driver.findElement(By.id("datePickerMonthYearInput")).clear();
        driver.findElement(By.id("datePickerMonthYearInput")).sendKeys("12/04/1997");
        driver.findElement(By.id("datePickerMonthYearInput")).sendKeys(Keys.ENTER);
    }

    @Test
    public void selectDropDown() {
        driver.get("https://demoqa.com/select-menu");
        Select color = new Select(driver.findElement(By.id("oldSelectMenu")));
        color.selectByValue("2");
        Select cars = new Select(driver.findElement(By.id("cars")));
        if (cars.isMultiple()) {
            cars.selectByValue("volvo");
            cars.selectByValue("audi");
        }
    }

    @Test
    public void handleNewTab() throws InterruptedException {
        driver.get("https://demoqa.com/links");
        driver.findElement(By.id("simpleLink")).click();
        Thread.sleep(5000);
        ArrayList<String> newTab = new ArrayList<String>(driver.getWindowHandles());
        // switch to open tab
        driver.switchTo().window(newTab.get(1));
        System.out.println("New tab title: " + driver.getTitle());

        wait = new WebDriverWait(driver, Duration.ofSeconds(50));
        WebElement imgElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//header/a[1]/img[1]")));
        Boolean status = imgElement.isDisplayed();
        //Boolean status = driver.findElement(By.xpath("//header/a[1]/img[1]")).isDisplayed();
        Assert.assertEquals(true,status);
        driver.close();
        driver.switchTo().window(newTab.get(0));
    }

    @Test
    public void handleChildWindow() {
        driver.get("https://demoqa.com/browser-windows");
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.id("windowButton")));
        button.click();
        String mainWindowHandle = driver.getWindowHandle();
        Set<String> allWindowHandles = driver.getWindowHandles();
        Iterator<String> iterator = allWindowHandles.iterator();

        while (iterator.hasNext()) {
            String ChildWindow = iterator.next();
            if (!mainWindowHandle.equalsIgnoreCase(ChildWindow)) {
                driver.switchTo().window(ChildWindow);
                String text = driver.findElement(By.id("sampleHeading")).getText();
                Assert.assertTrue(text.contains("This is a sample page"));
                driver.close();
            }
        }
    }

    @Test
    public void modalDialog() {
        driver.get("https://demoqa.com/modal-dialogs");
        wait = new WebDriverWait(driver, Duration.ofSeconds(40));
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id("showSmallModal")));
        //driver.findElement(By.id("showSmallModal")).click();
        element.click();
        driver.findElement(By.id("closeSmallModal")).click();
    }

    @Test
    public void webTables() {
        driver.get("https://demoqa.com/webtables");
        driver.findElement(By.xpath("//span[@id='edit-record-1']")).click();
        driver.findElement(By.xpath("//input[@id='firstName']")).clear();
        driver.findElement(By.xpath("//input[@id='firstName']")).sendKeys("Mr. Abid");
        driver.findElement(By.id("submit")).click();
    }

    @Test
    public void scrapDataFromWeb() {
        driver.get("https://demoqa.com/webtables");
        WebElement table = driver.findElement(By.className("rt-tbody"));
        List<WebElement> allRows = table.findElements(By.className("rt-tr"));
        int i = 0;
        for(WebElement row : allRows) {
            List<WebElement> cells = row.findElements(By.className("rt-td"));
            for(WebElement cell : cells) {
                i++;
                System.out.println("num[" + i + "] " + cell.getText());
            }
        }
    }

    @Test
    public void uploadImage() {
        driver.get("https://demoqa.com/upload-download");
        WebElement uploadElement = driver.findElement(By.id("uploadFile"));
        uploadElement.sendKeys("C:\\Users\\MD SHAHNEWAZ ABID\\Desktop\\abid.jpg");
        String text = driver.findElement(By.id("uploadedFilePath")).getText();
        Assert.assertTrue(text.contains("abid.jpg"));
    }

    @Test
    public void handleIframe() {
        driver.get("https://demoqa.com/frames");
        driver.switchTo().frame("frame1");
        String text = driver.findElement(By.id("sampleHeading")).getText();
        System.out.println(text);
        Assert.assertTrue(text.contains("This is a sample page"));
        driver.switchTo().defaultContent();
    }

    @Test
    public void mouseHover() throws InterruptedException {
        driver.get("https://green.edu.bd");
        WebElement menuAboutElement = driver.findElement(By.xpath("//header/nav[@id='primary-navbar']/div[1]/ul[1]/li[1]/a[1]"));
        Actions actions = new Actions(driver);
        actions.moveToElement(menuAboutElement).perform();
        Thread.sleep(5000);
    }

    @Test
    public void keyBoardEvents() throws InterruptedException {
        driver.get("https://www.google.com");
        WebElement searchElement = driver.findElement(By.name("q"));
        Actions actions = new Actions(driver);
        actions.moveToElement(searchElement)
                .keyDown(Keys.SHIFT)
                .sendKeys("Selenium Webdriver")
                .keyUp(Keys.SHIFT)
                .doubleClick()
                .contextClick().perform();
        Thread.sleep(5000);
    }

    @Test
    public void takeScreenshot() throws IOException {
        driver.get("https://demoqa.com");
        File screenshotFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        String time = new SimpleDateFormat("dd-MM-yyyy-hh-ss-ss-aa").format(new Date());
        String fileWithPath = "./src/test/resources/screenshots/" + time + ".png";
        File DestFile = new File(fileWithPath);
        FileUtils.copyFile(screenshotFile, DestFile);
    }

    public void readFromExcel(String filePath,String fileName,String sheetName) throws IOException{

        //Create an object of File class to open xlsx file
        File file = new File(filePath+"\\"+fileName);

        //Create an object of FileInputStream class to read excel file
        FileInputStream inputStream = new FileInputStream(file);

        Workbook workbook = null;

        //Find the file extension by splitting file name in substring  and getting only extension name
        String fileExtensionName = fileName.substring(fileName.indexOf("."));


        //Check condition if the file is xls file
        if(fileExtensionName.equals(".xls")){

            //If it is xls file then create object of HSSFWorkbook class
            workbook = new HSSFWorkbook(inputStream);

        }

        //Read sheet inside the workbook by its name
        Sheet sheet = workbook.getSheet(sheetName);

        //Find number of rows in excel file
        int rowCount = sheet.getLastRowNum()-sheet.getFirstRowNum();

        //Create a loop over all the rows of excel file to read it
        for (int i = 0; i < rowCount+1; i++) {

            Row row = sheet.getRow(i);

            //Create a loop to print cell values in a row
            for (int j = 0; j < row.getLastCellNum(); j++) {

                //Print Excel data in console
                System.out.print(row.getCell(j).getStringCellValue()+"|| ");

            }

            System.out.println();
        }
    }

    @Test
    public void readData() throws IOException {
        readFromExcel("D:\\", "Book1.xls", "Sheet1");
    }

    @After
    public void closeBrower() {
        //driver.close();
        driver.quit();
    }
}
