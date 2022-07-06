package com.optivem.kata.banking.bdd.glue;

import an.awesome.pipelinr.Command;
import com.optivem.kata.banking.adapters.driven.fake.*;
import com.optivem.kata.banking.adapters.driver.web.controllers.BankAccountController;
import com.optivem.kata.banking.core.common.factories.CleanArchUseCaseFactory;
import com.optivem.kata.banking.core.ports.driven.events.AccountOpenedDto;
import com.optivem.kata.banking.core.ports.driver.accounts.openaccount.OpenAccountRequest;
import com.optivem.kata.banking.core.ports.driver.accounts.openaccount.OpenAccountResponse;
import com.optivem.kata.banking.core.ports.driver.exceptions.ValidationMessages;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.optivem.kata.banking.core.common.Givens.givenThat;
import static com.optivem.kata.banking.core.common.Verifications.verifyThat;
import static com.optivem.kata.banking.core.common.builders.requests.OpenAccountRequestBuilder.openAccountRequest;

public class OpenAccountUseCaseBDD {

    private FakeBankAccountStorage storage;
    private FakeAccountIdGenerator accountIdGenerator;
    private FakeAccountNumberGenerator accountNumberGenerator;
    private FakeDateTimeService dateTimeService;
    private FakeEventBus eventBus;


    private Command.Handler<OpenAccountRequest, OpenAccountResponse> useCase;
    private long generatedAccountId;
    private String generatedAccountNumber;

    private LocalDateTime openingDate;
    private OpenAccountRequest request;
    private String firstname;
    private String lastname;
    private Integer balance;

    @Given("I don't have any account")
    public void i_don_t_have_any_account() {
        this.storage = new FakeBankAccountStorage();
        this.accountIdGenerator = new FakeAccountIdGenerator();
        this.accountNumberGenerator = new FakeAccountNumberGenerator();
        this.dateTimeService = new FakeDateTimeService();
        this.eventBus = new FakeEventBus();

        var useCaseFactory = new CleanArchUseCaseFactory();
        this.useCase = useCaseFactory.createOpenAccountHandler(storage, accountIdGenerator, accountNumberGenerator, dateTimeService, eventBus);
    }
    @Given("I have a firstname {string}")
    public void i_have_a_firstname_mary(String firstname) {
        this.firstname = firstname;
    }

    @Given("I have a lastname {string}")
    public void i_have_a_lastname_jackson(String lastname) {
        this.lastname = lastname;
    }
    @Given("the initial balance is {int}")
    public void the_initial_balance_is(Integer balance) {
        this.balance = balance;
    }

    @When("I open a new account with accountNumber {string}, accountId {int} and openingDate {string}")
    public void i_open_a_new_account(String accountNumber, Integer accountId, String openingDate) {
        this.generatedAccountNumber = accountNumber;
        this.generatedAccountId = accountId;
        this.openingDate = LocalDateTime.parse(openingDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        givenThat(this.accountIdGenerator).willGenerate(generatedAccountId);
        givenThat(this.accountNumberGenerator).willGenerate(generatedAccountNumber);
        givenThat(this.dateTimeService).willReturn(this.openingDate);


        this.request = openAccountRequest()
                .withFirstName(this.firstname)
                .withLastName(this.lastname)
                .withBalance(this.balance)
                .build();
    }
    @Then("I get his accountNumber {string}")
    public void i_get_his_account_number(String accountNumber) {
        var expectedResponse = OpenAccountResponse.builder()
                .accountNumber(accountNumber)
                .build();
        var expectedEvent = AccountOpenedDto.builder()
                .timestamp(this.openingDate)
                .accountId(this.generatedAccountId)
                .firstName(this.firstname)
                .lastName(this.lastname)
                .balance(this.balance)
                .build();
        verifyThat(this.useCase).withRequest(this.request).shouldReturnResponse(expectedResponse);
        verifyThat(this.storage).shouldContain(accountNumber);
        verifyThat(this.eventBus).shouldHavePublishedExactly(expectedEvent);
    }
    @Then("I get an Error on First Name, First name is empty")
    public void i_get_an_error_on_firstname() {
        verifyThat(this.useCase).withRequest(this.request).shouldThrowValidationException(ValidationMessages.FIRST_NAME_EMPTY);
    }

    @Then("I get an Error on Last Name, Last name is empty")
    public void i_get_an_error_on_lastname() {
        verifyThat(this.useCase).withRequest(this.request).shouldThrowValidationException(ValidationMessages.LAST_NAME_EMPTY);
    }

    @Then("I get an Error on Balance, Balance is negative")
    public void i_get_an_error_on_balance() {
        verifyThat(this.useCase).withRequest(this.request).shouldThrowValidationException(ValidationMessages.BALANCE_NEGATIVE);
    }
}
