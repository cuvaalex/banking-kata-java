package com.optivem.kata.banking.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features/",
        dryRun = false,
        plugin = {
                "pretty", "html:target/cucumber.html", "json:target/cucumber.json", "rerun:target/cucumber-api.txt"
        },
        glue = {"com.optivem.kata.banking.bdd.glue"}
)
public class CucumberFunctionalTest {
}
