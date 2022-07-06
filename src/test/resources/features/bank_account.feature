Feature: BankAccount
  In order to create a new bank account
  As new customer
  I want ...

  Scenario Outline: Open Account
    Given I don't have any account
    And I have a firstname "<firstname>"
    And I have a lastname "<lastname>"
    And the initial balance is <balance>
    When I open a new account with accountNumber "<accountNumber>", accountId <accountId> and openingDate "<openingDate>"
    Then I get his accountNumber "<accountNumber>"
    Examples:
      | firstname | lastname | accountNumber          | balance | accountId | openingDate |
      | Mary      | Jackson  | GB41OMQP68570038161775 | 200     | 3456      | 2022-02-23T10:15:30  |
      | Thimoty   | Laurence | GB41OMQP68570038161889 | 0       | 735353    | 2022-05-02T10:15:30  |

    Scenario: Should Notify an error when the first name is empty
      Given I don't have any account
      And I have a firstname ""
      And I have a lastname "John"
      And the initial balance is 200
      When I open a new account with accountNumber "GB41OMQP68570038161775", accountId 233 and openingDate "2022-02-23T10:15:30"
      Then I get an Error on First Name, First name is empty

  Scenario: Should Notify an error when the last name is empty
    Given I don't have any account
    And I have a firstname "Mary"
    And I have a lastname ""
    And the initial balance is 200
    When I open a new account with accountNumber "GB41OMQP68570038161775", accountId 233 and openingDate "2022-02-23T10:15:30"
    Then I get an Error on Last Name, Last name is empty

  Scenario: Should Notify an error when the balance is negatif
    Given I don't have any account
    And I have a firstname "Mary"
    And I have a lastname "Gary"
    And the initial balance is -200
    When I open a new account with accountNumber "GB41OMQP68570038161775", accountId 233 and openingDate "2022-02-23T10:15:30"
    Then I get an Error on Balance, Balance is negative
