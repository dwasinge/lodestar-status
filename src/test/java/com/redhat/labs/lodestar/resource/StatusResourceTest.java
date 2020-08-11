package com.redhat.labs.lodestar.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import javax.ws.rs.WebApplicationException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.redhat.labs.lodestar.model.status.response.HealthStatus;
import com.redhat.labs.lodestar.rest.client.BackendClient;
import com.redhat.labs.lodestar.rest.client.FrontendClient;
import com.redhat.labs.lodestar.rest.client.GitApiClient;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import io.restassured.response.Response;

@QuarkusTest
public class StatusResourceTest {

    @InjectMock
    @RestClient
    GitApiClient gitApiClient;

    @InjectMock
    @RestClient
    BackendClient backendClient;

    @InjectMock
    @RestClient
    FrontendClient frontendClient;

    @Test
    public void testGetSuccess() {

        Mockito.when(backendClient.getBackendStatus()).thenReturn(HealthStatus.builder().status("UP").build());
        Mockito.when(gitApiClient.getGitApiStatus()).thenReturn(HealthStatus.builder().status("DOWN").build());
        Mockito.when(frontendClient.getFrontendStatus()).thenReturn(javax.ws.rs.core.Response.ok().build());

        given()
           .get("/api/v1/status")
        .then()
            .statusCode(200)
            .body("component_name", hasItem("BACKEND"))
            .body("component_name", hasItem("GIT_API"))
            .body("component_name", hasItem("FE"))
            .body("operational", hasItem(true))
            .body("operational", hasItem(false));

    }

    @Test
    public void testGetSuccessRestCallException() {

        Mockito.when(backendClient.getBackendStatus()).thenReturn(HealthStatus.builder().status("UP").build());
        Mockito.when(gitApiClient.getGitApiStatus()).thenReturn(HealthStatus.builder().status("DOWN").build());
        Mockito.when(frontendClient.getFrontendStatus()).thenThrow(new WebApplicationException(500));

        given()
           .get("/api/v1/status")
        .then()
            .statusCode(200)
            .body("component_name", hasItem("BACKEND"))
            .body("component_name", hasItem("GIT_API"))
            .body("component_name", hasItem("FE"))
            .body("operational", hasItem(true))
            .body("operational", hasItem(false));

    }

}