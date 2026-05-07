package com.socially.app.authcucumber;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME,
    value = "com.socially.app.authcucumber,com.socially.app.cucumber.steps")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@auth")
@ConfigurationParameter(
    key = PLUGIN_PROPERTY_NAME,
    value =
        "pretty, html:target/cucumber-reports/auth-cucumber.html, json:target/cucumber-reports/auth-cucumber.json")
public class AuthCucumberTestRunner {}
